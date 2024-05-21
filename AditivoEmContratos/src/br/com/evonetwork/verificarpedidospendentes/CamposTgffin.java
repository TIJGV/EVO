package br.com.evonetwork.verificarpedidospendentes;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.evonetwork.utils.Utils;


public class CamposTgffin {
	private BigDecimal codcencus;
	private BigDecimal codemp;
	private BigDecimal codmoeda;
	private BigDecimal codnat;
	private BigDecimal codparc;
	private BigDecimal codtiptit;
	private BigDecimal numcontrato;
	private BigDecimal numnota;
	private BigDecimal nunota;
	private BigDecimal recdesp;
	private BigDecimal vlrdesdob;
	private String provisao;
	private String dtneg;
	private String dtvenc;
	private String origem;
	private BigDecimal diapag;
		
	public BigDecimal getCodcencus() {
		return codcencus;
	}
	public void setCodcencus(BigDecimal codcencus) {
		this.codcencus = codcencus;
	}
	public BigDecimal getCodemp() {
		return codemp;
	}
	public void setCodemp(BigDecimal codemp) {
		this.codemp = codemp;
	}
	public BigDecimal getCodmoeda() {
		return codmoeda;
	}
	public void setCodmoeda(BigDecimal codmoeda) {
		this.codmoeda = codmoeda;
	}
	public BigDecimal getCodnat() {
		return codnat;
	}
	public void setCodnat(BigDecimal codnat) {
		this.codnat = codnat;
	}
	public BigDecimal getCodparc() {
		return codparc;
	}
	public void setCodparc(BigDecimal codparc) {
		this.codparc = codparc;
	}
	public BigDecimal getCodtiptit() {
		return codtiptit;
	}
	public void setCodtiptit(BigDecimal codtiptit) {
		this.codtiptit = codtiptit;
	}
	public BigDecimal getNumcontrato() {
		return numcontrato;
	}
	public void setNumcontrato(BigDecimal numcontrato) {
		this.numcontrato = numcontrato;
	}
	public BigDecimal getNumnota() {
		return numnota;
	}
	public void setNumnota(BigDecimal numnota) {
		this.numnota = numnota;
	}
	public BigDecimal getNunota() {
		return nunota;
	}
	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}
	public BigDecimal getRecdesp() {
		return recdesp;
	}
	public void setRecdesp(BigDecimal recdesp) {
		this.recdesp = recdesp;
	}
	public BigDecimal getVlrdesdob() {
		return vlrdesdob;
	}
	public void setVlrdesdob(BigDecimal vlrdesdob) {
		this.vlrdesdob = vlrdesdob;
	}
	public Timestamp getDtneg() throws Exception {
		return Utils.stringToTimestamp(dtneg);
	}
	public void setDtneg(String dtneg) {
		this.dtneg = dtneg;
	}
	public Timestamp getDtvenc() throws Exception {
		return Utils.stringToTimestamp(dtvenc);
	}
	public void setDtvenc(String dtvenc) {
		this.dtvenc = dtvenc;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getProvisao() {
		String provi = "";
		if(this.provisao.equals("P")) {
			provi = "S";
		}else {
			provi = "N"; 
		}
		return provi;
	}
	public void setProvisao(String provisao) {
		this.provisao = provisao;
	}
	public BigDecimal getDiapag() {
		return diapag;
	}
	public void setDiapag(BigDecimal diapag) {
		this.diapag = diapag;
	}

}
