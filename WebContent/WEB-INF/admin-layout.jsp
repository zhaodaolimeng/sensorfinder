<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>

<%@ include file="taglib.jsp" %>

<html lang="en">
<head>
<title>Curiosity: A Search Platform for Public IOT Device</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="resources/favicon.ico" type="image/x-icon" />
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
<!-- <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.2/css/bootstrap-responsive.css" rel="stylesheet"> -->
<!-- <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" rel="stylesheet"> -->
<style type="text/css">
body {
	padding-top:65px;
}
@media (min-width: 979px) {
  #midCol.affix-top {
      position:fixed;
  	  width:265px;
  	  margin-right:10px;
  }
  #midCol.affix {
      position:static;
      width:100%;
  }
}
@media (min-width: 767px) {
  .affix,.affix-top {
      position:fixed;
  }
}
</style>

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

<decorator:head/>
</head>

<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">Curiosity</a>
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target=".nav-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="#">Home</a></li>
				<li><a href="#about">Discuss</a></li>
				<li><a href="#contact">Contact</a></li>
			</ul>
		</div>
	</nav>
	
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="col-sm-3">
				<ul class="nav sidebar-nav" id="nav_list">
					<li><a href="index" class="active">Index</a></li>
					<li class="sidebar-brand"><a href="#">IoT Device Crawl</a></li>
					<li><a href="crawl-stats">Data Overview</a></li>
					<li><a href="crawl-metadata">Fetch Metadata</a></li>
					<li class="sidebar-brand"><a href="#">Device Information Indexing</a></li>
					<li><a href="idx-datacheck">Data Check</a></li>
					<li><a href="idx-reverseindex">Build Index</a></li>
					<li class="sidebar-brand"><a href="#">Search</a></li>
					<li><a href="vis-searchdevice">Search device</a></li>
					<li><a href="vis-entitynetwork">IoT Entity Networks</a></li>
					<li class="sidebar-brand"><a href="#">Analysis</a></li>
					<li><a href="vis-topicanalysis">IoT Topics Analysis</a></li>
					<li><a href="vis-devicepattern">Analyse IoT Device Pattern</a></li>
				</ul>
			</div>
			<div class="col-sm-7">
				<decorator:body />
			</div>
		</div>
	</div>
<!-- 	<div class="container text-xs-center"> -->
<!-- 		<p class="text-muted credit" style="color:#000">Curiosity 0.1 - Copyright &copy; 2003 - 2017 ICTWSN </p> -->
<!-- 	</div> -->
	
	<script type="text/javascript">
	$(document).ready(function () {
		$('[data-toggle="offcanvas"]').click(function () {
			$('.row-offcanvas').toggleClass('active')
		});
	});
	</script>
</body>
</html>