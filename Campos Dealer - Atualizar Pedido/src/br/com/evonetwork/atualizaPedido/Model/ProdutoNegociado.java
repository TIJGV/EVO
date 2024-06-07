package br.com.evonetwork.atualizaPedido.Model;

import java.math.BigDecimal;

public class ProdutoNegociado {
	private BigDecimal codprod;
	private BigDecimal qtdneg;
	private BigDecimal vlrunit;
	private BigDecimal codtipvenda;
	private BigDecimal codtipoper;
	private String codvol;
	private BigDecimal codlocal;
	private String controle;

	public BigDecimal getCodprod() {
		return codprod;
	}

	public void setCodprod(BigDecimal codprod) {
		this.codprod = codprod;
	}

	public BigDecimal getQtdneg() {
		return qtdneg;
	}

	public void setQtdneg(BigDecimal qtdneg) {
		this.qtdneg = qtdneg;
	}

	public BigDecimal getVlrunit() {
		return vlrunit;
	}

	public void setVlrunit(BigDecimal vlrunit) {
		this.vlrunit = vlrunit;
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

	public String getCodvol() {
		return codvol;
	}

	public void setCodvol(String codvol) {
		this.codvol = codvol;
	}

	public BigDecimal getCodlocal() {
		return codlocal;
	}

	public void setCodlocal(BigDecimal codlocal) {
		this.codlocal = codlocal;
	}

	public String getControle() {
		return controle;
	}

	public void setControle(String controle) {
		this.controle = controle;
	}

	
}
