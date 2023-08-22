package br.com.evonetwork.eventoprogramado;

import java.math.BigDecimal;

import br.com.evonetwork.campooberservacao.Observacao;
import br.com.evonetwork.gravarvaloranterior.CampoAntesAlteracao;
import br.com.evonetwork.tratamentodedados.ListaItensAditivosContrato;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoProgramado implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		DynamicVO verificacao = (DynamicVO) event.getVo();
		Observacao campoObservacao = new Observacao(event);
		CampoAntesAlteracao valorAntigo = new CampoAntesAlteracao(event, campoObservacao.getTipCampo());
		
	}

	@SuppressWarnings("unused")
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		DynamicVO verificacao = (DynamicVO) event.getVo();
		Observacao campoObservacao = new Observacao(event);	
		CampoAntesAlteracao valorAntigo = new CampoAntesAlteracao(event, campoObservacao.getTipCampo());
		
		VerificarItensAdicon(verificacao);
		
	}

	@Override
	public void beforeCommit(TransactionContext tranCtx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public void VerificarItensAdicon(DynamicVO verificacao) throws Exception{
		BigDecimal campoAlter = verificacao.asBigDecimal("CAMPOALTER");
		int contagem = 0;
		try {
			ListaItensAditivosContrato listaAdIteadicon = new ListaItensAditivosContrato();
			listaAdIteadicon.ListaDeItensAditivos(verificacao.asBigDecimal("CODADITIVO"));
			
			for (int i = 0; i < listaAdIteadicon.getListaCampoAlter().size(); i++) {
				System.out.println(campoAlter.compareTo(listaAdIteadicon.getListaCampoAlter().get(i)));
				if(campoAlter.compareTo(listaAdIteadicon.getListaCampoAlter().get(i)) == 0) {
					contagem++;
				}
				if(contagem > 1) {
					throw new Exception("O campo de alteração " + campoAlter + " já foi cadastrado!");
				}
			}
		} catch (Exception e) {
			throw new Exception("O campo de alteração " + campoAlter + " já foi cadastrado!");
		}
	}

}
