package br.com.evonetwork.transferirItensDeUmaOS.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.commons.codec.binary.Base64;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class Controller {
	
	public static void inserirItensNaNotaDeTransferencia(Registro linha, ContextoAcao ca, BigDecimal sequencia, BigDecimal nuNotaTransferenciaGerada, String statusNota, BigDecimal nuNotaOriginalTransferencia) throws Exception {
		BigDecimal codProd = (BigDecimal) linha.getCampo("CODPROD");
		String unidade = (String) linha.getCampo("CODVOL");
		BigDecimal qtd = BigDecimal.ZERO;
		BigDecimal localOrigem = getLocalOrigem(sequencia.multiply(BigDecimal.valueOf(-1)), codProd, nuNotaOriginalTransferencia);
		BigDecimal localDestino = (BigDecimal) linha.getCampo("CODLOCALORIG");
		
//		imprimirDadosNoLog(codProd, qtd, nuNotaOriginalTransferencia, localOrigem, unidade, localDestino, sequencia);
		
		lancarItensNaNotaTransferencia(nuNotaTransferenciaGerada, codProd, qtd, unidade, localOrigem, localDestino, sequencia, nuNotaOriginalTransferencia);
		
		inserirTGFVAR(nuNotaOriginalTransferencia, nuNotaTransferenciaGerada, sequencia, qtd, statusNota);
	}

	private static BigDecimal getLocalOrigem(BigDecimal sequencia, BigDecimal codProd, BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal localOrigem = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODLOCALORIG FROM TGFITE WHERE NUNOTA = "+nuNota+" AND CODPROD = "+codProd+" AND SEQUENCIA = "+sequencia);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				localOrigem = rset.getBigDecimal("CODLOCALORIG");
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
		return localOrigem;
	}

	private static void inserirTGFVAR(BigDecimal nuNotaOriginalTransferencia, BigDecimal nuNotaTransferenciaGerada,
			BigDecimal sequencia, BigDecimal qtd, String statusNota) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tgfvarDAO = JapeFactory.dao(DynamicEntityNames.COMPRA_VENDA_VARIOS_PEDIDO);
			@SuppressWarnings("unused")
			DynamicVO save = tgfvarDAO.create()
				.set("NUNOTAORIG", nuNotaOriginalTransferencia)
				.set("NUNOTA", nuNotaTransferenciaGerada)
				.set("SEQUENCIA", sequencia)
				.set("SEQUENCIAORIG", sequencia)
				.set("QTDATENDIDA", qtd)
				.set("STATUSNOTA", statusNota)
				.save(); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

//	private static void imprimirDadosNoLog(BigDecimal codProd, BigDecimal qtd, BigDecimal nuNotaOriginalTransferencia,
//			BigDecimal topTransferencia, String unidade, BigDecimal localDestino, BigDecimal sequencia) {
//		System.out.println("codProd: "+codProd);
//		System.out.println("qtd: "+qtd);
//		System.out.println("nuNotaOriginalTransferencia: "+nuNotaOriginalTransferencia);
//		System.out.println("topTransferencia: "+topTransferencia);
//		System.out.println("unidade: "+unidade);
//		System.out.println("localDestino: "+localDestino);
//		System.out.println("sequencia: "+sequencia);
//	}

	private static void lancarItensNaNotaTransferencia(BigDecimal nuNota, BigDecimal codProd, BigDecimal qtd,
			String unidade, BigDecimal localOrigem, BigDecimal localDestino, BigDecimal sequencia, BigDecimal nuNotaOriginalTransferencia) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper iNotaDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			DynamicVO save = iNotaDAO.create()
				.set("NUNOTA", nuNota)
				.set("CODPROD", codProd)
				.set("CODVOL", unidade)
				.set("QTDNEG", qtd)
				.set("VLRTOT", getVlrTot(nuNotaOriginalTransferencia, sequencia))
				.set("VLRUNIT", getVlrUnit(nuNotaOriginalTransferencia, sequencia))
				.set("CODLOCALORIG", localOrigem)
				.set("SEQUENCIA", sequencia)
				.save();
			System.out.println("Criado item com NUNOTA: "+save.asBigDecimal("NUNOTA")+" SEQUENCIA: "+save.asBigDecimal("SEQUENCIA"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
	        JapeSession.close(hnd);
	    }
		
		try {
			hnd = JapeSession.open();
			JapeWrapper itemNotaDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			DynamicVO itemVO = itemNotaDAO.findOne("NUNOTA = "+nuNota+" AND SEQUENCIA = "+sequencia.multiply(BigDecimal.valueOf(-1)));
			itemNotaDAO.prepareToUpdate(itemVO)
				.set("CODLOCALORIG", localDestino)
				.update();
			System.out.println("Atualizado item com NUNOTA: "+itemVO.asBigDecimal("NUNOTA")+" SEQUENCIA: "+itemVO.asBigDecimal("SEQUENCIA"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
//		try {
//			hnd = JapeSession.open();
//			JapeWrapper iNotaDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
//			DynamicVO save = iNotaDAO.create()
//				.set("NUNOTA", nuNota)
//				.set("CODPROD", codProd)
//				.set("CODVOL", unidade)
//				.set("QTDNEG", qtd)
//				.set("CODLOCALORIG", localDestino)
//				.set("SEQUENCIA", sequencia.multiply(BigDecimal.valueOf(-1)))
//				.save();
//			System.out.println("Criado item com NUNOTA: "+save.asBigDecimal("NUNOTA")+" SEQUENCIA: "+save.asBigDecimal("SEQUENCIA"));
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//	        JapeSession.close(hnd);
//	    }
	}

	private static Object getVlrUnit(BigDecimal nuNota, BigDecimal sequencia) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrUnit = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT VLRUNIT FROM TGFITE WHERE NUNOTA = "+nuNota+" AND SEQUENCIA = "+sequencia);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				vlrUnit = rset.getBigDecimal("VLRUNIT");
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
		return vlrUnit;
	}

	private static Object getVlrTot(BigDecimal nuNota, BigDecimal sequencia) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrTotal = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT VLRTOT FROM TGFITE WHERE NUNOTA = "+nuNota+" AND SEQUENCIA = "+sequencia);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				vlrTotal = rset.getBigDecimal("VLRTOT");
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
		return vlrTotal;
	}

	public static DynamicVO lancarCabecalhoDeTransferencia(BigDecimal empresa, BigDecimal topTransferencia,
			Timestamp dtHoje, BigDecimal nuNotaOriginalTransferencia) throws Exception {
		DynamicVO notaDeTransferencia = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper cabecalhoDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);
			notaDeTransferencia = cabecalhoDAO.create()
				.set("CODEMP", empresa)
				.set("CODEMPNEGOC", empresa)
				.set("CODTIPOPER", topTransferencia)
				.set("NUMNOTA", BigDecimal.ZERO)
				.set("VLRNOTA", getVlrNota(nuNotaOriginalTransferencia))
				.set("CODCENCUS", getCentroDeResultado(nuNotaOriginalTransferencia))
				.set("CODNAT", getNatureza(nuNotaOriginalTransferencia))
				.set("DTNEG", dtHoje)
				.set("HRENTSAI", dtHoje)
				.set("TIPMOV", "T") //CODCIDORIGEM, CODCIDDESTINO, CODUFORIGEM, CODUFDESTINO
				.save(); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return notaDeTransferencia;
	}

	private static Object getNatureza(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal natureza = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODNAT FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				natureza = rset.getBigDecimal("CODNAT");
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
		return natureza;
	}

	private static Object getCentroDeResultado(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal cr = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODCENCUS FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				cr = rset.getBigDecimal("CODCENCUS");
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
		return cr;
	}

	private static Object getVlrNota(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrNota = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT VLRNOTA FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				vlrNota = rset.getBigDecimal("VLRNOTA");
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
		return vlrNota;
	}
	
	public static String abrirTela(BigDecimal nuNota) throws Exception {
		String tela = "br.com.sankhya.com.mov.CentralNotas";
		byte[] encodedBytesTela = Base64.encodeBase64(tela.getBytes());
		
		String parametros = "{\"NUNOTA\":"+nuNota+"}";
		byte[] encodedBytesParametros = Base64.encodeBase64(parametros.getBytes());
		
		String link = MGECoreParameter.getParameterAsString("URLSANKHYA")+"/mge/system.jsp#app/" + new String(encodedBytesTela) + "/" + new String(encodedBytesParametros) + "/";
		System.out.println("Link devolução: "+link);
		
		String url = "<a target=\"_parent\" title=\"Trasferência gerada com sucesso\" href=\""+link+"\">"+nuNota+"</a>";
				
		return url;
	}
}
