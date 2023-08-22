package br.com.evonetwork.botaodeacao;

import java.math.BigDecimal;

import br.com.evonetwork.tgfcab.PreencherCab;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class BotaoDeAcao implements AcaoRotinaJava{
	
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] linhas = contexto.getLinhas();
		PreencherCab cab = new PreencherCab();
		
		for (Registro linha : linhas) {
			
			BigDecimal numContrato = (BigDecimal) linha.getCampo("NUMCONTRATO");
			BigDecimal codTipContrato = (BigDecimal) linha.getCampo("AD_CODTIPCON");
			cab.criarRegistroTgfcab(numContrato,codTipContrato);
		}
	}
}
