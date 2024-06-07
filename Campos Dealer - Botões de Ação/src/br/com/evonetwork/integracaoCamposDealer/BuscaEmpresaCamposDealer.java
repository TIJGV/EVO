package br.com.evonetwork.integracaoCamposDealer;

import br.com.evonetwork.integracaoCamposDealer.Controller.EmpresaController;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class BuscaEmpresaCamposDealer implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		try {
			Registro[] linha = ca.getLinhas();

			for (Registro registro : linha) {
				EmpresaController.iniciarBusca(ca, registro);
			}
		} catch (Exception e) {
			ca.setMensagemRetorno("Erro na execução: " + e.getMessage());
			System.out.println("Erro na execução: " + e.getMessage());
			e.printStackTrace();
		}

	}

}
