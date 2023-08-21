package br.com.evonetwork.precificacaoDeServico.Evento;

import br.com.evonetwork.precificacaoDeServico.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class PrecificacaoDeServico implements EventoProgramavelJava {

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
	public void beforeInsert(PersistenceEvent event) throws Exception {
		iniciarPrecificacao(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		iniciarPrecificacao(event);
	}

	private void iniciarPrecificacao(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - PRECIFICAÇÃO DO SERVIÇO - INICIO***");
		DynamicVO servicoExecutadoVO = (DynamicVO) event.getVo();
		Controller.gerenciarPrecificacao(servicoExecutadoVO);
		System.out.println("***EVO - PRECIFICAÇÃO DO SERVIÇO - FIM***");
	}

}
