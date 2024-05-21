package br.com.thinklife.unapel.os.consulta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import java.math.BigDecimal;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class buscaDadosStatusPeca
{
    public static ResultSet VerificarPecaDevolvida(final JdbcWrapper jdbcWrapper, final BigDecimal nunota) throws Exception {
        final String sql = "SELECT ITE.CODPROD,\r\nITE3.NUNOTA,\r\nITE3.SEQUENCIA,\r\nITE3.QTDNEG AS QTD_SOLICITADA,\r\nCAST(ITE.QTDNEG + (SELECT NVL(QTDDEVOLVIDA, 0) FROM AD_TCSPRO WHERE CODPROD = ITE.CODPROD AND NUMOS = (SELECT NUMOS FROM AD_TCSPRO WHERE NUMREQUISICAO = ITE3.NUNOTA AND ROWNUM = 1) AND NUMREQUISICAO = ITE3.NUNOTA AND ROWNUM = 1) AS NUMBER(10,2)) AS QTD_DEVOLVIDA,\r\n(CASE WHEN CAST(ITE.QTDNEG + (SELECT NVL(QTDDEVOLVIDA, 0) FROM AD_TCSPRO WHERE CODPROD = ITE.CODPROD AND NUMOS = (SELECT NUMOS FROM AD_TCSPRO WHERE NUMREQUISICAO = ITE3.NUNOTA AND ROWNUM = 1) AND NUMREQUISICAO = ITE3.NUNOTA AND ROWNUM = 1) AS NUMBER(10,2)) < ITE3.QTDNEG THEN 'DP' ELSE 'D' END) AS STATUS\r\n\r\nFROM TGFITE ITE\r\nINNER JOIN TGFCAB CAB ON ITE.NUNOTA = CAB.NUNOTA\r\nINNER JOIN TGFVAR VAR ON ITE.NUNOTA =VAR.NUNOTA AND ITE.SEQUENCIA = VAR.SEQUENCIA \r\nINNER JOIN TGFITE ITE2 ON VAR.NUNOTAORIG = ITE2.NUNOTA AND VAR.SEQUENCIAORIG = ITE2.SEQUENCIA\r\nINNER JOIN TGFCAB CAB2 ON ITE2.NUNOTA = CAB2.NUNOTA\r\nINNER JOIN TGFVAR VAR2 ON ITE2.NUNOTA =VAR2.NUNOTA AND ITE2.SEQUENCIA = VAR2.SEQUENCIA \r\nINNER JOIN TGFITE ITE3 ON VAR2.NUNOTAORIG = ITE3.NUNOTA AND VAR2.SEQUENCIAORIG = ITE3.SEQUENCIA\r\nINNER JOIN TGFCAB CAB3 ON ITE3.NUNOTA = CAB3.NUNOTA\r\nWHERE ITE.NUNOTA = " + nunota;
        System.out.println("SQL: "+sql);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }
    
    public static ResultSet VerificarPecaDevolvidaBkp(final JdbcWrapper jdbcWrapper, final BigDecimal nunota) throws Exception {
        final String sql = "SELECT \r\nD.NUNOTA,\r\nD.SEQUENCIA\r\nFROM TGFCAB A\r\nINNER JOIN TGFITE B ON A.NUNOTA = B.NUNOTA\r\nINNER JOIN TGFVAR C ON A.NUNOTA = C.NUNOTA AND B.SEQUENCIA = C.SEQUENCIA\r\nINNER JOIN TGFITE D ON C.NUNOTAORIG = D.NUNOTA AND C.SEQUENCIAORIG = D.SEQUENCIA\r\nWHERE A.CODTIPOPER = 600 AND A.NUNOTA =" + nunota;
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }
    
    public static void updateStatusPecaDevolvido(final JdbcWrapper jdbcWrapper, final BigDecimal nunota, final BigDecimal sequencia, final String status, final BigDecimal qtdDevolvida) throws Exception {
        final String update = "UPDATE AD_TCSPRO SET STATUS = '" + status + "', QTDDEVOLVIDA = " + qtdDevolvida + " WHERE NUMREQUISICAO = " + nunota + " AND SEQREQUISICAO =" + sequencia;
        System.out.println("UPD: "+update);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(update);
        pstm.executeUpdate();
    }
    
    public static ResultSet VerificarPecaEntregue(final JdbcWrapper jdbcWrapper, final BigDecimal nunota) throws Exception {
        final String sql = "SELECT ITE.CODPROD,\r\nITE2.NUNOTA,\r\nITE2.SEQUENCIA\r\nFROM TGFITE ITE\r\nINNER JOIN TGFVAR VAR ON VAR.NUNOTA = ITE.NUNOTA AND VAR.SEQUENCIA = ITE.SEQUENCIA\r\nINNER JOIN TGFITE ITE2 ON ITE2.NUNOTA = VAR.NUNOTAORIG  AND ITE2.SEQUENCIA = VAR.SEQUENCIAORIG\r\nINNER JOIN AD_TCSPRO PRO ON ITE2.NUNOTA = PRO.NUMREQUISICAO AND ITE2.SEQUENCIA = PRO.SEQREQUISICAO\r\nWHERE ITE.NUNOTA =" + nunota;
        System.out.println("SQL: "+sql);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }
    
    public static ResultSet VerificarPecaEntregueBkp(final JdbcWrapper jdbcWrapper, final BigDecimal nunota) throws Exception {
        final String sql = "SELECT \r\nA.NUNOTA,\r\nA.SEQUENCIA\r\nFROM TGFITE A\r\nWHERE NUNOTA = " + nunota;
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }
    
    public static void updateStatusPecaEntregue(final JdbcWrapper jdbcWrapper, final BigDecimal nunota, final BigDecimal sequencia) throws Exception {
        final String update = "UPDATE AD_TCSPRO SET STATUS = 'E' WHERE NUMREQUISICAO = " + nunota + " AND SEQREQUISICAO =" + sequencia;
        System.out.println("UPD: "+update);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(update);
        pstm.executeUpdate();
    }
    
    public static ResultSet VerificarStatusPeca(final JdbcWrapper jdbcWrapper, final BigDecimal numos) throws Exception {
        final String sql = "SELECT \r\nSTATUS\r\nFROM AD_TCSPRO\r\nWHERE NUMOS = " + numos;
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }

	public static void updateStatusFerramentaDevolvida(final JdbcWrapper jdbcWrapper, final BigDecimal nunota, final BigDecimal sequencia, final String status, final BigDecimal qtdDevolvida) throws Exception {
        final String update = "UPDATE AD_MAQUINASUTILIZADAS SET STATUS = '" + status + "' WHERE NUMREQUISICAO = " + nunota + " AND SEQUENCIA =" + sequencia;
        System.out.println("UPD: "+update);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(update);
        pstm.executeUpdate();
    }

	public static void updateStatusFerramentaEntregue(final JdbcWrapper jdbcWrapper, final BigDecimal nunota, final BigDecimal sequencia) throws Exception {
        final String update = "UPDATE AD_MAQUINASUTILIZADAS SET STATUS = 'E' WHERE NUMREQUISICAO = " + nunota + " AND SEQUENCIA =" + sequencia;
        System.out.println("UPD: "+update);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(update);
        pstm.executeUpdate();
    }

	public static ResultSet VerificarFerramentaDevolvida(JdbcWrapper jdbcWrapper, BigDecimal nunota) throws Exception {
        final String sql = "SELECT ITE.CODPROD,\r\nITE3.NUNOTA,\r\nITE3.SEQUENCIA,\r\nITE3.QTDNEG AS QTD_SOLICITADA,\r\nITE.QTDNEG AS QTD_DEVOLVIDA,\r\n(CASE WHEN ITE.QTDNEG < ITE3.QTDNEG THEN 'DP' ELSE 'D' END) AS STATUS\r\n\r\nFROM TGFITE ITE\r\nINNER JOIN TGFCAB CAB ON ITE.NUNOTA = CAB.NUNOTA\r\nINNER JOIN TGFVAR VAR ON ITE.NUNOTA =VAR.NUNOTA AND ITE.SEQUENCIA = VAR.SEQUENCIA \r\nINNER JOIN TGFITE ITE2 ON VAR.NUNOTAORIG = ITE2.NUNOTA AND VAR.SEQUENCIAORIG = ITE2.SEQUENCIA\r\nINNER JOIN TGFCAB CAB2 ON ITE2.NUNOTA = CAB2.NUNOTA\r\nINNER JOIN TGFVAR VAR2 ON ITE2.NUNOTA =VAR2.NUNOTA AND ITE2.SEQUENCIA = VAR2.SEQUENCIA \r\nINNER JOIN TGFITE ITE3 ON VAR2.NUNOTAORIG = ITE3.NUNOTA AND VAR2.SEQUENCIAORIG = ITE3.SEQUENCIA\r\nINNER JOIN TGFCAB CAB3 ON ITE3.NUNOTA = CAB3.NUNOTA\r\nWHERE ITE.NUNOTA = " + nunota;
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }

	public static ResultSet VerificarFerramentaEntregue(JdbcWrapper jdbcWrapper, BigDecimal nunota) throws Exception {
        final String sql = "SELECT ITE.CODPROD,\r\nITE2.NUNOTA,\r\nITE2.SEQUENCIA\r\nFROM TGFITE ITE\r\nINNER JOIN TGFVAR VAR ON VAR.NUNOTA = ITE.NUNOTA AND VAR.SEQUENCIA = ITE.SEQUENCIA\r\nINNER JOIN TGFITE ITE2 ON ITE2.NUNOTA = VAR.NUNOTAORIG  AND ITE2.SEQUENCIA = VAR.SEQUENCIAORIG\r\nINNER JOIN AD_MAQUINASUTILIZADAS MAQ ON ITE2.NUNOTA = MAQ.NUMREQUISICAO AND ITE2.SEQUENCIA = MAQ.SEQUENCIA\r\nWHERE ITE.NUNOTA =" + nunota;
        System.out.println("SQL: "+sql);
        final PreparedStatement pstm = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet query = pstm.executeQuery();
        return query;
    }

	public static ResultSet VerificarPecaDevolvidaNativeSQL(JdbcWrapper jdbcWrapper, BigDecimal nunota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT ITE.CODPROD, ITE3.NUNOTA, ITE3.SEQUENCIA, ITE3.QTDNEG AS QTD_SOLICITADA, \r\n"
					+ "CAST(ITE.QTDNEG + (SELECT NVL(QTDDEVOLVIDA, 0) FROM AD_TCSPRO WHERE CODPROD = ITE.CODPROD AND NUMOS = (SELECT NUMOS FROM AD_TCSPRO WHERE NUMREQUISICAO = ITE3.NUNOTA AND ROWNUM = 1)) AS NUMBER(10,2)) AS QTD_DEVOLVIDA, \r\n"
					+ "(CASE WHEN CAST(ITE.QTDNEG + (SELECT NVL(QTDDEVOLVIDA, 0) FROM AD_TCSPRO WHERE CODPROD = ITE.CODPROD AND NUMOS = (SELECT NUMOS FROM AD_TCSPRO WHERE NUMREQUISICAO = ITE3.NUNOTA AND ROWNUM = 1)) AS NUMBER(10,2)) < ITE3.QTDNEG THEN 'DP' ELSE 'D' END) AS STATUS  \r\n"
					+ "FROM TGFITE ITE \r\n"
					+ "INNER JOIN TGFCAB CAB ON ITE.NUNOTA = CAB.NUNOTA \r\n"
					+ "INNER JOIN TGFVAR VAR ON ITE.NUNOTA =VAR.NUNOTA AND ITE.SEQUENCIA = VAR.SEQUENCIA  \r\n"
					+ "INNER JOIN TGFITE ITE2 ON VAR.NUNOTAORIG = ITE2.NUNOTA AND VAR.SEQUENCIAORIG = ITE2.SEQUENCIA \r\n"
					+ "INNER JOIN TGFCAB CAB2 ON ITE2.NUNOTA = CAB2.NUNOTA \r\n"
					+ "INNER JOIN TGFVAR VAR2 ON ITE2.NUNOTA =VAR2.NUNOTA AND ITE2.SEQUENCIA = VAR2.SEQUENCIA  \r\n"
					+ "INNER JOIN TGFITE ITE3 ON VAR2.NUNOTAORIG = ITE3.NUNOTA AND VAR2.SEQUENCIAORIG = ITE3.SEQUENCIA \r\n"
					+ "INNER JOIN TGFCAB CAB3 ON ITE3.NUNOTA = CAB3.NUNOTA WHERE ITE.NUNOTA = "+nunota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return rset;
	}
}
