package br.com.evonetwork.importarNota.Rotina;

import br.com.evonetwork.importarNota.Controller.ImportarItensController;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class ImportarBotao implements AcaoRotinaJava {

	//Botão na AD_IMPNOTA
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - IMPORTAÇÃO DE NOTA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		if(linhas.length == 0) {
			throw new Exception("Selecione uma importação!");
		}
		for (Registro linha : linhas) {
			try {
				System.out.println("Iniciando a importação de nro único: "+linha.getCampo("NUNICO"));
				ImportarItensController.iniciarImportacao(linha, ca);
			} catch (Exception e) {
				ImportarItensController.removerImportacao(linha);
				System.out.println("Erro na execução da importação "+linha.getCampo("NUNICO")+": "+e.getMessage());
				throw new Exception("Erro na execução da importação "+linha.getCampo("NUNICO")+": "+e.getMessage());
			}
		}
		ca.setMensagemRetorno("Importação finalizada!");
		System.out.println("***EVO - IMPORTAÇÃO DE NOTA - FIM***");
	}

}
