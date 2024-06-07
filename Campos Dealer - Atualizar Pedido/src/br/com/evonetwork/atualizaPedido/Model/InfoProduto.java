package br.com.evonetwork.atualizaPedido.Model;

import java.math.BigDecimal;

public class InfoProduto {
	private String codvol;
	private BigDecimal codtipvenda;
	private BigDecimal codtipoper;

	public String getCodvol() {
		return codvol;
	}

	public void setCodvol(String codvol) {
		this.codvol = codvol;
	}

	public BigDecimal getCodtipvenda() {
		return codtipvenda;
	}

	public void setCodtipvenda(BigDecimal codtipvenda) {
		this.codtipvenda = codtipvenda;
	}

	public BigDecimal getCodtipoper() {
		return codtipoper;
	}

	public void setCodtipoper(BigDecimal codtipoper) {
		this.codtipoper = codtipoper;
	}

}
