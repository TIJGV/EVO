package br.com.evonetwork.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
	public String convertDate(String mDate) {
		if (mDate == null || mDate == "" || "null".equals(mDate))
			return "";

		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date newDate = inputFormat.parse(mDate);
			inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			mDate = inputFormat.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDate;
	}

	public static Timestamp stringToTimestamp(String data) throws Exception {
		Timestamp timestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date parsedDate = dateFormat.parse(data);
			timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return timestamp;
	}

	public static int calculaMeses(Timestamp dtContrato, Timestamp dtTermino) throws Exception {
		int calculo;
		Calendar c = Calendar.getInstance();
		c.setTime(dtContrato);
		int mes1 = c.get(Calendar.MONTH);
		int ano1 = c.get(Calendar.YEAR);
		c.setTime(dtTermino);
		int mes2 = c.get(Calendar.MONTH);
		int ano2 = c.get(Calendar.YEAR);
		
		if(ano1 == ano2) {
			calculo = mes2 - mes1 + 1;
		} else {
			calculo = (12 - mes1 +1) + mes2 + (ano2 - ano1 - 1)*12;
		}
		
		return calculo;
	}
	
	public static Timestamp somaMeses(Timestamp dtContrato, int meses) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(dtContrato);
		c.add(Calendar.MONTH, meses);
		return new Timestamp(c.getTimeInMillis());
	}
	public static BigDecimal calculaValor(BigDecimal vlrTotal, int qtdMeses, int i) {
		BigDecimal novoVlrTotal;
		
		if (i == qtdMeses) {
			BigDecimal vlrQuebrado = vlrTotal.divide(new BigDecimal(qtdMeses), 2, RoundingMode.DOWN);
			BigDecimal x = vlrQuebrado.multiply(new BigDecimal(qtdMeses - 1));
			
			novoVlrTotal = vlrTotal.subtract(x);
		} else {
			novoVlrTotal = vlrTotal.divide(new BigDecimal(qtdMeses), 2, RoundingMode.DOWN);
		}
		return novoVlrTotal;
	}
	public static Timestamp MudarDiaPag(Timestamp dt, BigDecimal diaPag) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.set(Calendar.DAY_OF_MONTH, diaPag.intValue());;
		return new Timestamp(c.getTimeInMillis());
	}
}