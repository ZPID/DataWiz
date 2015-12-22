$tag_box = null;
(function($, window, document, undefined) {
	$(document).ready(
	    function() {
		    setProjectSubmenu(null);
		    // ***** begin Tags *****
		    var my_custom_options = {
		      "no-duplicate" : true,
		      "no-duplicate-callback" : false,
		      "no-duplicate-text" : "Duplicate tags",
		      "tags-input-name" : "taggone",
		      "edit-on-delete" : false,
		      "no-comma" : false,
		    };
		    if ($("#tagging").length) {
			    var t = $("#tagging").tagging(my_custom_options);
			    $tag_box = t[0];
			    $tag_box.addClass("form-control");
			    // add current saved tags to taglist
			    $tag_box.tagging("add", $("#tags").val().split(','));
		    }
		    // ***** end Tags *****
		    // ***** begin accordion *****
		    $('[data-toggle="tooltip"]').tooltip();
		    $('#accordion').on('hidden.bs.collapse', function() {
			    // do something...
		    })
		    $('.accordion-toggle').click(
		        function(e) {
			        var chevState = $(e.target).siblings("row").siblings("i.indicator").toggleClass(
			            'glyphicon-chevron-down glyphicon-chevron-up');
			        $("i.indicator").not(chevState).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
		        });
		    // ***** end accordion *****
		    $(window).bind('scroll', function() {
			    var navHeight = 100; // custom nav height
			    ($(window).scrollTop() > navHeight) ? $('nav').addClass('goToTop') : $('nav').removeClass('goToTop');
		    });
		    $(this).scrollTop(0);

	    });
})(window.jQuery, window, document);

$(".projectContentClick").click(function() {
	setProjectSubmenu($(this).attr("id"));
});

function setDelPos(pos) {
	alert(pos);
}

$("#meta_submit").click(function() {
	$("#tags").val($tag_box.tagging("getTags"));
});

function setProjectSubmenu(id) {
	if (id != null) {
		if (!$("#" + id).hasClass("active")) {
			$(".projectContent").hide();
			$(".projectContentClick").removeClass("active");
			$("#" + id).addClass("active");
			$("#" + id.replace('Click', 'Content')).show();
		}
	} else {
		$(".projectContent").hide();
		$(".projectContentClick").removeClass("active");
		switch ($("#jQueryMap").val()) {
		case "contri":
			$("#workersActiveClick").addClass("active");
			$("#workersActiveContent").show();
			break;
		default:
			$("#metaActiveClick").addClass("active");
			$("#metaActiveContent").show();
			break;
		}
	}
}

function GetURLParameter(sParam) {
	var sURLVariables = window.location.search.substring(1).split('&');
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam) {
			return sParameterName[1];
		}
	}
}