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
		<h1>Topic Analysis for Sensors</h1>
		<p>Temporal and spatial trends for different kind of IOT sensors</p>
	</div>
	<div class="row-fluid">
		<div class="span6">
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
		<div class="span6">
			<h2>Metadata Freshness</h2>
			<p>This graph shows the update time distribution of Xively in local database.</p>
			<p>
				<a class="btn" href="#">View details &raquo;</a>
			</p>
		</div> <!--/span-->
	</div> <!--/row-->
	<div class="row-fluid">
		<div class="span12">
			<h2>Topic Based Ranking</h2>
			<p>Different mix parameter can be set, 
			beta=1 equals pure topic based rank, 
			beta=0 equals pure full-text based rank</p>
		</div>
	</div>
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script lang="javascript">
$('#nav_list').children().eq(10).addClass('active')
</script>

</body>
</html>
