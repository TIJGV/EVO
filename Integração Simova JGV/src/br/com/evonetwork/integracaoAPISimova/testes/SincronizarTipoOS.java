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
import br.com.evonetwork.integracaoAPISimova.Model.TipoOS;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarTipoOS {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlTipoOS = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/TIPO_OS";

		URL url = null;
		URL urlTipoOS = null;
		
		Token token = null;
		ArrayList<TipoOS> TipoOSArray = new ArrayList<TipoOS>();
		
		String ativo = "1";
		String codTipoOS = "AB";
		String descTipoOS = "Simova Teste";
		
		addTipoOS(TipoOSArray, ativo, codTipoOS, descTipoOS);
		
		try {
			url = new URL(strUrl);
			urlTipoOS = new URL(strUrlTipoOS);
			
			token = getToken(auth, url);
			
			criarTipoOSs(token, urlTipoOS, TipoOSArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarTipoOSs(Token token, URL url, ArrayList<TipoOS> TipoOSArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (TipoOS TipoOS : TipoOSArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+TipoOS.getAtivo()+"\",\r\n"
					+ "\"CodigoTipoOS\": \""+TipoOS.getCodTipoOS()+"\",\r\n"
					+ "\"DescricaoTipoOS\": \""+TipoOS.getDescTipoOS()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoTipoOSs = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoTipoOSs.length; i++) {
        	System.out.println(retornoTipoOSs[i].getMsg());
            System.out.println("ID TipoOS: "+retornoTipoOSs[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addTipoOS(ArrayList<TipoOS> TipoOSArray, String ativo, String codTipoOS, String descTipoOS) {
		TipoOS TipoOS = new TipoOS();
		
		//ativo, codTipoOS, descTipoOS
		
		TipoOS.setAtivo(ativo);
		TipoOS.setCodTipoOS(codTipoOS);
		TipoOS.setDescTipoOS(descTipoOS);
		
		TipoOSArray.add(TipoOS);
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
