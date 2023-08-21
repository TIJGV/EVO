package br.com.evonetwork.importarGruposDeProdutos;

import br.com.evonetwork.importarGruposDeProdutos.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;

public class ImportarPlanilha implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY); // em casos de deadlock, esta sessão cai
			for (int i = 0; i < ca.getLinhas().length; i++) {
	            Registro linha = ca.getLinhas()[i];
                System.out.println("***INICIO - IMPORTAÇÃO GRUPOS DE PRODUTOS - EVO***");
                Controller.importar(ca, linha);
	        }
		} finally {
			System.out.println("***FIM - IMPORTAÇÃO GRUPOS DE PRODUTOS - EVO***");
			JapeSession.close(hnd);
		}
	}
}
