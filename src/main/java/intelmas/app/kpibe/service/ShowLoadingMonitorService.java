package intelmas.app.kpibe.service;

import intelmas.app.kpibe.controller.dto.LoadingStatusDTO;
import intelmas.app.kpibe.exception.ProcessingException;

public interface ShowLoadingMonitorService {

	// public LoadingStatusDTO getLoadingMonitor(String organisation, Timestamp startDate, Timestamp endDate) throws ProcessingException;
	public LoadingStatusDTO generateLoadingMonitor(String organisation, String date) throws ProcessingException;
}
