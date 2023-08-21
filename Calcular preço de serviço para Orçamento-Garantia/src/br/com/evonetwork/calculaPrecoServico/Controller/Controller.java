package br.com.evonetwork.calculaPrecoServico.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	public static BigDecimal getCodTop(BigDecimal nuNota) throws Exception {
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
				dado = rset.getBigDecimal("CODTIPOPER");
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

	public static boolean topCalculaPreco(BigDecimal codTOP) throws Exception {
		try {
			String calculaPrecoServico = getCalculaPrecoServico(codTOP);
			if("S".equals(calculaPrecoServico))
				return true;
			return false;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static String getCalculaPrecoServico(BigDecimal codTOP) throws Exception {
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
			
			sql.appendSql("SELECT AD_CALCPRECOSERV FROM TGFTOP WHERE CODTIPOPER = "+codTOP+" ORDER BY DHALTER DESC");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("AD_CALCPRECOSERV");
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

	public static boolean verificaSeEServico(BigDecimal codServ) throws Exception {
		String usoProd = getUsoProd(codServ);
		if("S".equals(usoProd))
			return true;
		return false;
	}

	private static String getUsoProd(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT USOPROD FROM TGFPRO WHERE CODPROD = "+codServ);
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
		return dado;
	}

	public static boolean validarQuantidade(BigDecimal quantidade) {
		if(quantidade != null && quantidade.compareTo(BigDecimal.ZERO) != 0)
			return true;
		return false;
	}

	public static void calculaPrecoDoServicoEPreencheNaNota(BigDecimal codServ, BigDecimal quantidade,
			BigDecimal nuNota, BigDecimal codUsuResp, DynamicVO itemNotaVO) throws Exception {
		String baseValor = getBaseValorDoServico(codServ);
		if(!"M".equals(baseValor)) { // Se for manual, não alterar valor
			BigDecimal vlrPadrao = getValorPadraoDoServico(codServ);
			BigDecimal codParcMecanico = getCodParcDoUsuario(codUsuResp);
			BigDecimal valorUnit = getValor(baseValor, vlrPadrao, codParcMecanico, codServ, nuNota);
			BigDecimal valorTotal = quantidade.multiply(valorUnit);
			atualizarValorDoServico(itemNotaVO, valorUnit, valorTotal);
		}
//		adicionarValorAoCabecalho(nuNota, valorTotal); Corrigir conta de precisar reativar
	}

//	private static void adicionarValorAoCabecalho(BigDecimal nuNota, BigDecimal valorTotalItem) throws Exception {
//		BigDecimal vlrNota = getValorNota(nuNota);
//		vlrNota = vlrNota.add(valorTotalItem);
//		atualizarValorNota(nuNota, vlrNota);
//	}

//	private static void atualizarValorNota(BigDecimal nuNota, BigDecimal vlrNota) throws Exception {
//		SessionHandle hnd = null;
//		try {
//			hnd = JapeSession.open();
//			JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA).prepareToUpdateByPK(nuNota)
//				.set("VLRNOTA", vlrNota)
//				.update();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JapeSession.close(hnd);
//		}
//	}
//
//	private static BigDecimal getValorNota(BigDecimal nuNota) throws Exception {
//		JdbcWrapper jdbc = null;
//		NativeSql sql = null;
//		ResultSet rset = null;
//		SessionHandle hnd = null;
//		BigDecimal dado = null;
//		try {
//			hnd = JapeSession.open();
//			hnd.setFindersMaxRows(-1);
//			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
//			jdbc = entity.getJdbcWrapper();
//			jdbc.openSession();
//			
//			sql = new NativeSql(jdbc);
//			
//			sql.appendSql("SELECT VLRNOTA FROM TGFCAB WHERE NUNOTA = "+nuNota);
//			System.out.println("SQL: "+sql.toString());
//			
//			rset = sql.executeQuery();
//			
//			if (rset.next()) {
//				dado = rset.getBigDecimal("VLRNOTA");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JdbcUtils.closeResultSet(rset);
//			NativeSql.releaseResources(sql);
//			JdbcWrapper.closeSession(jdbc);
//			JapeSession.close(hnd);
//		}
//		return dado;
//	}

	private static void atualizarValorDoServico(DynamicVO itemNotaVO, BigDecimal valorUnit, BigDecimal valorTotal) {
		itemNotaVO.setProperty("VLRUNIT", valorUnit);
		itemNotaVO.setProperty("VLRTOT", valorTotal);
	}

	private static BigDecimal getCodParcDoUsuario(BigDecimal codUsuResp) throws Exception {
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
			
			sql.appendSql("SELECT CODPARC FROM TSIUSU WHERE CODUSU = "+codUsuResp);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODPARC");
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

	private static BigDecimal getValor(String baseValor, BigDecimal vlrPadrao, BigDecimal codParcMecanico, BigDecimal codServ, BigDecimal nuNota) throws Exception {
		if("V".equals(baseValor)) { //Mecânico - Venda
			BigDecimal vlrMecanicoVenda = getValorVendaMecanico(codParcMecanico);
			if((BigDecimal.ZERO).compareTo(vlrMecanicoVenda) == 1)
				throw new Exception("Valor de venda para o mecânico "+codParcMecanico+" está negativo!");
			if(vlrMecanicoVenda != null)
				return vlrMecanicoVenda;
			else
				throw new Exception("Valor de venda não encontrado para mecânico "+codParcMecanico+"!");
		} else if("S".equals(baseValor)) { //Serviço
			if((BigDecimal.ZERO).compareTo(vlrPadrao) == 1)
				throw new Exception("Valor do serviço está negativo!");
			if(vlrPadrao != null)
				return vlrPadrao;
			else
				throw new Exception("Valor do serviço não encontrado!");
		} else if("C".equals(baseValor)) { //Mecânico - Custo
			BigDecimal vlrMecanicoCusto = getValorCustoMecanico(codParcMecanico);
			if((BigDecimal.ZERO).compareTo(vlrMecanicoCusto) == 1)
				throw new Exception("Valor de custo para o mecânico "+codParcMecanico+" está negativo!");
			if(vlrMecanicoCusto != null)
				return vlrMecanicoCusto;
			else
				throw new Exception("Valor de custo não encontrado para mecânico "+codParcMecanico+"!");
		} else if("T".equals(baseValor)) { //Tabela tipo de OS
			BigDecimal vlrTabelaDePrecos = BigDecimal.ZERO;
			try {
				BigDecimal tipoOs = getTipoOs(nuNota);
				BigDecimal empresa = getEmpresa(nuNota);
				vlrTabelaDePrecos = coletarPrecoDaTabelaDePrecoDoServico(tipoOs, empresa, codServ);
				if((BigDecimal.ZERO).compareTo(vlrTabelaDePrecos) == 1)
					throw new Exception("Valor negativo encontrado para o Serviço "+codServ+" na Tabela de Preço do Serviço!");
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			return vlrTabelaDePrecos;
		}
		return null;
	}

	private static BigDecimal coletarPrecoDaTabelaDePrecoDoServico(BigDecimal tipoOs, BigDecimal empresa,
			BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT VALOR FROM AD_TABPRECOSERV WHERE CODEMP = "+empresa+" AND CODSERV = "+codServ+" AND CODTIPODEOS = "+tipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("VALOR");
			} else {
				throw new Exception("Valor não encontrado na tabela de preços para a Empresa: "+empresa+", Serviço: "+codServ+" e Tipo de OS: "+tipoOs);
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

	private static BigDecimal getEmpresa(BigDecimal nuNota) throws Exception {
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
			
			sql.appendSql("SELECT CODEMP FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODEMP");
			} else {
				throw new Exception("Empresa não encontrada para OS "+nuNota);
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

	private static BigDecimal getTipoOs(BigDecimal nuNota) throws Exception {
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
			
			sql.appendSql("SELECT AD_CODTIPODEOS FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODTIPODEOS");
			} else {
				throw new Exception("Tipo de OS não encontrado para OS "+nuNota);
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

	private static BigDecimal getValorCustoMecanico(BigDecimal codParcMecanico) throws Exception {
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
			
			sql.appendSql("SELECT AD_VLRCUSTO FROM TGFPAR WHERE CODPARC = "+codParcMecanico);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_VLRCUSTO");
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

	private static BigDecimal getValorVendaMecanico(BigDecimal codParcMecanico) throws Exception {
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
			
			sql.appendSql("SELECT AD_VLRVENDA FROM TGFPAR WHERE CODPARC = "+codParcMecanico);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_VLRVENDA");
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

	private static BigDecimal getValorPadraoDoServico(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT AD_VLRPADRAO FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_VLRPADRAO");
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

	private static String getBaseValorDoServico(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT AD_BASEVLRHORA FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("AD_BASEVLRHORA");
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
	
	public static BigDecimal getCodUsuMecanico(BigDecimal nuNota) throws Exception {
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
			
			sql.appendSql("SELECT AD_CODUSUEXEC FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_CODUSUEXEC");
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
