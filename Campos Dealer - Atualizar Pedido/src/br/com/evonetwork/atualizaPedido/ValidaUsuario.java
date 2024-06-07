package br.com.evonetwork.atualizaPedido;

import java.math.BigDecimal;

import br.com.evonetwork.atualizaPedido.Controller.CabecalhoNotaController;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import br.com.sankhya.ws.ServiceContext;

public class ValidaUsuario implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {

	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {

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
		evento(event);

	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		evento(event);

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		evento(event);

	}

	public void evento(PersistenceEvent e) throws Exception {
		System.out.println("***EVO - VALIDAR USUÄRIO LOGADO NOTA CAMPOS DEALER - INICIO***");
		DynamicVO r = (DynamicVO) e.getVo();
		
		BigDecimal nunota = r.asBigDecimal("NUNOTA");
		BigDecimal numos = CabecalhoNotaController.buscarNumos(nunota);
		
		BigDecimal usuarioCD = new BigDecimal(MGECoreParameter.getParameterAsInt("USUAPICAMPOSD"));
		BigDecimal usuariologado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID();
		
		boolean origemCamposDealer = CabecalhoNotaController.validaOrigemCD(nunota, numos);
		
		if(origemCamposDealer && usuariologado.compareTo(usuarioCD) != 0) {
			throw new Exception("Nota com origem no Campos Dealer não pode ser alterada!");
		}
		
		
		System.out.println("***EVO - VALIDAR USUÄRIO LOGADO NOTA CAMPOS DEALER - INICIO***");
	}

}
