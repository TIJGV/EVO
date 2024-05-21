package br.com.evonetwork.importarProdutos.Model;

import java.math.BigDecimal;

public class CamposImportacao {
	
	private String nomeCampo;
	private String nomeTabela;
	private String conteudoString;
	private BigDecimal conteudoNumero;
	private int tipoConteudo;
	
	public String getNomeCampo() {
		return nomeCampo;
	}
	
	public void setNomeCampo(String nomeCampo) {
		this.nomeCampo = nomeCampo;
	}

	public String getConteudoString() {
		return conteudoString;
	}

	public void setConteudoString(String conteudoString) {
		this.conteudoString = conteudoString;
	}

	public BigDecimal getConteudoNumero() {
		return conteudoNumero;
	}

	public void setConteudoNumero(BigDecimal conteudoNumero) {
		this.conteudoNumero = conteudoNumero;
	}

	public int getTipoConteudo() {
		return tipoConteudo;
	}

	public void setTipoConteudo(int tipoConteudo) {
		this.tipoConteudo = tipoConteudo;
	}

	public String getNomeTabela() {
		return nomeTabela;
	}

	public void setNomeTabela(String nomeTabela) {
		this.nomeTabela = nomeTabela;
	}
	
}
