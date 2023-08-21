package br.com.evonetwork.validarUsuarioApontamentos.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class Controller {

	public static void validarUsuario(PersistenceEvent event) throws Exception {
		DynamicVO apontamentoVO = (DynamicVO) event.getVo();
		String apontamentoAutomatico = (String) apontamentoVO.getProperty("APONTAMENTOAUTO");
		if(!ehApontamentoAutomatico(apontamentoAutomatico))
			if(!usuarioTemPermissao(((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID()))
				throw new Exception("Usuário não tem permissão para alterar apontamentos!");
	}

	private static boolean usuarioTemPermissao(BigDecimal codUsu) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String flag = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_ALTERAAPONTAMENTOS FROM TSIUSU WHERE CODUSU = "+codUsu);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				flag = rset.getString("AD_ALTERAAPONTAMENTOS");
				if("S".equals(flag))
					return true;
				else
					return false;
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

	private static boolean ehApontamentoAutomatico(String apontamentoAutomatico) {
		if("S".equals(apontamentoAutomatico))
			return true;
		return false;
	}

	public static void desativarFlagApontamentoAutomatico(PersistenceEvent event) {
		DynamicVO apontamentoVO = (DynamicVO) event.getVo();
		apontamentoVO.setProperty("APONTAMENTOAUTO", "N");
	}

}
