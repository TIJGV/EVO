package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Equipamento;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerEquipamento {
	
	public static void enviarEquipamentoPorDynamicVO(DynamicVO EquipamentosVO) {
		System.out.println("Iniciando envio de equipamento ao Simova");
		try {
			String completarURL = "EQUIPAMENTO";
			
			BigDecimal codEmpresa = (BigDecimal) EquipamentosVO.getProperty("AD_FILIAL");
			
			String filial = Controller.getFilial(codEmpresa);
			
			if("".equals(filial)) {
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			if("C".equals(EquipamentosVO.getProperty("AD_FINALIDADE")) && Controller.verificaFlagDeEnvioParaVeiculo((BigDecimal) EquipamentosVO.getProperty("AD_NROUNICOMODELO"))) {
				Equipamento Equipamento = setEquipamento(EquipamentosVO);
				
				Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
				
				URL url = new URL(auth.getUrl());
				URL urlEquipamento = new URL(auth.getUrlAcesso());
				
				System.out.println("Autenticando...");
				Token token = Controller.getToken(auth, url);
				
				System.out.println("Enviando equipamento ao Simova...");
				String idSimova = criarEquipamentoSimova(token, urlEquipamento, Equipamento);
				
				if(((BigDecimal) EquipamentosVO.getProperty("AD_INTEGRACAO") != null) && idSimova.equals(((BigDecimal) EquipamentosVO.getProperty("AD_INTEGRACAO")).toString()))
					System.out.println("ID Simova não foi atualizado!");
				else
					Controller.atualizarIdIntegracao(idSimova, "AD_INTEGRACAO", EquipamentosVO, "Veiculo");
			} else {
				System.out.println("Não é veículo de cliente ou flag de bloqueio do envio está ligada!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de equipamento ao Simova");
	}
	
	private static String criarEquipamentoSimova(Token token, URL urlEquipamento, Equipamento Equipamento) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlEquipamento.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+Equipamento.getAtivo()+"\",\r\n"
				+ "\"ChassiVeiculo\":\""+Equipamento.getChassiVeiculo()+"\",\r\n"
				+ "\"ChassiReduzido\":\""+Equipamento.getChassiReduzido()+"\",\r\n"
				+ "\"DescricaoVeiculo\":\""+Equipamento.getDescVeiculo()+"\",\r\n"
				+ "\"CodigoCliente\":\""+Equipamento.getCodCliente()+"\",\r\n"
				+ "\"LojaCliente\":\""+Equipamento.getLojaCliente()+"\",\r\n"
				+ "\"CodigoModeloVeiculo\":\""+Equipamento.getCodModeloVeiculo()+"\"\r\n"
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
        
        RetornoCadastro[] retornoEquipamentos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoEquipamentos[0].getMsg());
        System.out.println("ID Equipamento: "+retornoEquipamentos[0].getId());
        
        conn.disconnect();
        
        return retornoEquipamentos[0].getId();
	}

	private static Equipamento setEquipamento(DynamicVO EquipamentosVO) throws Exception {
		Equipamento Equipamento = new Equipamento();
		
		String ativoSankhya = (String) EquipamentosVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		String codCliente = ((BigDecimal) EquipamentosVO.getProperty("CODPARC"))+"";
		String filial = Controller.getFilial(Controller.getFilialCliente(codCliente));
		String codModeloVeiculo = ((BigDecimal) EquipamentosVO.getProperty("AD_NROUNICOMODELO"))+"";
		
		Equipamento.setAtivo(""+ativo);
		Equipamento.setChassiVeiculo((String) EquipamentosVO.getProperty("CHASSIS"));
		Equipamento.setChassiReduzido((String) EquipamentosVO.getProperty("CHASSIS"));
		Equipamento.setDescVeiculo((String) EquipamentosVO.getProperty("MARCAMODELO"));
		Equipamento.setCodCliente(codCliente);
		Equipamento.setLojaCliente(filial);
		Equipamento.setCodModeloVeiculo(codModeloVeiculo);
		
		// Verifica se cliente vinculado ao equip. possui filial e se está integrado com o Simova, se não estiver, o mesmo será integrado
		if("".equals(filial))
			System.out.println("Parceiro vinculado ao equipamento não possui Filial!");
		else
			Controller.verificarSeClienteEstaCriadoNoSimova((BigDecimal) EquipamentosVO.getProperty("CODPARC"), filial);
		
		// Verifica se Modelo Veículo está integrado com o Simova, se não estiver, o mesmo será integrado
		Controller.verificarSeModeloVeiculoEstaCriadoNoSimova((BigDecimal) EquipamentosVO.getProperty("AD_NROUNICOMODELO"));
		
		return Equipamento;
	}
}
