package br.com.evonetwork.calculaPrecoServico.Evento;

import java.math.BigDecimal;

import br.com.evonetwork.calculaPrecoServico.Controller.Controller;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoItemNota implements EventoProgramavelJava {

	// Evento na TGFITE
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		realizarVerificacoes(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		realizarVerificacoes(event);
	}
	
	private void realizarVerificacoes(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - CALCULAR PREÇO DE SERVIÇO PARA ORÇAMENTO/GARANTIA - INICIO***");
		try {
			DynamicVO itemNotaVO = (DynamicVO) event.getVo();
			BigDecimal nuNota = itemNotaVO.asBigDecimal("NUNOTA");
			BigDecimal codTOP = Controller.getCodTop(nuNota);
			if(Controller.topCalculaPreco(codTOP)) {
				BigDecimal codServ = itemNotaVO.asBigDecimal("CODPROD");
				if(Controller.verificaSeEServico(codServ)) {
					BigDecimal quantidade = itemNotaVO.asBigDecimal("QTDNEG");
					if(Controller.validarQuantidade(quantidade)) {
						BigDecimal codUsuMecanico = Controller.getCodUsuMecanico(nuNota);
						Controller.calculaPrecoDoServicoEPreencheNaNota(codServ, quantidade, nuNota, codUsuMecanico, itemNotaVO);
					}
				}
			}
		} catch(Exception e) {
			System.out.println("Erro: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - CALCULAR PREÇO DE SERVIÇO PARA ORÇAMENTO/GARANTIA - FIM***");
	}

}
