package br.com.evonetwork.precificacaoDeServico.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	public static String prepararParaCalcularPrecificacao(DynamicVO servicoExecutadoVO) throws Exception {
		BigDecimal codServ = servicoExecutadoVO.asBigDecimal("CODSERV");
		
		String tipoCalculo = Controller.getTipoCalculo(codServ);
		if(tipoCalculo == null || "#".equals(tipoCalculo) || "".equals(tipoCalculo))
			throw new Exception("Tipo de cálculo de preço não preenchido para o serviço "+codServ);
		
		String baseValor = Controller.getBaseValor(codServ);
		if(baseValor == null || "#".equals(baseValor) || "".equals(baseValor))
			throw new Exception("Base Valor Hora não preenchido para o serviço "+codServ);
		
		BigDecimal vlrPadrao = Controller.getValorPadrao(codServ);
		String retorno = calcularPreco(tipoCalculo, baseValor, vlrPadrao, servicoExecutadoVO);
		
		return retorno;
	}

	public static void gerenciarPrecificacao(DynamicVO servicoExecutadoVO) throws Exception {
		if(servicoExecutadoVO.asBigDecimal("VALORSERVICO") == null) {
			prepararParaCalcularPrecificacao(servicoExecutadoVO);
		} else {
			System.out.println("Preço já preenchido!");
		}
	}

	private static String calcularPreco(String tipoCalculo, String baseValor, BigDecimal vlrPadrao, DynamicVO servicoExecutadoVO) throws Exception {
		BigDecimal codParcMecanico = getCodParcDoUsuario(servicoExecutadoVO.asBigDecimal("CODUSURESP"));
		StringBuilder str = new StringBuilder();
		str.append("");
		switch (tipoCalculo) {
			case "V": //Horas Vendidas x Valor Hora
				try {
					BigDecimal valor = getValor(baseValor, vlrPadrao, codParcMecanico, str, servicoExecutadoVO);
					BigDecimal precoServico = BigDecimal.ZERO;
					BigDecimal quantidade = BigDecimal.ZERO;
					BigDecimal hrsTrabalhadas = servicoExecutadoVO.asBigDecimal("HORASVENDIDASDEC");
					if(hrsTrabalhadas == null || hrsTrabalhadas.compareTo(BigDecimal.ZERO) == 0) {
        				BigDecimal hrsApontamentos = servicoExecutadoVO.asBigDecimal("TOTHORASAPONTDEC");
        				if(hrsApontamentos == null || hrsApontamentos.compareTo(BigDecimal.ZERO) == 0) {
    						throw new Exception("Horas não preenchidas para o serviço "+servicoExecutadoVO.asBigDecimal("CODSERV"));
    					} else {
    						quantidade = hrsApontamentos;
    					}
    				} else {
    					quantidade = hrsTrabalhadas;
    				}
					precoServico = valor.multiply(quantidade);
					atualizarPrecoDoServico(precoServico, servicoExecutadoVO);
//					int hrInicial = servicoExecutadoVO.asInt("HRINICIAL");
//					int hrFinal = servicoExecutadoVO.asInt("HRFINAL");
//					int intervalo = servicoExecutadoVO.asInt("INTERVALO");
//					if(hrFinal == 0) {
//						int hrsApontamentos = servicoExecutadoVO.asInt("HORAAPONTAMENTOS");
//						if(hrsApontamentos == 0)
//							throw new Exception("O serviço executado não possui Horas preenchidas para realizar o cálculo do preço!");
//						BigDecimal hrsVendidas = calcularHoras(0, hrsApontamentos);
//						precoServico = valor.multiply(hrsVendidas);
//						atualizarPrecoDoServico(precoServico, servicoExecutadoVO);
//					} else {
//						float hrsSemIntervalo = calcularHorasDoServico(hrInicial, hrFinal);
////						str.append("\nHrs: "+hrsSemIntervalo);
//						float hrIntervalo = (calcularHoras(0, intervalo).multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.UP)).intValue();
////						str.append("\nIntervalo: "+hrIntervalo);
//						BigDecimal hrsVendidas = BigDecimal.valueOf((hrsSemIntervalo - hrIntervalo)/60);
////						str.append("\nHrs Total: "+hrsVendidas);
//						precoServico = valor.multiply(hrsVendidas);
////						str.append("\nPreço: "+precoServico);
//						atualizarPrecoDoServico(precoServico, servicoExecutadoVO);
//					}
				} catch(Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
				return str.toString();
			case "F": //Preço Fixo
				try {
					BigDecimal precoServico = getValor(baseValor, vlrPadrao, codParcMecanico, str, servicoExecutadoVO);
					atualizarPrecoDoServico(precoServico, servicoExecutadoVO);
				} catch(Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
				return str.toString();
			case "N": //Preço Manual
				str.append("Preço do serviço "+servicoExecutadoVO.asBigDecimal("CODSERV")+" é manual, o usuário deve inserir o preço manualmente na OS.\n");
				return str.toString();
			case "P": //Tempo Padrão x Valor
				try {
					BigDecimal valor = getValor(baseValor, vlrPadrao, codParcMecanico, str, servicoExecutadoVO);
					BigDecimal tempoPadrao = getTempoPadrao(servicoExecutadoVO.asBigDecimal("CODSERV"));
					if(tempoPadrao == null)
						throw new Exception("Tempo padrão não está preenchido no Serviço!");
					BigDecimal precoServico = valor.multiply(tempoPadrao);
					atualizarPrecoDoServico(precoServico, servicoExecutadoVO);
				} catch(Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
				return str.toString();
			case "Q": //Quantidade x Valor
				try {
					BigDecimal valor = getValor(baseValor, vlrPadrao, codParcMecanico, str, servicoExecutadoVO);
					BigDecimal qtd = servicoExecutadoVO.asBigDecimal("QTDNEG");
					if(qtd == null)
						throw new Exception("Quantidade/KM Rodado não está preenchido na OS!");
					if((BigDecimal.ZERO).compareTo(qtd) == 1)
						throw new Exception("Quantidade/KM Rodado está com valor negativo!");
					BigDecimal precoServico = valor.multiply(qtd);
					atualizarPrecoDoServico(precoServico, servicoExecutadoVO);
				} catch(Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
				return str.toString();
			default:
				return str.toString();
		}
	}

//	private static int calcularHorasDoServico(int hrInicial, int hrFinal) throws Exception {
//		try {
//			String dhIni = "2023-01-01 "+formatarInteiroParaHoras(hrInicial);
//			String dhFin = "2023-01-01 "+formatarInteiroParaHoras(hrFinal);
//			
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			
//			Date d1 = df.parse(dhIni);
//			Date d2 = df.parse(dhFin);
//			
//			BigDecimal diff = BigDecimal.valueOf(d2.getTime() - d1.getTime());
//		    BigDecimal diffHours = diff.divide(BigDecimal.valueOf(60 * 60 * 1000), 2, RoundingMode.HALF_UP);
//		    
//			return diffHours.multiply(BigDecimal.valueOf(60)).intValue();
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
//	}

//	private static String formatarInteiroParaHoras(int hrInicial) {
//		String hora = String.valueOf(hrInicial);
//		if(hora.length() == 4) {
//			hora = hora.substring(0, 2)+":"+hora.substring(2, hora.length());
//		} else if(hora.length() == 3) {
//			hora = "0"+hora.substring(0, 1)+":"+hora.substring(1, hora.length());
//		} else if(hora.length() == 2) {
//			hora = "00:"+hora;
//		} else if(hora.length() == 1) {
//			hora = "00:0"+hora;
//		}
//		return hora;
//	}

	private static BigDecimal getEmpresa(BigDecimal numOs) throws Exception {
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
			
			sql.appendSql("SELECT CODEMP FROM AD_TCSOSE WHERE NUMOS = "+numOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODEMP");
			} else {
				throw new Exception("Empresa não encontrada para OS "+numOs);
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

	private static BigDecimal getTipoOs(BigDecimal numOs) throws Exception {
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
			
			sql.appendSql("SELECT CODTIPODEOS FROM AD_TCSOSE WHERE NUMOS = "+numOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODTIPODEOS");
			} else {
				throw new Exception("Tipo de OS não encontrado para OS "+numOs);
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

	private static BigDecimal calcularHoras(int hrInicial, int hrFinal) {
		int horas = hrFinal - hrInicial;
		BigDecimal qtdHr = BigDecimal.valueOf(horas/100);
		int sub = qtdHr.intValue() * 40;
		double horasCalculadas = horas - sub;
		return BigDecimal.valueOf(horasCalculadas/60);
	}

	private static BigDecimal getTempoPadrao(BigDecimal codProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		int dado = 0;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_TEMPPADRAO FROM TGFPRO WHERE CODPROD = "+codProd);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getInt("AD_TEMPPADRAO");
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
		return calcularHoras(0, dado);
	}

	private static void atualizarPrecoDoServico(BigDecimal precoServico, DynamicVO servicoExecutadoVO) throws Exception {
		BigDecimal numItem = servicoExecutadoVO.asBigDecimal("NUMITEM");
		BigDecimal numOs = servicoExecutadoVO.asBigDecimal("NUMOS");
		System.out.println("Atualizando serviço da OS "+numOs+" nro "+numItem+" com o valor "+precoServico);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("TCSITE");
			DynamicVO servico = servicoExecutadoDAO.findOne("NUMITEM = "+numItem+" AND NUMOS = "+numOs);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("VALORSERVICO", precoServico)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static BigDecimal getValor(String baseValor, BigDecimal vlrPadrao, BigDecimal codParcMecanico, StringBuilder str, DynamicVO servicoExecutadoVO) throws Exception {
		try {
			switch (baseValor) {
				case "V": //Mecânico - Venda
					BigDecimal vlrMecanicoVenda = getValorVendaMecanico(codParcMecanico);
					if(vlrMecanicoVenda == null)
						throw new Exception("Valor de venda não encontrado para mecânico "+codParcMecanico+"!");
					if((BigDecimal.ZERO).compareTo(vlrMecanicoVenda) == 1)
						throw new Exception("Valor de venda para o mecânico "+codParcMecanico+" está negativo!");
					if(vlrMecanicoVenda != null)
						return vlrMecanicoVenda;
				case "S": //Serviço
					if(vlrPadrao == null)
						throw new Exception("Valor do serviço não encontrado!");
					if((BigDecimal.ZERO).compareTo(vlrPadrao) == 1)
						throw new Exception("Valor do serviço está negativo!");
					if(vlrPadrao != null)
						return vlrPadrao;
				case "M": //Manual
					str.append("Valor do serviço "+servicoExecutadoVO.asBigDecimal("CODSERV")+" é manual, o usuário deve inserir o preço manualmente na OS.\n");
					return BigDecimal.ZERO;
				case "C": //Mecânico - Custo
					BigDecimal vlrMecanicoCusto = getValorCustoMecanico(codParcMecanico);
					if(vlrMecanicoCusto == null)
						throw new Exception("Valor de custo não encontrado para mecânico "+codParcMecanico+"!");
					if((BigDecimal.ZERO).compareTo(vlrMecanicoCusto) == 1)
						throw new Exception("Valor de custo para o mecânico "+codParcMecanico+" está negativo!");
					if(vlrMecanicoCusto != null)
						return vlrMecanicoCusto;
				case "T": //Tabela tipo de OS
					BigDecimal vlrTabelaDePrecos = BigDecimal.ZERO;
					try {
						BigDecimal numOs = servicoExecutadoVO.asBigDecimal("NUMOS");
						BigDecimal tipoOs = getTipoOs(numOs);
						BigDecimal empresa = getEmpresa(numOs);
						BigDecimal codServ = servicoExecutadoVO.asBigDecimal("CODSERV");
						vlrTabelaDePrecos = coletarPrecoDaTabelaDePrecoDoServico(tipoOs, empresa, codServ);
						if(vlrTabelaDePrecos == null)
							throw new Exception("Valor não encontrado para o Serviço "+codServ+" na Tabela de Preço do Serviço!");
						if((BigDecimal.ZERO).compareTo(vlrTabelaDePrecos) == 1)
							throw new Exception("Valor negativo encontrado para o Serviço "+codServ+" na Tabela de Preço do Serviço!");
					} catch(Exception e) {
						e.printStackTrace();
						throw new Exception(e.getMessage());
					}
					return vlrTabelaDePrecos;
				default:
					return BigDecimal.ZERO;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	private static BigDecimal coletarPrecoDaTabelaDePrecoDoServico(BigDecimal tipoOs, BigDecimal empresa,
			BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT VALOR FROM AD_TABPRECOSERV WHERE CODEMP = "+empresa+" AND CODSERV = "+codServ+" AND CODTIPODEOS = "+tipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("VALOR");
			} else {
				throw new Exception("Valor não encontrado na tabela de preços para a Empresa: "+empresa+", Serviço: "+codServ+" e Tipo de OS: "+tipoOs);
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

	private static BigDecimal getValorCustoMecanico(BigDecimal codParcMecanico) throws Exception {
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
			
			sql.appendSql("SELECT AD_VLRCUSTO FROM TGFPAR WHERE CODPARC = "+codParcMecanico);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_VLRCUSTO");
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

	private static BigDecimal getValorVendaMecanico(BigDecimal codParcMecanico) throws Exception {
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
			
			sql.appendSql("SELECT AD_VLRVENDA FROM TGFPAR WHERE CODPARC = "+codParcMecanico);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_VLRVENDA");
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

	public static BigDecimal getCodParcDoUsuario(BigDecimal codUsu) throws Exception {
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
			
			sql.appendSql("SELECT CODPARC FROM TSIUSU WHERE CODUSU = "+codUsu);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODPARC");
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

	private static String getTipoCalculo(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT AD_TIPOCALCPRECO FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("AD_TIPOCALCPRECO");
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

	public static String getBaseValor(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT AD_BASEVLRHORA FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("AD_BASEVLRHORA");
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

	public static BigDecimal getValorPadrao(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT AD_VLRPADRAO FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("AD_VLRPADRAO");
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
}
