package intelmas.app.kpibe.controller.dto;

import intelmas.app.kpibe.controller.dto.model.Kpi;

public class KpiDTO extends BaseDTO {

	private Kpi kpi;
	
	public KpiDTO() {
		super("0000", "OK");
	}
	
	public Kpi getKpi() {
		return kpi;
	}
	
	public void setKpi(Kpi kpi) {
		this.kpi = kpi;
	}
	
}
