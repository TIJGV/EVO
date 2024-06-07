package br.com.evonetwork.integracaoCamposDealer;

import br.com.evonetwork.integracaoCamposDealer.Controller.ProdutoController;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class IntegraProdutoMarcaModelo implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		Registro[] linha = ca.getLinhas();
		for (Registro r : linha) {
			ProdutoController.integraProduto(r, ca);
		}
	}

}
