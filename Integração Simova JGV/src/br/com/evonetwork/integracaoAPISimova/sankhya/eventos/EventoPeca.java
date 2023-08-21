package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerPeca;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoPeca implements EventoProgramavelJava{

	//Evento na tabela > TGFPRO (Produto)
	
	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {}
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {}
	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeCommit(TransactionContext ctx) throws Exception {}
	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {}
	
	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		criarAtualizarPeca(event);
	}
	
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarPeca(event);
	}
	
	private void criarAtualizarPeca(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - PEÇA***");
		ControllerPeca.enviarPecaPorDynamicVO((DynamicVO) event.getVo());
		System.out.println("***Fim integração SIMOVA - PEÇA***");
	}
}