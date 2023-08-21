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
import br.com.evonetwork.integracaoAPISimova.Model.TipoTempo;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarTipoTempo {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("UNU");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlTipoTempo = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/TIPO_TEMPO";

		URL url = null;
		URL urlTipoTempo = null;
		
		Token token = null;
		ArrayList<TipoTempo> TipoTempoArray = new ArrayList<TipoTempo>();
		
		String ativo = "0";
		String codTipoTempo = "1";
		String descTipoTempo = "Teste";
		
		addTipoTempo(TipoTempoArray, ativo, codTipoTempo, descTipoTempo);
		
		try {
			url = new URL(strUrl);
			urlTipoTempo = new URL(strUrlTipoTempo);
			
			token = getToken(auth, url);
			
			criarTipoTempos(token, urlTipoTempo, TipoTempoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarTipoTempos(Token token, URL url, ArrayList<TipoTempo> TipoTempoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (TipoTempo TipoTempo : TipoTempoArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+TipoTempo.getAtivo()+"\",\r\n"
					+ "\"CodigoTipoTempo\": \""+TipoTempo.getCodTipoTempo()+"\",\r\n"
					+ "\"DescricaoTipoTempo\": \""+TipoTempo.getDescTipoTempo()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoTipoTempos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoTipoTempos.length; i++) {
        	System.out.println(retornoTipoTempos[i].getMsg());
            System.out.println("ID TipoTempo: "+retornoTipoTempos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addTipoTempo(ArrayList<TipoTempo> TipoTempoArray, String ativo, String codTipoTempo, String descTipoTempo) {
		TipoTempo TipoTempo = new TipoTempo();
		
		//ativo, codTipoTempo, descTipoTempo
		
		TipoTempo.setAtivo(ativo);
		TipoTempo.setCodTipoTempo(codTipoTempo);
		TipoTempo.setDescTipoTempo(descTipoTempo);
		
		TipoTempoArray.add(TipoTempo);
	}
	
	private static Token getToken(Autenticacao auth, URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
        
        String data = "{\r\n"
        		+ "\"user\":\""+auth.getUser()+"\",\r\n"
        		+ "\"password\":\""+auth.getPass()+"\",\r\n"
        		+ "\"empresa\":\""+auth.getEmpresa()+"\"\r\n"
        		+ "}";
        
        System.out.println("URL: "+url.toString());
        System.out.println(data);
        
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
