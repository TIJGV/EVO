package br.com.evonetwork.tgffin;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaCamposParaTgffin {
	CamposTgffin fin = new CamposTgffin();
	
	public void getDadosParaTgffin(BigDecimal nuNota) throws Exception {
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
			sql.appendSql("SELECT CAB.CODCENCUS,CAB.CODEMP,CAB.CODMOEDA,CAB.CODNAT,CAB.CODPARC,"
					+ "(SELECT TIPOTITULO FROM TCSCON WHERE NUMCONTRATO = ITE.NUMCONTRATO) AS CODTIPTIT,"
					+ "ITE.NUMCONTRATO,CAB.NUMNOTA,CAB.NUNOTA,VLRTOT,CAB.DTNEG,"
					+ "(SELECT DIAPAG FROM TCSCON WHERE NUMCONTRATO = ITE.NUMCONTRATO) AS DIAPAG,"
					+ "(SELECT TIPATUALFIN FROM TGFTOP WHERE CODTIPOPER = CAB.CODTIPOPER AND DHALTER = CAB.DHTIPOPER) AS PROVISAO,"
					+ "(SELECT ATUALFIN FROM TGFTOP WHERE CODTIPOPER = CAB.CODTIPOPER AND DHALTER = CAB.DHTIPOPER) AS RECDESP,"
					+ "(SELECT DTCONTRATO FROM TCSCON WHERE NUMCONTRATO = ITE.NUMCONTRATO) AS DTVENC"
					+ " FROM TGFCAB CAB JOIN TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA WHERE CAB.NUNOTA = " + nuNota);

			rset = sql.executeQuery();

			while (rset.next()) {
				fin.setCodcencus(rset.getBigDecimal("CODCENCUS"));
				fin.setCodemp(rset.getBigDecimal("CODEMP"));
				fin.setCodmoeda(rset.getBigDecimal("CODMOEDA"));
				fin.setCodnat(rset.getBigDecimal("CODNAT"));
				fin.setCodparc(rset.getBigDecimal("CODPARC"));
				fin.setCodtiptit(rset.getBigDecimal("CODTIPTIT"));
				fin.setNumcontrato(rset.getBigDecimal("NUMCONTRATO"));
				fin.setNumnota(rset.getBigDecimal("NUMNOTA"));
				fin.setNunota(rset.getBigDecimal("NUNOTA"));
				fin.setRecdesp(rset.getBigDecimal("RECDESP"));
				fin.setVlrdesdob(rset.getBigDecimal("VLRTOT"));
				fin.setDtneg(rset.getString("DTNEG"));
				fin.setDtvenc(rset.getString("DTVENC"));
				fin.setOrigem("E");
				fin.setProvisao(rset.getString("PROVISAO"));
				fin.setDiapag(rset.getBigDecimal("DIAPAG"));
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

	public CamposTgffin getFin() {
		return fin;
	}
}
