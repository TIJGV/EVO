package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.DAO.FilialPadrao;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.ModeloEquipamento;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerModeloEquipamento {

	public static void enviarModeloEquipamentoPorDynamicVO (DynamicVO ModeloEquipamentosVO) {
		System.out.println("Iniciando envio de modelo de equipamento ao Simova");
		String filial = FilialPadrao.getFilialPadrao();
		try {
			String completarURL = "MODELO_EQUIPAMENTO";
			
			ModeloEquipamento ModeloEquipamento = setModeloEquipamento(ModeloEquipamentosVO);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlModeloEquipamento = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando modelo equipamento ao Simova...");
			String idSimova = criarModeloEquipamentoSimova(token, urlModeloEquipamento, ModeloEquipamento);
			
			if(((BigDecimal) ModeloEquipamentosVO.getProperty("IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) ModeloEquipamentosVO.getProperty("IDINTEGRACAO")).toString()))
				System.out.println("ID Simova não foi atualizado!");
			else
				Controller.atualizarIdIntegracao(idSimova, "IDINTEGRACAO", ModeloEquipamentosVO, "AD_MODELOVEI");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de modelo de equipamento ao Simova");
	}
	
	private static String criarModeloEquipamentoSimova(Token token, URL urlModeloEquipamento, ModeloEquipamento ModeloEquipamento) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlModeloEquipamento.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+ModeloEquipamento.getAtivo()+"\",\r\n"
				+ "\"CodigoModeloVeiculo\":\""+ModeloEquipamento.getCodModeloVeiculo()+"\",\r\n"
				+ "\"CodigoTipoVeiculo\": \""+ModeloEquipamento.getCodTipoVeiculo()+"\",\r\n"
				+ "\"DescricaoModeloVeiculo\": \""+ModeloEquipamento.getDescModeloVeiculo()+"\"\r\n"
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
        
        RetornoCadastro[] retornoModeloEquipamentos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoModeloEquipamentos[0].getMsg());
        System.out.println("ID ModeloEquipamento: "+retornoModeloEquipamentos[0].getId());
        
        conn.disconnect();
        
        return retornoModeloEquipamentos[0].getId();
	}

	private static ModeloEquipamento setModeloEquipamento(DynamicVO ModeloEquipamentosVO) throws Exception {
		ModeloEquipamento ModeloEquipamento = new ModeloEquipamento();
		
		String ativoSankhya = (String) ModeloEquipamentosVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		ModeloEquipamento.setAtivo(""+ativo);
		ModeloEquipamento.setCodModeloVeiculo((BigDecimal) ModeloEquipamentosVO.getProperty("NROUNICO")+"");
		ModeloEquipamento.setCodTipoVeiculo((BigDecimal) ModeloEquipamentosVO.getProperty("CODTIPO")+"");
		ModeloEquipamento.setDescModeloVeiculo((String) ModeloEquipamentosVO.getProperty("DESCRICAO"));
		
		// Verifica se o Tipo de veículo vinculado a este está integrado com o Simova, se não estiver, o mesmo será criado
		Controller.verificarSeTipoVeiculoEstaCriadoNoSimova((BigDecimal) ModeloEquipamentosVO.getProperty("CODTIPO"));
		
		return ModeloEquipamento;
	}
}
