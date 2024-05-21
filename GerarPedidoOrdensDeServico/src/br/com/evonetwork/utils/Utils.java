package br.com.evonetwork.utils;

import java.sql.Timestamp;

public class Utils {
	public static Timestamp obterHoraAtual() {
		long milissegundos = System.currentTimeMillis();
		Timestamp horaAtual = new Timestamp(milissegundos);
		return horaAtual;
	}
}
