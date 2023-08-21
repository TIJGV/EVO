package br.com.evonetwork.importarProspects.Rotina;

import br.com.evonetwork.importarProspects.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;

public class ImportarProspectsBotao implements AcaoRotinaJava {

	//Botão na AD_IMPPROSP
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - IMPORTAÇÃO DE PROSPECTS - INICIO***");
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
			for (int i = 0; i < ca.getLinhas().length; i++) {
	            Registro linha = ca.getLinhas()[i];
                Controller.iniciarImportacao(ca, linha);
	        }
		} catch(Exception e) {
			ca.setMensagemRetorno(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("***EVO - IMPORTAÇÃO DE PROSPECTS - FIM***");
	}

}
