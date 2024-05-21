package br.com.evonetwork.tgfite;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GetDadosTgfite {
	public static ArrayList<DadosTgfite> itensDaNota(BigDecimal numos) throws Exception {
		System.out.println("GetDadosTgfite >>");
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		ArrayList<DadosTgfite> iteList = new ArrayList<>();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT ");
			sb.append(" CODPROD ");
			sb.append(" ,CODVOL ");
			sb.append(" ,VLRUNIT ");
			sb.append(" ,QUANTIDADE ");
			sb.append(" ,SEQUENCIA ");
			sb.append(" , (VLRUNIT * QUANTIDADE) AS VLRTOT ");
			sb.append(",DESCONTOREAIS , DESCONTOPCTGM ");
			sb.append(" FROM AD_TCSPRO ");
			sb.append(" WHERE NUMOS = " + numos);
			System.out.println("SQL: " + sb.toString());
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				DadosTgfite ite = new DadosTgfite();
				ite.setCodprod(rset.getBigDecimal("CODPROD"));
				ite.setSequencia(rset.getBigDecimal("SEQUENCIA"));
				ite.setCodvol(rset.getString("CODVOL"));
				ite.setVlrunit(rset.getBigDecimal("VLRUNIT"));
				ite.setQtdneg(rset.getBigDecimal("QUANTIDADE"));
				ite.setVlrtot(rset.getBigDecimal("VLRTOT"));
				ite.setDescontopctgm(rset.getBigDecimal("DESCONTOPCTGM"));
				ite.setDescontoreais(rset.getBigDecimal("DESCONTOREAIS"));
				System.out.println(ite.toString());
				iteList.add(ite);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			System.out.println("GetDadosTgfite <<");
		}
		return iteList;
	}
	
	public static BigDecimal somaDescontoReais(BigDecimal numos) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal descontoReais = null;
		
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			StringBuilder sb = new StringBuilder();
			
			sb.append("SELECT SUM(DESCONTOREAIS) AS DESCONTOREAIS FROM AD_TCSPRO WHERE NUMOS = " + numos);

			System.out.println("SQL: " + sb.toString());
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				descontoReais = rset.getBigDecimal("DESCONTOREAIS");
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
		return descontoReais;
	}

}
