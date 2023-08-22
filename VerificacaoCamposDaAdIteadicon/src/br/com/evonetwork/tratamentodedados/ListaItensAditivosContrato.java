package br.com.evonetwork.tratamentodedados;

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

public class ListaItensAditivosContrato {
	ArrayList<BigDecimal> listaCampoAlter = new ArrayList<>();
	
	public void ListaDeItensAditivos(BigDecimal codAditivo) {

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
			sql.appendSql("SELECT CAMPOALTER FROM AD_ITEADICON WHERE CODADITIVO = " + codAditivo);
			rset = sql.executeQuery();
			System.out.println("TESTE 500 - LISTA CAMPOALTER");
			while (rset.next()) {
				BigDecimal campoAlter = rset.getBigDecimal("CAMPOALTER");
				System.out.println(campoAlter);
				listaCampoAlter.add(campoAlter);
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

	public ArrayList<BigDecimal> getListaCampoAlter() {
		return listaCampoAlter;
	}

}
