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
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerValidacaoQuantidade {

	public static void validar(PersistenceEvent event) throws Exception {
		DynamicVO itemInseridoVO = (DynamicVO) event.getVo();
		DynamicVO itemOriginalVO = null;
		
		if(validarNota((BigDecimal) itemInseridoVO.getProperty("NUNOTA"))) {
			try {
				itemOriginalVO = getItemOriginalVO(itemInseridoVO);
			} catch(Exception e) {
				e.printStackTrace();
	//			throw new Exception(e.getMessage());
			}
			
			if(itemOriginalVO != null) {
				if(((BigDecimal) itemInseridoVO.getProperty("QTDNEG")).compareTo((BigDecimal) itemOriginalVO.getProperty("QTDNEG")) == 1)
					throw new Exception("Quantidade inserida maior que a quantidade da nota original!");
				else
					System.out.println("Quantidade permitida.");
			} else {
				System.out.println("Item original não encontrado.");
			}
		} else {
			System.out.println("TOP não é 700");
		}
	}

	private static boolean validarNota(BigDecimal nuNota) throws Exception {
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

			sql.appendSql("SELECT CODTIPOPER FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				if(rset.getBigDecimal("CODTIPOPER").compareTo(BigDecimal.valueOf(700)) == 0)
					return true;
				else
					return false;
			} else {
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
	}

	private static DynamicVO getItemOriginalVO(DynamicVO itemInseridoVO) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal nuNotaInserida = itemInseridoVO.asBigDecimal("NUNOTA");
		BigDecimal seqItemInserido = itemInseridoVO.asBigDecimal("SEQUENCIA");
		BigDecimal codProdInserido = itemInseridoVO.asBigDecimal("CODPROD");
		BigDecimal localOrigemOriginal = null;
		BigDecimal localDestinoOriginal = null;
		BigDecimal seqItemOriginal = null;
		BigDecimal nuNotaOriginal = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODLOCALORIG, SEQUENCIA, NUNOTA FROM TGFITE WHERE NUNOTA = (SELECT NUNOTAORIG FROM TGFVAR WHERE NUNOTA = "+nuNotaInserida+" AND SEQUENCIA = "+seqItemInserido+") AND CODPROD = "+codProdInserido);
			System.out.println("SQL: "+sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				if(rset.getBigDecimal("SEQUENCIA").compareTo(BigDecimal.ZERO) == 1) {
					seqItemOriginal = rset.getBigDecimal("SEQUENCIA");
					nuNotaOriginal = rset.getBigDecimal("NUNOTA");
					localDestinoOriginal = rset.getBigDecimal("CODLOCALORIG");
				} else {
					localOrigemOriginal = rset.getBigDecimal("CODLOCALORIG");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		DynamicVO retorno = null;
		try {
			retorno = getDynamicVODoItemOriginal(codProdInserido, nuNotaOriginal, seqItemOriginal, localDestinoOriginal, localOrigemOriginal);
		} catch(Exception e) {
			e.printStackTrace();
//			throw new Exception(e.getMessage());
		}
		return retorno;
	}

	private static DynamicVO getDynamicVODoItemOriginal(BigDecimal codProdInserido, BigDecimal nuNotaOriginal,
			BigDecimal seqItemOriginal, BigDecimal localDestinoOriginal, BigDecimal localOrigemOriginal) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal nuNota = null;
		BigDecimal sequencia = null;
		DynamicVO retornoVO = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT NUNOTA, SEQUENCIA FROM TGFITE WHERE CODPROD = "+codProdInserido+" AND NUNOTA = "+nuNotaOriginal+" AND EXISTS (SELECT * FROM TGFITE WHERE SEQUENCIA = "+seqItemOriginal+" AND CODLOCALORIG = "+localDestinoOriginal+") AND EXISTS (SELECT * FROM TGFITE WHERE SEQUENCIA = "+(seqItemOriginal.multiply(BigDecimal.valueOf(-1)))+" AND CODLOCALORIG = "+localOrigemOriginal+") AND SEQUENCIA >= 1");
			System.out.println("SQL: "+sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				nuNota = rset.getBigDecimal("NUNOTA");
				sequencia = rset.getBigDecimal("SEQUENCIA");
			}
			
			JapeWrapper itemDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			retornoVO = itemDAO.findOne(" NUNOTA = "+nuNota+" AND SEQUENCIA = "+sequencia);
		} catch (Exception e) {
			e.printStackTrace();
//			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return retornoVO;
	}

}
