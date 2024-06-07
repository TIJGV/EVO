package br.com.evonetwork.integracaoCamposDealer.Model;

public class ProdutoModelo {
	private int idProdutoModelo;
	private String codProdutoModelo;
	private int idProdutoMarca;
	private String codProdutoMarca;
	private String dscModelo;
	private String dthRegistro;

	public int getIdProdutoModelo() {
		return idProdutoModelo;
	}

	public void setIdProdutoModelo(int idProdutoModelo) {
		this.idProdutoModelo = idProdutoModelo;
	}

	public String getCodProdutoModelo() {
		return codProdutoModelo;
	}

	public void setCodProdutoModelo(String codProdutoModelo) {
		this.codProdutoModelo = codProdutoModelo;
	}

	public int getIdProdutoMarca() {
		return idProdutoMarca;
	}

	public void setIdProdutoMarca(int idProdutoMarca) {
		this.idProdutoMarca = idProdutoMarca;
	}

	public String getCodProdutoMarca() {
		return codProdutoMarca;
	}

	public void setCodProdutoMarca(String codProdutoMarca) {
		this.codProdutoMarca = codProdutoMarca;
	}

	public String getDscModelo() {
		return dscModelo;
	}

	public void setDscModelo(String dscModelo) {
		this.dscModelo = dscModelo;
	}

	public String getDthRegistro() {
		return dthRegistro;
	}

	public void setDthRegistro(String dthRegistro) {
		this.dthRegistro = dthRegistro;
	}

}
