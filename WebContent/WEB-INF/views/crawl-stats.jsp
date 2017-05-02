<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@include file="../taglib.jsp" %>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Curiosity</title>
<link href="https://cdn.datatables.net/1.10.12/css/jquery.dataTables.min.css" rel="stylesheet">
<link href="resources/css/utils.css" rel="stylesheet">
</head>
<body>
<div class="span9">
	<div class="page-header">
		<h1>Data Overview</h1> <hr/>
	</div>
	<div class="row-fluid">
		<div class="span9">
			<h2>Xively Counter</h2>
			<div class="col-xs-12" style="height:21px;"></div>
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
		</div><!--/span-->
	</div> <!--/row-->
	
	<div class="row-fluid">
		<div class="span9">
			<h2>Fetched Metadata</h2>
			<div class="col-xs-12" style="height:10px;"></div>
			<table id="metadata_table" class="display compact" cellspacing="0" width="100%">
				<thead>
		            <tr>
		                <th>ID</th>
		                <th>Title</th>
		                <th>Tags</th>
		                <th>Desc.</th>
		                <th>Status</th>
		                <th>Updated</th>
		                <th>Created</th>
		                <th>Lat.</th>
		                <th>Lng.</th>
		                <th>More</th>
		            </tr>
		        </thead>
		        <tfoot>
		            <tr>
		                <th>ID</th>
		                <th>Title</th>
		                <th>Tags</th>
		                <th>Description</th>
		                <th>Status</th>
		                <th>Updated Time</th>
		                <th>Created Time</th>
		                <th>Latitude</th>
		                <th>Longitude</th>
		                <th>More</th>
		            </tr>
		        </tfoot>
			</table>
		</div>
	</div>
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script src="resources/js/jquery.spring-friendly.js"></script>

<script lang="javascript">

$('#nav_list').children().eq(2).addClass('active')

function updateCountAll(){
	$('#xively_count_all').text('Loading...');
	$.ajax({
	    headers: { 
	    	'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },
	    url: 'crawl/SimpleCount',
	    type: 'POST',
	    data: JSON.stringify({alive:false}),
	    success : function(data) {
	    	$('#xively_count_all').text(data['content']['count']);
	    }
	})
}

function updateCountAlive(){
	$('#xively_count_alive').text('Loading...');
	$.ajax({
	    headers: { 
	    	'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },
	    url: 'crawl/SimpleCount',
	    type: 'POST',
	    data: JSON.stringify({alive:true}),
	    success : function(data) {
	    	$('#xively_count_alive').text(data['content']['count']);
	    }
	})	
}


$(document).ready(function() {
	updateCountAll();
	updateCountAlive();
})

$('#xively_count_all').on('click', function(event) {
	updateCountAll();
});

$('#xively_count_alive').on('click', function(event) {
	updateCountAlive();
});


//==========================================================
// Datatable
// ==========================================================

// init datatables
var table = $('#metadata_table').DataTable({
	"serverSide": true,
	"ajax": "crawl/showMetadata",
	"columns" : [{
			data : 'id'
		},{
			data : 'title',
			orderable : false,
			"render": function ( data, type, full, meta ) {
				if(data == null) return data;
				return type === 'display' && data.length > 14 ?
			        '<span title="'+data+'">'+data.substr( 0, 12 )+'...</span>' : data;
			}
		},{
			data : 'tags', 
			orderable : false,
			"render": function ( data, type, full, meta ) {
				if(data == null) return data;
				data = chunk(data, 12,' '); // chunk to fit size of cell
				return type === 'display' && data.length > 18 ?
			        '<span title="'+data+'">'+data.substr( 0, 16 )+'...</span>' : data;
			}
		},{
			data : 'description', 
			orderable : false,
			"render": function ( data, type, full, meta ) {
				if(data == null) return data;
				return type === 'display' && data.length > 30 ?
			        '<span title="'+data+'">'+data.substr( 0, 28 )+'...</span>' : data;
			}
		},{
			data : 'status', searchable : false
		},{
			data : 'updated',
			render : function(data, type, full, meta){
				return formatDate(data);
			}
		},{
			data : 'created',
			render : function(data, type, full, meta){
				return formatDate(data);
			}
		},{
			data : 'lat',
			render : function(data, type, full, meta){
				if(data == null) return data;
				return data.substr(0, 7);
			}
		},{
			data : 'lng',
			render : function(data, type, full, meta){
				if(data == null) return data;
				return data.substr(0, 7);
			}
		},{
            "className":	'details-control',
            "orderable":	false,
            "data":			null,
            "defaultContent": ''
        }
	]
});

// add details-control for each item in datatables
function formatDetail(d){
	var datastreams = '';
	var plotTrigger = '';
	// post to server to check if the datastream are available
	$.ajax({
		type : "GET",
		url : "crawl/getSingleFeedDetail",
		success : function(data){
			//TODO
		}
	})
    // `d` is the original data object for the row
    return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'+
        '<tr>'+
            '<td>Device id:</td>'+
            '<td>'+d.id+'</td>'+
        '</tr>'+
        '<tr>'+
            '<td>Description:</td>'+
            '<td>'+d.description+'</td>'+
        '</tr>'+
        '<tr>'+
            '<td>Tags:</td>'+
            '<td>'+d.tags+'</td>'+
        '</tr>'+
        '<tr>'+
            '<td>Datastreams:</td>'+
            '<td>'+datastreams+'</td>'+
        '</tr>'+
        plotTrigger + 
    '</table>';
}

	// Add event listener for opening and closing details
$('#metadata_table tbody').on('click', 'td.details-control', function () {
    var tr = $(this).closest('tr');
    var row = table.row( tr );
    if ( row.child.isShown() ) {
        // This row is already open - close it
        row.child.hide();
        tr.removeClass('shown');
    } else {
        // Open this row
        row.child( formatDetail(row.data()) ).show();
        tr.addClass('shown');
    }
} );
	
function formatDate(UNIX_timestamp){
	var date = new Date(UNIX_timestamp);
	var month = date.getUTCMonth() + 1; //months from 1-12
	var day = date.getUTCDate();
	var year = date.getUTCFullYear();
	var formattedTime = year + '/' + month + '/' + day + "\n";
	var minutes = "0" + date.getMinutes();
	var seconds = "0" + date.getSeconds();
	return formattedTime + date.getHours() + ':' + minutes.substr(-2) + ':' + seconds.substr(-2); 
} 

function chunk(str, n, mark) {
    var ret = "";
    var i, len;
    for(i = 0, len = str.length; i < len; i += n){ 
       ret += str.substr(i, n);
       ret += mark;
    }
    return ret;
}

</script>

</body>
</html>
