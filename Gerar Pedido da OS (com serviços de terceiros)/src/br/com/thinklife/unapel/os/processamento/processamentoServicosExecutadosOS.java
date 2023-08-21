package br.com.thinklife.unapel.os.processamento;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.gerarPedidoOS.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import br.com.thinklife.unapel.os.save.saves;

public class processamentoServicosExecutadosOS
{
    @SuppressWarnings("unused")
	public static void processar(final ContextoAcao arg0) throws Exception {
        final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        final BigDecimal usuLogado = arg0.getUsuarioLogado();
        final Registro[] linhas = arg0.getLinhas();
        BigDecimal numos = BigDecimal.ZERO;
        String fechamento = "";
        BigDecimal nunotaorigem = BigDecimal.ZERO;
        for (Integer i = 0; i < linhas.length; ++i) {
            numos = new BigDecimal(new StringBuilder().append(linhas[i].getCampo("NUMOS")).toString());
            fechamento = new StringBuilder().append(linhas[i].getCampo("FECHAMENTO")).toString();
            nunotaorigem = new BigDecimal(new StringBuilder().append(linhas[i].getCampo("NUNOTA")).toString());
            if (!fechamento.equals("S")) {
                arg0.mostraErro("Não é possível gerar pedido para Ordem de Serviço em aberto");
            }
            final String consultaModeloNOTA = buscaDados.modNotaServico(numos, usuLogado);
            final PreparedStatement pstm1 = jdbcWrapper.getPreparedStatement(consultaModeloNOTA);
            final ResultSet modelodeNota = pstm1.executeQuery();
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
            BigDecimal localpadcliente = BigDecimal.ZERO;
            BigDecimal vendedor = BigDecimal.ZERO;
            Timestamp dtneg = null;
            String tipmov = "";
            String tipoOs = "";
            while (modelodeNota.next()) {
                localVenda = modelodeNota.getBigDecimal("LOCAL_VENDA");
                codemp = modelodeNota.getBigDecimal("CODEMP");
                codparc = modelodeNota.getBigDecimal("CODPARC");
                codparcDest = modelodeNota.getBigDecimal("CODPARCDEST");
                codtipoper = modelodeNota.getBigDecimal("CODTIPOPER");
                codtipvenda = modelodeNota.getBigDecimal("CODTIPVENDA");
                codnat = modelodeNota.getBigDecimal("CODNAT");
                codcencus = modelodeNota.getBigDecimal("CODCENCUS");
                numnota = modelodeNota.getBigDecimal("NUMNOTA");
                localpad = modelodeNota.getBigDecimal("LOCALPAD");
                localpadcliente = modelodeNota.getBigDecimal("LOCALPADCLIENTE");
                dtneg = ArrumaData(modelodeNota.getString("DTNEG"));
                tipmov = modelodeNota.getString("TIPMOV");
                tipoOs = modelodeNota.getString("TIPOOS");
//                vendedor = modelodeNota.getBigDecimal("VENDEDOR");
            }
            BigDecimal codVeiculo = (BigDecimal) linhas[i].getCampo("CODVEICULO");
            BigDecimal vlrNota = BigDecimal.ZERO;
            final String consulta = buscaDados.consultaSQL(numos);
            final PreparedStatement pstm2 = jdbcWrapper.getPreparedStatement(consulta);
            final ResultSet pedidodeVendaOS = pstm2.executeQuery();
            BigDecimal nunota = BigDecimal.ZERO;
            Integer contador = 0;
            while (pedidodeVendaOS.next()) {
                final BigDecimal pedidoVenda = pedidodeVendaOS.getBigDecimal("AD_NROPEDVENDA");
                if (pedidoVenda.compareTo(BigDecimal.ZERO) <= 0) {
                    if (contador < 1) {
                        nunota = saves.cabecalhoPecasUtilizadas(dwf, codemp, codparc, codparcDest, numnota, codtipoper, codtipvenda, codnat, codcencus, numos, tipmov, tipoOs, localVenda, vendedor, codVeiculo);
                        System.out.println("Nota criada: "+nunota);
                        saves.salvarTCSNOTAS(dwf, codtipoper, codparc, nunota, dtneg, numos);
                    }
                    BigDecimal codprod = pedidodeVendaOS.getBigDecimal("CODSERV");
                    String usoProd = Controller.getUsoProd(codprod);
                    BigDecimal quantidade = BigDecimal.ZERO;
                    BigDecimal vlrUnit = BigDecimal.ZERO;
                    final String garantia = pedidodeVendaOS.getString("GARANTIA");
//                    if (garantia.equals("S")) {
//                        quantidade = pedidodeVendaOS.getBigDecimal("TEMPOPADRAO");
//                    } else { // ------- INICIO ALTERAÇÕES DE ACORDO COM PRECIFICAÇÃO DO SERVICO ------- //
                    	String tipoCalculo = Controller.getTipoCalculo(codprod);
                    	System.out.println("Tipo calc: "+tipoCalculo);
                		if(tipoCalculo == null || "#".equals(tipoCalculo) || "".equals(tipoCalculo))
                			throw new Exception("Tipo de cálculo de preço não preenchido para o serviço "+codprod);
                		
                		String baseValor = Controller.getBaseValor(codprod);
                		System.out.println("Base valor: "+baseValor);
                		if(baseValor == null || "#".equals(baseValor) || "".equals(baseValor))
                			throw new Exception("Base Valor Hora não preenchido para o serviço "+codprod);
                		
                		BigDecimal vlrPadrao = Controller.getValorPadrao(codprod);
                		
                		if("V".equals(tipoCalculo)) { // Horas Vendidas x Valor Hora
            				System.out.println("Horas Vendidas x Valor Hora");
            				BigDecimal hrsTrabalhadas = pedidodeVendaOS.getBigDecimal("HSTRABALHADAS");
        					if(hrsTrabalhadas == null || hrsTrabalhadas.compareTo(BigDecimal.ZERO) == 0) {
	            				BigDecimal hrsApontamentos = pedidodeVendaOS.getBigDecimal("APONTAMENTOS");
	            				if(hrsApontamentos == null || hrsApontamentos.compareTo(BigDecimal.ZERO) == 0) {
            						throw new Exception("Horas não preenchidas para o serviço "+codprod);
            					} else {
            						quantidade = hrsTrabalhadas;
            					}
            				} else {
            					quantidade = hrsTrabalhadas;
            				}
                		} else if("F".equals(tipoCalculo)) { // Preço Fixo
            				System.out.println("Preço Fixo");
            				quantidade = BigDecimal.ONE;
                		} else if("N".equals(tipoCalculo)) { // Preço Manual
            				System.out.println("Preço Manual");
            				quantidade = BigDecimal.ONE;
                		} else if("P".equals(tipoCalculo)) { // Tempo Padrão x Valor
            				System.out.println("Tempo Padrão x Valor");
            				quantidade = pedidodeVendaOS.getBigDecimal("TEMPOPADRAO");
            				if(quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
        						throw new Exception("Tempo padrão não preenchidas para o serviço "+codprod);
        					}
                		} else if("Q".equals(tipoCalculo)) { // Quantidade x Valor
            				System.out.println("Quantidade x Valor");
            				quantidade = pedidodeVendaOS.getBigDecimal("QTDNEG");
            				if(quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
        						throw new Exception("Quantidade não preenchidas para o serviço "+codprod);
        					}
                		}
                		BigDecimal codParcMecanico = Controller.getCodParcDoUsuario(pedidodeVendaOS.getBigDecimal("CODUSURESP"));
                		if("V".equals(baseValor)) { // Mecânico - Venda
                			System.out.println("Mecânico - Venda");
                			BigDecimal vlrMecanicoVenda = Controller.getValorVendaMecanico(codParcMecanico);
        					if((BigDecimal.ZERO).compareTo(vlrMecanicoVenda) == 1)
        						throw new Exception("Valor de venda para o mecânico "+codParcMecanico+" está negativo!");
        					if(vlrMecanicoVenda != null)
        						vlrUnit = vlrMecanicoVenda;
        					else
        						throw new Exception("Valor de venda não encontrado para mecânico "+codParcMecanico+"!");
                		} else if("S".equals(baseValor)) { // Serviço
                			System.out.println("Serviço");
                			if((BigDecimal.ZERO).compareTo(vlrPadrao) == 1)
        						throw new Exception("Valor do serviço está negativo!");
        					if(vlrPadrao != null)
        						vlrUnit = vlrPadrao;
        					else
        						throw new Exception("Valor do serviço não encontrado!");
                		} else if("M".equals(baseValor)) { // Manual
                			System.out.println("Manual");
                			vlrUnit = pedidodeVendaOS.getBigDecimal("VALORSERVICO");
                		} else if("C".equals(baseValor)) { // Mecânico - Custo
                			System.out.println("Mecânico - Custo");
                			BigDecimal vlrMecanicoCusto = Controller.getValorCustoMecanico(codParcMecanico);
        					if((BigDecimal.ZERO).compareTo(vlrMecanicoCusto) == 1)
        						throw new Exception("Valor de custo para o mecânico "+codParcMecanico+" está negativo!");
        					if(vlrMecanicoCusto != null)
        						vlrUnit = vlrMecanicoCusto;
        					else
        						throw new Exception("Valor de custo não encontrado para mecânico "+codParcMecanico+"!");
                		} else if("T".equals(baseValor)) { // Tabela tipo de OS
                			System.out.println("Tabela tipo de OS");
                			BigDecimal vlrTabelaDePrecos = BigDecimal.ZERO;
        					try {
        						BigDecimal tipoOS = Controller.getTipoOs(numos);
        						BigDecimal empresa = Controller.getEmpresa(numos);
        						vlrTabelaDePrecos = Controller.coletarPrecoDaTabelaDePrecoDoServico(tipoOs, empresa, codprod);
        						if((BigDecimal.ZERO).compareTo(vlrTabelaDePrecos) == 1)
        							throw new Exception("Valor negativo encontrado para o Serviço "+codprod+" na Tabela de Preço do Serviço!");
        					} catch(Exception e) {
        						e.printStackTrace();
        						throw new Exception(e.getMessage());
        					}
        					vlrUnit = vlrTabelaDePrecos;
                		}
//            		} 
                    final String codvol = pedidodeVendaOS.getString("CODVOL");
                    BigDecimal vlrtot = pedidodeVendaOS.getBigDecimal("VALORSERVICO");
                    if(vlrtot == null || vlrtot.equals(BigDecimal.ZERO))
                    	throw new Exception("Valor do serviço "+codprod+" não está preenchido!");
//                    vlrtot = vlrtot.multiply(quantidade);
                    vlrNota = vlrNota.add(vlrtot);
                    // ------- FIM ALTERAÇÕES DE ACORDO COM PRECIFICAÇÃO DO SERVICO ------- //
                    final BigDecimal nuItemOs = pedidodeVendaOS.getBigDecimal("NUMITEM");
                    final String reserva = "N";
                    final BigDecimal atualestoque = BigDecimal.ZERO;
                    final BigDecimal codVendServico = Controller.getCodVendDoUsuario(pedidodeVendaOS.getBigDecimal("CODUSURESP"));
                    final BigDecimal sequencia = saves.itemServicosUtilizados(dwf, nunota, codprod, codvol, quantidade, localpad, null, new BigDecimal(contador + 1), vlrtot, null, atualestoque, reserva, vlrUnit, usoProd, codVendServico);
                    final String update = "update AD_TCSITE set AD_NROPEDVENDA = " + nunota + " where numos = " + numos + " and NUMITEM =" + nuItemOs;
                    final PreparedStatement pstmupdate = jdbcWrapper.getPreparedStatement(update);
                    pstmupdate.executeUpdate();
                    final String consulta2 = buscaDados.consultaTGFVAR(nunota);
                    final PreparedStatement prepTGFVAR = jdbcWrapper.getPreparedStatement(consulta2);
                    final ResultSet dadosTGFVAR = prepTGFVAR.executeQuery();
                    while (dadosTGFVAR.next()) {
                        final String statusnota = dadosTGFVAR.getString("STATUSNOTA");
                        saves.salvarTGFVAR(dwf, nunota, sequencia, nunotaorigem, sequencia, statusnota);
                    }
                    ++contador;
                }
            }
            // ------- INICIO ALTERAÇÕES PARA INCLUIR SERVIÇO DE TERCEIROS NO PEDIDO ------- //
            if(existeServicoDeTerceiro(numos)) {
	            if(nunota.compareTo(BigDecimal.ZERO) == 0) {
	            	nunota = saves.cabecalhoPecasUtilizadas(dwf, codemp, codparc, codparcDest, numnota, codtipoper, codtipvenda, codnat, codcencus, numos, tipmov, tipoOs, localVenda, vendedor, codVeiculo);
	                System.out.println("Nota criada: "+nunota);
	                saves.salvarTCSNOTAS(dwf, codtipoper, codparc, nunota, dtneg, numos);
	            }
	            BigDecimal vlrServicosTerceiro = gerarServicosDeTerceirosNaNota(nunota, localpad, nunotaorigem, numos);
	            vlrNota = vlrNota.add(vlrServicosTerceiro);
            }
            // ------- FIM ALTERAÇÕES PARA INCLUIR SERVIÇO DE TERCEIROS NO PEDIDO ------- //
            final String update2 = "UPDATE TGFCAB SET VLRNOTA = " + vlrNota + " WHERE NUNOTA = " + nunota;
            final PreparedStatement pstmNota = jdbcWrapper.getPreparedStatement(update2);
            pstmNota.executeUpdate();
            // ------- INICIO ALTERAÇÕES PARA ALTERAR ORÇAMENTO PARA PENDENTE = 'N' ------- //
//            procurarFecharOrcamento(nunotaorigem);
            fecharOrcamento(nunotaorigem);
            // ------- FIM ALTERAÇÕES PARA ALTERAR ORÇAMENTO PARA PENDENTE = 'N' ------- //
        }
    }
    
    private static boolean existeServicoDeTerceiro(BigDecimal numos) throws Exception {
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
			
			sql.appendSql("SELECT * FROM AD_TCSSERVTERC WHERE NUMOS = "+numos);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				return true;
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

//	private static void procurarFecharOrcamento(BigDecimal nunotaorigem) throws Exception {
//    	JdbcWrapper jdbc = null;
//		NativeSql sql = null;
//		ResultSet rset = null;
//		SessionHandle hnd = null;
//		BigDecimal nuNota = null;
//		try {
//			hnd = JapeSession.open();
//			hnd.setFindersMaxRows(-1);
//			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
//			jdbc = entity.getJdbcWrapper();
//			jdbc.openSession();
//			
//			sql = new NativeSql(jdbc);
//			
//			sql.appendSql("SELECT NUNOTA FROM AD_TCSOSE WHERE NUMOS = "+nunotaorigem);
//			System.out.println("SQL: "+sql.toString());
//			
//			rset = sql.executeQuery();
//			
//			if (rset.next()) {
//				nuNota = rset.getBigDecimal("NUNOTA");
//				fecharOrcamento(nuNota);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JdbcUtils.closeResultSet(rset);
//			NativeSql.releaseResources(sql);
//			JdbcWrapper.closeSession(jdbc);
//			JapeSession.close(hnd);
//		}
//	}

	private static void fecharOrcamento(BigDecimal nuNota) throws Exception {
		System.out.println("Alterando orçamento "+nuNota+" para PENTENDE = 'N'");
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper orcamentoDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);
			DynamicVO orcamento = orcamentoDAO.findOne(" NUNOTA = "+nuNota);
			orcamentoDAO.prepareToUpdate(orcamento)
				.set("PENDENTE", "N")
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static BigDecimal gerarServicosDeTerceirosNaNota(BigDecimal nunota, BigDecimal localpad, BigDecimal nunotaorigem, BigDecimal numos) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrTotal = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPROD, VALORSERVICO FROM AD_TCSSERVTERC WHERE NUMOS = "+numos);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				BigDecimal codprod = rset.getBigDecimal("CODPROD");
				String usoprod = Controller.getUsoProd(codprod);
				BigDecimal vlrtot = rset.getBigDecimal("VALORSERVICO");
				BigDecimal vlrUnit = vlrtot;
				vlrTotal = vlrTotal.add(vlrtot);
				BigDecimal seq = getSequencia(nunota);
				String codvol = getCodVolDoServico(codprod);
				System.out.println("Criando serviço de terceiro... "+nunota+" "+codprod+" "+vlrUnit);
		        BigDecimal sequencia = saves.criarServicoDeTerceiroNaOS(nunota, codprod, codvol, BigDecimal.ONE, localpad, null, seq, vlrtot, null, BigDecimal.ZERO, "N", vlrUnit, usoprod);
		        
		        final String consulta2 = buscaDados.consultaTGFVAR(nunota);
		        final PreparedStatement prepTGFVAR = jdbc.getPreparedStatement(consulta2);
		        final ResultSet dadosTGFVAR = prepTGFVAR.executeQuery();
		        while (dadosTGFVAR.next()) {
		            final String statusnota = dadosTGFVAR.getString("STATUSNOTA");
		            System.out.println("Criando TGFVAR...");
		            saves.criarRegistroNaTGFVAR(nunota, sequencia, nunotaorigem, sequencia, statusnota);
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
		return vlrTotal;
	}

	private static BigDecimal getSequencia(BigDecimal nunota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT MAX(SEQUENCIA) FROM TGFITE WHERE NUNOTA = "+nunota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
				if(dado == null)
					dado = BigDecimal.ZERO;
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
		return dado.add(BigDecimal.ONE);
	}

	private static String getCodVolDoServico(BigDecimal codprod) throws Exception {
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
			
			sql.appendSql("SELECT CODVOL FROM TGFPRO WHERE CODPROD = "+codprod);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString(1);
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
		return dado;
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
