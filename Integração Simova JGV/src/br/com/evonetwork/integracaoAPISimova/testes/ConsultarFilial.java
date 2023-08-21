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
import br.com.evonetwork.integracaoAPISimova.Model.Filial;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class ConsultarFilial {

	public static void main(String[] args) {
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("joao.caetano@evonetwork.com.br");
		auth.setPass("Senha123");
		auth.setEmpresa("UNU");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlFilial = "https://grupojgv.h.simova.cloud/nfs/api/v1/integration/FILIAL";

		URL url = null;
		URL urlFilial = null;
		
		Token token = null;
		ArrayList<Filial> FilialArray = new ArrayList<Filial>();
		
		String empresa = "4";
		
		try {
			url = new URL(strUrl);
			urlFilial = new URL(strUrlFilial);
			
			token = getToken(auth, url);
			
			consultarFilial(token, urlFilial, FilialArray, empresa);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void consultarFilial(Token token, URL url, ArrayList<Filial> FilialArray, String empresa) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("{\r\n"
				+ "\"Empresa\": \""+empresa+"\"\r\n"
				+ "}");
		
		String data = body.toString();
		
		System.out.println("URL: "+url.toString());
        System.out.println("Request Method: "+conn.getRequestMethod());
        System.out.println("Properties: "+conn.getRequestProperties());
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        Filial[] retornoFilial = new Gson().fromJson(resposta, Filial[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoFilial.length; i++) {
        	System.out.println("Nome: "+retornoFilial[i].getNome());
            System.out.println("Filial: "+retornoFilial[i].getFilial());
            System.out.println("Local: "+retornoFilial[i].getlocal());
            System.out.println("URLAcesso: "+retornoFilial[i].getUrlAcesso());
            System.out.println("FilialERP: "+retornoFilial[i].getFilialERP());
        }
        
        conn.disconnect();
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
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            body = new StringBuilder();
            while ((line = br.readLine()) != null)
                body.append(line);
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
