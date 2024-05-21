package br.com.evonetwork.deletarpedidosextras;

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

public class VerificaVar {

	ArrayList<DadosPedidosExtras> varExtra = new ArrayList<>();

	public void VarExtras(BigDecimal numContrato) throws Exception {
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
			sql.appendSql("SELECT CAB.CODEMP ,CAB.DTNEG ,CAB.NUNOTA, CAB.CODTIPOPER FROM TGFVAR VAR "
					+ "JOIN TGFCAB CAB ON VAR.NUNOTAORIG = CAB.NUNOTA WHERE CAB.NUMCONTRATO = " + numContrato);
			System.out.println("query verifica var");
			System.out.println(sql.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				DadosPedidosExtras dpe = new DadosPedidosExtras();
				dpe.setCodemp(rset.getBigDecimal("CODEMP"));
				dpe.setDtneg(rset.getString("DTNEG"));
				dpe.setNunota(rset.getBigDecimal("NUNOTA"));
				dpe.setCodtipoper(rset.getBigDecimal("CODTIPOPER"));

				getVarExtra().add(dpe);
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

	public ArrayList<DadosPedidosExtras> getVarExtra() {
		return varExtra;
	}
}
