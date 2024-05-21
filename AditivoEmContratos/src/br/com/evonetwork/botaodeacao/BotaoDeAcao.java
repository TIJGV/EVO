package br.com.evonetwork.botaodeacao;

import java.math.BigDecimal;

import br.com.evonetwork.adadicon.ColetaDadosDosCamposDaAdAdicon;
import br.com.evonetwork.tcscon.ColetaDadosDosCamposDaTcscon;
import br.com.evonetwork.tcspre.ColetaDadosDosCamposDaTcspre;
import br.com.evonetwork.tcspsc.ColetaCamposTcspsc;
import br.com.evonetwork.updatenatcscon.ColetaCamposDaAdItetipcon;
import br.com.evonetwork.verificarpedidospendentes.RastreiaPedidos;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.util.JapeSessionContext;

public class BotaoDeAcao implements AcaoRotinaJava {

	ColetaDadosDosCamposDaAdAdicon aditivarContrato = new ColetaDadosDosCamposDaAdAdicon();
	ColetaDadosDosCamposDaTcscon contrato = new ColetaDadosDosCamposDaTcscon();
	ColetaCamposTcspsc produtosNoContrato = new ColetaCamposTcspsc();
	ColetaDadosDosCamposDaTcspre precoNosProdutosDoContrato = new ColetaDadosDosCamposDaTcspre();
	ColetaCamposDaAdItetipcon itensAditivos = new ColetaCamposDaAdItetipcon();
	RastreiaPedidos cc = new RastreiaPedidos();
	
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		try {
			Registro[] linhas = contexto.getLinhas();

			for (Registro linha : linhas) {
				
				if (linhas.length == 0) {
					contexto.setMensagemRetorno("Selecione pelo menos um aditivo!");
					return;
				}
				BigDecimal usuarioLogado = BigDecimal.ZERO;
				JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
				
				BigDecimal codigoDoAditivo = (BigDecimal) linha.getCampo("CODADITIVO");
				BigDecimal contratoOriginal = (BigDecimal) linha.getCampo("NUMCONTRATO");
				BigDecimal campoAlter = (BigDecimal) linha.getCampo("CAMPOALTER");

				aditivarContrato.coletarDadosAdAdicon(codigoDoAditivo);
				contrato.coletarDadosTcscon(aditivarContrato.getAdAdicon().getNumcontrato());
				produtosNoContrato.coletarDadosTcspsc(aditivarContrato.getAdAdicon().getNumcontrato());
				itensAditivos.coletarDadosAdItetipcon(codigoDoAditivo,contratoOriginal);

				if (aditivarContrato.getAdAdicon().getConcopia() != null) {
					contexto.setMensagemRetorno("O aditivo " + aditivarContrato.getAdAdicon().getCodaditivo()
							+ " já foi utilizado! Para uma nova adição no contrato é necessária a  criação de um novo aditivo!");
				} else {
					NovaLinhaNaTcscon(contexto);
				}
				
				/////*************************
				cc.PedidosExtras(contratoOriginal);
				
				////**************************
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void NovaLinhaNaTcscon(ContextoAcao contexto) throws Exception {
		// calcula a quantidade de contratos antes do insert
		RastreabilidadeNumeroContrato quantidadeDeContratosAntes = new RastreabilidadeNumeroContrato();
		quantidadeDeContratosAntes.QuantidadeDeContratos();

		// nova linha na tcscon
		Registro linhaNaTcscon = contexto.novaLinha("TCSCON");

		// ***********************BIGDECIMAL************************
		linhaNaTcscon.setCampo("TOPFATURCON", contrato.getTcscon().getTopfaturcon());
		linhaNaTcscon.setCampo("QTDPROVISAO", contrato.getTcscon().getQtdprovisao());
		linhaNaTcscon.setCampo("CODCID", contrato.getTcscon().getCodcid());
		linhaNaTcscon.setCampo("TIPOTITULO", contrato.getTcscon().getTipotitulo());
		linhaNaTcscon.setCampo("QTDPARCPGCOM", contrato.getTcscon().getQtdparcpgcom());
		linhaNaTcscon.setCampo("RECDESP", contrato.getTcscon().getRecdesp());
		linhaNaTcscon.setCampo("CODCENCUS", contrato.getTcscon().getCodcencus());
		linhaNaTcscon.setCampo("CODCONTATO", contrato.getTcscon().getCodcontato());
		linhaNaTcscon.setCampo("CODCRITERIO", contrato.getTcscon().getCodcriterio());
		linhaNaTcscon.setCampo("CODEMP", contrato.getTcscon().getCodemp());
		linhaNaTcscon.setCampo("CODIMPLANT", contrato.getTcscon().getCodimplant());
		linhaNaTcscon.setCampo("CODMOEALTREAJ", contrato.getTcscon().getCodmoealtreaj());
		linhaNaTcscon.setCampo("CODMOEDA", contrato.getTcscon().getCodmoeda());
		linhaNaTcscon.setCampo("CODMONSANKHYA", contrato.getTcscon().getCodmonsankhya());
		linhaNaTcscon.setCampo("CODNAT", contrato.getTcscon().getCodnat());
		linhaNaTcscon.setCampo("CODPARC", contrato.getTcscon().getCodparc());
		linhaNaTcscon.setCampo("CODPARCPREST", contrato.getTcscon().getCodparcprest());
		linhaNaTcscon.setCampo("CODPARCSEC", contrato.getTcscon().getCodparcsec());
		linhaNaTcscon.setCampo("CODPROJ", contrato.getTcscon().getCodproj());
		linhaNaTcscon.setCampo("CODPROJSINT", contrato.getTcscon().getCodprojsint());
		linhaNaTcscon.setCampo("CODTIPVENDA", contrato.getTcscon().getCodtipvenda());
		linhaNaTcscon.setCampo("CODUSU", contrato.getTcscon().getCodusu());
		linhaNaTcscon.setCampo("DIAFIMMED", contrato.getTcscon().getDiafimmed());
		linhaNaTcscon.setCampo("DIAPAG", contrato.getTcscon().getDiapag());
		linhaNaTcscon.setCampo("FREQREAJ", contrato.getTcscon().getFreqreaj());
		linhaNaTcscon.setCampo("FREQVISITAS", contrato.getTcscon().getFreqvisitas());
		linhaNaTcscon.setCampo("GATILHO", contrato.getTcscon().getGatilho());
		linhaNaTcscon.setCampo("NUMCONTRATOORIGEM", contrato.getTcscon().getNumcontratoorigem());
		linhaNaTcscon.setCampo("NUNOTAPEDLOC", contrato.getTcscon().getNunotapedloc());
		linhaNaTcscon.setCampo("NUSLA", contrato.getTcscon().getNusla());
		linhaNaTcscon.setCampo("PARCELAATUAL", contrato.getTcscon().getParcelaatual());
		linhaNaTcscon.setCampo("PARCELAQTD", contrato.getTcscon().getParcelaqtd());
		linhaNaTcscon.setCampo("PERCIRF", contrato.getTcscon().getPercirf());
		linhaNaTcscon.setCampo("PERCISS", contrato.getTcscon().getPerciss());
		linhaNaTcscon.setCampo("PERCLOC", contrato.getTcscon().getPercloc());
		linhaNaTcscon.setCampo("PRAZOVENCTO", contrato.getTcscon().getPrazovencto());
		linhaNaTcscon.setCampo("CODUSUALTER", contrato.getTcscon().getCodusualter());
		linhaNaTcscon.setCampo("CODSERVEX", contrato.getTcscon().getCodservex());
		linhaNaTcscon.setCampo("CODCENCUSAR", contrato.getTcscon().getCodcencusar());
		linhaNaTcscon.setCampo("CODCENCUSEX", contrato.getTcscon().getCodcencusex());
		linhaNaTcscon.setCampo("CODNATAR", contrato.getTcscon().getCodnatar());
		linhaNaTcscon.setCampo("CODNATEX", contrato.getTcscon().getCodnatex());
		linhaNaTcscon.setCampo("CODSAF", contrato.getTcscon().getCodsaf());
		linhaNaTcscon.setCampo("CODTIPVENDAAR", contrato.getTcscon().getCodtipvendaar());
		linhaNaTcscon.setCampo("CODTIPVENDAEX", contrato.getTcscon().getCodtipvendaex());
		linhaNaTcscon.setCampo("DIACARECAR", contrato.getTcscon().getDiacarecar());
		linhaNaTcscon.setCampo("DIACARENC", contrato.getTcscon().getDiacarenc());
		linhaNaTcscon.setCampo("DIACARENCEX", contrato.getTcscon().getDiacarencex());
		linhaNaTcscon.setCampo("PADCLASS", contrato.getTcscon().getPadclass());
		linhaNaTcscon.setCampo("QUEBRATEC", contrato.getTcscon().getQuebratec());
		linhaNaTcscon.setCampo("RESPPAGAR", contrato.getTcscon().getResppagar());
		linhaNaTcscon.setCampo("TABPRECUMI", contrato.getTcscon().getTabprecumi());
		linhaNaTcscon.setCampo("TABPRECUMIAR", contrato.getTcscon().getTabprecumiar());
		linhaNaTcscon.setCampo("TIPOTITULOAR", contrato.getTcscon().getTipotituloar());
		linhaNaTcscon.setCampo("TIPOTITULOEX", contrato.getTcscon().getTipotituloex());
		linhaNaTcscon.setCampo("UMIDPADRA", contrato.getTcscon().getUmidpadra());
		linhaNaTcscon.setCampo("UNICONVSC", contrato.getTcscon().getUniconvsc());
		linhaNaTcscon.setCampo("AD_CODTIPCON", contrato.getTcscon().getAd_codtipcon());
		linhaNaTcscon.setCampo("CODOBS", contrato.getTcscon().getCodobs());
		linhaNaTcscon.setCampo("PRAZOMENSAL", contrato.getTcscon().getPrazomensal());
		linhaNaTcscon.setCampo("NUNOTA", contrato.getTcscon().getNunota());
		linhaNaTcscon.setCampo("CODCLC", contrato.getTcscon().getCodclc());
		linhaNaTcscon.setCampo("VALQUEBTRANS", contrato.getTcscon().getValquebtrans());
		linhaNaTcscon.setCampo("NUNOTAREFARMAZE", contrato.getTcscon().getNunotarefarmaze());
		linhaNaTcscon.setCampo("NUNOTAREFEXPREC", contrato.getTcscon().getNunotarefexprec());
		linhaNaTcscon.setCampo("NUMCSTC", contrato.getTcscon().getNumcstc());
		linhaNaTcscon.setCampo("AD_VLRCONTRATO", contrato.getTcscon().getAd_vlrcontrato());

		// ************************STRING************************
		linhaNaTcscon.setCampo("TEMIRF", contrato.getTcscon().getTemirf());
		linhaNaTcscon.setCampo("TEMISS", contrato.getTcscon().getTemiss());
		linhaNaTcscon.setCampo("TEMMED", contrato.getTcscon().getTemmed());
		linhaNaTcscon.setCampo("RETEMISS", contrato.getTcscon().getRetemiss());
		linhaNaTcscon.setCampo("ACESSAHISTSUBOS", contrato.getTcscon().getAcessahistsubos());
		linhaNaTcscon.setCampo("AMBIENTE", contrato.getTcscon().getAmbiente());
		linhaNaTcscon.setCampo("ATIVO", "N");
		linhaNaTcscon.setCampo("CLAUSCONTRATO", contrato.getTcscon().getClauscontrato());
		linhaNaTcscon.setCampo("DIAUTIL", contrato.getTcscon().getDiautil());
		linhaNaTcscon.setCampo("EQUIPAMENTO", contrato.getTcscon().getEquipamento());
		linhaNaTcscon.setCampo("FERIADOEST", contrato.getTcscon().getFeriadoest());
		linhaNaTcscon.setCampo("FERIADOMUN", contrato.getTcscon().getFeriadomun());
		linhaNaTcscon.setCampo("FERIADONAC", contrato.getTcscon().getFeriadonac());
		linhaNaTcscon.setCampo("GERARNF", contrato.getTcscon().getGerarnf());
		linhaNaTcscon.setCampo("IMPRIME", contrato.getTcscon().getImprime());
		linhaNaTcscon.setCampo("IMPRPRECINDIV", contrato.getTcscon().getImprprecindiv());
		linhaNaTcscon.setCampo("OBSERVACOES", contrato.getTcscon().getObservacoes());
		linhaNaTcscon.setCampo("REAJUSTENEGATIVO", contrato.getTcscon().getReajustenegativo());
		linhaNaTcscon.setCampo("GERARFINNOTA", contrato.getTcscon().getGerarfinnota());
		linhaNaTcscon.setCampo("LOCACAOBEM", contrato.getTcscon().getLocacaobem());
		linhaNaTcscon.setCampo("NUMCONTIN", contrato.getTcscon().getNumcontin());
		linhaNaTcscon.setCampo("COBPROPORCAR", contrato.getTcscon().getCobproporcar());
		linhaNaTcscon.setCampo("DEFTIPA", contrato.getTcscon().getDeftipa());
		linhaNaTcscon.setCampo("PERCOBRA", contrato.getTcscon().getPercobra());
		linhaNaTcscon.setCampo("PERCOBRAAR", contrato.getTcscon().getPercobraar());
		linhaNaTcscon.setCampo("PERDESC", contrato.getTcscon().getPerdesc());
		linhaNaTcscon.setCampo("PERDESCON", contrato.getTcscon().getPerdescon());
		linhaNaTcscon.setCampo("SITCONT", contrato.getTcscon().getSitcont());
		linhaNaTcscon.setCampo("TIPCOBR", contrato.getTcscon().getTipcobr());
		linhaNaTcscon.setCampo("TIPOARM", contrato.getTcscon().getTipoarm());
		linhaNaTcscon.setCampo("TIPQUEBRA", contrato.getTcscon().getTipquebra());
		linhaNaTcscon.setCampo("ULTTABUMI", contrato.getTcscon().getUlttabumi());
		linhaNaTcscon.setCampo("VALPEDFIN", contrato.getTcscon().getValpedfin());
		linhaNaTcscon.setCampo("COBPROQUE", contrato.getTcscon().getCobproque());
		linhaNaTcscon.setCampo("REGLAUDSAIDA", contrato.getTcscon().getReglaudsaida());
		linhaNaTcscon.setCampo("DIAFIXO", contrato.getTcscon().getDiafixo());
		linhaNaTcscon.setCampo("TIPISENCAO", contrato.getTcscon().getTipisencao());
		linhaNaTcscon.setCampo("FORMFATARMAZE", contrato.getTcscon().getFormfatarmaze());
		linhaNaTcscon.setCampo("FORMFATEXPREC", contrato.getTcscon().getFormfatexprec());
		linhaNaTcscon.setCampo("CIF_FOB", contrato.getTcscon().getCif_fob());
		linhaNaTcscon.setCampo("TIPO", contrato.getTcscon().getTipo());
		linhaNaTcscon.setCampo("TIPPAG", contrato.getTcscon().getTippag());
		linhaNaTcscon.setCampo("TIPOCONTRATO", contrato.getTcscon().getTipocontrato());
		linhaNaTcscon.setCampo("CONTROLOCBENS", contrato.getTcscon().getControlocbens());
		linhaNaTcscon.setCampo("SERFATURCON", contrato.getTcscon().getSerfaturcon());
		linhaNaTcscon.setCampo("GRUPOFATURPRORATA", contrato.getTcscon().getGrupofaturprorata());
		linhaNaTcscon.setCampo("AD_CONTROLEADITIVO", "S");
		linhaNaTcscon.setCampo("CONTRORGPUBLICO", contrato.getTcscon().getControrgpublico());
		linhaNaTcscon.setCampo("FATURPRORATA", contrato.getTcscon().getFaturprorata());

		// ***********************************DATA*************
		linhaNaTcscon.setCampo("DTBASEREAJ", contrato.getTcscon().getDtbasereaj());
		linhaNaTcscon.setCampo("DTTERMINO", contrato.getTcscon().getDttermino());
		linhaNaTcscon.setCampo("DTREFEXPREC", contrato.getTcscon().getDtrefexprec());
		linhaNaTcscon.setCampo("DTREFARMAZE", contrato.getTcscon().getDtrefarmaze());
		linhaNaTcscon.setCampo("DTREFPROXFAT", contrato.getTcscon().getDtrefproxfat());
		linhaNaTcscon.setCampo("DTENVIOEMAIL", contrato.getTcscon().getDtenvioemail());
		linhaNaTcscon.setCampo("DTCONTRATO", contrato.getTcscon().getDtcontrato());

		// ****************************HORA*****************
		linhaNaTcscon.setCampo("DURACAO", contrato.getTcscon().getDuracao());
		linhaNaTcscon.save();

		// calcula a quantidade de contratos pós insert
		RastreabilidadeNumeroContrato quantidadeDeContratosDepois = new RastreabilidadeNumeroContrato();
		quantidadeDeContratosDepois.QuantidadeDeContratos();

		// descobre qual o "novo" contrato criado
		BigDecimal numContratoNovo = BigDecimal.valueOf(0);
		for (int i = 0; i < quantidadeDeContratosDepois.getListaDeContratos().size(); i++) {
			if (!quantidadeDeContratosAntes.getListaDeContratos()
					.contains(quantidadeDeContratosDepois.getListaDeContratos().get(i))) {
				numContratoNovo = quantidadeDeContratosDepois.getListaDeContratos().get(i);
			}
		}
		quantidadeDeContratosDepois.updateAdAdicon(aditivarContrato.getAdAdicon().getCodaditivo(), numContratoNovo);
		inserirProduto(numContratoNovo, contexto);
	}

	public void inserirProduto(BigDecimal numContratoNovo, ContextoAcao contexto) throws Exception {

		for (int i = 0; i < produtosNoContrato.quantidadeDeProdutosNoContrato(); i++) {
			Registro linhaNaTcspsc = contexto.novaLinha("TCSPSC");
			// BIGDECIMAL
			linhaNaTcspsc.setCampo("TOPFATURCON", produtosNoContrato.getListaTcspsc(i).getTopfaturcon());
			linhaNaTcspsc.setCampo("VLRUNIT", produtosNoContrato.getListaTcspsc(i).getVlrunit());
			linhaNaTcspsc.setCampo("QTDMESES", produtosNoContrato.getListaTcspsc(i).getQtdmeses());
			linhaNaTcspsc.setCampo("QTDEPREVISTA", produtosNoContrato.getListaTcspsc(i).getQtdeprevista());
			linhaNaTcspsc.setCampo("NUMUSUARIOS", produtosNoContrato.getListaTcspsc(i).getNumusuarios());
			linhaNaTcspsc.setCampo("NUMCONTRATO", numContratoNovo);
			linhaNaTcspsc.setCampo("GRUPIMPRESSAO", produtosNoContrato.getListaTcspsc(i).getGrupimpressao());
			linhaNaTcspsc.setCampo("FREQUENCIA", produtosNoContrato.getListaTcspsc(i).getFrequencia());
			linhaNaTcspsc.setCampo("CODPROD", produtosNoContrato.getListaTcspsc(i).getCodprod());
			linhaNaTcspsc.setCampo("CODPARCPREF", produtosNoContrato.getListaTcspsc(i).getCodparcpref());
			linhaNaTcspsc.setCampo("QTDUSU", produtosNoContrato.getListaTcspsc(i).getQtdusu());

			// STRING
			linhaNaTcspsc.setCampo("LIMITANTE", produtosNoContrato.getListaTcspsc(i).getLimitante());
			linhaNaTcspsc.setCampo("IMPROS", produtosNoContrato.getListaTcspsc(i).getImpros());
			linhaNaTcspsc.setCampo("IMPRNOTA", produtosNoContrato.getListaTcspsc(i).getImprnota());
			linhaNaTcspsc.setCampo("VERSAO", produtosNoContrato.getListaTcspsc(i).getVersao());
			linhaNaTcspsc.setCampo("SERFATURCON", produtosNoContrato.getListaTcspsc(i).getSerfaturcon());
			linhaNaTcspsc.setCampo("OBSERVACAO", produtosNoContrato.getListaTcspsc(i).getObservacao());
			linhaNaTcspsc.setCampo("SITPROD", produtosNoContrato.getListaTcspsc(i).getSitprod());
			linhaNaTcspsc.setCampo("NUMSERIE", produtosNoContrato.getListaTcspsc(i).getNumserie());
			linhaNaTcspsc.setCampo("AD_CODBEM", produtosNoContrato.getListaTcspsc(i).getAd_codbem());
			linhaNaTcspsc.setCampo("PRODPRINC", produtosNoContrato.getListaTcspsc(i).getProdprinc());
			linhaNaTcspsc.save();
			
			precoNosProdutosDoContrato.getListaTcspre().clear();
			precoNosProdutosDoContrato.setContador(0);
			
			precoNosProdutosDoContrato.coletarDadosTcspre(aditivarContrato.getAdAdicon().getNumcontrato(),
					produtosNoContrato.getListaTcspsc(i).getCodprod());
			inserirPrecoNoProduto(numContratoNovo, produtosNoContrato.getListaTcspsc(i).getCodprod(), contexto);
		}
	}

	public void inserirPrecoNoProduto(BigDecimal numContratoNovo, BigDecimal codProd, ContextoAcao contexto)
			throws Exception {

		for (int i = 0; i < precoNosProdutosDoContrato.quantidadeDePrecosNoProduto(); i++) {
			Registro linhaNaTcspre = contexto.novaLinha("TCSPRE");
			linhaNaTcspre.setCampo("CODPROD", codProd);
			linhaNaTcspre.setCampo("CODSERV", precoNosProdutosDoContrato.getListaTcspre(i).getCodserv());
			linhaNaTcspre.setCampo("CODTERRESPAR", precoNosProdutosDoContrato.getListaTcspre(i).getCodterrespar());
			linhaNaTcspre.setCampo("NUMCONTRATO", numContratoNovo);
			linhaNaTcspre.setCampo("VALOR", precoNosProdutosDoContrato.getListaTcspre(i).getValor());
			linhaNaTcspre.setCampo("REFERENCIA", precoNosProdutosDoContrato.getListaTcspre(i).getReferencia());
		
			linhaNaTcspre.save();
		}
	}
}
