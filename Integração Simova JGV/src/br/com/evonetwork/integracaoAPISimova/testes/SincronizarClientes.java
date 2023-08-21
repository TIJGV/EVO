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
import br.com.evonetwork.integracaoAPISimova.Model.Cliente;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarClientes {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlCliente = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/CLIENTE";

		URL url = null;
		URL urlCliente = null;
		
		Token token = null;
		ArrayList<Cliente> clienteArray = new ArrayList<Cliente>();
		
		String codigoCliente = "111111119";
		String loja = "0001";
		String nome = "Simova Teste";
		String cpfCnpj = "34913234872";
		String nroProprietario = "1234567890";
		String identificacao = "123";
		String endereco = "Fazenda Gaucha";
		String inscEstadual = "54321";
		String municipio = "Capela de Santana";
		String bairro = "Vila Zelia";
		String telefone = "1232145687";
		String celular = "12982348256";
		String cep = "12246034";
		String email = "simova.teste@simova.com.br";
		String uf = "SP";
		String ativo = "1";
		
		addCliente(clienteArray, codigoCliente, loja, nome, cpfCnpj, nroProprietario, identificacao, endereco, inscEstadual,
				municipio, bairro, telefone, celular, cep, email, uf, ativo);
		
		try {
			url = new URL(strUrl);
			urlCliente = new URL(strUrlCliente);
			
			token = getToken(auth, url);
			
			criarClientes(token, urlCliente, clienteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarClientes(Token token, URL url, ArrayList<Cliente> clienteArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
//		conn.setRequestProperty("PHPSESSID", auth.getClientId());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[");
		
		for (Cliente cliente : clienteArray) {
			body.append("{\r\n"
	        		+ "\"CodigoCliente\":\""+cliente.getCodigoCliente()+"\",\r\n"
	        		+ "\"Loja\":\""+cliente.getLoja()+"\",\r\n"
	        		+ "\"Nome\":\""+cliente.getNome()+"\",\r\n"
	        		+ "\"CpfCnpj\":\""+cliente.getCpfCnpj()+"\",\r\n"
	        		+ "\"NroProprietario\":\""+cliente.getNroProprietario()+"\",\r\n"
	        		+ "\"Identificacao\":\""+cliente.getIdentificacao()+"\",\r\n"
	        		+ "\"Endereco\":\""+cliente.getEndereco()+"\",\r\n"
	        		+ "\"InscricaoEstadual\":\""+cliente.getInscEstadual()+"\",\r\n"
	        		+ "\"Municipio\":\""+cliente.getMunicipio()+"\",\r\n"
	        		+ "\"Bairro\":\""+cliente.getBairro()+"\",\r\n"
	        		+ "\"Telefone\":\""+cliente.getTelefone()+"\",\r\n"
	        		+ "\"Celular\":\""+cliente.getCelular()+"\",\r\n"
	        		+ "\"CEP\":\""+cliente.getCep()+"\",\r\n"
	        		+ "\"Email\":\""+cliente.getEmail()+"\",\r\n"
	        		+ "\"UF\":\""+cliente.getUf()+"\",\r\n"
	        		+ "\"Ativo\": \""+cliente.getAtivo()+"\"\r\n"
	        		+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoClientes = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoClientes.length; i++) {
        	System.out.println(retornoClientes[i].getMsg());
            System.out.println("ID Cliente: "+retornoClientes[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addCliente(ArrayList<Cliente> clienteArray, String codigoCliente, String loja, String nome, 
			String cpfCnpj, String nroProprietario, String identificacao, String endereco, String inscEstadual, 
			String municipio, String bairro, String telefone, String celular, String cep, String email, String uf, 
			String ativo) {
		Cliente cliente = new Cliente();
		
		cliente.setCodigoCliente(codigoCliente);
		cliente.setLoja(loja);
		cliente.setNome(nome);
		cliente.setCpfCnpj(cpfCnpj);
		cliente.setNroProprietario(nroProprietario);
		cliente.setIdentificacao(identificacao);
		cliente.setEndereco(endereco);
		cliente.setInscEstadual(inscEstadual);
		cliente.setMunicipio(municipio);
		cliente.setBairro(bairro);
		cliente.setTelefone(telefone);
		cliente.setCelular(celular);
		cliente.setCep(cep);
		cliente.setEmail(email);
		cliente.setUf(uf);
		cliente.setAtivo(ativo);
		
		clienteArray.add(cliente);
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
