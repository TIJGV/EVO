package br.com.evonetwork.adtcsose;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AdTcsose {
	public static DadosParaCab dadosParaCab(BigDecimal numos) throws Exception {
		System.out.println("dadosParaCab >>");

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		DadosParaCab cab = new DadosParaCab();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT  ");
			sb.append(" OSE.AD_NUNOTASIMDESC AS NUNOTA");
			sb.append(" ,OSE.CODPARC ");
			sb.append(" ,OSE.CODCENCUS ");
			sb.append(" ,OSE.CODNAT ");
			sb.append(" ,OSE.CODEMP ");
			sb.append(" ,NULL AS CODTIPVENDA ");
			sb.append(" ,NULL AS DTNEG ");
			sb.append(" ,NULL AS DTENTSAI ");
			sb.append(" ,NULL AS SERIENOTA ");
			sb.append(" ,9999 AS CODTIPOPER ");
			sb.append(" ,(SELECT NVL(MAX(NUMNOTA) + 1,1) AS NUMNOTA FROM TGFCAB ");
			sb.append(" WHERE STATUSNOTA = 'L' AND CODTIPOPER = 9999) AS NUMNOTA ");
			sb.append(" FROM AD_TCSOSE OSE WHERE NUMOS = " + numos);
			System.out.println("SQL: " + sb.toString());
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				cab.setNunota(rset.getBigDecimal("NUNOTA"));
				cab.setCodparc(rset.getBigDecimal("CODPARC"));
				cab.setCodcencus(rset.getBigDecimal("CODCENCUS"));
				cab.setCodnat(rset.getBigDecimal("CODNAT"));
				cab.setCodemp(rset.getBigDecimal("CODEMP"));

//				cab.setCodtipvenda(rset.getBigDecimal("CODTIPVENDA"));
				cab.setCodtipvenda(new BigDecimal(25));

//				cab.setDtneg(rset.getTimestamp("DTNEG"));
				cab.setDtneg(Utils.obterHoraAtual());

//				cab.setDtentsai(rset.getTimestamp("DTENTSAI"));
				cab.setDtentsai(Utils.obterHoraAtual());

//				cab.setSerienota(rset.getString("SERIENOTA"));
				cab.setSerienota("");

				cab.setCodtipoper(new BigDecimal(rset.getString("CODTIPOPER")));
				cab.setNumnota(rset.getBigDecimal("NUMNOTA"));
				cab.setStatusnota("P");
				System.out.println(cab.toString());
				System.out.println("dadosParaCab <<");
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
		return cab;
	}

	public static void updateAdNunotasimdesc(BigDecimal numos, BigDecimal nunotasimdesc) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("TCSOSE");
			DynamicVO servico = servicoExecutadoDAO.findOne("NUMOS = " + numos);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("AD_NUNOTASIMDESC", nunotasimdesc)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static BigDecimal vlrtotal(BigDecimal numos) throws Exception {
		System.out.println("vlrtotal >>");
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal vlrtotal = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT  ");
			sb.append(" SUM(VLRUNIT * QUANTIDADE) AS VLRNOTA ");
			sb.append(" FROM AD_TCSPRO  ");
			sb.append(" WHERE NUMOS =  " + numos);
			System.out.println("SQL: " + sb.toString());
			
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				vlrtotal = rset.getBigDecimal("VLRNOTA");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			System.out.println("vlrtotal <<");
		}
		return vlrtotal;
	}
}
