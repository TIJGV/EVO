package br.com.evonetwork.tgfcab;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.adtcsose.AdTcsose;
import br.com.evonetwork.adtcsose.DadosParaCab;
import br.com.evonetwork.tgfite.GetDadosTgfite;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Tgfcab {

	@SuppressWarnings("unused")
	public static BigDecimal criarCab(DadosParaCab tgfcab, BigDecimal numos) throws Exception {
		System.out.println("criarCab >>");
		
		String nomeDaInstancia = "CabecalhoNota";
		
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance(nomeDaInstancia);
		DynamicVO cab = (DynamicVO) entityVO;

		cab.setProperty("CODPARC", tgfcab.getCodparc());
		cab.setProperty("CODNAT", tgfcab.getCodnat());
		cab.setProperty("CODEMP", tgfcab.getCodemp());
		cab.setProperty("CODTIPVENDA", tgfcab.getCodtipvenda());
		cab.setProperty("NUMNOTA", tgfcab.getNumnota());
		cab.setProperty("DTNEG", tgfcab.getDtneg());
		cab.setProperty("DTENTSAI", tgfcab.getDtentsai());
		cab.setProperty("SERIENOTA", "");
		cab.setProperty("AD_CODOAT", new BigDecimal(6));
		cab.setProperty("CODTIPOPER", tgfcab.getCodtipoper());
		cab.setProperty("CODCENCUS", tgfcab.getCodcencus());
		cab.setProperty("NUMNOTA", tgfcab.getNumnota());
		cab.setProperty("VLRNOTA", AdTcsose.vlrtotal(numos));
		cab.setProperty("AD_NUMOS", numos);

		PersistentLocalEntity createEntity = dwfFacade.createEntity(nomeDaInstancia, entityVO);

		DynamicVO save = (DynamicVO) createEntity.getValueObject();
		BigDecimal nunota = save.asBigDecimal("NUNOTA");

		System.out.println("criarCab <<");
		return nunota;
	}
	
	public static void updateVlrdesctotitem(BigDecimal nunota, BigDecimal numos) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("CabecalhoNota");
			DynamicVO servico = servicoExecutadoDAO.findOne("nunota = " + nunota);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("VLRDESCTOTITEM", GetDadosTgfite.somaDescontoReais(numos))
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	public static void updateVlrnota(BigDecimal numos, BigDecimal nunota) throws Exception {
		System.out.println("updateVlrnota >> ");
		JapeSession.SessionHandle hnd = null;
		BigDecimal vlrnota = AdTcsose.vlrtotal(numos);

		System.out.println("vlrnota: " + vlrnota);
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("CabecalhoNota");
			DynamicVO servico = servicoExecutadoDAO.findOne("NUNOTA = " + nunota);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("VLRNOTA", vlrnota)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
			System.out.println("updateVlrnota <<");
		}
	}
	
	public static Boolean nunotaExiste(BigDecimal nunota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		Boolean nunotaExiste = false;
		BigDecimal validacao = null;
		
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			String select = "SELECT 1 AS VALIDACAO FROM TGFCAB WHERE NUNOTA = " + nunota;

			sql.appendSql(select);
			rset = sql.executeQuery();

			while (rset.next()) {
				validacao = rset.getBigDecimal("VALIDACAO");
			}

			if(validacao != null) {
				nunotaExiste = true;
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
		return nunotaExiste;
	}

}
