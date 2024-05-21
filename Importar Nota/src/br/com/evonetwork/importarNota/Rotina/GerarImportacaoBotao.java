package br.com.evonetwork.importarNota.Rotina;

import java.math.BigDecimal;

import org.apache.commons.codec.binary.Base64;

import br.com.evonetwork.importarNota.Controller.GerarImportacaoController;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class GerarImportacaoBotao implements AcaoRotinaJava{

	//Botão na TGFCAB
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - GERANDO IMPORTAÇÃO DE NOTA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		if(linhas.length == 0) {
			throw new Exception("Selecione uma nota!");
		}
		if(linhas.length > 1) {
			ca.setMensagemRetorno("Selecione apenas uma nota por vez!");
		} else {
			try {
				System.out.println("Gerando a importação para a nota: "+linhas[0].getCampo("NUNOTA"));
				BigDecimal nunico = GerarImportacaoController.gerarImportacao((BigDecimal)linhas[0].getCampo("NUNOTA"));
				String url = abrirTela(nunico);
				ca.setMensagemRetorno(url);
			} catch (Exception e) {
				throw new Exception("Erro na execução da importação "+linhas[0].getCampo("NUNOTA")+": "+e.getMessage());
			}
		}
		System.out.println("***EVO - GERANDO IMPORTAÇÃO DE NOTA - FIM***");
	}

	private String abrirTela(BigDecimal nunico) throws Exception {
		String tela = "br.com.sankhya.menu.adicional.AD_IMPNOTA";
		byte[] encodedBytesTela = Base64.encodeBase64(tela.getBytes());
		
		String parametros = "{\"NUNICO\":"+nunico+"}";
		byte[] encodedBytesParametros = Base64.encodeBase64(parametros.getBytes());
		
		//Base de testes
//		String link = "http://unapel.nuvemdatacom.com.br:9707/mge/system.jsp#app/"+new String(encodedBytesTela)+"/"+new String(encodedBytesParametros)+"/";
		
		//Base de produção
		String link = MGECoreParameter.getParameterAsString("URLSANKHYA")+"/mge/system.jsp#app/"+new String(encodedBytesTela)+"/"+new String(encodedBytesParametros)+"/";
		System.out.println("Link Imp Notas: "+link);
		
		String url = "<a target=\"_parent\" title=\"Importação de Nota gerada com sucesso\" href=\""+link+"\" >Abrir Importação de Nota</a>";
		
		return url;
	}

}
