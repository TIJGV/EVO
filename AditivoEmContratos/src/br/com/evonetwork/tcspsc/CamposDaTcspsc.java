package br.com.evonetwork.tcspsc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamposDaTcspsc {
	//bigdecimal
	private BigDecimal topfaturcon;
	private BigDecimal vlrunit;
	private BigDecimal qtdmeses;
	private BigDecimal qtdeprevista;
	private BigDecimal numusuarios;
	private BigDecimal numcontrato;
	private BigDecimal grupimpressao;
	private BigDecimal frequencia;
	private BigDecimal codprod;
	private BigDecimal codparcpref;
	private BigDecimal qtdusu;

	//STRING
	private String limitante;
	private String impros;
	private String imprnota;
	private String versao;
	private String serfaturcon;
	private String observacao;
	private String sitprod;
	private String numserie;
	private String ad_codbem;
	private String prodprinc;

	//DATA
	private String dtversao;
	
	public BigDecimal getTopfaturcon() {
		return topfaturcon;
	}
	public void setTopfaturcon(BigDecimal topfaturcon) {
		this.topfaturcon = topfaturcon;
	}
	public BigDecimal getVlrunit() {
		return vlrunit;
	}
	public void setVlrunit(BigDecimal vlrunit) {
		this.vlrunit = vlrunit;
	}
	public BigDecimal getQtdmeses() {
		return qtdmeses;
	}
	public void setQtdmeses(BigDecimal qtdmeses) {
		this.qtdmeses = qtdmeses;
	}
	public BigDecimal getQtdeprevista() {
		return qtdeprevista;
	}
	public void setQtdeprevista(BigDecimal qtdeprevista) {
		this.qtdeprevista = qtdeprevista;
	}
	public BigDecimal getNumusuarios() {
		return numusuarios;
	}
	public void setNumusuarios(BigDecimal numusuarios) {
		this.numusuarios = numusuarios;
	}
	public BigDecimal getNumcontrato() {
		return numcontrato;
	}
	public void setNumcontrato(BigDecimal numcontrato) {
		this.numcontrato = numcontrato;
	}
	public BigDecimal getGrupimpressao() {
		return grupimpressao;
	}
	public void setGrupimpressao(BigDecimal grupimpressao) {
		this.grupimpressao = grupimpressao;
	}
	public BigDecimal getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(BigDecimal frequencia) {
		this.frequencia = frequencia;
	}
	public BigDecimal getCodprod() {
		return codprod;
	}
	public void setCodprod(BigDecimal codprod) {
		this.codprod = codprod;
	}
	public BigDecimal getCodparcpref() {
		return codparcpref;
	}
	public void setCodparcpref(BigDecimal codparcpref) {
		this.codparcpref = codparcpref;
	}
	public BigDecimal getQtdusu() {
		return qtdusu;
	}
	public void setQtdusu(BigDecimal qtdusu) {
		this.qtdusu = qtdusu;
	}
	public String getLimitante() {
		return limitante;
	}
	public void setLimitante(String limitante) {
		this.limitante = limitante;
	}
	public String getImpros() {
		return impros;
	}
	public void setImpros(String impros) {
		this.impros = impros;
	}
	public String getImprnota() {
		return imprnota;
	}
	public void setImprnota(String imprnota) {
		this.imprnota = imprnota;
	}
	public String getVersao() {
		return versao;
	}
	public void setVersao(String versao) {
		this.versao = versao;
	}
	public String getSerfaturcon() {
		return serfaturcon;
	}
	public void setSerfaturcon(String serfaturcon) {
		this.serfaturcon = serfaturcon;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getSitprod() {
		return sitprod;
	}
	public void setSitprod(String sitprod) {
		this.sitprod = sitprod;
	}
	public String getNumserie() {
		return numserie;
	}
	public void setNumserie(String numserie) {
		this.numserie = numserie;
	}
	public String getAd_codbem() {
		return ad_codbem;
	}
	public void setAd_codbem(String ad_codbem) {
		this.ad_codbem = ad_codbem;
	}
	public String getProdprinc() {
		return prodprinc;
	}
	public void setProdprinc(String prodprinc) {
		this.prodprinc = prodprinc;
	}
	public String getDtversao() {
		return convertDate(dtversao);
	}
	public void setDtversao(String dtversao) {
		this.dtversao = dtversao;
	}
	public String convertDate(String mDate) {
		if (mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date newDate = inputFormat.parse(mDate);
			inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			mDate = inputFormat.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDate;
	}
	@Override
	public String toString() {
		return "\nTOPFATURCON: " + topfaturcon +
				"\nVLRUNIT: " + vlrunit +
				"\nQTDMESES: " + qtdmeses +
				"\nQTDEPREVISTA: " + qtdeprevista +
				"\nNUMUSUARIOS: " + numusuarios +
				"\nNUMCONTRATO: " + numcontrato +
				"\nGRUPIMPRESSAO: " + grupimpressao +
				"\nFREQUENCIA: " + frequencia +
				"\nCODPROD: " + codprod +
				"\nCODPARCPREF: " + codparcpref +
				"\nQTDUSU: " + qtdusu +
				"\nLIMITANTE: " + limitante +
				"\nIMPROS: " + impros +
				"\nIMPRNOTA: " + imprnota +
				"\nVERSAO: " + versao +
				"\nSERFATURCON: " + serfaturcon +
				"\nOBSERVACAO: " + observacao +
				"\nSITPROD: " + sitprod +
				"\nNUMSERIE: " + numserie +
				"\nAD_CODBEM: " + ad_codbem +
				"\nPRODPRINC: " + prodprinc +
				"\nDTVERSAO: " + dtversao;
	}
}
