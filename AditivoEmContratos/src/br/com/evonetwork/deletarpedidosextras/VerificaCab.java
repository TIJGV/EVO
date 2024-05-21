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

public class VerificaCab {
	
	ArrayList<DadosPedidosExtras> cabExtra = new ArrayList<>();
	
	public void CabExtras(BigDecimal numContrato) throws Exception {
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
			sql.appendSql("SELECT CAB.NUNOTA,CAB.CODEMP, FIN.NUFIN,FIN.DTNEG, CAB.CODTIPOPER "
					+ "FROM TGFCAB CAB JOIN TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA "
					+ "JOIN TGFFIN FIN ON CAB.NUNOTA = FIN.NUNOTA WHERE CAB.NUMCONTRATO = " + numContrato
					+ "AND CAB.DTFATUR IS NULL");
			System.out.println("query verifica cab");
			System.out.println(sql.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				DadosPedidosExtras dpe = new DadosPedidosExtras();
				dpe.setCodemp(rset.getBigDecimal("CODEMP"));
				dpe.setDtneg(rset.getString("DTNEG"));
				dpe.setNunota(rset.getBigDecimal("NUNOTA"));
				dpe.setCodtipoper(rset.getBigDecimal("CODTIPOPER"));
				getCabExtra().add(dpe);
				
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

	public ArrayList<DadosPedidosExtras> getCabExtra() {
		return cabExtra;
	}
}
