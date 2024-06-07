package br.com.evonetwork.atualizaPedido.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.atualizaPedido.Controller.EmpresaController;
import br.com.evonetwork.atualizaPedido.Controller.ItemNotaController;
import br.com.evonetwork.atualizaPedido.Model.ProdutoNegociado;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class ItemNotaDAO {
	public static void criarItemNota(ProdutoNegociado p, BigDecimal nunota) throws Exception {
		BigDecimal usuarioLogado = new BigDecimal(MGECoreParameter.getParameterAsInt("USUAPICAMPOSD"));
		JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
		
		SessionHandle hnd = null;
		BigDecimal vlrTot = (p.getVlrunit()).multiply(p.getQtdneg());
		
		BigDecimal local = p.getCodlocal();
		BigDecimal codemp = EmpresaController.buscarCodEmp(nunota);
		
		if (local == null || local.compareTo(BigDecimal.ZERO) == 0) {
			local = EmpresaController.buscarLocalPadrao(codemp);
		}
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.ITEM_NOTA)
				.create()
				.set("NUNOTA", nunota)
				.set("CODPROD", p.getCodprod())
				.set("VLRUNIT", p.getVlrunit())
				.set("QTDNEG", p.getQtdneg())
				.set("CODVOL", p.getCodvol())
				.set("VLRTOT", vlrTot)
				.set("CONTROLE", p.getControle())
				.set("CODLOCALORIG", local)
				.set("ATUALESTOQUE", BigDecimal.ZERO)
				.set("ATUALESTTERC", "N")
				.set("RESERVA", "N").save();
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar ItemNota: "+e.getMessage());
		} finally {
			BigDecimal vlrTotNota = ItemNotaController.buscarVlrTotItens(nunota);
			JapeWrapper cabecalhoDAO = JapeFactory.dao("CabecalhoNota");
			DynamicVO updCabecalho = cabecalhoDAO.findByPK(nunota);
			cabecalhoDAO.prepareToUpdate(updCabecalho).set("VLRNOTA", vlrTotNota).update();
			JapeSession.close(hnd);
		}
	}
	
	public static void atualizarItemNota(ProdutoNegociado p, BigDecimal nunota, BigDecimal sequencia) throws Exception {
		BigDecimal usuarioLogado = new BigDecimal(MGECoreParameter.getParameterAsInt("USUAPICAMPOSD"));
		JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
		
		SessionHandle hnd = null;
		BigDecimal vlrTot = (p.getVlrunit()).multiply(p.getQtdneg());
		try {
			hnd = JapeSession.open();
			JapeWrapper serviceDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			DynamicVO service = serviceDAO.findOne("NUNOTA = " + nunota + " AND SEQUENCIA = " + sequencia);
			serviceDAO.prepareToUpdate(service)
				.set("NUNOTA", nunota)
				.set("VLRUNIT", p.getVlrunit())
				.set("QTDNEG", p.getQtdneg())
				.set("VLRTOT", vlrTot)
				.update();
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao atualizar ItemNota: "+e.getMessage());
		} finally {
			BigDecimal vlrTotNota = ItemNotaController.buscarVlrTotItens(nunota);
			JapeWrapper cabecalhoDAO = JapeFactory.dao("CabecalhoNota");
			DynamicVO updCabecalho = cabecalhoDAO.findByPK(nunota);
			cabecalhoDAO.prepareToUpdate(updCabecalho).set("VLRNOTA", vlrTotNota).update();
			JapeSession.close(hnd);
		}
	}
	
	public static void deletarItemNota(BigDecimal nunota, BigDecimal sequencia) throws Exception {
		BigDecimal usuarioLogado = new BigDecimal(MGECoreParameter.getParameterAsInt("USUAPICAMPOSD"));
		JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
		
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.ITEM_NOTA).deleteByCriteria("SEQUENCIA = " + sequencia + " AND NUNOTA = " + nunota);
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao deletar ItemNota: "+e.getMessage());
		} finally {
			BigDecimal vlrTotNota = ItemNotaController.buscarVlrTotItens(nunota);
			JapeWrapper cabecalhoDAO = JapeFactory.dao("CabecalhoNota");
			DynamicVO updCabecalho = cabecalhoDAO.findByPK(nunota);
			cabecalhoDAO.prepareToUpdate(updCabecalho).set("VLRNOTA", vlrTotNota).update();
			JapeSession.close(hnd);
		}
	}
}
