package br.com.evonetwork.apontamentos;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GetDadosApontamentos {
	public static String totalHorasApontadas(DynamicVO vo, BigDecimal numos, BigDecimal numItem) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		String totalHorasApontadas = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT\r\n"
					+ "NVL((SELECT SUM(TRUNC( 24* (DHFINAL - DHINICIAL))) + (SUM(( MOD(MOD(DHFINAL - DHINICIAL,1)*24,1)*60 ))/60) FROM AD_APONTAMENTOS WHERE NUMOS = "+numos+" AND NUMITEM = "+numItem+"),0)\r\n"
					+ "+\r\n"
					+ "NVL((SELECT SUM(TRUNC( 24* (TO_DATE('"+Utils.convertDate(vo.asTimestamp("DHFINAL").toString())+"', 'dd/mm/yyyy HH24:MI:SS') - TO_DATE('"+Utils.convertDate(vo.asTimestamp("DHINICIAL").toString())+"', 'dd/mm/yyyy HH24:MI:SS')))) + (SUM(( MOD(MOD(TO_DATE('"+Utils.convertDate(vo.asTimestamp("DHFINAL").toString())+"', 'dd/mm/yyyy HH24:MI:SS') - TO_DATE('"+Utils.convertDate(vo.asTimestamp("DHINICIAL").toString())+"', 'dd/mm/yyyy HH24:MI:SS'),1)*24,1)*60 ))/60) AS TOTHORASAPONTADAS FROM DUAL),0) AS TOTHORASAPONTADAS\r\n"
					+ "FROM DUAL");
			sql = new NativeSql(jdbc);
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();
			System.out.println(sb.toString());
			if (rset.next())
				totalHorasApontadas = rset.getString("TOTHORASAPONTADAS");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		} 
		return totalHorasApontadas;
	}

	public static String totalHorasApontadasManualmente(DynamicVO vo) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		String totalHorasApontadas = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade(); 
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT");
			sb.append(" ROUND(24 * (TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE("+vo.asBigDecimal("HRFINAL")+",0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE("+vo.asBigDecimal("HRFINAL")+",0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS') - TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE("+vo.asBigDecimal("HRINICIAL")+",0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE("+vo.asBigDecimal("HRINICIAL")+",0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')), 2) -");
			sb.append(" ROUND(24 * (TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE("+vo.asBigDecimal("INTERVALO")+",0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE("+vo.asBigDecimal("INTERVALO")+",0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS') - TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(0,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(0,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')), 2) AS TOTHORASAPONTADAS");
			sb.append(" FROM DUAL");
			sql = new NativeSql(jdbc);
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();
			System.out.println(sb.toString());
			if (rset.next())
				totalHorasApontadas = rset.getString("TOTHORASAPONTADAS");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		} 
		return totalHorasApontadas;
	}

	public static String converterHorasEmTexto(BigDecimal totalHoras) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		String totalHorasApontadas = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT \r\n"
					+ "LPAD(TRUNC("+totalHoras+"), 2, 0) ||':'||   LPAD(ROUND((("+totalHoras+" - TRUNC("+totalHoras+")) * 60),0), 2, 0) AS TOTHORASAPONTADAS\r\n"
					+ "FROM DUAL");
			sql = new NativeSql(jdbc);
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();
			System.out.println(sb.toString());
			if (rset.next())
				totalHorasApontadas = rset.getString("TOTHORASAPONTADAS"); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		} 
		return totalHorasApontadas;
	}

	public static boolean existeApontamentoAutomatico(BigDecimal numos, BigDecimal numItem) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT 1 FROM AD_APONTAMENTOS WHERE NUMOS = "+numos+" AND NUMITEM = "+numItem);
			sql = new NativeSql(jdbc);
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();
			System.out.println(sb.toString());
			if (rset.next())
				return true;
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

	public static String totalHorasApontadasManualmenteItem(BigDecimal numos, BigDecimal numItem) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		String totalHorasApontadas = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT");
			sb.append(" ROUND(24 * (TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HRFINAL,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HRFINAL,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS') - TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HRINICIAL,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HRINICIAL,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')), 2) -");
			sb.append(" ROUND(24 * (TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(INTERVALO,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(INTERVALO,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS') - TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(0,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(0,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')), 2) AS TOTHORASAPONTADAS");
			sb.append(" FROM AD_TCSITE");
			sb.append(" WHERE NUMOS = "+numos+" AND NUMITEM = "+numItem);
			sql = new NativeSql(jdbc);
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();
			System.out.println(sb.toString());
			if (rset.next())
				totalHorasApontadas = rset.getString("TOTHORASAPONTADAS");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		} 
		return totalHorasApontadas;
	}
}
