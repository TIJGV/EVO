package br.com.evonetwork.cadastrarProdutos;

import br.com.evonetwork.cadastrarProdutos.Controller.ControllerProdutos;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class CadastrarProdutos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("**EVO - INICIANDO CADASTRO DE PRODUTOS**");
		for (int i = 0; i < ca.getLinhas().length; i++) {
            Registro linha = ca.getLinhas()[i];
            ControllerProdutos.cadastrarProdutos(ca, linha);
        }
		System.out.println("**EVO - FIM CADASTRO DE PRODUTOS**");
	}
}
