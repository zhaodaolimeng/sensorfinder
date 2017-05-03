package ac.ictwsn.sensorfinder.web.controller;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ac.ictwsn.sensorfinder.service.SearchService;
import ac.ictwsn.sensorfinder.service.index.LuceneService;
import ac.ictwsn.sensorfinder.web.model.AjaxResponse;
import ac.ictwsn.sensorfinder.web.model.D3Response;
import ac.ictwsn.sensorfinder.web.model.IndexBuildRequest;
import ac.ictwsn.sensorfinder.web.model.SearchRequest;

@RestController
@RequestMapping("/search")
public class SearchController {
	
	private static final Logger logger = Logger.getLogger(SearchController.class);
	
	@Autowired
	private LuceneService luceneService;
	@Autowired
	private SearchService searchService;
	
	/**
	 * A json file is returned
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "fake.json", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody D3Response fakeContent(
			@RequestBody SearchRequest request) {
		logger.info("fake visual search");
		return searchService.fakeDataGenerate();
	}
	
	/**
	 * A json file is returned
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "visual.json", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody D3Response visualContent(
			@RequestBody SearchRequest request) {
		logger.info("visual search");
		logger.info("Controller get message = " + request.getFeedid() + ", " + request.getStreamid());
		Integer numOfSensorsToBeListed = request.getSensorNum();
		
		logger.info(request.getGraphType());
		logger.info(request.getSensorNum());
		
		D3Response response = null;
		if(request.getGraphType().equals("Topic vector")){
			response = searchService.visualQueryForTopic(
					request.getFeedid(), 
					request.getStreamid(), 
					numOfSensorsToBeListed);
		}else if(request.getGraphType().equals("Document vector")){
			response = searchService.visualQueryForDoc(
					request.getFeedid(), 
					request.getStreamid(), 
					numOfSensorsToBeListed);
		}else if(request.getGraphType().equals("Time and place relationship")){
			logger.error("Not implemented!");
		}
		return response;
	}
	
	/**
	 * Create full text search
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "search", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public @ResponseBody AjaxResponse fullTextQuery(
			@RequestBody IndexBuildRequest request){
		
		AjaxResponse response = new AjaxResponse();
		try {
			logger.info("Request = " + request.getQuery());
			HashMap<String, Object> content = new HashMap<String, Object>();
			content.put("result", luceneService.search(request.getQuery()));
			response.setContent(content);
		} catch (IOException | InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return response;
	}
}
