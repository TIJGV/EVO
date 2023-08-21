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
import br.com.evonetwork.integracaoAPISimova.Model.GrupoServico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarGrupoServico {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlGrupoServico = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/GRUPO_SERVICO";

		URL url = null;
		URL urlGrupoServico = null;
		
		Token token = null;
		ArrayList<GrupoServico> GrupoServicoArray = new ArrayList<GrupoServico>();
		
		String ativo = "1";
		String codGrupoServico = "1099";
		String descGrupoServico = "Simova Teste";
		String codMarca = "AW";
		
		addGrupoServico(GrupoServicoArray, ativo, codGrupoServico, descGrupoServico, codMarca);
		
		try {
			url = new URL(strUrl);
			urlGrupoServico = new URL(strUrlGrupoServico);
			
			token = getToken(auth, url);
			
			criarGrupoServicos(token, urlGrupoServico, GrupoServicoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarGrupoServicos(Token token, URL url, ArrayList<GrupoServico> GrupoServicoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (GrupoServico GrupoServico : GrupoServicoArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+GrupoServico.getAtivo()+"\",\r\n"
					+ "\"CodigoGrupoServico\": \""+GrupoServico.getCodGrupoServico()+"\",\r\n"
					+ "\"DescricaoGrupoServico\": \""+GrupoServico.getDescGrupoServico()+"\",\r\n"
					+ "\"CodigoMarca\": \""+GrupoServico.getCodMarca()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoGrupoServicos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoGrupoServicos.length; i++) {
        	System.out.println(retornoGrupoServicos[i].getMsg());
            System.out.println("ID GrupoServico: "+retornoGrupoServicos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addGrupoServico(ArrayList<GrupoServico> GrupoServicoArray, String ativo, String codGrupoServico, 
			String descGrupoServico, String codMarca) {
		GrupoServico GrupoServico = new GrupoServico();
		
		//ativo, codGrupoServico, descGrupoServico, codMarca
		
		GrupoServico.setAtivo(ativo);
		GrupoServico.setCodGrupoServico(codGrupoServico);
		GrupoServico.setDescGrupoServico(descGrupoServico);
		GrupoServico.setCodMarca(codMarca);
		
		GrupoServicoArray.add(GrupoServico);
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
