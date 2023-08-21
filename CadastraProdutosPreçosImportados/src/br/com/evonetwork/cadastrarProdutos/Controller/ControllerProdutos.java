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
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerProdutos {
	
	public static void cadastrarProdutos(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnicoImportacao = (BigDecimal) linha.getCampo("NROUNICO");
		BigDecimal nroUnicoConfig = (BigDecimal) linha.getCampo("NROUNICOCONFIG");
		String atualizarProdutos = (String) ca.getParam("ATUALIZARPROD");
		
		int qtdProdutos = 0;
		
		/* QUERY PARA COLETAR DADOS PRODUTOS */
		QueryExecutor query1 = ca.getQuery();
		query1.setParam("NROUNICO", nroUnicoImportacao);
		try {
			query1.nativeSelect("SELECT NROUNICOPRO, REFFORN, DESCRPROD, USOPROD, ORIGPROD, NCM, CODVOL FROM AD_DADOSPRODUTO WHERE NROUNICO = {NROUNICO} AND IMPORTADO = 'S' ORDER BY NROUNICOPRO");
			while(query1.next()) {
				String refForn = null;
				String descrProd = null;
				String usoProd = null;
				String origProd = null;
				String ncm = null;
				String codVol = null;
				BigDecimal codProImp = null;
				
				refForn = query1.getString("REFFORN");
				descrProd = query1.getString("DESCRPROD");
				
				usoProd = query1.getString("USOPROD");
				if(usoProd == null)
					usoProd = getUsoProdutoConfig(nroUnicoConfig, ca);
				
				origProd = query1.getString("ORIGPROD");
				if(origProd == null)
					origProd = getOrigemProdutoConfig(nroUnicoConfig, ca);
				
				if(!"0".equals(origProd)) { // ADICIONADO 27/07/2023 PARA DIFERENCIAR PRODUTOS COM ORIGENS DIFERENTES DE '0'
					String refFornAntiga = refForn;
					refForn = refForn+origProd;
					System.out.println("Atualizando REFFORN de: '"+refFornAntiga+"' para: '"+refForn+"'");
				}
				
				ncm = query1.getString("NCM");
				if(ncm == null) {
					ncm = "00000000";
				}
				
				codVol = query1.getString("CODVOL");
				if(codVol == null)
					codVol = getVolumeProdutoConfig(nroUnicoConfig, ca);

				codProImp = query1.getBigDecimal("NROUNICOPRO");
				
				//usoProd = getCodigoUsoProduto(usoProd);
				
				System.out.println("REFFORN: "+refForn+" - PROD: "+descrProd+" - NROUNICOPRO: "+codProImp);
				cadastrarAtualizarProdutos(refForn, descrProd, usoProd, origProd, ncm, codVol, ca, atualizarProdutos, nroUnicoImportacao, codProImp, nroUnicoConfig);
				
				qtdProdutos++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro(e.getMessage());
		} finally {
			query1.close();
		}
		/* ------ FIM DA QUERY ------ */
		if(qtdProdutos == 0) {
			ca.setMensagemRetorno("Nenhum produto encontrado!");
		} else {
			ca.setMensagemRetorno(qtdProdutos+" produtos cadastrados/atualizados!");
		}
	}

	private static String getVolumeProdutoConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		String retorno = "";
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
	
			sql.appendSql("SELECT CODVOL FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoConfig);
	
			rset = sql.executeQuery();
	
			if (rset.next()) {
				retorno = rset.getString("CODVOL");
			} else {
				throw new Exception("Unidade do produto não encontrada");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Unidade do produto não foi encontrada na planilha nem na configuração!");
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return retorno;
	}

	private static String getOrigemProdutoConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		String retorno = "";
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
	
			sql.appendSql("SELECT ORIGPROD FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoConfig);
	
			rset = sql.executeQuery();
	
			if (rset.next()) {
				retorno = rset.getString("ORIGPROD");
			} else {
				throw new Exception("Origem do produto não encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Origem do produto não foi encontrado na planilha nem na configuração!");
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return retorno;
	}

	private static String getUsoProdutoConfig(BigDecimal nroUnicoConfig, ContextoAcao ca) throws Exception {
		String retorno = "";
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
	
			sql.appendSql("SELECT USOPROD FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoConfig);
	
			rset = sql.executeQuery();
	
			if (rset.next()) {
				retorno = rset.getString("USOPROD");
			} else {
				throw new Exception("Uso do produto não encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Uso do produto não foi encontrado na planilha nem na configuração!");
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return retorno;
	}

//	private static String getCodigoUsoProduto(String usoProd) {
//		if("Revenda".equals(usoProd)) {
//			return "R";
//		} else if("M.P.".equals(usoProd)) {
//			return "M";
//		} else if("Venda de prod.".equals(usoProd)) {
//			return "V";
//		} else if("".equals(usoProd) || " ".equals(usoProd) || usoProd == null) {
//			return "R";
//		}
//		return "R";
//	}

	private static void cadastrarAtualizarProdutos(String refForn, String descrProd, String usoProd, String origProd, String ncm, String codVol,
			ContextoAcao ca, String atualizarProdutos, BigDecimal nroUnicoImportacao,  BigDecimal codProImp, BigDecimal nroUnicoConfig) throws Exception {
		BigDecimal codProd = null;
		BigDecimal codProdCriado = null;
		int atualizado = 0;
		
		/* QUERY PARA VERIFICAR SE PRODUTO JÁ EXISTE */
		QueryExecutor query = ca.getQuery();
		QueryExecutor queryUpdate = ca.getQuery();
		try {
			String select = "SELECT CODPROD FROM TGFPRO WHERE REFFORN = '"+refForn+"'";
			query.nativeSelect(select);
			while(query.next()) {
				codProd = query.getBigDecimal("CODPROD");
				if("S".equals(atualizarProdutos)) {
					// SE PRODUTO EXISTIR E FLAG ATUALIZAR = TRUE
					// ATUALIZAR OS DADOS
					queryUpdate.setParam("CODPROD", codProd);
					
					if(descrProd != null)
						queryUpdate.setParam("DESCRPROD", descrProd);
					else
						queryUpdate.setParam("DESCRPROD", "");
					
					if(usoProd != null)
						queryUpdate.setParam("USOPROD", usoProd);
					else
						queryUpdate.setParam("USOPROD", "");
					
					if(origProd != null)
						queryUpdate.setParam("ORIGPROD", origProd);
					else
						queryUpdate.setParam("ORIGPROD", "");
					
					if(ncm != null)
						queryUpdate.setParam("NCM", ncm);
					else
						queryUpdate.setParam("NCM", "");
					
					if(codVol != null)
						queryUpdate.setParam("CODVOL", codVol);
					else
						queryUpdate.setParam("CODVOL", "");
					
					String marca = getMarca(ca, nroUnicoConfig);
					if(marca != null)
						queryUpdate.setParam("MARCA", marca);
					else
						queryUpdate.setParam("MARCA", "");
					
					queryUpdate.setParam("CODGRUPOPROD", new BigDecimal((String) ca.getParam("CODGRUPOPROD")));
					
					queryUpdate.setParam("AD_DHIMPORTACAO", TimeUtils.getNow());
					
					queryUpdate.setParam("AD_CODUSUIMPORTACAO", ca.getUsuarioLogado());
					
					queryUpdate.setParam("ICMSGERENCIA", "S");
					
					queryUpdate.setParam("CALCULOGIRO", "G");
					
					try {
						queryUpdate.update("UPDATE TGFPRO SET DESCRPROD = {DESCRPROD}, USOPROD = {USOPROD}, ORIGPROD = {ORIGPROD}, NCM = {NCM}, CODVOL = {CODVOL}, MARCA = {MARCA}, CODGRUPOPROD = {CODGRUPOPROD}, AD_DHIMPORTACAO = {AD_DHIMPORTACAO}, AD_CODUSUIMPORTACAO = {AD_CODUSUIMPORTACAO}, ICMSGERENCIA = {ICMSGERENCIA}, CALCULOGIRO = {CALCULOGIRO} WHERE CODPROD = {CODPROD}");
					} catch (Exception e) {
						ca.mostraErro(e.getMessage());
					} finally {
						queryUpdate.close();
					}
					codProdCriado = codProd;
					atualizado = 1;
				} else {
					codProdCriado = codProd;
					atualizarProdutoImportado(nroUnicoImportacao, codProImp, codProdCriado, ca);
					return;
				}
			}
		} catch (Exception e1) {
			ca.mostraErro(e1.getMessage());
		} finally {
			query.close();
		}
		/* ------ FIM DA QUERY ------ */
		
		if(atualizado == 0) {
			// SE PRODUTO NÃO EXISTIR, REALIZAR CADASTRO
			JapeSession.SessionHandle hnd = null;
			try {
				if(descrProd == null)
					descrProd = "";
				
				if(usoProd == null)
					usoProd = "";
				
				if(origProd == null)
					origProd = "";
				
				if(ncm == null)
					ncm = "";
				
				if(codVol == null)
					codVol = "";
				
				String marca = getMarca(ca, nroUnicoConfig);
				if(marca == null)
					marca = "";
				
				hnd = JapeSession.open();
				JapeWrapper InsertPRO = JapeFactory.dao(DynamicEntityNames.PRODUTO);
				DynamicVO salvar = InsertPRO.create()
						.set("REFFORN", refForn)
						.set("DESCRPROD", descrProd)
						.set("USOPROD", usoProd)
						.set("ORIGPROD", origProd)
						.set("NCM", ncm)
						.set("CODVOL", codVol)
						.set("ATIVO", "N") // PRODUTO CADASTRADO ENTRA DESATIVADO
						.set("MARCA", marca)
						.set("CODGRUPOPROD", new BigDecimal((String) ca.getParam("CODGRUPOPROD")))
						.set("AD_DHIMPORTACAO", TimeUtils.getNow())
						.set("AD_CODUSUIMPORTACAO", ca.getUsuarioLogado())
						.set("ICMSGERENCIA", "S") //Adição à pedido do Rafael/Davi
						.set("CALCULOGIRO", "G") //Adição à pedido do Rafael/Davi
						.set("USALOCAL", "S") //Adição à pedido do Rafael/Davi
						.save();
				codProdCriado = (BigDecimal) salvar.getProperty("CODPROD");
				
				System.out.println("Produto criado: "+codProdCriado+" com REFFORN: "+refForn);
			} catch (Exception e) {
				ca.mostraErro(e.getMessage());
			} finally {
				JapeSession.close(hnd);
			}
		}
		
		atualizarProdutoImportado(nroUnicoImportacao, codProImp, codProdCriado, ca);
	}

	private static void atualizarProdutoImportado(BigDecimal nroUnicoImportacao, BigDecimal codProImp,
		BigDecimal codProdCriado, ContextoAcao ca) throws Exception {
		QueryExecutor queryUpdateImportacao = ca.getQuery();
		
		queryUpdateImportacao.setParam("NROUNICO", nroUnicoImportacao);
		queryUpdateImportacao.setParam("NROUNICOPRO", codProImp);
		queryUpdateImportacao.setParam("CODPROIMP", codProdCriado);
		try {
			queryUpdateImportacao.update("UPDATE AD_DADOSPRODUTO SET CODPROIMP = {CODPROIMP}, IMPORTADO = 'N', PRECOIMPORTADO = 'N' WHERE NROUNICO = {NROUNICO} AND NROUNICOPRO = {NROUNICOPRO}");
		} catch (Exception e) {
			ca.mostraErro(e.getMessage());
		} finally {
			queryUpdateImportacao.close();
		}
	}

	private static String getMarca(ContextoAcao ca, BigDecimal nroUnicoConfig) throws Exception {
		String marca = null;
		BigDecimal codigo = null;
		
		QueryExecutor query1 = ca.getQuery();
		query1.setParam("NROUNICOCONFIG", nroUnicoConfig);
		try {
			query1.nativeSelect("SELECT CODMARCA FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = {NROUNICOCONFIG}");
			while(query1.next()) {
				codigo = query1.getBigDecimal("CODMARCA");
			}
		} catch (Exception e) {
			ca.mostraErro(e.getMessage());
		} finally {
			query1.close();
		}
		
		QueryExecutor query2 = ca.getQuery();
		query2.setParam("CODIGO", codigo);
		try {
			query2.nativeSelect("SELECT DESCRICAO FROM TGFMAR WHERE CODIGO = {CODIGO}");
			while(query2.next()) {
				marca = query2.getString("DESCRICAO");
			}
		} catch (Exception e) {
			ca.mostraErro(e.getMessage());
		} finally {
			query2.close();
		}
		
		return marca;
	}
}
