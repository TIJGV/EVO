package br.com.thinklife.unapel.os.iniciar;

import javax.xml.bind.DatatypeConverter;

import com.sankhya.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.thinklife.unapel.os.save.saves;
import java.sql.Timestamp;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import br.com.thinklife.unapel.os.processamento.ConverterData;
import java.math.BigDecimal;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

public class ProcessamentoOS implements AcaoRotinaJava
{
    public void doAction(final ContextoAcao arg0) throws Exception {
    	System.out.println("***THINKLIFE/EVO - PROCESSAMENTO DE OS - INICIO***");
        final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        final Registro[] linhas = arg0.getLinhas();
        String nunota = "";
        String statusnota = "";
        BigDecimal tipoos = BigDecimal.ZERO;
        BigDecimal tipofat = BigDecimal.ZERO;
        BigDecimal tipoatendimento = BigDecimal.ZERO;
        for (Integer i = 0; i < linhas.length; ++i) {
            nunota = new StringBuilder().append(linhas[i].getCampo("NUNOTA")).toString();
            statusnota = new StringBuilder().append(linhas[i].getCampo("STATUSNOTA")).toString();
        }
        final BigDecimal codusulogado = arg0.getUsuarioLogado();
        final PreparedStatement pstm11 = jdbcWrapper.getPreparedStatement("select * from AD_TCSOSE where NUNOTA= " + nunota);
        final ResultSet retorno11 = pstm11.executeQuery();
        if (retorno11.next()) {
            arg0.mostraErro(mensagemRetornoErro(retorno11.getBigDecimal("NUMOS")));
        }
        if (!statusnota.equals("L")) {
            arg0.mostraErro("<br><br>É necessario confirmar a nota.<br><br>");
        }
        final String consulta = buscaDados.consultaOrdemServico(new BigDecimal(nunota));
        final PreparedStatement pstm12 = jdbcWrapper.getPreparedStatement(consulta);
        final ResultSet ordemServico = pstm12.executeQuery();
        while (ordemServico.next()) {
            final BigDecimal codemp = ordemServico.getBigDecimal("CODEMP");
            final BigDecimal codcencus = ordemServico.getBigDecimal("CODCENCUS");
            BigDecimal codparc = ordemServico.getBigDecimal("CODPARC");
            final BigDecimal codproj = ordemServico.getBigDecimal("CODPROJ");
            final BigDecimal horimetro = ordemServico.getBigDecimal("HORIMETRO");
            final BigDecimal mecanico = ordemServico.getBigDecimal("MECANICO");
            final String dataentrada = ordemServico.getString("SYSDATE");
            final String obs = ordemServico.getString("OBSERVACAO");
            final BigDecimal codveiculo = ordemServico.getBigDecimal("CODVEICULO");
            final BigDecimal codnat = ordemServico.getBigDecimal("CODNAT");
            final String garantia = ordemServico.getString("GARANTIA");
            final BigDecimal codParcDest = ordemServico.getBigDecimal("CODPARCDEST");
            final BigDecimal codMecanico = ordemServico.getBigDecimal("AD_CODUSUEXEC");
            tipoos = ordemServico.getBigDecimal("AD_CODTIPODEOS");
            tipofat = ordemServico.getBigDecimal("CODFATUR");
            tipoatendimento = ordemServico.getBigDecimal("CODATENDIMENTO");
            final Timestamp d1Inicial = ConverterData.converterData(dataentrada);
            if (garantia.equals("S")) {
                codparc = codParcDest;
            }
            try {
                if (tipoos == null || tipofat == null || tipoatendimento == null) {
                    arg0.mostraErro("<br><br>Para cadastrar OS é necessario preencher: TIPO da <br>OS <br>Faturamento <br>Atendimento.<br><br>");
                }
            }
            catch (Exception resp) {
                arg0.mostraErro("<br><br>Para cadastrar OS é necessario preencher: TIPO da <br>OS <br>Faturamento <br>Atendimento.<br><br>");
            }
            final BigDecimal numosNovo = saves.salvarOrdemServico(dwf, nunota, codusulogado, codemp, codcencus, codparc, codproj, codveiculo, new Timestamp(d1Inicial.getTime()), horimetro, tipoos, tipofat, tipoatendimento, codnat, obs, mecanico);
            final String consulta2 = buscaDados.consultaTCSITE(numosNovo);
            final PreparedStatement pstm13 = jdbcWrapper.getPreparedStatement(consulta2);
            final ResultSet ordemServicoTCSITE = pstm13.executeQuery();
            while (ordemServicoTCSITE.next()) {
                final BigDecimal codprod = ordemServicoTCSITE.getBigDecimal("CODPROD");
                final String dataalt = ordemServicoTCSITE.getString("DATA");
                final BigDecimal vlrtot = ordemServicoTCSITE.getBigDecimal("VALORSERVICO");
                final BigDecimal codemp2 = ordemServicoTCSITE.getBigDecimal("CODEMP");
                final Timestamp data = ConverterData.converterData(dataalt);
                saves.salvarTCSITE(dwf, codprod, data, vlrtot, codemp2, numosNovo, codMecanico);
            }
            final String sqlPRO = buscaDados.consultaTCSPRO2(new BigDecimal(nunota));
            final PreparedStatement prepPRO = jdbcWrapper.getPreparedStatement(sqlPRO);
            final ResultSet rsPRO = prepPRO.executeQuery();
            while (rsPRO.next()) {
                final String lote = rsPRO.getString("CONTROLE");
                final BigDecimal codprodPRO = rsPRO.getBigDecimal("CODPROD");
                final BigDecimal vlrunit = rsPRO.getBigDecimal("VLRUNIT");
                final BigDecimal quantidade = rsPRO.getBigDecimal("QUANTIDADE");
                saves.salvarTCSPRO(dwf, numosNovo, codprodPRO, vlrunit, nunota, codparc, quantidade, lote);
            }
            // INICIO ALTERAÇÃO EVO NETWORK - RETORNAR ERRO QUANDO GRUPO ESTIVER COM FLAG AD_PECASUTILIZADASOFICINA DESMARCADA
        	final String sqlPRO2 = buscaDados.consultaTCSPRO3(new BigDecimal(nunota));
        	final PreparedStatement prepPRO2 = jdbcWrapper.getPreparedStatement(sqlPRO2);
            final ResultSet rsPRO2 = prepPRO2.executeQuery();
            while (rsPRO2.next()) {
            	final BigDecimal codGrupoProd = rsPRO2.getBigDecimal("CODGRUPOPROD");
            	final String usoProd = rsPRO2.getString("USOPROD");
            	if("R".equals(usoProd) && grupoEstaComFlagPecasDesmarcada(codGrupoProd)) {
            		throw new Exception("O campo 'Peças Utilizadas na oficina' está desmarcado para o Grupo "+codGrupoProd+"!");
            	}
            }
            // FIM ALTERAÇÃO EVO NETWORK
            arg0.setMensagemRetorno(mensagemRetorno(numosNovo));
        }
        jdbcWrapper.closeSession();
        System.out.println("***THINKLIFE/EVO - PROCESSAMENTO DE OS - FIM***");
    }
    
    private boolean grupoEstaComFlagPecasDesmarcada(BigDecimal codGrupoProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String dado = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_PECASUTILIZADASOFICINA FROM TGFGRU WHERE CODGRUPOPROD = "+codGrupoProd);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString(1);
				if(!"S".equals(dado)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return false;
	}

	public static String mensagemRetorno(final BigDecimal numero) {
        final String id = "br.com.sankhya.menu.adicional.TCSOSE";
        final String mensagemSucesso = "Ordem de serviço gerada com sucesso!";
        final String pk = "{\"NUMOS\"=\"" + numero + "\"}";
        final String caminho = "/mge/system.jsp#app/";
        final String idBase64 = DatatypeConverter.printBase64Binary(id.getBytes());
        final String paransBase64 = DatatypeConverter.printBase64Binary(pk.toString().replaceAll("=", ":").getBytes());
        final String icone = "<p align=\"rigth\"><a href=\"" + caminho + idBase64 + "/" + paransBase64 + "\" target=\"_top\" >" + "<img src=\"http://imageshack.com/a/img923/7316/ux573F.png\" ><font size=\"20\" color=\"#008B45\"><b>" + numero + "</b></font></a></p>";
        final String mensagemRetorno = icone + "<p align=\"left\">" + mensagemSucesso + "<br></p>";
        return mensagemRetorno;
    }
    
    public static String mensagemRetornoErro(final BigDecimal numos) {
        final String id = "br.com.sankhya.menu.adicional.TCSOSE";
        final String mensagemSucesso = "A Ordem de Serviço já foi criada!";
        final String pk = "{\"NUMOS\"=\"" + numos + "\"}";
        final String caminho = "/mge/system.jsp#app/";
        final String idBase64 = DatatypeConverter.printBase64Binary(id.getBytes());
        final String paransBase64 = DatatypeConverter.printBase64Binary(pk.toString().replaceAll("=", ":").getBytes());
        final String icone = "<p align=\"rigth\"><a href=\"" + caminho + idBase64 + "/" + paransBase64 + "\" target=\"_top\" >" + "<img src=\"http://imageshack.com/a/img923/7316/ux573F.png\" ><font size=\"20\" color=\"#008B45\"><b>" + numos + "</b></font></a></p>";
        final String mensagemRetorno = icone + "<p align=\"left\">" + mensagemSucesso + "<br></p>";
        return mensagemRetorno;
    }
}
