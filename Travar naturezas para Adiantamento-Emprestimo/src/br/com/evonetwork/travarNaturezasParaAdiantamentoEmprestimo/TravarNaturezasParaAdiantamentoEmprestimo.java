package br.com.evonetwork.travarNaturezasParaAdiantamentoEmprestimo;

import java.math.BigDecimal;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class TravarNaturezasParaAdiantamentoEmprestimo implements EventoProgramavelJava {

	// Evento na TGFFIN
	
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
		travarNaturezas(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		travarNaturezas(event);
	}
	
	private void travarNaturezas(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - TRAVAR NATUREZAS PARA ADIANTAMENTOS/EMPRESTICOS - INICIO***");
		DynamicVO tgffinVO = (DynamicVO) event.getVo();
		
		BigDecimal nuCompensacao = tgffinVO.asBigDecimal("NUCOMPENS");
		
		System.out.println("NuCompens: "+nuCompensacao);
		System.out.println("TOP: "+tgffinVO.asBigDecimal("CODTIPOPER"));
		System.out.println("Natureza: "+tgffinVO.asBigDecimal("CODNAT"));
		
		if(nuCompensacao == null && !verificaTOP(tgffinVO.asBigDecimal("CODTIPOPER")) && verificaNaturezas(tgffinVO.asBigDecimal("CODNAT"))) {
			throw new Exception("TOP 1304 e Naturezas 10101005, 10101006 e 10101008 só podem ser utilizadas para Adiantamentos/Empréstimos!");
		}
		System.out.println("***EVO - TRAVAR NATUREZAS PARA ADIANTAMENTOS/EMPRESTICOS - INICIO***");
	}
	
	private boolean verificaTOP(BigDecimal codTop) {
		if((BigDecimal.valueOf(1304)).compareTo(codTop) == 0)
			return true;
		return false;
	}
	
	private boolean verificaNaturezas(BigDecimal codNat) {
		if((BigDecimal.valueOf(10101005)).compareTo(codNat) == 0)
			return true;
		else if((BigDecimal.valueOf(10101006)).compareTo(codNat) == 0)
			return true;
		else if((BigDecimal.valueOf(10101008)).compareTo(codNat) == 0)
			return true;
		return false;
	}

}
