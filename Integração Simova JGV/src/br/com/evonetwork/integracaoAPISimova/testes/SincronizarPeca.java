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
import br.com.evonetwork.integracaoAPISimova.Model.Peca;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarPeca {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("UNU");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlPeca = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/PECA";

		URL url = null;
		URL urlPeca = null;
		
		Token token = null;
		ArrayList<Peca> PecaArray = new ArrayList<Peca>();
		
		String ativo = "1";
		String codProduto = "010199";
		String descProduto = "Simova Teste";
		
		addPeca(PecaArray, ativo, codProduto, descProduto);
		
		try {
			url = new URL(strUrl);
			urlPeca = new URL(strUrlPeca);
			
			token = getToken(auth, url);
			
			criarPecas(token, urlPeca, PecaArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarPecas(Token token, URL url, ArrayList<Peca> PecaArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (Peca Peca : PecaArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+Peca.getAtivo()+"\",\r\n"
					+ "\"CodigoProduto\": \""+Peca.getCodProduto()+"\",\r\n"
					+ "\"DescricaoProduto\": \""+Peca.getDescProduto()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoPecas = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoPecas.length; i++) {
        	System.out.println(retornoPecas[i].getMsg());
            System.out.println("ID Peca: "+retornoPecas[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addPeca(ArrayList<Peca> PecaArray, String ativo, String codProduto, String descProduto) {
		Peca Peca = new Peca();
		
		//ativo, codProduto, descProduto
		
		Peca.setAtivo(ativo);
		Peca.setCodProduto(codProduto);
		Peca.setDescProduto(descProduto);
		
		PecaArray.add(Peca);
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
