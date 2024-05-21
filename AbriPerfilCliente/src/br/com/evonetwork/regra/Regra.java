package br.com.evonetwork.regra;

import java.math.BigDecimal;

import br.com.evonetwork.botaoacao.AbrirPerfilCliente;
import br.com.evonetwork.url.GetLink;
import br.com.evonetwork.url.GetUrl;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

@SuppressWarnings("serial")
public class Regra implements br.com.sankhya.modelcore.comercial.Regra {

	@Override
	public void afterDelete(ContextoRegra ctx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(ContextoRegra ctx) throws Exception {
		
	}

	@Override
	public void afterUpdate(ContextoRegra ctx) throws Exception {
		PrePersistEntityState state = ctx.getPrePersistEntityState();		
		final DynamicVO registro = state.getOldVO();
		BigDecimal codparc = registro.asBigDecimal("CODPARC");
		
		final boolean isCabecalho = registro.getValueObjectID().indexOf(DynamicEntityNames.CABECALHO_NOTA) > -1;
		final boolean isItem = registro.getValueObjectID().indexOf(DynamicEntityNames.ITEM_NOTA) > -1;
		final boolean isFinanceiro= registro.getValueObjectID().indexOf(DynamicEntityNames.FINANCEIRO) > -1;

		if (isCabecalho) {
			String linkDaBase = "http://unapel2.nuvemdatacom.com.br:9707/";
			String linkDaTela = "br.com.sankhya.menu.adicional.nuDsb.99.1";
			String pkJason = "{\"P_CODPARC\":"+codparc+"}";
			String mensagem = "Acessa aqui";
			
			ctx.getBarramentoRegra().addMensagem(GetLink.getLink(linkDaTela, linkDaBase, pkJason, mensagem));
			
		}
		
		if (isItem) {
			
		}
		
		if (isFinanceiro) {
			
		}
	}

	@Override
	public void beforeDelete(ContextoRegra ctx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInsert(ContextoRegra ctx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(ContextoRegra ctx) throws Exception {
		// TODO Auto-generated method stub

	}

}
