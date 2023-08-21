package br.com.evonetwork.importarNota.Utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

	public static Timestamp stringToTimestamp(String data) throws Exception {
		Timestamp timestamp = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    Date parsedDate = dateFormat.parse(data);
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) {
		    e.printStackTrace();
		    throw new Exception(e.getMessage());
		}
		return timestamp;
	}

	public static String convertData(String mDate) {
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		
	   SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	    }

	   return mDate;
	}

	public static Timestamp convertDataHora(String date) throws Exception {
		Date parsedDate;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //dd/MM/yyyy hh:mm:ss.SSS -- 2000-01-28 00:00:00
			parsedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	    return new java.sql.Timestamp(parsedDate.getTime());
	}

}
