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
		    backTotop();
		    $("#dwmainnavbar").on('affixed-top.bs.affix', function() {
			    $("#navbarlogo").fadeOut();
		    });
		    $("#dwmainnavbar").on('affix.bs.affix', function() {
			    $("#navbarlogo").fadeIn();
		    });
		    $.ajaxSetup({
			    headers : {
				    'X-CSRF-TOKEN' : $('input[name="_csrf"]').val()
			    }
		    });
		    $('[data-toggle="tooltip"]').tooltip()

		    // loading DMP Content
		    if (window.location.pathname.search("/dmp") > 0) {
			    $("#dmpForm :input").prop("disabled", false);
			    setProjectSubmenu(($("#pagePosi").val().trim() != "" ? $("#pagePosi").val() : null));
			    showorHideDMPContent();
			    $("#dmpForm").trackChanges();
			    $(window).on('beforeunload', function(e) {
				    if ($("#dmpForm").isChanged()) {
					    var msg = 'You are about to leave the page.  Continue?';
					    (e || window.event).returnValue = msg; // IE + Gecko
					    return msg; // Webkit
				    }
			    });
		    } // loading Project Content without study!
		    else if (window.location.pathname.search("/project") > 0 && window.location.pathname.search("/study") <= 0) {
			    // startTagging();
		    } // loading Study Content
		    else if (window.location.pathname.search("/study") > 0
		        && (window.location.pathname.search("/record") <= 0 || window.location.pathname.search("/records") > 0)) {
			    $("#studyFormDis :input").attr("disabled", $("#disStudyContent").val() === 'disabled');
			    $("#switchEditMode").attr("disabled", false);
			    scrollToPosition();
			    startAccordion();
			    startDatePicker();
			    setStudySubmenu(null);
			    showorHideStudyContent();
		    } // loading Record Content
		    else if (window.location.pathname.search("/record") > 0 && window.location.pathname.search("/records") <= 0) {
			    $("#studyFormDis :input:not([name=pageLoadMin],[name=pageLoadMax],[name=setNumofVars])").attr("disabled",
			        $("#disStudyContent").val() === 'disabled');
			    $("#spssSelected").show();
			    $("#csvSelected").hide();
			    $("#selectedFileType").change(function() {
				    if ($("#selectedFileType").val() === 'SPSS') {
					    $("#spssSelected").show();
					    $("#csvSelected").hide();
				    } else {
					    $("#spssSelected").hide();
					    $("#csvSelected").show();
				    }
			    });
		    }// loading Panel Content
		    else if (window.location.pathname.search("/panel") > 0) {
			    $("#projectfilter").keyup(function() {
				    filterProjectList($(this).val());
			    });
			    $("#filterSelect").change(function() {
				    filterProjectList($("#projectfilter").val());
			    });
		    } // loading access Content
		    else if (window.location.pathname.search("/access") > 0) {
			    showHideNewRole();
		    } // loading usersetings Content
		    else if (window.location.pathname.search("/usersettings") > 0) {
			    if ($('#passwd_error').val())
				    $('.user-pswd-button, #user-pswd-content').toggle();
			    $('#pwdcheckstr span').html(checkStrength($('.pwdcheckin').val()))
		    }
		    $(".loader").fadeOut("slow");
		    $(".uploader").fadeOut("slow");
	    });
})(window.jQuery, window, document);

$(".projectContentClick").click(function() {
	$("#pagePosi").val($(this).attr("id"));
	setProjectSubmenu($(this).attr("id"));
});

$(".studyContentClick").click(function() {
	$('#jQueryMap').val($(this).attr("id"));
	setStudySubmenu($(this).attr("id"));
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
		case "study":
			$("#studiesActiveClick").addClass("active");
			$("#studiesActiveContent").show();
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

function setStudySubmenu(id) {
	if (id != null) {
		if (!$("#" + id).hasClass("active")) {
			$(".studyContent").hide();
			$(".studyContentClick").removeClass("active");
			$("#" + id).addClass("active");
			$("#" + id.replace('Click', 'Content')).show();
		}
	} else {
		$(".studyContent").hide();
		$(".studyContentClick").removeClass("active");
		switch ($("#jQueryMap").val()) {
		case "designActiveClick":
			$("#designActiveClick").addClass("active");
			$("#designActiveContent").show();
			break;
		case "sampleActiveClick":
			$("#sampleActiveClick").addClass("active");
			$("#sampleActiveContent").show();
			break;
		case "surveyActiveClick":
			$("#surveyActiveClick").addClass("active");
			$("#surveyActiveContent").show();
			break;
		case "ethicalActiveClick":
			$("#ethicalActiveClick").addClass("active");
			$("#ethicalActiveContent").show();
			break;
		default:
			$("#basisDataActiveClick").addClass("active");
			$("#basisDataActiveContent").show();
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
	$('#accordion').on('hidden.bs.collapse', function(e) {
		toggleChevron(e);
	})
	$('#accordion').on('shown.bs.collapse', function(e) {
		toggleChevron(e);
	})
}

function startDatePicker() {
	$('.input-daterange').datepicker({
	  format : "dd/mm/yyyy",
	  weekStart : 1,
	  todayBtn : "linked",
	  clearBtn : true,
	  language : "de",
	  todayHighlight : true
	});
}

function toggleChevron(e) {
	$(e.target).prev('.panel-heading').find('.indicator').toggleClass(
	    'glyphicon glyphicon-plus glyphicon glyphicon-minus');
}

var uploaderror = false;
// Start DROPZONE for project-material upload!
Dropzone.options.myDropzone = {
  uploadMultiple : true,
  autoProcessQueue : false,
  autoDiscover : false,
  parallelUploads : 1,
  // maxFiles : 20,
  maxFilesize : 1536, // MB
  dictMaxFilesExceeded : $('#maxFiles').val(),
  dictResponseError : $('#responseError').val(),
  dictDefaultMessage : $('#defaultMsg').val(),
  headers : {
	  'X-CSRF-Token' : $('input[name="_csrf"]').val()
  },
  init : function() {
	  $("#dz-upload-button").prop('disabled', true);
	  // upload button click event
	  var myDropzone = this;
	  $('#dz-upload-button').on("click", function(e) {
		  uploaderror = false;
		  myDropzone.processQueue();
	  });
	  // reset button click event
	  $('#dz-reset-button').on("click", function(e) {
		  myDropzone.removeAllFiles(true);
	  });
	  // adds the delete button after a file is added
	  this.on("addedfile", function(file) {
		  $("#dz-upload-button").prop('disabled', false);
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
	  this.on("removedfile", function(file) {
		  $("#dz-upload-button").prop('disabled', false);
	  });
	  // shows a dialog after multiple upload finished
	  this.on("queuecomplete", function(file, res) {
		  console.log(uploaderror + ' - ' + file + ' - ' + res);
		  // calls controller after successful multiple saving to reload the page"
		  if (!uploaderror) {
			  setTimeout(function() {
				  $("form#my-dropzone").attr("enctype", "").attr("action", "multisaved").submit();
			  }, 500);
		  }
	  });
	  this.on("successmultiple", function(files, serverResponse) {
		  myDropzone.processQueue();
	  });
	  var totalpercent = 0;
	  this.on("totaluploadprogress", function(progress) {
		  if (progress > totalpercent)
			  totalpercent = progress;
		  $('#loadstatebar .progress-bar').css('width', totalpercent + '%').attr('aria-valuenow', totalpercent).html(
		      Math.round(totalpercent) + '%');
	  });
	  this.on("error", function(file, serverResponse) {
		  uploaderror = true;
		  if (serverResponse.indexOf("Exception") > 0) {
			  this.defaultOptions.error(file, 'An error occurred!');
		  }
		  $("#loadstate").fadeOut("slow");
	  });
	  this.on("processingmultiple", function(files, serverResponse) {
		  $("#loadstate").fadeIn("slow");
	  });
	  this.on("maxfilesexceeded", function(file) {
		  $("#dz-upload-button").prop('disabled', true);
		  this.removeFile(file);
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
	switchViewIfSelected('selectframeworkNationality', 'international');
	switchViewIfSelected('selectinvolvedInformed', 1);
	switchViewIfSelected('selectcontributionsDefined', 1);
	switchViewIfSelected('selectmanagementWorkflow', 1);
	switchViewIfSelected('selectstaffDescription', 1);
	switchViewIfSelected('selectdataProtection', 1);
	switchViewIfSelectedMulti('selectconsentObtained', '0,1');
	switchViewIfSelected('selectirbApproval', 1);
	switchViewIfSelected('selectsensitiveDataIncluded', 1);
	switchViewIfSelected('selectexternalCopyright', 1);
	switchViewIfSelected('selectinternalCopyright', 1);
	switchViewIfSelectedMulti('selectspecificCosts', 'reference,lifecycle,other');
}

function startloader() {
	setTimeout($(".loader").fadeIn("slow"), 0);
}

function showorHideStudyContent() {
	switchViewIfSelected('selectThirdParty', 1);
	switchViewIfSelected('selectCopyright', 1);
	switchViewIfSelected('selectPersDataPres', 'ANONYMOUS');
	switchViewIfSelected('selectPersDataColl', 1);
	switchViewIfSelected('selectConsent', 1);
	switchViewIfSelected('selectIrb', 1);
	switchViewIfSelected('selectResponsibility', 'OTHER');
	switchViewIfChecked('selectCollectionModesIP');
	switchViewIfChecked('selectCollectionModesINP');
	switchViewIfSelected('selectSampMethod', 'OTHER');
	switchViewIfChecked('selectUsedSourFormat');
	switchViewIfSelected('selectSourTrans', 'COMPLEX');
	switchViewIfSelected('selectObsUnit', 'OTHER');
	switchViewIfChecked('selectExperimentalIntervention');
	switchViewIfSelected('selectPrevWork', 'OTHER');
	switchViewIfSelected('selectCompleteSel', 'EXCERPT');
	switchViewIfChecked('selectSurveyIntervention');
	switchViewIfSelected('selectrepMeasures', 'MULTIPLE');
	for (var i = 0; i < $("#constructSize").val(); i++) {
		switchViewIfSelected('selectConstructType' + i, 'OTHER');
	}
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
	var selected = $("#" + name).is(':checked');
	name = name.replace('select', 'content');
	$("#" + name + "").toggle(selected);
	$("." + name + "").toggle(selected);
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

function showHideNewRole() {
	var val = $("#accessMailChange").val();
	if (val != 0) {
		$("#accessChange, #accessChangeSel").prop("disabled", false);
		var val2 = $("#accessChangeSel").val();
		if (val2 == 0 || val2 === "PROJECT_ADMIN" || val2 === "PROJECT_READER" || val2 === "PROJECT_WRITER") {
			$("#accessChange").val("0");
			$("#accessChange").prop("disabled", true);
		} else {
			$("#accessChange").prop("disabled", false);
		}
	} else {
		$("#accessChange, #accessChangeSel").prop("disabled", true);
		$("#accessChange, #accessChangeSel").val("0");
	}
}

$.fn.serializeObject = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name]) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};

// Abfangen ob offline, oder Server nicht erreichbar, falls ja speichern der daten in json datei für späteren import
$(function() {
	$('#dmpForm').on('submit', function(e) {
		var formData = $(this).serializeObject();
		e.preventDefault();
		$.ajax({
		  url : "checkConnection",
		  timeout : 10000,
		  error : function(jqXHR) {
			  if (jqXHR.status == 0) {
				  console.log(formData);
				  var a = document.createElement('a');
				  a.setAttribute('href', 'data:text/plain;charset=utf-u,' + encodeURIComponent(JSON.stringify(formData)));
				  a.setAttribute('download', 'test.json');
				  a.click();
			  }
		  },
		  success : function() {
			  $('#dmpForm').unbind('submit').submit();
		  }
		});
	});
});

$('.user-pswd-button').on("click", function(e) {
	$('.user-pswd-button, #user-pswd-content').toggle();
});

/*
 * start password check function
 */
$('.pwdcheckin').keyup(function() {
	$('#pwdcheckstr span').html(checkStrength($('.pwdcheckin').val()))
})

function checkStrength(password) {
	var str = 0;
	if (password.length <= 0) {
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-danger').width(0);
		$('.progress_custom span').css('color', '#000');
		return 'Passwort eingeben';
	}
	if (password.length >= 8) {
		str++;
		if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/))
			str++;
		if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)) {
			str++;
			if (password.match(/([^A-Za-z0-9])/)) {
				str++;
			}
			if (password.match(/(.*[^A-Za-z0-9].*[^A-Za-z0-9])/)) {
				str++;
				if (password.length >= 16)
					str++;
			}
		} else if (password.match(/([a-zA-Z])/) && password.match(/([^A-Za-z0-9])/)) {
			str++;
		}
	}
	switch (str) {
	case 0:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-danger').width(0);
		$('.progress_custom span').css('color', '#000');
		return 'Too short';
	case 1:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-danger').width("15%");
		$('.progress_custom span').css('color', '#000');
		return 'Extrem Weak';
	case 2:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-warning').width("30%");
		$('.progress_custom span').css('color', '#000');
		return 'Weak';
	case 3:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-warning').width("50%");
		$('.progress_custom span').css('color', '#000');
		return 'Good';
	case 4:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-success').width("70%");
		$('.progress_custom span').css('color', '#fff');
		return 'Strong';
	case 5:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-success').width("85%");
		$('.progress_custom span').css('color', '#fff');
		return 'Strong';
	case 6:
		$('#pwdcheckstr').removeClass().addClass('progress-bar progress-bar-success').width("100%");
		$('.progress_custom span').css('color', '#fff');
		return 'Strongest';
	default:
	}
}

/*
 * end password check function
 */

function setScrollPosition() {
	var position = 0;
	if (typeof window.pageYOffset != 'undefined') {
		position = window.pageYOffset;
	} else if (typeof document.documentElement.scrollTop != 'undefined' && document.documentElement.scrollTop > 0) {
		position = document.documentElement.scrollTop;
	} else if (typeof document.body.scrollTop != 'undefined') {
		position = document.body.scrollTop;
	}
	$("#scrollPosition").val(position);
}

function scrollToPosition() {
	var position = $("#scrollPosition").val();
	if (typeof position != 'undefined' && position > 0) {
		window.scrollTo(0, position);
		$("#scrollPosition").val(0);
	}
}

$(function() {
	$(".popup").click(
	    function(event) {
		    event.preventDefault();
		    var href = $(this).attr("href");
		    var width = $(this).attr("data-width");
		    var height = $(this).attr("data-height");
		    var popup = window.open(href, "popup",
		        "toolbar=no, location=no, directories=no, status=no, menubar=no, top=20, left=20, height=" + height
		            + ", width=" + width + "");
	    });
});

function shortFilename(spanName, filePath) {
	$('#' + spanName).html(filePath.replace(/^.*\\/, ''));
}

function delVarValues(position) {
	$('#values' + position + 'val').val("");
	$('#values' + position + 'label').val("");
	$('#values' + position).hide();
	return false;
}

function showAjaxModal(url) {
	asyncSumbit(url, false);
}

function showGlobalAjaxModal(url) {
	asyncSumbit(url, true);
}

function asyncSumbit(uri, global) {
	var str = $("#StudyForm").serialize();
	$.ajax({
	  type : "post",
	  data : str,
	  url : "asyncSubmit",
	  async : true,
	  dataType : "json",
	  success : function() {
		  loadAjaxModal(uri, global)
	  },
	  error : function(result) {
		  loadAjaxModal(null, true);
	  }
	});
}

function loadAjaxModal(url, global) {
	if (url != null) {
		// if (!global) {
		// $(".modal-dialog").removeClass("modalWidth")
		// } else {
		// $(".modal-dialog").addClass("modalWidth")
		// }
		jQuery('#valueModal').modal('show', {
			backdrop : 'static'
		});
		jQuery('#valueModal .modal-dialog').load(url);
	} else {
		jQuery('#errorModal').modal('show', {
			backdrop : 'static'
		});
	}
}

document.onkeypress = stopRKey;

function stopRKey(evt) {
	var evt = (evt) ? evt : ((event) ? event : null);
	var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
	if ((evt.which == 13) && (node.type == "text")) {
		return false;
	}
}

function backTotop() {

	// Der Button wird ausgeblendet
	$(".dwgoup").hide();

	// Funktion für das Scroll-Verhalten
	$(function() {
		$(window).scroll(function() {
			if ($(this).scrollTop() > 100) { // Wenn 100 Pixel gescrolled wurde
				$('.dwgoup').fadeIn();
			} else {
				$('.dwgoup').fadeOut();
			}
		});

		$('.dwgoup').click(function() { // Klick auf den Button
			$('body,html').animate({
				scrollTop : 0
			}, 800);
			return false;
		});
	});
}

$('.exportlist li :checkbox').on('click', function() {
	var $chk = $(this), $li = $chk.closest('li'), $ul, $parent;
	if ($chk.hasClass('li_chkbox')) {
		$li.siblings().find(':checkbox').not(this).prop('checked', this.checked);
	}
	$state = $chk.prop('checked');
	$ul = $li.closest('ul');
	if ($state == false) {
		if ($ul.hasClass('li_head_u3')) {
			$ul.find('.li_checkbox_a3').prop('checked', this.checked);
			$ul.parent().closest('ul').find('.li_checkbox_a2').prop('checked', this.checked);
			$ul.parent().closest('ul').parent().closest('ul').find('.li_checkbox_a1').prop('checked', this.checked);
		} else if ($ul.hasClass('li_head_u2')) {
			$ul.find('.li_checkbox_a2').prop('checked', this.checked);
			$ul.parent().closest('ul').find('.li_checkbox_a1').prop('checked', this.checked);
		} else if ($ul.hasClass('exportlist')) {
			$ul.find('.li_checkbox_a1').prop('checked', this.checked);
		}
	} else {
		if ($ul.hasClass('li_head_u3')) {
			setParentChkbox($chk, '.li_checkbox_a3', 'li_head_u3');
			setParentChkbox($ul.parent(), '.li_checkbox_a2', 'li_head_u2');
			setParentChkbox($ul.parent().closest('ul').parent(), '.li_checkbox_a1', 'li_head_u1');
		} else if ($ul.hasClass('li_head_u2')) {
			setParentChkbox($chk, '.li_checkbox_a2', 'li_head_u2');
			setParentChkbox($ul.parent(), '.li_checkbox_a1', 'exportlist');
		} else if ($ul.hasClass('li_head_u2')) {
			setParentChkbox($chk, '.li_checkbox_a1', 'exportlist');
		}
	}
});

function setParentChkbox($chk, $checkbox, $li) {
	$ul = $chk.closest('ul');
	if ($ul.hasClass($li)) {
		$chkboxes = $ul.find(":checkbox:not(:checked):not(" + $checkbox + ")");
		if ($chkboxes.length == 0) {
			$ul.find($checkbox).prop('checked', true);
		}
	}
}

function checkDeletePhrase(phrase) {
	if ($("#deleteInputTXT").val() == phrase)
		return true;
	else {
		$("#deleteInputTXT").val("");
		$("#deleteAlert").show();
		return false;
	}
	
}