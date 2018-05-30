package intelmas.app.kpibe.service;

import intelmas.app.kpibe.controller.dto.BaseDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulaDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulasDTO;
import intelmas.app.kpibe.controller.dto.model.KpiFormula;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;


public interface KpiFormulaService {

	public KpiFormulasDTO getKpiFormulas(String organisation) throws ProcessingException;
	public Iterable<KpiFormulaEntity> getKpiFormulasByCategory(String organisation, String category) throws ProcessingException;
	public KpiFormulaDTO getKpiFormulasById(String id) throws ProcessingException;
	public BaseDTO createKpiFormula(KpiFormula kpiFormula) throws ProcessingException;
	public BaseDTO deleteKpiFormula(KpiFormula kpiFormula) throws ProcessingException;
}
