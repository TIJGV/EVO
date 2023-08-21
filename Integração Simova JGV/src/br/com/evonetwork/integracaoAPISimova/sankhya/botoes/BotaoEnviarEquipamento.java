package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerClientes;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerEquipamento;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerModeloEquipamento;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerTipoEquipamento;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class BotaoEnviarEquipamento implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela TGFVEI
		System.out.println("***EVO - ENVIANDO EQUIPAMENTO PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO equipamentoVO = getEquipamentoVO((BigDecimal) linha.getCampo("CODVEICULO"));
				if("C".equals(equipamentoVO.getProperty("AD_FINALIDADE")) && Controller.verificaFlagDeEnvioParaVeiculo((BigDecimal) equipamentoVO.getProperty("AD_NROUNICOMODELO"))) {
					BigDecimal codEmpresa = (BigDecimal) equipamentoVO.getProperty("AD_FILIAL");
					String filial = Controller.getFilial(codEmpresa);
					if("".equals(filial)) {
						ca.mostraErro("Empresa/Filial não encontrada para o veículo "+equipamentoVO.asBigDecimal("CODVEICULO")+", não é possível integrar.");
					}
					String codMarca = getMarcaVeiculo((BigDecimal) linha.getCampo("CODVEICULO"));
					if("".equals(codMarca) || codMarca == null)
						ca.mostraErro("Não foi encontrado o Modelo do veículo "+(BigDecimal) linha.getCampo("CODVEICULO"));
					DynamicVO modeloEquipVO = getModeloEquipVO(codMarca);
					BigDecimal codTipoEquip = (BigDecimal) modeloEquipVO.getProperty("CODTIPO");
					DynamicVO tipoEquipVO = getTipoEquipVO(codTipoEquip);
					ControllerTipoEquipamento.enviarTipoEquipamentoPorDynamicVO(tipoEquipVO);
					ControllerModeloEquipamento.enviarModeloEquipamentoPorDynamicVO(modeloEquipVO);
					DynamicVO parceiroVO = getParceiroVO((BigDecimal) linha.getCampo("CODPARC"));
					ControllerClientes.enviarClientePorDynamicVO(parceiroVO);
					ControllerEquipamento.enviarEquipamentoPorDynamicVO(equipamentoVO);
				} else {
					ca.mostraErro("Veículo "+equipamentoVO.asBigDecimal("CODVEICULO")+" não possui finalidade 'Cliente' ou flag de envio no Modelo do Veículo está desmarcada.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO EQUIPAMENTO PARA SIMOVA - FIM***");
	}

	private DynamicVO getParceiroVO(BigDecimal codParc) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper parceiroDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
			DynamicVO parceiroVO = parceiroDAO.findOne("CODPARC = "+codParc);
			return parceiroVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private DynamicVO getTipoEquipVO(BigDecimal codTipoEquip) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tipoVeiculoDAO = JapeFactory.dao("AD_ESPECIETIPOVEI");
			DynamicVO tipoVeiculoVO = tipoVeiculoDAO.findOne("NROUNICO = "+codTipoEquip);
			return tipoVeiculoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private DynamicVO getModeloEquipVO(String codMarca) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper modVeiculoDAO = JapeFactory.dao("AD_MODELOVEI");
			DynamicVO modVeiculoVO = modVeiculoDAO.findOne("NROUNICO = "+codMarca);
			return modVeiculoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private String getMarcaVeiculo(BigDecimal codVeiculo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		String codMarca = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			DynamicVO veiculo = veiculoDAO.findByPK(codVeiculo);
			if(veiculo.asBigDecimal("AD_NROUNICOMODELO") != null)
				codMarca = veiculo.asBigDecimal("AD_NROUNICOMODELO")+"";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return codMarca;
	}

	private DynamicVO getEquipamentoVO(BigDecimal codVeiculo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			DynamicVO veiculoVO = veiculoDAO.findOne("CODVEICULO = "+codVeiculo);
			return veiculoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
}
