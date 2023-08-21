package br.com.evonetwork.alterarStatusDaOS.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.comercial.AtributosRegras;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	public static boolean verificarSeEhNotaDeVendaOuRequisicao(DynamicVO cabecalhoVO) {
		String tipMov = cabecalhoVO.asString("TIPMOV");
		if("V".equals(tipMov) || "Q".equals(tipMov))
			return true;
		return false;
	}

	public static boolean notaEstaConfirmandoOuEstaConfirmada(DynamicVO cabecalhoVO) {
		String statusNota = cabecalhoVO.asString("STATUSNOTA");
		boolean confirmandoNota = JapeSession.getPropertyAsBoolean(AtributosRegras.CONFIRMANDO, false);
		if(confirmandoNota || "L".equals(statusNota))
			return true;
		return false;
	}

	public static BigDecimal encontrarNumOSPelaNota(DynamicVO cabecalhoVO) throws Exception {
		try {
			BigDecimal nuNota = cabecalhoVO.asBigDecimal("NUNOTA");
			BigDecimal nuPedido = encontrarNuPedidoPelaNota(nuNota);
			if(nuPedido == null)
				throw new Exception("Não foi encontrado nenhum pedido para a nota: "+nuNota+".");
			BigDecimal numOS = encontrarOSDoPedido(nuPedido);
			if(numOS == null)
				throw new Exception("Não foi encontrada nenhuma OS para o pedido: "+nuPedido+".");
			return numOS;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public static BigDecimal encontrarOSDoPedido(BigDecimal nuPedido) throws Exception {
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
			
			sql.appendSql("SELECT NUMOS FROM AD_VIEWTCSNOTAS WHERE NUNOTA = "+nuPedido);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("NUMOS");
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

	public static BigDecimal encontrarNuPedidoPelaNota(BigDecimal nuNota) throws Exception {
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
			
			sql.appendSql("SELECT NUNOTAORIG FROM TGFVAR WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("NUNOTAORIG");
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

	public static boolean todosPedidosDaOSEstaoFaturados(BigDecimal numOS, BigDecimal nuPedidoOriginal) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal nuPedido = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT NUNOTA FROM AD_VIEWTCSNOTAS WHERE NUMOS = "+numOS+" AND NUNOTA <> "+nuPedidoOriginal);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				nuPedido = rset.getBigDecimal("NUNOTA");
				BigDecimal nuNota = acharNotaDoPedido(nuPedido);
				if(notaEstaConfirmada(nuNota))
					continue;
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
		return true;
	}

	private static boolean notaEstaConfirmada(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String statusNota = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT STATUSNOTA FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				statusNota = rset.getString("STATUSNOTA");
				if("L".equals(statusNota))
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

	private static BigDecimal acharNotaDoPedido(BigDecimal nuPedido) throws Exception {
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
			
			sql.appendSql("SELECT NUNOTA FROM TGFVAR WHERE NUNOTAORIG = "+nuPedido);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("NUNOTA");
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

	public static void atualizarStatusDaOS(BigDecimal numOS, String status) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao("TCSOSE").prepareToUpdateByPK(numOS)
				.set("STATUS", status)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
