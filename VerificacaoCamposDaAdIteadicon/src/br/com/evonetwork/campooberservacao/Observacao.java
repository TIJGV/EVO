package br.com.evonetwork.campooberservacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.evonetwork.tratamentodedados.VerificaCampos;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class Observacao {
	private String observacao;
	String erro = "";
	ColetaOpcoesDosCampos coletaOpcoes = new ColetaOpcoesDosCampos();
	String descrCampo;
	String tipCampoSql;
	String tipCampo;
	String nomeCampo;

	public Observacao(PersistenceEvent event) throws Exception {
		DynamicVO evento = (DynamicVO) event.getVo();
		BigDecimal campoAlter = evento.asBigDecimal("CAMPOALTER");
		BigDecimal sequencia = evento.asBigDecimal("SEQUENCIA");
		BigDecimal codAditivo = evento.asBigDecimal("CODADITIVO");
		String alterarPara = evento.asString("ALTERARPARA");

		coletaOpcoes.ColetaCamposComOpcoes();

		MontarMensagemObservacao(campoAlter, alterarPara);
		UpdateCampoObservacao(codAditivo, sequencia);
	}

	public void MontarMensagemObservacao(BigDecimal campoAlter, String alterarPara) throws Exception {

		VerificaCampos verificaCampo = new VerificaCampos();
		verificaCampo.TipoDeCampo(campoAlter);

		descrCampo = verificaCampo.getDescrCampo();
		tipCampoSql = verificaCampo.getTipCampoSql();
		tipCampo = verificaCampo.getTipCampo();
		nomeCampo = verificaCampo.getNomeCampo();

		if (getTipCampo().equals("B")) { // conteudo binario
			try {
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
			} catch (Exception e) {
				throw new Exception(erro);
			}
		}
		if (getTipCampo().equals("C")) { // texto longo (CLOB)
			try {
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
			} catch (Exception e) {
				throw new Exception(erro);
			}
		}
		if (getTipCampo().equals("D")) { // data
			try {
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
				
				if (!checkDate(alterarPara) || alterarPara.length() > 10) {
					
					erro = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'" + " e o valor digitado '"
							+ alterarPara + "' não pode ser convertido para o tipo necessário. "
							+ "\nDigite um valor no formato dd/MM/aaaa.";
					throw new Exception();
				} else {
					observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
				}

			} catch (Exception e) {
				throw new Exception(erro);
			}
		}
		if (getTipCampo().equals("F")) { // numero decimal
			try {
				CamposComOpcoes(campoAlter, alterarPara, descrCampo, tipCampoSql, nomeCampo);
				erro = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'."
						+ "\nExemplo de valores aceitos: 1, 2, 3... 10";

				String VirgulaParaPonto = alterarPara.trim().replace(",", ".");
				@SuppressWarnings("unused")
				BigDecimal numeroDecimal = new BigDecimal(VirgulaParaPonto);
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
			} catch (Exception e) {
				throw new Exception(erro);
			}
		}
		if (getTipCampo().equals("H")) {// data e hora
			try {
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
			} catch (Exception e) {
				throw new Exception(erro, null);
			}
		}
		if (getTipCampo().equals("I")) {// numero inteiro
			try {
				CamposComOpcoes(campoAlter, alterarPara, descrCampo, tipCampoSql, nomeCampo);
				erro = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'."
						+ "\nApenas valores numéricos inteiros são aceitos!. Exemplo: 1, 2, 3... 10";
				@SuppressWarnings("unused")
				Integer numeroInteiro = Integer.valueOf(alterarPara);
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'.";
			} catch (Exception e) {
				throw new Exception(erro, null);
			}
		}
		if (getTipCampo().equals("S")) {// texto
			try {
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + ".\n\n";
				CamposComOpcoes(campoAlter, alterarPara, descrCampo, tipCampoSql, nomeCampo);
			} catch (Exception e) {
				throw new Exception(erro);
			}
		}
		if (getTipCampo().equals("T")) {// hora
			try {
				observacao = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + "'";
			} catch (Exception e) {
				throw new Exception(erro);
			}
		}
	}

	public void CamposComOpcoes(BigDecimal campoAlter, String alterarPara, String descrCampo, String tipCampoSql,
			String nomeCampo) throws Exception {
		ColetaCamposTddopc opcoes = new ColetaCamposTddopc();
		String observacao1 = "O campo '" + descrCampo + "' é do tipo '" + tipCampoSql + " com varias opções.\n\n";
		opcoes.ValorAntigo(nomeCampo, observacao1);
		
		if (coletaOpcoes.getListaNucampoComOpcoes().contains(campoAlter) && opcoes.listaOpcoes.contains(alterarPara.toUpperCase())) {

			observacao = opcoes.getOpcoes();
		}
		if (coletaOpcoes.getListaNucampoComOpcoes().contains(campoAlter) && !opcoes.listaOpcoes.contains(alterarPara.toUpperCase())) {
			erro = opcoes.getOpcoes();
			throw new Exception();
		}
	}

	public void UpdateCampoObservacao(BigDecimal codAditivo, BigDecimal sequencia) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("AD_ITEADICON");
			DynamicVO servico = servicoExecutadoDAO
					.findOne(" CODADITIVO = " + codAditivo + " AND SEQUENCIA = " + sequencia);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("OBSERVACAO", this.observacao).update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static boolean checkDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public String getTipCampo() {
		return tipCampo;
	}
}
