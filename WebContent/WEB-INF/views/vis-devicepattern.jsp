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
	<div class="hero-unit">
		<h1>A spider for IoT</h1>
		<p>Curiosity is an IoT search framework which crawls open IoT platforms 
		(currently support Xively and Thingspeak). Statistics methods, like TF-IDF and 
		topic models are used for better indexing strategy.</p>
	</div>
	<div class="row-fluid">
		<div class="span4">
			<h2>Quick View</h2>
			<p>A summary view of status for Xively. Click labels to update the numbers. </p>
			
			<table class="table">
				<tr>
					<td>Numbers of all devices</td>
					<td><label class="btn btn-default" id="xively_count_all">--</label></td>
				</tr>
				<tr>
					<td>Numbers of alive devices</td>
					<td><label class="btn btn-default" id="xively_count_alive">--</label></td>
				</tr>
			</table>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Metadata Freshness</h2>
			<p>This graph shows the update time distribution of Xively in local database.</p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Heading</h2>
			<p>Donec id elit non mi porta gravida at eget metus. Fusce
				dapibus, tellus ac cursus commodo, tortor mauris condimentum
				nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
				malesuada magna mollis euismod. Donec sed odio dui.</p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
	</div>
	<!--/row-->
	<div class="row-fluid">
		<div class="span4">
			<h2>Heading</h2>
			<p>Donec id elit non mi porta gravida at eget metus. Fusce
				dapibus, tellus ac cursus commodo, tortor mauris condimentum
				nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
				malesuada magna mollis euismod. Donec sed odio dui.</p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Heading</h2>
			<p>Donec id elit non mi porta gravida at eget metus. Fusce
				dapibus, tellus ac cursus commodo, tortor mauris condimentum
				nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
				malesuada magna mollis euismod. Donec sed odio dui.</p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>Heading</h2>
			<p>Donec id elit non mi porta gravida at eget metus. Fusce
				dapibus, tellus ac cursus commodo, tortor mauris condimentum
				nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
				malesuada magna mollis euismod. Donec sed odio dui.</p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div>
		<!--/span-->
	</div> <!--/row-->
</div> <!--/span-->

<script src="resources/js/jquery.min.js"></script>
<script lang="javascript">

$('#nav_list').children().eq(11).addClass('active')

$('#xively_count_all').on('click', function(event) {
	$('#xively_count_all').text('Loading...');
	$.ajax({
	    headers: { 
	    	'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },
	    url: 'SimpleCount',
	    type: 'POST',
	    data: JSON.stringify({isalive:false}),
	    success : function(data) {
	    	console.log(data['content']['count'])
	    	$('#xively_count_all').text(data['content']['count']);
	    }
	})
});

$('#xively_count_alive').on('click', function(event) {
	$('#xively_count_alive').text('Loading...');
	$.ajax({
	    headers: {
	    	'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },
	    url: 'SimpleCount',
	    type: 'POST',
	    data: JSON.stringify({isalive:true}),
	    success : function(data) {
	    	console.log(data['content']['count'])
	    	$('#xively_count_alive').text(data['content']['count']);
	    }
	})
});

</script>

</body>
</html>
