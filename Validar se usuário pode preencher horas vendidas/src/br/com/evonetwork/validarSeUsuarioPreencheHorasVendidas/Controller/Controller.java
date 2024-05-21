package br.com.evonetwork.validarSeUsuarioPreencheHorasVendidas.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class Controller {
	
	public static void iniciarValidacaoParaInsert(PersistenceEvent event) throws Exception {
		DynamicVO servicoVO = (DynamicVO) event.getVo();
		int horasVendidas = servicoVO.asInt("HORASVENDIDAS");
		if(horasVendidas != 0) {
			System.out.println("O campo 'Horas vendidas' está preenchido, verificando se usuário tem permissão.");
			verificarSeUsuarioLogadoTemPermissao();
		} else {
			System.out.println("O campo 'Horas vendidas' não está preenchido.");
		}
	}

	private static void verificarSeUsuarioLogadoTemPermissao() throws Exception {
		BigDecimal usuarioLogado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID();
		boolean temPermissao = buscarCampoPermissao(usuarioLogado);
		if(temPermissao) {
			System.out.println("O usuário "+usuarioLogado+" tem permissão para preencher o campo \"Horas vendidas\".");
		} else {
			String message = "O usuário "+usuarioLogado+" não tem permissão para preencher o campo \"Horas vendidas\".";
			System.out.println(message);
			throw new Exception(message);
		}
	}

	public static void iniciarValidacaoParaUpdate(PersistenceEvent event) throws Exception {
		ModifingFields md = event.getModifingFields();
		if (md.isModifing("HORASVENDIDAS")) {
			System.out.println("O campo 'Horas vendidas' está sendo modificado, verificando se usuário tem permissão.");
			verificarSeUsuarioLogadoTemPermissao();
		} else {
			System.out.println("O campo 'Horas vendidas' não está sendo modificado.");
		}
	}

	private static boolean buscarCampoPermissao(BigDecimal codUsu) throws Exception {
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
			
			sql.appendSql("SELECT NVL(AD_PREENCHEHORASVENDIDAS, 'N') AS PERMISSAO FROM TSIUSU WHERE CODUSU = "+codUsu);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				String preencheHorasVendidas = rset.getString("PERMISSAO");
				if("S".equals(preencheHorasVendidas))
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

}
