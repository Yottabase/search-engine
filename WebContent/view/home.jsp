<%@ include file="layout/header.jsp"%>

<div class="container">

	<div id="search-form">

		<div id="brand">
			<img alt="" class="img-responsive center-block"
				src="assets/images/brand.jpg">
		</div>

		<form>
			<div class="row">
				<div class="col-xs-6 col-xs-offset-3">

					<div class="input-group">
						<input type="text" class="form-control input-lg"
							placeholder="Cosa vuoi cercare oggi?" id="search-input"> 
						
						<span class="input-group-btn">
							<button class="btn btn-primary btn-lg" type="button">
								<span class="glyphicon glyphicon-search" aria-hidden="true"></span> Cerca
								</button>
						</span>
					</div>
					<!-- /input-group -->
				</div>

				<div class="col-xs-6"></div>
			</div>
		</form>
		
			<div class="row">
				<div class="col-xs-6 col-xs-offset-3">
		
					<div id="results">
					
						<div class="web-page">
							<div class="title"><a href="#">Prova</a></div>
							<div class="snippet">La mia descrizione</div>
							<div class="url"><a href="#">http://www.google.it</a></div>
						</div>
						
						<div class="web-page">
							<div class="title"><a href="#">Prova</a></div>
							<div class="snippet">La mia descrizione</div>
							<div class="url"><a href="#">http://www.google.it</a></div>
						</div>
						
						<div class="web-page">
							<div class="title"><a href="#">Prova</a></div>
							<div class="snippet">La mia descrizione</div>
							<div class="url"><a href="#">http://www.google.it</a></div>
						</div>
					
					
					</div>
				</div>
			</div>
	</div>


</div>
<!-- /.container -->

<%@ include file="layout/footer.jsp"%>