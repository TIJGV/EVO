package br.com.evonetwork.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Utils {
	
	public static String convertDate(String mDate) {
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
	    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	    try {
	    	Date newDate = inputFormat.parse(mDate);
	        inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	    	 e.printStackTrace();
	    }
	    return mDate;
	}
	
	public static BigDecimal calcHoras(String horasVendidas) {
		String horas = extrairAntesCaractere(horasVendidas);
		String minutos = pegarAposCaractere(horasVendidas);
		double horasDouble = (new BigDecimal(horas)).doubleValue();
		double minutosDouble = (new BigDecimal(minutos)).doubleValue() / 60.0D;
		double soma = horasDouble + minutosDouble;
		return (new BigDecimal(soma)).setScale(6, RoundingMode.HALF_UP);
	}
	
	public static String pegarAposCaractere(String str) {
		char caractere = ':';
		int indiceCaractere = str.indexOf(caractere);
		if (indiceCaractere != -1 && indiceCaractere < str.length() - 1)
		return str.substring(indiceCaractere + 1); 
		return "";
	}
	
	public static String extrairAntesCaractere(String str) {
		char caractere = ':';
		int indiceCaractere = str.indexOf(caractere);
		if (indiceCaractere != -1)
		return str.substring(0, indiceCaractere); 
		return str;
	}
	
	public static boolean verificaFormatoHora(String str) {
		String regex = "^\\d{2}:\\d{2}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(str).matches();
	}
}
