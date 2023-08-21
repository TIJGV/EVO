package br.com.evonetwork.buscarDadosReceitaSefaz;

import java.math.BigDecimal;
import java.sql.ResultSet;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.buscarDadosReceitaSefaz.Controller.Controller;
import br.com.evonetwork.buscarDadosReceitaSefaz.Dao.ParceiroDao;
import br.com.evonetwork.buscarDadosReceitaSefaz.Model.Parceiro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AtualizarDadosParceiroAcaoAgendada implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext sac) {
		System.out.println("***EVO - INICIANDO EVENTO DE ATUALIZAÇÃO DE PARCEIROS***");
		
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codParc = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODPARC FROM TGFPAR WHERE NVL(SITCADRF, -1) = -1 AND ROWNUM <= 3 AND CODPARC NOT IN (0, 1, 2)");

			rset = sql.executeQuery();

			while (rset.next()) {
				codParc = rset.getBigDecimal("CODPARC");

				System.out.println("***Atualizando Parceiro: "+codParc);
				
				JapeWrapper daoInfo = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
		        DynamicVO tgfparVO = daoInfo.findByPK(codParc);
		        
		        Parceiro parceiroAtual = ParceiroDao.setarParceiroUtilizandoDynamicVo(tgfparVO);
				
				Controller.realizarChamada(tgfparVO, parceiroAtual, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
}
