package br.com.evonetwork.integracaoCamposDealer.Rotina;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.evonetwork.integracaoCamposDealer.Controller.ProspectController;
import br.com.evonetwork.integracaoCamposDealer.DAO.LogDAO;

public class BuscarClientesAcaoAgendada implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext ctx) {
		System.out.println("***EVO - BUSCAR CLIENTES ALTERADOS CAMPOS DEALER - INICIO***");
		try {
			ProspectController.iniciarBusca(ctx);
		} catch (Exception e) {
			ctx.info("Erro na execução: "+e.getMessage());
			try {
				LogDAO.criarLog("P", e.getMessage());
			} catch (Exception err){
				ctx.info("Erro na execu��o: "+err.getMessage());
			}
			System.out.println("Erro na execução: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - BUSCAR CLIENTES ALTERADOS CAMPOS DEALER - FIM***");
	}

}
