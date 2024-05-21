package br.com.evonetwork.atualizarCustoDeProdutos.Rotina;

import br.com.evonetwork.atualizarCustoDeProdutos.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

public class AtualizarCustoBotao implements AcaoRotinaJava {
    public void doAction(final ContextoAcao ca) throws Exception {
        System.out.println("***EVO - ATUALIZAR CUSTO DE PRODUTOS (LEGADO) - INÍCIO***");
        Controller.iniciarAtualizacao(ca);
        System.out.println("***EVO - ATUALIZAR CUSTO DE PRODUTOS (LEGADO) - FIM***");
    }
}
