<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Sensor Finder</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <link rel="shortcut icon" href="resources/favicon.ico">

    <link rel="stylesheet" href="resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="resources/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="resources/css/main.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/openlayers/4.1.1/ol.css">

    <!--[if lt IE 9]>
        <script src="js/vendor/html5-3.6-respond-1.4.2.min.js"></script>
    <![endif]-->
</head>
<body style="padding-top: 40px;">
	<!--[if lt IE 8]>
	    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
	<![endif]-->
	<div class="container">
		<div class="row">
			<div
				class="col-xs-10 col-xs-offset-1 col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3">

				<form id="input-form">
					<div class="form-group text-center">
						<label for="search-query">Sensor Finder</label> <input type="text"
							class="form-control" id="search-query" name="search-query"
							placeholder="Type your query">
					</div>

					<div class="form-group text-center">
						<a id="search-button" class="btn btn-primary">Search</a>
						<a id="advanced-search-button" class="btn btn-default">Advanced Options</a>
					</div>

					<div class="form-group advanced-search-panel">
						<div class="panel panel-default">
							<ul class="list-group">

								<!-- add options here -->
								<li class="list-group-item">
									<div class="form-group">
										<div class="col-xs-4">
											<label for="search-location">Source</label>
										</div>

										<div class="col-xs-8">
											<div class="row">
												<fieldset class="col-xs-12 col-sm-6">
													<input type="checkbox" name="search-location"
														value="option1" autocomplete="off" checked> Xively
												</fieldset>

												<fieldset class="col-xs-12 col-sm-6">
													<input type="checkbox" name="search-location"
														value="Option 2" autocomplete="off" checked> ThingSpeak
												</fieldset>
											</div>
										</div>
									</div>

									<div class="clearfix"></div>
								</li>

								<li class="list-group-item">
									<div class="form-group">
										<div class="col-xs-4">
											<label for="search-location">Relevance</label>
										</div>

										<div class="col-xs-8">
											<div class="row">
												<fieldset class="col-xs-12 col-sm-6">
													<input type="radio" name="search-option" id="radio_keyword"
														value="option1" autocomplete="off" checked> Keyword
												</fieldset>
												<fieldset class="col-xs-12 col-sm-6">
													<input type="radio" name="search-option" id="radio_keyword_topic"
														value="option2" autocomplete="off"> Topic and Keyword 
												</fieldset>
											</div>
										</div>
									</div>
									<div class="clearfix"></div>
								</li> <!-- end of options -->
							</ul>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

	<div class="container">
		<div class="row">
			<div>

				<ul class="nav nav-tabs" role="tablist">
					<li role="presentation" class="active"><a href="#results1"
						aria-controls="results1" role="tab" data-toggle="tab">Text Results</a></li>
					<li role="presentation"><a href="#results2"
						aria-controls="results2" role="tab" data-toggle="tab">Location</a></li>
				</ul>

				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane tab-body active" id="results1">
						<div class="list-group" id="search-result"></div>
					</div>
					<div role="tabpanel" class="tab-pane tab-body" id="results2">
						<div id="map" class="map" style="height:250px"></div>
					</div>
				</div>
			</div>
		</div>
	</div>

<script src="resources/js/vendor/jquery-1.11.2.min.js"></script>
<script src="resources/js/vendor/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/openlayers/4.1.1/ol.js"></script>
<script type="text/javascript">

// search result cache 
var recent_result_list = null;
var map = null;

// set marker for a single sensor
function createSensorOnMap(lat, lng){
	var marker = new ol.Feature({
		geometry: new ol.geom.Point(ol.proj.fromLonLat([lng, lat]))
	});
	var markerStyle = new ol.style.Style({
		image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
			anchor: [0.5, 46],
			anchorXUnits: 'fraction',
			anchorYUnits: 'pixels',
			src: 'https://openlayers.org/en/v4.1.1/examples/data/icon.png'
		}))
	});
	marker.setStyle(markerStyle);
	return marker;
}

// set map
$(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
	
	if(recent_result_list == null)
		return;
	
	// extract sensor location
	var markerList = []
	$.each(recent_result_list['itemlist'], function(index, value){
		var lat = parseFloat(value['lat']);
		var lng = parseFloat(value['lng']);
		if(!isNaN(lat) && !isNaN(lng)){
			markerList.push(createSensorOnMap(lat, lng))
			console.log(lat + ' ' + lng);
		}
	});
	var vectorLayer = new ol.layer.Vector({
		source: new ol.source.Vector({
			features: markerList
		})
	});
	
	if(map != null) {
		vectorLayer.changed();
	}else{
		map = new ol.Map({
			layers: [
				new ol.layer.Tile({source: new ol.source.OSM()}),
				vectorLayer
			],
			target: document.getElementById('map'),
			view: new ol.View({
				center: [0, 0],
				zoom: 3
			})
		});
	}
})

// set search action
var searchit = function() {
	
	var text_val = $('#search-query').val()
	var request_json = {query : text_val}
	
	if($("#radio_keyword:checked").val())
		request_json.assistedWithTopic = false;
	else
		request_json.assistedWithTopic = true;
	
	if(text_val.length > 0){
		console.log(text_val);		
		$.ajax({
		    headers: {
		    	'Accept': 'application/json',
		        'Content-Type': 'application/json' 
		    },
		    url: 'search/search',
		    type: 'POST',
		    data: JSON.stringify(request_json),
		    success : function(data) {
		    	recent_result_list = data['content']['result'];
		    	
				// console.log(recent_result_list);
				$('#search-result').empty();
				$.each(recent_result_list['itemlist'], function(index, value){
					var metadata_str = '<div class="panel panel-default list-group-item">';
					metadata_str += '<div class="panel-body">';
					metadata_str += '<div class="media">'
					metadata_str += '<div class="media-left">'
					metadata_str += '</div>';
					metadata_str += '<div class="media-body">';
					metadata_str += '<h4 class="media-heading">' + value['feedid'] + '\t' + value['sensorid'] + '</h4> ';
					metadata_str += value['feedTitle'] + '<br/>' + value['snapshot'];
					metadata_str += '</div></div></div></div>';
					$('#search-result').append("<article class=\"search-result row\">" + metadata_str + "</article>");
				});
				$('#search-result').show();
		    }
		})
	}
}

$('#search-button').on('click', searchit);

$('#input-form').submit(function(event){
	event.preventDefault();
	searchit();
});

//check if parameter is set
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName, i;
    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] === sParam) 
            return sParameterName[1] === undefined ? true : sParameterName[1];
    }
};

if(undefined !== getUrlParameter('q')){
	$('#search-query').val(getUrlParameter('q'));
	//TODO datasource selector is not used, currently all the data is from Xively
	// var option1 = getUrlParameter('opt1');
	// var option2 = getUrlParameter('opt2');
	var radio_list = ['#radio_keyword', '#radio_keyword_topic'];
	$(radio_list[parseInt(getUrlParameter('radio'))]).prop("checked", true);
	searchit();
}

</script>
</body>
</html>
