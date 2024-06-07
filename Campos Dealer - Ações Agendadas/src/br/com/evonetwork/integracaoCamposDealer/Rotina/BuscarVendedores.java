package br.com.evonetwork.integracaoCamposDealer.Rotina;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.evonetwork.integracaoCamposDealer.Controller.VendedorController;
import br.com.evonetwork.integracaoCamposDealer.DAO.LogDAO;

public class BuscarVendedores implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext ctx) {
		System.out.println("***EVO - BUSCAR VENDEDORES CAMPOS DEALER - INICIO***");
		try {
			VendedorController.iniciarBusca(ctx);
		} catch (Exception e) {
			ctx.info("Erro na execução: "+e.getMessage());
			try {
				LogDAO.criarLog("V", e.getMessage());
			} catch (Exception err){
				ctx.info("Erro na execu��o: "+err.getMessage());
			}
			System.out.println("Erro na execução: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - BUSCAR VENDEDORES ALTERADOS CAMPOS DEALER - FIM***");
	}

}
