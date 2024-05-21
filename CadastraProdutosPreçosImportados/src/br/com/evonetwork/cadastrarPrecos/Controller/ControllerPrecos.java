package br.com.evonetwork.cadastrarPrecos.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

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
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class ControllerPrecos {
	
	private static BigDecimal nuTab = null;
	private static BigDecimal nuTabela = null;
	private static int qtdPrecos = 0;
	private static int qtdLinhas = 0;

	public static void cadastrarPrecos(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnicoImportacao = (BigDecimal) linha.getCampo("NROUNICO");
		BigDecimal nroUnicoCofig = (BigDecimal) linha.getCampo("NROUNICOCONFIG");
		System.out.println("NuConfig: "+nroUnicoCofig);
		StringBuilder logImportacao = new StringBuilder();
		BigDecimal codTab = getTabelaDePrecos(nroUnicoImportacao);
		System.out.println("Cód. tabela da configuração: "+codTab);
		if (codTab == null)
			throw new Exception("Cód. da Tabela de preço não foi encontrado na configuração da importação!");
		int qtdLinhas = MGECoreParameter.getParameterAsInt("QTDLINHASIMPPRO");
		int qtdLinhasTotal = buscarQuantidadeTotalDeLinhas(nroUnicoImportacao, ca);
		if(qtdLinhasTotal == 0) {
			ca.setMensagemRetorno("Nenhuma linha restante para importar!");
			return;
		}
		while (qtdLinhasTotal > 0) {
			SessionHandle hnd = null;
			try {
				hnd = JapeSession.open();
				hnd.setCanTimeout(false);
				hnd.execWithTX( new JapeSession.TXBlock() {
					public void doWithTx() throws Exception {
						JdbcWrapper jdbc = null;
						NativeSql sql = null;
						ResultSet rset = null;
						BigDecimal cusVariavel = null;
						BigDecimal cusRep = null;
						BigDecimal cusMed = null;
						BigDecimal cusGer = null;
						BigDecimal cusSemIcm = null;
						BigDecimal cusMedIcm = null;
						BigDecimal vlrVenda = null;
						BigDecimal codEmp = null;
						BigDecimal entComIcm = null;
						BigDecimal entSemIcm = null;
						BigDecimal codProImp = null;
						BigDecimal codPro = null;
						boolean importarDadosCusto = false;
						boolean importarDadosPreco = false;
						try {
							EntityFacade entity = EntityFacadeFactory.getDWFFacade();
							jdbc = entity.getJdbcWrapper();
							jdbc.openSession();
							
							sql = new NativeSql(jdbc);
							
							sql.appendSql("SELECT NROUNICOPRO, CODPROIMP FROM AD_DADOSPRODUTO WHERE NROUNICO = "+nroUnicoImportacao+" AND IMPORTADO = 'N' AND NVL(PRECOIMPORTADO, 'N') = 'N' AND ROWNUM <= "+qtdLinhas+"  ORDER BY NROUNICOPRO");
							System.out.println("SQL: "+sql.toString());
							
							rset = sql.executeQuery();
							
							while(rset.next()) {
								codProImp = rset.getBigDecimal("NROUNICOPRO");
								codPro = rset.getBigDecimal("CODPROIMP");
								JdbcWrapper jdbc2 = null;
								NativeSql sql2 = null;
								ResultSet rset2 = null;
								try {
									EntityFacade entity2 = EntityFacadeFactory.getDWFFacade();
									jdbc2 = entity2.getJdbcWrapper();
									jdbc2.openSession();
									
									sql2 = new NativeSql(jdbc2);
									
									sql2.appendSql("SELECT VLRVENDA FROM AD_DADOSEXCECAO WHERE NROUNICOPRO = "+codProImp+" AND NROUNICO = "+nroUnicoImportacao);
									System.out.println("SQL: "+sql2.toString());
									
									rset2 = sql2.executeQuery();
									
									while(rset2.next()) {
										importarDadosPreco = true;
										vlrVenda = rset2.getBigDecimal("VLRVENDA");
										if (vlrVenda == null)
											vlrVenda = BigDecimal.ZERO;
									}
								} catch (Exception e) {
									e.printStackTrace();
									throw new Exception(e.getMessage());
								} finally {
									rset2.getStatement().close();
									rset2.close();
									JdbcUtils.closeResultSet(rset2);
									NativeSql.releaseResources(sql2);
									JdbcWrapper.closeSession(jdbc2);
								}
								JdbcWrapper jdbc3 = null;
								NativeSql sql3 = null;
								ResultSet rset3 = null;
								try {
									boolean atualizaCusMedIcm = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTCUSMEDICM", nroUnicoCofig));
									boolean atualizaCusSemIcm = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTCUSSEMICM", nroUnicoCofig));
									boolean atualizaCusRep = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTCUSREP", nroUnicoCofig));
									boolean atualizaCusVariavel = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTCUSVARIAVEL", nroUnicoCofig));
									boolean atualizaCusGer = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTCUSGER", nroUnicoCofig));
									boolean atualizaCusMed = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTCUSMED", nroUnicoCofig));
									boolean atualizaEntComIcm = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTENTRADACOMICMS", nroUnicoCofig));
									boolean atualizaEntSemIcm = parametroParaBoolean(buscarParametroDaConfiguracao(ca, "ATTENTRADASEMICMS", nroUnicoCofig));
									EntityFacade entity2 = EntityFacadeFactory.getDWFFacade();
									jdbc3 = entity2.getJdbcWrapper();
									jdbc3.openSession();
									
									sql3 = new NativeSql(jdbc3);
									
									sql3.appendSql("SELECT ENTRADACOMICMS, ENTRADASEMICMS, CUSVARIAVEL, CUSREP, CUSMED, CUSGER, CUSSEMICM, CUSMEDICM, CODEMP FROM AD_DADOSCUSTO WHERE NROUNICOPRO = "+codProImp+" AND NROUNICO = "+nroUnicoImportacao);
									System.out.println("SQL: "+sql3.toString());
									
									rset3 = sql3.executeQuery();
									
									while(rset3.next()) {
										importarDadosCusto = true;
										System.out.println("Resultado encontrado");
										codEmp = rset3.getBigDecimal("CODEMP");
										if (codEmp == null)
											codEmp = BigDecimal.ONE;
										
										cusVariavel = rset3.getBigDecimal("CUSVARIAVEL");
										if (cusVariavel == null && atualizaCusVariavel)
											cusVariavel = buscarCustoAnterior(ca, codPro, codEmp, "CUSVARIAVEL");
										else if (cusVariavel == null)
											cusVariavel = BigDecimal.ZERO;
										
										cusRep = rset3.getBigDecimal("CUSREP");
										if (cusRep == null && atualizaCusRep)
											cusRep = buscarCustoAnterior(ca, codPro, codEmp, "CUSREP");
										else if (cusRep == null)
											cusRep = BigDecimal.ZERO;
										
										cusMed = rset3.getBigDecimal("CUSMED");
										if (cusMed == null && atualizaCusMed)
											cusMed = buscarCustoAnterior(ca, codPro, codEmp, "CUSMED");
										else if (cusMed == null)
											cusMed = BigDecimal.ZERO;
	
										cusGer = rset3.getBigDecimal("CUSGER");
										if (cusGer == null && atualizaCusGer)
											cusGer = buscarCustoAnterior(ca, codPro, codEmp, "CUSGER");
										else if (cusGer == null)
											cusGer = BigDecimal.ZERO;
	
										entComIcm = rset3.getBigDecimal("ENTRADACOMICMS");
										if (entComIcm == null && atualizaEntComIcm)
											entComIcm = buscarCustoAnterior(ca, codPro, codEmp, "ENTRADACOMICMS");
										else if (entComIcm == null)
											entComIcm = BigDecimal.ZERO;
	
										entSemIcm = rset3.getBigDecimal("ENTRADASEMICMS");
										if (entSemIcm == null && atualizaEntSemIcm)
											entSemIcm = buscarCustoAnterior(ca, codPro, codEmp, "ENTRADASEMICMS");
										else if (entSemIcm == null)
											entSemIcm = BigDecimal.ZERO;
	
										cusSemIcm = rset3.getBigDecimal("CUSSEMICM");
										if (cusSemIcm == null && atualizaCusSemIcm)
											cusSemIcm = buscarCustoAnterior(ca, codPro, codEmp, "CUSSEMICM");
										else if (cusSemIcm == null)
											cusSemIcm = BigDecimal.ZERO;
	
										cusMedIcm = rset3.getBigDecimal("CUSMEDICM");
										if (cusMedIcm == null && atualizaCusMedIcm) {
											cusMedIcm = buscarCustoAnterior(ca, codPro, codEmp, "CUSMEDICM");
										} else {
											if (cusMedIcm != null)
												continue;
											cusMedIcm = BigDecimal.ZERO;
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
									throw new Exception(e.getMessage());
								} finally {
									rset3.getStatement().close();
									rset3.close();
									JdbcUtils.closeResultSet(rset3);
									NativeSql.releaseResources(sql3);
									JdbcWrapper.closeSession(jdbc3);
								}
								System.out.println("CodPro: "+codPro);
								System.out.println("Importar custo: "+importarDadosCusto);
								System.out.println("Importar preços: "+importarDadosPreco);
								if(importarDadosCusto)
									cadastrarAtualizarPrecosTGFCUS(ca, cusVariavel, cusRep, codPro, cusMed, cusGer, cusSemIcm, cusMedIcm, codEmp, logImportacao);
								BigDecimal nuTab = criarOuRetornaRegistroTGFTAB(codTab, ca);
								System.out.println("Os preços serão cadastrados na tabela: "+nuTab);
								if(importarDadosPreco)
									cadastrarAtualizarPrecosTGFEXC(ca, vlrVenda, codPro, nuTab, logImportacao);
								if(importarDadosCusto || importarDadosPreco)
									atualizarPrecoImportado(nroUnicoImportacao, codProImp, ca);
								qtdPrecos += 1;
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new Exception(e.getMessage());
						} finally {
							rset.getStatement().close();
							rset.close();
							JdbcUtils.closeResultSet(rset);
							NativeSql.releaseResources(sql);
							JdbcWrapper.closeSession(jdbc);
						}
					}
				});
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			} finally {
				JapeSession.close(hnd);
			}
			qtdLinhasTotal = buscarQuantidadeTotalDeLinhas(nroUnicoImportacao, ca);
			System.out.println("Qtd. linhas totais: "+qtdLinhasTotal);
		}
		atualizarLogImportacao(nroUnicoImportacao, logImportacao);
		if (qtdPrecos == 0) {
			ca.setMensagemRetorno("Nenhum preço encontrado!");
		} else {
			ca.setMensagemRetorno(qtdPrecos+" preços cadastrados!");
		}
	}

	private static int buscarQuantidadeTotalDeLinhas(BigDecimal nroUnicoImportacao, ContextoAcao ca) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.execWithTX( new JapeSession.TXBlock() {
				public void doWithTx() throws Exception {
					JdbcWrapper jdbc = null;
					NativeSql sql = null;
					ResultSet rset = null;
					try {
						EntityFacade entity = EntityFacadeFactory.getDWFFacade();
						jdbc = entity.getJdbcWrapper();
						jdbc.openSession();
						
						sql = new NativeSql(jdbc);
						
						sql.appendSql("SELECT COUNT(*) AS QTDLINHAS FROM AD_DADOSPRODUTO WHERE NROUNICO = "+nroUnicoImportacao+" AND IMPORTADO = 'N' AND NVL(PRECOIMPORTADO, 'N') = 'N' ORDER BY NROUNICOPRO");
						System.out.println("SQL: "+sql.toString());
						
						rset = sql.executeQuery();
						
						if(rset.next())
							qtdLinhas = (rset.getBigDecimal("QTDLINHAS")).intValue();
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception(e.getMessage());
					} finally {
						rset.getStatement().close();
						rset.close();
						JdbcUtils.closeResultSet(rset);
						NativeSql.releaseResources(sql);
						JdbcWrapper.closeSession(jdbc);
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return qtdLinhas;
	}

	private static String buscarParametroDaConfiguracao(ContextoAcao ca, String parametro, BigDecimal nroUnicoCofig) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		String config = null;
		try {
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT "+parametro+" FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoCofig);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next())
				config = rset.getString(parametro);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			rset.getStatement().close();
			rset.close();
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
		}
		return config;
	}

	private static BigDecimal buscarCustoAnterior(ContextoAcao ca, BigDecimal codPro, BigDecimal codEmp, String nomeCampo) throws Exception {
		System.out.println("Buscando custo '"+nomeCampo+"' anterior para produto "+codPro);
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		BigDecimal custoAnterior = BigDecimal.ZERO;
		try {
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT "+nomeCampo+" FROM TGFCUS WHERE CODPROD = "+codPro+" AND CODEMP = "+codEmp+" AND DTATUAL < '"+convertDate(TimeUtils.getNow().toString())+"' AND PROCESSO <> 'br.com.sankhya.menu.adicional.AD_IMPPRO' AND ROWNUM = 1 ORDER BY DTATUAL DESC");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next())
				custoAnterior = rset.getBigDecimal(nomeCampo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao buscar custo anterior do produto "+codPro+": "+e.getMessage());
		} finally {
			rset.getStatement().close();
			rset.close();
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
		}
		return custoAnterior;
	}

	private static boolean parametroParaBoolean(String param) {
		return "S".equals(param);
	}

	private static void atualizarLogImportacao(BigDecimal nroUnicoImportacao, StringBuilder logImportacao) throws Exception {
		System.out.println("Atualizar Log Importacao: "+nroUnicoImportacao+", "+logImportacao);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.execWithTX(new JapeSession.TXBlock() {
				public void doWithTx() throws Exception {
					JapeFactory.dao("AD_IMPPRO").prepareToUpdateByPK(nroUnicoImportacao)
					.set("LOGIMP", logImportacao.toString().toCharArray())
					.update();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static void atualizarPrecoImportado(BigDecimal nroUnicoImportacao, BigDecimal codProImp, ContextoAcao ca) throws Exception {
		JapeWrapper dadosProdutoDAO = JapeFactory.dao("AD_DADOSPRODUTO");
		DynamicVO dadosProdVO = dadosProdutoDAO.findOne("NROUNICO = "+nroUnicoImportacao+" AND NROUNICOPRO = "+codProImp);
		dadosProdutoDAO.prepareToUpdate(dadosProdVO)
			.set("PRECOIMPORTADO", "S")
			.update();
	}

	private static BigDecimal criarOuRetornaRegistroTGFTAB(BigDecimal codTab, ContextoAcao ca) throws Exception {
		nuTab = existeRegistro(ca, codTab);
		if (nuTab != null)
			return nuTab;
		System.out.println("Não existe TGFTAB, criando uma nova (codTab: "+codTab+")...");
		JapeWrapper tgftabDAO = JapeFactory.dao("TabelaPreco");
		DynamicVO save = tgftabDAO.create()
			.set("CODTAB", codTab)
			.set("DTVIGOR", TimeUtils.getNow())
			.set("DTALTER", TimeUtils.getNow())
			.set("CODTABORIG", codTab)
			.save();
		nuTab = save.asBigDecimal("NUTAB");
		return nuTab;
	}

	private static BigDecimal existeRegistro(ContextoAcao ca, BigDecimal codTab) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		try {
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT NUTAB FROM TGFTAB WHERE TRUNC(DTVIGOR) = '"+convertDate(TimeUtils.getNow().toString())+"' AND CODTAB = "+codTab);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next())
				return rset.getBigDecimal("NUTAB");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			rset.getStatement().close();
			rset.close();
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
		}
		return null;
	}

	private static String convertDate(String mDate) {
		if (mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		try {
			Date newDate = inputFormat.parse(mDate);
			inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			mDate = inputFormat.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDate;
	}

	private static String convertDateFull(String mDate) {
		if (mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		try {
			Date newDate = inputFormat.parse(mDate);
			inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
			mDate = inputFormat.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDate;
	}

	private static BigDecimal getTabelaDePrecos(BigDecimal nroUnicoImportacao) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.execWithTX(new JapeSession.TXBlock() {
				public void doWithTx() throws Exception {
					JapeWrapper impProDAO = JapeFactory.dao("AD_IMPPRO");
					DynamicVO impProVO = impProDAO.findByPK(new Object[] { nroUnicoImportacao });
					JapeWrapper configDAO = JapeFactory.dao("AD_CONFIGIMPPRO");
					DynamicVO configVO = configDAO.findByPK(new Object[] { impProVO.asBigDecimal("NROUNICOCONFIG") });
					nuTabela = configVO.asBigDecimal("CODTAB");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return nuTabela;
	}

	private static void cadastrarAtualizarPrecosTGFEXC(ContextoAcao ca, BigDecimal vlrVenda, BigDecimal codPro, BigDecimal nuTab, StringBuilder logImportacao) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		try {
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT * FROM TGFEXC WHERE CODPROD = "+codPro+" AND NUTAB = "+nuTab+" AND CODLOCAL = 0 AND CONTROLE = ' '");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while(rset.next()) {
				logImportacao.append("Preço (TGFEXC) já existe para o CODPROD: "+codPro+" e Tabela: "+nuTab+" para a Data: "+convertDate(TimeUtils.getNow().toString())+"\n");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			rset.getStatement().close();
			rset.close();
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
		}
		JapeWrapper InsertEXC = JapeFactory.dao("Excecao");
		InsertEXC.create()
			.set("CODPROD", codPro)
			.set("VLRVENDA", vlrVenda)
			.set("NUTAB", nuTab)
			.set("CODLOCAL", BigDecimal.ZERO)
			.set("CONTROLE", " ")
			.save();
	}

	private static void cadastrarAtualizarPrecosTGFCUS(ContextoAcao ca, BigDecimal cusVariavel, BigDecimal cusRep, BigDecimal codPro, BigDecimal cusMed, BigDecimal cusGer, BigDecimal cusSemIcm, BigDecimal cusMedIcm, BigDecimal codEmp, StringBuilder logImportacao) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		try {
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT * FROM TGFCUS WHERE CODPROD = "+codPro+" AND CODEMP = "+codEmp+" AND (DTATUAL >= TO_DATE('"+convertDate(TimeUtils.getNow().toString())+" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') AND DTATUAL <= TO_DATE('"+convertDate(TimeUtils.getNow().toString())+" 23:59:59', 'DD/MM/YYYY HH24:MI:SS')) AND CODLOCAL = 0 AND CONTROLE = ' '");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while(rset.next()) {
				logImportacao.append("Custo (TGFCUS) já existe para o CODPROD: "+codPro+" e Empresa: "+codEmp+" para a Data: "+convertDateFull(TimeUtils.getNow().toString())+"\n");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			System.out.println("Fechando result set cadastrarAtualizarPrecosTGFCUS");
			rset.getStatement().close();
			rset.close();
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
		}
		JapeWrapper InsertCUS = JapeFactory.dao("Custo");
		InsertCUS.create()
			.set("CODPROD", codPro)
			.set("CODEMP", codEmp)
			.set("DTATUAL", TimeUtils.getNow())
			.set("CODLOCAL", BigDecimal.ZERO)
			.set("CONTROLE", " ")
			.set("NUNOTA", BigDecimal.ZERO)
			.set("SEQUENCIA", BigDecimal.ZERO)
			.set("CUSVARIAVEL", cusVariavel)
			.set("CUSREP", cusRep)
			.set("CUSMED", cusMed)
			.set("CUSGER", cusGer)
			.set("CUSSEMICM", cusSemIcm)
			.set("CUSMEDICM", cusMedIcm)
			.set("AUTOMATICO", "N")
			.set("CUSMEDCALC", "N")
			.save();
	}
}
