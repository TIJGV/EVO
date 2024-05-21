package br.com.evonetwork.cadastrarProdutos.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
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

public class ControllerProdutos {
	
	private static int qtdProdutos = 0;
	private static int qtdLinhas = 0;

	public static void cadastrarProdutos(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnicoImportacao = (BigDecimal) linha.getCampo("NROUNICO");
		BigDecimal nroUnicoConfig = (BigDecimal) linha.getCampo("NROUNICOCONFIG");
		String atualizarProdutos = (String) ca.getParam("ATUALIZARPROD");
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
						try {
							EntityFacade entity = EntityFacadeFactory.getDWFFacade();
							jdbc = entity.getJdbcWrapper();
							jdbc.openSession();
							
							sql = new NativeSql(jdbc);
							
							sql.appendSql("SELECT * FROM AD_DADOSPRODUTO WHERE NROUNICO = "+nroUnicoImportacao+" AND IMPORTADO = 'S' AND ROWNUM <= "+qtdLinhas+" ORDER BY NROUNICOPRO");
							System.out.println("SQL: "+sql.toString());
							
							rset = sql.executeQuery();
							
							while(rset.next()) {
								String refForn = rset.getString("REFFORN");
								String descrProd = rset.getString("DESCRPROD");
								String usoProd = rset.getString("USOPROD");
								String origProd = rset.getString("ORIGPROD");
								String ncm = rset.getString("NCM");
								String codVol = rset.getString("CODVOL");
								BigDecimal codProImp = rset.getBigDecimal("NROUNICOPRO");
								BigDecimal CodSTIEnt = rset.getBigDecimal("CSTIPIENT");
								BigDecimal CodSTISai = rset.getBigDecimal("CSTIPISAI");
								BigDecimal qtdEmbalagem = rset.getBigDecimal("QTDEMB");
								if (usoProd == null)
									usoProd = getUsoProdutoConfig(nroUnicoConfig, ca);
								if (origProd == null)
									origProd = getOrigemProdutoConfig(nroUnicoConfig, ca);
								if (refForn == null || refForn.isEmpty()) {
									System.out.println("Produto sem referência do fornecedor");
									removerProdutoDaImportacao(nroUnicoImportacao, codProImp);
								} else {
									if (ncm == null)
										ncm = "00000000";
									if (codVol == null)
										codVol = getVolumeProdutoConfig(nroUnicoConfig, ca);
									if (CodSTIEnt == null)
										CodSTIEnt = getCodSTIEntradaConfig(nroUnicoConfig, ca);
									if (CodSTISai == null)
										CodSTISai = getCodSTISaidaConfig(nroUnicoConfig, ca);
									if (descrProd == null || descrProd.isEmpty()) {
										System.out.println("Produto sem descrição");
										removerProdutoDaImportacao(nroUnicoImportacao, codProImp);
									} else {
										System.out.println("REFFORN: "+refForn+" - PROD: "+descrProd+" - NROUNICOPRO: "+codProImp);
										cadastrarAtualizarProdutos(refForn, descrProd, usoProd, origProd, ncm, codVol, ca, atualizarProdutos, nroUnicoImportacao, codProImp, nroUnicoConfig, CodSTIEnt, CodSTISai, qtdEmbalagem);
										qtdProdutos += 1;
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new Exception(e.getMessage());
						} finally {
							System.out.println("Fechando result set cadastrarProdutos");
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
		if (qtdProdutos == 0)
			ca.setMensagemRetorno("Nenhum produto encontrado!");
		else
			ca.setMensagemRetorno(String.valueOf(qtdProdutos)+" produtos cadastrados/atualizados!");
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
						
						sql.appendSql("SELECT COUNT(*) AS QTDLINHAS FROM AD_DADOSPRODUTO WHERE NROUNICO = "+nroUnicoImportacao+" AND IMPORTADO = 'S' ORDER BY NROUNICOPRO");
						System.out.println("SQL: "+sql.toString());
						
						rset = sql.executeQuery();
						
						if(rset.next())
							qtdLinhas = (rset.getBigDecimal("QTDLINHAS")).intValue();
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception(e.getMessage());
					} finally {
						System.out.println("Fechando result set buscarQuantidadeTotalDeLinhas");
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

	private static void removerProdutoDaImportacao(BigDecimal nroUnicoImportacao, BigDecimal codProImp) throws Exception {
		System.out.println("Removendo produto: "+nroUnicoImportacao+", "+codProImp);
		JapeWrapper configDao = JapeFactory.dao("AD_DADOSPRODUTO");
		configDao.deleteByCriteria("NROUNICO = "+nroUnicoImportacao+" AND NROUNICOPRO = "+codProImp);
	}

	private static BigDecimal getCodSTISaidaConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		BigDecimal retorno = null;
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		DynamicVO configuracaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("AD_CONFIGIMPPRO", nroUnicoConfig);
		retorno = configuracaoVO.asBigDecimal("CSTIPISAI");
		if(retorno == null)
			throw new Exception("Código Sit.Trib.IPI Saida não encontrado");
		return retorno;
	}

	private static BigDecimal getCodSTIEntradaConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		BigDecimal retorno = null;
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		DynamicVO configuracaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("AD_CONFIGIMPPRO", nroUnicoConfig);
		retorno = configuracaoVO.asBigDecimal("CSTIPIENT");
		if(retorno == null)
			throw new Exception("Código Sit.Trib.IPI Entrada não encontrado");
		return retorno;
	}

	private static String getVolumeProdutoConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		String retorno = "";
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		DynamicVO configuracaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("AD_CONFIGIMPPRO", nroUnicoConfig);
		retorno = configuracaoVO.asString("CODVOL");
		if("".equals(retorno))
			throw new Exception("Unidade do produto não encontrado");
		return retorno;
	}

	private static String getOrigemProdutoConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		String retorno = "";
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		DynamicVO configuracaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("AD_CONFIGIMPPRO", nroUnicoConfig);
		retorno = configuracaoVO.asString("ORIGPROD");
		if("".equals(retorno))
			throw new Exception("Origem do produto não encontrado");
		return retorno;
	}

	private static String getUsoProdutoConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		String retorno = "";
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		DynamicVO configuracaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("AD_CONFIGIMPPRO", nroUnicoConfig);
		retorno = configuracaoVO.asString("USOPROD");
		if("".equals(retorno))
			throw new Exception("Uso do produto não encontrado");
		return retorno;
	}

	private static void cadastrarAtualizarProdutos(String refForn, String descrProd, String usoProd, String origProd, String ncm, String codVol, ContextoAcao ca, String atualizarProdutos, BigDecimal nroUnicoImportacao, BigDecimal codProImp, BigDecimal nroUnicoConfig, BigDecimal codSTIEnt, BigDecimal codSTISai, BigDecimal qtdEmbalagem) throws Exception {
		BigDecimal codProd = null;
		BigDecimal codProdCriado = null;
		int atualizado = 0;
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		try {
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPROD FROM TGFPRO WHERE REFFORN = '"+refForn+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while(rset.next()) {
				codProd = rset.getBigDecimal("CODPROD");
				if (!"S".equals(atualizarProdutos)) {
					codProdCriado = codProd;
					System.out.println("Produto já existe: "+codProdCriado+" com REFFORN: "+refForn);
					atualizarProdutoImportado(nroUnicoImportacao, codProImp, codProdCriado);
					return;
				}
				QueryExecutor queryUpdate = ca.getQuery();
				queryUpdate.setParam("CODPROD", codProd);
				
				if (descrProd != null)
					queryUpdate.setParam("DESCRPROD", descrProd);
				else
					queryUpdate.setParam("DESCRPROD", "");
				
				if (usoProd != null)
					queryUpdate.setParam("USOPROD", usoProd);
				else
					queryUpdate.setParam("USOPROD", "");

				if (origProd != null)
					queryUpdate.setParam("ORIGPROD", origProd);
				else
					queryUpdate.setParam("ORIGPROD", "");

				if (ncm != null)
					queryUpdate.setParam("NCM", ncm);
				else
					queryUpdate.setParam("NCM", "");

				if (codVol != null)
					queryUpdate.setParam("CODVOL", codVol);
				else
					queryUpdate.setParam("CODVOL", "");

				String marca = getMarca(ca, nroUnicoConfig);
				if (marca != null)
					queryUpdate.setParam("MARCA", marca);
				else
					queryUpdate.setParam("MARCA", "");

				if (codSTIEnt != null)
					queryUpdate.setParam("CSTIPIENT", codSTIEnt);
				else
					queryUpdate.setParam("CSTIPIENT", "");

				if (codSTISai != null)
					queryUpdate.setParam("CSTIPISAI", codSTISai);
				else
					queryUpdate.setParam("CSTIPISAI", "");
				
				if (qtdEmbalagem != null)
					queryUpdate.setParam("QTDEMB", qtdEmbalagem);
				else
					queryUpdate.setParam("QTDEMB", "");
				
				queryUpdate.setParam("AD_NROIMPORTACAO", nroUnicoImportacao);
				queryUpdate.setParam("CODGRUPOPROD", new BigDecimal((String) ca.getParam("CODGRUPOPROD")));
				queryUpdate.setParam("AD_DHIMPORTACAO", TimeUtils.getNow());
				queryUpdate.setParam("AD_CODUSUIMPORTACAO", ca.getUsuarioLogado());
				queryUpdate.setParam("ICMSGERENCIA", "S");
				queryUpdate.setParam("CALCULOGIRO", "G");
				try {
					queryUpdate.update("UPDATE TGFPRO SET AD_NROIMPORTACAO = {AD_NROIMPORTACAO}, QTDEMB = {QTDEMB}, DESCRPROD = {DESCRPROD}, USOPROD = {USOPROD}, ORIGPROD = {ORIGPROD}, NCM = {NCM}, CODVOL = {CODVOL}, MARCA = {MARCA}, CODGRUPOPROD = {CODGRUPOPROD}, AD_DHIMPORTACAO = {AD_DHIMPORTACAO}, AD_CODUSUIMPORTACAO = {AD_CODUSUIMPORTACAO}, ICMSGERENCIA = {ICMSGERENCIA}, CALCULOGIRO = {CALCULOGIRO}, CSTIPIENT = {CSTIPIENT}, CSTIPISAI = {CSTIPISAI} WHERE CODPROD = {CODPROD}");
					System.out.println("UPD TGFPRO WHERE CODPROD = "+codProd);
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
				queryUpdate.close();
				codProdCriado = codProd;
				atualizado = 1;
				System.out.println("Produto atualizado: "+codProdCriado+" com REFFORN: "+refForn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			System.out.println("Fechando result set cadastrarAtualizarProdutos");
			rset.getStatement().close();
			rset.close();
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
		}
		if (atualizado == 0) {
			System.out.println("Criando produto: "+refForn);
			String marca = getMarca(ca, nroUnicoConfig);
			JapeWrapper InsertPRO = JapeFactory.dao("Produto");
			DynamicVO salvar = InsertPRO.create()
					.set("REFFORN", refForn)
					.set("DESCRPROD", descrProd)
					.set("USOPROD", usoProd)
					.set("ORIGPROD", origProd)
					.set("NCM", ncm)
					.set("CODVOL", codVol)
					.set("ATIVO", "N")
					.set("MARCA", marca)
					.set("CODGRUPOPROD", new BigDecimal((String) ca.getParam("CODGRUPOPROD")))
					.set("AD_DHIMPORTACAO", TimeUtils.getNow())
					.set("AD_CODUSUIMPORTACAO", ca.getUsuarioLogado())
					.set("ICMSGERENCIA", "S")
					.set("CALCULOGIRO", "G")
					.set("USALOCAL", "S")
					.set("CSTIPIENT", codSTIEnt)
					.set("CSTIPISAI", codSTISai)
					.set("AD_NROIMPORTACAO", nroUnicoImportacao)
					.save();
			codProdCriado = (BigDecimal) salvar.getProperty("CODPROD");
			System.out.println("Produto criado: "+codProdCriado+" com REFFORN: "+refForn);
		}
		atualizarProdutoImportado(nroUnicoImportacao, codProImp, codProdCriado);
	}

	private static void atualizarProdutoImportado(BigDecimal nroUnicoImportacao, BigDecimal codProImp, BigDecimal codProdCriado) throws Exception {
		System.out.println("Atualizar produto importado: "+nroUnicoImportacao+", "+codProImp+", "+codProdCriado);
		JapeWrapper dadosProdutoDAO = JapeFactory.dao("AD_DADOSPRODUTO");
		DynamicVO dadosProduto = dadosProdutoDAO.findOne("NROUNICO = "+nroUnicoImportacao+" AND NROUNICOPRO = "+codProImp);
		dadosProdutoDAO.prepareToUpdate(dadosProduto)
			.set("CODPROIMP", codProdCriado)
			.set("IMPORTADO", "N")
			.set("PRECOIMPORTADO", "N")
			.update();
	}

	private static String getMarca(ContextoAcao ca, BigDecimal nroUnicoConfig) throws Exception {
		BigDecimal codigo = null;
		String retorno = "";
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		DynamicVO configuracaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("AD_CONFIGIMPPRO", nroUnicoConfig);
		codigo = configuracaoVO.asBigDecimal("CODMARCA");
		if(codigo == null)
			throw new Exception("Marca não encontrada na configuração da importação!");
		DynamicVO marcaVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("MarcaProduto", codigo);
		retorno = marcaVO.asString("DESCRICAO");
		if("".equals(retorno))
			throw new Exception("Descrição da marca "+codigo+" não encontrada");
		return retorno;
	}
}
