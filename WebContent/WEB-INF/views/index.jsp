<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="">
<!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Sensor Finder</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="apple-touch-icon" href="apple-touch-icon.png">
<link rel="shortcut icon" href="../resources/favicon.ico">

<link rel="stylesheet" href="../resources/css/bootstrap.min.css">
<link rel="stylesheet" href="../resources/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="../resources/css/main.css">

<!--[if lt IE 9]>
            <script src="js/vendor/html5-3.6-respond-1.4.2.min.js"></script>
        <![endif]-->
</head>
<body>
	<!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. 
            Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->

	<div class="container fill">
		<div class="row vertical-center">
			<div
				class="col-xs-10 col-xs-offset-1 col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3">

				<form>
					<div class="form-group text-center">
						<label for="search-query">Sensor Finder</label> <input type="text"
							class="form-control" id="search-query" name="search-query"
							placeholder="Type your query">
					</div>

					<div class="form-group text-center">
						<button type="button" class="btn btn-primary" id='do-search'>Search</button>
						<a id="advanced-search-button" class="btn btn-default">Advanced
							search</a>
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
													<input type="checkbox" name="search-location" id="source-is-xively"
														value="option1" autocomplete="off" checked> Xively
												</fieldset>

												<fieldset class="col-xs-12 col-sm-6">
													<input type="checkbox" name="search-location" id="source-is-thingspeak"
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

	<script src="../resources/js/vendor/jquery-1.11.2.min.js"></script>
	<script src="../resources/js/vendor/bootstrap.min.js"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#search-query').focus();
		});

		$('#advanced-search-button').click(function() {
			$('.advanced-search-panel').slideToggle('fast');
			$(this).toggleClass('active');
		});

		$(document).ready(function($) {
			$('#tabs').tab();
		});
		
		$('#do-search').click(function(){
			var q = encodeURIComponent($('#search-query').val());
			var option1 = encodeURIComponent($('#source-is-xively').is(':checked'));
			var option2 = encodeURIComponent($('#source-is-thingspeak').is(':checked'));
			var radio = 0;
			var radio_choices = ['radio_keyword', 'radio_keyword_topic'];
			for(var i=0; i<radio_choices.length; i++){
				if($('#'+radio_choices[i])==true){
					radio = i;
					break;
				}
			}
			radio = encodeURIComponent(radio);
			window.location = '../results?q=' + q + '&opt1=' + option1 + '&opt2=' + option2 + '&radio=' + radio;
		});
		
	</script>
	
</body>
</html>
