package br.com.thinklife.unapel.os.processamento;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

public class ConverterData
{
    @SuppressWarnings("unused")
	public static Timestamp converterData(final String data) throws ParseException {
        final String datainicial = data;
        final String horainicial = datainicial.split(" ")[1];
        final String[] datainicial2 = datainicial.replace(horainicial, "").split("-");
        final String diadata = datainicial2[2];
        final String mesdata = datainicial2[1];
        final String anodata = datainicial2[0];
        final String dataa;
        final String data_nova_inicial = dataa = String.valueOf(diadata.replace(" ", "")) + "/" + mesdata + "/" + anodata;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final Date d1Inicial = dateFormat.parse(String.valueOf(dataa) + " " + horainicial.replace(".0", ""));
        return new Timestamp(d1Inicial.getTime());
    }
}
