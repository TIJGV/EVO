package br.com.evonetwork.cadastrarProdutos;

import br.com.evonetwork.cadastrarProdutos.Controller.ControllerProdutos;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;

public class CadastrarProdutos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("**EVO - INICIANDO CADASTRO DE PRODUTOS**");
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY); // em casos de deadlock, esta sess�o cai
			for (int i = 0; i < ca.getLinhas().length; i++) {
	            Registro linha = ca.getLinhas()[i];
                ControllerProdutos.cadastrarProdutos(ca, linha);
	        }
		} finally {
			JapeSession.close(hnd);
			System.out.println("**EVO - FIM CADASTRO DE PRODUTOS**");
		}
	}
}
