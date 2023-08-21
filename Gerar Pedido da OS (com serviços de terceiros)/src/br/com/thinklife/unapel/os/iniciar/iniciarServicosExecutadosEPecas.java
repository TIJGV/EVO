package br.com.thinklife.unapel.os.iniciar;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.thinklife.unapel.os.processamento.processamentoServicosExecutadosOS;
import br.com.thinklife.unapel.os.processamento.processarPecasUtilizadasOS;

public class iniciarServicosExecutadosEPecas implements AcaoRotinaJava
{
    public void doAction(final ContextoAcao arg0) throws Exception {
    	System.out.println("***EVO - GERANDO PEDIDO PARA OS - INICIO***");
        processamentoServicosExecutadosOS.processar(arg0);
        processarPecasUtilizadasOS.processar(arg0);
        System.out.println("***EVO - GERANDO PEDIDO PARA OS - FIM***");
    }
}
