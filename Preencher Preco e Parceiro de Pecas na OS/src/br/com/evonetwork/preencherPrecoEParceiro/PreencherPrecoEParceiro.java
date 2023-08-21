package br.com.evonetwork.preencherPrecoEParceiro;

import br.com.evonetwork.preencherPrecoEParceiro.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class PreencherPrecoEParceiro implements EventoProgramavelJava{
	
	// evento na AD_TCSPRO

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
		iniciarBuscaDePreco(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		iniciarBuscaDePreco(event);
	}

	private void iniciarBuscaDePreco(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - BUSCAR PRECO DE PEÇA E PARCEIRO - INICIO***");
		try {
			Controller.buscarPreco(event);
			Controller.buscarParceiro(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - BUSCAR PRECO DE PEÇA E PARCEIRO - FIM***");
	}

}
