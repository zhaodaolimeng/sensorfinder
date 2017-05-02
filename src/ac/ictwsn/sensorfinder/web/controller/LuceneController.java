package ac.ictwsn.sensorfinder.web.controller;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ac.ictwsn.sensorfinder.service.index.LuceneService;
import ac.ictwsn.sensorfinder.web.model.AjaxResponse;
import ac.ictwsn.sensorfinder.web.model.IndexBuildRequest;

@RestController
@RequestMapping("/lucene")
public class LuceneController {
	
	private static final Logger logger = Logger.getLogger(LuceneController.class);
	
	@Autowired
	LuceneService luceneService;
	
	/**
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "UpdateIndex", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody AjaxResponse buildIndex(
			@RequestBody IndexBuildRequest request) {
		logger.info("Start building lucene index...");
		
		luceneService.startBuildLuceneIndex(request.getOptions());
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
		content.put("state", luceneService.checkIndexingState());  
		response.setContent(content);
		return response;
	}
	
}
