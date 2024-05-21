package br.com.evonetwork.verificarpedidospendentes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaCamposParaTgfcab {
	ArrayList<CamposTgfcab> listaCab = new ArrayList<>();
	BigDecimal linhas = new BigDecimal(0);

	public void getDadosParaTgfcab(BigDecimal numContrato) throws Exception {
		System.out.println("inside getDadosParaTgfcab");
		verificaSql(numContrato);

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		try {
			Calendar c = Calendar.getInstance();

			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			if (linhas.compareTo(new BigDecimal(0)) > 0) {
				sql.appendSql(
						"SELECT (AD_VLRCONTRATO * PERCRATEIO/100) AS VLRTOT, CODTIPVENDA, CODMONSANKHYA, TOPFATURCON, OBSERVACOES "
								+ ",RAV.CODCENCUS, RAV.CODNAT, CON.CODPARC, CON.DTCONTRATO , CON.DTTERMINO,"
								+ " (SELECT AD_CODEMP FROM TSICUS WHERE CODCENCUS = RAV.CODCENCUS) AS CODEMP"
								+ " FROM TGFCRI CRI JOIN TGFRAV RAV ON CRI.CODCRITERIO = RAV.NUFIN "
								+ "JOIN TCSCON CON ON CRI.NUMCONTRATO = CON.NUMCONTRATO "
								+ "WHERE RAV.ORIGEM = 'C' AND CRI.NUMCONTRATO = " + numContrato);
			} else {
				sql.appendSql("SELECT AD_VLRCONTRATO AS VLRTOT, OBSERVACOES, CODTIPVENDA, CODMONSANKHYA, TOPFATURCON ,CON.CODCENCUS,"
						+ " CON.CODNAT, CON.CODPARC, CON.DTCONTRATO ,CON.DTTERMINO,"
						+ "(SELECT AD_CODEMP FROM TSICUS WHERE CODCENCUS = CON.CODCENCUS) AS CODEMP"
						+ " FROM TCSCON CON WHERE CON.NUMCONTRATO = " + numContrato);
			}
			System.out.println(sql.toString());
			rset = sql.executeQuery();

			while (rset.next()) {
				CamposTgfcab cab = new CamposTgfcab();
				cab.setDtentsai(rset.getString("DTCONTRATO"));
				cab.setDttermino(rset.getString("DTTERMINO"));
				cab.setDtneg(rset.getString("DTCONTRATO"));
				cab.setQtMeses(Utils.calculaMeses(cab.getDtneg(), cab.getDttermino()));
				cab.setMesesExtras(Utils.calculaMeses(new Timestamp(c.getTimeInMillis()), cab.getDttermino()));
				cab.setVlrtot(rset.getBigDecimal("VLRTOT"));
				cab.setCodcencus(rset.getBigDecimal("CODCENCUS"));
				cab.setCodemp(rset.getBigDecimal("CODEMP"));
				cab.setCodnat(rset.getBigDecimal("CODNAT"));
				cab.setCodparc(rset.getBigDecimal("CODPARC"));
				cab.setCodtipoper(rset.getBigDecimal("TOPFATURCON"));
				cab.setCodtipvenda(rset.getBigDecimal("CODTIPVENDA"));
				cab.setNumnota(new BigDecimal(0));
				cab.setCodusucomprador(rset.getBigDecimal("CODMONSANKHYA"));
				cab.setObservacao("Pedido do contrato número " + numContrato + ".\n" + rset.getString("OBSERVACOES"));
				cab.setSerienota("1");
				cab.setNumcontrato(numContrato);
				listaCab.add(cab);
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

	public void verificaSql(BigDecimal numContrato) throws Exception {

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
			sql.appendSql("SELECT COUNT(*) AS COUNT FROM TGFCRI CRI "
					+ "JOIN TGFRAV RAV ON CRI.CODCRITERIO = RAV.NUFIN JOIN TCSCON CON ON "
					+ "CRI.NUMCONTRATO = CON.NUMCONTRATO WHERE RAV.ORIGEM = 'C' AND CRI.NUMCONTRATO = " + numContrato);

			rset = sql.executeQuery();

			while (rset.next()) {
				linhas = rset.getBigDecimal("COUNT");
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

	public ArrayList<CamposTgfcab> getListaCab() {
		return listaCab;
	}
}
