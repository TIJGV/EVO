package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Controller.NegocioController;
import br.com.evonetwork.integracaoCamposDealer.Controller.ProdutoController;
import br.com.evonetwork.integracaoCamposDealer.Model.NegocioXProduto;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ProdutoNegociadoDAO {
	public static void salvarProdutoNegociado(NegocioXProduto p, BigDecimal numos, BigDecimal codpap) throws Exception {
		if(p.getfAtivo() == 0 || NegocioController.produtoNegociadoJaIntegrado(p.getIdNegocioXProduto())) {
			return;
		}
	
		BigDecimal codprod = ProdutoController.buscarCODPRODPeloIdCamposDealer(new BigDecimal(p.getIdProduto()));
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper negocioProdutoDAO = JapeFactory.dao("AD_TGFPRONEG");
			negocioProdutoDAO.create()
				.set("CODPAP", codpap)
				.set("NUMOS", numos)
				.set("CODPROD", codprod)
				.set("QTDNEG", new BigDecimal(p.getQtd()))
				.set("VLRUNIT", new BigDecimal(p.getVlrUnitario()))
				.set("VLRTOTAL", new BigDecimal(p.getVlr()))
				.set("IDCAMPOSDEALER", p.getIdNegocioXProduto())
				.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Produto Negociado: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	public static void atualizarProdutoNegociado(NegocioXProduto p, BigDecimal numos, BigDecimal codpap) throws Exception {
		if(p.getfAtivo() == 0) {
			deletarProdutoNegociado(p, numos, codpap);
			return;
		}
		BigDecimal codprod = ProdutoController.buscarCODPRODPeloIdCamposDealer(new BigDecimal(p.getIdProduto()));
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper negocioProdutoDAO = JapeFactory.dao("AD_TGFPRONEG");
			DynamicVO servico = negocioProdutoDAO.findOne("IDCAMPOSDEALER = '"+p.getIdNegocioXProduto()+"'" + " AND NUMOS = " + numos + " AND CODPAP = " + codpap);
			negocioProdutoDAO.prepareToUpdate(servico)
					.set("NUMOS", numos)
					.set("CODPROD", codprod)
					.set("QTDNEG", new BigDecimal(p.getQtd()))
					.set("VLRUNIT", new BigDecimal(p.getVlrUnitario()))
					.set("VLRTOTAL", new BigDecimal(p.getVlr()))
					.set("IDCAMPOSDEALER", p.getIdNegocioXProduto())
					.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao atualizar Produto Negociado: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	public static void deletarProdutoNegociado(NegocioXProduto p, BigDecimal numos, BigDecimal codpap) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper negocioProdutoDAO = JapeFactory.dao("AD_TGFPRONEG");
			negocioProdutoDAO.deleteByCriteria("IDCAMPOSDEALER = '"+p.getIdNegocioXProduto()+"'" + " AND NUMOS = " + numos + " AND CODPAP = " + codpap);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao deletar Produto Negociado: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}

