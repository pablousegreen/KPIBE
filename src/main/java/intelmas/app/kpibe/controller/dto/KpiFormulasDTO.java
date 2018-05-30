package intelmas.app.kpibe.controller.dto;

import java.util.HashSet;
import java.util.Set;

import intelmas.app.kpibe.controller.dto.model.KpiFormula;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;

public class KpiFormulasDTO extends BaseDTO {

	private Set<KpiFormula> kpiFormulas;
	
	public KpiFormulasDTO() {
		super("0000", "OK");
	}
	
	public Set<KpiFormula> getKpiFormulas() {
		return kpiFormulas;
	}
	
	public void setKpiFormulas(Set<KpiFormula> kpiFormulas) {
		this.kpiFormulas = kpiFormulas;
	}
	
	public void setFromEntities(Iterable<KpiFormulaEntity> entities){
		this.kpiFormulas = new HashSet<KpiFormula>();
		entities.forEach( 
			item -> {
				this.kpiFormulas.add(item.generateKpiFormula());
			}
		);
	}
}
