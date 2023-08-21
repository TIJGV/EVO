package br.com.thinklife.unapel.os.consulta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import br.com.sankhya.jape.dao.JdbcWrapper;
import java.math.BigDecimal;

public class buscaDados
{
    public static String budcaChassis(final String chassis) {
        final String sql = "select CHASSIS from TGFVEI WHERE CHASSIS = '" + chassis + "'";
        return sql;
    }
    
    public static String buscaChassisIte(final BigDecimal nunota) {
        final String sql = "SELECT distinct\r\nA.SEQUENCIA,\r\na.nunota,\r\nA.AD_NROUNICOCAPPOTCILVEI,\r\nA.AD_NROUNICOCORVEI,\r\ncoalesce(A.CONTROLE,'null') AS CONTROLE,\r\n(SELECT Coalesce(VEI.CHASSIS,'null') FROM TGFVEI VEI WHERE VEI.CHASSIS = A.CONTROLE) AS CHASSIS_VEI,\r\nCOALESCE((SELECT NROUNICO FROM AD_MODELOVEI MARC INNER JOIN TGFPRO PRO ON PRO.AD_CODMODVEI = MARC.NROUNICO  WHERE PRO.CODPROD = A.CODPROD),0) AS MODELO,\r\nCOALESCE((SELECT CODIGO FROM TGFMAR MARC INNER JOIN TGFPRO PRO ON PRO.CODMARCA = MARC.CODIGO  WHERE PRO.CODPROD = A.CODPROD),0) AS MARCA,\r\nCOALESCE((SELECT NROUNICO FROM AD_ESPECIETIPOVEI ESP INNER JOIN TGFPRO PRO ON PRO.AD_NROUNICOESPECIETIPO = ESP.NROUNICO  WHERE PRO.CODPROD = A.CODPROD),0) AS ESPECIE_TIPO,\r\nCOALESCE((SELECT NROUNICO FROM AD_CATEGORIAVEI CAT INNER JOIN TGFPRO PRO ON PRO.AD_NROUNICOCATEGORIA = CAT.NROUNICO  WHERE PRO.CODPROD = A.CODPROD),0) AS CATEGORIA\r\nFROM TGFITE A\r\nINNER JOIN TGFCAB B ON A.NUNOTA = B.NUNOTA\r\nINNER JOIN TGFTOP C ON B.CODTIPOPER = C.CODTIPOPER\r\nWHERE A.SEQUENCIA > 0 \r\nAND B.CODTIPOPER IN(select a.codtipoper from tgftop a inner join\r\n(select max(dhalter) as dhalter,codtipoper from tgftop group by codtipoper)b\r\non a.codtipoper = b.codtipoper and a.dhalter = b.dhalter\r\nwhere AD_GERACADVEICULO = 'S') AND A.NUNOTA =" + nunota;
        return sql;
    }
    
    public static String buscaChassisPorVeiculo(final BigDecimal nunota) {
        final String sql = "SELECT \r\nA.SEQUENCIA,\r\nB.CODPROD,\r\nB.AD_CHASSI,\r\n(SELECT VEI.CHASSIS FROM TGFVEI VEI WHERE VEI.CHASSIS = B.AD_CHASSI) AS CHASSIS_VEI\r\nFROM TGFITE A\r\nINNER JOIN TGFPRO B ON A.CODPROD = B.CODPROD\r\nWHERE A.SEQUENCIA > 0 AND A.NUNOTA = " + nunota;
        return sql;
    }
    
    public static String modNota(final BigDecimal codProd, final BigDecimal numOs) {
        final String sql = "SELECT \r\n(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "(SELECT coalesce(FECHAMENTO,'N') FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS FECHAMENTO,\r\n" + "A.CODPARC AS CODPARC,\r\n" + "(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n" + "A.CODTIPOPER,\r\n" + "(SELECT CODPARCDEST FROM TGFCAB WHERE NUNOTA = A.NUNOTA) AS PARCDEST,\r\n" + "(SELECT (SELECT CAB.CODPARCDEST FROM TGFCAB CAB WHERE CAB.NUNOTA = OSE.NUNOTA) FROM AD_TCSOSE OSE WHERE OSE.NUMOS = " + numOs + ") AS PARCDEST,\r\n" + "A.CODTIPVENDA,\r\n" + "(SELECT\r\n" + "COALESCE(TIP.CODNAT,OSE.CODNAT,0)\r\n" + "FROM AD_TIPOORDEMSERVICO TIP\r\n" + "INNER JOIN AD_TCSOSE OSE ON TIP.CODTIPODEOS = OSE.CODTIPODEOS\r\n" + "WHERE OSE.NUMOS = " + numOs + ") AS CODNAT,\r\n" + "A.CODCENCUS,\r\n" + "A.NUMNOTA,\r\n" + "COALESCE((SELECT CODLOCALPAD FROM TGFPEM WHERE CODPROD = " + codProd + "),(SELECT EMP.LOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFEMP EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + ")) AS LOCALPAD,\r\n" + "(SELECT CODVOL FROM TGFPRO WHERE CODPROD = " + codProd + " ) AS CODVOL, \r\n" + "(SELECT TIPCONTEST FROM TGFPRO WHERE CODPROD = " + codProd + " ) AS NECESSARIOLOTE \r\n" + "FROM TGFCAB A\r\n" + "LEFT JOIN TGFEMP B ON A.CODEMP = B.CODEMP\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'MODREQMAQUINAS')";
        return sql;
    }
    
    public static ResultSet modNotaReqPeca(final JdbcWrapper jdbcWrapper, final BigDecimal numOs, final BigDecimal codUsu) throws Exception {
        final String sql = "SELECT \r\n(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "A.CODPARC AS CODPARC,\r\n" + "(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n" + "A.CODTIPOPER,\r\n" + "(SELECT CODPARCDEST FROM TGFCAB WHERE NUNOTA = A.NUNOTA) AS PARCDEST,\r\n" + "A.CODTIPVENDA,\r\n" + "(SELECT CODVEND FROM TSIUSU WHERE CODUSU =" + codUsu + ") AS VENDEDOR ,\r\n" + "(SELECT\r\n" + "COALESCE(TIP.CODNAT,OSE.CODNAT,0)\r\n" + "FROM AD_TIPOORDEMSERVICO TIP\r\n" + "INNER JOIN AD_TCSOSE OSE ON TIP.CODTIPODEOS = OSE.CODTIPODEOS\r\n" + "WHERE OSE.NUMOS = " + numOs + ") AS CODNAT,\r\n" + "A.CODCENCUS,\r\n" + "A.NUMNOTA\r\n" + "FROM TGFCAB A\r\n" + "LEFT JOIN TGFEMP B ON A.CODEMP = B.CODEMP\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'MODNOTAREQPECAS')";
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }
    
    public static ResultSet verificacaoPeca(final JdbcWrapper jdbcWrapper, final BigDecimal codProd, final BigDecimal numOs) throws Exception {
        final String sql = "SELECT \r\n(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "(SELECT coalesce(FECHAMENTO,'N') FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS FECHAMENTO,\r\n" + "A.CODPARC AS CODPARC,\r\n" + "(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n" + "A.CODTIPOPER,\r\n" + "(SELECT CODPARCDEST FROM TGFCAB WHERE NUNOTA = A.NUNOTA) AS PARCDEST,\r\n" + "(SELECT (SELECT CAB.CODPARCDEST FROM TGFCAB CAB WHERE CAB.NUNOTA = OSE.NUNOTA) FROM AD_TCSOSE OSE WHERE OSE.NUMOS = " + numOs + ") AS PARCDEST,\r\n" + "A.CODTIPVENDA,\r\n" + "A.CODNAT,\r\n" + "A.CODCENCUS,\r\n" + "A.NUMNOTA,\r\n" + "COALESCE((SELECT CODLOCALPAD FROM TGFPEM WHERE CODPROD = " + codProd + "),(SELECT EMP.LOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFEMP EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + ")) AS LOCALPAD,\r\n" + "(SELECT CODVOL FROM TGFPRO WHERE CODPROD = " + codProd + " ) AS CODVOL, \r\n" + "(SELECT TIPCONTEST FROM TGFPRO WHERE CODPROD = " + codProd + " ) AS NECESSARIOLOTE \r\n" + "FROM TGFCAB A\r\n" + "LEFT JOIN TGFEMP B ON A.CODEMP = B.CODEMP\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'MODNOTAREQPECAS')";
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }
    
    public static void updatePecaUtilizada(final JdbcWrapper jdbcWrapper, final BigDecimal nuNota, final BigDecimal sequencia, final BigDecimal numos, final BigDecimal sequenciaPeca) throws Exception {
        final String update = "UPDATE AD_TCSPRO SET NUMREQUISICAO = " + nuNota + ", SEQREQUISICAO =" + sequencia + ", STATUS = 'R' WHERE NUMOS = " + numos + " AND SEQUENCIA = " + sequenciaPeca + " ";
        final PreparedStatement pstm2 = jdbcWrapper.getPreparedStatement(update);
        pstm2.executeUpdate();
    }
    
    public static void updateVlrNOTA(final JdbcWrapper jdbcWrapper, final BigDecimal nuNota, final BigDecimal vlrNota) throws Exception {
        final String update = "UPDATE TGFCAB SET VLRNOTA = " + vlrNota + " WHERE NUNOTA = " + nuNota;
        final PreparedStatement pstm2 = jdbcWrapper.getPreparedStatement(update);
        pstm2.executeUpdate();
    }
    
    public static String devolucaoPeca(final BigDecimal numOs, final BigDecimal seqPeca) {
        final String sql = "SELECT\r\nA.CODEMP,\r\ncoalesce(A.FECHAMENTO,'null') as FECHAMENTO,\r\nA.TIPOFATURAMENTO,\r\nB.QUANTIDADE,\r\n(SELECT PRO.CODVOL FROM TGFPRO PRO WHERE PRO.CODPROD = B.CODPROD) AS CODVOL,\r\n(SELECT PRO.CODPARCFORN FROM TGFPRO PRO WHERE PRO.CODPROD = B.CODPROD) AS CODFAB,\r\n(SELECT CODPARCDEST FROM TGFCAB WHERE NUNOTA = A.NUNOTA) AS PARCDEST,\r\n(SELECT PAR.CODLOCALPADRAO FROM TGFPRO PRO INNER JOIN TGFPAR PAR ON PRO.CODPARCFORN = PAR.CODPARC WHERE PRO.CODPROD = B.CODPROD) AS LOCALPADRAOFAB\r\nFROM AD_TCSOSE A\r\nINNER JOIN AD_TCSPRO B ON A.NUMOS = B.NUMOS\r\nWHERE B.NUMOS = " + numOs + " AND B.SEQUENCIA = " + seqPeca + " ";
        return sql;
    }
    
    public static String devolucaoPeca2(final BigDecimal numOs, final BigDecimal seqPeca) {
        final String sql = "SELECT\r\nA.CODEMP,\r\nB.CODPRODUTILIZADO,\r\nB.QUANTIDADE,\r\n(SELECT PRO.CODVOL FROM TGFPRO PRO WHERE PRO.CODPROD = B.CODPRODUTILIZADO) AS CODVOL,\r\nB.CODPARC AS CODFAB,\r\n(SELECT PAR.CODLOCALPADRAO FROM TGFPAR PAR WHERE PAR.CODPARC = B.CODPARC) AS LOCALPADRAOFAB\r\nFROM AD_TCSOSE A\r\nINNER JOIN AD_TCSPRO B ON A.NUMOS = B.NUMOS\r\nWHERE B.NUMOS = " + numOs + " AND B.SEQUENCIA = " + seqPeca + " ";
        return sql;
    }
    
    public static String modNotaGarantiaPeca(final BigDecimal codProd, final BigDecimal numOs) {
        final String sql = "SELECT \r\nA.CODTIPOPER,\r\n(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\nA.CODTIPVENDA,\r\nA.CODNAT,\r\nA.CODCENCUS,\r\nA.NUMNOTA,\r\n(SELECT EMP.LOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFEMP EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + " ) AS LOCALPAD,\r\n" + "(SELECT CODVOL FROM TGFPRO WHERE CODPROD = " + codProd + " ) AS CODVOL \r\n" + "FROM TGFCAB A\r\n" + "LEFT JOIN TGFEMP B ON A.CODEMP = B.CODEMP\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'MODNOTAGARANTIA')";
        return sql;
    }
    
    public static String modNotaGarantiaOS(final BigDecimal numOs) {
        final String sql = "SELECT \r\n(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "(SELECT CODPARC FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODPARC,\r\n" + "(SELECT NUNOTA FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS NOTAORIGEM,\r\n" + "A.CODTIPOPER,\r\n" + "A.CODTIPVENDA,\r\n" + "A.CODNAT,\r\n" + "A.CODCENCUS,\r\n" + "A.NUMNOTA,\r\n" + "(SELECT EMP.LOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFEMP EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + " ) AS LOCALPAD\r\n" + "FROM TGFCAB A\r\n" + "LEFT JOIN TGFEMP B ON A.CODEMP = B.CODEMP\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'MODNOTAGARSERVI')";
        return sql;
    }
    
    public static String geraSeguro(final BigDecimal nunota, final BigDecimal codtipoper) {
        final String sql = "SELECT \r\ncoalesce(AD_GERASEGURO,'N') as AD_GERASEGURO,\r\nAD_NROAPOLICE,\r\nAD_PARCSEGURADORA,\r\nAD_QDESINISTROS,\r\nAD_DTVIGENCIA,\r\nAD_CODVEICULO,\r\nAD_NROPROPOSTA,\r\nAD_DTVIGENCIAFIM,\r\nCOALESCE(AD_GERASEGURO,'null') AS TOPGERASEGURO\r\nFROM TGFITE\r\nWHERE NUNOTA = " + nunota;
        return sql;
    }
    
    public static String devolucaoMqpUtilizada(final BigDecimal nunota) {
        final String sql = "SELECT \r\nOSE.NUMOS,\r\nUTI.CODMAQUTILIZADA,\r\nUTI.NUMREQUISICAO,\r\n(SELECT VAR.NUNOTA FROM TGFVAR VAR WHERE VAR.NUNOTAORIG = UTI.NUMREQUISICAO AND VAR.SEQUENCIAORIG = UTI.SEQUENCIA) AS NOTAENTREGA,\r\n(SELECT distinct VAR.NUNOTA FROM TGFVAR VAR WHERE VAR.NUNOTAORIG = (SELECT VAR.NUNOTA FROM TGFVAR VAR WHERE VAR.NUNOTAORIG = UTI.NUMREQUISICAO AND VAR.SEQUENCIAORIG = UTI.SEQUENCIA) ) AS NOTADEVOLUCAO,\r\nCOALESCE((SELECT distinct VAR.STATUSNOTA FROM TGFVAR VAR WHERE VAR.NUNOTAORIG = (SELECT VAR.NUNOTA FROM TGFVAR VAR WHERE VAR.NUNOTAORIG = UTI.NUMREQUISICAO AND UTI.SEQUENCIA = VAR.SEQUENCIAORIG) ),'null') AS NOTADEVOLUCAO_STATUS\r\nFROM AD_TCSOSE OSE\r\nINNER JOIN AD_MAQUINASUTILIZADAS UTI ON OSE.NUMOS = UTI.NUMOS\r\nWHERE OSE.NUMOS = " + nunota;
        return sql;
    }
    
    public static String consultaTCSPRO(final BigDecimal numos) {
        final String sql = "select \r\nCODPROD,\r\n(SELECT PRO.TIPCONTEST FROM TGFPRO PRO WHERE PRO.CODPROD = TCS.CODPROD ) AS NECESSARIOLOTE, \r\nNUMOS,\r\nCODVOL,\r\nCOALESCE(QUANTIDADE,0)- COALESCE(QTDDEVOLVIDA,0)AS QUANTIDADE,\r\ncoalesce(NUNOTAPED,0) AS NUNOTAPED,\r\nSEQUENCIA,\r\nCOALESCE(NUNOTA,0) as NUNOTA2,\r\n(COALESCE(VLRUNIT,0)*(COALESCE(QUANTIDADE,0)-COALESCE(QTDDEVOLVIDA,0))) as VLRTOTAL,\r\nCOALESCE(VLRUNIT,0) AS VLRUNIT,\r\nTIPOMOVIMENTO,\r\n(SELECT \r\nITE2.CODLOCALORIG\r\nFROM TGFITE ITE \r\nINNER JOIN TGFVAR VAR ON ITE.NUNOTA = VAR.NUNOTAORIG  AND ITE.SEQUENCIA = VAR.SEQUENCIAORIG\r\nINNER JOIN TGFITE ITE2 ON VAR.NUNOTA = ITE2.NUNOTA AND VAR.SEQUENCIA = (ITE2.SEQUENCIA*-1)\r\nWHERE ITE.NUNOTA = TCS.NUMREQUISICAO AND ITE.SEQUENCIA = TCS.SEQREQUISICAO ) AS LOCAL_DEST,\r\ncoalesce(LOTE,'null') as LOTE \r\nfrom AD_TCSPRO TCS \r\nwhere NUNOTAPED is null AND STATUS IN ('E','DP') and NUMOS= " + numos + "order by TIPOMOVIMENTO asc";
        return sql;
    }
    
    public static String modNotaPecasUtilizadas(final BigDecimal numOs, final BigDecimal nunotaModelo, final BigDecimal codProd, final BigDecimal codUsu) {
        final String sql = "SELECT \r\n(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "(SELECT CODTIPODEOS FROM AD_TCSOSE WHERE NUMOS =" + numOs + " ) AS TIPOOS,\r\n" + "(SELECT CAB.CODPARC FROM TGFCAB CAB INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA =OSE.NUNOTA WHERE OSE.NUMOS =" + numOs + " ) AS CODPARC,\r\n" + "(SELECT CAB.CODPARCDEST FROM TGFCAB CAB INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA =OSE.NUNOTA WHERE OSE.NUMOS =" + numOs + " ) AS CODPARCDEST,\r\n" + "(SELECT EMP.CODLOCALPADRAO FROM AD_TCSOSE OSE INNER JOIN TGFPAR EMP ON OSE.CODPARC = EMP.CODPARC WHERE NUMOS = " + numOs + " ) AS LOCALPADCLIENTE,\r\n" + "(SELECT B.CODTIPOPERDESTPROD FROM AD_TCSOSE A INNER JOIN AD_TIPOORDEMSERVICO B ON A.CODTIPODEOS = B.CODTIPODEOS WHERE NUMOS = " + numOs + ") AS CODTIPOPER ,\r\n" + "(SELECT CODVEND FROM TSIUSU WHERE CODUSU =" + codUsu + ") AS VENDEDOR ,\r\n" + "(SELECT\r\n" + "CAB.AD_CODOAT\r\n" + "FROM TGFCAB CAB \r\n" + "INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA = OSE.NUNOTA\r\n" + "WHERE  OSE.NUMOS =" + numOs + ") AS LOCAL_VENDA ,\r\n" + "COALESCE((SELECT AD_CODCENCUS FROM TSIEMP WHERE CODEMP = (SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + ")),0) AS CODCENCUS,\r\n" + "(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n" + "A.CODTIPVENDA,\r\n" + "(SELECT\r\n" + "COALESCE(TIP.CODNAT,OSE.CODNAT,0)\r\n" + "FROM AD_TIPOORDEMSERVICO TIP\r\n" + "INNER JOIN AD_TCSOSE OSE ON TIP.CODTIPODEOS = OSE.CODTIPODEOS\r\n" + "WHERE OSE.NUMOS = " + numOs + ") AS CODNAT,\r\n" + "A.NUMNOTA,\r\n" + "SYSDATE AS DTNEG,\r\n" + "(SELECT EMP.CODLOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFPEM EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + " AND EMP.CODPROD = " + codProd + " ) AS LOCALPAD \r\n" + "FROM TGFCAB A\r\n" + "WHERE NUNOTA = " + nunotaModelo;
        return sql;
    }
    
    public static String consultaTGFVAR(final BigDecimal nunota) {
        final String sql = "SELECT\r\nCAB.STATUSNOTA\r\nFROM TGFCAB CAB\r\nWHERE CAB.NUNOTA= " + nunota;
        return sql;
    }
    
    public static String modNotaServico(final BigDecimal numOs, final BigDecimal codUsu) {
        final String sql = "SELECT \r\n(SELECT CODTIPODEOS FROM AD_TCSOSE WHERE NUMOS =" + numOs + " ) AS TIPOOS,\r\n" + "(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "(SELECT CAB.CODPARC FROM TGFCAB CAB INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA =OSE.NUNOTA WHERE OSE.NUMOS =" + numOs + " ) AS CODPARC,\r\n" + "(SELECT CAB.CODPARCDEST FROM TGFCAB CAB INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA =OSE.NUNOTA WHERE OSE.NUMOS =" + numOs + " ) AS CODPARCDEST,\r\n" + "(SELECT EMP.CODLOCALPADRAO FROM AD_TCSOSE OSE INNER JOIN TGFPAR EMP ON OSE.CODPARC = EMP.CODPARC WHERE NUMOS = " + numOs + " ) AS LOCALPADCLIENTE,\r\n" + "(SELECT B.CODTIPOPERDESTFAT FROM AD_TCSOSE A INNER JOIN AD_TIPOORDEMSERVICO B ON A.CODTIPODEOS = B.CODTIPODEOS WHERE NUMOS = " + numOs + ") AS CODTIPOPER ,\r\n" + "(SELECT CAB.AD_CODOAT FROM TGFCAB CAB  INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA = OSE.NUNOTA WHERE  OSE.NUMOS =" + numOs + ") AS LOCAL_VENDA ,\r\n" + "(SELECT CODVEND FROM TSIUSU WHERE CODUSU =" + codUsu + ") AS VENDEDOR ,\r\n" + "(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n" + "A.CODTIPVENDA,\r\n" + "(SELECT\r\n" + "COALESCE(TIP.CODNAT,OSE.CODNAT,0)\r\n" + "FROM AD_TIPOORDEMSERVICO TIP\r\n" + "INNER JOIN AD_TCSOSE OSE ON TIP.CODTIPODEOS = OSE.CODTIPODEOS\r\n" + "WHERE OSE.NUMOS = " + numOs + ") AS CODNAT,\r\n" + "(SELECT CODCENCUS FROM AD_TCSOSE WHERE NUMOS = " + numOs + ") AS CODCENCUS,\r\n" + "A.NUMNOTA,\r\n" + "SYSDATE AS DTNEG,\r\n" + "(SELECT EMP.LOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFEMP EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + " ) AS LOCALPAD \r\n" + "FROM TGFCAB A\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'PV_OS_SERVICO')";
        return sql;
    }
    
    public static String consultaSQL(final BigDecimal numos) {
        final String sql = "select \r\na.codserv,\r\n(SELECT COALESCE(OS.GARANTIA,'N') FROM AD_TCSOSE OSE INNER JOIN AD_TIPOORDEMSERVICO OS ON OSE.CODTIPODEOS = OS.CODTIPODEOS WHERE OSE.NUMOS = " + numos + ") AS GARANTIA,\r\n" + "                \r\n" + "COALESCE((select COALESCE(pro.AD_TEMPPADRAO,0)/100 from tgfpro pro where pro.codprod = a.codserv),0) AS TEMPOPADRAO,\r\n" + "        \r\n" + "(TRUNC((SELECT\r\n" + "((TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HRFINAL,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HRFINAL,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')-\r\n" + "TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HRINICIAL,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HRINICIAL,0),4,0),3,4)||':00','dd/mm/yyyy HH24:MI:SS'))*1440)/60 AS MINUTOS\r\n" + "FROM AD_TCSITE ITE\r\n" + "WHERE ITE.NUMOS =A.NUMOS AND ITE.NUMITEM = A.NUMITEM),2)) AS HSTRABALHADAS,\r\n" + "                \r\n" + "\r\n" + "COALESCE(a.valorservico, (SELECT PRO.AD_VLRPADRAO FROM TGFPRO PRO WHERE PRO.CODPROD = a.codserv)) AS valorservico,\r\n" + "a.NUMITEM,\r\n" + "COALESCE(a.AD_NROPEDVENDA,0) as AD_NROPEDVENDA,\r\n" + "(select codvol from tgfpro where codprod=a.codserv) as codvol\r\n" + "from ad_tcsite a\r\n" + "where A.numos= " + numos + " and A.AD_NROPEDVENDA is null AND (SELECT AD_GERCOB FROM TSIUSU USU WHERE a.CODUSURESP = USU.CODUSU) = 'S'  \r\n" + "AND (SELECT COALESCE(AD_FATURASERVICO,'N') \r\n" + "    FROM TGFPRO PRO\r\n" + "    INNER JOIN TGFGRU GRU ON PRO.CODGRUPOPROD = GRU.CODGRUPOPROD\r\n" + "    WHERE PRO.CODPROD = A.codserv) = 'N'";
        return sql;
    }
    
    public static String consultaSQLBackup(final BigDecimal numos) {
        final String sql = "select \r\na.codserv,\r\n(SELECT COALESCE(OS.GARANTIA,'N') FROM AD_TCSOSE OSE INNER JOIN AD_TIPOORDEMSERVICO OS ON OSE.CODTIPODEOS = OS.CODTIPODEOS WHERE OSE.NUMOS = " + numos + ") AS GARANTIA,\r\n" + "COALESCE((select COALESCE(pro.AD_TEMPPADRAO,0)/100 from tgfpro pro where pro.codprod = a.codserv),0) AS TEMPOPADRAO,\r\n" + "COALESCE(a.hstrabalhadas,0) AS HSTRABALHADAS,\r\n" + "a.valorservico,\r\n" + "a.NUMITEM,\r\n" + "COALESCE(a.AD_NROPEDVENDA,0) as AD_NROPEDVENDA,\r\n" + "(select codvol from tgfpro where codprod=a.codserv) as codvol\r\n" + "from ad_tcsite a\r\n" + "where A.numos= " + numos + " and A.AD_NROPEDVENDA is null ";
        return sql;
    }
    
    public static String consultaTGFVAR(final BigDecimal numos, final BigDecimal nunota) {
        final String sql = "SELECT\r\n(SELECT NUNOTA FROM AD_TCSOSE WHERE NUMOS= " + numos + " ) AS NUNOTAORIG,\r\n" + "(SELECT SEQUENCIA FROM TGFITE ITE1 INNER JOIN AD_TCSOSE OSE1 ON ITE1.NUNOTA=OSE1.NUNOTA \r\n" + "WHERE NUMOS= " + numos + " AND SEQUENCIA=ITE.SEQUENCIA) AS SEQORIGEM,\r\n" + "CAB.STATUSNOTA\r\n" + "FROM TGFCAB CAB\r\n" + "INNER JOIN TGFITE ITE ON CAB.NUNOTA=ITE.NUNOTA\r\n" + "WHERE CAB.NUNOTA= " + nunota;
        return sql;
    }
    
    public static String modNotaItem(final BigDecimal numOs) {
        final String sql = "SELECT \r\n(SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODEMP,\r\n" + "(SELECT CODPARC FROM AD_TCSOSE WHERE NUMOS = " + numOs + " ) AS CODPARC,\r\n" + "(SELECT EMP.CODLOCALPADRAO FROM AD_TCSOSE OSE INNER JOIN TGFPAR EMP ON OSE.CODPARC = EMP.CODPARC WHERE NUMOS = " + numOs + " ) AS LOCALPADCLIENTE,\r\n" + "A.CODTIPOPER,\r\n" + "(SELECT TIPMOV FROM TGFTOP TOP WHERE TOP.CODTIPOPER = A.CODTIPOPER AND TOP.DHALTER = (SELECT MAX(DHALTER)AS DHALTER FROM TGFTOP TOP2 WHERE TOP2.CODTIPOPER = TOP.CODTIPOPER )) AS TIPMOV,\r\n" + "A.CODTIPVENDA,\r\n" + "A.CODNAT,\r\n" + "A.CODCENCUS,\r\n" + "A.NUMNOTA,\r\n" + "SYSDATE AS DTNEG,\r\n" + "(SELECT EMP.LOCALPAD FROM AD_TCSOSE OSE INNER JOIN TGFEMP EMP ON OSE.CODEMP = EMP.CODEMP WHERE NUMOS = " + numOs + " ) AS LOCALPAD \r\n" + "FROM TGFCAB A\r\n" + "WHERE NUNOTA = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'PV_OS_ITEM')";
        return sql;
    }
    
    public static String consultaPedidoItem(final BigDecimal numos) {
        final String sql = "select \r\ncodprod,\r\nsequencia,\r\ncodvol,\r\nquantidade,\r\nvlrunit*quantidade as valortotal,\r\nlote\r\nfrom ad_tcspro\r\nwhere numos= " + numos;
        return sql;
    }
    
    public static String consultaTGFVAR2(final BigDecimal numos, final BigDecimal nunota) {
        final String sql = "SELECT\r\n(SELECT NUNOTA FROM AD_TCSOSE WHERE NUMOS= " + numos + " ) AS NUNOTAORIG,\r\n" + "(SELECT SEQUENCIA FROM TGFITE ITE1 INNER JOIN AD_TCSOSE OSE1 ON ITE1.NUNOTA=OSE1.NUNOTA \r\n" + "WHERE NUMOS= " + numos + " AND SEQUENCIA=ITE.SEQUENCIA) AS SEQORIGEM,\r\n" + "CAB.STATUSNOTA\r\n" + "FROM TGFCAB CAB\r\n" + "INNER JOIN TGFITE ITE ON CAB.NUNOTA=ITE.NUNOTA\r\n" + "WHERE CAB.NUNOTA= " + nunota;
        return sql;
    }
    
    public static String consultaOrdemServico(final BigDecimal nunota) {
        final String query = "SELECT \r\nSYSDATE,\r\nA.CODEMP,\r\nA.CODTIPOPER,\r\nA.DTNEG,\r\nA.OBSERVACAO,\r\nA.CODPARC,\r\nA.AD_CODUSUEXEC MECANICO,\r\nA.AD_CODTIPODEOS,\r\nA.AD_CODUSUEXEC,\r\nCOALESCE((select tip.GARANTIA from AD_TIPOORDEMSERVICO tip where tip.CODTIPODEOS = A.AD_CODTIPODEOS),'N') as GARANTIA,\r\nCOALESCE(A.CODPARCDEST,A.CODPARC) as CODPARCDEST,\r\nA.CODPROJ,\r\nA.CODVEICULO,\r\nA.STATUSNOTA,\r\n(select TIPOS.CODATENDIMENTO from AD_TIPOORDEMSERVICO TIPOS WHERE TIPOS.CODTIPODEOS = A.AD_CODTIPODEOS) AS CODATENDIMENTO,\r\n(select TIPOS.CODFATUR from AD_TIPOORDEMSERVICO TIPOS WHERE TIPOS.CODTIPODEOS = A.AD_CODTIPODEOS) AS CODFATUR,\r\nA.CODCENCUS AS CODCENCUS,\r\nCOALESCE((select TIPOS.CODNAT from AD_TIPOORDEMSERVICO TIPOS WHERE TIPOS.CODTIPODEOS = A.AD_CODTIPODEOS),A.CODNAT) AS CODNAT,\r\nA.AD_HORIMETRO AS HORIMETRO\r\nFROM TGFCAB A\r\nLEFT JOIN TGFVEI B ON A.CODVEICULO=B.CODVEICULO\r\nLEFT JOIN AD_TIPOORDEMSERVICO C ON A.AD_CODTIPODEOS=C.CODTIPODEOS\r\nWHERE\r\nA.NUNOTA=" + nunota + " AND\r\n" + "A.CODTIPOPER IN(select a.codtipoper from tgftop a inner join\r\n" + "(select max(dhalter) as dhalter,codtipoper from tgftop group by codtipoper)B\r\n" + "on a.codtipoper = b.codtipoper and a.dhalter = b.dhalter\r\n" + "where AD_AUTGERARORDEMSERVICO = 'S')";
        return query;
    }
    
    public static String consultaTCSNOTAS(final BigDecimal nunota) {
        final String query = "select \r\na.numos,\r\na.nunota,\r\nb.dtneg,\r\nb.codtipoper,\r\nb.codparc\r\n\r\nfrom ad_tcsose a\r\nleft join tgfcab b on a.nunota=b.nunota\r\n\r\nwhere a.nunota= " + nunota;
        return query;
    }
    
    public static String consultaTCSITE(final BigDecimal numosNovo) {
        final String query = "select \r\nb.codprod,\r\na.dtalter as data,\r\nb.vlrtot as valorservico,\r\na.codemp\r\nfrom tgfcab a \r\ninner join tgfite b on a.nunota=b.nunota\r\nleft join tgfpro c on b.codprod=c.codprod\r\nleft join ad_tcsose d on a.nunota=d.nunota\r\nwhere c.CODGRUPOPROD in (select CODGRUPOPROD from  tgfgru where AD_SERVICOOFICINA ='S') and d.numos= " + numosNovo;
        return query;
    }
    
    public static String consultaTCSPRO2(final BigDecimal numosNovo) {
        final String query = "select \r\nb.codprod,\r\nb.vlrunit,\r\nCOALESCE(b.CONTROLE,'null') as CONTROLE,\r\nb.qtdneg as quantidade\r\nfrom tgfcab a \r\ninner join tgfite b on a.nunota=b.nunota\r\nleft join tgfpro c on b.codprod=c.codprod\r\nleft join ad_tcsose d on a.nunota=d.nunota\r\nwhere c.CODGRUPOPROD in (select CODGRUPOPROD from  tgfgru where AD_PECASUTILIZADASOFICINA ='S') AND a.nunota= " + numosNovo;
        return query;
    }
    
    public static String consultaParqueMaq(final BigDecimal nunota) {
        final String query = "SELECT \r\nCASE WHEN A.ATUALESTOQUE=-1 THEN 2 ELSE 1 END AS STATUS, \r\nA.NUNOTA, \r\nsysdate as DTALTERACAO, \r\nA.CONTROLE, \r\nCASE WHEN (select ad_tipparcparqmaq from tgftop a inner join \r\n(select max(dhalter) as dhalter,codtipoper from tgftop group by codtipoper)b \r\non a.codtipoper = b.codtipoper and a.dhalter = b.dhalter \r\nwhere AD_AUTPARQMAQUINAS = 'S' and A.CODTIPOPER=CAB.CODTIPOPER)=1 THEN E.CODPARC \r\nELSE CAB.CODPARC END AS CODPARC, \r\nCOALESCE((select distinct CASE WHEN status=1 THEN '1' WHEN STATUS=2 THEN '2' ELSE '0' END AS VALIDACAO from ad_parquedemaquinas  \r\nwhere dtalteracao=(select max(dtalteracao) from ad_parquedemaquinas  \r\nwhere nrochassis=(select controle from tgfite ite where nunota=" + nunota + " and a.SEQUENCIA = ite.SEQUENCIA))),'0') AS VALIDACAO, \r\n" + "D.NUMNOTA AS NOTAORIGEM, \r\n" + "A.CODPROD, \r\n" + "C.TIPOVEICULO AS TIPO \r\n" + "FROM TGFITE A \r\n" + "LEFT JOIN TGFCAB CAB ON A.NUNOTA=CAB.NUNOTA \r\n" + "LEFT JOIN TGFVEI C ON A.CODPROD=C.CODPROD \r\n" + "LEFT JOIN TGFFIN D ON A.NUNOTA=D.NUNOTA \r\n" + "LEFT JOIN TSIEMP E ON A.CODEMP=E.CODEMP \r\n" + "WHERE \r\n" + "A.NUNOTA=" + nunota + " AND \r\n" + "CAB.CODTIPOPER IN(select a.codtipoper from tgftop a inner join \r\n" + "(select max(dhalter) as dhalter,codtipoper from tgftop group by codtipoper)b \r\n" + "on a.codtipoper = b.codtipoper and a.dhalter = b.dhalter \r\n" + "where AD_AUTPARQMAQUINAS = 'S')";
        return query;
    }
}
