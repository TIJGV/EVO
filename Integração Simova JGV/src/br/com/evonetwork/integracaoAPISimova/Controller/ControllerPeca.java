package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.DAO.FilialPadrao;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Peca;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerPeca {

	public static void enviarPecaPorDynamicVO (DynamicVO PecasVO) {
		String filial = FilialPadrao.getFilialPadrao();
		try {
			String completarURL = "PECA";
			
			if("R".equals(PecasVO.asString("USOPROD")) && Controller.verificaFlagDeEnvioParaPeca(PecasVO.asBigDecimal("CODGRUPOPROD"))) {
				Peca Peca = setPeca(PecasVO);
				
				Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
				
				URL url = new URL(auth.getUrl());
				URL urlPeca = new URL(auth.getUrlAcesso());
				
				System.out.println("Autenticando...");
				Token token = Controller.getToken(auth, url);
				
				System.out.println("Enviando peça ao Simova...");
				String idSimova = criarPecaSimova(token, urlPeca, Peca);
				
				if(((BigDecimal) PecasVO.getProperty("AD_IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) PecasVO.getProperty("AD_IDINTEGRACAO")).toString()))
					System.out.println("ID Simova não foi atualizado!");
				else
					Controller.atualizarIdIntegracao(idSimova, "AD_IDINTEGRACAO", PecasVO, "Produto");
			} else {
				System.out.println("Não é Peça ou flag de bloqueio do envio está ligada!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String criarPecaSimova(Token token, URL urlPeca, Peca Peca) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlPeca.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+Peca.getAtivo()+"\",\r\n"
				+ "\"CodigoProduto\": \""+Peca.getCodProduto()+"\",\r\n"
				+ "\"DescricaoProduto\": \""+Peca.getDescProduto()+"\"\r\n"
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
        
        RetornoCadastro[] retornoPecas = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("RespPecata HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoPecas[0].getMsg());
        System.out.println("ID Peca: "+retornoPecas[0].getId());
        
        conn.disconnect();
        
        return retornoPecas[0].getId();
	}

	private static Peca setPeca(DynamicVO PecasVO) {
		Peca Peca = new Peca();
		
		String ativoSankhya = (String) PecasVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		Peca.setAtivo(""+ativo);
		Peca.setCodProduto(((String) PecasVO.getProperty("REFFORN"))); // Alteração Solicitada pelo Leonardo - 22/02/2023
		Peca.setDescProduto(((String) PecasVO.getProperty("DESCRPROD")).replace("\"", ""));
		
		return Peca;
	}
}
