package intelmas.app.kpibe.service;

import java.sql.Timestamp;

import org.elasticsearch.search.sort.SortOrder;

import intelmas.app.kpibe.constant.TermConstant;
import intelmas.app.kpibe.controller.dto.KpiDTO;
import intelmas.app.kpibe.controller.dto.KpisDTO;
import intelmas.app.kpibe.controller.dto.TypeCompleteStatusesDTO;
import intelmas.app.kpibe.exception.ProcessingException;


public interface KpiService {

	public TypeCompleteStatusesDTO getStatus(String organisation, Timestamp datetime) throws ProcessingException;
	
	public KpisDTO getNotificationsData(String organisation, String node, String cell, String name, Integer resolution) throws ProcessingException;
	public KpisDTO getSoftAlarmData(String organisation, String oss, String name, Integer resolution) throws ProcessingException;
	
	public KpisDTO getLatestKpis(String organisation, String node, String cell) throws ProcessingException;
	public KpisDTO getLatestKpisByResolution(String organisation, String node, String cell, Integer resolution) throws ProcessingException;
	
	public KpisDTO getLatestHourKpis(String organisation, String node, String cell) throws ProcessingException;
	public KpisDTO getLatestHourKpisByTerm(String organisation, TermConstant term, String value) throws ProcessingException;
	
	public KpisDTO getKpisByDate(String organisation, Timestamp startDate, Timestamp endDate, String node, String cell) throws ProcessingException;
	
	public KpisDTO getKpisByType(String type) throws ProcessingException;
	
	public KpisDTO getAggregatedKpisByDateAndTerm(Timestamp startDate, Timestamp endDate, TermConstant term, String value) throws ProcessingException;
	
	public KpisDTO getKpisPerTerm(String organisation,
			String kpiName, TermConstant term, String termValue, int page, int size) throws ProcessingException;
	
	public KpiDTO getAverageKpi(String organisation, 
			String kpiName, TermConstant term, String value) throws ProcessingException;
	
	public KpisDTO getTopHitKpis(String organisation, 
			Timestamp startDate, Timestamp endDate, String kpiName, TermConstant term, String termValue, Integer size, SortOrder order) throws ProcessingException;
	
	public KpisDTO getKpisDashboardPerNode(String organisation,
			String node) throws ProcessingException;
	
	public KpisDTO getKpisAggregationPerTime(String organisation, String kpiName, TermConstant term, String termValue, String resolution, Timestamp startDate, Timestamp endDate) throws ProcessingException;
	
	
	
	/* NOTE: Not needed anymore
	 * 
	
	public KpiDTO getAverageKpiPerRegion(String organisation, 
			String kpiName, String region) throws ProcessingException;
	
	public KpiDTO getAverageKpiPerTech(String organisation, 
			String kpiName, String tech) throws ProcessingException;
	
	public KpisDTO getAggregatedKpisByDateAndRegion(Timestamp startDate, Timestamp endDate, String region) throws ProcessingException;
	
	public KpisDTO getAggregatedKpisByDateAndTech(Timestamp startDate, Timestamp endDate, String tech) throws ProcessingException;
	
	public KpisDTO getKpisByDateAndRegion(String organisation, Timestamp startDate, Timestamp endDate, String node, String cell, String region) throws ProcessingException;
	public KpisDTO getKpisByDateAndTech(String organisation, Timestamp startDate, Timestamp endDate, String node, String cell, String tech) throws ProcessingException;
		
	public KpisDTO getLatestHourKpisByRegion(String organisation, String region) throws ProcessingException;
	public KpisDTO getLatestHourKpisByTech(String organisation, String tech) throws ProcessingException;
	
	
	public KpisDTO getKpisPerRegion(String organisation,
			String kpiName, String region, int page, int size) throws ProcessingException;
	
	public KpisDTO getKpisPerTech(String organisation, 
			String kpiName, String tech, int page, int size) throws ProcessingException;
	
	public KpisDTO getKpisPerNode(String organisation,
			String kpiName, String region, int page, int size) throws ProcessingException;
	
	public KpisDTO getBottomHitKpisByRegion(String organisation, 
			Timestamp startDate, Timestamp endDate, String kpiName, String region, Integer size) throws ProcessingException;
	public KpisDTO getTopHitKpisByRegion(String organisation,
			Timestamp startDate, Timestamp endDate, String kpiName, String region, Integer size) throws ProcessingException;
	public KpisDTO getBottomHitKpisByTech(String organisation, 
			Timestamp startDate, Timestamp endDate, String kpiName, String tech, Integer size) throws ProcessingException;
	public KpisDTO getTopHitKpisByTech(String organisation,
			Timestamp startDate, Timestamp endDate, String kpiName, String tech, Integer size) throws ProcessingException;
	*/
	
}
