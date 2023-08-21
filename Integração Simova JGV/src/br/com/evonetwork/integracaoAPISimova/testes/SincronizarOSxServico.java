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
import br.com.evonetwork.integracaoAPISimova.Model.OSxServico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarOSxServico {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlOSxServico = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/OS_SERVICO";

		URL url = null;
		URL urlOSxServico = null;
		
		Token token = null;
		ArrayList<OSxServico> OSxServicoArray = new ArrayList<OSxServico>();
		
		String filial = "Matriz";
		String local = "Matriz";
		String codOS = "90000999";
		String codTipoTempo = "CS";
		String nroRequisicao = "123456";
		String codTipoServico = "DS";
		String tempoPadrao = "100";
		String tempoCobrado = "0";
		String codServico = "01-DS-99999";
		String codMarca = "AW";
		String ativo = "1";
		
		addOSxServico(OSxServicoArray, filial, local, codOS, codTipoTempo, nroRequisicao, codTipoServico, tempoPadrao, 
				tempoCobrado, codServico, codMarca, ativo);
		
		try {
			url = new URL(strUrl);
			urlOSxServico = new URL(strUrlOSxServico);
			
			token = getToken(auth, url);
			
			criarOSxServicos(token, urlOSxServico, OSxServicoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarOSxServicos(Token token, URL url, ArrayList<OSxServico> OSxServicoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (OSxServico OSxServico : OSxServicoArray) {
			body.append("{\r\n"
					+ "\"Filial\": \""+OSxServico.getFilial()+"\",\r\n"
					+ "\"Local\": \""+OSxServico.getLocal()+"\",\r\n"
					+ "\"CodigoOs\": \""+OSxServico.getCodOs()+"\",\r\n"
					+ "\"CodigoTipoTempo\": \""+OSxServico.getCodTipoTempo()+"\",\r\n"
					+ "\"NroRequisicao\":\""+OSxServico.getNroRequisicao()+"\",\r\n"
					+ "\"CodigoTipoServico\": \""+OSxServico.getCodTipoServico()+"\",\r\n"
					+ "\"TempoPadrao\": \""+OSxServico.getTempoPadrao()+"\",\r\n"
					+ "\"TempoCobrado\": \""+OSxServico.getTempoCobrado()+"\",\r\n"
					+ "\"CodigoServico\": \""+OSxServico.getCodServico()+"\",\r\n"
					+ "\"CodigoMarca\": \""+OSxServico.getCodMarca()+"\",\r\n"
					+ "\"Ativo\": \""+OSxServico.getAtivo()+"\""
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String respOSxServicota = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSxServicos = new Gson().fromJson(respOSxServicota, RetornoCadastro[].class); 
        
        System.out.println("RespOSxServicota: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoOSxServicos.length; i++) {
        	System.out.println(retornoOSxServicos[i].getMsg());
            System.out.println("ID OSxServico: "+retornoOSxServicos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addOSxServico(ArrayList<OSxServico> OSxServicoArray, String filial, String local, String codOS, 
			String codTipoTempo, String nroRequisicao, String codTipoServico, String tempoPadrao, String tempoCobrado, 
			String codServico, String codMarca, String ativo) {
		OSxServico OSxServico = new OSxServico();
		
		//filial, local, codOS, codTipoTempo, nroRequisicao, codTipoServico, tempoPadrao, 
		//tempoCobrado, codServico, codMarca, ativo
		
		OSxServico.setFilial(filial);
		OSxServico.setLocal(local);
		OSxServico.setCodOs(codOS);
		OSxServico.setCodTipoTempo(codTipoTempo);
		OSxServico.setNroRequisicao(nroRequisicao);
		OSxServico.setCodTipoServico(codTipoServico);
		OSxServico.setTempoPadrao(tempoPadrao);
		OSxServico.setTempoCobrado(tempoCobrado);
		OSxServico.setCodServico(codServico);
		OSxServico.setCodMarca(codMarca);
		OSxServico.setAtivo(ativo);
		
		OSxServicoArray.add(OSxServico);
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
        
        String respOSxServicota = getResponseBody(conn);
        
        Gson g = new Gson();
        Token token = g.fromJson(respOSxServicota, Token.class);
        
//        System.out.println("RespOSxServicota: "+conn.getResponseCode()+" "+conn.getResponseMessage());
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
