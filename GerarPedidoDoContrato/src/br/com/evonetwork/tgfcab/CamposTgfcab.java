package br.com.evonetwork.tgfcab;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.evonetwork.util.Utils;

public class CamposTgfcab {
	private BigDecimal nunota;
	private BigDecimal codcencus;
	private BigDecimal codemp;
	private BigDecimal codnat;
	private BigDecimal codparc;
	private BigDecimal codtipoper;
	private BigDecimal codtipvenda;
	private BigDecimal numnota;
	private BigDecimal codusucomprador;
	private BigDecimal vlrtot;
	private String dtneg;
	private String dtentsai;
	private String observacao;
	private String serienota;
	private String dttermino;
	private int qtMeses;
	private BigDecimal numcontrato;
	
	Utils dt = new Utils();
	
	public BigDecimal getNunota() {
		return nunota;
	}
	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}
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
	public BigDecimal getCodtipoper() {
		return codtipoper;
	}
	public void setCodtipoper(BigDecimal codtipoper) {
		this.codtipoper = codtipoper;
	}
	public BigDecimal getCodtipvenda() {
		return codtipvenda;
	}
	public void setCodtipvenda(BigDecimal codtipvenda) {
		this.codtipvenda = codtipvenda;
	}
	public BigDecimal getNumnota() {
		return numnota;
	}
	public void setNumnota(BigDecimal numnota) {
		this.numnota = numnota;
	}
	public BigDecimal getCodusucomprador() {
		return codusucomprador;
	}
	public void setCodusucomprador(BigDecimal codusucomprador) {
		this.codusucomprador = codusucomprador;
	}
	public Timestamp getDtneg() throws Exception {
		return Utils.stringToTimestamp(dtneg);
	}
	public void setDtneg(String dtneg) {
		this.dtneg = dtneg;
	}
	public Timestamp getDtentsai() throws Exception {
		return Utils.stringToTimestamp(dtentsai);
	}
	public void setDtentsai(String dtentsai) {
		this.dtentsai = dtentsai;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getSerienota() {
		return serienota;
	}
	public void setSerienota(String serienota) {
		this.serienota = serienota;
	}
	public BigDecimal getVlrtot() {
		return vlrtot;
	}
	public void setVlrtot(BigDecimal vlrtot) {
		this.vlrtot = vlrtot;
	}
	public Timestamp getDttermino() throws Exception {
		return Utils.stringToTimestamp(dttermino);
	}
	public void setDttermino(String dttermino) {
		this.dttermino = dttermino;
	}
	public BigDecimal getNumcontrato() {
		return numcontrato;
	}
	public void setNumcontrato(BigDecimal numcontrato) {
		this.numcontrato = numcontrato;
	}
	public int getQtMeses() {
		return qtMeses;
	}
	public void setQtMeses(int qtMeses) {
		this.qtMeses = qtMeses;
	}
	@Override
	public String toString(){
		return "\nnunota: " + this.nunota +
				"\ncodcencus: " + this.codcencus +
				"\ncodemp: " + this.codemp +
				"\ncodnat: " + this.codnat +
				"\ncodparc: " + this.codparc +
				"\ncodtipoper: " + this.codtipoper +
				"\ncodtipvenda: " + this.codtipvenda +
				"\nnumnota: " + this.numnota +
				"\ncodusucomprador: " + this.codusucomprador +
				"\nvlrtot: " + this.vlrtot +
				"\ndtneg: " + this.dtneg +
				"\ndtentsai: " + this.dtentsai +
				"\nobservacao: " + this.observacao +
				"\nserienota: " + this.serienota +
				"\ndttermino: " + this.dttermino +
				"\nqtMeses: " + this.qtMeses;
	}
}
