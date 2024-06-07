package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.ModeloController;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ModeloDAO {

	public static void salvaIdCamposDealer(BigDecimal nroUnico,  BigDecimal codemp, BigDecimal idCamposDealer) throws Exception {
		JapeSession.SessionHandle hnd = null;
		BigDecimal id = ModeloController.buscarIdCamposDealerModelo(nroUnico, codemp);
		if (!id.equals(BigDecimal.ZERO)) {
			return;
		}
		try {
			JapeWrapper grupoProdutoDAO = JapeFactory.dao("AD_IDCDMOD");	
			grupoProdutoDAO.create()
					.set("IDCAMPOSDEALER", idCamposDealer.toString())
					.set("CODEMP", codemp)
					.set("NROUNICO", nroUnico)
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao salvar id CampOS Dealer: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
