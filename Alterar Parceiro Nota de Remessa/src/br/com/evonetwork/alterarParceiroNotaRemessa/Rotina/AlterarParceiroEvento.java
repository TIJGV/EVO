package br.com.evonetwork.alterarParceiroNotaRemessa.Rotina;

import br.com.evonetwork.alterarParceiroNotaRemessa.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class AlterarParceiroEvento implements EventoProgramavelJava {

	//Evento na TGFCAB
	
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
		System.out.println("***EVO - ALTERAR PARCEIRO DA NOTA DE REMESSA - INICIO***");
		Controller.iniciarAlteracao(event);
		System.out.println("***EVO - ALTERAR PARCEIRO DA NOTA DE REMESSA - FIM***");
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {}

}