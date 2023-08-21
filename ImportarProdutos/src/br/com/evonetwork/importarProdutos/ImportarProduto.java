package br.com.evonetwork.importarProdutos;

import br.com.evonetwork.importarProdutos.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;

public class ImportarProduto implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY); // em casos de deadlock, esta sessão cai
			for (int i = 0; i < ca.getLinhas().length; i++) {
	            Registro linha = ca.getLinhas()[i];
                System.out.println("**EVO - INICIANDO IMPORTAÇÃO PRODUTOS**");
                Controller.importar(ca, linha);
	        }
		} finally {
			System.out.println("**EVO - FIM IMPORTAÇÃO PRODUTOS**");
			JapeSession.close(hnd);
		}
	}
}
