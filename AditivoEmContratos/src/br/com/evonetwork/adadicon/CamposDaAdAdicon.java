package br.com.evonetwork.adadicon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamposDaAdAdicon {

	private BigDecimal codtipadi;
	private BigDecimal codmotadi;
	private BigDecimal concopia;
	private BigDecimal codaditivo;
	private BigDecimal numcontrato;
	private BigDecimal codusu;
	private String dhalter;

	public BigDecimal getCodtipadi() {
		return codtipadi;
	}

	public void setCodtipadi(BigDecimal codtipadi) {
		this.codtipadi = codtipadi;
	}

	public BigDecimal getCodmotadi() {
		return codmotadi;
	}

	public void setCodmotadi(BigDecimal codmotadi) {
		this.codmotadi = codmotadi;
	}

	public BigDecimal getConcopia() {
		return concopia;
	}

	public void setConcopia(BigDecimal concopia) {
		this.concopia = concopia;
	}

	public BigDecimal getCodaditivo() {
		return codaditivo;
	}

	public void setCodaditivo(BigDecimal codaditivo) {
		this.codaditivo = codaditivo;
	}

	public BigDecimal getNumcontrato() {
		return numcontrato;
	}

	public void setNumcontrato(BigDecimal numcontrato) {
		this.numcontrato = numcontrato;
	}

	public BigDecimal getCodusu() {
		return codusu;
	}

	public void setCodusu(BigDecimal codusu) {
		this.codusu = codusu;
	}

	public String getDhalter() {
		return convertDate(dhalter);
	}

	public void setDhalter(String dhalter) {
		this.dhalter = dhalter;
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
		return 	"Codtipadi: " + codtipadi +
				"\nCodmotadi: " + codmotadi +
				"\nConcopia: " + concopia +
				"\nCodaditivo: " + codaditivo +
				"\nNumcontrato: " + numcontrato +
				"\nCodusu: " + codusu +
				"\nDhalter: " + dhalter;
	}
}
