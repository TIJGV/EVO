package br.com.evonetwork.importarNota.Controller;

import java.math.BigDecimal;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class GerarImportacaoController {

	public static BigDecimal gerarImportacao(BigDecimal nuNota) throws Exception {
		JapeSession.SessionHandle hnd = null;
		BigDecimal nroUnico = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
			JapeWrapper importacaoNotaDAO = JapeFactory.dao("AD_IMPNOTA");
			DynamicVO registro = importacaoNotaDAO.create()
				.set("NUNOTA", nuNota)
				.set("NUCONFIG", BigDecimal.valueOf(3)) //nro único da configuração na tela Configuração de Importação de Planilhas
				.save();
			nroUnico = registro.asBigDecimal("NUNICO");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return nroUnico;
	}

}
