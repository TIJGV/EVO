package br.com.evonetwork.alterarStatusDaOS.Evento;

import java.math.BigDecimal;

import br.com.evonetwork.alterarStatusDaOS.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoCabecalhoNota implements EventoProgramavelJava {

	// Evento na TGFCAB
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		iniciarEvento(event);
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {}

	private void iniciarEvento(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - ALTERAR STATUS DA OS (FATURAMENTO) - INICIO***");
		try {
			DynamicVO cabecalhoVO = (DynamicVO) event.getVo();
			BigDecimal nuNota = cabecalhoVO.asBigDecimal("NUNOTA");
			if(Controller.verificarSeEhNotaDeVendaOuRequisicao(cabecalhoVO)) {
				if(Controller.notaEstaConfirmandoOuEstaConfirmada(cabecalhoVO)) {
					BigDecimal nuPedido = Controller.encontrarNuPedidoPelaNota(nuNota);
					if(nuPedido == null)
						throw new Exception("Não foi encontrado nenhum pedido para a nota "+nuNota+".");
					
					BigDecimal numOS = Controller.encontrarOSDoPedido(nuPedido);
					if(numOS == null)
						throw new Exception("Não foi encontrada nenhuma OS para o pedido "+nuPedido+".");
					
					if(Controller.todosPedidosDaOSEstaoFaturados(numOS, nuPedido))
						Controller.atualizarStatusDaOS(numOS, "FA");
					else
						Controller.atualizarStatusDaOS(numOS, "PFA");
				} else {
					System.out.println("Nota "+nuNota+" não está confirmando nem está confirmada.");
				}
			} else {
				System.out.println("Nota "+nuNota+" não é nota de venda ou requisição.");
			}
		} catch(Exception e) {
			System.out.println("Erro: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - ALTERAR STATUS DA OS (FATURAMENTO) - FIM***");
	}
}
