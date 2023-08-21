package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.DAO.FilialPadrao;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.TipoEquipamento;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerTipoEquipamento {

	public static void enviarTipoEquipamentoPorDynamicVO (DynamicVO TipoEquipamentosVO) {
		String filial = FilialPadrao.getFilialPadrao();
		try {
			String completarURL = "TIPO_EQUIPAMENTO";
			
			TipoEquipamento TipoEquipamento = setTipoEquipamento(TipoEquipamentosVO);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlTipoEquipamento = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando tipo de equipamento ao Simova...");
			String idSimova = criarTipoEquipamentoSimova(token, urlTipoEquipamento, TipoEquipamento);
			
			if(((BigDecimal) TipoEquipamentosVO.getProperty("IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) TipoEquipamentosVO.getProperty("IDINTEGRACAO")).toString()))
				System.out.println("ID Simova não foi atualizado!");
			else
				Controller.atualizarIdIntegracao(idSimova, "IDINTEGRACAO", TipoEquipamentosVO, "AD_ESPECIETIPOVEI");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String criarTipoEquipamentoSimova(Token token, URL urlTipoEquipamento, TipoEquipamento TipoEquipamento) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlTipoEquipamento.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+TipoEquipamento.getAtivo()+"\",\r\n"
				+ "\"CodigoTipoVeiculo\": \""+TipoEquipamento.getCodTipoVeiculo()+"\",\r\n"
				+ "\"DescricaoTipoVeiculo\": \""+TipoEquipamento.getDescTipoVeiculo()+"\"\r\n"
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
        
        RetornoCadastro[] retornoTipoEquipamentos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("RespTipoEquipamentota HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoTipoEquipamentos[0].getMsg());
        System.out.println("ID TipoEquipamento: "+retornoTipoEquipamentos[0].getId());
        
        conn.disconnect();
        
        return retornoTipoEquipamentos[0].getId();
	}

	private static TipoEquipamento setTipoEquipamento(DynamicVO TipoEquipamentosVO) {
		TipoEquipamento TipoEquipamento = new TipoEquipamento();
		
		String ativoSankhya = (String) TipoEquipamentosVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		TipoEquipamento.setAtivo(""+ativo);
		TipoEquipamento.setCodTipoVeiculo(((BigDecimal) TipoEquipamentosVO.getProperty("NROUNICO"))+"");
		TipoEquipamento.setDescTipoVeiculo((String) TipoEquipamentosVO.getProperty("DESCRICAO"));
		
		return TipoEquipamento;
	}
}
