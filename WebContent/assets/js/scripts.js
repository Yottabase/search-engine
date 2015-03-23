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
			
			$('#suggested-search .words').empty();
			data.suggestedSearch.forEach(function(item){
				$('#suggested-search .words').append('<a href="#">'+item+'</a>');
			});
			$('#suggested-search').show();
			
			
			if(! hasResults){
				hasResults = true;
				$('#brand img').fadeOut(500, function(){
					
					$('#brand').animate({
						  height: 0,
					}, 300, function(){
						$('#brand img').removeClass('center-block');
						$('#brand').addClass('little');
						$('#brand img').fadeIn(200);
					});
				});
			}
			resultsBlock.fadeIn(500);
			
		});
	});
});