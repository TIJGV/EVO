package br.com.evonetwork.integracaoCamposDealer.Rotina;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.evonetwork.integracaoCamposDealer.Controller.NegocioController;
import br.com.evonetwork.integracaoCamposDealer.DAO.LogDAO;

public class BuscaNegociacao implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext ctx) {
		System.out.println("***EVO - BUSCAR NEGOCIOS ALTERADOS CAMPOS DEALER - INICIO***");
		try {
			NegocioController.iniciarBusca(ctx);
		} catch (Exception e) {
			ctx.info("Erro na execução: "+e.getMessage());
			try {
				LogDAO.criarLog("N", e.getMessage());
			} catch (Exception err){
				ctx.info("Erro na execução: "+err.getMessage());
			}
			
			System.out.println("Erro na execução: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - BUSCAR NEGOCIOS ALTERADOS CAMPOS DEALER - FIM***");
	}

	

}