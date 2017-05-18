<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@include file="../../taglib.jsp" %>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Curiosity</title>
</head>

<body>
<div class="span9">
	<div class="jumbotron">
		<h1>A search engine for IOT</h1>
		<p>SensorGuide is an IOT search framework which crawls open IoT platforms 
		(currently support Xively and Thingspeak). Statistics methods, like TF-IDF and 
		topic models are used for better indexing strategy.</p>
	</div>
	<div class="row-fluid">
		<div class="span4">
			<h2>Device Dashboard</h2>
			<p>A summary view of status for Xively. 
			You can also check all of the meta data 
			crawled from Xively and other famous IoT platforms. </p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Crawler Control</h2>
			<p>You can easily update IoT device information through re-crawling device. 
			This graph shows the update time distribution of Xively in local database.</p>
			<p><a class="btn" href="#">View details &raquo;</a></p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Index Control</h2>
			<p>You can create/update a traditional reverse index for devices 
			descriptions using a built in lucene core. This index can provide 
			a quick full-text search function for device. </p>
			<p><a class="btn" href="#">View details &raquo;</a></p>
		</div>
		<!--/span-->
	</div>
	<!--/row-->
	<div class="row-fluid">
		<div class="span4">
			<h2>Topic Analysis</h2>
			<p>We are running a topic model on all IoT devices to provide an 
			enhancement to search/discover useful/interesting devices. </p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>IoT Explorer</h2>
			<p>IoT explorer is a tool for users to explore the IoT world. 
			People find it difficult to visualize the IoT world cause the 
			lackage of device-to-device/device-to-information relationship. 
			IoT explorer is building a bridge between real and cyber world. </p>
			<p>
				<a class="btn" href="vis-entitynetwork">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Talent Agent</h2>
			<p>People got great ideas that may be easily ignored. 
			This function can help you find crazy ideas/devices in the IoT world.
			Combine datastream fingerprints with device descriptions, we can 
			quickly decide if the design of a IoT device is gifted. </p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
	</div> <!--/row-->
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script lang="javascript">

$('#nav_list').children().eq(0).addClass('active')

</script>

</body>
</html>
