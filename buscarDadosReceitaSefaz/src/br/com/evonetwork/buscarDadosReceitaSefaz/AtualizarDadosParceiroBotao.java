package br.com.evonetwork.buscarDadosReceitaSefaz;

import java.math.BigDecimal;

import br.com.evonetwork.buscarDadosReceitaSefaz.Controller.Controller;
import br.com.evonetwork.buscarDadosReceitaSefaz.Dao.ParceiroDao;
import br.com.evonetwork.buscarDadosReceitaSefaz.Model.Parceiro;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class AtualizarDadosParceiroBotao implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - INICIANDO ATUALIZAÇÃO DE PARCEIROS***");
		int count = 0;
		for (int i = 0; i < ca.getLinhas().length; i++) {
            Registro linha = ca.getLinhas()[i];
            try {
                body(ca, linha);
                count++;
                if(count == 3) {
                	System.out.println("***Esperando 61 segundos...");
                	Thread.sleep(61000);
                	System.out.println("***Fim da espera.");
                	count = 0;
                }
            } catch (Exception e) {
            	e.printStackTrace();
                ca.mostraErro(e.getMessage());
            }
        }
		ca.setMensagemRetorno("Parceiros atualizados.");
	}

	private void body(ContextoAcao ca, Registro linha) throws Exception {
		// TGFPAR - PARCEIRO
        BigDecimal codParc = (BigDecimal) linha.getCampo("CODPARC");
        JapeWrapper daoInfo = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
        DynamicVO tgfparVO = daoInfo.findByPK(codParc);
        
        Parceiro parceiroAtual = ParceiroDao.setarParceiroUtilizandoDynamicVo(tgfparVO);
		
		Controller.realizarChamada(tgfparVO, parceiroAtual, 2);
	}

}
