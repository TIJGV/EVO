package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerTipoOS;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoTipoOS implements EventoProgramavelJava{

	//Evento na tabela > AD_TIPOORDEMSERVICO
	
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
		criarAtualizarTipoOS(event);
	}
	
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarTipoOS(event);
	}
	
	private void criarAtualizarTipoOS(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - TIPO OS***");
		ControllerTipoOS.enviarTipoOSPorDynamicVO((DynamicVO) event.getVo());
		System.out.println("***Fim integração SIMOVA - TIPO OS***");
	}
}