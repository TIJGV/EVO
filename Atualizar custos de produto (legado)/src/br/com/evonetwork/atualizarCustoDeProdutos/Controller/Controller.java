package br.com.evonetwork.atualizarCustoDeProdutos.Controller;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;

import br.com.evonetwork.atualizarCustoDeProdutos.DAO.BuscarDados;
import br.com.evonetwork.atualizarCustoDeProdutos.Model.CustoProduto;
import br.com.evonetwork.atualizarCustoDeProdutos.Utils.Utils;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	public static void iniciarAtualizacao(ContextoAcao ca) throws Exception {
		BigDecimal codEmp = new BigDecimal((String) ca.getParam("CODEMP"));
		Timestamp periodoIni = (Timestamp) ca.getParam("DTINI");
		Timestamp periodoFim = (Timestamp) ca.getParam("DTFIM");
		String codProd = (String) ca.getParam("CODPROD");
		String grupoProd = (String) ca.getParam("GRUPOPROD");
		System.out.println("Parâmetros: {codEmp: "+codEmp+", periodoIni: "+periodoIni.toString()+", " + "periodoFim: "+periodoFim.toString()+", codProd: "+codProd+", codGrupo: "+grupoProd+"}");
		if (codProd == null || codProd.isEmpty() || "null".equals(codProd)) {
			if (grupoProd == null || grupoProd.isEmpty() || "null".equals(grupoProd)) {
				System.out.println("Atualizando todos os produtos...");
				ArrayList<DynamicVO> produtos = BuscarDados.buscarTodosProdutosComCusto(ca, codEmp, periodoIni, periodoFim);
				int count = 0;
				for (DynamicVO produto : produtos) {
					atualizarCustosDoProduto(produto, ca);
					++count;
				}
				ca.setMensagemRetorno("Os custos de "+count+" produtos foram atualizados.");
			} else {
				System.out.println("Atualizando todos os produtos do grupo "+grupoProd+"...");
				ArrayList<DynamicVO> produtos = BuscarDados.buscarTodosProdutosDoGrupoComCusto(ca, codEmp, periodoIni, periodoFim, grupoProd);
				int count = 0;
				for (DynamicVO produto : produtos) {
					atualizarCustosDoProduto(produto, ca);
					++count;
				}
				ca.setMensagemRetorno("Os custos de "+count+" produtos do grupo "+grupoProd+" foram atualizados.");
			}
		} else {
			BigDecimal codProdBD = new BigDecimal(codProd);
			System.out.println("Atualizando apenas o produto "+codProdBD+"...");
			ArrayList<DynamicVO> produtos = BuscarDados.buscarCustoDaImportacaoParaProduto(ca, codProdBD, codEmp, periodoIni, periodoFim);
			for (DynamicVO produto : produtos) {
				atualizarCustosDoProduto(produto, ca);
			}
			ca.setMensagemRetorno("Os custos do produto "+codProdBD+" foram atualizados.");
		}
	}

	private static void atualizarCustosDoProduto(DynamicVO produto, ContextoAcao ca) throws Exception {
		System.out.println("Atualizando custos do produto "+produto.asBigDecimal("CODPROD"));
		System.out.println("Gerando custo atual: ");
		CustoProduto custoAtual = gerarCustoProdutoPeloVO(produto);
		System.out.println("Gerando custo novo: ");
		CustoProduto custoNovo = gerarCustoProdutoPeloVO(produto);
		verificarDadosQueSeraoAtualizados(ca, custoAtual, custoNovo);
		System.out.println("Custo novo após atualizações: "+custoNovo.toString());
		atualizarCusto(custoNovo, produto);
	}

	private static void atualizarCusto(CustoProduto custoNovo, DynamicVO produto) throws Exception {
		System.out.println("Atualizando Custo...");
		EntityFacade entity = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = entity.getJdbcWrapper();
		jdbc.openSession();
		String scriptUpdate = "UPDATE TGFCUS SET CUSMEDICM = "+custoNovo.getCusMedIcm()+", "+"CUSSEMICM = "
				+ custoNovo.getCusSemIcm()+", "+"CUSREP = "+custoNovo.getCusRep()+", "+"CUSVARIAVEL = "
				+ custoNovo.getCusVariavel()+", "+"CUSGER = "+custoNovo.getCusGer()+", "+"CUSMED = "
				+ custoNovo.getCusMed()+" "+"WHERE CODPROD = "+custoNovo.getCodProd()+" "+"AND CODEMP = "
				+ custoNovo.getCodEmp()+" "+"AND TO_DATE(TO_CHAR(DTATUAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') = '"
				+ Utils.convertDate2(custoNovo.getDtAtual().toString())+"' "+"AND CODLOCAL = "
				+ custoNovo.getCodLocal()+" "+"AND CONTROLE = '"+custoNovo.getControle()+"' "
				+ "AND PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO'";
		System.out.println("UPD: "+scriptUpdate);
		PreparedStatement pstmUpdate = jdbc.getPreparedStatement(scriptUpdate);
		pstmUpdate.executeUpdate();
	}

	private static void verificarDadosQueSeraoAtualizados(ContextoAcao ca, CustoProduto custoAtual, CustoProduto custoNovo) throws Exception {
		System.out.println("Verificando o que será atualizado...");
		boolean atualizaCusMedIcm = Utils.parametroParaBoolean((String) ca.getParam("ATTCUSMEDICM"));
		boolean atualizaCusSemIcm = Utils.parametroParaBoolean((String) ca.getParam("ATTCUSSEMICM"));
		boolean atualizaCusRep = Utils.parametroParaBoolean((String) ca.getParam("ATTCUSREP"));
		boolean atualizaCusVariavel = Utils.parametroParaBoolean((String) ca.getParam("ATTCUSVARIAVEL"));
		boolean atualizaCusGer = Utils.parametroParaBoolean((String) ca.getParam("ATTCUSGER"));
		boolean atualizaCusMed = Utils.parametroParaBoolean((String) ca.getParam("ATTCUSMED"));
		boolean atualizaEntComIcm = Utils.parametroParaBoolean((String) ca.getParam("ATTENTRADACOMICMS"));
		boolean atualizaEntSemIcm = Utils.parametroParaBoolean((String) ca.getParam("ATTENTRADASEMICMS"));
		BigDecimal cusMedIcm = BigDecimal.ZERO;
		BigDecimal cusSemIcm = BigDecimal.ZERO;
		BigDecimal cusRep = BigDecimal.ZERO;
		BigDecimal cusVariavel = BigDecimal.ZERO;
		BigDecimal cusGer = BigDecimal.ZERO;
		BigDecimal cusMed = BigDecimal.ZERO;
		BigDecimal entComIcm = BigDecimal.ZERO;
		BigDecimal entSemIcm = BigDecimal.ZERO;
		QueryExecutor query = ca.getQuery();
		try {
			String select = "SELECT ENTRADACOMICMS, ENTRADASEMICMS, CUSMEDICM, CUSSEMICM, CUSREP, CUSVARIAVEL, CUSGER, CUSMED FROM TGFCUS WHERE CODPROD = "
					+ custoAtual.getCodProd()+" "+"AND CODEMP = "+custoAtual.getCodEmp()+" "
					+ "AND TO_DATE(TO_CHAR(DTATUAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') < '"
					+ Utils.convertDate2(custoAtual.getDtAtual().toString())+"' "+"AND CODLOCAL = "
					+ custoAtual.getCodLocal()+" "+"AND CONTROLE = '"+custoAtual.getControle()+"' "
					+ "AND PROCESSO <> 'br.com.sankhya.menu.adicional.AD_IMPPRO' "+"ORDER BY DTATUAL DESC";
			query.nativeSelect(select);
			System.out.println("SQL: "+select);
			if (query.next()) {
				cusMedIcm = query.getBigDecimal("CUSMEDICM");
				cusSemIcm = query.getBigDecimal("CUSSEMICM");
				cusRep = query.getBigDecimal("CUSREP");
				cusVariavel = query.getBigDecimal("CUSVARIAVEL");
				cusGer = query.getBigDecimal("CUSGER");
				cusMed = query.getBigDecimal("CUSMED");
				entComIcm = query.getBigDecimal("ENTRADACOMICMS");
				entSemIcm = query.getBigDecimal("ENTRADASEMICMS");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			query.close();
		}
		query.close();
		if (atualizaCusMedIcm)
			custoNovo.setCusMedIcm(cusMedIcm);
		if (atualizaCusSemIcm)
			custoNovo.setCusSemIcm(cusSemIcm);
		if (atualizaCusRep)
			custoNovo.setCusRep(cusRep);
		if (atualizaCusVariavel)
			custoNovo.setCusVariavel(cusVariavel);
		if (atualizaCusGer)
			custoNovo.setCusGer(cusGer);
		if (atualizaCusMed)
			custoNovo.setCusMed(cusMed);
		if (atualizaEntComIcm)
			custoNovo.setEntComIcm(entComIcm);
		if (atualizaEntSemIcm)
			custoNovo.setEntSemIcm(entSemIcm);
	}

	private static CustoProduto gerarCustoProdutoPeloVO(DynamicVO produto) {
		CustoProduto custoProduto = new CustoProduto();
		custoProduto.setCodProd(produto.asBigDecimal("CODPROD"));
		custoProduto.setCodEmp(produto.asBigDecimal("CODEMP"));
		custoProduto.setDtAtual(produto.asTimestamp("DTATUAL"));
		custoProduto.setCodLocal(produto.asBigDecimal("CODLOCAL"));
		custoProduto.setControle(produto.asString("CONTROLE"));
		custoProduto.setCusMedIcm(produto.asBigDecimal("CUSMEDICM"));
		custoProduto.setCusSemIcm(produto.asBigDecimal("CUSSEMICM"));
		custoProduto.setCusRep(produto.asBigDecimal("CUSREP"));
		custoProduto.setCusVariavel(produto.asBigDecimal("CUSVARIAVEL"));
		custoProduto.setCusGer(produto.asBigDecimal("CUSGER"));
		custoProduto.setCusMed(produto.asBigDecimal("CUSMED"));
		custoProduto.setEntComIcm(produto.asBigDecimal("ENTRADACOMICMS"));
		custoProduto.setEntSemIcm(produto.asBigDecimal("ENTRADASEMICMS"));
		custoProduto.setProcesso(produto.asString("PROCESSO"));
		System.out.println(custoProduto.toString());
		return custoProduto;
	}
}
