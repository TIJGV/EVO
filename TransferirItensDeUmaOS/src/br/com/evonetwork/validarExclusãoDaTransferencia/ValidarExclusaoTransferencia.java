package br.com.evonetwork.validarExclusãoDaTransferencia;

import br.com.evonetwork.transferirItensDeUmaOS.Controller.ControllerValidacaoExclusao;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class ValidarExclusaoTransferencia implements EventoProgramavelJava {

	// evento na TGFCAB
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - VALIDAR EXCLUSAO DA TRANSFERENCIA - INICIO***");
		try {
			ControllerValidacaoExclusao.validar(event);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VALIDAR EXCLUSAO DA TRANSFERENCIA - FIM***");
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {}

}
