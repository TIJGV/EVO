package br.com.evonetwork.evento;

import java.math.BigDecimal;

import br.com.evonetwork.adtcsose.AdTcsose;
import br.com.evonetwork.adtcsose.DadosParaCab;
import br.com.evonetwork.tgfcab.Tgfcab;
import br.com.evonetwork.tgfite.Tgfite;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class GerarPedido implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		evento(event);
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		evento(event);
	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		evento(event);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {

	}

	public void evento(PersistenceEvent event) throws Exception {
		try {
			System.out.println("EVO - GERAR PEDIDO DA NEGOCIACAO - EVENTO NAS TABELA DE PECAS teste 3");
			DynamicVO registro = (DynamicVO) event.getVo();

			BigDecimal numos = registro.asBigDecimal("NUMOS");
			DadosParaCab cab = AdTcsose.dadosParaCab(numos);
			BigDecimal nunota = cab.getNunota();

			if (!Tgfcab.nunotaExiste(nunota)) {
				nunota = null;
			}

			if (nunota == null) {
				BigDecimal nunotaCriado = Tgfcab.criarCab(cab, numos);
				AdTcsose.updateAdNunotasimdesc(numos, nunotaCriado);
				Tgfite.criarIte(numos, nunotaCriado, registro);
				Tgfcab.updateVlrdesctotitem(nunotaCriado, numos);
			} else {
				Tgfite.removerIte(nunota);
				Tgfite.criarIte(numos, nunota, registro);
				Tgfcab.updateVlrdesctotitem(nunota, numos);
			}

			System.out.println("FIM - GERAR PEDIDO DA NEGOCIACAO - EVENTO NAS TABELA DE PECAS");

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

}
