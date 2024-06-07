package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.ProdutoController;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ProdutoDAO {

	public static void salvaIdCamposDealer(BigDecimal codProd,  BigDecimal codemp, BigDecimal idCamposDealer) throws Exception {
		JapeSession.SessionHandle hnd = null;
		
		BigDecimal id = ProdutoController.buscarIdCamposDealer(codProd, codemp);
		if (!id.equals(BigDecimal.ZERO)) {
			return;
		}
		try {
			JapeWrapper grupoProdutoDAO = JapeFactory.dao("AD_IDCDPRO");	
			grupoProdutoDAO.create()
					.set("IDCAMPOSDEALER", idCamposDealer.toString())
					.set("CODEMP", codemp)
					.set("CODPROD", codProd)
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao salvar id CampOS Dealer: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
