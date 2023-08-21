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
import br.com.evonetwork.integracaoAPISimova.Model.Funcionario;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;

public class SincronizarFuncionario {

	public static void main(String[] args) {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("Matriz");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlFuncionario = "https://grupojgv.h.simova.cloud/nfs/api/v1/sync/FUNCIONARIO";

		URL url = null;
		URL urlFuncionario = null;
		
		Token token = null;
		ArrayList<Funcionario> funcionarioArray = new ArrayList<Funcionario>();
		
		String filial = "Matriz";
		String crachaFuncionario = "101";
		String nome = "SIMOVA TESTE";
		String flagTipoFuncionario = "2";
		String seqEquipe = "1";
		String ativo = "1";
		String celular = "0";
		String local = "Matriz";
		String dataEmissao = "20220802";
		String dataRetornoFerias = "";
		String flagFerias = "0";
		
		addFuncionario(funcionarioArray, filial, crachaFuncionario, nome, flagTipoFuncionario, seqEquipe, ativo, celular, local,
				dataEmissao, dataRetornoFerias, flagFerias);
		
		try {
			url = new URL(strUrl);
			urlFuncionario = new URL(strUrlFuncionario);
			
			token = getToken(auth, url);
			
			criarFuncionarios(token, urlFuncionario, funcionarioArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criarFuncionarios(Token token, URL url, ArrayList<Funcionario> FuncionarioArray) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		
		for (Funcionario funcionario : FuncionarioArray) {
			body.append("{\r\n"
					+ "\"Filial\": \""+funcionario.getFilial()+"\",\r\n"
					+ "\"CrachaFuncionario\": \""+funcionario.getCrachaFuncionario()+"\",\r\n"
					+ "\"Ativo\": \""+funcionario.getAtivo()+"\",\r\n"
					+ "\"Nome\": \""+funcionario.getNome()+"\",\r\n"
					+ "\"FlagTipoFuncionario\": \""+funcionario.getFlagTipoFuncionario()+"\",\r\n"
					+ "\"SeqEquipe\": \""+funcionario.getSeqEquipe()+"\",\r\n"
					+ "\"DataDemissao\": \""+funcionario.getDataDemissao()+"\",\r\n"
					+ "\"NumeroCelular\": \""+funcionario.getCelular()+"\",\r\n"
					+ "\"FlagFerias\":\""+funcionario.getFlagFerias()+"\",\r\n"
					+ "\"DataRetornoFerias\":\""+funcionario.getDataRetornoFerias()+"\"\r\n"
					+ "\"Local\": \""+funcionario.getLocal()+"\"\r\n"
					+ "}\r\n");
		}
		
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoFuncionarios = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        System.out.println("Token: "+token.getToken());
        
        for(int i = 0; i < retornoFuncionarios.length; i++) {
        	System.out.println(retornoFuncionarios[i].getMsg());
            System.out.println("ID Funcionario: "+retornoFuncionarios[i].getId());
        }
        
        conn.disconnect();
	}

	private static void addFuncionario(ArrayList<Funcionario> funcionarioArray, String filial, String crachaFuncionario, 
			String nome, String seqTipoFuncionario, String seqEquipe, String ativo, String celular, String local, 
			String dataEmissao, String dataRetornoFerias, String flagFerias) {
		Funcionario funcionario = new Funcionario();
		
		funcionario.setFilial(filial);
		funcionario.setCrachaFuncionario(crachaFuncionario);
		funcionario.setNome(nome);
		funcionario.setFlagTipoFuncionario(seqTipoFuncionario);
		funcionario.setSeqEquipe(seqEquipe);
		funcionario.setAtivo(ativo);
		funcionario.setCelular(celular);
		funcionario.setLocal(local);
		funcionario.setDataDemissao(dataEmissao);
		funcionario.setDataRetornoFerias(dataRetornoFerias);
		funcionario.setFlagFerias(flagFerias);
		
		funcionarioArray.add(funcionario);
		
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
