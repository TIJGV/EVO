package br.com.evonetwork.integracaoAPISimova.testes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.OSxPecas;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarOSxPecas {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlOSxPecas = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/OS_PECA";

		URL url = null;
		URL urlOSxPecas = null;
		
		Token token = null;
		ArrayList<OSxPecas> OSxPecasArray = new ArrayList<OSxPecas>();
		
		/*"Filial": "010101",
		"Local": "010101",
		"CodigoOs": "90000999",
		"CodigoProduto": "010199",
		"QtdRequisitada":"50",
		"QtdUtilizada": "50",
		"QtdDevolvida": "",
		"Ativo": "1"*/
		
		String filial = "Matriz";
		String local = "Matriz";
		String codOS = "90000999";
		String codigoProduto = "010199";
		String qtdRequisitada = "50";
		String qtdUtilizada = "50";
		String qtdDevolvida = "";
		String ativo = "1";
		
		addOSxPecas(OSxPecasArray, filial, local, codOS, codigoProduto, qtdRequisitada, qtdUtilizada, qtdDevolvida, ativo);
		
		try {
			url = new URL(strUrl);
			urlOSxPecas = new URL(strUrlOSxPecas);
			
			token = getToken(auth, url);
			
			criarOSxPecass(token, urlOSxPecas, OSxPecasArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarOSxPecass(Token token, URL url, ArrayList<OSxPecas> OSxPecasArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (OSxPecas OSxPecas : OSxPecasArray) {
			body.append("{\r\n"
					+ "\"Filial\": \""+OSxPecas.getFilial()+"\",\r\n"
					+ "\"Local\": \""+OSxPecas.getLocal()+"\",\r\n"
					+ "\"CodigoOs\": \""+OSxPecas.getCodOS()+"\",\r\n"
					+ "\"CodigoProduto\": \""+OSxPecas.getCodProduto()+"\",\r\n"
					+ "\"QtdRequisitada\":\""+OSxPecas.getQtdRequisitada()+"\",\r\n"
					+ "\"QtdUtilizada\": \""+OSxPecas.getQtdUtilizada()+"\",\r\n"
					+ "\"QtdDevolvida\": \""+OSxPecas.getQtdDevolvida()+"\",\r\n"
					+ "\"Ativo\": \""+OSxPecas.getAtivo()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String respOSxPecasta = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(respOSxPecasta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoOSxPecass.length; i++) {
        	System.out.println(retornoOSxPecass[i].getMsg());
            System.out.println("ID OSxPecas: "+retornoOSxPecass[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addOSxPecas(ArrayList<OSxPecas> OSxPecasArray, String filial, String local, String codOS, 
			String codigoProduto, String qtdRequisitada, String qtdUtilizada, String qtdDevolvida, String ativo) {
		OSxPecas OSxPecas = new OSxPecas();
		
		//filial, local, codOS, codigoProduto, qtdRequisitada, qtdUtilizada, qtdDevolvida, ativo
		
		OSxPecas.setFilial(filial);
		OSxPecas.setLocal(local);
		OSxPecas.setCodOS(codOS);
		OSxPecas.setCodProduto(codigoProduto);
		OSxPecas.setQtdRequisitada(qtdRequisitada);
		OSxPecas.setQtdUtilizada(qtdUtilizada);
		OSxPecas.setQtdDevolvida(qtdDevolvida);
		OSxPecas.setAtivo(ativo);
		
		OSxPecasArray.add(OSxPecas);
	}
	
	private static Token getToken(Autenticacao auth, URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
//		conn.setRequestProperty("PHPSESSID", auth.getClientId());
        
        String data = "{\r\n"
        		+ "\"user\":\""+auth.getUser()+"\",\r\n"
        		+ "\"password\":\""+auth.getPass()+"\",\r\n"
        		+ "\"empresa\":\""+auth.getEmpresa()+"\"\r\n"
        		+ "}";
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        Gson g = new Gson();
        Token token = g.fromJson(resposta, Token.class);
        
//        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        
        System.out.println("Token: "+token.getToken());
        conn.disconnect();
        
        return token;
	}

	private static String getResponseBody(HttpURLConnection conn) {
		BufferedReader br = null;
        StringBuilder body = null;
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            body = new StringBuilder();
            while ((line = br.readLine()) != null)
                body.append(line);
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
