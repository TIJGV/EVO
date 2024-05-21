package br.com.evonetwork.cancelarOS.Botao;

import java.math.BigDecimal;

import br.com.evonetwork.cancelarOS.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class BotaoCancelarOS implements AcaoRotinaJava {

	// Botão na AD_TCSOSE

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - CANCELAR OS - INICIO***");
		final Registro linha = ca.getLinhas()[0];
		final String motivoCancelamento = (String) ca.getParam("MOTIVOCANCELAMENTO");
		final BigDecimal numOS = Controller.validarECancelarOS(linha, motivoCancelamento);
		if (numOS != null) {
			ca.setMensagemRetorno("A OS " + numOS + " foi cancelada.");
		}
		System.out.println("***EVO - CANCELAR OS - FIM***");
	}

}
