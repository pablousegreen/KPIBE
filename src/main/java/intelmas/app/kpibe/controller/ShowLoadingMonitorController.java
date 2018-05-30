package intelmas.app.kpibe.controller;

import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import intelmas.app.kpibe.controller.dto.LoadingStatusDTO;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.service.ShowLoadingMonitorService;
import intelmas.app.kpibe.tools.Utils;


@RestController
@RequestMapping("/monitorApi")
public class ShowLoadingMonitorController extends BasicController {
	
	@Autowired
	private ShowLoadingMonitorService showLoadingMonitorService;
	
	/*
	@GetMapping("/generateLoadingMonitor")
    public LoadingStatusDTO generateLoadingMonitor (
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String date) throws ProcessingException {
		
		if(StringUtils.isBlank(date)) throw new ProcessingException("2000", "date is missing");
		String startDatetime = date + "0000";
		String endDatetime = date + "2359";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(Utils.TIMEZONE);
		LocalDateTime localStartDate = LocalDateTime.parse(startDatetime, formatter);
		LocalDateTime localEndDate = LocalDateTime.parse(endDatetime, formatter);
		ZonedDateTime zonedStartDate = localStartDate.atZone(Utils.TIMEZONE);
		ZonedDateTime zonedEndDate = localEndDate.atZone(Utils.TIMEZONE);
		return showLoadingMonitorService.getLoadingMonitor(vendor, Timestamp.from(zonedStartDate.toInstant()), Timestamp.from(zonedEndDate.toInstant()));
	}
	*/
	
	@GetMapping("/showLoadingMonitor")
    public LoadingStatusDTO showLoadingMonitor (
    		String vendor, 
    		String date) throws ProcessingException {
		
		if(StringUtils.isBlank(date)) throw new ProcessingException("2000", "date is missing");
		try{
			DateTimeFormatter.ofPattern("yyyyMMdd").withZone(Utils.TIMEZONE);
			return showLoadingMonitorService.generateLoadingMonitor(vendor, date);
			
		}catch(Exception e){ throw new ProcessingException("2000", "Invalid data format [Exception:" + e.toString() + "]");}
		
	}
}
