package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.CarteiraController;
import br.com.evonetwork.integracaoCamposDealer.Model.CarteiraCliente;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class CarteiraDAO {
	public static void criarCarteira(CarteiraCliente carteira) throws Exception {
		JapeSession.SessionHandle hnd = null;

		if (carteira.getCodigoCliente().equals("null") || carteira.getCodUsuario().equals("null")) {
			return;
		}

		BigDecimal codvend = new BigDecimal(carteira.getCodUsuario());
		BigDecimal codpap = new BigDecimal(carteira.getCodigoCliente());
		BigDecimal codparc = CarteiraController.buscarCodParc(codpap);

		if (!CarteiraController.vendedorExiste(codvend)) {
			return;
		}

		BigDecimal codcarteira = CarteiraController.buscarCodigoCarteira(codvend);

		if (!CarteiraController.parceiroExiste(codparc)
				|| CarteiraController.parceiroExisteNaCarteira(codparc, codcarteira) || codparc.equals(BigDecimal.ZERO)
				|| codcarteira.equals(BigDecimal.ZERO)) {
			return;
		}

		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper relacaoCarteiraDAO = JapeFactory.dao("AD_CARTEIRACLIENTE");
			relacaoCarteiraDAO.create().set("CODCARTEIRA", codcarteira).set("CODPARC", codparc).save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Relação entre Carteiras: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
