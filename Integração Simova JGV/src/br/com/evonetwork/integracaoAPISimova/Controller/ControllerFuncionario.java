package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Funcionario;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ControllerFuncionario {
	public static void enviarFuncionarioPorDynamicVO (DynamicVO FuncionariosVO) {
		System.out.println("Iniciando envio de funcionario ao Simova");
		try {
			String completarURL = "FUNCIONARIO";
			
			ArrayList<BigDecimal> filiaisFuncionario = Controller.getFiliaisFuncionario(FuncionariosVO.asBigDecimal("CODPARC"));
			
			if(filiaisFuncionario.isEmpty()){
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			for (BigDecimal filialFuncionario : filiaisFuncionario) {
				String filial = Controller.getFilial(filialFuncionario);
				
				if("S".equals(FuncionariosVO.getProperty("AD_FUNCIONARIO"))) {
					Funcionario Funcionario = setFuncionario(FuncionariosVO, filial);
					
					Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
					
					URL url = new URL(auth.getUrl());
					URL urlFuncionario = new URL(auth.getUrlAcesso());
					
					System.out.println("Autenticando...");
					Token token = Controller.getToken(auth, url);
					
					System.out.println("Enviando funcionário ao Simova...");
					String idSimova = criarFuncionarioSimova(token, urlFuncionario, Funcionario);
					
					if(((BigDecimal) FuncionariosVO.getProperty("AD_IDINTEGRACAOFUN") != null) && idSimova.equals(((BigDecimal) FuncionariosVO.getProperty("AD_IDINTEGRACAOFUN")).toString()))
						System.out.println("ID Simova não foi atualizado!");
					else
						atualizarIdIntegracao(idSimova, FuncionariosVO.asBigDecimal("CODPARC"), filialFuncionario);
				} else {
					System.out.println("Não é funcionário!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de funcionario ao Simova");
	}
	
	private static void atualizarIdIntegracao(String idSimova, BigDecimal codParc, BigDecimal filialFuncionario) throws Exception {
		SessionHandle hnd = null;
		System.out.println("Atualizando funcionario "+codParc+" com IDINTEGRACAO "+idSimova+" na FILIAL "+filialFuncionario+"...");
		try {
			hnd = JapeSession.open();
			
			JapeWrapper filiaisParceiroDAO = JapeFactory.dao("AD_FILIAISPARC");
			DynamicVO filParcVO = filiaisParceiroDAO.findOne("CODPARC = "+codParc+" AND CODEMP = "+filialFuncionario);
			
			filiaisParceiroDAO.prepareToUpdate(filParcVO)
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static String criarFuncionarioSimova(Token token, URL urlFuncionario, Funcionario Funcionario) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlFuncionario.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Filial\": \""+Funcionario.getFilial()+"\",\r\n"
				+ "\"CrachaFuncionario\": \""+Funcionario.getCrachaFuncionario()+"\",\r\n"
				+ "\"Ativo\": \""+Funcionario.getAtivo()+"\",\r\n"
				+ "\"Nome\": \""+Funcionario.getNome()+"\",\r\n"
				+ "\"FlagTipoFuncionario\": \""+Funcionario.getFlagTipoFuncionario()+"\",\r\n"
				+ "\"SeqEquipe\": \""+Funcionario.getSeqEquipe()+"\",\r\n"
				+ "\"DataDemissao\": \""+Funcionario.getDataDemissao()+"\",\r\n"
				+ "\"NumeroCelular\": \""+Funcionario.getCelular()+"\",\r\n"
				+ "\"FlagFerias\":\""+Funcionario.getFlagFerias()+"\",\r\n"
				+ "\"DataRetornoFerias\":\""+Funcionario.getDataRetornoFerias()+"\",\r\n"
				+ "\"Local\": \""+Funcionario.getLocal()+"\"\r\n"
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
        
        RetornoCadastro[] retornoFuncionarios = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoFuncionarios[0].getMsg());
        System.out.println("ID Funcionario: "+retornoFuncionarios[0].getId());
        
        conn.disconnect();
        
        return retornoFuncionarios[0].getId();
	}

	private static Funcionario setFuncionario(DynamicVO FuncionariosVO, String filial) {
		Funcionario Funcionario = new Funcionario();
		
		String ativoSankhya = (String) FuncionariosVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		Funcionario.setFilial(filial);
		Funcionario.setCrachaFuncionario(""+(BigDecimal) FuncionariosVO.getProperty("CODPARC"));
		Funcionario.setNome((String) FuncionariosVO.getProperty("NOMEPARC"));
		Funcionario.setFlagTipoFuncionario((BigDecimal) FuncionariosVO.getProperty("AD_CODTIPOFUNC")+"");
		Funcionario.setSeqEquipe((BigDecimal) FuncionariosVO.getProperty("AD_CODEQUIPEFUNC")+"");
		Funcionario.setAtivo(""+ativo);
		Funcionario.setCelular((String) FuncionariosVO.getProperty("FAX"));
		Funcionario.setLocal(filial);
		Funcionario.setDataDemissao("");
		Funcionario.setDataRetornoFerias("");
		Funcionario.setFlagFerias("0");
		
		return Funcionario;
	}
}
