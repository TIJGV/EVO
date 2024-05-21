package br.com.evonetwork.somahorastrabalhadas;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.evonetwork.apontamentos.GetDadosApontamentos;
import br.com.evonetwork.utils.Utils;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class SomaTotalHorasTrabalhadas implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

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
		DynamicVO vo = (DynamicVO) event.getVo();
		calcHorasTrabalhadas(vo);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO) event.getVo();
		calcHorasTrabalhadas(vo);

	}

	public void calcHorasTrabalhadas(DynamicVO vo) {
		System.out.println("EVO - INICIO - SOMA TOTAL DE HORAS TRABALHADAS teste 1");

		BigDecimal numos = vo.asBigDecimal("NUMOS");
		String horasvendidastexto = vo.asString("HORASVENDIDASTEXTO");
		
		if(horasvendidastexto.equals("")) {
			horasvendidastexto = null;
		}
		
		if (horasvendidastexto != null) {
			BigDecimal horasvendidasdec = Utils.calcHoras(horasvendidastexto);
			vo.setProperty("HORASVENDIDASDEC", horasvendidasdec);
		} else {
			vo.setProperty("HORASVENDIDASDEC", null);
		}
		
		String totalHorasApontadas = GetDadosApontamentos.totalHorasApontadas(numos);

		vo.setProperty("TOTHORASAPONTDEC", new BigDecimal(totalHorasApontadas).setScale(2, RoundingMode.HALF_UP));
		
		System.out.println();
		System.out.println("EVO - FIM - SOMA TOTAL DE HORAS TRABALHADAS");
	}
}
