package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.DAO.EnderecoDAO;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Cliente;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerClientes {

	public static void enviarClientePorDynamicVO(DynamicVO clientesVO) {
		System.out.println("Iniciando envio de cliente ao Simova");
		try {
			String completarURL = "CLIENTE";
			
			String filial = Controller.getFilial(clientesVO.asBigDecimal("AD_FILIAL"));
			
			if("".equals(filial)) {
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			if("S".equals(clientesVO.getProperty("CLIENTE"))) {
				Cliente cliente = setCliente(clientesVO, filial);
				
				Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
				
				URL url = new URL(auth.getUrl());
				URL urlCliente = new URL(auth.getUrlAcesso());
				
				System.out.println("Autenticando...");
				Token token = Controller.getToken(auth, url);
				
				System.out.println("Enviando cliente ao Simova...");
				String idSimova = criarClienteSimova(token, urlCliente, cliente);
				
				if(((BigDecimal) clientesVO.getProperty("AD_IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) clientesVO.getProperty("AD_IDINTEGRACAO")).toString()))
					System.out.println("ID Simova não foi atualizado!");
				else
					Controller.atualizarIdIntegracao(idSimova, "AD_IDINTEGRACAO", clientesVO, "Parceiro");
			} else {
				System.out.println("Não é cliente!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de cliente ao Simova");
	}
	
	private static String criarClienteSimova(Token token, URL urlCliente, Cliente cliente) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlCliente.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
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
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoErro = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoErro[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoErro[0].getMsg());
        }
        
        RetornoCadastro[] retornoClientes = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoClientes[0].getMsg());
        System.out.println("ID Cliente: "+retornoClientes[0].getId());
        
        conn.disconnect();
        
        return retornoClientes[0].getId();
	}

	private static Cliente setCliente(DynamicVO clientesVO, String filial) {
		Cliente cliente = new Cliente();
		
		String ativoSankhya = (String) clientesVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		String endereco = EnderecoDAO.getEndereco((BigDecimal) clientesVO.getProperty("CODEND"));
		String municipio = EnderecoDAO.getCidade((BigDecimal) clientesVO.getProperty("CODCID"));
		//String uf = EnderecoDAO.getUFpelaCidade((BigDecimal) clientesVO.getProperty("CODCID"));
		String bairro = EnderecoDAO.getBairro((BigDecimal) clientesVO.getProperty("CODBAI"));
		
		cliente.setCodigoCliente(""+(BigDecimal) clientesVO.getProperty("CODPARC"));
		cliente.setLoja(filial);
		cliente.setNome((String) clientesVO.getProperty("RAZAOSOCIAL")); //Alterado a pedido do Rafael
		cliente.setCpfCnpj((String) clientesVO.getProperty("CGC_CPF"));
		cliente.setNroProprietario("");
		cliente.setIdentificacao("");
		cliente.setEndereco(endereco);
		cliente.setInscEstadual((String) clientesVO.getProperty("IDENTINSCESTAD"));
		cliente.setMunicipio(municipio);
		cliente.setBairro(bairro);
		cliente.setTelefone((String) clientesVO.getProperty("TELEFONE"));
		cliente.setCelular((String) clientesVO.getProperty("FAX"));
		cliente.setCep((String) clientesVO.getProperty("CEP"));
		cliente.setEmail((String) clientesVO.getProperty("EMAIL"));
		cliente.setUf((String) clientesVO.getProperty("AD_UF"));
		cliente.setAtivo(""+ativo);
		
		return cliente;
	}
}
