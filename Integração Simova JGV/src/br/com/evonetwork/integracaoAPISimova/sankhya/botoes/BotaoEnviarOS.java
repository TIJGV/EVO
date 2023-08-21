package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerEnviarOS;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.MGEModelException;

public class BotaoEnviarOS implements AcaoRotinaJava {
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela AD_TCSOSE
		System.out.println("***EVO - ENVIANDO O.S. PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				ControllerEnviarOS.coletarDadosEEnviarParaSimova(linha, ca);
			}catch(MGEModelException e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO O.S. PARA SIMOVA - FIM***");
	}
}
