package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;
import br.com.evonetwork.integracaoCamposDealer.Model.Usuario;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class VendedorDAO {
	public static void atualizarVendedor(Usuario usuario) throws Exception {
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.VENDEDOR)
					.prepareToUpdateByPK(new BigDecimal(usuario.getCodUsuario()))
					.set("AD_IDCAMPOSDEALER", new BigDecimal(usuario.getIdUsuario())).update();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao atualizar Vendedor: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
