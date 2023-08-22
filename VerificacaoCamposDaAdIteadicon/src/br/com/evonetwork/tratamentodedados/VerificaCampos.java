package br.com.evonetwork.tratamentodedados;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class VerificaCampos {
	
	private String tipCampoJava;
	private String descrCampo;
	private String tipCampoSql;
	private String tipCampo;
	private String nomeCampo;
	
	public void TipoDeCampo(BigDecimal campoAlter) {

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
			sql.appendSql("SELECT NOMECAMPO, TIPCAMPO, DESCRCAMPO, CASE "
					+ "WHEN TIPCAMPO = 'D' THEN 'Data' "
					+ "WHEN TIPCAMPO = 'F' THEN 'BigDecimal' "
					+ "WHEN TIPCAMPO = 'I' THEN 'BigDecimal' "
					+ "WHEN TIPCAMPO = 'S' THEN 'String' "
					+ "WHEN TIPCAMPO = 'T' THEN 'String' "
					+ "END AS TIPCAMPOJAVA "
					+ ",CASE WHEN TIPCAMPO = 'B' THEN 'Conteúdo Binário'"
					+ " WHEN TIPCAMPO = 'C' THEN 'Texto Longo (CLOB)'"
					+ " WHEN TIPCAMPO = 'D' THEN 'Data'"
					+ " WHEN TIPCAMPO = 'F' THEN 'Número Decimal'"
					+ " WHEN TIPCAMPO = 'H' THEN 'Data e Hora'"
					+ " WHEN TIPCAMPO = 'I' THEN 'Número Inteiro'"
					+ " WHEN TIPCAMPO = 'S' THEN 'Texto'"
					+ " WHEN TIPCAMPO = 'T' THEN 'Hora'"
					+ " END AS TIPCAMPOSQL"
					+ " FROM TDDCAM WHERE NOMETAB = 'TCSCON' AND NUCAMPO = " + campoAlter);
			rset = sql.executeQuery();

			while (rset.next()) {
				this.tipCampoJava = rset.getString("TIPCAMPOJAVA");
				this.tipCampoSql= rset.getString("TIPCAMPOSQL");
				this.descrCampo = rset.getString("DESCRCAMPO");
				this.tipCampo = rset.getString("TIPCAMPO");
				this.nomeCampo = rset.getString("NOMECAMPO");
			}
		} catch (
		Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}

	public String getTipCampoJava() {
		return tipCampoJava;
	}

	public void setTipCampoJava(String tipCampoJava) {
		this.tipCampoJava = tipCampoJava;
	}

	public String getDescrCampo() {
		return descrCampo;
	}

	public void setDescrCampo(String descrCampo) {
		this.descrCampo = descrCampo;
	}

	public String getTipCampoSql() {
		return tipCampoSql;
	}

	public void setTipCampoSql(String tipCampoSql) {
		this.tipCampoSql = tipCampoSql;
	}

	public String getTipCampo() {
		return tipCampo;
	}

	public void setTipCampo(String tipCampo) {
		this.tipCampo = tipCampo;
	}

	public String getNomeCampo() {
		return nomeCampo;
	}

	public void setNomeCampo(String nomeCampo) {
		this.nomeCampo = nomeCampo;
	}
	@Override
	public String toString() {
		return "tipCampoJava" + tipCampoJava +
				"descrCampo" + descrCampo +
				"tipCampoSql" + tipCampoSql +
				"tipCampo" + tipCampo +
				"nomeCampo" + nomeCampo;
	}
}
