package br.com.thinklife.unapel.os.iniciar;

import br.com.thinklife.unapel.os.processamento.requisicaoPecas;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

public class iniciarRequisicaoPeca implements AcaoRotinaJava {
	
	//Botão na AD_TCSPRO
	
    public void doAction(final ContextoAcao arg0) throws Exception {
    	System.out.println("***EVO - REQUISITAR PEÇA - INICIO***");
        requisicaoPecas.requisitar(arg0);
        System.out.println("***EVO - REQUISITAR PEÇA - FIM***");
    }
    
}
