package br.com.evonetwork.tcscon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamposDaTcscon {
	//************************bigdecimal*************************
	private BigDecimal topfaturcon;
	private BigDecimal qtdprovisao;
	private BigDecimal codcid;
	private BigDecimal tipotitulo;
	private BigDecimal qtdparcpgcom;
	private BigDecimal recdesp;
	private BigDecimal codcencus;
	private BigDecimal codcontato;
	private BigDecimal codcriterio;
	private BigDecimal codemp;
	private BigDecimal codimplant;
	private BigDecimal codmoealtreaj;
	private BigDecimal codmoeda;
	private BigDecimal codmonsankhya;
	private BigDecimal codnat;
	private BigDecimal codparc;
	private BigDecimal codparcprest;
	private BigDecimal codparcsec;
	private BigDecimal codproj;
	private BigDecimal codprojsint;
	private BigDecimal codtipvenda;
	private BigDecimal codusu;
	private BigDecimal diafimmed;
	private BigDecimal diapag;
	private BigDecimal freqreaj;
	private BigDecimal freqvisitas;
	private BigDecimal gatilho;
	private BigDecimal numcontratoorigem;
	private BigDecimal nunotapedloc;
	private BigDecimal nusla;
	private BigDecimal parcelaatual;
	private BigDecimal parcelaqtd;
	private BigDecimal percirf;
	private BigDecimal perciss;
	private BigDecimal percloc;
	private BigDecimal prazovencto;
	private BigDecimal codusualter;
	private BigDecimal codservex;
	private BigDecimal codcencusar;
	private BigDecimal codcencusex;
	private BigDecimal codnatar;
	private BigDecimal codnatex;
	private BigDecimal codsaf;
	private BigDecimal codtipvendaar;
	private BigDecimal codtipvendaex;
	private BigDecimal diacarecar;
	private BigDecimal diacarenc;
	private BigDecimal diacarencex;
	private BigDecimal padclass;
	private BigDecimal quebratec;
	private BigDecimal resppagar;
	private BigDecimal tabprecumi;
	private BigDecimal tabprecumiar;
	private BigDecimal tipotituloar;
	private BigDecimal tipotituloex;
	private BigDecimal umidpadra;
	private BigDecimal uniconvsc;
	private BigDecimal ad_codtipcon;
	private BigDecimal codobs;
	private BigDecimal prazomensal;
	private BigDecimal nunota;
	private BigDecimal codclc;
	private BigDecimal valquebtrans;
	private BigDecimal nunotarefarmaze;
	private BigDecimal nunotarefexprec;
	private BigDecimal numcstc;
	private BigDecimal ad_vlrcontrato;

	//************************string*************************
	private String temirf;
	private String temiss;
	private String temmed;
	private String retemiss;
	private String acessahistsubos;
	private String ambiente;
	private String ativo;
	private String clauscontrato;
	private String diautil;
	private String equipamento;
	private String feriadoest;
	private String feriadomun;
	private String feriadonac;
	private String gerarnf;
	private String imprime;
	private String imprprecindiv;
	private String observacoes;
	private String reajustenegativo;
	private String gerarfinnota;
	private String locacaobem;
	private String numcontin;
	private String cobproporcar;
	private String deftipa;
	private String percobra;
	private String percobraar;
	private String perdesc;
	private String perdescon;
	private String sitcont;
	private String tipcobr;
	private String tipoarm;
	private String tipquebra;
	private String ulttabumi;
	private String valpedfin;
	private String cobproque;
	private String reglaudsaida;
	private String diafixo;
	private String tipisencao;
	private String formfatarmaze;
	private String formfatexprec;
	private String cif_fob;
	private String tipo;
	private String tippag;
	private String tipocontrato;
	private String controlocbens;
	private String serfaturcon;
	private String grupofaturprorata;
	private String controrgpublico;
	private String faturprorata;

	//***********************************data**************
	private String dtbasereaj;
	private String dttermino;
	private String dtrefexprec;
	private String dtrefarmaze;
	private String dtrefproxfat;
	private String dtenvioemail;
	private String dtcontrato;
	
	//****************************hora******************
	private String duracao;

	public BigDecimal getTopfaturcon() {
		return topfaturcon;
	}

	public void setTopfaturcon(BigDecimal topfaturcon) {
		this.topfaturcon = topfaturcon;
	}

	public BigDecimal getQtdprovisao() {
		return qtdprovisao;
	}

	public void setQtdprovisao(BigDecimal qtdprovisao) {
		this.qtdprovisao = qtdprovisao;
	}

	public BigDecimal getCodcid() {
		return codcid;
	}

	public void setCodcid(BigDecimal codcid) {
		this.codcid = codcid;
	}

	public BigDecimal getTipotitulo() {
		return tipotitulo;
	}

	public void setTipotitulo(BigDecimal tipotitulo) {
		this.tipotitulo = tipotitulo;
	}

	public BigDecimal getQtdparcpgcom() {
		return qtdparcpgcom;
	}

	public void setQtdparcpgcom(BigDecimal qtdparcpgcom) {
		this.qtdparcpgcom = qtdparcpgcom;
	}

	public BigDecimal getRecdesp() {
		return recdesp;
	}

	public void setRecdesp(BigDecimal recdesp) {
		this.recdesp = recdesp;
	}

	public BigDecimal getCodcencus() {
		return codcencus;
	}

	public void setCodcencus(BigDecimal codcencus) {
		this.codcencus = codcencus;
	}

	public BigDecimal getCodcontato() {
		return codcontato;
	}

	public void setCodcontato(BigDecimal codcontato) {
		this.codcontato = codcontato;
	}

	public BigDecimal getCodcriterio() {
		return codcriterio;
	}

	public void setCodcriterio(BigDecimal codcriterio) {
		this.codcriterio = codcriterio;
	}

	public BigDecimal getCodemp() {
		return codemp;
	}

	public void setCodemp(BigDecimal codemp) {
		this.codemp = codemp;
	}

	public BigDecimal getCodimplant() {
		return codimplant;
	}

	public void setCodimplant(BigDecimal codimplant) {
		this.codimplant = codimplant;
	}

	public BigDecimal getCodmoealtreaj() {
		return codmoealtreaj;
	}

	public void setCodmoealtreaj(BigDecimal codmoealtreaj) {
		this.codmoealtreaj = codmoealtreaj;
	}

	public BigDecimal getCodmoeda() {
		return codmoeda;
	}

	public void setCodmoeda(BigDecimal codmoeda) {
		this.codmoeda = codmoeda;
	}

	public BigDecimal getCodmonsankhya() {
		return codmonsankhya;
	}

	public void setCodmonsankhya(BigDecimal codmonsankhya) {
		this.codmonsankhya = codmonsankhya;
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

	public BigDecimal getCodparcprest() {
		return codparcprest;
	}

	public void setCodparcprest(BigDecimal codparcprest) {
		this.codparcprest = codparcprest;
	}

	public BigDecimal getCodparcsec() {
		return codparcsec;
	}

	public void setCodparcsec(BigDecimal codparcsec) {
		this.codparcsec = codparcsec;
	}

	public BigDecimal getCodproj() {
		return codproj;
	}

	public void setCodproj(BigDecimal codproj) {
		this.codproj = codproj;
	}

	public BigDecimal getCodprojsint() {
		return codprojsint;
	}

	public void setCodprojsint(BigDecimal codprojsint) {
		this.codprojsint = codprojsint;
	}

	public BigDecimal getCodtipvenda() {
		return codtipvenda;
	}

	public void setCodtipvenda(BigDecimal codtipvenda) {
		this.codtipvenda = codtipvenda;
	}

	public BigDecimal getCodusu() {
		return codusu;
	}

	public void setCodusu(BigDecimal codusu) {
		this.codusu = codusu;
	}

	public BigDecimal getDiafimmed() {
		return diafimmed;
	}

	public void setDiafimmed(BigDecimal diafimmed) {
		this.diafimmed = diafimmed;
	}

	public BigDecimal getDiapag() {
		return diapag;
	}

	public void setDiapag(BigDecimal diapag) {
		this.diapag = diapag;
	}

	public BigDecimal getFreqreaj() {
		return freqreaj;
	}

	public void setFreqreaj(BigDecimal freqreaj) {
		this.freqreaj = freqreaj;
	}

	public BigDecimal getFreqvisitas() {
		return freqvisitas;
	}

	public void setFreqvisitas(BigDecimal freqvisitas) {
		this.freqvisitas = freqvisitas;
	}

	public BigDecimal getGatilho() {
		return gatilho;
	}

	public void setGatilho(BigDecimal gatilho) {
		this.gatilho = gatilho;
	}
	public BigDecimal getNumcontratoorigem() {
		return numcontratoorigem;
	}

	public void setNumcontratoorigem(BigDecimal numcontratoorigem) {
		this.numcontratoorigem = numcontratoorigem;
	}

	public BigDecimal getNunotapedloc() {
		return nunotapedloc;
	}

	public void setNunotapedloc(BigDecimal nunotapedloc) {
		this.nunotapedloc = nunotapedloc;
	}

	public BigDecimal getNusla() {
		return nusla;
	}

	public void setNusla(BigDecimal nusla) {
		this.nusla = nusla;
	}

	public BigDecimal getParcelaatual() {
		return parcelaatual;
	}

	public void setParcelaatual(BigDecimal parcelaatual) {
		this.parcelaatual = parcelaatual;
	}

	public BigDecimal getParcelaqtd() {
		return parcelaqtd;
	}

	public void setParcelaqtd(BigDecimal parcelaqtd) {
		this.parcelaqtd = parcelaqtd;
	}

	public BigDecimal getPercirf() {
		return percirf;
	}

	public void setPercirf(BigDecimal percirf) {
		this.percirf = percirf;
	}

	public BigDecimal getPerciss() {
		return perciss;
	}

	public void setPerciss(BigDecimal perciss) {
		this.perciss = perciss;
	}

	public BigDecimal getPercloc() {
		return percloc;
	}

	public void setPercloc(BigDecimal percloc) {
		this.percloc = percloc;
	}

	public BigDecimal getPrazovencto() {
		return prazovencto;
	}

	public void setPrazovencto(BigDecimal prazovencto) {
		this.prazovencto = prazovencto;
	}

	public BigDecimal getCodusualter() {
		return codusualter;
	}

	public void setCodusualter(BigDecimal codusualter) {
		this.codusualter = codusualter;
	}

	public BigDecimal getCodservex() {
		return codservex;
	}

	public void setCodservex(BigDecimal codservex) {
		this.codservex = codservex;
	}

	public BigDecimal getCodcencusar() {
		return codcencusar;
	}

	public void setCodcencusar(BigDecimal codcencusar) {
		this.codcencusar = codcencusar;
	}

	public BigDecimal getCodcencusex() {
		return codcencusex;
	}

	public void setCodcencusex(BigDecimal codcencusex) {
		this.codcencusex = codcencusex;
	}

	public BigDecimal getCodnatar() {
		return codnatar;
	}

	public void setCodnatar(BigDecimal codnatar) {
		this.codnatar = codnatar;
	}

	public BigDecimal getCodnatex() {
		return codnatex;
	}

	public void setCodnatex(BigDecimal codnatex) {
		this.codnatex = codnatex;
	}

	public BigDecimal getCodsaf() {
		return codsaf;
	}

	public void setCodsaf(BigDecimal codsaf) {
		this.codsaf = codsaf;
	}

	public BigDecimal getCodtipvendaar() {
		return codtipvendaar;
	}

	public void setCodtipvendaar(BigDecimal codtipvendaar) {
		this.codtipvendaar = codtipvendaar;
	}

	public BigDecimal getCodtipvendaex() {
		return codtipvendaex;
	}

	public void setCodtipvendaex(BigDecimal codtipvendaex) {
		this.codtipvendaex = codtipvendaex;
	}

	public BigDecimal getDiacarecar() {
		return diacarecar;
	}

	public void setDiacarecar(BigDecimal diacarecar) {
		this.diacarecar = diacarecar;
	}

	public BigDecimal getDiacarenc() {
		return diacarenc;
	}

	public void setDiacarenc(BigDecimal diacarenc) {
		this.diacarenc = diacarenc;
	}

	public BigDecimal getDiacarencex() {
		return diacarencex;
	}

	public void setDiacarencex(BigDecimal diacarencex) {
		this.diacarencex = diacarencex;
	}

	public BigDecimal getPadclass() {
		return padclass;
	}

	public void setPadclass(BigDecimal padclass) {
		this.padclass = padclass;
	}

	public BigDecimal getQuebratec() {
		return quebratec;
	}

	public void setQuebratec(BigDecimal quebratec) {
		this.quebratec = quebratec;
	}

	public BigDecimal getResppagar() {
		return resppagar;
	}

	public void setResppagar(BigDecimal resppagar) {
		this.resppagar = resppagar;
	}

	public BigDecimal getTabprecumi() {
		return tabprecumi;
	}

	public void setTabprecumi(BigDecimal tabprecumi) {
		this.tabprecumi = tabprecumi;
	}

	public BigDecimal getTabprecumiar() {
		return tabprecumiar;
	}

	public void setTabprecumiar(BigDecimal tabprecumiar) {
		this.tabprecumiar = tabprecumiar;
	}

	public BigDecimal getTipotituloar() {
		return tipotituloar;
	}

	public void setTipotituloar(BigDecimal tipotituloar) {
		this.tipotituloar = tipotituloar;
	}

	public BigDecimal getTipotituloex() {
		return tipotituloex;
	}

	public void setTipotituloex(BigDecimal tipotituloex) {
		this.tipotituloex = tipotituloex;
	}

	public BigDecimal getUmidpadra() {
		return umidpadra;
	}

	public void setUmidpadra(BigDecimal umidpadra) {
		this.umidpadra = umidpadra;
	}

	public BigDecimal getUniconvsc() {
		return uniconvsc;
	}

	public void setUniconvsc(BigDecimal uniconvsc) {
		this.uniconvsc = uniconvsc;
	}

	public BigDecimal getAd_codtipcon() {
		return ad_codtipcon;
	}

	public void setAd_codtipcon(BigDecimal ad_codtipcon) {
		this.ad_codtipcon = ad_codtipcon;
	}

	public BigDecimal getCodobs() {
		return codobs;
	}

	public void setCodobs(BigDecimal codobs) {
		this.codobs = codobs;
	}

	public BigDecimal getPrazomensal() {
		return prazomensal;
	}

	public void setPrazomensal(BigDecimal prazomensal) {
		this.prazomensal = prazomensal;
	}

	public BigDecimal getNunota() {
		return nunota;
	}

	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}

	public BigDecimal getCodclc() {
		return codclc;
	}

	public void setCodclc(BigDecimal codclc) {
		this.codclc = codclc;
	}

	public BigDecimal getValquebtrans() {
		return valquebtrans;
	}

	public void setValquebtrans(BigDecimal valquebtrans) {
		this.valquebtrans = valquebtrans;
	}

	public BigDecimal getNunotarefarmaze() {
		return nunotarefarmaze;
	}

	public void setNunotarefarmaze(BigDecimal nunotarefarmaze) {
		this.nunotarefarmaze = nunotarefarmaze;
	}

	public BigDecimal getNunotarefexprec() {
		return nunotarefexprec;
	}

	public void setNunotarefexprec(BigDecimal nunotarefexprec) {
		this.nunotarefexprec = nunotarefexprec;
	}

	public BigDecimal getNumcstc() {
		return numcstc;
	}

	public void setNumcstc(BigDecimal numcstc) {
		this.numcstc = numcstc;
	}

	public BigDecimal getAd_vlrcontrato() {
		return ad_vlrcontrato;
	}

	public void setAd_vlrcontrato(BigDecimal ad_vlrcontrato) {
		this.ad_vlrcontrato = ad_vlrcontrato;
	}

	public String getTemirf() {
		return temirf;
	}

	public void setTemirf(String temirf) {
		this.temirf = temirf;
	}

	public String getTemiss() {
		return temiss;
	}

	public void setTemiss(String temiss) {
		this.temiss = temiss;
	}

	public String getTemmed() {
		return temmed;
	}

	public void setTemmed(String temmed) {
		this.temmed = temmed;
	}

	public String getRetemiss() {
		return retemiss;
	}

	public void setRetemiss(String retemiss) {
		this.retemiss = retemiss;
	}

	public String getAcessahistsubos() {
		return acessahistsubos;
	}

	public void setAcessahistsubos(String acessahistsubos) {
		this.acessahistsubos = acessahistsubos;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getAtivo() {
		return ativo;
	}

	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}

	public String getClauscontrato() {
		return clauscontrato;
	}

	public void setClauscontrato(String clauscontrato) {
		this.clauscontrato = clauscontrato;
	}

	public String getDiautil() {
		return diautil;
	}

	public void setDiautil(String diautil) {
		this.diautil = diautil;
	}

	public String getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	public String getFeriadoest() {
		return feriadoest;
	}

	public void setFeriadoest(String feriadoest) {
		this.feriadoest = feriadoest;
	}

	public String getFeriadomun() {
		return feriadomun;
	}

	public void setFeriadomun(String feriadomun) {
		this.feriadomun = feriadomun;
	}

	public String getFeriadonac() {
		return feriadonac;
	}

	public void setFeriadonac(String feriadonac) {
		this.feriadonac = feriadonac;
	}

	public String getGerarnf() {
		return gerarnf;
	}

	public void setGerarnf(String gerarnf) {
		this.gerarnf = gerarnf;
	}

	public String getImprime() {
		return imprime;
	}

	public void setImprime(String imprime) {
		this.imprime = imprime;
	}

	public String getImprprecindiv() {
		return imprprecindiv;
	}

	public void setImprprecindiv(String imprprecindiv) {
		this.imprprecindiv = imprprecindiv;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getReajustenegativo() {
		return reajustenegativo;
	}

	public void setReajustenegativo(String reajustenegativo) {
		this.reajustenegativo = reajustenegativo;
	}

	public String getGerarfinnota() {
		return gerarfinnota;
	}

	public void setGerarfinnota(String gerarfinnota) {
		this.gerarfinnota = gerarfinnota;
	}

	public String getLocacaobem() {
		return locacaobem;
	}

	public void setLocacaobem(String locacaobem) {
		this.locacaobem = locacaobem;
	}

	public String getNumcontin() {
		return numcontin;
	}

	public void setNumcontin(String numcontin) {
		this.numcontin = numcontin;
	}

	public String getCobproporcar() {
		return cobproporcar;
	}

	public void setCobproporcar(String cobproporcar) {
		this.cobproporcar = cobproporcar;
	}

	public String getDeftipa() {
		return deftipa;
	}

	public void setDeftipa(String deftipa) {
		this.deftipa = deftipa;
	}

	public String getPercobra() {
		return percobra;
	}

	public void setPercobra(String percobra) {
		this.percobra = percobra;
	}

	public String getPercobraar() {
		return percobraar;
	}

	public void setPercobraar(String percobraar) {
		this.percobraar = percobraar;
	}

	public String getPerdesc() {
		return perdesc;
	}

	public void setPerdesc(String perdesc) {
		this.perdesc = perdesc;
	}

	public String getPerdescon() {
		return perdescon;
	}

	public void setPerdescon(String perdescon) {
		this.perdescon = perdescon;
	}

	public String getSitcont() {
		return sitcont;
	}

	public void setSitcont(String sitcont) {
		this.sitcont = sitcont;
	}

	public String getTipcobr() {
		return tipcobr;
	}

	public void setTipcobr(String tipcobr) {
		this.tipcobr = tipcobr;
	}

	public String getTipoarm() {
		return tipoarm;
	}

	public void setTipoarm(String tipoarm) {
		this.tipoarm = tipoarm;
	}

	public String getTipquebra() {
		return tipquebra;
	}

	public void setTipquebra(String tipquebra) {
		this.tipquebra = tipquebra;
	}

	public String getUlttabumi() {
		return ulttabumi;
	}

	public void setUlttabumi(String ulttabumi) {
		this.ulttabumi = ulttabumi;
	}

	public String getValpedfin() {
		return valpedfin;
	}

	public void setValpedfin(String valpedfin) {
		this.valpedfin = valpedfin;
	}

	public String getCobproque() {
		return cobproque;
	}

	public void setCobproque(String cobproque) {
		this.cobproque = cobproque;
	}

	public String getReglaudsaida() {
		return reglaudsaida;
	}

	public void setReglaudsaida(String reglaudsaida) {
		this.reglaudsaida = reglaudsaida;
	}

	public String getDiafixo() {
		return diafixo;
	}

	public void setDiafixo(String diafixo) {
		this.diafixo = diafixo;
	}

	public String getTipisencao() {
		return tipisencao;
	}

	public void setTipisencao(String tipisencao) {
		this.tipisencao = tipisencao;
	}

	public String getFormfatarmaze() {
		return formfatarmaze;
	}

	public void setFormfatarmaze(String formfatarmaze) {
		this.formfatarmaze = formfatarmaze;
	}

	public String getFormfatexprec() {
		return formfatexprec;
	}

	public void setFormfatexprec(String formfatexprec) {
		this.formfatexprec = formfatexprec;
	}

	public String getCif_fob() {
		return cif_fob;
	}

	public void setCif_fob(String cif_fob) {
		this.cif_fob = cif_fob;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTippag() {
		return tippag;
	}

	public void setTippag(String tippag) {
		this.tippag = tippag;
	}

	public String getTipocontrato() {
		return tipocontrato;
	}

	public void setTipocontrato(String tipocontrato) {
		this.tipocontrato = tipocontrato;
	}

	public String getControlocbens() {
		return controlocbens;
	}

	public void setControlocbens(String controlocbens) {
		this.controlocbens = controlocbens;
	}

	public String getSerfaturcon() {
		return serfaturcon;
	}

	public void setSerfaturcon(String serfaturcon) {
		this.serfaturcon = serfaturcon;
	}

	public String getGrupofaturprorata() {
		return grupofaturprorata;
	}

	public void setGrupofaturprorata(String grupofaturprorata) {
		this.grupofaturprorata = grupofaturprorata;
	}

	public String getControrgpublico() {
		return controrgpublico;
	}

	public void setControrgpublico(String controrgpublico) {
		this.controrgpublico = controrgpublico;
	}

	public String getFaturprorata() {
		return faturprorata;
	}

	public void setFaturprorata(String faturprorata) {
		this.faturprorata = faturprorata;
	}

	public String getDtbasereaj() {
		return convertDate(dtbasereaj);
	}

	public void setDtbasereaj(String dtbasereaj) {
		this.dtbasereaj = dtbasereaj;
	}

	public String getDttermino() {
		return convertDate(dttermino);
	}

	public void setDttermino(String dttermino) {
		this.dttermino = dttermino;
	}

	public String getDtrefexprec() {
		return convertDate(dtrefexprec);
	}

	public void setDtrefexprec(String dtrefexprec) {
		this.dtrefexprec = dtrefexprec;
	}

	public String getDtrefarmaze() {
		return convertDate(dtrefarmaze);
	}

	public void setDtrefarmaze(String dtrefarmaze) {
		this.dtrefarmaze = dtrefarmaze;
	}

	public String getDtrefproxfat() {
		return convertDate(dtrefproxfat);
	}

	public void setDtrefproxfat(String dtrefproxfat) {
		this.dtrefproxfat = dtrefproxfat;
	}

	public String getDtenvioemail() {
		return convertDate(dtenvioemail);
	}

	public void setDtenvioemail(String dtenvioemail) {
		this.dtenvioemail = dtenvioemail;
	}

	public String getDtcontrato() {
		return convertDate(dtcontrato);
	}

	public void setDtcontrato(String dtcontrato) {
		this.dtcontrato = dtcontrato;
	}

	public String getDuracao() {
		return duracao;
	}

	public void setDuracao(String duracao) {
		this.duracao = duracao;
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
		return "topfaturcon: " + topfaturcon +
				"\nqtdprovisao: " + qtdprovisao +
				"\ncodcid: " + codcid +
				"\ntipotitulo: " + tipotitulo +
				"\nqtdparcpgcom: " + qtdparcpgcom +
				"\nrecdesp: " + recdesp +
				"\ncodcencus: " + codcencus +
				"\ncodcontato: " + codcontato +
				"\ncodcriterio: " + codcriterio +
				"\ncodemp: " + codemp +
				"\ncodimplant: " + codimplant +
				"\ncodmoealtreaj: " + codmoealtreaj +
				"\ncodmoeda: " + codmoeda +
				"\ncodmonsankhya: " + codmonsankhya +
				"\ncodnat: " + codnat +
				"\ncodparc: " + codparc +
				"\ncodparcprest: " + codparcprest +
				"\ncodparcsec: " + codparcsec +
				"\ncodproj: " + codproj +
				"\ncodprojsint: " + codprojsint +
				"\ncodtipvenda: " + codtipvenda +
				"\ncodusu: " + codusu +
				"\ndiafimmed: " + diafimmed +
				"\ndiapag: " + diapag +
				"\nfreqreaj: " + freqreaj +
				"\nfreqvisitas: " + freqvisitas +
				"\ngatilho: " + gatilho +
				"\nnumcontratoorigem: " + numcontratoorigem +
				"\nnunotapedloc: " + nunotapedloc +
				"\nnusla: " + nusla +
				"\nparcelaatual: " + parcelaatual +
				"\nparcelaqtd: " + parcelaqtd +
				"\npercirf: " + percirf +
				"\nperciss: " + perciss +
				"\npercloc: " + percloc +
				"\nprazovencto: " + prazovencto +
				"\ncodusualter: " + codusualter +
				"\ncodservex: " + codservex +
				"\ncodcencusar: " + codcencusar +
				"\ncodcencusex: " + codcencusex +
				"\ncodnatar: " + codnatar +
				"\ncodnatex: " + codnatex +
				"\ncodsaf: " + codsaf +
				"\ncodtipvendaar: " + codtipvendaar +
				"\ncodtipvendaex: " + codtipvendaex +
				"\ndiacarecar: " + diacarecar +
				"\ndiacarenc: " + diacarenc +
				"\ndiacarencex: " + diacarencex +
				"\npadclass: " + padclass +
				"\nquebratec: " + quebratec +
				"\nresppagar: " + resppagar +
				"\ntabprecumi: " + tabprecumi +
				"\ntabprecumiar: " + tabprecumiar +
				"\ntipotituloar: " + tipotituloar +
				"\ntipotituloex: " + tipotituloex +
				"\numidpadra: " + umidpadra +
				"\nuniconvsc: " + uniconvsc +
				"\nad_codtipcon: " + ad_codtipcon +
				"\ncodobs: " + codobs +
				"\nprazomensal: " + prazomensal +
				"\nnunota: " + nunota +
				"\ncodclc: " + codclc +
				"\nvalquebtrans: " + valquebtrans +
				"\nnunotarefarmaze: " + nunotarefarmaze +
				"\nnunotarefexprec: " + nunotarefexprec +
				"\nnumcstc: " + numcstc +
				"\nad_vlrcontrato: " + ad_vlrcontrato +
				"\ntemirf: " + temirf +
				"\ntemiss: " + temiss +
				"\ntemmed: " + temmed +
				"\nretemiss: " + retemiss +
				"\nacessahistsubos: " + acessahistsubos +
				"\nambiente: " + ambiente +
				"\nativo: " + ativo +
				"\nclauscontrato: " + clauscontrato +
				"\ndiautil: " + diautil +
				"\nequipamento: " + equipamento +
				"\nferiadoest: " + feriadoest +
				"\nferiadomun: " + feriadomun +
				"\nferiadonac: " + feriadonac +
				"\ngerarnf: " + gerarnf +
				"\nimprime: " + imprime +
				"\nimprprecindiv: " + imprprecindiv +
				"\nobservacoes: " + observacoes +
				"\nreajustenegativo: " + reajustenegativo +
				"\ngerarfinnota: " + gerarfinnota +
				"\nlocacaobem: " + locacaobem +
				"\nnumcontin: " + numcontin +
				"\ncobproporcar: " + cobproporcar +
				"\ndeftipa: " + deftipa +
				"\npercobra: " + percobra +
				"\npercobraar: " + percobraar +
				"\nperdesc: " + perdesc +
				"\nperdescon: " + perdescon +
				"\nsitcont: " + sitcont +
				"\ntipcobr: " + tipcobr +
				"\ntipoarm: " + tipoarm +
				"\ntipquebra: " + tipquebra +
				"\nulttabumi: " + ulttabumi +
				"\nvalpedfin: " + valpedfin +
				"\ncobproque: " + cobproque +
				"\nreglaudsaida: " + reglaudsaida +
				"\ndiafixo: " + diafixo +
				"\ntipisencao: " + tipisencao +
				"\nformfatarmaze: " + formfatarmaze +
				"\nformfatexprec: " + formfatexprec +
				"\ncif_fob: " + cif_fob +
				"\ntipo: " + tipo +
				"\ntippag: " + tippag +
				"\ntipocontrato: " + tipocontrato +
				"\ncontrolocbens: " + controlocbens +
				"\nserfaturcon: " + serfaturcon +
				"\ngrupofaturprorata: " + grupofaturprorata +
				"\ncontrorgpublico: " + controrgpublico +
				"\nfaturprorata: " + faturprorata +
				"\ndtbasereaj: " + dtbasereaj +
				"\ndttermino: " + dttermino +
				"\ndtrefexprec: " + dtrefexprec +
				"\ndtrefarmaze: " + dtrefarmaze +
				"\ndtrefproxfat: " + dtrefproxfat +
				"\ndtenvioemail: " + dtenvioemail +
				"\ndtcontrato: " + dtcontrato +
				"\nduracao: " + duracao;
	}
}
