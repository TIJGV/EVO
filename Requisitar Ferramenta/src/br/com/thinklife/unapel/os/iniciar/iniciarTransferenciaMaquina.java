package br.com.thinklife.unapel.os.iniciar;

import br.com.thinklife.unapel.os.processamento.processarTransfMqpUtilizada;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

public class iniciarTransferenciaMaquina implements AcaoRotinaJava
{
    public void doAction(final ContextoAcao arg0) throws Exception {
        processarTransfMqpUtilizada.gerarTransferencia(arg0);
    }
}
