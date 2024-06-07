package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.CarteiraController;
import br.com.evonetwork.integracaoCamposDealer.Model.CarteiraCliente;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class RelacaoCarteirasDAO {
	public static void criarRelacaoCarteira(CarteiraCliente carteira) throws Exception {
		JapeSession.SessionHandle hnd = null;

		if (carteira.getCodigoCliente().equals("null") || carteira.getCodUsuario().equals("null")) {
			return;
		}

		BigDecimal codvend = new BigDecimal(carteira.getCodUsuario());
		BigDecimal codpap = new BigDecimal(carteira.getCodigoCliente());
		BigDecimal codparc = CarteiraController.buscarCodParc(codpap);
		BigDecimal idcarteira = new BigDecimal(carteira.getIdCarteiraCliente());
		BigDecimal codcarteira = CarteiraController.buscarCodigoCarteira(codvend, codparc);

		if (!CarteiraController.vendedorExiste(codvend) || !CarteiraController.parceiroExiste(codparc)
				|| codparc.equals(BigDecimal.ZERO)) {
			return;

		}

		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper relacaoCarteiraDAO = JapeFactory.dao("AD_RELACAOCARTEIRA");
			relacaoCarteiraDAO.create().set("CODCARTEIRA", codcarteira).set("CODPARC", codparc).set("CODPAP", codpap)
					.set("CODVEND", codvend).set("IDCARTEIRA", idcarteira).save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Relação entre Carteiras: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void atualizarRelacaoCarteira(CarteiraCliente carteira) throws Exception{
		BigDecimal codvend = new BigDecimal(carteira.getCodUsuario());
		BigDecimal codpap = new BigDecimal(carteira.getCodigoCliente());
		BigDecimal codparc = CarteiraController.buscarCodParc(codpap);
		BigDecimal idcarteira = new BigDecimal(carteira.getIdCarteiraCliente());
		BigDecimal codcarteira = CarteiraController.buscarCodigoCarteira(codvend, codparc);
		
SessionHandle hnd = null;
		
		try {
			hnd = JapeSession.open();
			JapeWrapper relacaoCarteiraDAO = JapeFactory.dao("AD_RELACAOCARTEIRA");
			DynamicVO servico = relacaoCarteiraDAO.findOne("IDCARTEIRA = " + idcarteira + " AND CODVEND = " + codvend + " AND CODPARC = " + codparc);
			relacaoCarteiraDAO.prepareToUpdate(servico)
			.set("CODCARTEIRA", codcarteira)
			.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao atualizar Relação entre Carteiras: "+e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
