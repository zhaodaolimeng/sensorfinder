<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>simple-search</title>
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

				<form>
					<div class="form-group text-center">
						<label for="search-query">Sensor Finder</label> <input type="text"
							class="form-control" id="search-query" name="search-query"
							placeholder="Type your query">
					</div>

					<div class="form-group text-center">
						<button type="submit" class="btn btn-primary">Search</button>
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
						<div class="list-group">

							<div class="panel panel-default list-group-item">
								<div class="panel-body">
									<div class="media">
										<div class="media-left">
											<a href="#"> <img class="media-object"
												src="http://placehold.it/90x120" alt="...">
											</a>
										</div>
										<div class="media-body">
											<h4 class="media-heading">Trapézio Descendente</h4>
											Aqui é Body Builder Ipsum PORRA! Vo derrubar tudo essas
											árvore do parque ibirapuera. Eita porra!, tá saindo da
											jaula o monstro! Ajuda o maluco que tá doente. Não vai dá
											não. Vai subir árvore é o caralho porra! Tá comigo porra.
										</div>
									</div>
								</div>
							</div>

							<div class="panel panel-default list-group-item">
								<div class="panel-body">
									<div class="media">
										<div class="media-left">
											<a href="#"> <img class="media-object"
												src="http://placehold.it/90x120" alt="...">
											</a>
										</div>
										<div class="media-body">
											<h4 class="media-heading">Saindo da Jaula</h4>
											Eita porra!, tá saindo da jaula o monstro! Sai filho da
											puta! É 13 porra! Sabe o que é isso daí? Trapézio
											descendente é o nome disso aí. Vamo monstro! É esse que a
											gente quer, é ele que nóis vamo buscar.
										</div>
									</div>
								</div>
							</div>

							<div class="panel panel-default list-group-item">
								<div class="panel-body">
									<div class="media">
										<div class="media-left">
											<a href="#"> <img class="media-object"
												src="http://placehold.it/90x120" alt="...">
											</a>
										</div>
										<div class="media-body">
											<h4 class="media-heading">É ele que nós vamos buscar</h4>
											Vo derrubar tudo essas árvore do parque ibirapuera. Ó o
											homem ali porra!, é 13 porra! É nóis caraio é trapezera
											buscando caraio! Sai de casa comi pra caralho porra. Vai
											subir árvore é o caralho porra! Bora caralho, você quer
											ver essa porra velho.
										</div>
									</div>
								</div>
							</div>

							<div class="panel panel-default list-group-item">
								<div class="panel-body">
									<div class="media">
										<div class="media-left">
											<a href="#"> <img class="media-object"
												src="http://placehold.it/90x120" alt="...">
											</a>
										</div>
										<div class="media-body">
											<h4 class="media-heading">BIRL</h4>
											Aqui nóis constrói fibra, não é água com músculo. Sabe
											o que é isso daí? Trapézio descendente é o nome disso
											aí. Vo derrubar tudo essas árvore do parque ibirapuera.
											Aqui é bodybuilder porra! Boraaa, Hora do Show Porra. Vai
											subir árvore é o caralho porra!
										</div>
									</div>
								</div>
							</div>
							
							
							<div class="panel panel-default list-group-item">
								<div class="panel-body">
									<div class="media">
										<div class="media-left">
											<a href="#"> <img class="media-object"
												src="http://placehold.it/90x120" alt="...">
											</a>
										</div>
										<div class="media-body">
											<h4 class="media-heading">Água com músculo</h4>
											Aqui é bodybuilder porra! Que não vai dá rapaiz, não vai
											dá essa porra. Eu quero esse 13 daqui a pouquinho aí.
											AHHHHHHHHHHHHHHHHHHHHHH..., porra! Bora caralho, você quer
											ver essa porra velho. É verão o ano todo vem cumpadi.
										</div>
									</div>
								</div>
							</div>

							
						</div>
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
var searchit = function(event) {
	var text_val = $('#query-string').val()
	if(text_val.length > 0){
		console.log(text_val);		
		$.ajax({
		    headers: {
		    	'Accept': 'application/json',
		        'Content-Type': 'application/json' 
		    },
		    url: 'user/search/search',
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
					metadata_str += '<a href="#"> <img class="media-object" src="http://placehold.it/90x120" alt="..."></a>';
					metadata_str += '</div>';
					metadata_str += '<div class="media-body">';
					metadata_str += '<h4 class="media-heading">Title</h4>';
					metadata_str += '</div></div></div></div>';
					
// 					var metadata_str = "<div class=\"col-xs-12 col-sm-12 col-md-3\"><ul class=\"meta-search\">";
// 					metadata_str +=	"<li><i class=\"glyphicon glyphicon-th-large\"></i><span>Feed ID: ";
// 					metadata_str += value['feedid'] + "</span></li><li><i class=\"glyphicon glyphicon-th\"></i><span>Stream ID: ";
// 					metadata_str += value['sensorid'] + "</span></li><li><i class=\"glyphicon glyphicon-time\"></i><span>Created Time:";
// 					metadata_str += value['createdTime'] + "</span></li></ul></div>";
					
// 					var snapshort_str = "<div class=\"col-xs-12 col-sm-12 col-md-8 excerpet\"><h3><a href=\"#\" title=\"\">";
// 					snapshort_str += value['feedTitle'];
// 					snapshort_str += "</a></h3><p>";
// 					snapshort_str += value['snapshot'];
// 					snapshort_str += "</p></div><span class=\"clearfix borda\"></span>";

					$('#search-result').append("<article class=\"search-result row\">" + metadata_str + snapshort_str + "</article>");
				});
				
				$('#search-result').show();
		    }
		})
	}
}
</script>
</body>
</html>
