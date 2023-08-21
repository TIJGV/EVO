package br.com.evonetwork.validarQtdDaTransferencia;

import br.com.evonetwork.transferirItensDeUmaOS.Controller.ControllerValidacaoQuantidade;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class ValidarQtdDaTransferencia implements EventoProgramavelJava {

	// evento na TGFITE
	
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
		validarQtdDaTransferencia(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		validarQtdDaTransferencia(event);
	}

	private void validarQtdDaTransferencia(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - VALIDAR QTD DA TRANSFERENCIA - INICIO***");
		try {
			ControllerValidacaoQuantidade.validar(event);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VALIDAR QTD DA TRANSFERENCIA - FIM***");
	}

}
