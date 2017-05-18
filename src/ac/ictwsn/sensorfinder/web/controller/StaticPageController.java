package ac.ictwsn.sensorfinder.web.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StaticPageController {
	
	private static final Logger logger = Logger.getLogger(StaticPageController.class); 
	
	/**
	 * Default filter
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/admin/{varPost}", method = RequestMethod.GET)
	public String adminHop(@PathVariable("varPost") String destPage) {
		logger.info("To Page = " + destPage);
		return "admin/" + destPage;
	}
	
	@RequestMapping(value = "/user/{varPost}", method = RequestMethod.GET)
	public String userHop(@PathVariable("varPost") String destPage) {
		logger.info("To Page = " + destPage);
		return "user/" + destPage;
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String welcome(){
		return "user/index";
	}
}
