// the project tag_box
$tag_box = null;

/**
 * Onload function
 * 
 * @param $
 * @param window
 * @param document
 * @param undefined
 */
(function($, window, document, undefined) {
	$(document).ready(
	    function() {
		    // set the project submenu after reload or refresh
		    setProjectSubmenu(null);
		    /**
				 * ***** begin Tags *****
				 */
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
		    /**
				 * ***** end Tags *****
				 */
		    /**
				 * ***** begin accordion *****
				 */
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
		    /**
				 * ***** end accordion *****
				 */
		    $(window).bind('scroll', function() {
			    var navHeight = 100; // custom nav height
			    ($(window).scrollTop() > navHeight) ? $('nav').addClass('goToTop') : $('nav').removeClass('goToTop');
		    });
		    $(this).scrollTop(0);
		    // checkbox style		    
		    // DMP existing Data Content
		    switchViewIfSelected("selectExistingData", 1);
		    switchViewIfChecked('selectOtherDataTypes');
		    switchViewIfChecked('selectCollectionModesIP');
		    switchViewIfChecked('selectCollectionModesINP');
		    switchViewIfSelected("selectStorageWC", 1);
		    switchViewIfSelected("selectGoodScientific", 1);
		    switchViewIfSelected("selectSubsequentUse", 1);
		    switchViewIfSelected("selectDataRequirements", 1);
		    switchViewIfSelected("selectDataDocumentation", 1);
		    switchViewIfSelected("selectDataSelection", 1);
	    });
})(window.jQuery, window, document);

$(".projectContentClick").click(function() {
	setProjectSubmenu($(this).attr("id"));
});

/**
 * saves the tags to a hidden field before project form is submitted!
 */
$("#meta_submit").click(function() {
	$("#tags").val($tag_box.tagging("getTags"));
});

/**
 * Handles the project sub-menu and the content This is important, because the whole project page is loaded as single
 * page and is divided with jQuery into individual parts.
 * 
 * @param id
 */
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
		case "material":
			$("#materialsActiveClick").addClass("active");
			$("#materialsActiveContent").show();
			break;
		default:
			$("#metaActiveClick").addClass("active");
			$("#metaActiveContent").show();
			$("#administratriveActiveClick").addClass("active");
			$("#administratriveActiveContent").show();
			break;
		}
	}
}

/**
 * 
 * @param sParam
 * @returns
 */
function GetURLParameter(sParam) {
	var sURLVariables = window.location.search.substring(1).split('&');
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam) {
			return sParameterName[1];
		}
	}
}
// Start DROPZONE for project-material upload!
Dropzone.options.myDropzone = {
  uploadMultiple : true,
  autoProcessQueue : false,
  autoDiscover : false,
  parallelUploads : 20,
  // maxFiles : 2,
  maxFilesize : 256, // MB
  dictMaxFilesExceeded : $('#maxFiles').val(),
  dictResponseError : $('#responseError').val(),
  dictDefaultMessage : $('#defaultMsg').val(),
  headers : {
	  'X-CSRF-Token' : $('input[name="_csrf"]').val()
  },
  init : function() {
	  // upload button click event
	  var myDropzone = this;
	  $('#dz-upload-button').on("click", function(e) {
		  myDropzone.processQueue();
	  });
	  // reset button click event
	  $('#dz-reset-button').on("click", function(e) {
		  myDropzone.removeAllFiles(true);
	  });
	  // adds the delete button after a file is added
	  this.on("addedfile", function(file) {
		  var removeButton = Dropzone
		      .createElement("<button class='btn btn-block btn-danger btn-xs' style='margin-top: 5px;' >"
		          + $('#genDelete').val() + "</button>");
		  // Capture the Dropzone instance as closure.
		  var _this = this;
		  removeButton.addEventListener("click", function(e) {
			  e.preventDefault();
			  e.stopPropagation();
			  _this.removeFile(file);
		  });
		  // Add the button to the file preview element.
		  file.previewElement.appendChild(removeButton);
	  });
	  // shows a dialog after multiple upload finished
	  this.on("successmultiple", function(files, serverResponse) {
		  // calls controller after successful multiple saving to reload the page"
		  setTimeout(function() {
			  $("form#my-dropzone").attr("enctype", "").attr("action", "multisaved").submit();
		  }, 1000);
	  });
  }
};

/**
 * 
 * @param files
 * @param objectArray
 */
function showInformationDialog(files, objectArray) {
	var responseHead = objectArray["headline"];
	var responseContent = objectArray["content"];
	BootstrapDialog.show({
	  title : '<b>' + responseHead + '</b>',
	  message : responseContent
	});
}

// End DROPZONE for project-material upload!

/**
 * 
 * @param name
 * @param show
 */
function switchViewIfSelected(name, show) {
	console.log(name);
	var selected = $("#" + name).val();
	name = name.replace('select', 'content');
	$("#" + name + "").toggle(show == selected);
}

/**
 * 
 * @param name
 * @param show
 */
function switchViewIfChecked(name) {
	console.log(name);
	var selected = $("#" + name).is(':checked');
	name = name.replace('select', 'content');
	$("#" + name + "").toggle(selected);
}
