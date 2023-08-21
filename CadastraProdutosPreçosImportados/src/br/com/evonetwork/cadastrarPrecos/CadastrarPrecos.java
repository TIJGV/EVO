package br.com.evonetwork.cadastrarPrecos;

import br.com.evonetwork.cadastrarPrecos.Controller.ControllerPrecos;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;

public class CadastrarPrecos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("**EVO - INICIANDO CADASTRO DE PREÇOS**");
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY); // em casos de deadlock, esta sess�o cai
			for (int i = 0; i < ca.getLinhas().length; i++) {
	            Registro linha = ca.getLinhas()[i];
                ControllerPrecos.cadastrarPrecos(ca, linha);
	        }
		} finally {
			JapeSession.close(hnd);
			System.out.println("**EVO - FIM CADASTRO DE PREÇOS**");
		}
	}
}
