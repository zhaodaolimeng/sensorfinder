<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@include file="../taglib.jsp" %>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Curiosity</title>
<style type="text/css">
.search-result .thumbnail { border-radius: 0 !important; }
.search-result:first-child { margin-top: 0 !important; }
.search-result { margin-top: 20px; }
.search-result .col-md-2 { border-right: 1px dotted #ccc; min-height: 140px; }
.search-result ul { padding-left: 0 !important; list-style: none;  }
.search-result ul li { font: 400 normal .85em "Roboto",Arial,Verdana,sans-serif;  line-height: 30px; }
.search-result ul li i { padding-right: 5px; }
.search-result .col-md-7 { position: relative; }
.search-result h3 { font: 500 normal 1.375em "Roboto",Arial,Verdana,sans-serif; margin-top: 0 !important; margin-bottom: 10px !important; }
.search-result h3 > a, .search-result i { color: #248dc1 !important; }
.search-result p { font: normal normal 1.125em "Roboto",Arial,Verdana,sans-serif; } 
</style>
<!-- <link href="resources/css/utils.css" rel="stylesheet"> -->
</head>

<body>
<div class="span9">
	<div class="hero-unit buttom-buffer">
		<h1>Keyword search for IOT sensors</h1>
	</div>
	
	<div id="custom-search-input" class="row-fluid buttom-buffer">
	    <div class="input-group col-md-8">
		    <input id="query-string"  type="text" class="form-control buttom-buffer" placeholder="Search for...">
		    	<span class="input-group-btn">
		    	<button class="btn btn-default" type="button" id='search-btn'>Go!</button>
		    </span>
	    </div>
	</div>
	
	<div class="row-fluid">
		<div class="col-xs-12 col-sm-6 col-md-12" id="search-result">
		</div>
	</div>
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script src="resources/js/jquery.spring-friendly.js"></script>

<script lang="javascript">

var recent_result_list;
var caption_init = false;

$('#search-result').hide();
$('#nav_list').children().eq(8).addClass('active');

var searchit = function(event) {
	var text_val = $('#query-string').val()
	if(text_val.length > 0){
		console.log(text_val);		
		$.ajax({
		    headers: {
		    	'Accept': 'application/json',
		        'Content-Type': 'application/json' 
		    },
		    url: 'search/search',
		    type: 'POST',
		    data: JSON.stringify({query:text_val}),
		    success : function(data) {
		    	recent_result_list = data['content']['result'];
				console.log(recent_result_list);
				if(caption_init === false){
					$('#search-result').prepend('<h2>Search Results</h2>');
					caption_init = true;
				}
				$('#search-result').empty();
				
				$.each(recent_result_list['itemlist'], function(index, value){
					
					var metadata_str = "<div class=\"col-xs-12 col-sm-12 col-md-3\"><ul class=\"meta-search\">";
					metadata_str +=	"<li><i class=\"glyphicon glyphicon-th-large\"></i><span>Feed ID: ";
					metadata_str += value['feedid'] + "</span></li><li><i class=\"glyphicon glyphicon-th\"></i><span>Stream ID: ";
					metadata_str += value['sensorid'] + "</span></li><li><i class=\"glyphicon glyphicon-time\"></i><span>Created Time:";
					metadata_str += value['createdTime'] + "</span></li></ul></div>";
					
					var snapshort_str = "<div class=\"col-xs-12 col-sm-12 col-md-8 excerpet\"><h3><a href=\"#\" title=\"\">";
					snapshort_str += value['feedTitle'];
					snapshort_str += "</a></h3><p>";
					/* if(value['sensorLabel'])
						snapshort_str += value['sensorLabel'] + '. ';	
					if(value['feedTags'])
						snapshort_str += value['feedTags'] + '. '; */
					
					snapshort_str += value['snapshot'];
					snapshort_str += "</p></div><span class=\"clearfix borda\"></span>";

					$('#search-result').append("<article class=\"search-result row\">" + metadata_str + snapshort_str + "</article>");
				});
				
				$('#search-result').show();
		    }
		})
	}
}

$('#search-btn').on('click', searchit);
$('#query-string').keypress(function (e) {
	if(e.which == 13) {
		searchit();
	}
});   

$('#query-string').on('input', function(event) {
	$('#search-result').hide();
})

</script>

</body>
</html>
