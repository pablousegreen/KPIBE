package intelmas.app.kpibe.tools;

import java.util.ArrayList;
import java.util.List;

import intelmas.app.kpibe.model.cassandra.FormulaEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;

public class KpiFormulaVault {
	
	private List<KpiFormulaEntity> kpiFormulaEntities;
	
	public KpiFormulaVault(Iterable<KpiFormulaEntity> kpiFormulaEntities) {
		if(kpiFormulaEntities == null || kpiFormulaEntities.iterator() == null) return;
		
		List<KpiFormulaEntity> entityList = new ArrayList<KpiFormulaEntity>();
		kpiFormulaEntities.forEach( entity -> {
			if(entity.isActive()) entityList.add(entity);
		});
		
		this.setKpiFormulaEntities(entityList);
		//this.setKpiFormulaEntities(Lists.newArrayList(kpiFormulaEntities));
	}
	
	public KpiFormulaVault(List<KpiFormulaEntity> kpiFormulaEntities) {
		this.setKpiFormulaEntities(kpiFormulaEntities);
	}
	
	public List<KpiFormulaEntity> getKpiFormulaEntities() {
		return kpiFormulaEntities;
	}
	
	public void setKpiFormulaEntities(List<KpiFormulaEntity> kpiFormulaEntities) {
		this.kpiFormulaEntities = kpiFormulaEntities;
	}
	
	public boolean isNeededParameter(String parameter){
		for(KpiFormulaEntity entity: this.kpiFormulaEntities){
			FormulaEntity formula = entity.getFormula();
			if(formula == null || formula.getParametermapping() == null || formula.getParametermapping().size() == 0)
				return Boolean.FALSE;
			
			if(entity.getFormula().getParametermapping().containsKey(parameter)) 
				return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
}
