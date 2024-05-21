package br.com.evonetwork.adtcsose;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class DadosParaCab {
	private BigDecimal nunota;
	private BigDecimal codparc;
	private BigDecimal codcencus;
	private BigDecimal codnat;
	private BigDecimal codemp;
	private BigDecimal codtipvenda;
	private Timestamp dtneg;
	private BigDecimal numnota;
	private Timestamp dtentsai;
	private String serienota;
	private BigDecimal codtipoper;
	private String statusnota;
	

	public BigDecimal getCodparc() {
		return codparc;
	}
	public void setCodparc(BigDecimal codparc) {
		this.codparc = codparc;
	}
	public BigDecimal getCodcencus() {
		return codcencus;
	}
	public void setCodcencus(BigDecimal codcencus) {
		this.codcencus = codcencus;
	}
	public BigDecimal getCodnat() {
		return codnat;
	}
	public void setCodnat(BigDecimal codnat) {
		this.codnat = codnat;
	}
	public BigDecimal getCodemp() {
		return codemp;
	}
	public void setCodemp(BigDecimal codemp) {
		this.codemp = codemp;
	}
	public BigDecimal getCodtipvenda() {
		return codtipvenda;
	}
	public void setCodtipvenda(BigDecimal codtipvenda) {
		this.codtipvenda = codtipvenda;
	}
	public Timestamp getDtneg() {
		return dtneg;
	}
	public void setDtneg(Timestamp dtneg) {
		this.dtneg = dtneg;
	}
	public BigDecimal getNumnota() {
		return numnota;
	}
	public void setNumnota(BigDecimal numnota) {
		this.numnota = numnota;
	}
	public Timestamp getDtentsai() {
		return dtentsai;
	}
	public void setDtentsai(Timestamp dtentsai) {
		this.dtentsai = dtentsai;
	}
	public String getSerienota() {
		return serienota;
	}
	public void setSerienota(String serienota) {
		this.serienota = serienota;
	}
	public BigDecimal getCodtipoper() {
		return codtipoper;
	}
	public void setCodtipoper(BigDecimal codtipoper) {
		this.codtipoper = codtipoper;
	}
	public String getStatusnota() {
		return statusnota;
	}
	public void setStatusnota(String statusnota) {
		this.statusnota = statusnota;
	}
	public BigDecimal getNunota() {
		return nunota;
	}
	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}
	
	@Override
	public String toString() {
		return "nunota: " + nunota +
				"\ncodparc: " + codparc +
				"\ncodcencus: " + codcencus +
				"\ncodnat: " + codnat +
				"\ncodemp: " + codemp +
				"\ncodtipvenda: " + codtipvenda +
				"\ndtneg: " + dtneg +
				"\nnumnota: " + numnota +
				"\ndtentsai: " + dtentsai +
				"\nserienota: " + serienota +
				"\ncodtipoper: " + codtipoper +
				"\nstatusnota: " + statusnota;
	}
}
