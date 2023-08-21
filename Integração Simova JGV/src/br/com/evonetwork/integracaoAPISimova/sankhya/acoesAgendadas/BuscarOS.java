package br.com.evonetwork.integracaoAPISimova.sankhya.acoesAgendadas;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerBuscarOs;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class BuscarOS implements ScheduledAction{

	@Override
	public void onTime(ScheduledActionContext ctx) {
		System.out.println("***Iniciando atualização de OS Sankhya-SIMOVA***");
		String urlAcesso = "https://grupojgv.h.simova.cloud/nfs/api/v1/integration/list/VIEW_INTEGRACAO_";
		try {
			//selecionar todos CODEMP e CODFILIAL
			ArrayList<String> codEmpresas = new ArrayList<String>();
			ArrayList<String> codFiliais = new ArrayList<String>();
			getEmpresasEFiliais(codEmpresas, codFiliais);
			
			for(int i = 0; i < codEmpresas.size(); i++) {
				String codEmp = codEmpresas.get(i);
				String filial = codFiliais.get(i);
				
				ArrayList<BigDecimal> numOSs = new ArrayList<BigDecimal>();
				getNumOSs(numOSs, codEmp);
				if(numOSs.size() > 0) {
					for (BigDecimal numOS : numOSs) {
						//fazer requisições na ZZS, ZZT e ZZU
						ControllerBuscarOs.receberDadosApontamento(urlAcesso, "ZZS", numOS.toString(), filial);
						//ControllerBuscarOs.receberDadosApontamento(urlAcesso, "ZZT", numOS.toString(), filial);
						//ControllerBuscarOs.receberDadosApontamento(urlAcesso, "ZZU", numOS.toString(), filial);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("***Fim da atualização de OS Sankhya-SIMOVA***");
	}

	private void getNumOSs(ArrayList<BigDecimal> numOSs, String codEmp) {
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

			sql.appendSql("SELECT NUMOS FROM AD_TCSOSE WHERE CODEMP = "+codEmp+" AND (STATUS <> 'F' OR STATUS IS NULL) AND IDINTEGRACAO IS NOT NULL");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			while (rset.next()) {
				numOSs.add(rset.getBigDecimal("NUMOS"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}

	private void getEmpresasEFiliais(ArrayList<String> codEmpresas, ArrayList<String> codFiliais) {
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

			sql.appendSql("SELECT AD_CODFILIAL, CODEMP FROM TSIEMP WHERE (AD_CODFILIAL <> '' OR AD_CODFILIAL IS NOT NULL) ORDER BY CODEMP");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			while (rset.next()) {
				codEmpresas.add(rset.getBigDecimal("CODEMP").toString());
				codFiliais.add(rset.getString("AD_CODFILIAL"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
}
