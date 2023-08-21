package br.com.evonetwork.validarValorEntrePedidoNota;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ValidarValorEntrePedidoNota implements EventoProgramavelJava {

	// Evento na TGFCAB
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		validarValor(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		validarValor(event);
	}

	private void validarValor(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - VALIDAR VALOR ENTRE PEDIDO E NOTA - INICIO***");
		DynamicVO tgfcabVO = (DynamicVO) event.getVo();
		try {
			if(verificarNota(tgfcabVO)) {
				BigDecimal nuNotaOrig = getPedidoDaNota(tgfcabVO);
				if(nuNotaOrig != null) {
					if(verificarNotaOrigem(nuNotaOrig)) {
						validarValorEntreNotaEPedido(tgfcabVO.asBigDecimal("NUNOTA"), nuNotaOrig);
					} else {
						System.out.println("Nota de origem não possui TIPMOV = 'P' ou 'O'");
					}
				} else {
					System.out.println("Nota de origem não encontrada");
				}
			} else {
				System.out.println("Nota não possui TIPMOV = 'C' ou 'V'");
			}
		} catch(Exception e) {
			System.out.println("Erro: "+e.getMessage());
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VALIDAR VALOR ENTRE PEDIDO E NOTA - FIM***");
	}

	private void validarValorEntreNotaEPedido(BigDecimal nuNota, BigDecimal nuNotaOrig) throws Exception {
		BigDecimal vlrItensNota = calcularValorDosItensDaNota(nuNota);
		BigDecimal vlrItensNotaOrig = calcularValorDosItensDaNota(nuNotaOrig);
		if(vlrItensNota.compareTo(vlrItensNotaOrig) == 0)
			System.out.println("Valores correspondem!");
		else
			throw new Exception("Valor entre Pedido e Nota estão diferentes!\n<br>Nota: "+nuNota+", Pedido: "+nuNotaOrig+"\n<br>Valor da nota: "+vlrItensNota+", Valor do pedido: "+vlrItensNotaOrig);
	}

	private BigDecimal calcularValorDosItensDaNota(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrItem = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT VLRUNIT*QTDNEG FROM TGFITE WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				vlrItem = vlrItem.add(rset.getBigDecimal(1));
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
		System.out.println("Valor total da nota "+nuNota+": "+vlrItem);
		return vlrItem;
	}

	private boolean verificarNotaOrigem(BigDecimal nuNotaOrig) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String dado = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT TIPMOV FROM TGFCAB WHERE NUNOTA = "+nuNotaOrig);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString(1);
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
		
		if("P".equals(dado) || "O".equals(dado))
			return true;
		return false;
	}

	private BigDecimal getPedidoDaNota(DynamicVO tgfcabVO) throws Exception {
		BigDecimal nuNota = tgfcabVO.asBigDecimal("NUNOTA");
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal nuNotaOrig = null;
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
			
			if (rset.next())
				nuNotaOrig = rset.getBigDecimal("NUNOTAORIG");
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return nuNotaOrig;
	}

	private boolean verificarNota(DynamicVO tgfcabVO) {
		String tipMov = tgfcabVO.asString("TIPMOV");
		if("V".equals(tipMov) || "C".equals(tipMov))
			return true;
		return false;
	}

}
