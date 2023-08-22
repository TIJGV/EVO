package br.com.evonetwork.clacadpap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.clacadpapcam.GetNucampos;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ClassificacaoProspect {
	
	public static void getCodclassifcadpap(BigDecimal codpap) {
		System.out.println(">> getCodclassifcadpap");

		ArrayList<BigDecimal> codclassifcadpap = new ArrayList<>();
		
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
			sql.appendSql("SELECT CODCLASSIFCADPAP FROM AD_CLACADPAP ORDER BY ORDEM DESC");
			System.out.println(sql.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				BigDecimal cod = rset.getBigDecimal("CODCLASSIFCADPAP");
				codclassifcadpap.add(cod);
			}

			GetNucampos.getNomecampo(codclassifcadpap, codpap);

			codclassifcadpap.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			System.out.println("<< getCodclassifcadpap");

		}
	}
}
