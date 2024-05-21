package br.com.evonetwork.eventoprogramado;

import java.math.BigDecimal;

import br.com.evonetwork.adadicon.UpdateAdAdicon;
import br.com.evonetwork.tgfcab.UpdateCab;
import br.com.evonetwork.url.GetLink;
import br.com.evonetwork.url.GetUrl;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoProgramadoPerfilCliente implements EventoProgramavelJava {

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
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		System.out.println("Perfil do cliente teste 777");
		DynamicVO registro = (DynamicVO) event.getOldVO();
		BigDecimal codaditivo = registro.asBigDecimal("CODADITIVO");
		//String link = GetLink.getLink();
//		System.out.println(link);
//		UpdateAdAdicon.update(codaditivo, link);
	}

}
