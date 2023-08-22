package br.com.evonetwork.campooberservacao;

public class CamposTddopc {
	private String valor;
	private String opcao;
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getOpcao() {
		return opcao;
	}
	public void setOpcao(String opcao) {
		this.opcao = opcao;
	}
	
	@Override
	public String toString() {
		return "'" + this.valor + "' para '" + this.opcao + "',\n";
	}

}
