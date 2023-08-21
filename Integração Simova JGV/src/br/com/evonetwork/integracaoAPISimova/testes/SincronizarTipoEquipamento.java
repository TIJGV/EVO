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
import br.com.evonetwork.integracaoAPISimova.Model.TipoEquipamento;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarTipoEquipamento {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("joao.caetano@evonetwork.com.br");
		auth.setPass("Senha123");
		auth.setEmpresa("UNU");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlTipoEquipamento = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/TIPO_EQUIPAMENTO";

		URL url = null;
		URL urlTipoEquipamento = null;
		
		Token token = null;
		ArrayList<TipoEquipamento> TipoEquipamentoArray = new ArrayList<TipoEquipamento>();
		
		String ativo = "1";
		String codTipoVeiculo = "010199";
		String descTipoVeiculo = "Simova teste API";
		
		addTipoEquipamento(TipoEquipamentoArray, ativo, codTipoVeiculo, descTipoVeiculo);
		
		try {
			url = new URL(strUrl);
			urlTipoEquipamento = new URL(strUrlTipoEquipamento);
			
			token = getToken(auth, url);
			
			criarTipoEquipamentos(token, urlTipoEquipamento, TipoEquipamentoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarTipoEquipamentos(Token token, URL url, ArrayList<TipoEquipamento> TipoEquipamentoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (TipoEquipamento TipoEquipamento : TipoEquipamentoArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+TipoEquipamento.getAtivo()+"\",\r\n"
					+ "\"CodigoTipoVeiculo\": \""+TipoEquipamento.getCodTipoVeiculo()+"\",\r\n"
					+ "\"DescricaoTipoVeiculo\": \""+TipoEquipamento.getDescTipoVeiculo()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
		System.out.println("Properties: "+conn.getRequestProperties());
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoTipoEquipamentos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoTipoEquipamentos.length; i++) {
        	System.out.println(retornoTipoEquipamentos[i].getMsg());
            System.out.println("ID TipoEquipamento: "+retornoTipoEquipamentos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addTipoEquipamento(ArrayList<TipoEquipamento> TipoEquipamentoArray, String ativo, 
			String codTipoVeiculo, String descTipoVeiculo) {
		TipoEquipamento TipoEquipamento = new TipoEquipamento();
		
		//ativo, codTipoVeiculo, descTipoVeiculo
		
		TipoEquipamento.setAtivo(ativo);
		TipoEquipamento.setCodTipoVeiculo(codTipoVeiculo);
		TipoEquipamento.setDescTipoVeiculo(descTipoVeiculo);
		
		TipoEquipamentoArray.add(TipoEquipamento);
		
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
//        System.out.println("Token: "+token.getToken());
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
