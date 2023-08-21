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
import br.com.evonetwork.integracaoAPISimova.Model.ModeloEquipamento;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarModeloEquipamento {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlModeloEquipamento = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/MODELO_EQUIPAMENTO";

		URL url = null;
		URL urlModeloEquipamento = null;
		
		Token token = null;
		ArrayList<ModeloEquipamento> ModeloEquipamentoArray = new ArrayList<ModeloEquipamento>();
		
		String ativo = "1";
		String codModeloVeiculo = "AS4345";
		String codTipoVeiculo = "100";
		String descModeloVeiculo = "Simova teste";
		
		addModeloEquipamento(ModeloEquipamentoArray, ativo, codModeloVeiculo, codTipoVeiculo, descModeloVeiculo);
		
		try {
			url = new URL(strUrl);
			urlModeloEquipamento = new URL(strUrlModeloEquipamento);
			
			token = getToken(auth, url);
			
			criarModeloEquipamentos(token, urlModeloEquipamento, ModeloEquipamentoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarModeloEquipamentos(Token token, URL url, ArrayList<ModeloEquipamento> ModeloEquipamentoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (ModeloEquipamento ModeloEquipamento : ModeloEquipamentoArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+ModeloEquipamento.getAtivo()+"\",\r\n"
					+ "\"CodigoModeloVeiculo\":\""+ModeloEquipamento.getCodModeloVeiculo()+"\",\r\n"
					+ "\"CodigoTipoVeiculo\": \""+ModeloEquipamento.getCodTipoVeiculo()+"\",\r\n"
					+ "\"DescricaoModeloVeiculo\": \""+ModeloEquipamento.getDescModeloVeiculo()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoModeloEquipamentos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoModeloEquipamentos.length; i++) {
        	System.out.println(retornoModeloEquipamentos[i].getMsg());
            System.out.println("ID ModeloEquipamento: "+retornoModeloEquipamentos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addModeloEquipamento(ArrayList<ModeloEquipamento> ModeloEquipamentoArray, String ativo, 
			String codModeloVeiculo, String codTipoVeiculo, String descModeloVeiculo) {
		ModeloEquipamento ModeloEquipamento = new ModeloEquipamento();
		
		//ativo, codModeloVeiculo, codTipoVeiculo, descModeloVeiculo
		
		ModeloEquipamento.setAtivo(ativo);
		ModeloEquipamento.setCodModeloVeiculo(codModeloVeiculo);
		ModeloEquipamento.setCodTipoVeiculo(codTipoVeiculo);
		ModeloEquipamento.setDescModeloVeiculo(descModeloVeiculo);
		
		ModeloEquipamentoArray.add(ModeloEquipamento);
		
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
