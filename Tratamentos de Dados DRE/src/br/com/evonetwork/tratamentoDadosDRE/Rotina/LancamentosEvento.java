package br.com.evonetwork.tratamentoDadosDRE.Rotina;

import br.com.evonetwork.tratamentoDadosDRE.Controller.TratamentoDadosController;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class LancamentosEvento implements EventoProgramavelJava{

	//Evento na TCBLAN
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - TRATAR DADOS DE LANÇAMENTO PARA DRE (INSERT) - INICIO***");
		try {
			TratamentoDadosController.tratarDados(event);
		} catch (Exception e) {
			System.out.println("Erro: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - TRATAR DADOS DE LANÇAMENTO PARA DRE (INSERT) - FIM***");
	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - TRATAR DADOS DE LANÇAMENTO PARA DRE (UPDATE) - INICIO***");
		try {
			TratamentoDadosController.tratarDados(event);
		} catch (Exception e) {
			System.out.println("Erro: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - TRATAR DADOS DE LANÇAMENTO PARA DRE (UPDATE) - FIM***");
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {}

}
