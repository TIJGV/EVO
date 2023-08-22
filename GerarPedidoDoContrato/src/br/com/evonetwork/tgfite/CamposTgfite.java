package br.com.evonetwork.tgfite;

import java.math.BigDecimal;

public class CamposTgfite {
	private BigDecimal vlrunit;
	private BigDecimal numcontrato;
	private BigDecimal codprod;
	private BigDecimal nunota;
	private String codvol;
	private BigDecimal qtdneg;

	public BigDecimal getVlrunit() {
		return vlrunit;
	}
	public void setVlrunit(BigDecimal vlrunit) {
		this.vlrunit = vlrunit;
	}
	public BigDecimal getNumcontrato() {
		return numcontrato;
	}
	public void setNumcontrato(BigDecimal numcontrato) {
		this.numcontrato = numcontrato;
	}
	public BigDecimal getCodprod() {
		return codprod;
	}
	public void setCodprod(BigDecimal codprod) {
		this.codprod = codprod;
	}
	public BigDecimal getNunota() {
		return nunota;
	}
	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}
	public String getCodvol() {
		return codvol;
	}
	public void setCodvol(String codvol) {
		this.codvol = codvol;
	}
	public BigDecimal getQtdneg() {
		return qtdneg;
	}
	public void setQtdneg(BigDecimal qtdneg) {
		this.qtdneg = qtdneg;
	}
}
