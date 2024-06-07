package br.com.evonetwork.atualizaPedido;

import java.math.BigDecimal;

import br.com.evonetwork.atualizaPedido.Controller.CabecalhoNotaController;
import br.com.evonetwork.atualizaPedido.Controller.ItemNotaController;
import br.com.evonetwork.atualizaPedido.Controller.ProdutoController;
import br.com.evonetwork.atualizaPedido.DAO.ItemNotaDAO;
import br.com.evonetwork.atualizaPedido.Model.InfoProduto;
import br.com.evonetwork.atualizaPedido.Model.ProdutoNegociado;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class AtualizaSankhya implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - DELETAR ITEMNOTA EXCLUIDO CAMPOS DEALER - INICIO***");
		DynamicVO registro = (DynamicVO) event.getVo();
		BigDecimal numos = registro.asBigDecimal("NUMOS");
		BigDecimal codprod = registro.asBigDecimal("CODPROD");

		BigDecimal nunota = CabecalhoNotaController.buscarNunota(numos);
		
		if (!nunota.equals(BigDecimal.ZERO)) {
			BigDecimal sequencia = ItemNotaController.buscarSequencia(nunota, codprod);

			ItemNotaDAO.deletarItemNota(nunota, sequencia);
		}
		System.out.println("***EVO - DELETAR ITEMNOTA EXCLUIDO CAMPOS DEALER - FIM***");
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - INSERIR NOVO ITEMNOTA ADICIONADO CAMPOS DEALER - INICIO***");
		DynamicVO registro = (DynamicVO) event.getVo();

		BigDecimal numos = registro.asBigDecimal("NUMOS");

		InfoProduto info = ProdutoController.buscaInfoProduto(registro.asBigDecimal("CODPROD"));
		
		ProdutoNegociado p = new ProdutoNegociado();
		p.setCodprod(registro.asBigDecimal("CODPROD")); 
		p.setQtdneg(registro.asBigDecimal("QTDNEG")); 
		p.setVlrunit(registro.asBigDecimal("VLRUNIT"));
		p.setCodtipvenda(info.getCodtipvenda()); 
		p.setCodtipoper(info.getCodtipoper()); 
		p.setCodvol(info.getCodvol());
		p.setCodlocal(registro.asBigDecimal("CODLOCAL")); 
		p.setControle(registro.asString("CONTROLE"));

		BigDecimal nunota = CabecalhoNotaController.buscarNunota(numos);

		if (!nunota.equals(BigDecimal.ZERO)) {
			ItemNotaDAO.criarItemNota(p, nunota);
		}
		System.out.println("***EVO - INSERIR NOVO ITEMNOTA ADICIONADO CAMPOS DEALER - FIM***");
	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - ATUALIZAR ITEMNOTA ALTERADO CAMPOS DEALER - INICIO***");
		DynamicVO registro = (DynamicVO) event.getVo();

		BigDecimal numos = registro.asBigDecimal("NUMOS");
		BigDecimal codprod = registro.asBigDecimal("CODPROD");

		ProdutoNegociado p = new ProdutoNegociado();
		p.setQtdneg(registro.asBigDecimal("QTDNEG"));
		p.setVlrunit(registro.asBigDecimal("VLRUNIT"));

		BigDecimal nunota = CabecalhoNotaController.buscarNunota(numos);

		if (!nunota.equals(BigDecimal.ZERO)) {
			BigDecimal sequencia = ItemNotaController.buscarSequencia(nunota, codprod);

			ItemNotaDAO.atualizarItemNota(p, nunota, sequencia);
		}
		System.out.println("***EVO - ATUALIZAR ITEMNOTA ALTERADO CAMPOS DEALER - INICIO***");
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

}
