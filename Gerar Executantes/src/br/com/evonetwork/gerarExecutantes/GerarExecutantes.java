package br.com.evonetwork.gerarExecutantes;

import java.util.ArrayList;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.evonetwork.gerarExecutantes.Controller.Controller;
import br.com.evonetwork.gerarExecutantes.Model.Etapa;

public class GerarExecutantes implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext ctx) {
		try {
			ArrayList<Etapa> etapas = Controller.buscarEtapas();
			
			for(Etapa e: etapas) {
				Controller.gerarExecutante(e);
			}
			
		} catch (Exception e) {
			ctx.info(e.getMessage());
		}
		
	}

}
