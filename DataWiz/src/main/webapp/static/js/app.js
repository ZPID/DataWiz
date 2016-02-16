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
		    $(window).bind(
		        'scroll',
		        function() {
			        var navHeight = 100; // custom nav height
			        ($(window).scrollTop() > navHeight) ? $('.mainnavtop').addClass('goToTop') : $('.mainnavtop')
			            .removeClass('goToTop');
			        navHeight = 280;
			        ($(window).scrollTop() > navHeight && $(window).width() > 1000) ? $('.subnavtop').addClass('goToTop2')
			            .removeClass('') : $('.subnavtop').removeClass('goToTop2').addClass('');
		        });
		    $(this).scrollTop(0);
		    // loading DMP Content - not nessesary on other pages!
		    if (window.location.pathname.search("/dmp/") > 0) {
			    showorHideDMPContent();
			    $("#dmpForm").trackChanges();
			    $(window).on('beforeunload', function(e) {
				    if ($("#dmpForm").isChanged()) {
					    var msg = 'You are about to leave the page.  Continue?';
					    (e || window.event).returnValue = msg; // IE + Gecko
					    return msg; // Webkit
				    }
			    });
		    } // loading Project Content - not nessesary on other pages!
		    else if (window.location.pathname.search("/project/") > 0) {
			    startTagging();
		    } // loading Panel Content - not nessesary on other pages!
		    else if (window.location.pathname.search("/panel/") > 0) {
			    startAccordion();
		    }

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

function startTagging() {
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
}

function startAccordion() {
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

function showorHideDMPContent() {
	switchViewIfSelected("selectExistingData", 'existingUsed');
	switchViewIfChecked('selectOtherDataTypes');
	switchViewIfChecked('selectCollectionModesIP');
	switchViewIfChecked('selectCollectionModesINP');
	switchViewIfSelected("selectworkingCopy", 1);
	switchViewIfSelected("selectgoodScientific", 1);
	switchViewIfSelected("selectsubsequentUse", 1);
	switchViewIfSelected("selectrequirements", 1);
	switchViewIfSelected("selectdocumentation", 1);
	switchViewIfSelected("selectDataSelection", 1);
	switchViewIfSelectedMulti('selectPublStrategy', 'repository,author,nopubl');
	switchViewIfSelected('selectNoAccessReason', 'other');
	switchViewIfSelected('selectaccessCosts', 1);
	switchViewIfSelected('selectclarifiedRights', 1);
	switchViewIfSelected('selectUsedPID', 'other');
	switchViewIfSelected('selectstorageRequirements', 1);
	switchViewIfSelected('selectstorageSuccession', 1);
	switchViewIfSelected('selectframeworkNationality', 'international_specific');
	switchViewIfSelected('selectcontributionsDefined', 1);
	switchViewIfSelected('selectinvolvedInformed', 1);
	switchViewIfSelected('selectmanagementWorkflow', 1);
	switchViewIfSelected('selectstaffDescription', 1);
	switchViewIfSelected('selectdataProtection', 1);
	switchViewIfSelectedMulti('selectconsentObtained', '0,1');
	switchViewIfSelected('selectcontributionsDefined', 0);
	switchViewIfSelected('selectsensitiveDataIncluded', 1);
	switchViewIfSelected('selectexternalCopyright', 1);
	switchViewIfSelected('selectinternalCopyright', 1);
	switchViewIfSelectedMulti('selectspecificCosts', 'reference,lifecycle,other');
}

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
	console.log(name + " - " + show);
	var selected = $("#" + name).val();
	name = name.replace('select', 'content');
	// TODO alles auf klasse ändern!!!!
	$("#" + name + "").toggle(show == selected);
	$("." + name + "").toggle(show == selected);
}

/**
 * 
 * @param name
 * @param show
 */
function switchViewIfSelectedMulti(name, show) {
	var selected = $("#" + name).val().trim();
	name = name.replace('select', 'content');
	var showA = show.split(',');
	var hideContainer = true;
	for (var i = 0; i < showA.length; i++)
		$("." + name + i).toggle(false);
	for (var i = 0; i < showA.length; i++) {
		$("." + name + i).toggle(showA[i].trim() == selected);
		if (showA[i] == selected) {
			hideContainer = false;
			break;
		}
	}
	$("#" + name + "").toggle(!hideContainer);

}

/**
 * 
 * @param hideContainer@param
 *          show
 */
function switchViewIfChecked(name) {
	console.log(name);
	var selected = $("#" + name).is(':checked');
	name = name.replace('select', 'content');
	$("#" + name + "").toggle(selected);
}

function countChar(val) {
	var len = val.value.length;
	if (len >= 3000) {
		val.value = val.value.substring(0, 3000);
	} else {
		$('#charNum').text(3000 - len);
	}
};

function checkOnSubmit() {
	$("#dmpForm").data("changed", false);
}

$.fn.extend({
  trackChanges : function() {
	  $(":input", this).change(function() {
		  $(this.form).data("changed", true);
	  });
  },
  isChanged : function() {
	  return this.data("changed");
  }
});
