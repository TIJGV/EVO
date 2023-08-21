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
import br.com.evonetwork.integracaoAPISimova.Model.OS;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarOS {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlOS = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/OS";

		URL url = null;
		URL urlOS = null;
		
		Token token = null;
		ArrayList<OS> OSArray = new ArrayList<OS>();
		
		String filial = "Matriz";
		String local = "Matriz";
		String codOS = "90000999";
		String chassiVeiculo = "SDFJNJNFSJDHUW123";
		String codMarca = "AW";
		String tipoAtendimento = "1";
		String proprietario = "111111119";
		String lojaProprietario = "0001";
		String observacao = "Simova Teste";
		String dataInclusaoOS = "20220816113323";
		String dataEntregaVeiculo = "20220822";
		String codigoStatusOs = "1";
		String ativo = "1";
		
		addOS(OSArray, filial, local, codOS, chassiVeiculo, codMarca, tipoAtendimento, proprietario, lojaProprietario,
				observacao, dataInclusaoOS, dataEntregaVeiculo, codigoStatusOs, ativo);
		
		try {
			url = new URL(strUrl);
			urlOS = new URL(strUrlOS);
			
			token = getToken(auth, url);
			
			criarOSs(token, urlOS, OSArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarOSs(Token token, URL url, ArrayList<OS> OSArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (OS OS : OSArray) {
			body.append("{\r\n"
					+ "\"Filial\": \""+OS.getFilial()+"\",\r\n"
					+ "\"Local\": \""+OS.getLocal()+"\",\r\n"
					+ "\"CodigoOS\": \""+OS.getCodOS()+"\",\r\n"
					+ "\"ChassiVeiculo\": \""+OS.getChassiVeiculo()+"\",\r\n"
					+ "\"CodigoMarca\": \""+OS.getCodMarca()+"\",\r\n"
					+ "\"TipoAtendimento\": \""+OS.getTipoAtendimento()+"\",\r\n"
					+ "\"Proprietario\": \""+OS.getProprietario()+"\",\r\n"
					+ "\"LojaProprietario\": \""+OS.getLojaProprietario()+"\",\r\n"
					+ "\"Observacao\": \""+OS.getObservacao()+"\",\r\n"
					+ "\"DataInclusaoOS\": \""+OS.getDataInclusaoOS()+"\",\r\n"
					+ "\"DataEntregaVeiculo\": \""+OS.getDataEntregaVeiculo()+"\",\r\n"
					+ "\"CodigoStatusOs\": \""+OS.getCodStatusOS()+"\",\r\n"
					+ "\"Ativo\": \""+OS.getAtivo()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSs = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoOSs.length; i++) {
        	System.out.println(retornoOSs[i].getMsg());
            System.out.println("ID OS: "+retornoOSs[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addOS(ArrayList<OS> OSArray, String filial, String local, String codOS, String chassiVeiculo, 
			String codMarca, String tipoAtendimento, String proprietario, String lojaProprietario, String observacao, 
			String dataInclusaoOS, String dataEntregaVeiculo, String codigoStatusOs, String ativo) {
		OS OS = new OS();
		
		//filial, local, codOS, chassiVeiculo, codMarca, tipoAtendimento, proprietario, lojaProprietario,
		//observacao, dataInclusaoOS, dataEntregaVeiculo, codigoStatusOs, ativo
		
		OS.setFilial(filial);
		OS.setLocal(local);
		OS.setCodOS(codOS);
		OS.setChassiVeiculo(chassiVeiculo);
		OS.setCodMarca(codMarca);
		OS.setTipoAtendimento(tipoAtendimento);
		OS.setProprietario(proprietario);
		OS.setLojaProprietario(lojaProprietario);
		OS.setObservacao(observacao);
		OS.setDataInclusaoOS(dataInclusaoOS);
		OS.setDataEntregaVeiculo(dataEntregaVeiculo);
		OS.setCodStatusOS(codigoStatusOs);
		OS.setAtivo(ativo);
		
		OSArray.add(OS);
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
