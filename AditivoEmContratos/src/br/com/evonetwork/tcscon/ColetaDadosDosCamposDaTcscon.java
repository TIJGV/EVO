package br.com.evonetwork.tcscon;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaDadosDosCamposDaTcscon {
	
	CamposDaTcscon tcscon = new CamposDaTcscon();
	
	public void coletarDadosTcscon(BigDecimal numeroDoContrato) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT * FROM TCSCON WHERE NUMCONTRATO = " + numeroDoContrato);
			rset = sql.executeQuery();

			while (rset.next()) {
				//************************bigdecimal*************************
				getTcscon().setTopfaturcon(rset.getBigDecimal("TOPFATURCON"));
				getTcscon().setQtdprovisao(rset.getBigDecimal("QTDPROVISAO"));
				getTcscon().setCodcid(rset.getBigDecimal("CODCID"));
				getTcscon().setTipotitulo(rset.getBigDecimal("TIPOTITULO"));
				getTcscon().setQtdparcpgcom(rset.getBigDecimal("QTDPARCPGCOM"));
				getTcscon().setRecdesp(rset.getBigDecimal("RECDESP"));
				getTcscon().setCodcencus(rset.getBigDecimal("CODCENCUS"));
				getTcscon().setCodcontato(rset.getBigDecimal("CODCONTATO"));
				getTcscon().setCodcriterio(rset.getBigDecimal("CODCRITERIO"));
				getTcscon().setCodemp(rset.getBigDecimal("CODEMP"));
				getTcscon().setCodimplant(rset.getBigDecimal("CODIMPLANT"));
				getTcscon().setCodmoealtreaj(rset.getBigDecimal("CODMOEALTREAJ"));
				getTcscon().setCodmoeda(rset.getBigDecimal("CODMOEDA"));
				getTcscon().setCodmonsankhya(rset.getBigDecimal("CODMONSANKHYA"));
				getTcscon().setCodnat(rset.getBigDecimal("CODNAT"));
				getTcscon().setCodparc(rset.getBigDecimal("CODPARC"));
				getTcscon().setCodparcprest(rset.getBigDecimal("CODPARCPREST"));
				getTcscon().setCodparcsec(rset.getBigDecimal("CODPARCSEC"));
				getTcscon().setCodproj(rset.getBigDecimal("CODPROJ"));
				getTcscon().setCodprojsint(rset.getBigDecimal("CODPROJSINT"));
				getTcscon().setCodtipvenda(rset.getBigDecimal("CODTIPVENDA"));
				getTcscon().setCodusu(rset.getBigDecimal("CODUSU"));
				getTcscon().setDiafimmed(rset.getBigDecimal("DIAFIMMED"));
				getTcscon().setDiapag(rset.getBigDecimal("DIAPAG"));
				getTcscon().setFreqreaj(rset.getBigDecimal("FREQREAJ"));
				getTcscon().setFreqvisitas(rset.getBigDecimal("FREQVISITAS"));
				getTcscon().setGatilho(rset.getBigDecimal("GATILHO"));
				getTcscon().setNumcontratoorigem(rset.getBigDecimal("NUMCONTRATOORIGEM"));
				getTcscon().setNunotapedloc(rset.getBigDecimal("NUNOTAPEDLOC"));
				getTcscon().setNusla(rset.getBigDecimal("NUSLA"));
				getTcscon().setParcelaatual(rset.getBigDecimal("PARCELAATUAL"));
				getTcscon().setParcelaqtd(rset.getBigDecimal("PARCELAQTD"));
				getTcscon().setPercirf(rset.getBigDecimal("PERCIRF"));
				getTcscon().setPerciss(rset.getBigDecimal("PERCISS"));
				getTcscon().setPercloc(rset.getBigDecimal("PERCLOC"));
				getTcscon().setPrazovencto(rset.getBigDecimal("PRAZOVENCTO"));
				getTcscon().setCodusualter(rset.getBigDecimal("CODUSUALTER"));
				getTcscon().setCodservex(rset.getBigDecimal("CODSERVEX"));
				getTcscon().setCodcencusar(rset.getBigDecimal("CODCENCUSAR"));
				getTcscon().setCodcencusex(rset.getBigDecimal("CODCENCUSEX"));
				getTcscon().setCodnatar(rset.getBigDecimal("CODNATAR"));
				getTcscon().setCodnatex(rset.getBigDecimal("CODNATEX"));
				getTcscon().setCodsaf(rset.getBigDecimal("CODSAF"));
				getTcscon().setCodtipvendaar(rset.getBigDecimal("CODTIPVENDAAR"));
				getTcscon().setCodtipvendaex(rset.getBigDecimal("CODTIPVENDAEX"));
				getTcscon().setDiacarecar(rset.getBigDecimal("DIACARECAR"));
				getTcscon().setDiacarenc(rset.getBigDecimal("DIACARENC"));
				getTcscon().setDiacarencex(rset.getBigDecimal("DIACARENCEX"));
				getTcscon().setPadclass(rset.getBigDecimal("PADCLASS"));
				getTcscon().setQuebratec(rset.getBigDecimal("QUEBRATEC"));
				getTcscon().setResppagar(rset.getBigDecimal("RESPPAGAR"));
				getTcscon().setTabprecumi(rset.getBigDecimal("TABPRECUMI"));
				getTcscon().setTabprecumiar(rset.getBigDecimal("TABPRECUMIAR"));
				getTcscon().setTipotituloar(rset.getBigDecimal("TIPOTITULOAR"));
				getTcscon().setTipotituloex(rset.getBigDecimal("TIPOTITULOEX"));
				getTcscon().setUmidpadra(rset.getBigDecimal("UMIDPADRA"));
				getTcscon().setUniconvsc(rset.getBigDecimal("UNICONVSC"));
				getTcscon().setAd_codtipcon(rset.getBigDecimal("AD_CODTIPCON"));
				getTcscon().setCodobs(rset.getBigDecimal("CODOBS"));
				getTcscon().setPrazomensal(rset.getBigDecimal("PRAZOMENSAL"));
				getTcscon().setNunota(rset.getBigDecimal("NUNOTA"));
				getTcscon().setCodclc(rset.getBigDecimal("CODCLC"));
				getTcscon().setValquebtrans(rset.getBigDecimal("VALQUEBTRANS"));
				getTcscon().setNunotarefarmaze(rset.getBigDecimal("NUNOTAREFARMAZE"));
				getTcscon().setNunotarefexprec(rset.getBigDecimal("NUNOTAREFEXPREC"));
				getTcscon().setNumcstc(rset.getBigDecimal("NUMCSTC"));
				getTcscon().setAd_vlrcontrato(rset.getBigDecimal("AD_VLRCONTRATO"));

				//************************string*************************
				getTcscon().setTemirf(rset.getString("TEMIRF"));
				getTcscon().setTemiss(rset.getString("TEMISS"));
				getTcscon().setTemmed(rset.getString("TEMMED"));
				getTcscon().setRetemiss(rset.getString("RETEMISS"));
				getTcscon().setAcessahistsubos(rset.getString("ACESSAHISTSUBOS"));
				getTcscon().setAmbiente(rset.getString("AMBIENTE"));
				getTcscon().setAtivo(rset.getString("ATIVO"));
				getTcscon().setClauscontrato(rset.getString("CLAUSCONTRATO"));
				getTcscon().setDiautil(rset.getString("DIAUTIL"));
				getTcscon().setEquipamento(rset.getString("EQUIPAMENTO"));
				getTcscon().setFeriadoest(rset.getString("FERIADOEST"));
				getTcscon().setFeriadomun(rset.getString("FERIADOMUN"));
				getTcscon().setFeriadonac(rset.getString("FERIADONAC"));
				getTcscon().setGerarnf(rset.getString("GERARNF"));
				getTcscon().setImprime(rset.getString("IMPRIME"));
				getTcscon().setImprprecindiv(rset.getString("IMPRPRECINDIV"));
				getTcscon().setObservacoes(rset.getString("OBSERVACOES"));
				getTcscon().setReajustenegativo(rset.getString("REAJUSTENEGATIVO"));
				getTcscon().setGerarfinnota(rset.getString("GERARFINNOTA"));
				getTcscon().setLocacaobem(rset.getString("LOCACAOBEM"));
				getTcscon().setNumcontin(rset.getString("NUMCONTIN"));
				getTcscon().setCobproporcar(rset.getString("COBPROPORCAR"));
				getTcscon().setDeftipa(rset.getString("DEFTIPA"));
				getTcscon().setPercobra(rset.getString("PERCOBRA"));
				getTcscon().setPercobraar(rset.getString("PERCOBRAAR"));
				getTcscon().setPerdesc(rset.getString("PERDESC"));
				getTcscon().setPerdescon(rset.getString("PERDESCON"));
				getTcscon().setSitcont(rset.getString("SITCONT"));
				getTcscon().setTipcobr(rset.getString("TIPCOBR"));
				getTcscon().setTipoarm(rset.getString("TIPOARM"));
				getTcscon().setTipquebra(rset.getString("TIPQUEBRA"));
				getTcscon().setUlttabumi(rset.getString("ULTTABUMI"));
				getTcscon().setValpedfin(rset.getString("VALPEDFIN"));
				getTcscon().setCobproque(rset.getString("COBPROQUE"));
				getTcscon().setReglaudsaida(rset.getString("REGLAUDSAIDA"));
				getTcscon().setDiafixo(rset.getString("DIAFIXO"));
				getTcscon().setTipisencao(rset.getString("TIPISENCAO"));
				getTcscon().setFormfatarmaze(rset.getString("FORMFATARMAZE"));
				getTcscon().setFormfatexprec(rset.getString("FORMFATEXPREC"));
				getTcscon().setCif_fob(rset.getString("CIF_FOB"));
				getTcscon().setTipo(rset.getString("TIPO"));
				getTcscon().setTippag(rset.getString("TIPPAG"));
				getTcscon().setTipocontrato(rset.getString("TIPOCONTRATO"));
				getTcscon().setControlocbens(rset.getString("CONTROLOCBENS"));
				getTcscon().setSerfaturcon(rset.getString("SERFATURCON"));
				getTcscon().setGrupofaturprorata(rset.getString("GRUPOFATURPRORATA"));
				getTcscon().setControrgpublico(rset.getString("CONTRORGPUBLICO"));
				getTcscon().setFaturprorata(rset.getString("FATURPRORATA"));

				//***********************************data**************
				getTcscon().setDtbasereaj(rset.getString("DTBASEREAJ"));
				getTcscon().setDttermino(rset.getString("DTTERMINO"));
				getTcscon().setDtrefexprec(rset.getString("DTREFEXPREC"));
				getTcscon().setDtrefarmaze(rset.getString("DTREFARMAZE"));
				getTcscon().setDtrefproxfat(rset.getString("DTREFPROXFAT"));
				getTcscon().setDtenvioemail(rset.getString("DTENVIOEMAIL"));
				getTcscon().setDtcontrato(rset.getString("DTCONTRATO"));

				//****************************hora******************
				getTcscon().setDuracao(rset.getString("DURACAO"));

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
	public CamposDaTcscon getTcscon() {
		return tcscon;
	}
	@Override
	public String toString() {
		return getTcscon().toString();
	}
}
