package br.com.evonetwork.cancelarOS.Botao;

import java.math.BigDecimal;

import br.com.evonetwork.cancelarOS.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class BotaoCancelarOS implements AcaoRotinaJava{
	
	// Botão na AD_TCSOSE
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - CANCELAR OS - INICIO***");
		Registro[] linhas = ca.getLinhas();
		BigDecimal numOS = null;
		for (Registro linha : linhas) {
			numOS = Controller.validarECancelarOS(linha);
		}
		if(numOS != null)
			ca.setMensagemRetorno("A OS "+numOS+" foi cancelada.");
		System.out.println("***EVO - CANCELAR OS - FIM***");
	}
	
}
