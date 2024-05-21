package br.com.evonetwork.botaoacao;

import java.math.BigDecimal;

import br.com.evonetwork.url.GetLink;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class AbrirPerfilCliente implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		try {
			Registro[] linhas = contexto.getLinhas();

			for (Registro linha : linhas) {
				
				BigDecimal codparc = (BigDecimal) linha.getCampo("CODPARC");
				String linkDaBase = "http://unapel2.nuvemdatacom.com.br:9707/";
				String linkDaTela = "br.com.sankhya.menu.adicional.nuDsb.99.1";
				String pkJason = "{\"P_CODPARC\":"+codparc+"}";
				String mensagem = "Acessa aqui";
			
				contexto.setMensagemRetorno(GetLink.getLink(linkDaBase, linkDaTela, pkJason, mensagem));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
