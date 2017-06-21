package ac.ictwsn.sensorfinder.web.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StaticPageController {
	
	private static final Logger logger = Logger.getLogger(StaticPageController.class);
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminDefault() {
		return "redirect:admin/index";
	}
	
	@RequestMapping(value = "/admin/{varPost}", method = RequestMethod.GET)
	public String adminHop(@PathVariable("varPost") String destPage) {
		logger.info("To Page = " + destPage);
		return "admin/" + destPage;
	}
	
	@RequestMapping(value = "/{varPost}", method = RequestMethod.GET)
	public String userHop(@PathVariable("varPost") String destPage) {
		logger.info("To Page = " + destPage);
		return destPage;
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String welcome(){
		logger.info("Welcome!");
		return "redirect:index";
	}
}
