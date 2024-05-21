package br.com.evonetwork.somarHorasApontadas;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.evonetwork.apontamentos.GetDadosApontamentos;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class SomarHorasApontadasEvento implements EventoProgramavelJava {

	public void afterDelete(PersistenceEvent event) throws Exception {}

	public void afterInsert(PersistenceEvent event) throws Exception {}

	public void afterUpdate(PersistenceEvent event) throws Exception {}

	public void beforeCommit(TransactionContext tranCtx) throws Exception {}

	public void beforeDelete(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO)event.getVo();
		if("AD_APONTAMENTOS".equals(event.getEntity().getName()))
			calcHorasTrabalhadas(event, vo, true);
	}

	public void beforeInsert(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO)event.getVo();
		calcHorasTrabalhadas(event, vo, false);
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO)event.getVo();
		ModifingFields md = event.getModifingFields();
		boolean apontamentoManualAlterado = md.isModifingAny("HRINICIAL,HRFINAL,INTERVALO");
		if("TCSITE".equals(event.getEntity().getName()) && apontamentoManualAlterado)
			calcHorasTrabalhadas(event, vo, false);
		else if("AD_APONTAMENTOS".equals(event.getEntity().getName()))
			calcHorasTrabalhadas(event, vo, false);
	}

	public void calcHorasTrabalhadas(PersistenceEvent event, DynamicVO vo, boolean isDelete) throws Exception {
		System.out.println("EVO - SOMA TOTAL DE HORAS APONTADAS - INICIO");
		BigDecimal numos = vo.asBigDecimal("NUMOS");
		BigDecimal numItem = vo.asBigDecimal("NUMITEM");
		String totalHorasApontadas = null;
		if("AD_APONTAMENTOS".equals(event.getEntity().getName()) && !isDelete)
			totalHorasApontadas = GetDadosApontamentos.totalHorasApontadas(vo, numos, numItem);
		else {
			if(GetDadosApontamentos.existeApontamentoAutomatico(numos, numItem) && !isDelete)
				return;
			if(!isDelete)
				totalHorasApontadas = GetDadosApontamentos.totalHorasApontadasManualmente(vo);
			else
				totalHorasApontadas = GetDadosApontamentos.totalHorasApontadasManualmenteItem(numos, numItem);
		}
		System.out.println("Total horas apontadas: "+totalHorasApontadas);
		if(totalHorasApontadas == null)
			totalHorasApontadas = BigDecimal.ZERO.toString();
		if("AD_APONTAMENTOS".equals(event.getEntity().getName()))
			atualizarHorasApontadasServicoExecutado(numos, numItem, totalHorasApontadas);
		else
			atualizarHorasApontadasManualmenteServicoExecutado(numos, numItem, totalHorasApontadas, vo);
		System.out.println("EVO - SOMA TOTAL DE HORAS APONTADAS - FIM");
	}

	private void atualizarHorasApontadasManualmenteServicoExecutado(BigDecimal numos, BigDecimal numItem, String totalHorasApontadas, DynamicVO vo) throws Exception {
		BigDecimal totalHoras = (new BigDecimal(totalHorasApontadas)).setScale(6, RoundingMode.HALF_UP);
		System.out.println("(Manual) Atualizando Serviço Executado "+numItem+" da OS "+numos+" com valor: "+totalHoras);
		vo.setProperty("TOTHORASAPONTDEC", totalHoras);
		vo.setProperty("TOTHORASAPONT_TEXTO", GetDadosApontamentos.converterHorasEmTexto(totalHoras));
	}

	private void atualizarHorasApontadasServicoExecutado(BigDecimal numos, BigDecimal numItem, String totalHorasApontadas) throws Exception {
		BigDecimal totalHoras = (new BigDecimal(totalHorasApontadas)).setScale(6, RoundingMode.HALF_UP);
		System.out.println("Atualizando Serviço Executado "+numItem+" da OS "+numos+" com valor: "+totalHoras);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("TCSITE");
			DynamicVO servico = servicoExecutadoDAO.findOne("NUMITEM = "+numItem+" AND NUMOS = "+numos);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("TOTHORASAPONTDEC", totalHoras)
				.set("TOTHORASAPONT_TEXTO", GetDadosApontamentos.converterHorasEmTexto(totalHoras))
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
