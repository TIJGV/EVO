package br.com.thinklife.unapel.os.processamento;

import javax.xml.bind.DatatypeConverter;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.EntityFacade;
import br.com.thinklife.unapel.os.save.saves;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import java.math.BigDecimal;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class processarTransfMqpUtilizada
{
    public static void gerarTransferencia(final ContextoAcao arg0) throws Exception {
        final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        final BigDecimal usu = arg0.getUsuarioLogado();
        final Registro[] registros = arg0.getLinhas();
        BigDecimal nuNota = BigDecimal.ZERO;
        BigDecimal sequencia = BigDecimal.ZERO;
        for (int i = 0; i < registros.length; ++i) {
            final BigDecimal numos = new BigDecimal(new StringBuilder().append(registros[i].getCampo("NUMOS")).toString());
            final BigDecimal seqmaquina = new BigDecimal(new StringBuilder().append(registros[i].getCampo("CODMAQUTILIZADA")).toString());
            final BigDecimal maquina = new BigDecimal(new StringBuilder().append(registros[i].getCampo("CODPROD")).toString());
            final BigDecimal qtd = new BigDecimal(new StringBuilder().append(registros[i].getCampo("QTD")).toString());
            final String lote = new StringBuilder().append(registros[i].getCampo("LOTE")).toString();
            final String nroReq = new StringBuilder().append(registros[i].getCampo("NUMREQUISICAO")).toString();
            if (!nroReq.equals("null")) {
                arg0.mostraErro("Requisição já realizada!");
            }
            BigDecimal parcDest = BigDecimal.ZERO;
            BigDecimal codEmp = BigDecimal.ZERO;
            BigDecimal codParc = BigDecimal.ZERO;
            BigDecimal codTipOper = BigDecimal.ZERO;
            BigDecimal codTipVenda = BigDecimal.ZERO;
            BigDecimal codNat = BigDecimal.ZERO;
            BigDecimal codCenCus = BigDecimal.ZERO;
            BigDecimal numNota = BigDecimal.ZERO;
            BigDecimal localPadrao = BigDecimal.ZERO;
            String necessarioLote = "";
            String unidadeMedida = "";
            String tipMov = "";
            String fechado = "";
            final String sql = buscaDados.modNota(maquina, numos);
            final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
            final ResultSet modNota = pstm.executeQuery();
            while (modNota.next()) {
                codEmp = modNota.getBigDecimal("CODEMP");
                codParc = modNota.getBigDecimal("CODPARC");
                codTipOper = modNota.getBigDecimal("CODTIPOPER");
                codTipVenda = modNota.getBigDecimal("CODTIPVENDA");
                codNat = modNota.getBigDecimal("CODNAT");
                codCenCus = modNota.getBigDecimal("CODCENCUS");
                numNota = modNota.getBigDecimal("NUMNOTA");
                localPadrao = modNota.getBigDecimal("LOCALPAD");
                unidadeMedida = modNota.getString("CODVOL");
                tipMov = new StringBuilder().append(modNota.getString("TIPMOV")).toString();
                fechado = new StringBuilder().append(modNota.getString("FECHAMENTO")).toString();
                parcDest = modNota.getBigDecimal("PARCDEST");
                necessarioLote = new StringBuilder().append(modNota.getString("NECESSARIOLOTE")).toString();
            }
            if (!necessarioLote.equals("N") && (lote.equals("null") || lote.isEmpty())) {
                arg0.mostraErro("Necessario informar o lote da ferramenta/produto!");
            }
            if (fechado.equalsIgnoreCase("S")) {
                arg0.mostraErro("Não pode ser feitas requisições em OS's fechadas!");
            }
            if (i < 1) {
                nuNota = saves.cabecalho(dwf, codEmp, codParc, numNota, codTipOper, codTipVenda, tipMov, codNat, codCenCus, parcDest);
            }
            sequencia = saves.item3(dwf, nuNota, maquina, unidadeMedida, qtd, localPadrao, localPadrao, new BigDecimal(i + 1), lote, necessarioLote, BigDecimal.ZERO, BigDecimal.ZERO);
            final String update = "UPDATE AD_MAQUINASUTILIZADAS SET NUMREQUISICAO = " + nuNota + " ,SEQUENCIA = " + sequencia + " , DTREQUISICAO = SYSDATE , CODUSUREQUISICAO = " + usu + " , STATUS = 'R' WHERE NUMOS = " + numos + " AND CODMAQUTILIZADA = " + seqmaquina + " ";
            final PreparedStatement pstm2 = jdbcWrapper.getPreparedStatement(update);
            pstm2.executeUpdate();
        }
        arg0.setMensagemRetorno(mensagemRetorno(nuNota));
        jdbcWrapper.closeSession();
    }
    
    public static String mensagemRetorno(final BigDecimal nunota) {
        final String id = "br.com.sankhya.com.mov.CentralNotas";
        final String mensagemSucesso = "Requisição gerada com sucesso!";
        final String pk = "{\"NUNOTA\"=\"" + nunota + "\"}";
        final String caminho = "/mge/system.jsp#app/";
        final String idBase64 = DatatypeConverter.printBase64Binary(id.getBytes());
        final String paransBase64 = DatatypeConverter.printBase64Binary(pk.toString().replaceAll("=", ":").getBytes());
        final String icone = "<p align=\"rigth\"><a href=\"" + caminho + idBase64 + "/" + paransBase64 + "\" target=\"_top\" >" + "<img src=\"http://imageshack.com/a/img923/7316/ux573F.png\" ><font size=\"20\" color=\"#008B45\"><b>" + nunota + "</b></font></a></p>";
        final String mensagemRetorno = icone + "<p align=\"left\">" + mensagemSucesso + "<br></p>";
        return mensagemRetorno;
    }
}
