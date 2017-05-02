# Intro

SensorGuide is an IoT search framework which crawls open IoT platforms (currently Xively API and Thingspeak). 
Statistics methods, like TF-IDF and topic models are used for better indexing strategy.    

There are 3 parts include in this project: Spider, Indexer and Querier.   
- Spider is used to collected data from different datasources.  
- Indexer is for maintaining indexes.  
- Querier is for reply to user's requests.

# Install

Import as a maven project from eclipse.  

# API Design

## Common Header
All the following content are put into the header of HTTP.  

### Request Header  
sessionid: string // temporary, consider using cookie instead  

### Response Header  
```javascript
{
	"code": int, // Application server result code
	"msg": string // For debug purpose, optional  
	"content": {
		// Real content returned from the server
	}
}
```

## Crawling  
Each feed contains metadata describing functions and deployment information of a single iot device. 
Each feed may contains one or several types of datastream, which is raw data inputs of sensors.  

### SimpleCount  
Count Xively devices.  
Method type: POST  
Request format:  
```javascript
{
	"isalive": boolean // if false then all devices are showed
}
```  
Response format:  
```javascript  
{
	"count": int
}
```

### UpdateAllFeed  
Start to automatically update/fetch all feeds, with a visual process bar. 
Datastream should also be fetched. All the feeds will be fetched in the order of feed ID, incrementally.  
Method type: POST  
Request format:  
```javascript
{
	"startat": string // Long in java, feed id
}
```
Response format: Common header.  
Consequences: A GET method will be triggered to retrieval the metadata information for all feeds, e.g. https://api.xively.com/v2/feeds?content=summary&status=live&page=3&per_page=50  

### UpdateFeed  
Batch update the device metadata. Feeds number are limited to 100.    
Method type: GET  
Request format:  
```javascript
{
	"feedlist":[string, ... ] // Feed id for each device.
}
```
Response format: Common header.
Consequences: GET method called, e.g. https://api.xively.com/v2/feeds/1873602884
 

### StartUpdateAllDatastream
Update all datastream features for each feed, with a process bar. 
Datastream are updated in order of device ID, start point can be set.  
Method type: POST
Request format:  
```javascript
{
	"startat": string, // Feed id, default 0
}
```
Response format: Common header.
Consequence: Datastreams for 1 minute/hour/day/week/month/year are gathered from this api. GET method called as https://api.xively.com/v2/feeds/1103963078/datastreams/LAeq2s?duration=1days&interval=900. 
An X-ApiKey field is needed for the header.  

### CheckUpdateAllDatastreamProgress

A tool API for progress bar. From user to server.
Method type: GET  
Response format:  
```
{
    "percent": int // A number from 1-100
}
```

### CancelUpdateAllDatastream
Datastream crawling process can be cumbersome, set it as an interruptible process.  
Method type: GET  
Response format:  
```javascript
{
	"stopat": int // Next feed id to update/fetch 
}
```  

### UpdateDatastream
Batch update selected feeds, for several features, like average in minute, hour, day, month and year.  
Method type: POST  
Request format:  
```javascript
{
	"feedlist":[string, ... ] // feedid for each datastream
}
```
Response format: Common header.  

## Analysis

### BuildIndex

Create new lucene index, using different field. 
Different data clean method maybe used.    
Method type: POST  
Request format:   
```javascript
{
	"include":[string, ... ] // field that will be used in index
}
```

### BuildTopicModel

Run topic analysis method.
Method type: GET

## Render

### TextSearch  
Full text search for device descriptions.  
Method type: POST  
```javascript
{
	"query":string // query string
}
```

### ListTopic


