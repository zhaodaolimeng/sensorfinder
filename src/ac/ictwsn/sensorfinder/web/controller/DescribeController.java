package ac.ictwsn.sensorfinder.web.controller;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ac.ictwsn.sensorfinder.service.DescribeService;
import ac.ictwsn.sensorfinder.web.model.AjaxResponse;
import ac.ictwsn.sensorfinder.web.model.IndexBuildRequest;

/**
 * 设备自动描述，即设备补全的控制模块
 * @author limeng
 *
 */
@RestController
@RequestMapping("/desc")
public class DescribeController {
	
	private static final Logger logger = Logger.getLogger(DescribeController.class);
	
	@Autowired
	DescribeService describeService;
	
	/**
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "UpdateIndex", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody AjaxResponse buildIndex(
			@RequestBody IndexBuildRequest request) {
		logger.info("Trying to fix device descriptions...");
		
//		luceneService.startBuildLuceneIndex(request.getOptions());
		describeService.refineDescription();
		
		AjaxResponse response = new AjaxResponse();		
		HashMap<String, Object> content = new HashMap<String, Object>();
		
		response.setContent(content);
		return response;
	}
	
//	/**
//	 * Check indexing state
//	 * listed in enum IndexingTaskSate
//	 * @return
//	 */
//	@RequestMapping(value = "CheckIndexingTaskState", 
//			method = RequestMethod.GET)
//	public @ResponseBody AjaxResponse checkIndexIsDone(){
//		
//		AjaxResponse response = new AjaxResponse();
//		HashMap<String, Object> content = new HashMap<String, Object>();
//		content.put("state", luceneService.checkIndexingState());  
//		response.setContent(content);
//		return response;
//	}
//	
//	/**
//	 * Create full text search
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "search", method = RequestMethod.POST, 
//			consumes = "application/json", produces = "application/json")
//	public @ResponseBody AjaxResponse fullTextQuery(
//			@RequestBody IndexBuildRequest request){
//		
//		AjaxResponse response = new AjaxResponse();
//		try {
//			logger.info("Request = " + request.getQuery());
//			HashMap<String, Object> content = new HashMap<String, Object>();
//			content.put("result", luceneService.search(request.getQuery()));
//			response.setContent(content);
//		} catch (IOException | InvalidTokenOffsetsException e) {
//			e.printStackTrace();
//		}
//		return response;
//	}

}
