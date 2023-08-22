package VerificaTipoContrato;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CodempParceiro {
	public static BigDecimal getCodempParceiro(BigDecimal numContrato) throws Exception {
		BigDecimal codemp = new BigDecimal(0);
		
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
			sql.appendSql("SELECT DISTINCT CODEMPMATRIZ FROM TSIEMP WHERE CODPARC = "
						+ "(SELECT CODPARC FROM TCSCON WHERE NUMCONTRATO = " + numContrato +")");
			System.out.println(sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				codemp = rset.getBigDecimal("CODEMPMATRIZ");
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
		return codemp;
	}
}
