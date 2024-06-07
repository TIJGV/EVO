package br.com.evonetwork.integracaoCamposDealer;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.ProspectController;
import br.com.evonetwork.integracaoCamposDealer.Model.DadosCliente;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class EnviaCamposDealer implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		Registro[] linha = ca.getLinhas();

		for (Registro registro : linha) {
			DadosCliente cliente = new DadosCliente();
			
			cliente.setCodigoCliente(((BigDecimal) registro.getCampo("CODPAP")).toString());
			cliente.setCNPJ_CPF((String) registro.getCampo("CGC_CPF"));
			cliente.setRG((String) registro.getCampo("RG"));
			cliente.setNome((String) registro.getCampo("NOMEPAP"));
			cliente.setEndereco((String) registro.getCampo("ENDERECO"));
			cliente.setCEP((String) registro.getCampo("CEP"));
			cliente.setCidade((String) registro.getCampo("NOMECID"));
			
			String uf = ProspectController.getUf((BigDecimal) registro.getCampo("CODUF"));
			
			cliente.setEstado(uf);
			
			cliente.setTelefone((String) registro.getCampo("TELEFONE"));
			cliente.setMailCliente((String) registro.getCampo("EMAIL"));
			
			ProspectController.enviaProspect(cliente, ca);

		}
		
	}

}
