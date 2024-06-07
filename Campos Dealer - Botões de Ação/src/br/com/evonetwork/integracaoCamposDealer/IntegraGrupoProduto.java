package br.com.evonetwork.integracaoCamposDealer;

import br.com.evonetwork.integracaoCamposDealer.Controller.GrupoProdutoController;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class IntegraGrupoProduto implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		Registro[] linha = ca.getLinhas();
		for (Registro r : linha) {
			GrupoProdutoController.integraGrupo(r, ca);
		}
	}

}
