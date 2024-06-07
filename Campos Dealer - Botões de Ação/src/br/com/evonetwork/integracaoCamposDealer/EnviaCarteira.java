package br.com.evonetwork.integracaoCamposDealer;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.evonetwork.integracaoCamposDealer.Controller.CarteiraController;
import br.com.evonetwork.integracaoCamposDealer.Model.CarteiraCliente;
import br.com.evonetwork.integracaoCamposDealer.Model.CodSankhyaXIdCamposDealer;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class EnviaCarteira implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		Registro[] linha = ca.getLinhas();

		for (Registro registro : linha) {
			BigDecimal codCarteira = (BigDecimal) registro.getCampo("CODCARTEIRA");

			ArrayList<CodSankhyaXIdCamposDealer> vendedores = CarteiraController.buscarVendedoresCarteira(codCarteira);
			ArrayList<CodSankhyaXIdCamposDealer> clientes = CarteiraController.buscarClientesCarteira(codCarteira);

			for (CodSankhyaXIdCamposDealer v : vendedores) {
				for (CodSankhyaXIdCamposDealer c : clientes) {
					CarteiraCliente carteira = new CarteiraCliente();
					carteira.setCodigoCliente(c.getCodSankhya().toString());
					carteira.setIdCliente(c.getIdCamposDealer().intValue());
					carteira.setCodUsuario(v.getCodSankhya().toString());
					carteira.setIdUsuario(v.getIdCamposDealer().intValue());
					carteira.setCodCarteira(codCarteira);
					
					CarteiraController.enviaCarteira(carteira, ca);
				}
				CarteiraController.coletarDadosDaCarteira(v.getCodSankhya());
			}
			
		}

	}
}
