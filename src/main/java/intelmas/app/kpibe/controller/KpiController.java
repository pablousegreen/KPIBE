package intelmas.app.kpibe.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import intelmas.app.kpibe.constant.TermConstant;
import intelmas.app.kpibe.controller.dto.KpiDTO;
import intelmas.app.kpibe.controller.dto.KpisDTO;
import intelmas.app.kpibe.controller.dto.TypeCompleteStatusesDTO;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.service.KpiService;
import intelmas.app.kpibe.service.impl.KpiServiceImpl;
import intelmas.app.kpibe.tools.Utils;


@RestController
@RequestMapping("/kpiApi")
public class KpiController extends BasicController {
	
	@Autowired
	private KpiService kpiService;
	
	private static final Logger LOG = LoggerFactory.getLogger(KpiController.class);
	
	
	@GetMapping("/regards")
	public String getRegards(){
		return "Hello KPI";
	}
	
	@GetMapping("/getStatus")
    public TypeCompleteStatusesDTO getStatus(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		@RequestParam(value = "checkTime") String checkTime) throws ProcessingException {
		
		if(StringUtils.isBlank(checkTime)) throw new ProcessingException("2000", "checkTime is missing");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localCheckDate = LocalDateTime.parse(checkTime, formatter);
		ZonedDateTime zonedCheckDate = localCheckDate.atZone(Utils.TIMEZONE);		
		return kpiService.getStatus(vendor, Timestamp.from(zonedCheckDate.toInstant()));
	}
	
	
	@GetMapping("/getBottomKpisByRegion")
    public KpisDTO getBottomKpisByRegion(
    		@RequestParam(value = "vendor", required = false) String vendor,
    		String endDatetime, String name, String region, Integer size) throws ProcessingException {
		
		return getTopKpis(vendor, endDatetime, name, TermConstant.region, region, size, SortOrder.ASC);
		// return kpiService.getBottomHitKpisByRegion(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()), name, region, size);
	}
	
	@GetMapping("/getTopKpisByRegion")
    public KpisDTO getTopKpisByRegion(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String endDatetime, String name, String region, Integer size) throws ProcessingException {
		
		return getTopKpis(vendor, endDatetime, name, TermConstant.region, region, size, SortOrder.DESC);
		/*
		if(StringUtils.isBlank(endDatetime)) throw new ProcessingException("2000", "endDatetime is missing");
		String startDatetime = StringUtils.substring(endDatetime, 0, -4) + "0000";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDatetime, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDatetime, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		
		return kpiService.getTopHitKpisByRegion(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()), name, region, size);
		*/
	}
	
	@GetMapping("/getBottomKpisByTech")
    public KpisDTO getBottomKpisByTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String endDatetime, String name, String tech, Integer size) throws ProcessingException {
		return getTopKpis(vendor, endDatetime, name, TermConstant.tech, tech, size, SortOrder.ASC);
		/*
		if(StringUtils.isBlank(endDatetime)) throw new ProcessingException("2000", "endDatetime is missing");
		String startDatetime = StringUtils.substring(endDatetime, 0, -4) + "0000";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDatetime, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDatetime, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		
		return kpiService.getBottomHitKpisByTech(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()), name, tech, size);
		*/
	}
	
	@GetMapping("/getTopKpisByTech")
    public KpisDTO getTopKpisByTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String endDatetime, String name, String tech, Integer size) throws ProcessingException {
		return getTopKpis(vendor, endDatetime, name, TermConstant.tech, tech, size, SortOrder.DESC);
		
		/*
		if(StringUtils.isBlank(endDatetime)) throw new ProcessingException("2000", "endDatetime is missing");
		String startDatetime = StringUtils.substring(endDatetime, 0, -4) + "0000";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDatetime, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDatetime, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		return kpiService.getTopHitKpisByTech(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()), name, tech, size);
		*/
	}
	
	private KpisDTO getTopKpis(String vendor, String endDatetime, String name, TermConstant term, String termValue, Integer size, SortOrder sort) throws ProcessingException{
		if(StringUtils.isBlank(endDatetime)) throw new ProcessingException("2000", "endDatetime is missing");
		String startDatetime = StringUtils.substring(endDatetime, 0, -4) + "0000";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDatetime, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDatetime, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		return kpiService.getTopHitKpis(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()), name, term, termValue, size, sort);
		// return kpiService.getBottomHitKpisByRegion(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()), name, region, size);
	}
	
	@GetMapping("/getKpisDashboardPerNode")
    public KpisDTO getKpisDashboardPerNode(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String node) throws ProcessingException {
		
		return kpiService.getKpisDashboardPerNode(vendor, node);
	}
	
	/*
	@GetMapping("/getAggregatedKpisPerNode")
    public KpisDTO getAggregatedKpisPerNode(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String node,
    		String resolution,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDate, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDate, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		return kpiService.getKpisAggregationPerTime(vendor, name, TermConstant.node, node, resolution, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()));
	}
	*
	*/
	
	@GetMapping("/getAggregatedKpisPerCell")
    public KpisDTO getAggregatedKpisPerCell(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String cell,
    		String resolution,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		return getAggregatedKpisPerTerm(vendor, name, TermConstant.cell, cell, resolution, startDate, endDate);
	}
	
	@GetMapping("/getAggregatedKpisPerNode")
    public KpisDTO getAggregatedKpisPerNode(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String node,
    		String resolution,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		return getAggregatedKpisPerTerm(vendor, name, TermConstant.node, node, resolution, startDate, endDate);
	}
	
	@GetMapping("/getAggregatedKpisPerRegion")
    public KpisDTO getAggregatedKpisPerRegion(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String region,
    		String resolution,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		return getAggregatedKpisPerTerm(vendor, name, TermConstant.region, region, resolution, startDate, endDate);
	}
	
	@GetMapping("/getAggregatedKpisPerTech")
    public KpisDTO getAggregatedKpisPerTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String tech,
    		String resolution,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		return getAggregatedKpisPerTerm(vendor, name, TermConstant.tech, tech, resolution, startDate, endDate);
	}
	
	private KpisDTO getAggregatedKpisPerTerm(
    		String vendor, 
    		String name, TermConstant term, String termValue,
    		String resolution,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDate, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDate, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		return kpiService.getKpisAggregationPerTime(vendor, name, term, termValue, resolution, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()));
	}
	
	
	@GetMapping("/getKpisPerNode")
    public KpisDTO getKpisPerNode(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String node, 
    		@RequestParam(value="page", defaultValue="0") int page, 
    		@RequestParam(value="size", defaultValue="10") int size) throws ProcessingException {
		
		return kpiService.getKpisPerTerm(vendor, name, TermConstant.node, node, page, size);
	}
	
	@GetMapping("/getKpisPerRegion")
    public KpisDTO getKpisPerRegion(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String region, 
    		@RequestParam(value="page", defaultValue="0") int page, 
    		@RequestParam(value="size", defaultValue="10") int size) throws ProcessingException {
		
		return kpiService.getKpisPerTerm(vendor, name, TermConstant.region, region, page, size);
	}
	
	@GetMapping("/getKpisPerTech")
    public KpisDTO getKpisPerTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String tech, 
    		@RequestParam(value="page", defaultValue="0") int page, 
    		@RequestParam(value="size", defaultValue="10") int size) throws ProcessingException {
		
		return kpiService.getKpisPerTerm(vendor, name, TermConstant.tech, tech, page, size);
	}
	
	@GetMapping("/getAverageKpiPerRegion")
    public KpiDTO getAverageKpiPerRegion(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String region) throws ProcessingException {
		
		return kpiService.getAverageKpi(vendor, name, TermConstant.region, region);
	}
	
	@GetMapping("/getAverageKpiPerTech")
    public KpiDTO getAverageKpiPerTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String name, String tech) throws ProcessingException {
		
		return kpiService.getAverageKpi(vendor, name, TermConstant.tech, tech);
	}
	
	@GetMapping("/getNotificationsData")
    public KpisDTO getNotificationsData(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String node, String cell, String name, Integer resolution) throws ProcessingException {
		
		if(StringUtils.isBlank(node)) throw new ProcessingException("2000", "node is empty");
		if(StringUtils.isBlank(cell)) throw new ProcessingException("2000", "cell is empty");
		if(StringUtils.isBlank(name)) throw new ProcessingException("2000", "name is empty");
		if(resolution == null) throw new ProcessingException("2000", "resolution is empty");
		
		return kpiService.getNotificationsData(vendor, node, cell, name, resolution);
	}
	
	@GetMapping("/getSoftAlarmData")
    public KpisDTO getSoftAlarmData(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String oss, String name, Integer resolution) throws ProcessingException {
		
		if(StringUtils.isBlank(oss)) throw new ProcessingException("2000", "oss is empty");
		if(StringUtils.isBlank(name)) throw new ProcessingException("2000", "name is empty");
		if(resolution == null) throw new ProcessingException("2000", "resolution is empty");
		
		return kpiService.getSoftAlarmData(vendor, oss, name, resolution);
	}
	
	@GetMapping("/getLatestKpis")
    public KpisDTO getLatestKpis(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String node, String cell) throws ProcessingException {
		
		return kpiService.getLatestKpis(vendor, node, cell);
	}
	
	
	@GetMapping("/getLatestKpisByResolution")
    public KpisDTO getLatestKpisByHour(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String node, String cell, Integer resolution) throws ProcessingException {
		LOG.info("1AL001 getLatestKpisByResolution: "+resolution);
		return kpiService.getLatestKpisByResolution(vendor, node, cell, resolution);
	}
	
	
	@GetMapping("/getLatestHourKpis")
    public KpisDTO getLatestHourKpis(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String node, String cell) throws ProcessingException {
		
		return kpiService.getLatestHourKpis(vendor, node, cell);
	}
	
	
	@GetMapping("/getLatestHourKpisByRegion")
    public KpisDTO getLatestHourKpisByRegion(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String region) throws ProcessingException {
		
		return kpiService.getLatestHourKpisByTerm(vendor, TermConstant.region, region);
	}
	
	@GetMapping("/getLatestHourKpisByTech")
    public KpisDTO getLatestHourKpisByTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String tech) throws ProcessingException {
		
		return kpiService.getLatestHourKpisByTerm(vendor, TermConstant.tech, tech);
	}
	
	
	
	@GetMapping("/getKpisByDate")
    public KpisDTO getKpisByDate(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String node, String cell, 
    		String startDate,
    		String endDate) throws ProcessingException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDate, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDate, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		
		return kpiService.getKpisByDate(
				vendor,
				Timestamp.from(zonedStartDate.toInstant()), 
				Timestamp.from(zonedEndDate.toInstant()), node, cell);
	}
	
	
	@GetMapping("/getAverageKpisByDateAndRegion")
    public KpisDTO getAverageKpisByDateAndRegion(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String region,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDate, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDate, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		return kpiService.getAggregatedKpisByDateAndTerm(
				Timestamp.from(zonedStartDate.toInstant()), 
				Timestamp.from(zonedEndDate.toInstant()), TermConstant.region, region);
	}
	
	
	@GetMapping("/getAverageKpisByDateAndTech")
    public KpisDTO getAverageKpisByDateAndTech(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String tech,
    		String startDate,
    		String endDate) throws ProcessingException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDate, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDate, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		
		return kpiService.getAggregatedKpisByDateAndTerm(
				Timestamp.from(zonedStartDate.toInstant()), 
				Timestamp.from(zonedEndDate.toInstant()), TermConstant.tech, tech);
	}
	
	
	@GetMapping("/getKpisByType")
    public KpisDTO getKpisByType(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String type) throws ProcessingException {
		
		return kpiService.getKpisByType(type);
	}
}
