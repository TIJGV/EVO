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

import br.com.evonetwork.integracaoAPISimova.Model.ApontamentoZZU;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Filial;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class ConsultarApontamentoZZU {
	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlFilial = "https://grupojgv.h.simova.cloud/nfs/api/v1/integration/VIEW_INTEGRACAO_ZZU";

		URL url = null;
		URL urlFilial = null;
		
		Token token = null;
		ArrayList<Filial> FilialArray = new ArrayList<Filial>();
		
		String codOs = "90000999";
		
		try {
			url = new URL(strUrl);
			urlFilial = new URL(strUrlFilial);
			
			token = getToken(auth, url);
			
			consultarApontamentoZZU(token, urlFilial, FilialArray, codOs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void consultarApontamentoZZU(Token token, URL url, ArrayList<Filial> FilialArray, 
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
        
        ApontamentoZZU[] retornoApontamentoZZU = new Gson().fromJson(resposta, ApontamentoZZU[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoApontamentoZZU.length; i++) {
        	System.out.println("Filial"+retornoApontamentoZZU[i].getZzu_filial());
            System.out.println("Local: "+retornoApontamentoZZU[i].getZzu_local());
            System.out.println("NumOSv: "+retornoApontamentoZZU[i].getZzu_numosv());
            System.out.println("SeqDbOs: "+retornoApontamentoZZU[i].getSeq_db_os());
            System.out.println("DataLancamento: "+retornoApontamentoZZU[i].getZzu_dtlanc());
            System.out.println("DataIntegracao: "+retornoApontamentoZZU[i].getZzu_datint());
            System.out.println("HoraIntegracao: "+retornoApontamentoZZU[i].getZzu_horint());
            System.out.println("DataInsercao: "+retornoApontamentoZZU[i].getZzu_datins());
            System.out.println("Delete: "+retornoApontamentoZZU[i].getD_e_l_e_t_());
            System.out.println("proc_st: "+retornoApontamentoZZU[i].getProc_st());
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
