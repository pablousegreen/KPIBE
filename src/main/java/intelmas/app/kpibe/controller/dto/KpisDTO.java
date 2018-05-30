package intelmas.app.kpibe.controller.dto;

import java.util.HashSet;
import java.util.Set;

import intelmas.app.kpibe.controller.dto.model.Kpi;
import intelmas.app.kpibe.model.elastic.KpiDocument;

public class KpisDTO extends BaseDTO {

	private Set<Kpi> kpis;
	private long totalElements;
	private long totalPages;
	
	public KpisDTO() {
		super("0000", "OK");
	}
	
	public Set<Kpi> getKpis() {
		return kpis;
	}
	
	public void setKpis(Set<Kpi> kpis) {
		this.kpis = kpis;
	}
	
	public long getTotalElements() {
		return totalElements;
	}
	
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
	
	public long getTotalPages() {
		return totalPages;
	}
	
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	
	public void setFromDocument(Iterable<KpiDocument> documents){
		if(documents == null) return;
		this.kpis = new HashSet<Kpi>();
		documents.forEach( 
				document -> {
					this.kpis.add(document.generateKpi());
				}
			);
	}
}
