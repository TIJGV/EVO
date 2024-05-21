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
	public static void processar(ContextoAcao arg0) throws Exception {
        EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        BigDecimal usuLogado = arg0.getUsuarioLogado();
        Registro[] linhas = arg0.getLinhas();
        BigDecimal numos = BigDecimal.ZERO;
        String fechamento = "";
        BigDecimal nunotaorigem = BigDecimal.ZERO;
        for (Integer i = 0; i < linhas.length; ++i) {
            numos = new BigDecimal(new StringBuilder().append(linhas[i].getCampo("NUMOS")).toString());
            fechamento = new StringBuilder().append(linhas[i].getCampo("FECHAMENTO")).toString();
            BigDecimal nroOrcamentoOrigem = (BigDecimal) linhas[i].getCampo("NUNOTA");
            if(nroOrcamentoOrigem == null)
            	nunotaorigem = null;
            else
            	nunotaorigem = new BigDecimal(new StringBuilder().append(nroOrcamentoOrigem).toString());
            if(nunotaorigem == null)
            	System.out.println("NUNOTA Nulo, não serão criados registros na TGFVAR.");
            if (!fechamento.equals("S"))
                arg0.mostraErro("Não é possível gerar pedido para Ordem de Serviço em aberto");
            String consultaModeloNOTA = buscaDados.modNotaServico(numos, usuLogado);
            PreparedStatement pstm1 = jdbcWrapper.getPreparedStatement(consultaModeloNOTA);
            ResultSet modelodeNota = pstm1.executeQuery();
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
            String consulta = buscaDados.consultaSQL(numos);
            PreparedStatement pstm2 = jdbcWrapper.getPreparedStatement(consulta);
            ResultSet pedidodeVendaOS = pstm2.executeQuery();
            BigDecimal nunota = BigDecimal.ZERO;
            Integer contador = 0;
            while (pedidodeVendaOS.next()) {
                BigDecimal pedidoVenda = pedidodeVendaOS.getBigDecimal("AD_NROPEDVENDA");
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
                    String garantia = pedidodeVendaOS.getString("GARANTIA");
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
            						quantidade = hrsApontamentos;
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
                			if(vlrMecanicoVenda == null)
                				throw new Exception("Valor de venda para o mecânico "+codParcMecanico+" está vazio!");
        					if((BigDecimal.ZERO).compareTo(vlrMecanicoVenda) == 1)
        						throw new Exception("Valor de venda para o mecânico "+codParcMecanico+" está negativo!");
        					if(vlrMecanicoVenda != null)
        						vlrUnit = vlrMecanicoVenda;
        					else
        						throw new Exception("Valor de venda não encontrado para mecânico "+codParcMecanico+"!");
                		} else if("S".equals(baseValor)) { // Serviço
                			System.out.println("Serviço");
                			if(vlrPadrao == null)
                				throw new Exception("Valor do serviço está vazio!");
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
                			if(vlrMecanicoCusto == null)
        						throw new Exception("Valor de custo para o mecânico "+codParcMecanico+" está vazio!");
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
        						if(vlrTabelaDePrecos == null)
        							throw new Exception("Valor vazio encontrado para o Serviço "+codprod+" na Tabela de Preço do Serviço!");
        						if((BigDecimal.ZERO).compareTo(vlrTabelaDePrecos) == 1)
        							throw new Exception("Valor negativo encontrado para o Serviço "+codprod+" na Tabela de Preço do Serviço!");
        					} catch(Exception e) {
        						e.printStackTrace();
        						throw new Exception(e.getMessage());
        					}
        					vlrUnit = vlrTabelaDePrecos;
                		}
//            		} 
                    String codvol = pedidodeVendaOS.getString("CODVOL");
                    BigDecimal vlrtot = pedidodeVendaOS.getBigDecimal("VALORSERVICO");
                    if(vlrtot == null || vlrtot.equals(BigDecimal.ZERO))
                    	throw new Exception("Valor do serviço "+codprod+" não está preenchido!");
//                    vlrtot = vlrtot.multiply(quantidade);
                    vlrNota = vlrNota.add(vlrtot);
                    // ------- FIM ALTERAÇÕES DE ACORDO COM PRECIFICAÇÃO DO SERVICO ------- //
                    BigDecimal nuItemOs = pedidodeVendaOS.getBigDecimal("NUMITEM");
                    String reserva = "N";
                    BigDecimal atualestoque = BigDecimal.ZERO;
                    BigDecimal codVendServico = Controller.getCodVendDoUsuario(pedidodeVendaOS.getBigDecimal("CODUSURESP"));
                    BigDecimal sequencia = saves.itemServicosUtilizados(dwf, nunota, codprod, codvol, quantidade, localpad, null, new BigDecimal(contador + 1), vlrtot, null, atualestoque, reserva, vlrUnit, usoProd, codVendServico);
                    String update = "update AD_TCSITE set AD_NROPEDVENDA = " + nunota + " where numos = " + numos + " and NUMITEM =" + nuItemOs;
                    PreparedStatement pstmupdate = jdbcWrapper.getPreparedStatement(update);
                    pstmupdate.executeUpdate();
                    String consulta2 = buscaDados.consultaTGFVAR(nunota);
                    PreparedStatement prepTGFVAR = jdbcWrapper.getPreparedStatement(consulta2);
                    ResultSet dadosTGFVAR = prepTGFVAR.executeQuery();
                    while (dadosTGFVAR.next()) {
                        String statusnota = dadosTGFVAR.getString("STATUSNOTA");
                        if(nunotaorigem != null)
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
	            BigDecimal vlrServicosTerceiro = gerarServicosDeTerceirosNaNota(nunota, localpad, nunotaorigem, numos, jdbcWrapper);
	            vlrNota = vlrNota.add(vlrServicosTerceiro);
            }
            // ------- FIM ALTERAÇÕES PARA INCLUIR SERVIÇO DE TERCEIROS NO PEDIDO ------- //
            String update2 = "UPDATE TGFCAB SET VLRNOTA = " + vlrNota + " WHERE NUNOTA = " + nunota;
            PreparedStatement pstmNota = jdbcWrapper.getPreparedStatement(update2);
            pstmNota.executeUpdate();
            // ------- INICIO ALTERAÇÕES PARA ALTERAR ORÇAMENTO PARA PENDENTE = 'N' ------- //
//            procurarFecharOrcamento(nunotaorigem);
            if(nunotaorigem != null)
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
			
			sql.appendSql("SELECT * FROM AD_TCSSERVTERC WHERE NVL(NUNOTAPED,0) = 0 AND NUMOS = "+numos);
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

	private static BigDecimal gerarServicosDeTerceirosNaNota(BigDecimal nunota, BigDecimal localpad, BigDecimal nunotaorigem, BigDecimal numos, JdbcWrapper jdbcWrapper) throws Exception {
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
			
			sql.appendSql("SELECT CODPROD, VALORSERVICO, CODSERVTERC FROM AD_TCSSERVTERC WHERE NUMOS = "+numos);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				BigDecimal codprod = rset.getBigDecimal("CODPROD");
				String usoprod = Controller.getUsoProd(codprod);
				BigDecimal vlrtot = rset.getBigDecimal("VALORSERVICO");
				BigDecimal codServTerc = rset.getBigDecimal("CODSERVTERC");
				BigDecimal vlrUnit = vlrtot;
				vlrTotal = vlrTotal.add(vlrtot);
				BigDecimal seq = getSequencia(nunota);
				String codvol = getCodVolDoServico(codprod);
				System.out.println("Criando serviço de terceiro... "+nunota+" "+codprod+" "+vlrUnit);
		        BigDecimal sequencia = saves.criarServicoDeTerceiroNaOS(nunota, codprod, codvol, BigDecimal.ONE, localpad, null, seq, vlrtot, null, BigDecimal.ZERO, "N", vlrUnit, usoprod);
		        
		        String consulta2 = buscaDados.consultaTGFVAR(nunota);
		        PreparedStatement prepTGFVAR = jdbc.getPreparedStatement(consulta2);
		        ResultSet dadosTGFVAR = prepTGFVAR.executeQuery();
		        while (dadosTGFVAR.next()) {
		            String statusnota = dadosTGFVAR.getString("STATUSNOTA");
		            System.out.println("Criando TGFVAR...");
		            if(nunotaorigem != null)
		            	saves.criarRegistroNaTGFVAR(nunota, sequencia, nunotaorigem, sequencia, statusnota);
		        }
		        String update = "UPDATE AD_TCSSERVTERC SET NUNOTAPED = "+nunota+" WHERE NUMOS = "+numos+" AND CODSERVTERC = "+codServTerc;
                PreparedStatement pstmupdate = jdbcWrapper.getPreparedStatement(update);
                pstmupdate.executeUpdate();
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
