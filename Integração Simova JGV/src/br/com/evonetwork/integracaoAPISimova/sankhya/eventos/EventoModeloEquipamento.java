package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerModeloEquipamento;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoModeloEquipamento implements EventoProgramavelJava{

	//Evento na tabela > AD_MODELOVEI
	
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
		criarAtualizarModeloEquipamento(event);
	}
	
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarModeloEquipamento(event);
	}
	
	private void criarAtualizarModeloEquipamento(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - MODELO EQUIPAMENTO***");
		ControllerModeloEquipamento.enviarModeloEquipamentoPorDynamicVO((DynamicVO) event.getVo());
		System.out.println("***Fim integração SIMOVA - MODELO EQUIPAMENTO***");
	}
}