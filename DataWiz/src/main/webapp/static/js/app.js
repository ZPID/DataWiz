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
  $(document).ready(function() {
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
      setProjectSubmenu(null);
      startTagging();
    } // loading Study Content
    else if (window.location.pathname.search("/study") > 0) {
      startAccordion();
    } // loading Panel Content
    else if (window.location.pathname.search("/panel") > 0) {
      startAccordion();
    } // loading access Content
    else if (window.location.pathname.search("/access") > 0) {
      showHideNewRole();
    } // loading usersetings Content
    else if (window.location.pathname.search("/usersettings") > 0) {
      if ($('#passwd_error').val())
        $('.user-pswd-button, #user-pswd-content').toggle();
      $('#pwdcheckstr span').html(checkStrength($('.pwdcheckin').val()))
    }
  });
})(window.jQuery, window, document);

$(".projectContentClick").click(function() {
  $("#pagePosi").val($(this).attr("id"));
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

function toggleChevron(e) {
  $(e.target).prev('.panel-heading').find('.indicator').toggleClass('glyphicon glyphicon-plus glyphicon glyphicon-minus');
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
