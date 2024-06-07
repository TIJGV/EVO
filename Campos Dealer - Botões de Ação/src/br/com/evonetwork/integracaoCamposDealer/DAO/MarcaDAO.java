package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.MarcaController;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class MarcaDAO {

	public static void salvaIdCamposDealer(BigDecimal codMarca,  BigDecimal codemp, BigDecimal idCamposDealer) throws Exception {
		JapeSession.SessionHandle hnd = null;
		BigDecimal id = MarcaController.buscarIdCamposDealerMarca(codMarca, codemp);
		if (!id.equals(BigDecimal.ZERO)) {
			return;
		}
		try {
			JapeWrapper grupoProdutoDAO = JapeFactory.dao("AD_IDCDMAR");	
			grupoProdutoDAO.create()
					.set("IDCAMPOSDEALER", idCamposDealer.toString())
					.set("CODEMP", codemp)
					.set("CODIGO", codMarca)
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao salvar id CampOS Dealer: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
