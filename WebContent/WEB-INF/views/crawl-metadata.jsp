<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@include file="../taglib.jsp" %>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Curiosity</title>
</head>

<body>
<div class="span9">
	<div class="page-header">
		<h1>Fetch Metadata</h1> <hr/>
	</div>
	<div class="row-fluid">
		<div class="span9">
			<h2>Feed of Xively</h2> 
			<p>Click the refresh button to crawl feeds</p>
			<div class="input-group">
				<span class="input-group-btn">
					<button id="crawl_feed" class="btn btn-secondary" type="button">Start</button>
				</span>
				<input id="startfrom_feed" type="text" class="form-control" placeholder="Start from ...">
			</div>
			<div id="progress_field_feed" style="display: none">
				<div class="text-xs-center" id="crawl_feed_progress_text">Reticulating splines&hellip; 0%</div>
				<progress 
					id="crawl_feed_progress" 
					class="progress" value="0" max="100" 
					aria-describedby="crawl_feed_progress_text"></progress>
				<button id="stop_crawl_feed" class="btn btn-secondary" type="button">Stop</button>
				<div id="noty_field_feed"></div>
			</div>
			<div class="col-xs-12" style="height:50px;"></div>
			
			<!-- add padding -->			
			<h2>Datastream of Xively</h2> 
			<p>Click the refresh button to crawl all datastreams based on feeds</p>
			<div class="input-group">
				<span class="input-group-btn">
					<button id="crawl_datastream" class="btn btn-secondary" type="button">Start</button>
				</span>
				<input id="startfrom_datastream" type="text" class="form-control" placeholder="Start from ...">
			</div>
			<div id="progress_field_datastream" style="display: none">
				<div class="text-xs-center" id="crawl_datastream_progress_text">Reticulating splines&hellip; 0%</div>
				<progress 
					id="crawl_datastream_progress" 
					class="progress" value="0" max="100" 
					aria-describedby="crawl_datastream_progress_text"></progress>
				<button id="stop_crawl_datastream" class="btn btn-secondary" type="button">Stop</button>
				<div id="noty_field_datastream"></div>
			</div>
			
		</div> <!--/span-->
	</div> <!--/row-->
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>

<script lang="javascript">

$('#nav_list').children().eq(3).addClass('active');
$(document).ready(function() {
	
	var is_crawling_feed = false;
	var is_crawling_datastream = false;
	
	// ==========================================================
	// Feed Crawler Control
	// ==========================================================
		
	// start crawl all
	$('#crawl_feed').on('click', function(){
		console.log('POST: refresh ALL metadata');
		var startfrom = $('#startfrom_feed').val();
		if(startfrom === null || startfrom.length == 0) startfrom = 0;
		$.ajax({
			contentType : 'application/json',
			type : "POST",
			url : "crawl/UpdateAllFeed",
			data : JSON.stringify({"startat" : startfrom}),
			success : function(data){
				if(data['status']==1000){
					is_crawling_feed = true;
					console.log("start to update ...");
				}else{
					console.log("shit happened: " + data['status']);
				}
			}
		});
	});
	
	// cancel crawling process
	$('#stop_crawl_feed').on('click', function(){
		$.ajax({
			type : "GET",
			url : "crawl/CancelUpdateAllFeed",
			success : function(data){
				if(data['status']==1000){
					//FIXME stop notification not working
					var stopat = data['content']['stopat'];
					is_crawling_feed = false;
					var info='The metadata update process stoped at ID.' + stopat;
					var snap='<div class="alert alert-warning" dismissiable="true" role="alert">'+ info +'</div>';
					$('#noty_field_feed').append(snap);
				}
			}
		})
	});
	
	// ==========================================================
	// Datastream Crawler Control
	// ==========================================================
		
	// start crawl all
	$('#crawl_datastream').on('click', function(){
		console.log('POST: refresh ALL metadata');
		var startfrom = $('#startfrom_datastream').val();
		if(startfrom === null || startfrom.length == 0) startfrom = 0;
		$.ajax({
			contentType : 'application/json',
			type : "POST",
			url : "crawl/UpdateAllDatastream",
			data : JSON.stringify({"startat" : startfrom}),
			success : function(data){
				if(data['status']==1000){
					is_crawling_datastream = true;
					console.log("start to update ...");
				}else{
					console.log("shit happened: " + data['status']);
				}
			}
		});
	});
	
	// cancel crawling process
	$('#stop_crawl_datastream').on('click', function(){
		$.ajax({
			type : "GET",
			url : "crawl/CancelUpdateAllDatastream",
			success : function(data){
				if(data['status']==1000){
					var stopat = data['content']['stopat'];
					is_crawling_datastream = false;
					var info='The metadata update process stoped at ID.' + stopat;
					var snap='<div class="alert alert-warning" dismissiable="true" role="alert">'+ info +'</div>';
					$('#noty_field_datastream').html(snap);
				}
			}
		})
	});
	
	// ==========================================================
	// Client Side Refresh Control 
	// ==========================================================
		
	// check crawling process
	setInterval(function(){
		console.log('Sychronizing status ... ');
		if(!is_crawling_feed){
			$('#progress_field_feed').css('display', 'none');
		}else{
			$('#progress_field_feed').css('display', '');
			$.ajax({
				type : "GET",
				url : "crawl/CheckAllFeedUpdateProgress",
				success : function(data){
					var percentage = data['content']['percent'];
					$('#crawl_feed_progress').val("" + percentage);
					$('#crawl_feed_progress_text').text('Updating feeds message: ' + percentage + '%');
					if(percentage===100){
						var info = 'Feeds crawling done!';
						var snap = '<div class="alert alert-success" dismissiable="true" role="alert">'+ info +'</div>';
						is_crawling_feed = false;
						$('#alert_placeholder').html(snap);
					}
				}
			})
		}
		if(!is_crawling_datastream){
			$('#progress_field_datastream').css('display', 'none');
		}else{
			$('#progress_field_datastream').css('display', '');
			$.ajax({
				type : "GET",
				url : "crawl/CheckAllDatastreamUpdateProgress",
				success : function(data){
					var percentage = data['content']['percent'];
					$('#crawl_datastream_progress').val("" + percentage);
					$('#crawl_datastream_progress_text').text('Updating datastream message: ' + percentage + '%');
					if(percentage===100){
						var info = 'Datastream crawling done!';
						var snap = '<div class="alert alert-success" dismissiable="true" role="alert">'+ info +'</div>';
						is_crawling_datastream = false;
						$('#alert_placeholder').html(snap);
					}
				}
			})
		}
	}, 3000);
} );

</script>

</body>
</html>
