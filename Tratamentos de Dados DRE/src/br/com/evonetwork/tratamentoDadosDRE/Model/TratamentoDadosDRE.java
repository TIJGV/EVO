package br.com.evonetwork.tratamentoDadosDRE.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TratamentoDadosDRE {
	
	private BigDecimal codCenCus;
	private BigDecimal codAgrupDRE;
	private BigDecimal codCtaCtb;
	private BigDecimal codEmp;
	private BigDecimal codUng;
	private BigDecimal numLote;
	private BigDecimal numLancamento;
	private BigDecimal vlrLancamento;
	
	private String tipoLancamento;
	
	private Timestamp referencia;
	
	public TratamentoDadosDRE() {}

	public BigDecimal getCodCenCus() {
		return codCenCus;
	}

	public void setCodCenCus(BigDecimal codCenCus) {
		this.codCenCus = codCenCus;
	}

	public BigDecimal getCodAgrupDRE() {
		return codAgrupDRE;
	}

	public void setCodAgrupDRE(BigDecimal codAgrupDRE) {
		this.codAgrupDRE = codAgrupDRE;
	}

	public BigDecimal getCodCtaCtb() {
		return codCtaCtb;
	}

	public void setCodCtaCtb(BigDecimal codCtaCtb) {
		this.codCtaCtb = codCtaCtb;
	}

	public BigDecimal getCodEmp() {
		return codEmp;
	}

	public void setCodEmp(BigDecimal codEmp) {
		this.codEmp = codEmp;
	}

	public BigDecimal getCodUng() {
		return codUng;
	}

	public void setCodUng(BigDecimal codUng) {
		this.codUng = codUng;
	}

	public BigDecimal getNumLote() {
		return numLote;
	}

	public void setNumLote(BigDecimal numLote) {
		this.numLote = numLote;
	}

	public BigDecimal getNumLancamento() {
		return numLancamento;
	}

	public void setNumLancamento(BigDecimal numLancamento) {
		this.numLancamento = numLancamento;
	}

	public BigDecimal getVlrLancamento() {
		return vlrLancamento;
	}

	public void setVlrLancamento(BigDecimal vlrLancamento) {
		this.vlrLancamento = vlrLancamento;
	}

	public String getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(String tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	public Timestamp getReferencia() {
		return referencia;
	}

	public void setReferencia(Timestamp referencia) {
		this.referencia = referencia;
	}

}
