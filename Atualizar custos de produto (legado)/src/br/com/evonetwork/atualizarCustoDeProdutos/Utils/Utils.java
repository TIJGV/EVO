package br.com.evonetwork.atualizarCustoDeProdutos.Utils;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {
	
    public static String convertDate(String mDate) {
        if (mDate == null || mDate == "" || "null".equals(mDate)) {
            return "";
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            final Date newDate = inputFormat.parse(mDate);
            inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            mDate = inputFormat.format(newDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate;
    }
    
    public static boolean parametroParaBoolean(final String param) {
        return "S".equals(param);
    }
    
    public static String convertDate2(String mDate) {
        if (mDate == null || mDate == "" || "null".equals(mDate)) {
            return "";
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            final Date newDate = inputFormat.parse(mDate);
            inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            mDate = inputFormat.format(newDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate;
    }
}
