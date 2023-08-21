package br.com.evonetwork.tratamentoDadosDRE.DAO;

import br.com.evonetwork.tratamentoDadosDRE.Model.TratamentoDadosDRE;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class TratamentoDeDadosDAO {

	public static void criarTratamentoDeDados(TratamentoDadosDRE tratamentoDados) throws Exception {
		System.out.println("Criando registro...");
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);//em casos de deadlock, esta sess o cai
			JapeWrapper tratamentoDAO = JapeFactory.dao("AD_TRATDADOSDRE");
			@SuppressWarnings("unused")
			DynamicVO save = tratamentoDAO.create()
				.set("CODCENCUS", tratamentoDados.getCodCenCus())
				.set("CODAGRUPDRE", tratamentoDados.getCodAgrupDRE())
				.set("CODCTACTB", tratamentoDados.getCodCtaCtb())
				.set("CODEMP", tratamentoDados.getCodEmp())
				.set("CODUNG", tratamentoDados.getCodUng())
				.set("REFERENCIA", tratamentoDados.getReferencia())
				.set("NUMLOTE", tratamentoDados.getNumLote())
				.set("NUMLANC", tratamentoDados.getNumLancamento())
				.set("TIPLANC", tratamentoDados.getTipoLancamento())
				.set("VLRLANC", tratamentoDados.getVlrLancamento())
				.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	

}
