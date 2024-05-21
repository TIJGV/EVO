package br.com.evonetwork.tgfite;

import java.math.BigDecimal;

public class DadosTgfite {
	private BigDecimal nunota;
	private BigDecimal vlrunit;
	private BigDecimal vlrtot;
	private BigDecimal numcontrato;
	private BigDecimal codprod;
	private String codvol;
	private BigDecimal qtdneg;
	private BigDecimal sequencia;
	private BigDecimal descontoreais;
	private BigDecimal descontopctgm;
	
	public BigDecimal getNunota() {
		return nunota;
	}
	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}
	public BigDecimal getVlrunit() {
		return vlrunit;
	}
	public void setVlrunit(BigDecimal vlrunit) {
		this.vlrunit = vlrunit;
	}
	public BigDecimal getVlrtot() {
		return vlrtot;
	}
	public void setVlrtot(BigDecimal vlrtot) {
		this.vlrtot = vlrtot;
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
	
	public BigDecimal getSequencia() {
		return sequencia;
	}
	public void setSequencia(BigDecimal sequencia) {
		this.sequencia = sequencia;
	}
	public BigDecimal getDescontoreais() {
		return descontoreais;
	}
	public void setDescontoreais(BigDecimal descontoreais) {
		this.descontoreais = descontoreais;
	}
	public BigDecimal getDescontopctgm() {
		return descontopctgm;
	}
	public void setDescontopctgm(BigDecimal descontopctgm) {
		this.descontopctgm = descontopctgm;
	}
	@Override
	public String toString() {
		return "nunota" + nunota +
		"\nvlrunit" + vlrunit +
		"\nvlrtot" + vlrtot +
		"\nnumcontrato" + numcontrato +
		"\ncodprod" + codprod +
		"\ncodvol" + codvol +
		"\nqtdneg" + qtdneg;
	}
}
