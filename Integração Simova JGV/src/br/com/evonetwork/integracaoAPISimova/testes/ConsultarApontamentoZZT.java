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

import br.com.evonetwork.integracaoAPISimova.Model.ApontamentoZZT;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Filial;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class ConsultarApontamentoZZT {
	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlFilial = "https://grupojgv.h.simova.cloud/nfs/api/v1/integration/VIEW_INTEGRACAO_ZZT";

		URL url = null;
		URL urlFilial = null;
		
		Token token = null;
		ArrayList<Filial> FilialArray = new ArrayList<Filial>();
		
		String codOs = "90000999";
		
		try {
			url = new URL(strUrl);
			urlFilial = new URL(strUrlFilial);
			
			token = getToken(auth, url);
			
			consultarApontamentoZZT(token, urlFilial, FilialArray, codOs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void consultarApontamentoZZT(Token token, URL url, ArrayList<Filial> FilialArray, 
			String codOs) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("{\r\n"
				+ "\"CodigoOs\": \""+codOs+"\"\r\n"
				+ "}\r\n");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        ApontamentoZZT[] retornoApontamentoZZT = new Gson().fromJson(resposta, ApontamentoZZT[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoApontamentoZZT.length; i++) {
        	System.out.println("Filial"+retornoApontamentoZZT[i].getZzt_filial());
            System.out.println("Local: "+retornoApontamentoZZT[i].getZzt_local());
            System.out.println("NumOSv: "+retornoApontamentoZZT[i].getZzt_numosv());
            System.out.println("Texto: "+retornoApontamentoZZT[i].getZzt_texto());
            System.out.println("DtLanc: "+retornoApontamentoZZT[i].getZzt_dtlanc());
            System.out.println("Delete: "+retornoApontamentoZZT[i].getD_e_l_e_t_());
            System.out.println("proc_st: "+retornoApontamentoZZT[i].getProc_st());
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
