package br.com.evonetwork.importarProdutos;

import br.com.evonetwork.importarProdutos.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class ImportarProduto implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - IMPORTAR PRODUTOS - INICIO***");
		if(ca.getLinhas().length > 1)
			throw new Exception("Selecione apenas uma linha para importar!");
		Registro linha = ca.getLinhas()[0];
		Controller.importar(ca, linha);
		System.out.println("***EVO - IMPORTAR PRODUTOS - FIM***");
	}
}
