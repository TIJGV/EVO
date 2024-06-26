package br.com.thinklife.unapel.os.processamento;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.evonetwork.gerarPedidoOS.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import br.com.thinklife.unapel.os.save.saves;

public class processarPecasUtilizadasOS {
	
	public static void processar(ContextoAcao arg0) throws Exception {
        EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        Registro[] linhas = arg0.getLinhas();
        BigDecimal numos = BigDecimal.ZERO;
        String fechamento = "";
        BigDecimal notaOrigem = BigDecimal.ZERO;
        BigDecimal usuLogado = (BigDecimal)JapeSessionContext.getRequiredProperty("usuario_logado");
        for (int i = 0; i < linhas.length; ++i) {
            numos = new BigDecimal(new StringBuilder().append(linhas[i].getCampo("NUMOS")).toString());
            fechamento = new StringBuilder().append(linhas[i].getCampo("FECHAMENTO")).toString();
            BigDecimal nroOrcamentoOrigem = (BigDecimal) linhas[i].getCampo("NUNOTA");
            if(nroOrcamentoOrigem == null)
            	notaOrigem = null;
            else
            	notaOrigem = new BigDecimal(new StringBuilder().append(nroOrcamentoOrigem).toString());
            if(notaOrigem == null)
            	System.out.println("NUNOTA Nulo, não serão criados registros na TGFVAR.");
            if (!fechamento.equals("S")) {
                arg0.mostraErro("Não é possível gerar pedido para Ordem de Serviço em aberto");
            }
            String consultaTCSPRO = buscaDados.consultaTCSPRO(numos);
            PreparedStatement prepTCSPRO = jdbcWrapper.getPreparedStatement(consultaTCSPRO);
            ResultSet rsTCSPRO = prepTCSPRO.executeQuery();
            String tipMovPecaOld = "";
            BigDecimal preferenciaModNota = BigDecimal.ZERO;
            BigDecimal nunota = BigDecimal.ZERO;
            BigDecimal sequenciaItem = BigDecimal.ZERO;
            BigDecimal vlrNota = BigDecimal.ZERO;
            int contador = 0;
            while (rsTCSPRO.next()) {
                BigDecimal localOrigem = rsTCSPRO.getBigDecimal("LOCAL_DEST");
                BigDecimal codprod = rsTCSPRO.getBigDecimal("CODPROD");
                String usoProd = Controller.getUsoProd(codprod);
                String codvol = rsTCSPRO.getString("CODVOL");
                BigDecimal quantidade = rsTCSPRO.getBigDecimal("QUANTIDADE");
                BigDecimal vlrUnit = rsTCSPRO.getBigDecimal("VLRUNIT");
                BigDecimal valortotal = rsTCSPRO.getBigDecimal("VLRTOTAL");
                BigDecimal sequencia = rsTCSPRO.getBigDecimal("SEQUENCIA");
//                BigDecimal nunotaItem = rsTCSPRO.getBigDecimal("NUNOTA2");
                BigDecimal nunotaped = rsTCSPRO.getBigDecimal("NUNOTAPED");
                String lote = rsTCSPRO.getString("LOTE");
                String tipMovPeca = rsTCSPRO.getString("TIPOMOVIMENTO");
//                BigDecimal descReais = rsTCSPRO.getBigDecimal("DESCONTOREAIS");
//                BigDecimal descPorcentagem = rsTCSPRO.getBigDecimal("DESCONTOPCTGM");
//                if(descReais != null)
//                	vlrNota = vlrNota.add(valortotal.subtract(descReais));
//                else
//                	vlrNota = valortotal;
                String necessarioLote = new StringBuilder().append(rsTCSPRO.getString("NECESSARIOLOTE")).toString();
                if (!necessarioLote.equals("N") && (lote.equals("null") || lote.isEmpty())) {
                    arg0.mostraErro("<br><br>Necessario informar o lote do produto: " + codprod + " <br><br>");
                }
                if (lote.equals("null")) {
                    lote = "";
                }
                Label_0734: {
                    switch (tipMovPeca) {
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
//                BigDecimal localpad = BigDecimal.ZERO;
//                BigDecimal localpadcliente = BigDecimal.ZERO;
                Timestamp dtneg = null;
                String tipmov = "";
                String tipoOs = "";
                String consultaModeloCabecalho = buscaDados.modNotaPecasUtilizadas(numos, preferenciaModNota, codprod, usuLogado);
                System.out.println("SQL: "+consultaModeloCabecalho);
                PreparedStatement prepCabecalho = jdbcWrapper.getPreparedStatement(consultaModeloCabecalho);
                ResultSet rsCabecalho = prepCabecalho.executeQuery();
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
//                    localpad = rsCabecalho.getBigDecimal("LOCALPAD");
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
                    String reserva = "S";
                    BigDecimal atualestoque = BigDecimal.ONE;
                    sequenciaItem = saves.itemPecasUtilizadas(dwf, nunota, codprod, codvol, quantidade, localOrigem, null, new BigDecimal(contador + 1), valortotal, lote, atualestoque, reserva, vlrUnit, usoProd, null, null);
                    String update = "UPDATE AD_TCSPRO SET NUNOTAPED= " + nunota + " where NUMOS= " + numos + " and SEQUENCIA= " + sequencia;
                    PreparedStatement prepUpdate = jdbcWrapper.getPreparedStatement(update);
                    prepUpdate.executeUpdate();
                    String consultaTGFVAR = buscaDados.consultaTGFVAR(nunota);
                    PreparedStatement prepTGFVAR = jdbcWrapper.getPreparedStatement(consultaTGFVAR);
                    ResultSet rsTGFVAR = prepTGFVAR.executeQuery();
                    while (rsTGFVAR.next()) {
                        String statusnota = rsTGFVAR.getString("STATUSNOTA");
                        if(notaOrigem != null)
                        	saves.salvarTGFVAR(dwf, nunota, sequenciaItem, notaOrigem, new BigDecimal(1), statusnota);
                    }
                    ++contador;
                }
                tipMovPecaOld = tipMovPeca;
            }
            String update2 = "UPDATE TGFCAB SET VLRNOTA = " + vlrNota + " WHERE NUNOTA = " + nunota;
            PreparedStatement pstmNota = jdbcWrapper.getPreparedStatement(update2);
            pstmNota.executeUpdate();
            arg0.setMensagemRetorno("<br><font size=\"20\" >Notas geradas com sucesso, confira na aba \"Notas Geradas\"!</font><br>");
        }
        jdbcWrapper.closeSession();
    }
    
    @SuppressWarnings("unused")
	public static Timestamp ArrumaData(String data) throws Exception {
        String datainicial = data;
        String horainicial = datainicial.split(" ")[1];
        String[] datainicial2 = datainicial.replace(horainicial, "").split("-");
        String diadata = datainicial2[2];
        String mesdata = datainicial2[1];
        String anodata = datainicial2[0];
        String dataa;
        String data_nova_inicial = dataa = String.valueOf(diadata.replace(" ", "")) + "/" + mesdata + "/" + anodata;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d1Inicial = dateFormat.parse(String.valueOf(dataa) + " " + horainicial.replace(".0", ""));
        return new Timestamp(d1Inicial.getTime());
    }
}
