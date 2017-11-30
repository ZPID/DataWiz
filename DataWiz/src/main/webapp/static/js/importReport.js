var GLOBAL_LOG = true;
var CODEBOOK_VAR_AMOUNT_PER_PAGE;
var CODEBOOK_PAGES;
var CODEBOOK_ACT_PAGE;
var CODEBOOK_ELEMENTS = $("#lazyLoadCodebook");

$(document).ready(function() {
	if (window.location.pathname.includes("/importReport") || window.location.pathname.includes("/data")) {
		$('#lazyLoadImportMatrix').DataTable({
		  processing : true,
		  ordering : false,
		  scrollX : true,
		  serverSide : true,
		  ajax : {
		    url : 'getMatrixAsync/import',
		    type : 'POST'
		  }
		});
		initializeCodebook(5);
		$('#lazyLoadFinalMatrix').DataTable({
		  processing : true,
		  ordering : false,
		  scrollX : true,
		  serverSide : true,
		  ajax : {
		    url : 'getMatrixAsync/final',
		    type : 'POST'
		  }
		});

	}
});

function loadDataTable(tColumns) {

}

$('#pagerCodebookNext1').click(function(e) {
	e.preventDefault();
	codeBookGoTo(++CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookNext2').click(function(e) {
	e.preventDefault();
	CODEBOOK_ACT_PAGE += 2;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookNext5').click(function(e) {
	e.preventDefault();
	CODEBOOK_ACT_PAGE += 5;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookPrev1').click(function(e) {
	e.preventDefault();
	codeBookGoTo(--CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookPrev2').click(function(e) {
	e.preventDefault();
	CODEBOOK_ACT_PAGE -= 2;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookPrev5').click(function(e) {
	e.preventDefault();
	CODEBOOK_ACT_PAGE -= 5;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookEnd').click(function(e) {
	e.preventDefault();
	CODEBOOK_ACT_PAGE = CODEBOOK_PAGES;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
});

$('#pagerCodebookBegin').click(function(e) {
	e.preventDefault();
	CODEBOOK_ACT_PAGE = 1;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
});

function setPagerElements() {
	$('#actCodeBookEntreeNum').val(CODEBOOK_VAR_AMOUNT_PER_PAGE);
	$('#actCodeBookPage').val(CODEBOOK_ACT_PAGE);
	$('#maxCodeBookPage').html("/ " + (CODEBOOK_PAGES));
	$('.codeBookPagerItems').hide();
	$('#pagerCodebookPrev2').html(CODEBOOK_ACT_PAGE - 2);
	$('#pagerCodebookPrev1').html(CODEBOOK_ACT_PAGE - 1);
	$('#pagerCodebookAct').html(CODEBOOK_ACT_PAGE);
	$('#pagerCodebookNext1').html(CODEBOOK_ACT_PAGE + 1);
	$('#pagerCodebookNext2').html(CODEBOOK_ACT_PAGE + 2);
	if (CODEBOOK_ACT_PAGE >= 0) {
		$('#pagerCodebookAct').show();
	}
	if (CODEBOOK_ACT_PAGE > 1) {
		$('#pagerCodebookBegin').show();
		$('#pagerCodebookPrev1').show();
	}
	if (CODEBOOK_ACT_PAGE > 2) {
		$('#pagerCodebookPrev2').show();
	}
	if (CODEBOOK_ACT_PAGE > 5) {
		$('#pagerCodebookPrev5').show();
	}
	if (CODEBOOK_PAGES > 0 && CODEBOOK_ACT_PAGE < CODEBOOK_PAGES) {
		$('#pagerCodebookEnd').show();
	}
	if (CODEBOOK_ACT_PAGE < CODEBOOK_PAGES) {
		$('#pagerCodebookNext1').show();
	}
	if (CODEBOOK_ACT_PAGE + 1 < CODEBOOK_PAGES) {
		$('#pagerCodebookNext2').show();
	}
	if (CODEBOOK_ACT_PAGE + 5 <= CODEBOOK_PAGES) {
		$('#pagerCodebookNext5').show();
	}
}

$("#actCodeBookEntreeNum").change(function(e) {
	initializeCodebook(parseInt($("#actCodeBookEntreeNum").val()));
});

$("#actCodeBookPage").keyup(function(e) {
	var input = $("#actCodeBookPage").val();
	if (is_int(input)) {
		input = parseInt(input);
		if (input <= 0)
			input = 1;
		else if (input > CODEBOOK_PAGES)
			input = CODEBOOK_PAGES;
		console.log(input)
		CODEBOOK_ACT_PAGE = input;
		codeBookGoTo(CODEBOOK_ACT_PAGE);
	} else if (input === "") {

	} else {
		$("#actCodeBookPage").val(CODEBOOK_ACT_PAGE)
	}
});

function initializeCodebook(numOfCodeBookElements) {
	CODEBOOK_VAR_AMOUNT_PER_PAGE = numOfCodeBookElements;
	var childrenCodeBook = CODEBOOK_ELEMENTS.children();
	var codeBookSize = childrenCodeBook.size();
	CODEBOOK_PAGES = Math.ceil(codeBookSize / CODEBOOK_VAR_AMOUNT_PER_PAGE);
	CODEBOOK_ACT_PAGE = 1;
	codeBookGoTo(CODEBOOK_ACT_PAGE);
}

function codeBookGoTo(page) {
	var startAt = (page - 1) * CODEBOOK_VAR_AMOUNT_PER_PAGE, endOn = startAt + CODEBOOK_VAR_AMOUNT_PER_PAGE;
	setTimeout(function() {
		CODEBOOK_ELEMENTS.children().css('display', 'none').slice(startAt, endOn).css('display', 'block').slice(startAt,
		    endOn).css('display', 'block');
		setPagerElements();
	}, 0);
}

function matrixGoTo(page) {
	var startAt = (page - 1) * MATRIX_VAR_AMOUNT_PER_PAGE, endOn = startAt + MATRIX_VAR_AMOUNT_PER_PAGE;
	setTimeout(function() {
		console.log(MATRIX_ELEMENTS.children())
		MATRIX_ELEMENTS.children().css('display', 'none').slice(startAt, endOn).css('display', 'block').slice(startAt,
		    endOn).css('display', 'block');
		// setPagerElements();
	}, 0);
}

function is_int(value) {
	if ((parseFloat(value) == parseInt(value)) && !isNaN(value)) {
		return true;
	} else {
		return false;
	}
}
