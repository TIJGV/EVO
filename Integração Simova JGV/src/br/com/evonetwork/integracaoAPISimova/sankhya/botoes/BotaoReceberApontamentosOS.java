package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerBuscarOs;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.MGEModelException;

public class BotaoReceberApontamentosOS implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela AD_TCSOSE
		System.out.println("***EVO - RECEBENDO APONTAMENTOS DO SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				BigDecimal numOs = (BigDecimal) linha.getCampo("NUMOS");
				String filial = Controller.getFilial((BigDecimal) linha.getCampo("CODEMP"));
//				String urlIntegrarOS = Controller.getUrlIntegrarOS();
//				Controller.integrarOS(urlIntegrarOS, numOs, filial);
				String urlAcesso = Controller.getUrlAcesso();
				ControllerBuscarOs.receberDadosApontamento(urlAcesso, "VIEW_INTEGRACAO_ZZS", numOs.toString(), filial);
				ControllerBuscarOs.receberDadosApontamento(urlAcesso, "VIEW_INTEGRACAO_ZZT", numOs.toString(), filial);
//				ControllerBuscarOs.receberDadosApontamento(urlAcesso, "VIEW_INTEGRACAO_ZZU", numOs.toString(), filial);
			}catch(MGEModelException e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Apontamentos recebidos!");
		System.out.println("***EVO - RECEBENDO APONTAMENTOS DO SIMOVA - FIM***");
	}

}
