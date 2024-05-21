package br.com.evonetwork.deletarpedidosextras;

import java.util.ArrayList;

import br.com.evonetwork.deletarpedidos.DeletarPedidos;

public class DeletarPedidosExtras {
	
	public static void DeletPedidoExtra(ArrayList<DadosPedidosExtras> var, ArrayList<DadosPedidosExtras> cab) throws Exception {
		DeletarPedidos dp = new DeletarPedidos();
		for (int i = 0; i < var.size(); i++) {
			for (int j = 0; j < cab.size(); j++) {
				if(var.get(i).getCodemp().equals(cab.get(j).getCodemp()) 
						&& var.get(i).getDtneg().equals(cab.get(j).getDtneg())
						&& var.get(i).getCodtipoper().equals(cab.get(j).getCodtipoper())) {
					dp.removerCabs(cab.get(j).getNunota());
				}
			}
		}
	}
}
