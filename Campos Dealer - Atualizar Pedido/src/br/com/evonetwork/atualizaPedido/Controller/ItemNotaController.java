package br.com.evonetwork.atualizaPedido.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ItemNotaController {
	public static BigDecimal buscarSequencia(BigDecimal nunota, BigDecimal codprod) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal sequencia = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT SEQUENCIA FROM TGFITE WHERE NUNOTA = " + nunota + " AND CODPROD = " + codprod);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				sequencia = rset.getBigDecimal("SEQUENCIA");
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
		return sequencia;
	}
	
	public static BigDecimal buscarVlrTotItens(BigDecimal nunota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal vlrtot = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT SUM(VLRTOT) AS VLRTOT FROM TGFITE WHERE NUNOTA = " + nunota);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				vlrtot = rset.getBigDecimal("VLRTOT");
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
		return vlrtot;
	}
}
