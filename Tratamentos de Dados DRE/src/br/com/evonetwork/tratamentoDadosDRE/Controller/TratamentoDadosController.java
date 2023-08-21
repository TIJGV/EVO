package br.com.evonetwork.tratamentoDadosDRE.Controller;

import java.math.BigDecimal;

import br.com.evonetwork.tratamentoDadosDRE.DAO.BuscarDados;
import br.com.evonetwork.tratamentoDadosDRE.DAO.TratamentoDeDadosDAO;
import br.com.evonetwork.tratamentoDadosDRE.Model.TratamentoDadosDRE;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;

public class TratamentoDadosController {

	public static void tratarDados(PersistenceEvent event) throws Exception {
		DynamicVO lancamentoVO = (DynamicVO) event.getVo();
		BuscarDados buscarDados = new BuscarDados();
		TratamentoDadosDRE tratamentoDados = new TratamentoDadosDRE();
		if(centroDeResultadoEhValido(lancamentoVO)) {
			tratamentoDados.setCodCenCus(lancamentoVO.asBigDecimal("CODCENCUS"));
			tratamentoDados.setCodCtaCtb(lancamentoVO.asBigDecimal("CODCTACTB"));
			tratamentoDados.setCodEmp(lancamentoVO.asBigDecimal("CODEMP"));
			tratamentoDados.setNumLancamento(lancamentoVO.asBigDecimal("NUMLANC"));
			tratamentoDados.setNumLote(lancamentoVO.asBigDecimal("NUMLOTE"));
			tratamentoDados.setReferencia(lancamentoVO.asTimestamp("REFERENCIA"));
			tratamentoDados.setTipoLancamento(lancamentoVO.asString("TIPLANC"));
			tratamentoDados.setVlrLancamento(lancamentoVO.asBigDecimal("VLRLANC"));
			tratamentoDados.setCodAgrupDRE(buscarDados.buscarCodAgrupDre(tratamentoDados.getCodCtaCtb()));
			tratamentoDados.setCodUng(buscarDados.buscarCodUng(tratamentoDados.getCodCenCus()));
			TratamentoDeDadosDAO.criarTratamentoDeDados(tratamentoDados);
		} else {
			System.out.println("Centro de resultado não é válido.");
		}
	}

	private static boolean centroDeResultadoEhValido(DynamicVO lancamentoVO) {
		BigDecimal centroResultado = lancamentoVO.asBigDecimal("CODCENCUS");
		if(centroResultado != null && !centroResultado.equals(BigDecimal.ZERO))
			return true;
		return false;
	}

}
