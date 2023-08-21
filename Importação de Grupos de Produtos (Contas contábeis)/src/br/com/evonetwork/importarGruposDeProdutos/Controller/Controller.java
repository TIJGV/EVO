package br.com.evonetwork.importarGruposDeProdutos.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.importarGruposDeProdutos.Model.CamposImportacao;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
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
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {
	
	static ArrayList<CamposImportacao> camposImp = new ArrayList<>();

	public static void importar(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnico = (BigDecimal) linha.getCampo("NUIMPPLANILHA");
		BigDecimal nroUnicoCofig = (BigDecimal) linha.getCampo("NUCONFIG");
		DynamicVO impPlanilhaVO = null;
		InputStream is = null;
		Workbook workbook = null;
		
		JapeWrapper daoInfo = JapeFactory.dao("AD_IMPPLANILHA");
		impPlanilhaVO = daoInfo.findByPK(nroUnico);
		
		byte[] arquivo = impPlanilhaVO.asBlob("ARQUIVO");
		if (arquivo == null) {
            ca.setMensagemRetorno("Arquivo Inválido! Aconteceu um problema na importação do Arquivo!");
            return;
        }
		
		is = new ByteArrayInputStream(arquivo);
        
        InputStream inArquivo = getLerArquivo(is);

		workbook = WorkbookFactory.create(inArquivo);
		
		lerArquivo(ca, workbook, nroUnico, linha, nroUnicoCofig);
		
		linha.setCampo("CODUSU", ca.getUsuarioLogado());
		linha.setCampo("DHIMPORTACAO", TimeUtils.getNow());
		
		ca.setMensagemRetorno("Planilha importada!");
	}
	
	private static void lerArquivo(ContextoAcao ca, Workbook workbook, BigDecimal nroUnico, 
			Registro linha, BigDecimal nroUnicoCofig) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		ArrayList<String> caracteres = new ArrayList<String>();
		int linhaAtual = 0;
		
		System.out.println("getCamposColunas");
		getCamposColunas(nroUnico, campo, tabela, coluna, tipoCampo, caracteres, ca, nroUnicoCofig);
		
		//removendo cabeçalho da planilha
		System.out.println("getCabecalho");
		BigDecimal cabecalho = getCabecalho(nroUnicoCofig);
		if(cabecalho == null)
			cabecalho = BigDecimal.ZERO;
		int cabec = cabecalho.intValue();
		for(int i=0; i < cabec; i++) {
			Row rowCabecalho = sheet.getRow(i);
			if(rowCabecalho != null)
				sheet.removeRow(rowCabecalho);
		}
		
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
        	camposImp = new ArrayList<>();
            Row row = rowIterator.next();
            System.out.println("Lendo Linha: "+linhaAtual);
    		try {
    			for(int j=0; j < coluna.size(); j++) {
    				String conteudoString = null;
    				BigDecimal conteudoNumero = null;
    				Timestamp conteudoData = null;
    				
    				int conteudo = 0;
    				int numLinha = 0;
    				int calcularLinha = 0;
    				
    				// pegando coluna como char
//    				System.out.println("Coluna: "+coluna.get(j));
    				if(coluna.get(j).length() == 1) {
	    				char ch = coluna.get(j).charAt(0);
	    				// transformado char em inteiro (A = 0, B = 1, C = 2, ...)
	    				numLinha = (int) ch - 65;
    				} else if(coluna.get(j).length() > 1) {
    					char ch1 = coluna.get(j).charAt(0);
    					int x = (int) ch1 - 64;
    					calcularLinha = 26 * x;
	    				char ch = coluna.get(j).charAt(1);
	    				// transformado char em inteiro (AA = 26, AB = 27, AC = 28, ...)
	    				numLinha = (int) ch - 65 + calcularLinha;
    				}
    				
    				Cell cell = row.getCell(numLinha);
    				if(cell != null) {
	    				// DEFINIR TIPO DE DADOS DA CELULA
//    					System.out.println("Tipo do campo: "+tipoCampo.get(j));
	    				switch(tipoCampo.get(j)) {
	    				case "F":
	    					//decimal
	    					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	    					conteudo = 2;
	    					break;
	    				case "S":
	    					//texto
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 1;
	    					break;
	    				case "I":
	    					//inteiro
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 0;
	    					break;
	    				case "H":
	    					//data e hora
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 4;
	    					break;
	    				case "D":
	    					//data
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 3;
	    					break;
	    				case "C":
	    					//texto grande
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 1;
	    					break;
	    				}
	    				
	    				camposImp.add(new CamposImportacao());
	    				int i = camposImp.size() - 1;
	    				camposImp.get(i).setNomeCampo(campo.get(j));
	    				camposImp.get(i).setNomeTabela(tabela.get(j));
	    				
//	    				System.out.println("Nome campo adicionado: "+camposImp.get(i).getNomeCampo());
//	    				System.out.println("Nome tabela adicionada: "+camposImp.get(i).getNomeTabela());
	    				
	    				if(conteudo == 0) { //INTEIRO
//	    					System.out.println("Dado na planilha: "+cell.getStringCellValue());
	    					if(cell.getStringCellValue() == null || "".equals(cell.getStringCellValue()))
	    						conteudoNumero = null;
	    					else
	    						conteudoNumero = new BigDecimal(cell.getStringCellValue());
	    					camposImp.get(i).setConteudoNumero(conteudoNumero);
	    					camposImp.get(i).setTipoConteudo(0);
//	    					System.out.println("Conteudo: "+conteudoNumero);
	    				} else if(conteudo == 1) { //STRING
//	    					System.out.println("Dado na planilha: "+cell.getStringCellValue());
	    					conteudoString = cell.getStringCellValue();
	    					camposImp.get(i).setConteudoString(conteudoString.replace("'", ""));
	    					camposImp.get(i).setTipoConteudo(1);
//	    					System.out.println("Conteudo: "+conteudoString);
	    				} else if(conteudo == 2){ //NUMERO
//	    					System.out.println("Dado na planilha: "+cell.getNumericCellValue());
	    					conteudoNumero = BigDecimal.valueOf(cell.getNumericCellValue());
//		    					conteudoNumero = conteudoNumero.setScale(10, BigDecimal.ROUND_HALF_EVEN);
	    					camposImp.get(i).setConteudoNumero(conteudoNumero);
	    					camposImp.get(i).setTipoConteudo(2);
//	    					System.out.println("Conteudo: "+conteudoNumero);
	    				} else if(conteudo == 3){ //DATA
	    					System.out.println("Dado na planilha: "+cell.getStringCellValue().replace("'", ""));
	    					conteudoData = convertData(cell.getStringCellValue().replace("'", ""));
	    					camposImp.get(i).setConteudoData(conteudoData);
	    					camposImp.get(i).setTipoConteudo(3);
	    					System.out.println("Conteudo: "+conteudoData.toString());
	    				} else if(conteudo == 4){ //DATA E HORA
	    					System.out.println("Dado na planilha: "+cell.getStringCellValue().replace("'", ""));
	    					conteudoData = convertDataHora(cell.getStringCellValue().replace("'", ""));
	    					camposImp.get(i).setConteudoData(conteudoData);
	    					camposImp.get(i).setTipoConteudo(4);
	    					System.out.println("Conteudo: "+conteudoData.toString());
	    				}
    				} else {
    					System.out.println("Célula nula");
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			ca.mostraErro("Ocorreu um erro na leitura do arquivo, verifique o arquivo e a configuração da importação.");
    		}
    		importarGruposDeProdutos(ca);
//    		importarDadosSankhya(ca);
    		linhaAtual++;
        }
	}
	
	private static void importarGruposDeProdutos(ContextoAcao ca) throws Exception {
		BigDecimal codGrupoProduto = null;
		for(int j = 0; j < camposImp.size(); ++j){
			if("CODGRUPOPROD".equals(camposImp.get(j).getNomeCampo()))
				codGrupoProduto = camposImp.get(j).getConteudoNumero();
		}
		if(grupoJaEstaCadastrado(codGrupoProduto))
			atualizarGrupoExistente(codGrupoProduto);
		else
			cadastrarGrupo();
	}

	private static void cadastrarGrupo() throws Exception {
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		String nomeTabela = camposImp.get(0).getNomeTabela();
		String nomeInstancia = getNomeInstancia(nomeTabela);
		DynamicVO tabelaVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance(nomeInstancia);
		BigDecimal conteudoNumero = null;
		String conteudoString = null;
		Timestamp conteudoData = null;
		for(int j = 1; j <= camposImp.size(); ++j){
			try {
				String campo = camposImp.get(j-1).getNomeCampo();
				if(camposImp.get(j-1).getTipoConteudo() == 1) {
					conteudoString = camposImp.get(j-1).getConteudoString();
					if(nomeTabela.equals(camposImp.get(j-1).getNomeTabela())) {
						tabelaVO.setProperty(campo, conteudoString);
					}
				} else if(camposImp.get(j-1).getTipoConteudo() == 0 || camposImp.get(j-1).getTipoConteudo() == 2) {
					conteudoNumero = camposImp.get(j-1).getConteudoNumero();
					if(nomeTabela.equals(camposImp.get(j-1).getNomeTabela())) {
						tabelaVO.setProperty(campo, conteudoNumero);
					}
				} else if(camposImp.get(j-1).getTipoConteudo() == 3 || camposImp.get(j-1).getTipoConteudo() == 4) {
					conteudoData = camposImp.get(j-1).getConteudoData();
					if(nomeTabela.equals(camposImp.get(j-1).getNomeTabela())) {
						tabelaVO.setProperty(campo, conteudoData);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Ocorreu um erro na geração da nota, verifique o arquivo e a configuração.");
			}
		}
		dwfFacade.createEntity(nomeInstancia, (EntityVO) tabelaVO);
		System.out.println("Criado registro com a PK: "+tabelaVO.getPrimaryKey());
	}

	private static void atualizarGrupoExistente(BigDecimal codGrupoProduto) throws Exception {
		BigDecimal ctaCtbEstoque = null;
		BigDecimal ctaCtbReceita = null;
		BigDecimal ctaCtbCusto = null;
		BigDecimal ctaCtbDevolucao = null;
		BigDecimal ctaCtbICMSeISS = null;
		BigDecimal ctaCtbPIS = null;
		BigDecimal ctaCtbCOFINS = null;
		BigDecimal ctaCtbDevICMSeISS = null;
		BigDecimal ctaCtbDevPIS = null;
		BigDecimal ctaCtbDevCOFINS = null;
		
		for(int i = 0; i < camposImp.size(); i++) {
			switch(camposImp.get(i).getNomeCampo()) {
				case "AD_CODCTACTB_P1":
					ctaCtbEstoque = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P2":
					ctaCtbReceita = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P3":
					ctaCtbCusto = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P4":
					ctaCtbDevolucao = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P5":
					ctaCtbICMSeISS = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P6":
					ctaCtbPIS = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P7":
					ctaCtbCOFINS = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P8":
					ctaCtbDevICMSeISS = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P9":
					ctaCtbDevPIS = camposImp.get(i).getConteudoNumero();
					break;
				case "AD_CODCTACTB_P10":
					ctaCtbDevCOFINS = camposImp.get(i).getConteudoNumero();
					break;
			}
		}
		
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.GRUPO_PRODUTO)
				.prepareToUpdateByPK(codGrupoProduto)
				.set("AD_CODCTACTB_P1", ctaCtbEstoque)
				.set("AD_CODCTACTB_P2", ctaCtbReceita)
				.set("AD_CODCTACTB_P3", ctaCtbCusto)
				.set("AD_CODCTACTB_P4", ctaCtbDevolucao)
				.set("AD_CODCTACTB_P5", ctaCtbICMSeISS)
				.set("AD_CODCTACTB_P6", ctaCtbPIS)
				.set("AD_CODCTACTB_P7", ctaCtbCOFINS)
				.set("AD_CODCTACTB_P8", ctaCtbDevICMSeISS)
				.set("AD_CODCTACTB_P9", ctaCtbDevPIS)
				.set("AD_CODCTACTB_P10", ctaCtbDevCOFINS)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("Grupo "+codGrupoProduto+" atualizado.");
	}

	private static boolean grupoJaEstaCadastrado(BigDecimal codGrupoProduto) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		boolean existe = false;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT * FROM TGFGRU WHERE CODGRUPOPROD = "+codGrupoProduto);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				existe = true;
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
		return existe;
	}

//	private static void importarDadosSankhya(ContextoAcao ca) throws Exception {
//		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
//		ArrayList<String> tabelas = getNomeTabelas();
//		for (String tabela : tabelas) {
//			String nomeInstancia = getNomeInstancia(tabela);
//			DynamicVO tabelaVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance(nomeInstancia);
//			BigDecimal conteudoNumero = null;
//			String conteudoString = null;
//			Timestamp conteudoData = null;
//			for(int j = 1; j <= camposImp.size(); ++j){
//				try {
//					String campo = camposImp.get(j-1).getNomeCampo();
//					if(camposImp.get(j-1).getTipoConteudo() == 1) {
//						conteudoString = camposImp.get(j-1).getConteudoString();
//						if(tabela.equals(camposImp.get(j-1).getNomeTabela())) {
//							tabelaVO.setProperty(campo, conteudoString);
//						}
//					} else if(camposImp.get(j-1).getTipoConteudo() == 0 || camposImp.get(j-1).getTipoConteudo() == 2) {
//						conteudoNumero = camposImp.get(j-1).getConteudoNumero();
//						if(tabela.equals(camposImp.get(j-1).getNomeTabela())) {
//							tabelaVO.setProperty(campo, conteudoNumero);
//						}
//					} else if(camposImp.get(j-1).getTipoConteudo() == 3 || camposImp.get(j-1).getTipoConteudo() == 4) {
//						conteudoData = camposImp.get(j-1).getConteudoData();
//						if(tabela.equals(camposImp.get(j-1).getNomeTabela())) {
//							tabelaVO.setProperty(campo, conteudoData);
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					ca.mostraErro("Ocorreu um erro na geração da nota, verifique o arquivo e a configuração.");
//				}
//			}
//			dwfFacade.createEntity(nomeInstancia, (EntityVO) tabelaVO);
//			System.out.println("Criado registro na tabela "+tabela);
//		}
//	}
//
//	private static ArrayList<String> getNomeTabelas() {
//		ArrayList<String> tabelas = new ArrayList<String>();
//		for(int i = 0; i < camposImp.size(); i++) {
//			if(!tabelas.isEmpty()) {
//				boolean existeTabela = false;
//				for (String tabela : tabelas) {
//					if(camposImp.get(i).getNomeTabela().equals(tabela)) {
//						existeTabela = true;
//					}
//				}
//				if(!existeTabela) {
//					tabelas.add(camposImp.get(i).getNomeTabela());
//				}
//			} else {
//				tabelas.add(camposImp.get(i).getNomeTabela());
//			}
//		}
//		return tabelas;
//	}

//	private static void importarDadosSankhya(ContextoAcao ca) throws Exception {
//		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
//		String nomeTabela = camposImp.get(0).getNomeTabela();
//		String nomeInstancia = getNomeInstancia(nomeTabela);
//		DynamicVO tabelaVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance(nomeInstancia);
//		BigDecimal conteudoNumero = null;
//		String conteudoString = null;
//		Timestamp conteudoData = null;
//		for(int j = 1; j <= camposImp.size(); ++j){
//			try {
//				String campo = camposImp.get(j-1).getNomeCampo();
//				if(camposImp.get(j-1).getTipoConteudo() == 1) {
//					conteudoString = camposImp.get(j-1).getConteudoString();
//					if(nomeTabela.equals(camposImp.get(j-1).getNomeTabela())) {
//						tabelaVO.setProperty(campo, conteudoString);
//					}
//				} else if(camposImp.get(j-1).getTipoConteudo() == 0 || camposImp.get(j-1).getTipoConteudo() == 2) {
//					conteudoNumero = camposImp.get(j-1).getConteudoNumero();
//					if(nomeTabela.equals(camposImp.get(j-1).getNomeTabela())) {
//						tabelaVO.setProperty(campo, conteudoNumero);
//					}
//				} else if(camposImp.get(j-1).getTipoConteudo() == 3 || camposImp.get(j-1).getTipoConteudo() == 4) {
//					conteudoData = camposImp.get(j-1).getConteudoData();
//					if(nomeTabela.equals(camposImp.get(j-1).getNomeTabela())) {
//						tabelaVO.setProperty(campo, conteudoData);
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				ca.mostraErro("Ocorreu um erro na geração da nota, verifique o arquivo e a configuração.");
//			}
//		}
//		dwfFacade.createEntity(nomeInstancia, (EntityVO) tabelaVO);
//		System.out.println("Criado registro na tabela "+nomeTabela);
//	}
	
	private static String getNomeInstancia(String nomeTabela) throws Exception {
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
			
			sql.appendSql("SELECT NOMEINSTANCIA FROM TDDINS WHERE NOMETAB = '"+nomeTabela+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("NOMEINSTANCIA");
			} else {
				throw new Exception("Não foi encontrada nenhuma tabela com o nome: "+nomeTabela);
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

	public static Timestamp convertData(String date) throws Exception {
		Date parsedDate;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			parsedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	    return new java.sql.Timestamp(parsedDate.getTime());
	}
	
	public static Timestamp convertDataHora(String date) throws Exception {
		Date parsedDate;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
			parsedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	    return new java.sql.Timestamp(parsedDate.getTime());
	}

	private static BigDecimal getCabecalho(BigDecimal nroUnicoCofig) {
		BigDecimal cabecalho = null;
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

			sql.appendSql("SELECT CABECALHO FROM AD_CONFIGIMPPLANILHA WHERE NUCONFIG = "+nroUnicoCofig);
			System.out.println("SQL: "+sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				cabecalho = rset.getBigDecimal("CABECALHO");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return cabecalho;
	}

	private static void getCamposColunas(BigDecimal nroUnico, ArrayList<String> campo, 
			ArrayList<String> tabela, ArrayList<String> coluna, ArrayList<String> tipoCampo, 
			ArrayList<String> caracteres, ContextoAcao ca, BigDecimal nroUnicoCofig) throws Exception {
		BigDecimal nuCampo = null;
		
		/* QUERY PARA COLETAR CAMPO E COLUNA */
		QueryExecutor query1 = ca.getQuery();
		QueryExecutor query2 = ca.getQuery();
		try {
			query1.setParam("NUCONFIG", nroUnicoCofig);
			query1.nativeSelect("SELECT COLUNA, TABELA, NUCAMPO FROM AD_COLUNASIMPPLANILHA WHERE NUCONFIG = {NUCONFIG} ORDER BY TABELA");
			while(query1.next()) {
				coluna.add(query1.getString("COLUNA").replace(" ", "").toUpperCase());
				tabela.add(query1.getString("TABELA").replace(" ", "").toUpperCase());
//				caracteres.add(query1.getString("CARACTERES"));
				nuCampo = query1.getBigDecimal("NUCAMPO");
				query2.setParam("NUCAMPO", nuCampo);
				query2.nativeSelect("SELECT NOMECAMPO, TIPCAMPO FROM TDDCAM WHERE NUCAMPO = {NUCAMPO}");
				while(query2.next()) {
					campo.add(query2.getString("NOMECAMPO"));
					tipoCampo.add(query2.getString("TIPCAMPO"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Ocorreu um erro na leitura da configuração das colunas!");
		} finally {
			query1.close();
			query2.close();
		}
		/* ------ FIM DA QUERY ------ */
	}
	
	public static boolean isCellEmpty(Cell cell) {
	    if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
	        return true;
	    }

	    if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
	        return true;
	    }

	    if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().isEmpty()) {
	        return true;
	    }

	    return false;
	}
	
	private static InputStream getLerArquivo(InputStream inputStream) throws IOException {
		JdbcWrapper jdbc = null;
        NativeSql query = null;

        try {
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringBuffer buf = new StringBuffer();
            InputStream in = inputStream;

            byte[] b = new byte[2048];
            int length;
            boolean hasFileInfo = false;
            boolean writeDirectly = false;
            int offset = 0;
            while ((length = in.read(b)) > 0) {
                if (writeDirectly) {
                    baos.write(b, 0, length);
                } else {
                    offset = buf.length();
                    buf.append(new String(b));
                    if (!hasFileInfo && "__start_fileinformation__".equals(buf.substring(0, 25))) {
                        hasFileInfo = true;
                    }

                    if (hasFileInfo) {
                        int i = buf.indexOf("__end_fileinformation__");
                        if (i > -1) {
                            i += 23;// tamanho do "__end_fileinformation__"
                            i -= offset; // O quanto ja havia sido lido antes
                            baos.write(b, i, length - i);
                            writeDirectly = true;
                        }
                    } else {
                        baos.write(b, 0, length);
                        writeDirectly = true;
                    }
                }
            }
            baos.flush();
            in.close();
            inputStream = new ByteArrayInputStream(baos.toByteArray());
        } finally {
            NativeSql.releaseResources(query);
            JdbcWrapper.closeSession(jdbc);
        }
        return inputStream;
    }
	
	public static String abrirTela(BigDecimal nuNota) {
		String tela = "br.com.sankhya.com.mov.CentralNotas";
		byte[] encodedBytesTela = Base64.encodeBase64(tela.getBytes());
		
		String parametros = "{\"NUNOTA\":"+nuNota+"}";
		byte[] encodedBytesParametros = Base64.encodeBase64(parametros.getBytes());
		
		String link = "https://alsol.nuvemdatacom.com.br:8491/mge/system.jsp#app/" + new String(encodedBytesTela) + "/" + new String(encodedBytesParametros) + "/";
		
		String url = "<a target=\"_parent\" title=\"Nota gerada com sucesso\" href=\""+link+"\">"+nuNota+"</a>";
				
		return url;
	}

}
