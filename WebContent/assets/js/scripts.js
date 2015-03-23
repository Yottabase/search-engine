jQuery( document ).ready(function( $ ) {

	var resultsBlock = $('#results');
	
	var webPageTemplate = $(' \
		<div class="web-page"> \
			<div class="title"><a href="#">Prova</a></div> \
			<div class="snippet">La mia descrizione</div> \
			<div class="url"><a href="#"></a></div> \
		</div>');
	
	
	var hasResults = false;
	
	$('#search-input').on('input', function(){
		
		console.log("query:" + $(this).val());
		
		$.get("apiSearch.do", function( data ) {
			
			console.log(data);
			
			resultsBlock.empty();
			resultsBlock.stop(true, true);
			resultsBlock.fadeOut(200);
			
			data.webPages.forEach(function(item){
				
				var resultBlock = webPageTemplate.clone();
				resultBlock.find('.title a').text(item.title);
				resultBlock.find('.snippet').text(item.snippet);
				resultBlock.find('.url a').text(item.url);
				resultBlock.find('a').prop('href',item.url);
				
				resultsBlock.append(resultBlock);
				console.log(resultBlock.html());
				
			});
			
			if(! hasResults){
				hasResults = true;
				$('#search-form').animate({
					  marginTop: 5,
				}, { duration: 200, queue: false });
				$('#brand').animate({
					  marginBottom: 10,
				}, { duration: 200, queue: false });
			}
			resultsBlock.fadeIn(500);
			
		});
	});
});