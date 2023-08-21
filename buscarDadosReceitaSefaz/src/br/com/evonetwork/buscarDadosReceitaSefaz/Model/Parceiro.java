package br.com.evonetwork.buscarDadosReceitaSefaz.Model;

import java.math.BigDecimal;

public class Parceiro {

    private String tipoServico;
    private BigDecimal codParc;
    private String razaoSocial;
    private String nomeParc;
    private String cgcCpf;
    private String nomeBairro;
    private String nomeCidade;
    private String cep;
    private String nroEndereco;
    private String endereco;
    private String complemento;
    private String telefone;
    private String cnae;
    private String regimeApuracao;
    private BigDecimal codEstado;
    private String inscricaoEstadual;
    private String estado;
    private String situacaoSefaz;
    private String situacaoReceita;

    private BigDecimal codEndAdicional;
    private BigDecimal codCidAdicional;
    private BigDecimal codBairroAdicional;

    public Parceiro(){}

    public BigDecimal getCodParc() {
        return codParc;
    }

    public void setCodParc(BigDecimal codParc) {
        this.codParc = codParc;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCgcCpf() {
        return cgcCpf;
    }

    public void setCgcCpf(String cgcCpf) {
        this.cgcCpf = cgcCpf;
    }

    public String getNomeBairro() {
        return nomeBairro;
    }

    public void setNomeBairro(String nomeBairro) {
        this.nomeBairro = nomeBairro;
    }

    public String getNomeCidade() {
        return nomeCidade;
    }

    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNroEndereco() {
        return nroEndereco;
    }

    public void setNroEndereco(String nroEndereco) {
        this.nroEndereco = nroEndereco;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public BigDecimal getCodEndAdicional() {
        return codEndAdicional;
    }

    public void setCodEndAdicional(BigDecimal codEndAdicional) {
        this.codEndAdicional = codEndAdicional;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCnae() {
        return cnae;
    }

    public void setCnae(String cnae) {
        this.cnae = cnae;
    }

    public String getRegimeApuracao() {
        return regimeApuracao;
    }

    public void setRegimeApuracao(String regimeApuracao) {
        this.regimeApuracao = regimeApuracao;
    }

    public BigDecimal getCodEstado() {
        return codEstado;
    }

    public void setCodEstado(BigDecimal codEstado) {
        this.codEstado = codEstado;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSituacaoSefaz() {
        return situacaoSefaz;
    }

    public void setSituacaoSefaz(String situacaoSefaz) {
        this.situacaoSefaz = situacaoSefaz;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public BigDecimal getCodCidAdicional() {
        return codCidAdicional;
    }

    public void setCodCidAdicional(BigDecimal codCidAdicional) {
        this.codCidAdicional = codCidAdicional;
    }

    public BigDecimal getCodBairroAdicional() {
        return codBairroAdicional;
    }

    public void setCodBairroAdicional(BigDecimal codBairroAdicional) {
        this.codBairroAdicional = codBairroAdicional;
    }

    @Override
    public String toString() {
        return "Prospect{" +
                "tipoServico='" + tipoServico + '\'' +
                ", codParc=" + codParc +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", cgcCpf='" + cgcCpf + '\'' +
                ", nomeBairro='" + nomeBairro + '\'' +
                ", nomeCidade='" + nomeCidade + '\'' +
                ", cep='" + cep + '\'' +
                ", nroEndereco='" + nroEndereco + '\'' +
                ", endereco='" + endereco + '\'' +
                ", complemento='" + complemento + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cnae='" + cnae + '\'' +
                ", regimeApuracao='" + regimeApuracao + '\'' +
                ", codEstado=" + codEstado +
                ", inscricaoEstadual='" + inscricaoEstadual + '\'' +
                ", estado='" + estado + '\'' +
                ", situacaoSefaz='" + situacaoSefaz + '\'' +
                ", codEndAdicional=" + codEndAdicional +
                ", codCidAdicional=" + codCidAdicional +
                ", codBairroAdicional=" + codBairroAdicional +
                '}';
    }

	public String getSituacaoReceita() {
		return situacaoReceita;
	}

	public void setSituacaoReceita(String situacaoReceita) {
		this.situacaoReceita = situacaoReceita;
	}

	public String getNomeParc() {
		return nomeParc;
	}

	public void setNomeParc(String nomeParc) {
		this.nomeParc = nomeParc;
	}
}
