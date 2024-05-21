package br.com.evonetwork.botaodeacao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class RastreabilidadeNumeroContrato {

	ArrayList<BigDecimal> listaDeContratos = new ArrayList<BigDecimal>();
	
	public void QuantidadeDeContratos() {
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
			sql.appendSql("SELECT NUMCONTRATO FROM TCSCON");
			rset = sql.executeQuery();

			while (rset.next()) {
				getListaDeContratos().add(rset.getBigDecimal("NUMCONTRATO"));
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

	public ArrayList<BigDecimal> getListaDeContratos() {
		return listaDeContratos;
	}
	
	public String getQuantidadeDeContratos() {
		return String.valueOf(listaDeContratos.size());
	}
	public void updateAdAdicon(BigDecimal codAditivo, BigDecimal numContratoNovo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("AD_ADICON");
			DynamicVO servico = servicoExecutadoDAO.findOne("CODADITIVO = " + codAditivo);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("CONCOPIA", numContratoNovo)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
