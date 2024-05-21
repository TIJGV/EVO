package br.com.evonetwork.somarHorasVendidas;

import java.math.BigDecimal;

import br.com.evonetwork.utils.Utils;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class SomarHorasVendidasEvento implements EventoProgramavelJava {

	public void afterDelete(PersistenceEvent event) throws Exception {}

	public void afterInsert(PersistenceEvent event) throws Exception {}

	public void afterUpdate(PersistenceEvent event) throws Exception {}

	public void beforeCommit(TransactionContext tranCtx) throws Exception {}

	public void beforeDelete(PersistenceEvent event) throws Exception {}

	public void beforeInsert(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO)event.getVo();
		calcHorasTrabalhadas(vo);
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO)event.getVo();
		calcHorasTrabalhadas(vo);
	}

	public void calcHorasTrabalhadas(DynamicVO vo) throws Exception {
		System.out.println("EVO - SOMA TOTAL DE HORAS TRABALHADAS - INICIO");
		String horasvendidastexto = vo.asString("HORASVENDIDASTEXTO");
		if (horasvendidastexto == null || "".equals(horasvendidastexto)) {
			vo.setProperty("HORASVENDIDASDEC", null);
			return;
		}
		if(horasvendidastexto.length() >= 4) {
			//5000 > 50:00
			if(!horasvendidastexto.contains(":")) {
				horasvendidastexto = horasvendidastexto.substring(0, horasvendidastexto.length()-2)+":"+horasvendidastexto.substring(horasvendidastexto.length()-2, horasvendidastexto.length());
			} else {
				horasvendidastexto = completeNaEsquerda(horasvendidastexto, '0', 5);
			}
		} else if (horasvendidastexto.length() >= 1) {
			//50 > 00:50
			if(!horasvendidastexto.contains(":")) {
				horasvendidastexto = completeNaEsquerda(horasvendidastexto, '0', 4);
				horasvendidastexto = horasvendidastexto.substring(0, horasvendidastexto.length()-2)+":"+horasvendidastexto.substring(horasvendidastexto.length()-2, horasvendidastexto.length());
			}
		}
		validarMinutos(horasvendidastexto);
		BigDecimal horasvendidasdec = Utils.calcHoras(horasvendidastexto);
		vo.setProperty("HORASVENDIDASTEXTO", horasvendidastexto);
		vo.setProperty("HORASVENDIDASDEC", horasvendidasdec);
		System.out.println("EVO - SOMA TOTAL DE HORAS TRABALHADAS - FIM");
	}

	private void validarMinutos(String horasvendidastexto) throws Exception {
		String[] partes = horasvendidastexto.split(":");
		String minutos = partes[1];
		if(Integer.valueOf(minutos) > 59)
			throw new Exception("Não é permitido inserir um valor maior que 59 nos minutos!");
	}

	private String completeNaEsquerda(String value, char c, int size) {
        String result;
        for (result = value; result.length() < size; result = c + result) {}
        return result;
    }
}
