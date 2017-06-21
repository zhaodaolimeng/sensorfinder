package ac.ictwsn.sensorfinder.web.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ac.ictwsn.sensorfinder.service.XivelyAccessService;
import ac.ictwsn.sensorfinder.web.model.AjaxResponse;
import ac.ictwsn.sensorfinder.web.model.CrawlRequest;

@RestController
@RequestMapping("/admin")
public class CrawlerController {

	private static final Logger logger = Logger.getLogger(CrawlerController.class); 
	
	@Autowired
	private XivelyAccessService xivelyAccessService;
	
	/**
	 * Quickly count Xively
	 * @return
	 */
	@RequestMapping(value = "/crawl/SimpleCount", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody AjaxResponse simpleCount(
			@RequestBody CrawlRequest request) {
		logger.info("SimpleCount");
		AjaxResponse response = new AjaxResponse();
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("count", xivelyAccessService.countFeeds(request.isAlive()));
		response.setContent(content);
		return response;
	}
	
	/**
	 * Start to automatically update/fetch all feeds, in an async manner
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/crawl/UpdateAllFeed", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json")
	public AjaxResponse updateAllFeed(
			@RequestBody CrawlRequest request, HttpSession session) {
		logger.info("UpdateAllFeed");
		AjaxResponse response = new AjaxResponse();
		try {
			String startAt = request.getStartat();
			xivelyAccessService.asyncUpdateAllFeeds(startAt); // asychronized method
			response.setStatus("1000");
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("5000");
			response.setMessage("Shit always happens\n" + e.getMessage());
		}
		return response;
	}
	
	/**
	 * Get feed update progress, for progress bar  
	 * @return
	 */
	@RequestMapping(value = "CheckAllFeedUpdateProgress", 
			method = RequestMethod.GET)
	public AjaxResponse checkUpdateAllFeedProgress(){
		//FIXME add new method
		AjaxResponse response = new AjaxResponse();
		Integer progress = xivelyAccessService.getFeedProgress();
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("percent", progress);
		response.setContent(content);
		return response;
	}
	
	/**
	 * Stop datastreams update
	 * @return
	 */
	@RequestMapping(value = "CancelUpdateAllFeed",
			method = RequestMethod.GET)
	public AjaxResponse cancelUpdateAllFeed(){
		AjaxResponse response = new AjaxResponse();
		String feedId = xivelyAccessService.abortUpdateFeeds();
		response.setStatus("1000");
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("stopat", feedId);
		response.setContent(content);
		return response;
	}
	
	/**
	 * Start to automatically update/fetch all feeds, in an async manner
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "UpdateAllDatastream", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json")
	public AjaxResponse updateAllDatastream(
			@RequestBody CrawlRequest request, HttpSession session) {
		logger.info("UpdateAllDatastreams");
		AjaxResponse response = new AjaxResponse();
		try {
			String startAt = request.getStartat();
			xivelyAccessService.asyncUpdateAllDatastreams(startAt); // asychronized method
			response.setStatus("1000");
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("5000");
			response.setMessage("Shit always happens\n" + e.getMessage());
		}
		return response;
	}
	
	/**
	 * Get datastream update progress 
	 * @return
	 */
	@RequestMapping(value = "CheckAllDatastreamUpdateProgress", 
			method = RequestMethod.GET)
	public AjaxResponse checkUpdateAllDatastreamProgress(){
		AjaxResponse response = new AjaxResponse();
		Integer progress = xivelyAccessService.getDatastreamsProgress();
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("percent", progress);
		response.setContent(content);
		return response;
	}
	
	/**
	 * Stop datastreams update
	 * @return
	 */
	@RequestMapping(value = "CancelUpdateAllDatastream",
			method = RequestMethod.GET)
	public AjaxResponse cancelUpdateAllDatastream(){
		AjaxResponse response = new AjaxResponse();
		String feedId = xivelyAccessService.abortUpdateStreams();
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("stopat", feedId);
		response.setContent(content);
		return response;
	}
	
	/**
	 * Batch Update Datastream, multiple sensors in input json 
	 * @param req
	 * @param session
	 * @return
	 */
	@RequestMapping(
			value = "UpdateDatastream", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	@ResponseBody
	public AjaxResponse updateDatastream(
			@RequestBody CrawlRequest request, HttpSession session){
		
		List<String> feedslist = request.getFeedlist();
		AjaxResponse response = new AjaxResponse();
		xivelyAccessService.updateDatastreams(feedslist);
		return response; 
	}

}
