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
    <link rel="shortcut icon" href="../favicon.ico">

    <link rel="stylesheet" href="../resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="../resources/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="../resources/css/main.css">

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
						<a id="advanced-search-button" class="btn btn-default">Advanced search</a>
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
														value="Option 2" autocomplete="off" checked>
													ThingSpeak
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
													<input type="checkbox" name="search-location"
														value="option1" autocomplete="off" checked>
													Keyword
												</fieldset>

												<fieldset class="col-xs-12 col-sm-6">
													<input type="checkbox" name="search-location"
														value="option2" autocomplete="off" checked> Topic
												</fieldset>

												<fieldset class="col-xs-12 col-sm-6">
													<input type="checkbox" name="search-location"
														value="option3" autocomplete="off" checked> Time
													stamp
												</fieldset>

												<fieldset class="col-xs-12 col-sm-6">
													<input type="checkbox" name="search-location"
														value="option4" autocomplete="off" checked>
													Spatial
												</fieldset>
											</div>
										</div>
									</div>

									<div class="clearfix"></div>
								</li>
								<!-- end of options -->
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
						aria-controls="results1" role="tab" data-toggle="tab">Results
							tab 1</a></li>
					<li role="presentation"><a href="#results2"
						aria-controls="results2" role="tab" data-toggle="tab">Results
							tab 2</a></li>
				</ul>

				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane tab-body active" id="results1">
						<div class="list-group" id="search-result"></div>
					</div>
					<div role="tabpanel" class="tab-pane tab-body" id="results2">
						...</div>
				</div>

			</div>
		</div>
	</div>

<script src="../resources/js/vendor/jquery-1.11.2.min.js"></script>
<script src="../resources/js/vendor/bootstrap.min.js"></script>
<script src="../resources/js/main.js"></script>
<script type="text/javascript">

var caption_init = false;

var searchit = function(event) {
	var text_val = $('#search-query').val()
	if(text_val.length > 0){
		console.log(text_val);		
		$.ajax({
		    headers: {
		    	'Accept': 'application/json',
		        'Content-Type': 'application/json' 
		    },
		    url: 'search/mixedsearch',
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
					
					var metadata_str = '<div class="panel panel-default list-group-item">';
					metadata_str += '<div class="panel-body">';
					metadata_str += '<div class="media">'
					metadata_str += '<div class="media-left">'
// 					metadata_str += '<a href="#"> <img class="media-object" src="' + geo_url(value['location']) + '" alt="..."></a>';
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

// $('input-form').submit()

// use openstreet for location picture
function geo_url(){
	
}

$('#search-button').on('click', searchit);

$('#input-form').submit(function(event){
	event.preventDefault();
	searchit();
});

</script>
</body>
</html>
