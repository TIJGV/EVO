package br.com.evonetwork.integracaoCamposDealer.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ProdutoController {

	public static BigDecimal buscarValorVenda(BigDecimal codProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrVenda = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT VLRVENDA FROM TGFTAB TAB, TGFEXC EXC " + "WHERE TAB.NUTAB = EXC.NUTAB "
					+ "AND TAB.DTVIGOR = (SELECT MAX(DTVIGOR) FROM TGFTAB T, TGFEXC E WHERE E.NUTAB = T.NUTAB AND E.CODPROD = EXC.CODPROD) "
					+ "AND EXC.CODPROD = " + codProd);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				vlrVenda = rset.getBigDecimal("VLRVENDA");
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
		return vlrVenda;
	}

	public static BigDecimal buscarCODPRODPeloIdCamposDealer(BigDecimal id) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codprod = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODPROD FROM AD_IDCDPRO WHERE IDCAMPOSDEALER = " + id);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codprod = rset.getBigDecimal("CODPROD");
			} else {
				throw new Exception("Erro: Produto vinculado não está integrado ao CampOS Dealer!");
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
		return codprod;
	}
}
