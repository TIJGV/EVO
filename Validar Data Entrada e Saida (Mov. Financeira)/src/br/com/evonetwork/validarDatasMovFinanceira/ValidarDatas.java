package br.com.evonetwork.validarDatasMovFinanceira;

import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class ValidarDatas implements EventoProgramavelJava {

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
		validarDatas(event);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		validarDatas(event);
	}

	private void validarDatas(PersistenceEvent event) throws Exception {
		System.out.println("***EVO - VALIDAR DATAS (TGFFIN) - INICIO***");
		DynamicVO tgffinVO = (DynamicVO) event.getVo();
		
		Timestamp dtEntradaSaida = tgffinVO.asTimestamp("DTENTSAI");
		Timestamp dtNeg = tgffinVO.asTimestamp("DTNEG");
		Timestamp dtVenc = tgffinVO.asTimestamp("DTVENC");
		Timestamp dtBaixa = tgffinVO.asTimestamp("DHBAIXA");
		
//		System.out.println("Dt. Negociação: "+dtNeg);
//		System.out.println("Dt. Entrada/Saída: "+dtEntradaSaida);
//		System.out.println("Dt. Vencimento: "+dtVenc);
//		System.out.println("Dt. Baixa: "+dtBaixa);
		
		if(dtEntradaSaida == null)
			throw new Exception("Preenchimento da Dt. Entrada e Saída obrigatório!");
		
		if(dtEntradaSaida.compareTo(dtNeg) < 0)
			throw new Exception("Dt. Entrada e Saída precisa ser maior que a Dt. Negociação");
		
		if(dtVenc != null) {
			if(dtVenc.compareTo(dtNeg) < 0)
				throw new Exception("Dt. Vencimento precisa ser maior que a Dt. Negociação");
			
			if(dtVenc.compareTo(dtEntradaSaida) < 0)
				throw new Exception("Dt. Vencimento precisa ser maior que a Dt. Entrada e Saída");
		}
		
		if(dtBaixa != null) {
			if(dtBaixa.compareTo(dtNeg) < 0)
				throw new Exception("Dt. Baixa precisa ser maior que a Dt. Negociação");
			
			if(dtBaixa.compareTo(dtEntradaSaida) < 0)
				throw new Exception("Dt. Baixa precisa ser maior que a Dt. Entrada e Saída");
		}
		
		System.out.println("***EVO - VALIDAR DATAS (TGFFIN) - FIM***");
	}

}
