package br.com.evonetwork.gerarPedido;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.evonetwork.gerarPedido.DAO.LogDAO;
import br.com.evonetwork.gerarPedido.controller.Controller;
import br.com.evonetwork.gerarPedido.model.ProdutoNegociado;
import br.com.evonetwork.gerarPedido.model.TCSOSE;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;

public class GerarPedido implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		evento(event);
	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {

	}

	@Override
	public void beforeCommit(TransactionContext tranCtx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void evento(PersistenceEvent event) throws Exception {
		try {

			BigDecimal usuarioLogado = new BigDecimal(MGECoreParameter.getParameterAsInt("USUAPICAMPOSD"));
			JapeSessionContext.putProperty("usuario_logado", usuarioLogado);

			DynamicVO registro = (DynamicVO) event.getVo();
			BigDecimal codserv = registro.asBigDecimal("CODSERV");
			BigDecimal numos = registro.asBigDecimal("NUMOS");
			TCSOSE tcsose = Controller.buscarInfoOS(numos);

			BigDecimal codemp = tcsose.getAd_codemp();
			BigDecimal codpap = tcsose.getCodpap();

			if (codserv.compareTo(new BigDecimal(119528)) == 0) {

				BigDecimal codparc = Controller.buscarGerarParceiro(codpap);

				ArrayList<ProdutoNegociado> produtoNegociado = Controller.buscarProdutoNegociado(codpap, numos);

				if (produtoNegociado.size() == 0) {
					throw new Exception(
							"Pelo menos um produto deve ser informado na aba \"Produto Negociado\" na tela \"Prospects\"");
				}

				JapeWrapper cabecalhoDAO = JapeFactory.dao("CabecalhoNota");
				DynamicVO cabecalho = cabecalhoDAO.create().set("NUMNOTA", BigDecimal.ZERO).set("CODEMP", codemp)
						.set("CODPARC", codparc).set("CODTIPVENDA", produtoNegociado.get(0).getCodtipvenda())
						.set("CODTIPOPER", produtoNegociado.get(0).getCodtipoper())
						.set("OBSERVACAO", "Pedido gerado automaticamente pela negociação " + numos).set("NUMOS", numos)
						.set("CODCENCUS", tcsose.getCodcencus()).set("AD_CODOAT", tcsose.getCodoat())
						.set("CODNAT", tcsose.getCodnat()).set("CODVEND", tcsose.getCodvend()).save();

				BigDecimal nunota = cabecalho.asBigDecimal("NUNOTA");

				JapeWrapper servicoExecutadoDAO = JapeFactory.dao("OrdemServico");
				DynamicVO servico = servicoExecutadoDAO.findByPK(numos);
				servicoExecutadoDAO.prepareToUpdate(servico).set("CODPARC", codparc).set("NUNOTA", nunota).update();

				BigDecimal vlrTotNota = BigDecimal.ZERO;

				for (ProdutoNegociado p : produtoNegociado) {
					JapeWrapper itemDAO = JapeFactory.dao("ItemNota");
					BigDecimal vlrTot = (p.getVlrunit()).multiply(p.getQtdneg());
					vlrTotNota = vlrTotNota.add(vlrTot);
					BigDecimal local = p.getCodlocal();

					if (local == null || local.compareTo(BigDecimal.ZERO) == 0) {
						local = Controller.buscarLocalPadrao(codemp);
					}

					itemDAO.create().set("NUNOTA", nunota).set("CODPROD", p.getCodprod()).set("VLRUNIT", p.getVlrunit())
							.set("QTDNEG", p.getQtdneg()).set("CODVOL", p.getCodvol()).set("VLRTOT", vlrTot)
							.set("CONTROLE", p.getControle()).set("CODLOCALORIG", local)
							.set("ATUALESTOQUE", BigDecimal.ZERO).set("ATUALESTTERC", "N").set("RESERVA", "N").save();
				}

				DynamicVO updCabecalho = cabecalhoDAO.findByPK(nunota);
				cabecalhoDAO.prepareToUpdate(updCabecalho).set("VLRNOTA", vlrTotNota).update();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogDAO.criarLog("G", e.getMessage());
		}

	}

}
