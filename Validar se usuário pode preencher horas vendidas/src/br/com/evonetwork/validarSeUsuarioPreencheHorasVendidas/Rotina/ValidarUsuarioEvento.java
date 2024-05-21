package br.com.evonetwork.validarSeUsuarioPreencheHorasVendidas.Rotina;

import br.com.evonetwork.validarSeUsuarioPreencheHorasVendidas.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class ValidarUsuarioEvento implements EventoProgramavelJava {

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
		verificarNoInsert(event);
	}
	
	private void verificarNoInsert(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - VALIDAR SE USUÁRIO PODE PREENCHER HORAS VENDIDAS (INSERT) - INICIO***");
		try {
			Controller.iniciarValidacaoParaInsert(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VALIDAR SE USUÁRIO PODE PREENCHER HORAS VENDIDAS (INSERT) - FIM***");
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		verificarNoUpdate(event);
	}

	private void verificarNoUpdate(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - VALIDAR SE USUÁRIO PODE PREENCHER HORAS VENDIDAS (UPDATE) - INICIO***");
		try {
			Controller.iniciarValidacaoParaUpdate(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VALIDAR SE USUÁRIO PODE PREENCHER HORAS VENDIDAS (UPDATE) - FIM***");
	}

}
