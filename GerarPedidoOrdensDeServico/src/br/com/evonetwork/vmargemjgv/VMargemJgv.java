package br.com.evonetwork.vmargemjgv;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.adtcsose.DadosParaCab;
import br.com.evonetwork.utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class VMargemJgv {
	public static BigDecimal getAdMargem(BigDecimal nunota, BigDecimal sequencia) throws Exception {
		
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal admargem = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT  ");
			sb.append(" CASE WHEN (VLRTOTLIQ)*100 = 0 THEN 0 ELSE ");
			sb.append(" ((VLRTOTLIQ)- ");
			sb.append(" (GVTOTAL)- ");
			sb.append(" (CMV))/(VLRTOTLIQ)*100 END AS ADMARGEM");
			sb.append(" FROM V_MARGEM_JGV  ");
			sb.append(" WHERE NUNOTA = " + nunota);
			sb.append(" AND SEQUENCIA = " + sequencia);
			
			System.out.println("SQL: " + sb.toString());
			sql.appendSql(sb.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				admargem = rset.getBigDecimal("ADMARGEM");
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
		return admargem;
	}
}
