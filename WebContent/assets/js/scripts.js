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
		
		var q = $(this).val();
		
		$.get("apiSearch.do", function( data ) {
			
			console.log(data);
			
			//elimina vecchi risultati
			resultsBlock.fadeOut(200);
			resultsBlock.empty();
			resultsBlock.stop(true, true);
			$('#suggested-search').hide();
			$('#suggested-search .words').empty();
			
			//aggiorna statistiche
			$('#stats').text("Circa "+data.itemsCount+" risultati ("+data.queryResponseTime+" secondi)");
			
			//splitta le parole della query
			var queryWords = q.split(" ");
			queryWords = queryWords.remove(" ");
			queryWords = queryWords.remove("");
			console.log(queryWords);
			
			//disegna nuovi blocchi
			data.webPages.forEach(function(item){
				
				//mette in grassetto le parole degli snippets presenti nella query
				var snippet = item.snippet;
				queryWords.forEach(function(w){
					snippet = highlightWords(snippet, w);
				});
				
				//crea item di output
				var resultBlock = webPageTemplate.clone();
				resultBlock.find('.title a').text(item.title);
				resultBlock.find('.snippet').html(snippet);
				resultBlock.find('.url a').text(item.url);
				resultBlock.find('a').prop('href',item.url);
				
				resultsBlock.append(resultBlock);
			});
			
			
			
			//aggiunge parole suggerite
			data.suggestedSearch.forEach(function(item){
				$('#suggested-search .words').append('<a href="#">'+item+'</a>');
			});
			$('#suggested-search').show();
			
			//effetua lo swith alla modalità risultati
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
			
			//mostra risultati
			resultsBlock.fadeIn(500);
			
		});
	});

	
	//begin TYPEAHEAD
	var suggestions = new Bloodhound({
	  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
	  queryTokenizer: Bloodhound.tokenizers.whitespace,
	  remote: 'apiAutocomplete.do?q=%QUERY'
	});
		 
	suggestions.initialize();
		 
	$('#search-input').typeahead(null, {
	  name: 'suggestions',
	  hint: true,
	  highlight: true,
	  minLength: 1,
	  displayKey: 'value',
	  source: suggestions.ttAdapter()
	});
	//end TYPEAHEAD
});

/*** funzione per highlightWords ***/
function highlightWords( line, word )
{
     var regex = new RegExp( '(' + word + ')', 'gi' );
     return line.replace( regex, "<strong>$1</strong>" );
}

/*** funzione che rimuove elementi da un array ***/
Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

//END