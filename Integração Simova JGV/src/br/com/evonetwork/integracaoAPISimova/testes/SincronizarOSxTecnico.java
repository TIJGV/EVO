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
import br.com.evonetwork.integracaoAPISimova.Model.OSxTecnico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarOSxTecnico {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlOSxTecnico = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/OS_TECNICO";

		URL url = null;
		URL urlOSxTecnico = null;
		
		Token token = null;
		ArrayList<OSxTecnico> OSxTecnicoArray = new ArrayList<OSxTecnico>();
		
		String filial = "Matriz";
		String local = "Matriz";
		String codOS = "90000999";
		String codTecnico = "990022";
		String codStatusOs = "1";
		String ativo = "1";
		
		addOSxTecnico(OSxTecnicoArray, filial, local, codOS, codTecnico, codStatusOs, ativo);
		
		try {
			url = new URL(strUrl);
			urlOSxTecnico = new URL(strUrlOSxTecnico);
			
			token = getToken(auth, url);
			
			criarOSxTecnicos(token, urlOSxTecnico, OSxTecnicoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarOSxTecnicos(Token token, URL url, ArrayList<OSxTecnico> OSxTecnicoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (OSxTecnico OSxTecnico : OSxTecnicoArray) {
			body.append("{\r\n"
					+ "\"Filial\": \""+OSxTecnico.getFilial()+"\",\r\n"
					+ "\"Local\": \""+OSxTecnico.getLocal()+"\",\r\n"
					+ "\"CodigoOs\": \""+OSxTecnico.getCodOS()+"\",\r\n"
					+ "\"CodigoTecnico\": \""+OSxTecnico.getCodTecnico()+"\",\r\n"
					+ "\"CodigoStatusOs\":\""+OSxTecnico.getCodStatusOS()+"\",\r\n"
					+ "\"Ativo\": \""+OSxTecnico.getAtivo()+"\""
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String respOSxTecnicota = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSxTecnicos = new Gson().fromJson(respOSxTecnicota, RetornoCadastro[].class); 
        
        System.out.println("RespOSxTecnicota: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoOSxTecnicos.length; i++) {
        	System.out.println(retornoOSxTecnicos[i].getMsg());
            System.out.println("ID OSxTecnico: "+retornoOSxTecnicos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addOSxTecnico(ArrayList<OSxTecnico> OSxTecnicoArray, String filial, String local, String codOS, 
			String codTecnico, String codStatusOs, String ativo) {
		OSxTecnico OSxTecnico = new OSxTecnico();
		
		//filial, local, codOS, codTecnico, codStatusOs, ativo
		
		OSxTecnico.setFilial(filial);
		OSxTecnico.setLocal(local);
		OSxTecnico.setCodOS(codOS);
		OSxTecnico.setCodTecnico(codTecnico);
		OSxTecnico.setCodStatusOS(codStatusOs);
		OSxTecnico.setAtivo(ativo);
		
		OSxTecnicoArray.add(OSxTecnico);
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
        
        String respOSxTecnicota = getResponseBody(conn);
        
        Gson g = new Gson();
        Token token = g.fromJson(respOSxTecnicota, Token.class);
        
//        System.out.println("RespOSxTecnicota: "+conn.getResponseCode()+" "+conn.getResponseMessage());
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
