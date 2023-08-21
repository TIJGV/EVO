package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerReceberOS;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class BotaoReceberOsOffline implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela AD_TCSOSE
		System.out.println("***EVO - RECEBENDO O.S. DO SIMOVA - INICIO***");
		BigDecimal numOs = null;
		BigDecimal codEmp = null;
		try {
			numOs = BigDecimal.valueOf((int) ca.getParam("NUMOS"));
			codEmp = new BigDecimal((String) ca.getParam("CODEMP"));
			ControllerReceberOS.receberOS(numOs, codEmp, ca);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Erro: "+e.getMessage());
		}
		ca.setMensagemRetorno("Integração da OS "+numOs+" para a Empresa "+codEmp+" concluída!");
		System.out.println("***EVO - RECEBENDO O.S. DO SIMOVA - FIM***");
	}

}
