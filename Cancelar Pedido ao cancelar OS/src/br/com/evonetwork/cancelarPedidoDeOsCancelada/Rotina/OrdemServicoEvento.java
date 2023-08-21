package br.com.evonetwork.cancelarPedidoDeOsCancelada.Rotina;

import br.com.evonetwork.cancelarPedidoDeOsCancelada.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class OrdemServicoEvento implements EventoProgramavelJava{

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
		System.out.println("***EVO - CANCELAR PEDIDO DE OS CANCELADA - INICIO***");
		try {
			Controller.verificarSeOrdemServicoEstaCancelada(event);
		} catch(Exception e) {
			System.out.println("Erro: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - CANCELAR PEDIDO DE OS CANCELADA - FIM***");
	}

}
