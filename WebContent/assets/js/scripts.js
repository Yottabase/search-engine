
/*** env ***/
var resultsMode = false;
var webPageTemplate = $(' \
	<div class="web-page"> \
		<div class="title"><a href="#">Prova</a></div> \
		<div class="snippet">La mia descrizione</div> \
		<div class="url"><a href="#"></a></div> \
	</div>');


/*** gestione ricerca ***/
jQuery( document ).ready(function( $ ) {
	$('#search-input').on('input', function(){
		var q = $(this).val();
		clearResults();
		if(q.length != 0){
			performQuery(q);	
		}
	});
});

/*** autocompletamento ***/
jQuery( document ).ready(function( $ ) {	

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

/*** aggiunge highlightWords ***/
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

/*** esegue query e disegna risultati ***/
function performQuery(query, page){
	
	$.get("apiSearch.do", {
		"q" : query, 
		"page" : page
		}).done( function( data ) {
		
		console.log(data);
		
		var resultsBlock = $('#results');
		$('#stats').text("Circa "+data.itemsCount+" risultati ("+data.queryResponseTime+" secondi)");
		
		//disegna nuovi blocchi
		data.webPages.forEach(function(item){
			//mette in grassetto le parole degli snippets presenti nella query
			var snippet = item.snippet;
			item.highlights.forEach(function(w){
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
		
		//aggiunge ricerche suggerite
		if(data.suggestedSearch.length > 0){
			data.suggestedSearch.forEach(function(item){
				$('#suggested-search .words').append('<a href="#">'+item+'</a>');
			});
			$('#suggested-search').show();
		}
		
		if(! resultsMode ){ gotoResultsMode(); }
		resultsBlock.fadeIn(500);
		
	});
}


//effetua lo swith alla modalità risultati
function gotoResultsMode(){
	var resultsBlock = $('#results');
	
	resultsMode = true;
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


function clearResults(){
	//elimina vecchi risultati
	var resultsBlock = $('#results');
	resultsBlock.fadeOut(200);
	resultsBlock.empty();
	resultsBlock.stop(true, true);
	$('#suggested-search').hide();
	$('#suggested-search .words').empty();
	
	//aggiorna statistiche
	$('#stats').text("");
};





//END