package br.com.evonetwork.validarUsuarioApontamentos;

import br.com.evonetwork.validarUsuarioApontamentos.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class ValidarUsuarioApontamentos implements EventoProgramavelJava{
	
	// evento na AD_APONTAMENTOS

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
		iniciarValidacao(event);
	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		iniciarValidacao(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		Controller.desativarFlagApontamentoAutomatico(event);
		iniciarValidacao(event);
	}

	private void iniciarValidacao(PersistenceEvent event) throws Exception {
		System.out.println("***INICIO - VALIDAR USUARIO ALTERANDO APONTAMENTOS - EVO***");
		try {
			Controller.validarUsuario(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***FIM - VALIDAR USUARIO ALTERANDO APONTAMENTOS - EVO***");
	}

}
