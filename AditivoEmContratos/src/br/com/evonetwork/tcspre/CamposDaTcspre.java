package br.com.evonetwork.tcspre;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamposDaTcspre {
	//BIGDECIMAL
	private BigDecimal codprod;
	private BigDecimal codserv;
	private BigDecimal codterrespar;
	private BigDecimal numcontrato;
	private BigDecimal valor;

	//STRING
	private String referencia;

	public BigDecimal getCodprod() {
		return codprod;
	}

	public void setCodprod(BigDecimal codprod) {
		this.codprod = codprod;
	}

	public BigDecimal getCodserv() {
		return codserv;
	}

	public void setCodserv(BigDecimal codserv) {
		this.codserv = codserv;
	}

	public BigDecimal getCodterrespar() {
		return codterrespar;
	}

	public void setCodterrespar(BigDecimal codterrespar) {
		this.codterrespar = codterrespar;
	}

	public BigDecimal getNumcontrato() {
		return numcontrato;
	}

	public void setNumcontrato(BigDecimal numcontrato) {
		this.numcontrato = numcontrato;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getReferencia() {
		return convertDate(referencia);
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
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
		return "\ncodprod: " + codprod +
				"\ncodserv: " + codserv +
				"\ncodterrespar: " + codterrespar +
				"\nnumcontrato: " + numcontrato +
				"\nvalor: " + valor +
				"\nreferencia: " + referencia;
	}
}
