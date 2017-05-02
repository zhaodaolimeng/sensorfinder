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
	@RequestMapping(value = "/{varPost}", method = RequestMethod.GET)
	public String pageHop(@PathVariable("varPost") String destPage) {
		logger.info("To Page = " + destPage);
		return destPage;
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String welcome(){
		return "index";
	}
}
