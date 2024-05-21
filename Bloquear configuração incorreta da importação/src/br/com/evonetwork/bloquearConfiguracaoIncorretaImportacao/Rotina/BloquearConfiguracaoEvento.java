package br.com.evonetwork.bloquearConfiguracaoIncorretaImportacao.Rotina;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class BloquearConfiguracaoEvento implements EventoProgramavelJava {
	
	//Evento nas tabelas AD_CONFIGCOLIMPPRO e AD_CONFIGTXTIMPPRO

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
	public void beforeInsert(PersistenceEvent event) throws Exception {
		iniciarValidacaoDeConfiguracao(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		iniciarValidacaoDeConfiguracao(event);
	}

	private void iniciarValidacaoDeConfiguracao(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - BLOQUEAR CONFIGURAÇÃO INCORRETA IMPORTAÇÃO - INICIO***");
		DynamicVO configVO = (DynamicVO) event.getVo();
		String nomeEntidade = event.getEntity().getName();
		BigDecimal codConfig = configVO.asBigDecimal("NROUNICOCONFIG");
		String processo = buscarTipoConfigDoCabecalho(codConfig);
		System.out.println("Nome entidade: "+nomeEntidade+", Tipo config: "+processo);
		if("AD_CONFIGCOLIMPPRO".equals(nomeEntidade) && !"EXC".equals(processo))
			throw new Exception("Para criar uma configuração para Excel, favor selecionar a opção correta no campo Tipo da Configuração.");
		if("AD_CONFIGTXTIMPPRO".equals(nomeEntidade) && !"TXT".equals(processo))
			throw new Exception("Para criar uma configuração para TXT, favor selecionar a opção correta no campo Tipo da Configuração.");
		System.out.println("***EVO - BLOQUEAR CONFIGURAÇÃO INCORRETA IMPORTAÇÃO - FIM***");
	}

	private String buscarTipoConfigDoCabecalho(BigDecimal codConfig) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String dado = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT TIPOCONFIG FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+codConfig);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("TIPOCONFIG");
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
		return dado;
	}

}
