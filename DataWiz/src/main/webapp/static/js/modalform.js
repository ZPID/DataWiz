//jsdoc c:\Users\ronny\git\DataWiz\DataWiz\src\main\webapp\static\js\modalform.js -t templates\minami READMEDW.md

/**
 * debugging variable
 */
var MODALDEBUG = true;
var datawizDateTimePattern = /^((\d{2}\.\d{2}\.\d{4}(\s\d{2}:\d{2}((:\d{2})?|:\d{2}(.\d{2})?))?)$)|(\d{2}:\d{2}((:\d{2})?|:\d{2}(.\d{2})?))$/;
var dateTimeRegex = /^(?=\d)(?:(?:31(?!.(?:0?[2469]|11))|(?:30|29)(?!.0?2)|29(?=.0?2.(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))(?:\x20|$))|(?:2[0-8]|1\d|0?[1-9]))([-./])(?:1[012]|0?[1-9])\1(?:1[6-9]|[2-9]\d)?\d\d(?:(?=\x20\d)\x20|$))?(((0?[1-9]|1[012])(:[0-5]\d){0,2}(\x20[AP]M))|([01]\d|2[0-3])(((:[0-5]\d){1,2})|((:[0-5]\d){2}.\d{2})))?$/g;
var quaterRegex = /^(1|2|3|4)\sQ\s(19|20)\d{2}$/;
var moyrRegex = /^(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\s(19|20)\d{2}$/;
var wkyrRegex = /^([1-9]|[1-4][0-9]|[5][0-3])\sWK\s(19|20)\d{2}$/;
var dtimeRegex = /^([1-9]{1}|[1-9]{1}\d{1}|[1-2]{1}\d{1,2}|[3]{1}[0-5]{1}\d{1}|[3]{1}[6]{1}[0-6]{1})\s\d{2}:\d{2}((:\d{2})?|:\d{2}(.\d{2})?)$/;
var monthRegex = /^(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER)$/;
var dayRegex = /^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$/;
/**
 * jQuery Listener for "propertychange" on input fields which belongs to class ".varNames"
 */
$('.varNames').bind("input propertychange", function(e) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering $('.varNames').bind(\"input propertychange\", function(e) ");
  }
  findNameDoublets();
  if (MODALDEBUG) {
    console.log("Leaving $('.varNames').bind(\"input propertychange\", function(e) ");
    console.groupEnd();
  }
});

/**
 * This function searches for name field doublets in the codebook form and returns true if doublets have been found
 * 
 * @returns true, if doublets are existing <br />
 *          false, if doublets haven't been found
 */
function findNameDoublets() {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering findNameDoublets()");
  }
  var falseCount = 0;
  $('.varNames').each(function(i, obj) {
    obj.classList.remove("redborder");
    $('.varNames').each(function(i, obj1) {
      if (obj.value == obj1.value && obj.id != obj1.id) {
        obj.classList.add("redborder");
        obj1.classList.add("redborder");
        falseCount += 1;
      }
    });
  });
  if (MODALDEBUG) {
    console.log("Leaving findNameDoublets with result: ", (falseCount > 0 ? true : false));
    console.groupEnd();
  }
  return (falseCount > 0 ? true : false);
}
/**
 * This function adds a "Value-Label" row to the value modal. It is called from codebookModalContent.jsp
 */
function addValueLabel() {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering addValueLabel()");
  }
  var wrapper = $(".valvar_wrap");
  var type = $("#type").val();
  var count = wrapper.children().length;
  var checkfunc = (type === 'SPSS_FMT_F' ? 'onkeyup = "checkNumberField(\'values' + (count) + 'val\')"'
      : type === 'SPSS_FMT_DATE' ? 'onkeyup = "checkDateField(\'values' + (count) + 'val\')"' : '');
  $(wrapper).append(
      '<div class="form-group" id="values' + (count) + '">' + '<div class="col-sm-12">' + '<div class="col-sm-3">'
          + '<input id="values' + (count) + 'val" class="form-control" type="text" name="values[' + (count)
          + '].value" ' + checkfunc + ' />' + '</div>' + '<div class="col-sm-1">' + '=' + '</div>'
          + '<div class="col-sm-6">' + '<input id="values' + (count)
          + 'label" class="form-control" type="text" name="values[' + (count) + '].label"  />' + '</div>'
          + '<div class="col-sm-2">' + '<button class="btn btn-danger" onclick="delVarValues(' + (count)
          + ');return false;"><span class="glyphicon glyphicon-remove" aria-hidden="false"></span></button>' + '</div>'
          + '</div>' + '</div>');
  if (MODALDEBUG) {
    console.log("Leaving addValueLabel");
    console.groupEnd();
  }
}

/**
 * This function adds a "Value-Label" row to the global value modal. It is called from codebookModalGlobalValues.jsp
 * 
 * @param string
 *          Application-Resource name for the string variable types (SPSS_FMT_A)
 * @param number
 *          Application-Resource name for the number variable types (SPSS_FMT_F)
 * @param date
 *          Application-Resource name for the number date types (SPSS_FMT_DATE)
 */
function addGlobalValueLabel(string, number, date) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering addGlobalValueLabel(", string, ", ", number, ", ", date, ")");
  }
  var wrapper = $(".valvar_wrap");
  var count = wrapper.children().length;
  $(wrapper).append(
      '<li class="list-group-item" id="values' + (count) + '">' + '<div class="row">' + '<div class="col-sm-3">'
          + '<select name="values[' + (count) + '].id" class="form-control" id="values' + (count)
          + 'id" onchange="checkType(' + (count) + ', null);">' + '<option value="1">' + string + '</option>'
          + '<option value="5">' + number + '</option></select>' + '</div>' + '<div class="col-sm-3">'
          + '<input id="values' + (count) + 'val" class="form-control" type="text" name="values[' + (count)
          + '].value" onkeyup="checkType(' + (count) + ', null);" onblur="checkType(' + (count) + ', null);" />'
          + '</div>' + '<div class="col-sm-1">' + '=' + '</div>' + '<div class="col-sm-3">' + '<input id="values'
          + (count) + 'label" class="form-control" type="text" name="values[' + (count) + '].label" />' + '</div>'
          + '<div class="col-sm-1">' + '<button class="btn btn-danger" onclick="delVarValues(' + (count)
          + ');return false;"><span class="glyphicon glyphicon-remove" aria-hidden="false"></span></button>' + '</div>'
          + '</div>' + '</li>');
  if (MODALDEBUG) {
    console.log("Leaving addGlobalValueLabel");
    console.groupEnd();
  }
}

/**
 * This function changes the amount of the input fields in the missing modal according to the selected missing type
 * 
 * @param i
 *          position
 */
function changeMissingFields(i) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering changeMissingFields(", i, ")");
  }
  if (i !== null) {
    var selected = $("#missingFormat_" + i + " option:selected").val();
    var missingVal1 = $("#missingVal1_" + i);
    var missingVal2 = $("#missingVal2_" + i);
    var missingVal3 = $("#missingVal3_" + i);
    var missingSep1 = $("#missingSep1_" + i);
    var missingSep2 = $("#missingSep2_" + i);
  } else {
    var selected = $("#missingFormat option:selected").val();
    var missingVal1 = $("#missingVal1")
    var missingVal2 = $("#missingVal2")
    var missingVal3 = $("#missingVal3")
    var missingSep1 = $("#missingSep1")
    var missingSep2 = $("#missingSep2")
  }
  if (MODALDEBUG) {
    console.log("selected = ", selected);
  }
  switch (selected) {
  case 'SPSS_NO_MISSVAL':
    missingVal1.hide().val('');
    missingVal2.hide().val('');
    missingVal3.hide().val('');
    missingSep1.text(',').hide();
    missingSep2.hide();
    break;
  case 'SPSS_ONE_MISSVAL':
    missingVal1.show();
    missingVal2.hide().val('');
    missingVal3.hide().val('');
    missingSep1.text(',').hide();
    missingSep2.hide();
    break;
  case 'SPSS_TWO_MISSVAL':
    missingVal1.show();
    missingVal2.show();
    missingVal3.hide().val('');
    missingSep1.text(',').show();
    missingSep2.hide();
    break;
  case 'SPSS_THREE_MISSVAL':
    missingVal1.show();
    missingVal2.show();
    missingVal3.show();
    missingSep1.text(',').show();
    missingSep2.show();
    break;
  case 'SPSS_MISS_RANGE':
    missingVal1.show();
    missingVal2.show();
    missingVal3.hide().val('');
    missingSep1.text('-').show();
    missingSep2.hide();
    break;
  case 'SPSS_MISS_RANGEANDVAL':
    missingVal1.show();
    missingVal2.show();
    missingVal3.show();
    missingSep1.text('-').show();
    missingSep2.show();
    break;
  }
  if (MODALDEBUG) {
    console.log("Leaving changeMissingFields");
    console.groupEnd();
  }
}

/**
 * This function returns checkValueForm(null)
 * 
 * @returns checkValueForm(null)
 */
function checkValueForm() {
  return checkValueForm(null);
}

/**
 * This function is called from codebookModalContent.jsp and validates all values of the single value-label and missing
 * fields
 * 
 * @param form
 *          String, which includes "missings" or "values", to start the correct validation process
 * @returns true, if validation is successful <br />
 *          false, if an error has occurred
 */
function checkValueMissingForm(form) {
  var ret = false;
  var type = $("#type").val().trim();
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkValueMissingForm(", form, ") type = ", type);
  }
  if (form === "values") {
    ret = checkValueForm((type === "SPSS_FMT_F" ? "5" : type === "SPSS_FMT_DATE" ? "20" : ""));
  } else if (form === "missings") {
    var success = 0;
    for (var i = 1; i <= 3; i++) {
      success += (checkMissingField('missingVal' + i) ? 0 : 1);
    }
    success += (checkMissingVal($("#missingFormat"), $("#missingVal1"), $("#missingVal2"), $("#missingVal3")) ? 0 : 1);
    ret = (success > 0 ? false : true);
  }
  if (MODALDEBUG) {
    console.log("Leaving checkValueMissingForm with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * This function checks if the input field value matches to the variable type
 * 
 * @param id
 *          HTML input field identifier
 * @returns true, if the value matches the variable type <br />
 *          false, if the field contains a restricted value
 */
function checkMissingField(id) {
  var ret = false;
  var type = $("#type").val().trim();
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkMissingField(", id, ") type = ", type);
  }
  $("#" + id).removeClass("redborder");
  if (type === "SPSS_FMT_F") {
    ret = checkNumberField(id)
  } else if (type === "SPSS_FMT_DATE") {
    ret = checkDateField(id)
  } else if (type === "SPSS_FMT_A") {
    ret = true;
  }
  if (MODALDEBUG) {
    console.log("Leaving checkMissingField with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * 
 * @returns
 */
function checkMissingForm() {
  var falseCount = 0;
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkMissingForm()");
  }
  for (var i = 0; i <= 2; i++) {
    $("#missingVal1_" + i).removeClass("redborder");
    $("#missingVal2_" + i).removeClass("redborder");
    $("#missingVal3_" + i).removeClass("redborder");
    if (i == 1) {
      falseCount += checkNumberField("missingVal1_" + i) ? 0 : 1;
      falseCount += checkNumberField("missingVal2_" + i) ? 0 : 1;
      falseCount += checkNumberField("missingVal3_" + i) ? 0 : 1;
    } else if (i == 2) {
      falseCount += checkDateField("missingVal1_" + i) ? 0 : 1;
      falseCount += checkDateField("missingVal2_" + i) ? 0 : 1;
      falseCount += checkDateField("missingVal3_" + i) ? 0 : 1;
    }
    falseCount += (checkMissingVal($("#missingFormat_" + i), $("#missingVal1_" + i), $("#missingVal2_" + i),
        $("#missingVal3_" + i)) ? 0 : 1);
  }
  if (MODALDEBUG) {
    console.log("Leaving checkMissingForm with result: ", falseCount);
    console.groupEnd();
  }
  return (falseCount > 0 ? false : true);
}

/**
 * This function selects the correct validation for the missing value by the type which has been selected by the user
 * 
 * @param missingType
 *          HTML type select box
 * @param missVal1
 *          HTML input field
 * @param missVal2
 *          HTML input field
 * @param missVal3
 *          HTML input field
 * @returns true, if no error has occurred <br />
 *          false, if a validation error has occurred
 */
function checkMissingVal(missingType, missVal1, missVal2, missVal3) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkMissingVal(", (missingType != null) ? missingType.val() : null, ", ",
        (missVal1 != null) ? missVal1.val() : null, ", ", (missVal2 != null) ? missVal2.val() : null, ", ",
        (missVal3 != null) ? missVal3.val() : null, ")");
  }
  var falseCount = 0;
  switch (missingType.val()) {
  case "SPSS_ONE_MISSVAL":
    falseCount += (hasFieldValue(missVal1) ? 0 : 1);
    break
  case "SPSS_TWO_MISSVAL":
    falseCount += (hasFieldValue(missVal1) ? 0 : 1);
    falseCount += (hasFieldValue(missVal2) ? 0 : 1);
    falseCount += (compareFieldValue(missVal1, missVal2, null, false) ? 0 : 1);
    break
  case "SPSS_THREE_MISSVAL":
    falseCount += (hasFieldValue(missVal1) ? 0 : 1);
    falseCount += (hasFieldValue(missVal2) ? 0 : 1);
    falseCount += (hasFieldValue(missVal3) ? 0 : 1);
    falseCount += (compareFieldValue(missVal1, missVal2, missVal3, false) ? 0 : 1);
    break;
  case "SPSS_MISS_RANGE":
    falseCount += (hasFieldValue(missVal1) ? 0 : 1);
    falseCount += (hasFieldValue(missVal2) ? 0 : 1);
    falseCount += (compareFieldValue(missVal1, missVal2, null, true) ? 0 : 1);
    break;
  case "SPSS_MISS_RANGEANDVAL":
    falseCount += (hasFieldValue(missVal1) ? 0 : 1);
    falseCount += (hasFieldValue(missVal2) ? 0 : 1);
    falseCount += (hasFieldValue(missVal3) ? 0 : 1);
    falseCount += (compareFieldValue(missVal1, missVal2, missVal3, true) ? 0 : 1);
    break;
  }
  if (MODALDEBUG) {
    console.log("Leaving checkMissingVal with result: ", (falseCount > 0 ? false : true));
    console.groupEnd();
  }
  return (falseCount > 0 ? false : true);
}

/**
 * This function checks if a value has been set into an input field.
 * 
 * @param value
 *          HTML input field
 * @returns true, if value is not empty or null <br />
 *          false, if field is empty
 */
function hasFieldValue(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("hasFieldValue(", value != null ? value.val() : null, ")");
  }
  var hasValue = true;
  if ((value.val() == null || value.val() == "")) {
    value.addClass("redborder");
    hasValue = false;
  }
  if (MODALDEBUG) {
    console.log("Leaving hasFieldValue with result: ", hasValue);
    console.groupEnd();
  }
  return hasValue;
}

/**
 * This function compares all values of the missing form input fields. It return false, if doublets have been found, or
 * the range has been set incorrect.
 * 
 * @param value1
 *          HTML input field
 * @param value2
 *          HTML input field
 * @param value3
 *          HTML input field
 * @param isRange
 *          true if range has been selected (with and without third value)
 * @returns true, if no validation error has occurred <br />
 *          false, if doublets have found, or the range has been set wrong
 * 
 */
function compareFieldValue(value1, value2, value3, isRange) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering compareFieldValue(", value1.val(), ", ", value2.val(), ", ", (value3 != null) ? value3.val()
        : null, ", ", isRange, ")");
  }
  var falseCount = 0;
  if (!isRange) {
    falseCount += (!equalValues(value1, value2) ? 0 : 1);
    if (value3 != null) {
      falseCount += (!equalValues(value1, value3) ? 0 : 1);
      falseCount += (!equalValues(value2, value3) ? 0 : 1);
    }
  } else {
    var isNumber1 = isNumber(value1.val());
    var isNumber2 = isNumber(value2.val());
    var isNumber3 = (value3 != null) ? isNumber(value3.val()) : false;
    if ((isNumber1 && isNumber2 && parseFloat(value1.val()) >= parseFloat(value2.val()))) {
      if (MODALDEBUG) {
        console.log("Incorrect Assignment: Range Value 1 >= Range Value 2 (float values)");
      }
      if (!value1.hasClass("redborder"))
        value1.addClass("redborder");
      if (!value2.hasClass("redborder"))
        value2.addClass("redborder");
      falseCount += 1;
    } else if ((!isNumber1 || !isNumber2) && value1.val() >= value2.val()) {
      if (MODALDEBUG) {
        console.log("Incorrect Assignment: Range Value 1 >= Range Value 2 (Date values)");
      }
      if (!value1.hasClass("redborder"))
        value1.addClass("redborder");
      if (!value2.hasClass("redborder"))
        value2.addClass("redborder");
      falseCount += 1;
    }
    if (value3 != null
        && (isNumber1 && isNumber2 && isNumber3)
        && (parseFloat(value1.val()) <= parseFloat(value3.val()) && parseFloat(value2.val()) >= parseFloat(value3.val()))) {
      if (MODALDEBUG) {
        console.log("Incorrect Assignment: Value 3 lies between value 1 and value 2 (float values)");
      }
      if (!value3.hasClass("redborder"))
        value3.addClass("redborder");
      falseCount += 1;
    } else if (value3 != null && (!isNumber1 || !isNumber2 || !isNumber3)
        && (value1.val() <= value2.val() && value2.val() >= value3.val())) {
      if (MODALDEBUG) {
        console.log("Incorrect Assignment: Value 3 lies between value 1 and value 2 (date values)");
      }
      if (!value3.hasClass("redborder"))
        value3.addClass("redborder");
      falseCount += 1;
    }
  }
  if (MODALDEBUG) {
    console.log("Leaving compareFieldValue with result: ", (falseCount > 0 ? false : true));
    console.groupEnd();
  }
  return (falseCount > 0 ? false : true);
}

/**
 * This function verifies, if two input fields have the same values.
 * 
 * @param value1
 *          HTML input field
 * @param value2
 *          HTML input field
 * @returns true, if the values are equal <br />
 *          false if the values are different
 */
function equalValues(value1, value2) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering equalValues(", value1.val(), ", ", value2.val(), ")");
  }
  var equal = false;
  if ((value1.val() == value2.val())
      || (isNumber(value1.val()) && isNumber(value2.val()) && parseFloat(value1.val()) == parseFloat(value2.val()))) {
    if (!value1.hasClass("redborder"))
      value1.addClass("redborder");
    if (!value2.hasClass("redborder"))
      value2.addClass("redborder");
    equal = true;
  }
  if (MODALDEBUG) {
    console.log("Leaving equalValues with result: ", equal);
    console.groupEnd();
  }
  return equal;
}

/**
 * This function checks the whole value-label input fields. It's called from codebookModalGlobalValues.jsp (onsumbit)
 * and checkValueMissingForm(form), which is called from codebookModalContent.jsp (onsubmit).
 * 
 * @param type
 *          Type of the Variable ("undefined" if it's called from codebookModalGlobalValues)
 * @returns true, if all fields in "valvar_wrap" are valid <br />
 *          false, if one field is not valid, to prevent the submit of the form
 */
function checkValueForm(type) {
  var count = $(".valvar_wrap").children().length;
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkValueForm(", type, ") count = ", count);
  }
  var success = true;
  for (var i = 0; i < count; i++) {
    var corrType = true;
    if (checkType(i, type) == false) {
      success = false;
      corrType = false;
    }
    if (checklabel(i, corrType) == false)
      success = false;
  }
  if (MODALDEBUG) {
    console.log("Leaving checkValueForm with result: ", success);
    console.groupEnd();
  }
  return success;
}

/**
 * This function checks if label and value is set. It is not allowed to set only one field. The "type" boolean can be
 * set true if it isn't useful to check the value field (for example if the type check already failed it can be set
 * false - see usage at checkValueForm(type))
 * 
 * @param pos
 *          Position of the value field
 * @param type
 *          boolean
 * @returns true if both fields are set, or if no field is set <br />
 *          false if value or label is missing
 */
function checklabel(pos, type) {
  var label = $("#values" + pos + "label").val().trim();
  var value = $("#values" + pos + "val").val().trim();
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checklabel(", pos, ", ", type, ") - value = ", value, "; label = ", label);
  }
  var ret = true;
  $("#values" + pos + "label").removeClass("redborder");
  if ((value != null && value != "") && (label == null || label == "")) {
    $("#values" + pos + "label").addClass("redborder");
    ret = false;
  } else if (type && (value == null || value == "") && (label != null && label != "")) {
    $("#values" + pos + "val").addClass("redborder");
    ret = false;
  }
  if (MODALDEBUG) {
    console.log("Leaving checklabel with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * This function checks if the values matches the selected variable type in the global and single value-label modal
 * 
 * @param pos
 *          Position of the value field
 * @param type
 *          Type of the Variable
 * @returns true if the value matches with the variable type <br />
 *          false if the value doesn't match
 */
function checkType(pos, type) {
  if (type == null)
    type = $("#values" + pos + "id").val();
  var value = $("#values" + pos + "val").val();
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkType(", pos, ", ", type, ") - value = ", value);
  }
  var ret = true;
  $("#values" + pos + "val").removeClass("redborder");
  if (value != null && value != "") {
    if (type == "5" && !isNumber(value)) {
      $("#values" + pos + "val").addClass("redborder");
      ret = false;
    } else if (type == "20" && !checkDateField("values" + pos + "val")) {
      $("#values" + pos + "val").addClass("redborder");
      ret = false;
    }
  }
  if (MODALDEBUG) {
    console.log("Leaving checkType with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * This function checks if a field value is an valid date. If the field doesn't contain a valid date the function adds a
 * "redcorner" class to the field
 * 
 * @param fieldId
 *          The HTML ID of the input field
 * @returns <br />
 *          true = if the field value is a valid date <br />
 *          false = if the field value is not a valid date
 */
function checkDateField(fieldId) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkDateField(", fieldId, ")");
  }
  var ret = true;
  var field = $("#" + fieldId)

  field.removeClass("redborder");
  var spssDate = $("#spssType").val().trim();
  console.log(spssDate);
  if (field.val() != null && field.val() != "") {
    var value = field.val().toLocaleUpperCase().trim();
    if (spssDate != undefined && spssDate == "SPSS_FMT_QYR") {
      if (!checkQuaterOfYear(value)) {
        field.addClass("redborder");
        ret = false;
      }
    } else if (spssDate != undefined && spssDate == "SPSS_FMT_MOYR") {
      if (!checkMonthOfYear(value)) {
        field.addClass("redborder");
        ret = false;
      }
    } else if (spssDate != undefined && spssDate == "SPSS_FMT_WKYR") {
      if (!checkWeekOfYear(value)) {
        field.addClass("redborder");
        ret = false;
      }
    } else if (spssDate != undefined && spssDate == "SPSS_FMT_DTIME") {
      if (!checkDTIME(value)) {
        field.addClass("redborder");
        ret = false;
      }
    } else if (spssDate != undefined && spssDate == "SPSS_FMT_MONTH") {
      if (!checkMonth(value)) {
        field.addClass("redborder");
        ret = false;
      }
    } else if (spssDate != undefined && spssDate == "SPSS_FMT_WKDAY") {
      if (!checkDay(value)) {
        field.addClass("redborder");
        ret = false;
      }
    } else if (!checkDate(field.val())) {
      field.addClass("redborder");
      ret = false;
    }
  }
  if (MODALDEBUG) {
    console.log("Leaving checkDateField with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * This function checks if a field value is an Number. If the field doesn't contain a number the function adds a
 * "redcorner" class to the field
 * 
 * @param fieldId
 *          The HTML ID of the input field
 * @returns <br />
 *          true = if the field value is a number <br />
 *          false = if the field value is not a number
 */
function checkNumberField(fieldId) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkNumberField(", fieldId, ")");
  }
  var ret = true;
  var field = $("#" + fieldId)
  field.removeClass("redborder");
  if (field.val() != null && field.val() != "" && !isNumber(field.val())) {
    field.addClass("redborder");
    ret = false;
  }
  if (MODALDEBUG) {
    console.log("Leaving checkNumberField with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * This function checks if the passed value is a Valid Date (see regex at the begin of this file)
 * 
 * @param value
 * @returns <br />
 *          true = if the value is a valid date <br />
 *          false = if the value is not a valid date
 */
function checkDate(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering isDate(", value, ")");
  }
  var ret = true;
  dateTimeRegex.lastIndex = 0;
  datawizDateTimePattern.lastIndex = 0;
  if (datawizDateTimePattern.exec(value) === null)
    ret = false;
  if (dateTimeRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving isDate with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

function checkQuaterOfYear(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkQuaterOfYear(", value, ")");
  }
  var ret = true;
  quaterRegex.lastIndex = 0;
  if (quaterRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving checkQuaterOfYear with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

function checkMonthOfYear(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkMonthOfYear(", value, ")");
  }
  var ret = true;
  moyrRegex.lastIndex = 0;
  if (moyrRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving checkMonthOfYear with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

function checkWeekOfYear(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkWeekOfYear(", value, ")");
  }
  var ret = true;
  wkyrRegex.lastIndex = 0;
  if (wkyrRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving checkWeekOfYear with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

function checkDTIME(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkDTIME(", value, ")");
  }
  var ret = true;
  dtimeRegex.lastIndex = 0;
  if (dtimeRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving checkDTIME with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

function checkMonth(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkMonth(", value, ")");
  }
  var ret = true;
  monthRegex.lastIndex = 0;
  if (monthRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving checkMonth with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

function checkDay(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering checkDay(", value, ")");
  }
  var ret = true;
  dayRegex.lastIndex = 0;
  if (dayRegex.exec(value) === null)
    ret = false;
  if (MODALDEBUG) {
    console.log("Leaving checkDay with result: ", ret);
    console.groupEnd();
  }
  return ret;
}

/**
 * This function checks if the passed value is a Number
 * 
 * @param value
 * @returns <br />
 *          true = if the value is a number <br />
 *          false = if the value is not a number
 */
function isNumber(value) {
  if (MODALDEBUG) {
    console.group();
    console.log("Entering isNumber(", value, ")");
  }
  var ret = !isNaN(parseFloat(value)) && isFinite(value);
  if (MODALDEBUG) {
    console.log("Leaving isNumber with result: ", ret);
    console.groupEnd();
  }
  return ret;
}