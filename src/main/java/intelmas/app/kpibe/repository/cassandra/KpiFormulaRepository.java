package intelmas.app.kpibe.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntityKey;

public interface KpiFormulaRepository extends CrudRepository<KpiFormulaEntity, KpiFormulaEntityKey>{
	
	@Query("SELECT * FROM kpi_formulas_mappings WHERE organisation = :organisation")
	Iterable <KpiFormulaEntity> findByOrganisation(@Param("organisation") String organisation);
	
	@Query("SELECT * FROM kpi_formulas_mappings WHERE organisation = :organisation AND category = :category")
	Iterable <KpiFormulaEntity> findByOrganisationAndCategory(@Param("organisation") String organisation, @Param("category") String category);
	
}
