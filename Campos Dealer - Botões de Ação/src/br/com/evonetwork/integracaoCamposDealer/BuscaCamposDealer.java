package br.com.evonetwork.integracaoCamposDealer;

import br.com.evonetwork.integracaoCamposDealer.Controller.ProspectController;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class BuscaCamposDealer implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		try {
			ProspectController.iniciarBusca(ca);
		} catch (Exception e) {
			ca.setMensagemRetorno("Erro na execução: "+e.getMessage());
			System.out.println("Erro na execução: "+e.getMessage());
			e.printStackTrace();
		}
		
	}

}
