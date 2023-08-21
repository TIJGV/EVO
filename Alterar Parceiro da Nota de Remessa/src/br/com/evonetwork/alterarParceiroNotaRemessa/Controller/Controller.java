package br.com.evonetwork.alterarParceiroNotaRemessa.Controller;

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

public class Controller {

	public static void iniciarAlteracao(PersistenceEvent event) {
		try {
			DynamicVO tgfcabVO = (DynamicVO) event.getVo();
			BigDecimal nuNota = tgfcabVO.asBigDecimal("NUNOTA");
			BigDecimal codTOP = tgfcabVO.asBigDecimal("CODTIPOPER");
			System.out.println("TOP: "+codTOP+", Nota: "+nuNota);
			if((BigDecimal.valueOf(1129)).equals(codTOP)) {
				System.out.println("TOP é 1129.");
				BigDecimal numOS = tgfcabVO.asBigDecimal("AD_NUMOSOFICINA");
				if(numOS == null)
					throw new Exception("Número da O.S. não encontrado para a nota "+nuNota);
				BigDecimal codParc = buscarCodParceiroDoOrcamentoDaOS(numOS);
				if(codParc == null)
					throw new Exception("Parceiro não encontrado para o orçamento da O.S. "+numOS);
				System.out.println("Alterando parceiro da nota "+nuNota+" para: "+codParc);
				tgfcabVO.setProperty("CODPARC", codParc);
			} else {
				System.out.println("TOP não é 1129.");
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static BigDecimal buscarCodParceiroDoOrcamentoDaOS(BigDecimal numOS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codParc = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CAB.CODPARC AS CODPARC FROM TGFCAB CAB INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA = OSE.NUNOTA WHERE OSE.NUMOS = "+numOS);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				codParc = rset.getBigDecimal("CODPARC");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao buscar Parceiro do Orçamento da OS: "+e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return codParc;
	}

}
