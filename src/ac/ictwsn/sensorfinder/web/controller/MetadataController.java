package ac.ictwsn.sensorfinder.web.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;

@RestController
public class MetadataController {
	
	private static final Logger logger = Logger.getLogger(MetadataController.class); 
	
	@Autowired
    private FeedRepository feedRepository;

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "crawl/showMetadata", method = RequestMethod.GET)
    public DataTablesOutput<Feed> getMetadata(@Valid DataTablesInput input) {
    	logger.info("in show metadata");
    	DataTablesOutput<Feed> result = feedRepository.findAll(input);
        return result;
    }
    
    

}
