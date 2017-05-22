package ac.ictwsn.sensorfinder.web.controller;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ac.ictwsn.sensorfinder.service.index.MalletService;
import ac.ictwsn.sensorfinder.web.model.AjaxResponse;
import ac.ictwsn.sensorfinder.web.model.QueryRequest;

@RestController
@RequestMapping("/mallet")
public class MalletController {
	
	private static final Logger logger = Logger.getLogger(MalletController.class);
	
	@Autowired
	MalletService malletService;
	
	/**
	 * create mallet index, i.e. the state file of dmr
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "UpdateIndex", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody AjaxResponse buildIndex(
			@RequestBody QueryRequest request) {
		logger.info("Start building topic index...");
		
		malletService.startBuildTopicIndex();
		AjaxResponse response = new AjaxResponse();		
		HashMap<String, Object> content = new HashMap<String, Object>();
		
		response.setContent(content);
		return response;
	}
	
	
	/**
	 * Check indexing state
	 * listed in enum IndexingTaskSate
	 * @return
	 */
	@RequestMapping(value = "CheckIndexingTaskState", 
			method = RequestMethod.GET)
	public @ResponseBody AjaxResponse checkIndexIsDone(){
		
		AjaxResponse response = new AjaxResponse();
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("state", malletService.checkIndexingState());  
		response.setContent(content);
		return response;
	}

}
