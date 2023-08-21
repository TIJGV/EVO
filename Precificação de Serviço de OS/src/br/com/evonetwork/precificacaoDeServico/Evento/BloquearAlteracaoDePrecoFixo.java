package br.com.evonetwork.precificacaoDeServico.Evento;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.precificacaoDeServico.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class BloquearAlteracaoDePrecoFixo implements EventoProgramavelJava {

	// Evento na AD_TCSITE
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - BLOQUEIO DE ALTERAÇÃO DO PREÇO FIXO - INICIO***");
		DynamicVO servicoExecutadoVO = (DynamicVO) event.getVo();
		ModifingFields md = event.getModifingFields();
		if (md.isModifing("VALORSERVICO") && servicoTemPrecoFixo(servicoExecutadoVO) && !valorDoServicoEstaCorreto(servicoExecutadoVO)) {
			throw new Exception("Não é permitido alterar o valor de um Serviço que possui Preço Fixo!");
		}
		System.out.println("***EVO - BLOQUEIO DE ALTERAÇÃO DO PREÇO FIXO - FIM***");
	}

	private boolean valorDoServicoEstaCorreto(DynamicVO servicoExecutadoVO) throws Exception {
		BigDecimal valorServico = servicoExecutadoVO.asBigDecimal("VALORSERVICO");
		BigDecimal codServ = servicoExecutadoVO.asBigDecimal("CODSERV");
		BigDecimal codParcMecanico = Controller.getCodParcDoUsuario(servicoExecutadoVO.asBigDecimal("CODUSURESP"));
		StringBuilder str = new StringBuilder();
		str.append("");
		BigDecimal valorPrecoFixo = Controller.getValor(Controller.getBaseValor(codServ), Controller.getValorPadrao(codServ), codParcMecanico, str, servicoExecutadoVO);
		if(valorServico.compareTo(valorPrecoFixo) == 0)
			return true;
		return false;
	}

	private boolean servicoTemPrecoFixo(DynamicVO servicoExecutadoVO) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String tipoCalc = "";
		String baseVlr = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_TIPOCALCPRECO, AD_BASEVLRHORA FROM TGFPRO WHERE CODPROD = "+servicoExecutadoVO.asBigDecimal("CODSERV"));
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				tipoCalc = rset.getString("AD_TIPOCALCPRECO");
				baseVlr = rset.getString("AD_BASEVLRHORA");
				if("F".equals(tipoCalc) && !"M".equals(baseVlr))
					return true;
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
		return false;
	}

}
