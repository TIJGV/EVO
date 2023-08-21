package br.com.thinklife.unapel.os.processamento;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.EntityFacade;
import br.com.thinklife.unapel.os.save.saves;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import br.com.sankhya.jape.util.JapeSessionContext;
import java.math.BigDecimal;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.evonetwork.gerarPedidoOS.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class processarPecasUtilizadasOS
{
    @SuppressWarnings("unused")
	public static void processar(final ContextoAcao arg0) throws Exception {
        final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        final Registro[] linhas = arg0.getLinhas();
        BigDecimal numos = BigDecimal.ZERO;
        String fechamento = "";
        BigDecimal notaOrigem = BigDecimal.ZERO;
        final BigDecimal usuLogado = (BigDecimal)JapeSessionContext.getRequiredProperty("usuario_logado");
        for (int i = 0; i < linhas.length; ++i) {
            numos = new BigDecimal(new StringBuilder().append(linhas[i].getCampo("NUMOS")).toString());
            fechamento = new StringBuilder().append(linhas[i].getCampo("FECHAMENTO")).toString();
            notaOrigem = new BigDecimal(new StringBuilder().append(linhas[i].getCampo("NUNOTA")).toString());
            if (!fechamento.equals("S")) {
                arg0.mostraErro("Não é possível gerar pedido para Ordem de Serviço em aberto");
            }
            final String consultaTCSPRO = buscaDados.consultaTCSPRO(numos);
            final PreparedStatement prepTCSPRO = jdbcWrapper.getPreparedStatement(consultaTCSPRO);
            final ResultSet rsTCSPRO = prepTCSPRO.executeQuery();
            String tipMovPecaOld = "";
            BigDecimal preferenciaModNota = BigDecimal.ZERO;
            BigDecimal nunota = BigDecimal.ZERO;
            BigDecimal sequenciaItem = BigDecimal.ZERO;
            BigDecimal vlrNota = BigDecimal.ZERO;
            int contador = 0;
            while (rsTCSPRO.next()) {
                final BigDecimal localOrigem = rsTCSPRO.getBigDecimal("LOCAL_DEST");
                final BigDecimal codprod = rsTCSPRO.getBigDecimal("CODPROD");
                String usoProd = Controller.getUsoProd(codprod);
                final String codvol = rsTCSPRO.getString("CODVOL");
                final BigDecimal quantidade = rsTCSPRO.getBigDecimal("QUANTIDADE");
                final BigDecimal vlrUnit = rsTCSPRO.getBigDecimal("VLRUNIT");
                final BigDecimal valortotal = rsTCSPRO.getBigDecimal("VLRTOTAL");
                vlrNota = vlrNota.add(valortotal);
                final BigDecimal sequencia = rsTCSPRO.getBigDecimal("SEQUENCIA");
                final BigDecimal nunotaItem = rsTCSPRO.getBigDecimal("NUNOTA2");
                final BigDecimal nunotaped = rsTCSPRO.getBigDecimal("NUNOTAPED");
                String lote = rsTCSPRO.getString("LOTE");
                final String tipMovPeca = rsTCSPRO.getString("TIPOMOVIMENTO");
                final String necessarioLote = new StringBuilder().append(rsTCSPRO.getString("NECESSARIOLOTE")).toString();
                if (!necessarioLote.equals("N") && (lote.equals("null") || lote.isEmpty())) {
                    arg0.mostraErro("<br><br>Necessario informar o lote do produto: " + codprod + " <br><br>");
                }
                if (lote.equals("null")) {
                    lote = "";
                }
                Label_0734: {
                    final String s;
                    switch (s = tipMovPeca) {
                        case "G": {
                            preferenciaModNota = new BigDecimal(new StringBuilder().append(arg0.getParametroSistema("MODNOTAGARANTIA")).toString());
                            break Label_0734;
                        }
                        case "P": {
                            preferenciaModNota = new BigDecimal(new StringBuilder().append(arg0.getParametroSistema("MODNOTAPEDCOMPR")).toString());
                            break Label_0734;
                        }
                        case "R": {
                            preferenciaModNota = new BigDecimal(new StringBuilder().append(arg0.getParametroSistema("MODNOTAPEDREQ")).toString());
                            break Label_0734;
                        }
                        case "S": {
                            preferenciaModNota = new BigDecimal(new StringBuilder().append(arg0.getParametroSistema("MODNOTASOLCOMPR")).toString());
                            break Label_0734;
                        }
                        case "U": {
                            preferenciaModNota = new BigDecimal(new StringBuilder().append(arg0.getParametroSistema("MODNOTAREUTILIZ")).toString());
                            break Label_0734;
                        }
                        default:
                            break;
                    }
                    arg0.mostraErro("<br><br>Tipo de movimentação de peça não selecionado!<br><br>");
                }
                BigDecimal vendedor = BigDecimal.ZERO;
                BigDecimal localVenda = BigDecimal.ZERO;
                BigDecimal codemp = BigDecimal.ZERO;
                BigDecimal codparc = BigDecimal.ZERO;
                BigDecimal codparcDest = BigDecimal.ZERO;
                BigDecimal codtipoper = BigDecimal.ZERO;
                BigDecimal codtipvenda = BigDecimal.ZERO;
                BigDecimal codnat = BigDecimal.ZERO;
                BigDecimal codcencus = BigDecimal.ZERO;
                BigDecimal numnota = BigDecimal.ZERO;
                BigDecimal localpad = BigDecimal.ZERO;
                final BigDecimal localpadcliente = BigDecimal.ZERO;
                Timestamp dtneg = null;
                String tipmov = "";
                String tipoOs = "";
                final String consultaModeloCabecalho = buscaDados.modNotaPecasUtilizadas(numos, preferenciaModNota, codprod, usuLogado);
                final PreparedStatement prepCabecalho = jdbcWrapper.getPreparedStatement(consultaModeloCabecalho);
                final ResultSet rsCabecalho = prepCabecalho.executeQuery();
                while (rsCabecalho.next()) {
                    localVenda = rsCabecalho.getBigDecimal("LOCAL_VENDA");
                    codemp = rsCabecalho.getBigDecimal("CODEMP");
                    codparc = rsCabecalho.getBigDecimal("CODPARC");
                    codparcDest = rsCabecalho.getBigDecimal("CODPARCDEST");
                    codtipoper = rsCabecalho.getBigDecimal("CODTIPOPER");
                    codtipvenda = rsCabecalho.getBigDecimal("CODTIPVENDA");
                    codnat = rsCabecalho.getBigDecimal("CODNAT");
                    codcencus = rsCabecalho.getBigDecimal("CODCENCUS");
                    if (codcencus.compareTo(BigDecimal.ZERO) == 0) {
                        arg0.mostraErro("Favor preencher centro de resultado no cadastro da empresa!");
                    }
                    numnota = rsCabecalho.getBigDecimal("NUMNOTA");
                    localpad = rsCabecalho.getBigDecimal("LOCALPAD");
                    dtneg = ArrumaData(rsCabecalho.getString("DTNEG"));
                    tipmov = rsCabecalho.getString("TIPMOV");
                    tipoOs = rsCabecalho.getString("TIPOOS");
                    vendedor = rsCabecalho.getBigDecimal("VENDEDOR");
                }
                BigDecimal codVeiculo = (BigDecimal) linhas[i].getCampo("CODVEICULO");
                if (contador <= 0 || (!tipMovPeca.equals(tipMovPecaOld) && nunotaped.compareTo(BigDecimal.ZERO) <= 0)) {
                    nunota = saves.cabecalhoPecasUtilizadas(dwf, codemp, codparc, codparcDest, numnota, codtipoper, codtipvenda, codnat, codcencus, numos, tipmov, tipoOs, localVenda, vendedor, codVeiculo);
                    saves.salvarTCSNOTAS(dwf, codtipoper, codparc, nunota, dtneg, numos);
                    contador = 0;
                }
                if (nunotaped.compareTo(BigDecimal.ZERO) == 0) {
                    final String reserva = "S";
                    final BigDecimal atualestoque = BigDecimal.ONE;
                    sequenciaItem = saves.itemPecasUtilizadas(dwf, nunota, codprod, codvol, quantidade, localOrigem, null, new BigDecimal(contador + 1), valortotal, lote, atualestoque, reserva, vlrUnit, usoProd);
                    final String update = "UPDATE AD_TCSPRO SET NUNOTAPED= " + nunota + " where NUMOS= " + numos + " and SEQUENCIA= " + sequencia;
                    final PreparedStatement prepUpdate = jdbcWrapper.getPreparedStatement(update);
                    prepUpdate.executeUpdate();
                    final String consultaTGFVAR = buscaDados.consultaTGFVAR(nunota);
                    final PreparedStatement prepTGFVAR = jdbcWrapper.getPreparedStatement(consultaTGFVAR);
                    final ResultSet rsTGFVAR = prepTGFVAR.executeQuery();
                    while (rsTGFVAR.next()) {
                        final String statusnota = rsTGFVAR.getString("STATUSNOTA");
                        saves.salvarTGFVAR(dwf, nunota, sequenciaItem, notaOrigem, new BigDecimal(1), statusnota);
                    }
                    ++contador;
                }
                tipMovPecaOld = tipMovPeca;
            }
            final String update2 = "UPDATE TGFCAB SET VLRNOTA = " + vlrNota + " WHERE NUNOTA = " + nunota;
            final PreparedStatement pstmNota = jdbcWrapper.getPreparedStatement(update2);
            pstmNota.executeUpdate();
            arg0.setMensagemRetorno("<br><font size=\"20\" >Notas geradas com sucesso, confira na aba \"Notas Geradas\"!</font><br>");
        }
        jdbcWrapper.closeSession();
    }
    
    @SuppressWarnings("unused")
	public static Timestamp ArrumaData(final String data) throws Exception {
        final String datainicial = data;
        final String horainicial = datainicial.split(" ")[1];
        final String[] datainicial2 = datainicial.replace(horainicial, "").split("-");
        final String diadata = datainicial2[2];
        final String mesdata = datainicial2[1];
        final String anodata = datainicial2[0];
        final String dataa;
        final String data_nova_inicial = dataa = String.valueOf(diadata.replace(" ", "")) + "/" + mesdata + "/" + anodata;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final Date d1Inicial = dateFormat.parse(String.valueOf(dataa) + " " + horainicial.replace(".0", ""));
        return new Timestamp(d1Inicial.getTime());
    }
}
