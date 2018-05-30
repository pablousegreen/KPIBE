package intelmas.app.kpibe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import intelmas.app.kpibe.controller.dto.BaseDTO;
import intelmas.app.kpibe.exception.ProcessingException;

public class BasicController {
	
	private static final Logger LOG = LoggerFactory.getLogger(BasicController.class);
	
	@ExceptionHandler(ProcessingException.class)
	public BaseDTO handleError(ProcessingException ex) {
		return ex.generateBaseDTO();
	}
	
	@ExceptionHandler(Exception.class)
	public BaseDTO handleUnhandledError(Exception ex) {
		LOG.error("Get Uncaught Exception [Exception:{}]", ex.toString());
		
		return new BaseDTO("9999", "Unable to handle the request");
	}
}
