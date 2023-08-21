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
import br.com.evonetwork.integracaoAPISimova.Model.Equipamento;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarEquipamento {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlEquipamento = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/EQUIPAMENTO";

		URL url = null;
		URL urlEquipamento = null;
		
		Token token = null;
		ArrayList<Equipamento> EquipamentoArray = new ArrayList<Equipamento>();
		
		String ativo = "1";
		String chassiVeiculo = "SDFJNJNFSJDHUW123";
		String chassiReduzido = "SDFJNJN";
		String descVeiculo = "Simova teste";
		String codCliente = "111111119";
		String lojaCliente = "0001";
		String codModeloVeiculo = "AS4345";
		
		addEquipamento(EquipamentoArray, ativo, chassiVeiculo, chassiReduzido, descVeiculo, codCliente, lojaCliente, codModeloVeiculo);
		
		try {
			url = new URL(strUrl);
			urlEquipamento = new URL(strUrlEquipamento);
			
			token = getToken(auth, url);
			
			criarEquipamentos(token, urlEquipamento, EquipamentoArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarEquipamentos(Token token, URL url, ArrayList<Equipamento> EquipamentoArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (Equipamento Equipamento : EquipamentoArray) {
			body.append("{\r\n"
					+ "\"Ativo\": \""+Equipamento.getAtivo()+"\",\r\n"
					+ "\"ChassiVeiculo\":\""+Equipamento.getChassiVeiculo()+"\",\r\n"
					+ "\"ChassiReduzido\":\""+Equipamento.getChassiReduzido()+"\",\r\n"
					+ "\"DescricaoVeiculo\":\""+Equipamento.getDescVeiculo()+"\",\r\n"
					+ "\"CodigoCliente\":\""+Equipamento.getCodCliente()+"\",\r\n"
					+ "\"LojaCliente\":\""+Equipamento.getLojaCliente()+"\",\r\n"
					+ "\"CodigoModeloVeiculo\":\""+Equipamento.getCodModeloVeiculo()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoEquipamentos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoEquipamentos.length; i++) {
        	System.out.println(retornoEquipamentos[i].getMsg());
            System.out.println("ID Equipamento: "+retornoEquipamentos[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addEquipamento(ArrayList<Equipamento> EquipamentoArray, String ativo, String chassiVeiculo, 
			String chassiReduzido, String descVeiculo, String codCliente, String lojaCliente, String codModeloVeiculo) {
		Equipamento Equipamento = new Equipamento();
		
		//ativo, chassiVeiculo, chassiReduzido, descVeiculo, codCliente, lojaCliente, codModeloVeiculo
		
		Equipamento.setAtivo(ativo);
		Equipamento.setChassiVeiculo(chassiVeiculo);
		Equipamento.setChassiReduzido(chassiReduzido);
		Equipamento.setDescVeiculo(descVeiculo);
		Equipamento.setCodCliente(codCliente);
		Equipamento.setLojaCliente(lojaCliente);
		Equipamento.setCodModeloVeiculo(codModeloVeiculo);
		
		EquipamentoArray.add(Equipamento);
		
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
