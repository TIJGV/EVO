package br.com.evonetwork.tgfite;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaCamposParaTgfite {
	CamposTgfite ite = new CamposTgfite();
	
	public void getDadosParaTgfite(BigDecimal numContrato) throws Exception {

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
			sql.appendSql("SELECT PSC.CODPROD, CODVOL FROM TCSPSC PSC "
					+ "JOIN TGFPRO PRO ON PSC.CODPROD = PRO.CODPROD "
					+ "WHERE PSC.SITPROD = 'A' AND PSC.NUMCONTRATO = " + numContrato);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				getIte().setCodprod(rset.getBigDecimal("CODPROD"));
				getIte().setCodvol(rset.getString("CODVOL"));
				getIte().setQtdneg(new BigDecimal(1));

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

	public CamposTgfite getIte() {
		return ite;
	}
}
