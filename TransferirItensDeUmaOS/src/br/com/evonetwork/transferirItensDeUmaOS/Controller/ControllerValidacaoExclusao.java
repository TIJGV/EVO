package br.com.evonetwork.transferirItensDeUmaOS.Controller;

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
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerValidacaoExclusao {

	public static void validar(PersistenceEvent event) throws Exception {
		DynamicVO cabecalhoSendoExcluidoVO = (DynamicVO) event.getVo();
		
		if(((BigDecimal) cabecalhoSendoExcluidoVO.getProperty("CODTIPOPER")).compareTo(BigDecimal.valueOf(700)) == 0) {
			try {
				validarSeOSEstaFechada((BigDecimal) cabecalhoSendoExcluidoVO.getProperty("NUNOTA"));
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} else {
			System.out.println("TOP não é 700");
		}
	}
	
	private static void validarSeOSEstaFechada(BigDecimal nuNotaOriginalTransferencia) throws Exception {
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
			
			sql.appendSql("SELECT STATUS FROM AD_TCSOSE WHERE NUMOS = (SELECT NUMOS FROM AD_TCSPRO WHERE NUMREQUISICAO = (SELECT NUNOTAORIG FROM TGFVAR WHERE NUNOTA = "+nuNotaOriginalTransferencia+" AND ROWNUM = 1) AND ROWNUM = 1)");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				if("F".equals(rset.getString("STATUS")))
					throw new Exception("A Ordem de Serviço está fechada, exclusão de transferência bloqueada!");
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
	}

}
