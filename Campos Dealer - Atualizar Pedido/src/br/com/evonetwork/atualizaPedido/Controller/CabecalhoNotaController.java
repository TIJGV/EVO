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

public class CabecalhoNotaController {
	public static BigDecimal buscarNunota(BigDecimal numos) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal nunota = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql(
					"SELECT CAB.NUNOTA FROM TGFCAB CAB, TCSOSE OSE WHERE OSE.NUNOTA = CAB.NUNOTA AND OSE.NUMOS = CAB.NUMOS AND CAB.NUMOS = "
							+ numos);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				nunota = rset.getBigDecimal("NUNOTA");
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
		return nunota;
	}
	
	public static BigDecimal buscarNumos(BigDecimal nunota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal numos = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql(
					"SELECT CAB.NUMOS FROM TGFCAB CAB WHERE CAB.NUNOTA  = " + nunota);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				numos = rset.getBigDecimal("NUMOS");
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
		return numos;
	}

	public static boolean validaOrigemCD(BigDecimal nunota, BigDecimal numos) throws Exception{
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

			sql.appendSql(
					"SELECT  OSE.AD_IDCAMPOSDEALER "
					+ "FROM TGFCAB CAB, TCSOSE OSE "
					+ "WHERE OSE.AD_IDCAMPOSDEALER IS NOT NULL "
					+ "AND CAB.NUMOS = OSE.NUMOS "
					+ "AND CAB.NUNOTA = " + nunota
					+ " AND CAB.NUMOS = " + numos );
			System.out.println("SQL: " + sql.toString());

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

}
