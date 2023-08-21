package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerEquipamento;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoEquipamento implements EventoProgramavelJava{

	//Evento na tabela > TGFVEI
	
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
		criarAtualizarEquipamento(event);
	}
	
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarEquipamento(event);
	}
	
	private void criarAtualizarEquipamento(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - EQUIPAMENTO***");
		ControllerEquipamento.enviarEquipamentoPorDynamicVO((DynamicVO) event.getVo());
		System.out.println("***Fim integração SIMOVA - EQUIPAMENTO***");
	}
}