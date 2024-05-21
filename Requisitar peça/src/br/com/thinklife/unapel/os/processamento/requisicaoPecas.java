// 
// Decompiled by Procyon v0.5.36
// 

package br.com.thinklife.unapel.os.processamento;

import java.sql.ResultSet;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.EntityFacade;
import br.com.thinklife.unapel.os.save.saves;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import java.math.BigDecimal;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class requisicaoPecas {
	
    public static void requisitar(final ContextoAcao arg0) throws Exception {
        final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        final Registro[] registros = arg0.getLinhas();
        final BigDecimal usuLogado = arg0.getUsuarioLogado();
        BigDecimal nuNota = BigDecimal.ZERO;
        BigDecimal sequencia = BigDecimal.ZERO;
        BigDecimal vlrNota = BigDecimal.ZERO;
        for (int i = 0; i < registros.length; ++i) {
            final BigDecimal numos = new BigDecimal(new StringBuilder().append(registros[i].getCampo("NUMOS")).toString());
            final BigDecimal sequenciaPeca = new BigDecimal(new StringBuilder().append(registros[i].getCampo("SEQUENCIA")).toString());
            final BigDecimal peca = new BigDecimal(new StringBuilder().append(registros[i].getCampo("CODPROD")).toString());
            BigDecimal qtd = BigDecimal.ZERO;
            if (!new StringBuilder().append(registros[i].getCampo("QUANTIDADE")).toString().equalsIgnoreCase("null")) {
                qtd = new BigDecimal(new StringBuilder().append(registros[i].getCampo("QUANTIDADE")).toString());
            }
            BigDecimal vlrUnit = BigDecimal.ZERO;
            if (!new StringBuilder().append(registros[i].getCampo("VLRUNIT")).toString().equalsIgnoreCase("null")) {
                vlrUnit = new BigDecimal(new StringBuilder().append(registros[i].getCampo("VLRUNIT")).toString());
            }
            final BigDecimal vlrTotPeca = vlrUnit.multiply(qtd);
            vlrNota = vlrNota.add(vlrTotPeca);
            final String lote = new StringBuilder().append(registros[i].getCampo("LOTE")).toString();
            final String nroReq = new StringBuilder().append(registros[i].getCampo("NUMREQUISICAO")).toString();
            if (!nroReq.equals("null")) {
                arg0.mostraErro("Requisição já realizada!");
            }
            BigDecimal localPadrao = BigDecimal.ZERO;
            String necessarioLote = "";
            String unidadeMedida = "";
            String fechado = "";
            final ResultSet modNota = buscaDados.verificacaoPeca(jdbcWrapper, peca, numos);
            while (modNota.next()) {
                localPadrao = modNota.getBigDecimal("LOCALPAD");
                unidadeMedida = modNota.getString("CODVOL");
                necessarioLote = new StringBuilder().append(modNota.getString("NECESSARIOLOTE")).toString();
                fechado = new StringBuilder().append(modNota.getString("FECHAMENTO")).toString();
            }
            if (!necessarioLote.equals("N") && (lote.equals("null") || lote.isEmpty())) {
                arg0.mostraErro("Necessario informar o lote da ferramenta/produto!");
            }
            if (fechado.equalsIgnoreCase("S")) {
                arg0.mostraErro("Não pode ser feitas requisições em OS's fechadas!");
            }
            if (i < 1) {
                nuNota = saves.cabecalhoReqPeca(dwf, jdbcWrapper, numos, usuLogado);
            }
            sequencia = saves.item3(dwf, nuNota, peca, unidadeMedida, qtd, localPadrao, localPadrao, new BigDecimal(i + 1), lote, necessarioLote, vlrUnit, vlrTotPeca);
            buscaDados.updatePecaUtilizada(jdbcWrapper, nuNota, sequencia, numos, sequenciaPeca);
        }
        buscaDados.updateVlrNOTA(jdbcWrapper, nuNota, vlrNota);
        arg0.setMensagemRetorno("Peça(s) requisita(s)");
    }
    
}
