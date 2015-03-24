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
							placeholder="Cosa vuoi cercare oggi?" id="search-input"  autocomplete="off"> 
						
						<span class="input-group-btn">
							<button class="btn btn-primary btn-lg" type="button">
								<span class="glyphicon glyphicon-search" aria-hidden="true"></span> Cerca
								</button>
						</span>
					</div>
					<div id="suggested-search">
						Forse cercavi <span class="words"><a href="#">Prova</a></span>
					</div>
						<!-- /input-group -->
				</div>

				<div class="col-xs-6"></div>
			</div>
		</form>
		
			<div class="row">
				<div class="col-xs-6 col-xs-offset-3">
		
					<div id="stats"></div>
					<div id="results">
						<!-- qui ci vanno i risultati -->
					</div>
				</div>
			</div>
	</div>


</div>
<!-- /.container -->

<%@ include file="layout/footer.jsp"%>