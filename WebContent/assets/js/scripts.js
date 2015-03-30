
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

/*** gestione click pulsante ***/
jQuery( document ).ready(function( $ ) {
	$('#search-button').on('click submit', function(event){
		var q = $('#search-input').val();
		clearResults();
		if(q.length != 0){
			performQuery(q);
		}
		event.preventDefault();
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
	
	page = page || 1;
	
	$.get("apiSearch.do", {
		"q" : query, 
		"page" : page
		}).done( function( data ) {
		
		console.log(data);
		
		var resultsBlock = $('#results');
		$('#stats').text("Circa "+data.itemsCount+" risultati ("+data.queryResponseTime+" secondi)");
		
		//disegna nuovi blocchi
		data.webPages.forEach(function(item){
			
			var url = item.url;
			if (url.length > 90){
				url = url.substring(0,90) + '...';
			}
			
			//crea item di output
			var resultBlock = webPageTemplate.clone();
			resultBlock.find('.title a').text(item.title);
			resultBlock.find('.snippet').html(item.highlightedSnippet);
			resultBlock.find('.url a').text(url);
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
		
		drawPageNumbers(page, data.itemsCount, data.itemsInPage)
		
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
	$('#pagination').empty().off();
	
	//aggiorna statistiche
	$('#stats').text("");
};


function drawPageNumbers(currentPage, itemsCount, itemsInPage){
	
	var totPage = Math.ceil(itemsCount / itemsInPage);
	
	$('#pagination').bootpag({
	    total: totPage,
	    page: currentPage,
	    maxVisible: 10,
	    leaps: true
	}).on("page", function(event, num){
		
		var q = $('#search-input').val();
		clearResults();
		
		if(q.length != 0){
			performQuery(q, num);	
		}
	    
	});
	
}


// speech to text
jQuery( document ).ready(function( $ ) {
	if(annyang){
		var commands = {
			'ciao': function() { 
				alert('Hello world!'); 
			},
			'cercami *query': function(query) {
				
				$('#search-input').val(query);
				clearResults();
				performQuery(query, 1);
			},
			'pagina :page': function(page) {
				
				var q = $('#search-input').val();
				clearResults();
				
				if(q.length != 0){
					performQuery(q, page);	
				}
			},
			'scendi' : function(page) {
				$('body').animate({
				      scrollTop: $(document).height() - $(window).height()
				    }, 5000
				);
			},
			'sali' : function(page) {
				$('body').animate({
				      scrollTop: 0
				    }, 5000
				);
			},
			'stop' : function(page) {
				$('body').stop(true, true);
			},
		};
		
		annyang.addCommands(commands);
		annyang.debug();
		annyang.setLanguage('it_IT');
		
		//annyang.start();
		annyang.start({ autoRestart: true, continuous: true });
	}
});




//END