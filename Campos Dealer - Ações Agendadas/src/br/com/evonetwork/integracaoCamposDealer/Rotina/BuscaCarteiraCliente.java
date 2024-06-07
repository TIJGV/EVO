package br.com.evonetwork.integracaoCamposDealer.Rotina;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.evonetwork.integracaoCamposDealer.Controller.CarteiraController;
import br.com.evonetwork.integracaoCamposDealer.DAO.LogDAO;

public class BuscaCarteiraCliente  implements ScheduledAction{
	public void onTime(ScheduledActionContext ctx) {
		System.out.println("***EVO - BUSCAR NEGOCIOS ALTERADOS CAMPOS DEALER - INICIO***");
		try {
			ArrayList<BigDecimal> vendedores = CarteiraController.buscarVendedores();

			for(BigDecimal v: vendedores) {
				CarteiraController.coletarDadosDaCarteira(v);
			}
			
		} catch (Exception e) {
			ctx.info("Erro na execução: "+e.getMessage());
			try {
				LogDAO.criarLog("C", e.getMessage());
			} catch (Exception err){
				ctx.info("Erro na execução: "+err.getMessage());
			}
			
			System.out.println("Erro na execução: "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("***EVO - BUSCAR NEGOCIOS ALTERADOS CAMPOS DEALER - FIM***");
	}

}
