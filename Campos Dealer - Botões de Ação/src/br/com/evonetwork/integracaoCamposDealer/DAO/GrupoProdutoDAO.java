package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.GrupoProdutoController;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class GrupoProdutoDAO {

	public static void salvaIdCamposDealer(BigDecimal codGrupoProd, BigDecimal codemp, BigDecimal idCamposDealer) throws Exception {
		JapeSession.SessionHandle hnd = null;
		BigDecimal id = GrupoProdutoController.buscarIdSalvo(codGrupoProd, codemp);
		if(!id.equals(BigDecimal.ZERO)) {
			return;
		}
		try {
			JapeWrapper grupoProdutoDAO = JapeFactory.dao("AD_IDCDGRU");	
			grupoProdutoDAO.create()
					.set("IDCAMPOSDEALER", idCamposDealer.toString())
					.set("CODEMP", codemp)
					.set("CODGRUPOPROD", codGrupoProd)
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao salvar id CampOS Dealer: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
