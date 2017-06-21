<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@include file="../../taglib.jsp" %>
<html lang="en">
<head>
<style>
.links line {
  stroke: #999;
  stroke-opacity: 0.6;
}
.nodes circle {
  stroke: #fff;
  stroke-width: 1.5px;
}
div.tooltip {	
    position: absolute;			
    text-align: center;			
    width: 160px;					
    height: 36px;					
    padding: 2px;				
    font: 12px sans-serif;		
    background: lightsteelblue;	
    border: 0px;		
    border-radius: 8px;			
    pointer-events: none;			
}
</style>
<link href="../resources/css/utils.css" rel="stylesheet">
<link href="http://code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css" rel="stylesheet"/>
<meta charset="utf-8">
<title>Curiosity</title>
</head>

<body>
<div class="span8">
	<div class="hero-unit">
		<h1>IoT Entity Network for Sensors</h1><hr>
		<p>Topic relationship between sensors</p>
	</div>
	
	<div class="row-fluid">
		<div class="col-xs-4">
			<div class="row-fluid">
				<input id="query-feedid"  type="text" class="form-control buttom-buffer" placeholder="Feed ID..."/>
			</div>
			<!-- <div class="form-group col-xs-6"> -->
			<div class="row-fluid">
				<input id="query-streamid"  type="text" class="form-control buttom-buffer" placeholder="Stream ID..."/>
			</div>
			<div class="row-fluid">
				<input id="query-sensornum"  type="text" class="form-control buttom-buffer" placeholder="Number of sensor..."/>
			</div>
			<div class="row-fluid">
				<div class="dropdown">
					<button class="btn btn-default dropdown-toggle" type="button"
						id="query-graph-type" data-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false">
						Draw based on <span class="caret"></span>
					</button>
					<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
						<li><a href="#">Topic vector</a></li>
					    <li><a href="#">Document vector</a></li>
					    <li><a href="#">Time and place relationship</a></li>
					</ul>
				</div>
			</div>
			<div class="row-fluid"><br/></div>
			<div class="row-fluid">
				<button class="btn btn-default" type="submit" id='search-btn'>Submit Query</button>
			</div>
		</div>
	</div>
	<div class="col-xs-6">
		<div class="span8">
			<div id="graph_search" class="ui-widget">
			   <input id="search">
			    <button type="button" onclick="searchNode()">Quick Locate</button>
			</div>
			<svg width="0" height="0"></svg>
		</div><!--/span-->
	</div>
</div> <!--/span-->

<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js" ></script>
<script src="https://d3js.org/d3.v4.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script lang="javascript">

$('#nav_list').children().eq(9).addClass('active');
$('#graph_search').hide();
$(".dropdown-menu a").click(function(){
    $(".dropdown .btn:first-child").text($(this).text());
    $(".dropdown .btn:first-child").val($(this).text());
 });

var searchNode = null;

$('#search-btn').on('click', function(){
	
	var svg = d3.select("svg");
	svg.attr("width", 960);
	svg.attr("height", 600);
// 	svg.setAttribute("width",  960);
// 	svg.setAttribute("height", 600);
	
    width = +svg.attr("width"),
    height = +svg.attr("height");
	svg.selectAll("*").remove();
	
	var color = d3.scaleOrdinal(d3.schemeCategory20);
	var simulation = d3.forceSimulation()
	    .force("link", d3.forceLink().id(function(d) { return d.id; }))
	    .force("charge", d3.forceManyBody())
	    .force("center", d3.forceCenter((width>>2),(height>>2)));
	var div = d3.select("body").append("div")	
	    .attr("class", "tooltip")
	    .style("opacity", 0);
	
	// Drag effect
	function dragstarted(d) {
		if (!d3.event.active) simulation.alphaTarget(0.3).restart();
		d.fx = d.x;
		d.fy = d.y;
	}

	function dragged(d) {
		d.fx = d3.event.x;
		d.fy = d3.event.y;
	}

	function dragended(d) {
		if (!d3.event.active) simulation.alphaTarget(0);
		d.fx = null;
		d.fy = null;
	}
	
	d3.json("search/visual.json", function(error, graph) {
		if (error) throw error;
		
		$('#graph_search').show();
		var link = svg.append("g")
			.attr("class", "links").selectAll("line").data(graph.links).enter().append("line")
			.attr("stroke-width", function(d) { return Math.sqrt(d.value); });
		var node = svg.append("g")
			.attr("class", "nodes").selectAll("circle").data(graph.nodes).enter().append("circle")
		    .attr("r", 5).attr("fill", function(d) { return color(d.group); })
		    .call(d3.drag().on("start", dragstarted).on("drag", dragged).on("end", dragended))
		    .on("mouseover", function(d) {		
	            div.transition()		
	               .duration(200)		
	               .style("opacity", .9);
	            
	            // reformat id
	            var fs = d.id;
	            
	            var html_str = "Feed ID: " + fs.substr(0, fs.indexOf(',')) + "<br/>";
	            html_str += "Stream ID: " + fs.substr(fs.indexOf(',') + 1);
	            
	            div.html(html_str)	
	               .style("left", (d3.event.pageX) + "px")		
	               .style("top", (d3.event.pageY - 56) + "px");	
            }).on("mouseout", function(d) {		
	            div.transition()		
	               .duration(500)		
	               .style("opacity", 0);	
        	});
		
		node.append("title").text(function(d) { return d.id; });
		simulation.nodes(graph.nodes).on("tick", ticked);
		simulation.force("link").links(graph.links);
		function ticked() {
			link
				.attr("x1", function(d) { return d.source.x; })
		    	.attr("y1", function(d) { return d.source.y; })
		    	.attr("x2", function(d) { return d.target.x; })
		    	.attr("y2", function(d) { return d.target.y; });
			node
				.attr("cx", function(d) { return d.x; })
				.attr("cy", function(d) { return d.y; });
		}
		
		//add search function
		var optArray = [];
		for (var i = 0; i < graph.nodes.length - 1; i++) {
		    optArray.push(graph.nodes[i].id);
		}
		optArray = optArray.sort();
		$(function () {
		    $("#search").autocomplete({
		        source: optArray
		    });
		});

		searchNode = function() {
		    //find the node
		    var selectedVal = document.getElementById('search').value;
		    var node = svg.selectAll("circle");
		    if (selectedVal == "none") {
		        node.style("stroke", "white").style("stroke-width", "1");
		    } else {
		        var selected = node.filter(function (d, i) {
		            return d.id != selectedVal;
		        });
		        selected.style("opacity", "0");
		        var link = svg.selectAll("line")
		        link.style("opacity", "0");
		        d3.selectAll("circle, line").transition()
		            .duration(5000)
		            .style("opacity", 1);
		    }
		}
	}).header("Content-Type","application/json")
	.send("POST", JSON.stringify({
		feedid: $("#query-feedid").val(),
		streamid: $("#query-streamid").val(),
		sensorNum: $("#query-sensornum").val(),
		graphType: $(".dropdown .btn:first-child").text()
	}));
});

</script>

</body>
</html>