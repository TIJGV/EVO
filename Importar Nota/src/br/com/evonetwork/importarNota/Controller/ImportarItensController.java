package br.com.evonetwork.importarNota.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.importarNota.Model.CamposImportacao;
import br.com.evonetwork.importarNota.Model.FileInformation;
import br.com.evonetwork.importarNota.Utils.Utils;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ImportarItensController {
	
	static ArrayList<CamposImportacao> camposImp = new ArrayList<>();

	public static void iniciarImportacao(Registro linha, ContextoAcao ca) throws Exception {
		JapeWrapper impNotaDao = JapeFactory.dao("AD_IMPNOTA");
		BigDecimal nroUnico = (BigDecimal) linha.getCampo("NUNICO");
		BigDecimal nroUnicoConfig = (BigDecimal) linha.getCampo("NUCONFIG");
		BigDecimal nuNota = (BigDecimal) linha.getCampo("NUNOTA");
		DynamicVO imp = null;
		StringBuilder retorno = new StringBuilder();
		int importados = 0;
		try {
            imp = impNotaDao.findByPK(nroUnico);
            byte[] upload = imp.asBlob("ARQUIVO");
            if (upload == null) {
            	retorno.append("Arquivo Inválido! Aconteceu um problema na leitura do Arquivo!\n");
            }
            String contentInfo = new String(upload, StandardCharsets.ISO_8859_1);
            FileInformation fileInformation = extrairJson(contentInfo);
            String partes[] = fileInformation.getName().split("\\.");
            if("CSV".equals((partes[partes.length-1]).toUpperCase())) {
            	importados = lerArquivoCSV(upload, nroUnico, nroUnicoConfig, retorno, nuNota);
            } else {
            	retorno.append("Importação configurada apenas para arquivos .csv!\n");
            }
        } catch (Exception e) {
        	e.printStackTrace();
			throw new Exception(e.getMessage());
        }
		if(importados == 0) {
			retorno.append("Nenhuma linha foi encontrada.\n");
		} else {
			retorno.append("Arquivo importado! Foram criados "+importados+" registros.\n");
		}
		atualizarCabecalhoImportacao(nroUnico, retorno, ca.getUsuarioLogado());
	}

	private static int lerArquivoCSV(byte[] bytes, BigDecimal nroUnico, BigDecimal nroUnicoConfig, StringBuilder retorno, BigDecimal nuNota) throws Exception {
		String str = new String(bytes);
		String[] splitted = Arrays.stream(str.split("\n")).map(String::trim).toArray(String[]::new);
		List<String> list = Arrays.asList(splitted);
		BigDecimal codProd = null;
		String refForn = "";
		
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		
		getCamposColunas(campo, tabela, coluna, tipoCampo, nroUnicoConfig);
		
		int linhaInicial = 1;
        int linhasLidas = 0;
        int contadorDeImportacoes = 0;
        int linhaAtual = linhaInicial;
        
        System.out.println("Linha atual: "+linhaAtual);
        boolean removeCabecalho = true;
		for (String string : list) {
			String[] line = Arrays.stream(string.split(";")).map(String::trim).toArray(String[]::new);
			if (removeCabecalho) {
				removeCabecalho = false;
				continue;
			}
			camposImp = new ArrayList<>();
    		try {
    			for(int j=0; j < coluna.size(); j++) {
    				String conteudoString = null;
    				BigDecimal conteudoNumero = null;
    				Timestamp conteudoData = null;
    				int conteudo = 0;
    				int numColuna = 0;
    				int calcularLinha = 0;
    				
    				if(coluna.get(j).length() == 1) {
	    				char ch = coluna.get(j).charAt(0);
	    				numColuna = (int) ch - 65; 					// transformado char em inteiro (A = 0, B = 1, C = 2, ...)
    				} else if(coluna.get(j).length() > 1) {
    					char ch1 = coluna.get(j).charAt(0);
    					int x = (int) ch1 - 64;
    					calcularLinha = 26 * x;
	    				char ch = coluna.get(j).charAt(1);
	    				numColuna = (int) ch - 65 + calcularLinha; 	// transformado char em inteiro (AA = 26, AB = 27, AC = 28, ...)
    				}
    				
    				if(numColuna >= line.length) { //pular leitura se não houver mais nada na linha atual
    					continue;
    				}
    				
    				// DEFINIR TIPO DE DADOS DA CELULA
    				System.out.println("Tipo do campo: "+tipoCampo.get(j));
    				switch(tipoCampo.get(j)) {
	    				case "F": //decimal
	    					conteudo = 2;
	    					break;
	    				case "S": //texto
	    					conteudo = 1;
	    					break;
	    				case "I": //inteiro
	    					conteudo = 0;
	    					break;
	    				case "H": //data e hora
	    					conteudo = 4;
	    					break;
	    				case "D": //data
	    					conteudo = 3;
	    					break;
	    				case "C": //texto grande
	    					conteudo = 1;
	    					break;
    				}
    				
    				camposImp.add(new CamposImportacao());
    				int i = camposImp.size() - 1;
    				camposImp.get(i).setNomeCampo(campo.get(j));
    				camposImp.get(i).setNomeTabela(tabela.get(j));
    				
//    				System.out.println("Nome campo adicionado: "+camposImp.get(i).getNomeCampo());
//    				System.out.println("Nome tabela adicionada: "+camposImp.get(i).getNomeTabela());
//    				System.out.println("Num coluna: "+numColuna);
//    				System.out.println("Coluna size: "+coluna.size());
//    				System.out.println("Coluna: "+coluna.get(j));
    				if(conteudo == 0) { //INTEIRO
	    				System.out.println("Dado na planilha: "+line[numColuna].replace("\"", ""));
    					if(line[numColuna].replace("\"", "") == null || "".equals(line[numColuna].replace("\"", "")))
    						conteudoNumero = null;
    					else
    						conteudoNumero = new BigDecimal(line[numColuna].replace("\"", ""));
    					camposImp.get(i).setConteudoNumero(conteudoNumero);
    					camposImp.get(i).setTipoConteudo(0);
	    				System.out.println("Conteudo: "+conteudoNumero);
    				} else if(conteudo == 1) { //STRING
    					refForn = line[numColuna].replace("\"", "").trim();
    					System.out.println("Dado na planilha: "+refForn);
    					if("REFFORN".equals(camposImp.get(i).getNomeCampo())) {
    						conteudoNumero = buscarProdutoPelaRefForn(refForn);
    						codProd = conteudoNumero;
    						if(conteudoNumero == null) {
    							System.out.println("Nenhum produto encontrado com a Referência do Fornecedor \""+refForn+"\".");
    							retorno.append("Nenhum produto encontrado com a Referência do Fornecedor \""+refForn+"\".\n");
    						}
    						camposImp.get(i).setConteudoNumero(conteudoNumero);
        					camposImp.get(i).setTipoConteudo(0);
        					camposImp.get(i).setNomeCampo("CODPROD");
        					System.out.println("Conteudo: "+conteudoNumero);
    					} else {
	    					conteudoString = line[numColuna].replace("\"", "");
	    					camposImp.get(i).setConteudoString(conteudoString.replace("'", ""));
	    					camposImp.get(i).setTipoConteudo(1);
	    					System.out.println("Conteudo: "+conteudoString);
    					}
    				} else if(conteudo == 2){ //NUMERO
    					String dado = line[numColuna].replace("\"", "").replace("R$", "").replace("-", "").trim();
    					System.out.println("Dado na planilha: "+dado);
    					if("".equals(dado) || dado == null) {
    						conteudoNumero = BigDecimal.ZERO;
    					} else {
    						try {
    							conteudoNumero = new BigDecimal(dado);
    						} catch(NumberFormatException e) {
    							DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    							symbols.setGroupingSeparator('.');
    							symbols.setDecimalSeparator(',');
    							String pattern = "#,##0.0#";
    							DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
    							decimalFormat.setParseBigDecimal(true);
    							conteudoNumero = (BigDecimal) decimalFormat.parse(dado);
    						}
    					}
    					conteudoNumero = conteudoNumero.setScale(10, BigDecimal.ROUND_HALF_EVEN);
    					camposImp.get(i).setConteudoNumero(conteudoNumero);
    					camposImp.get(i).setTipoConteudo(2);
    					System.out.println("Conteudo: "+conteudoNumero);
    				} else if(conteudo == 3){ //DATA
    					try{
	    					System.out.println("Dado na planilha (D): "+line[numColuna].replace("\"", ""));
	    					conteudoData = Utils.stringToTimestamp(line[numColuna].replace("\"", ""));
	    					camposImp.get(i).setConteudoData(conteudoData);
	    					camposImp.get(i).setTipoConteudo(3);
	    					System.out.println("Conteudo: "+conteudoData.toString());
    					} catch(IllegalStateException e) {
    						System.out.println("Tentando com Data...");
	    					System.out.println("Dado na planilha: "+line[numColuna].replace("\"", "").toString());
	    					conteudoData = Utils.stringToTimestamp(Utils.convertData(line[numColuna].replace("\"", "").toString()));
	    					camposImp.get(i).setConteudoData(conteudoData);
	    					camposImp.get(i).setTipoConteudo(3);
    					}
    				} else if(conteudo == 4){ //DATA E HORA
    					System.out.println("Dado na planilha (DH): "+line[numColuna].replace("\"", ""));
    					conteudoData = Utils.convertDataHora(line[numColuna].replace("\"", ""));
    					camposImp.get(i).setConteudoData(conteudoData);
    					camposImp.get(i).setTipoConteudo(4);
    					System.out.println("Conteudo: "+conteudoData.toString());
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			throw new Exception("Ocorreu um erro na leitura do arquivo (linha "+linhaAtual+"), verifique o arquivo e a configuração da importação.");
    		}
    		linhaAtual = linhaInicial + linhasLidas;
    		try {
    			contadorDeImportacoes = contadorDeImportacoes + importarSankhya(nroUnico, nuNota, codProd, retorno, refForn);
    		} catch(Exception e) {
    			retorno.append("Erro ao gerar produto "+refForn+": "+e.getMessage()+" \n");
			}
    		linhaAtual++;
            linhasLidas++;
		}
		return contadorDeImportacoes;
	}

	private static BigDecimal buscarProdutoPelaRefForn(String refForn) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codProd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPROD FROM TGFPRO WHERE REFFORN = '"+refForn+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				codProd = rset.getBigDecimal(1);
			} else {
				refForn = completarComZeroAEsquerda(refForn);
				codProd = buscarProdutoFormatado(refForn);
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
		return codProd;
	}

	private static BigDecimal buscarProdutoFormatado(String refForn) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codProd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPROD FROM TGFPRO WHERE REFFORN = '"+refForn+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				codProd = rset.getBigDecimal(1);
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
		return codProd;
	}

	private static String completarComZeroAEsquerda(String refForn) {
        String result = refForn;
        while (result.length() < 8)
            result = "0"+result;
        return result;
    }

	private static int importarSankhya(BigDecimal nroUnico, BigDecimal nuNota, BigDecimal codProd, StringBuilder retorno, String refForn) throws Exception {
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		boolean itemEhValido = true;
		String tabela = "TGFITE";
		String nomeInstancia = "ItemNota";
		DynamicVO tabelaVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance(nomeInstancia);
		BigDecimal conteudoNumero = null;
		String conteudoString = null;
		Timestamp conteudoData = null;
		BigDecimal qtd = null;
		BigDecimal vlrUnit = null;
		for(int j = 1; j <= camposImp.size(); ++j){
			try {
				String campo = camposImp.get(j-1).getNomeCampo();
				String marcaProduto = marcaDoProduto(codProd);
				if(marcaProduto == null) {
					retorno.append("Produto "+refForn+" não possui marca.\n");
					return 0;
				}
				String marcaTOP = marcaDaTop(nuNota);
				if(!marcaTOP.contains(marcaProduto)) {
					retorno.append("Produto "+refForn+" não possui mesma marca da TOP.\n");
					return 0;
				}
				if("CODPROD".equals(campo) && camposImp.get(j-1).getConteudoNumero() == null) {
					System.out.println("CODPROD não encontrado, item não será criado");
					itemEhValido = false;
					return 0;
				}
				if("QTDNEG".equals(campo))
					qtd = camposImp.get(j-1).getConteudoNumero();
				if("VLRUNIT".equals(campo))
					vlrUnit = camposImp.get(j-1).getConteudoNumero();
				if(camposImp.get(j-1).getTipoConteudo() == 1) {
					conteudoString = camposImp.get(j-1).getConteudoString();
					if(conteudoString != null) {
						if(tabela.equals(camposImp.get(j-1).getNomeTabela())) {
							tabelaVO.setProperty(campo, conteudoString);
							System.out.println("Adicionando "+campo+" : "+conteudoString);
						}
					}
				} else if(camposImp.get(j-1).getTipoConteudo() == 0 || camposImp.get(j-1).getTipoConteudo() == 2) {
					conteudoNumero = camposImp.get(j-1).getConteudoNumero();
					if(conteudoNumero != null) {
						if(tabela.equals(camposImp.get(j-1).getNomeTabela())) {
							tabelaVO.setProperty(campo, conteudoNumero);
							System.out.println("Adicionando "+campo+" : "+conteudoNumero);
						}
					}
				} else if(camposImp.get(j-1).getTipoConteudo() == 3 || camposImp.get(j-1).getTipoConteudo() == 4) {
					conteudoData = camposImp.get(j-1).getConteudoData();
					if(conteudoData != null) {
						if(tabela.equals(camposImp.get(j-1).getNomeTabela())) {
							tabelaVO.setProperty(campo, conteudoData);
							System.out.println("Adicionando "+campo+" : "+conteudoData);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Ocorreu um erro na geração do registro, verifique o arquivo e a configuração.");
			}
		}
		if(camposImp.size() > 0 && itemEhValido) {
			tabelaVO.setProperty("NUNOTA", nuNota);
			tabelaVO.setProperty("CONTROLE", "");
			if(qtd != null && vlrUnit != null)
				tabelaVO.setProperty("VLRTOT", qtd.multiply(vlrUnit));
			String codVol = buscarVolumeDoProduto(codProd);
			tabelaVO.setProperty("CODVOL", codVol);
			BigDecimal atualizaEstoque = buscarAtualizaEstoqueDaTOPPelaNota(nuNota);
			String atualizaEstoqueTerceiro = buscarAtualizaEstoqueTerceiroDaTOPPelaNota(nuNota);
			tabelaVO.setProperty("ATUALESTOQUE", atualizaEstoque);
			tabelaVO.setProperty("ATUALESTTERC", atualizaEstoqueTerceiro);
			dwfFacade.createEntity(nomeInstancia, (EntityVO) tabelaVO);
		}
		return 1;
	}

	private static String marcaDaTop(BigDecimal nuNota) throws Exception {
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
			
			sql.appendSql("SELECT TOP.DESCROPER AS DESCROPER FROM TGFCAB CAB, TGFTOP TOP WHERE CAB.CODTIPOPER = TOP.CODTIPOPER AND CAB.NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("DESCROPER");
				if(dado == null)
					dado = "";
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

	private static String marcaDoProduto(BigDecimal codProd) throws Exception {
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
			
			sql.appendSql("SELECT MARCA FROM TGFPRO WHERE CODPROD = "+codProd);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("MARCA");
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

	private static String buscarAtualizaEstoqueTerceiroDaTOPPelaNota(BigDecimal nuNota) throws Exception {
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
			
			sql.appendSql("SELECT ATUALESTWMSTERC FROM TGFTOP WHERE CODTIPOPER = (SELECT CODTIPOPER FROM TGFCAB WHERE NUNOTA = "+nuNota+") ORDER BY DHALTER DESC");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("ATUALESTWMSTERC");
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

	private static BigDecimal buscarAtualizaEstoqueDaTOPPelaNota(BigDecimal nuNota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal attEstoque = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT ATUALEST FROM TGFTOP WHERE CODTIPOPER = (SELECT CODTIPOPER FROM TGFCAB WHERE NUNOTA = "+nuNota+") ORDER BY DHALTER DESC");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				if("N".equals(rset.getString("ATUALEST")))
					attEstoque = BigDecimal.ZERO;
				else
					attEstoque = BigDecimal.ONE;
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
		return attEstoque;
	}

	private static String buscarVolumeDoProduto(BigDecimal codProd) throws Exception {
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
			
			sql.appendSql("SELECT CODVOL FROM TGFPRO WHERE CODPROD = "+codProd);
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

	private static void getCamposColunas(ArrayList<String> campo, ArrayList<String> tabela, ArrayList<String> coluna,
			ArrayList<String> tipoCampo, BigDecimal nroUnicoConfig) throws Exception {
		BigDecimal nuCampo = null;
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
			
			sql.appendSql("SELECT COLUNA, TABELA, NUCAMPO FROM AD_COLUNASIMPPLANILHA WHERE NUCONFIG = "+nroUnicoConfig+" ORDER BY COLUNA");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				coluna.add(rset.getString("COLUNA").replace(" ", "").toUpperCase());
				tabela.add(rset.getString("TABELA").replace(" ", "").toUpperCase());
				nuCampo = rset.getBigDecimal("NUCAMPO");
				getCampos(nuCampo, campo, tipoCampo);
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
	}

	private static void getCampos(BigDecimal nuCampo, ArrayList<String> campo, ArrayList<String> tipoCampo) throws Exception {
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
			
			sql.appendSql("SELECT NOMECAMPO, TIPCAMPO FROM TDDCAM WHERE NUCAMPO = "+nuCampo);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while(rset.next()) {
				campo.add(rset.getString("NOMECAMPO"));
				tipoCampo.add(rset.getString("TIPCAMPO"));
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
	}

	private static void atualizarCabecalhoImportacao(BigDecimal nroUnico, StringBuilder retorno, BigDecimal usuario) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao("AD_IMPNOTA").prepareToUpdateByPK(nroUnico)
				.set("CODUSU", usuario)
				.set("DHIMPORTACAO", TimeUtils.getNow())
				.set("RETORNO", retorno.toString().toCharArray())
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static FileInformation extrairJson(String contentInfo) throws JsonParseException, JsonMappingException, IOException {
        String patternString = "__start_fileinformation__(.*?)__end_fileinformation__";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(contentInfo);

        if (matcher.find()) {
            String extractedString = matcher.group(1);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(extractedString, FileInformation.class);
        }
        
        return null;
    }

	public static void removerImportacao(Registro linha) {
		BigDecimal nroUnico = (BigDecimal) linha.getCampo("NUNICO");
		System.out.println("Limpando importação "+nroUnico);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper impNotaDAO = JapeFactory.dao("AD_IMPNOTA");
			DynamicVO impNotaVO = impNotaDAO.findOne("NUNICO = "+nroUnico);
			impNotaDAO.prepareToUpdate(impNotaVO)
				.set("CODUSU", null)
				.set("DHIMPORTACAO", null)
				.update();
		} catch (Exception e) {
			System.out.println("\nErro ao limpar cabeçalho da importação "+nroUnico);
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
	}

}
