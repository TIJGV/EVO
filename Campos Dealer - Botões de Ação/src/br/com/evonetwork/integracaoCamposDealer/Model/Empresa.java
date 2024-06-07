package br.com.evonetwork.integracaoCamposDealer.Model;

public class Empresa {
	private String codEmpresa;
	private int idEmpresa;
	private boolean fMatriz;
	private String CNPJ_CPF;
	private String Nome;
	private String NomeResponsavel;
	private String Telefone;
	private String Mail;
	private String Endereco;
	private String Cidade;
	private String Estado;
	private String Pais;
	private String dtCadastro;
	private String CPFResponsavel;

	public String getCodEmpresa() {
		return codEmpresa;
	}

	public void setCodEmpresa(String codEmpresa) {
		this.codEmpresa = codEmpresa;
	}

	public int getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public boolean isfMatriz() {
		return fMatriz;
	}

	public void setfMatriz(boolean fMatriz) {
		this.fMatriz = fMatriz;
	}

	public String getCNPJ_CPF() {
		return CNPJ_CPF;
	}

	public void setCNPJ_CPF(String cNPJ_CPF) {
		CNPJ_CPF = cNPJ_CPF;
	}

	public String getNome() {
		return Nome;
	}

	public void setNome(String nome) {
		Nome = nome;
	}

	public String getNomeResponsavel() {
		return NomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		NomeResponsavel = nomeResponsavel;
	}

	public String getTelefone() {
		return Telefone;
	}

	public void setTelefone(String telefone) {
		Telefone = telefone;
	}

	public String getMail() {
		return Mail;
	}

	public void setMail(String mail) {
		Mail = mail;
	}

	public String getEndereco() {
		return Endereco;
	}

	public void setEndereco(String endereco) {
		Endereco = endereco;
	}

	public String getCidade() {
		return Cidade;
	}

	public void setCidade(String cidade) {
		Cidade = cidade;
	}

	public String getEstado() {
		return Estado;
	}

	public void setEstado(String estado) {
		Estado = estado;
	}

	public String getPais() {
		return Pais;
	}

	public void setPais(String pais) {
		Pais = pais;
	}

	public String getDtCadastro() {
		return dtCadastro;
	}

	public void setDtCadastro(String dtCadastro) {
		this.dtCadastro = dtCadastro;
	}

	public String getCPFResponsavel() {
		return CPFResponsavel;
	}

	public void setCPFResponsavel(String cPFResponsavel) {
		CPFResponsavel = cPFResponsavel;
	}

}
