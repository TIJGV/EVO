package br.com.evonetwork.desconto.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DescontoUtils {

	public static BigDecimal calcularDescontoEmReais(BigDecimal valortotal, BigDecimal descPorcentagem) {
		return valortotal.multiply((descPorcentagem.divide(BigDecimal.valueOf(100))));
	}

	public static BigDecimal calcularDescontoEmPorcentagem(BigDecimal valortotal, BigDecimal descReais) {
		return (descReais.divide(valortotal, 5, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
	}

}
