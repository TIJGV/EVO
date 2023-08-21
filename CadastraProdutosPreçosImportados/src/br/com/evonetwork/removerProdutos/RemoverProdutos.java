package br.com.evonetwork.removerProdutos;

import java.math.BigDecimal;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class RemoverProdutos implements EventoProgramavelJava{

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		System.out.println("Iniciando remoção de produto");
		//TGFPRO
		DynamicVO tgfproVO = (DynamicVO) event.getVo();
		
		BigDecimal codProd = tgfproVO.asBigDecimal("CODPROD");
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			System.out.println("Deletando produto "+codProd+" da AD_DADOSPRODUTO");
			JapeWrapper dadosProdutoDAO = JapeFactory.dao("AD_DADOSPRODUTO");
			dadosProdutoDAO.deleteByCriteria("CODPROIMP = ? ", codProd);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JapeSession.close(hnd);
		}
		System.out.println("Fim remoção de produto");
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {}

}
