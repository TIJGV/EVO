package br.com.evonetwork.preencherPrecoEParceiro.Controller;

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

	public static void buscarPreco(PersistenceEvent event) throws Exception {
		try {
			DynamicVO itemVO = (DynamicVO) event.getVo();
			
			BigDecimal nuNota = getNuNota(itemVO);
			if(nuNota != null) {
				BigDecimal codTop = getCodTop(nuNota);
				if(codTop != null && codTop.compareTo(BigDecimal.valueOf(1015)) == 0) {
					System.out.println("TOP é 1015 - buscando preço de custo.");
					BigDecimal codProd = (BigDecimal) itemVO.getProperty("CODPROD");
					BigDecimal preco = getPrecoDeCusto(codProd);
					
					System.out.println("Alterando VLRUNIT para "+preco);
					if(preco.compareTo(BigDecimal.ZERO) != 0)
						itemVO.setProperty("VLRUNIT", preco);
					return;
				}
			}
			
			BigDecimal numOS = (BigDecimal) itemVO.getProperty("NUMOS");
			BigDecimal codProd = (BigDecimal) itemVO.getProperty("CODPROD");
			BigDecimal codEmp = getCodEmp(numOS);
			BigDecimal preco = getPreco(numOS, codProd, codEmp);
			
			System.out.println("Alterando VLRUNIT para "+preco);
			if(preco.compareTo(BigDecimal.ZERO) != 0)
				itemVO.setProperty("VLRUNIT", preco);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static BigDecimal getCodEmp(BigDecimal numOS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = "+numOS);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
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
		return dado;
	}

	private static BigDecimal getNuNota(DynamicVO itemVO) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT NUNOTA FROM AD_TCSOSE WHERE NUMOS = "+itemVO.asBigDecimal("NUMOS"));
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
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
		return dado;
	}

	private static BigDecimal getPrecoDeCusto(BigDecimal codProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CUSSEMICM FROM TGFCUS WHERE CODPROD = "+codProd+" AND ROWNUM = 1 ORDER BY DTATUAL DESC");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
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
		return dado;
	}

	private static BigDecimal getCodTop(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODTIPOPER FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
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
		return dado;
	}

	private static BigDecimal getPreco(BigDecimal numOS, BigDecimal codProd, BigDecimal codEmp) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT NVL(VLRVENDA, 0) FROM TGFEXC WHERE CODPROD = "+codProd+" AND NUTAB IN (SELECT NUTAB FROM TGFTAB WHERE CODTAB = (SELECT CODTAB FROM AD_TGFPPAEMP WHERE CODTIPPARC = (SELECT CODTIPPARC FROM TGFPAR WHERE CODPARC = (SELECT CODPARC FROM AD_TCSOSE WHERE NUMOS = "+numOS+")) AND CODEMP = "+codEmp+"))");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
			} else {
				dado = BigDecimal.ZERO;
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
		return dado;
	}

	public static void buscarParceiro(PersistenceEvent event) throws Exception {
		try {
			DynamicVO itemVO = (DynamicVO) event.getVo();
			BigDecimal numOS = (BigDecimal) itemVO.getProperty("NUMOS");
			BigDecimal codParc = getParceiro(numOS);
			System.out.println("Alterando PARCEIRO para "+codParc);
			itemVO.setProperty("CODPARC", codParc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static BigDecimal getParceiro(BigDecimal numOS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPARC FROM AD_TCSOSE WHERE NUMOS = "+numOS);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
			} else {
				dado = BigDecimal.ZERO;
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
		return dado;
	}

}
