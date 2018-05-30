package intelmas.app.kpibe.controller.dto;

import intelmas.app.kpibe.controller.dto.model.KpiFormula;

public class KpiFormulaDTO extends BaseDTO {

	private KpiFormula kpiFormula;
	
	public KpiFormulaDTO() {
		super("0000", "OK");
	}
	
	public KpiFormula getKpiFormula() {
		return kpiFormula;
	}
	
	public void setKpiFormula(KpiFormula kpiFormula) {
		this.kpiFormula = kpiFormula;
	}
}
