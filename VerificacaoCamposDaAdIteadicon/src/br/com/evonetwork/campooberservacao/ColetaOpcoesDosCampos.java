package br.com.evonetwork.campooberservacao;

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

public class ColetaOpcoesDosCampos {
	ArrayList<BigDecimal> listaNucampoComOpcoes = new ArrayList<>();
	private BigDecimal nuCampo;
	
	public void ColetaCamposComOpcoes() {

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
			sql.appendSql("SELECT DISTINCT OPC.NUCAMPO FROM TDDCAM CAM"
					+ " JOIN TDDOPC OPC ON OPC.NUCAMPO = CAM.NUCAMPO WHERE CAM.NOMETAB = 'TCSCON'");
			rset = sql.executeQuery();
			while (rset.next()) {
				this.nuCampo = rset.getBigDecimal("NUCAMPO");
				getListaNucampoComOpcoes().add(this.nuCampo);
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

	public ArrayList<BigDecimal> getListaNucampoComOpcoes() {
		return listaNucampoComOpcoes;
	}
}
