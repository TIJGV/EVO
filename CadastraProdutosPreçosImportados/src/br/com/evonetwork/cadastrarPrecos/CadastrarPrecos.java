package br.com.evonetwork.cadastrarPrecos;

import br.com.evonetwork.cadastrarPrecos.Controller.ControllerPrecos;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class CadastrarPrecos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("**EVO - INICIANDO CADASTRO DE PREÇOS**");
		for (int i = 0; i < ca.getLinhas().length; i++) {
			Registro linha = ca.getLinhas()[i];
			ControllerPrecos.cadastrarPrecos(ca, linha);
		}
		System.out.println("**EVO - FIM CADASTRO DE PREÇOS**");
	}
}
