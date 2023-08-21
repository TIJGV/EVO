package br.com.evonetwork.buscarDadosReceitaSefaz;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class CriarParceiroBotao implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - INICIANDO INSERÇÃO DE PARCEIRO***");
		
		String cgcCpf = (String) ca.getParam("CGC_CPF");
		cgcCpf = cgcCpf.replace(".", "")
	            .replace(" ", "")
	            .replace("-", "")
	            .replace("/", "")
	            .replace("\u00AD", "");
		
		String tipPessoa = null;
		
		System.out.println("***CNPJ/CPF: "+cgcCpf);
		
		JapeSession.SessionHandle hnd = null;
		
		if(cgcCpf.length() == 11) {
			tipPessoa = "F";
		} else {
			tipPessoa = "J";
		}
		
		try {
			hnd = JapeSession.open();
			JapeWrapper InsertPAR = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
			@SuppressWarnings("unused")
			DynamicVO salvar = InsertPAR.create()
					.set("CGC_CPF", cgcCpf)
					.set("NOMEPARC", "Nome não encontrado")
					.set("IDENTINSCESTAD", cgcCpf)
					.set("TIPPESSOA", tipPessoa)
					.set("CODCID", BigDecimal.ONE)
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Dados não encontrados.");
		} finally {
			JapeSession.close(hnd);
		}
		ca.setMensagemRetorno("Parceiro cadastrado.");
	}
}
