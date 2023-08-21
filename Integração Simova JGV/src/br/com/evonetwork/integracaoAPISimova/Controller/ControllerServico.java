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
import br.com.evonetwork.integracaoAPISimova.Model.Servico;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerServico {
	
	public static void enviarServicoPorDynamicVO (DynamicVO ServicosVO) {
		System.out.println("Iniciando envio de servico ao Simova");
		String filial = FilialPadrao.getFilialPadrao();
		try {
			String completarURL = "SERVICO";
			
			if("S".equals(ServicosVO.asString("USOPROD"))) {
				Servico Servico = setServico(ServicosVO);
				
				Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
				
				URL url = new URL(auth.getUrl());
				URL urlServico = new URL(auth.getUrlAcesso());
				
				System.out.println("Autenticando...");
				Token token = Controller.getToken(auth, url);
				
				System.out.println("Enviando serviço ao Simova...");
				String idSimova = criarServicoSimova(token, urlServico, Servico);
				
				if(((BigDecimal) ServicosVO.getProperty("AD_IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) ServicosVO.getProperty("AD_IDINTEGRACAO")).toString()))
					System.out.println("ID Simova não foi atualizado!");
				else
					Controller.atualizarIdIntegracao(idSimova, "AD_IDINTEGRACAO", ServicosVO, "Servico");
			} else {
				System.out.println("Não é Serviço!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de servico ao Simova");
	}
	
	private static String criarServicoSimova(Token token, URL urlServico, Servico Servico) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlServico.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+Servico.getAtivo()+"\",\r\n"
				+ "\"CodigoServico\": \""+Servico.getCodServico()+"\",\r\n"
				+ "\"DescricaoServico\": \""+Servico.getDescServico()+"\",\r\n"
				+ "\"TempoFabrica\": \""+Servico.getTempoFabrica()+"\",\r\n"
				+ "\"CodigoGrupoServico\": \""+Servico.getCodGrupoServico()+"\",\r\n"
				+ "\"CodigoMarca\": \""+Servico.getCodMarca()+"\"\r\n"
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
        
        RetornoCadastro[] retornoServicos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("RespServicota HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoServicos[0].getMsg());
        System.out.println("ID Servico: "+retornoServicos[0].getId());
        
        conn.disconnect();
        
        return retornoServicos[0].getId();
	}

	private static Servico setServico(DynamicVO ServicosVO) throws Exception {
		Servico Servico = new Servico();
		
		String ativoSankhya = (String) ServicosVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		BigDecimal tempoPadrao = (BigDecimal) ServicosVO.getProperty("AD_TEMPPADRAO");
		String tempoPadraoStr = "0";
		if(tempoPadrao != null) {
			tempoPadraoStr = tempoPadrao.toString();
		}
		
		Servico.setAtivo(""+ativo);
		Servico.setCodServico(((String) ServicosVO.getProperty("REFFORN"))); // Alterado de CODPROD para REFFORN à pedido do Leonardo
		Servico.setDescServico((String) ServicosVO.getProperty("DESCRPROD"));
		Servico.setTempoFabrica(tempoPadraoStr);
		Servico.setCodGrupoServico(((BigDecimal) ServicosVO.getProperty("CODGRUPOPROD")).toString());
		Servico.setCodMarca(Controller.coletarCodMarcaServico((BigDecimal) ServicosVO.getProperty("CODGRUPOPROD")));
		
		// Verifica se grupo vinculado a este serviço existe no Simova, se não existir, o mesmo será criado
		Controller.verificarSeGrupoServiçoEstaCriadoNoSimova((BigDecimal) ServicosVO.getProperty("CODGRUPOPROD"));
		
		return Servico;
	}
}
