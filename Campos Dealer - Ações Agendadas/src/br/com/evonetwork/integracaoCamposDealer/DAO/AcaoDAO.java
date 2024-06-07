package br.com.evonetwork.integracaoCamposDealer.DAO;

import br.com.evonetwork.integracaoCamposDealer.Model.Acao;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class AcaoDAO {
   public static void salvarAcao(Acao a, BigDecimal numos, BigDecimal codpap) throws Exception {
      Timestamp dtIni = Utils.convertDate2(a.getDthINI());
      Timestamp dtFin = Utils.convertDate2(a.getDthFIM());
      SessionHandle hnd = null;

      try {
         hnd = JapeSession.open();
         hnd.setCanTimeout(false);
         hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
         JapeWrapper negocioProdutoDAO = JapeFactory.dao("AD_ACAOPAP");
         negocioProdutoDAO.create().set("CODPAP", codpap)
	        .set("NUMOS", numos)
			.set("TIPOCONTATO", null)
			.set("TIPOACAO", null)
			.set("RESPONSAVEL", null)
			.set("DHINICIO", dtIni)
			.set("DHTERMINO", dtFin)
			.set("OBS1", a.getObsINI())
			.set("OBS2", a.getObsFIM())
			.set("IDCAMPOSDEALER", a.getIdAcao())
			.save();
      } catch (Exception var10) {
         var10.printStackTrace();
         throw new Exception("Erro ao criar Acao: " + var10.getMessage());
      } finally {
         JapeSession.close(hnd);
      }

   }

   public static void atualizarAcao(Acao a, BigDecimal numos) throws Exception {
      Timestamp dtIni = Utils.convertDate2(a.getDthINI());
      Timestamp dtFin = Utils.convertDate2(a.getDthFIM());
      SessionHandle hnd = null;

      try {
         hnd = JapeSession.open();
         hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
         JapeWrapper negocioProdutoDAO = JapeFactory.dao("AD_ACAOPAP");
         DynamicVO servico = negocioProdutoDAO.findOne("IDCAMPOSDEALER = '" + a.getIdAcao() + "'");
         negocioProdutoDAO.prepareToUpdate(servico)
        .set("TIPOCONTATO", null)
		.set("TIPOACAO", null)
		.set("RESPONSAVEL", null)
		.set("DHINICIO", dtIni)
		.set("DHTERMINO", dtFin)
		.set("OBS1", a.getObsINI())
		.set("OBS2", a.getObsFIM())
		.update();
      } catch (Exception var10) {
         var10.printStackTrace();
         throw new Exception("Erro ao atualizar Acao: " + var10.getMessage());
      } finally {
         JapeSession.close(hnd);
      }

   }
}