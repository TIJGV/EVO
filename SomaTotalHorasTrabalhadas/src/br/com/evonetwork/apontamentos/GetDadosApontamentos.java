package br.com.evonetwork.apontamentos;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GetDadosApontamentos {
	public static String totalHorasApontadas(BigDecimal numos) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		String totalHorasApontadas = null;
		
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(" SELECT");
			sb.append(" SUM(TRUNC( 24* (DHFINAL - DHINICIAL))) + (SUM(( MOD(MOD(DHFINAL - DHINICIAL,1)*24,1)*60 ))/60) AS TOTHORASAPONTADAS");
			sb.append(" FROM AD_APONTAMENTOS");
			sb.append(" WHERE NUMOS = " + numos);

			
			sql = new NativeSql(jdbc);
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();
			
			System.out.println(sb.toString());
			while (rset.next()) {
				totalHorasApontadas = rset.getString("TOTHORASAPONTADAS");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return totalHorasApontadas;
	}
}
