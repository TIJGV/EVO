package br.com.evonetwork.eventoprogramado;

import java.math.BigDecimal;

import br.com.evonetwork.clacadpap.ClassificacaoProspect;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoProgramado implements EventoProgramavelJava{

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
		System.out.println(">> afterUpdate");
		DynamicVO registro = (DynamicVO) event.getVo();
		BigDecimal codpap = registro.asBigDecimal("CODPAP");
		System.out.println("Teste 111");
		System.out.println("CODPAP: " + codpap);
		ClassificacaoProspect.getCodclassifcadpap(codpap);
		System.out.println("<< afterUpdate");
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
//		DynamicVO registro = (DynamicVO) event.getVo();
//		BigDecimal codpap = registro.asBigDecimal("CODPAP");
//		System.out.println("Teste 778");
//		System.out.println("CODPAP: " + codpap);
//		System.out.println(">> beforeUpdate");
//		ClassificacaoProspect.getCodclassifcadpap(codpap);
//		System.out.println("<< beforeUpdate");		
	}

}
