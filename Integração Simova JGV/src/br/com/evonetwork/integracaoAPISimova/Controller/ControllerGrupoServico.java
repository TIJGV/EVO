package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoAPISimova.DAO.FilialPadrao;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.GrupoServico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerGrupoServico {
	
	public static void enviarGrupoServicoPorDynamicVO (DynamicVO GrupoServicosVO) {
		System.out.println("Iniciando envio de grupo de servico ao Simova");
		String filial = FilialPadrao.getFilialPadrao();
		try {
			String completarURL = "GRUPO_SERVICO";
			
			if(verificaSeEServico(GrupoServicosVO.asBigDecimal("CODGRUPOPROD"))) {
				GrupoServico GrupoServico = setGrupoServico(GrupoServicosVO);
				
				Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
				
				URL url = new URL(auth.getUrl());
				URL urlGrupoServico = new URL(auth.getUrlAcesso());
				
				System.out.println("Autenticando...");
				Token token = Controller.getToken(auth, url);
				
				System.out.println("Enviando grupo servico ao Simova...");
				String idSimova = criarGrupoServicoSimova(token, urlGrupoServico, GrupoServico);
				
				if(((BigDecimal) GrupoServicosVO.getProperty("AD_IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) GrupoServicosVO.getProperty("AD_IDINTEGRACAO")).toString()))
					System.out.println("ID Simova não foi atualizado!");
				else
					Controller.atualizarIdIntegracao(idSimova, "AD_IDINTEGRACAO", GrupoServicosVO, "GrupoProduto");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de grupo de servico ao Simova");
	}
	
	private static boolean verificaSeEServico(BigDecimal codGrupo) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TGFGRU WHERE (CODGRUPOPROD >= 2000000000 AND CODGRUPOPROD < 3000000000) AND CODGRUPOPROD = "+codGrupo+" AND ANALITICO = 'S'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return false;
	}

	private static String criarGrupoServicoSimova(Token token, URL urlGrupoServico, GrupoServico GrupoServico) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlGrupoServico.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+GrupoServico.getAtivo()+"\",\r\n"
				+ "\"CodigoGrupoServico\": \""+GrupoServico.getCodGrupoServico()+"\",\r\n"
				+ "\"DescricaoGrupoServico\": \""+GrupoServico.getDescGrupoServico()+"\",\r\n"
				+ "\"CodigoMarca\": \""+GrupoServico.getCodMarca()+"\"\r\n"
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
        
        RetornoCadastro[] retornoGrupoServicos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoGrupoServicos[0].getMsg());
        System.out.println("ID GrupoServico: "+retornoGrupoServicos[0].getId());
        
        conn.disconnect();
        
        return retornoGrupoServicos[0].getId();
	}

	private static GrupoServico setGrupoServico(DynamicVO GrupoServicosVO) {
		GrupoServico GrupoServico = new GrupoServico();
		
		String ativoSankhya = (String) GrupoServicosVO.getProperty("ATIVO");
		int ativo = 0;
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		String marca = coletarCodMarcaGrupoServico(GrupoServicosVO);
		
		GrupoServico.setAtivo(""+ativo);
		GrupoServico.setCodGrupoServico((BigDecimal) GrupoServicosVO.getProperty("CODGRUPOPROD")+"");
		GrupoServico.setDescGrupoServico((String) GrupoServicosVO.getProperty("DESCRGRUPOPROD"));
		GrupoServico.setCodMarca(marca);
		
		return GrupoServico;
	}

	private static String coletarCodMarcaGrupoServico(DynamicVO GrupoServicosVO) {
		BigDecimal codMarca = (BigDecimal) GrupoServicosVO.getProperty("AD_CODMARCA");
		if(codMarca == null)
			return "";
		
		String nomeMarca = getNomeMarca(codMarca);
		if(nomeMarca.length() > 3)
			nomeMarca = nomeMarca.substring(0, 3);
		
		return nomeMarca;
	}

	private static String getNomeMarca(BigDecimal codMarca) {
		String nomeMarca = "";
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT DESCRICAO FROM TGFMAR WHERE CODIGO = "+codMarca);

			rset = sql.executeQuery();

			if (rset.next()) {
				if(rset.getString("DESCRICAO") != null)
					nomeMarca = rset.getString("DESCRICAO");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return nomeMarca;
	}
}
