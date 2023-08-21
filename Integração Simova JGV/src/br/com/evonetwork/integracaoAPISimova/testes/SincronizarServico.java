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
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Servico;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarServico {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlServico = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/SERVICO";

		URL url = null;
		URL urlServico = null;
		
		Token token = null;
		ArrayList<Servico> ServicoArray = new ArrayList<Servico>();
		
		String ativo = "1";
		String codServico = "01-DS-99999";
		String descServico = "Simova Teste";
		String tempoFabrica = "100";
		String codGrupoServico = "1099";
		String codMarca = "AW";
		
		addServico(ServicoArray, ativo, codServico, descServico, tempoFabrica, codGrupoServico, codMarca);
		
		try {
			url = new URL(strUrl);
			urlServico = new URL(strUrlServico);
			
			token = getToken(auth, url);
			
			criarServicos(token, urlServico, ServicoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarServicos(Token token, URL url, ArrayList<Servico> ServicoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (Servico Servico : ServicoArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+Servico.getAtivo()+"\",\r\n"
					+ "\"CodigoServico\": \""+Servico.getCodServico()+"\",\r\n"
					+ "\"DescricaoServico\": \""+Servico.getDescServico()+"\",\r\n"
					+ "\"TempoFabrica\": \""+Servico.getTempoFabrica()+"\",\r\n"
					+ "\"CodigoGrupoServico\": \""+Servico.getCodGrupoServico()+"\",\r\n"
					+ "\"CodigoMarca\": \""+Servico.getCodMarca()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoServicos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoServicos.length; i++) {
        	System.out.println(retornoServicos[i].getMsg());
            System.out.println("ID Servico: "+retornoServicos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addServico(ArrayList<Servico> ServicoArray, String ativo, String codServico, 
			String descServico, String tempoFabrica, String codGrupoServico, String codMarca) {
		Servico Servico = new Servico();
		
		//ativo, codServico, descServico, tempoFabrica, codGrupoServico, codMarca
		
		Servico.setAtivo(ativo);
		Servico.setCodServico(codServico);
		Servico.setDescServico(descServico);
		Servico.setTempoFabrica(tempoFabrica);
		Servico.setCodGrupoServico(codGrupoServico);
		Servico.setCodMarca(codMarca);
		
		ServicoArray.add(Servico);
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
