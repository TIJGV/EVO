package br.com.evonetwork.url;

import java.math.BigDecimal;

import org.apache.commons.codec.binary.Base64;


public class GetUrl {
	@SuppressWarnings("unused")
	public String abrirTela(BigDecimal codparc) {
		String tela = "br.com.sankhya.menu.adicional.nuDsb.99.1";
		byte[] encodedBytesTela = Base64.encodeBase64(tela.getBytes());
		String parametros = "{\"P_CODPARC\":"+codparc+"}";
		byte[] encodedBytesParametros = Base64.encodeBase64(parametros.getBytes());		
		//Base de testes

//		String link = "http://unapel.nuvemdatacom.com.br:9707/mge/system.jsp#app/"+new String(encodedBytesTela)+"/"+new String(encodedBytesParametros)+"/";
		//Base de produção

		String link = "http://unapel2.nuvemdatacom.com.br:9707/mge/system.jsp#app/"+new String(encodedBytesTela)+"/"+new String(encodedBytesParametros)+"/";
		String url = "<a target=\"_parent\" title=\"Importação de Nota gerada com sucesso\" href=\""+link+"\" >Abrir Perfil do Cliente</a>";
		return url;

	}
}
