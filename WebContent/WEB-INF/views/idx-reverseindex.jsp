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
		<h1>Build Full Text Search Index</h1>
		<p>Lucene is used for indexing metadata of IoT devices. </p>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<h2>Indexing Control</h2>
			<div class="col-xs-12" style="height:20px;"></div>
			<table class="table">
				<tr>
					<td>Build full text search index with Lucene</td>
					<td><button class="btn btn-default" id="lucene_index">Start building </button></td>
				</tr>
				<tr>
					<td>Build topic model with Mallet</td>
					<td><button class="btn btn-default" id="mallet_index">Start building </button></td>
				</tr>
			</table>
			<div id='alert_placeholder'></div>
		</div> <!--/span-->
	</div> <!--/row-->
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script lang="javascript">


$('#nav_list').children().eq(6).addClass('active')

var index_task_trigger = function(index_alias){
	var selector_name = '#' + index_alias + '_index'; 
	$(selector_name).on('click', function(event) {
		$.ajax({
		    headers: { 
		    	'Accept': 'application/json',
		        'Content-Type': 'application/json' 
		    },
		    url: index_alias + '/UpdateIndex',
		    type: 'POST',
		    data: JSON.stringify({}), // pattern of data: {options:[]}
		    success : function(data) {
		    	$(selector_name).html('Building...');
		    	timer = setInterval(function(){
		    		$.get(index_alias + "/CheckIndexingTaskState", function(data) {
		    			if(data['content']['state']==='DONE'){
		    				$(selector_name).html('Start building ');
		    				var msg = '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>'; 
		    				msg += '<strong>Success!</strong> ' + index_alias + ' Indexing Done.';
		    				var layout = '<div class="alert alert-success">' + msg + '</div>';
		    				$('#alert_placeholder').html(layout);
		    				clearInterval(timer);
		    			}else if(data['content']['state']=='FAILING'){
		    				$(selector_name).html('Start building ');
		    				var msg = '<a href="#" class="close" data-dismiss="alert" ar ia-label="close">&times;</a>';
		    				msg += '<strong>Failed!</strong> Index is not configured correctly, check if it\'s locked.';
		    				var layout = '<div class="alert alert-danger">' + msg + '</div>';
		    				$('#alert_placeholder').html(layout);
		    				clearInterval(timer);
		    			}
		    		});
		    	}, 2000);
		    }
		})
	});
}

var index_alias_arr = ['lucene', 'mallet'];
index_task_trigger('lucene');
index_task_trigger('mallet');

</script>
</body>
</html>
