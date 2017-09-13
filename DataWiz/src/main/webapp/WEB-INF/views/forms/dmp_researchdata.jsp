<%@ include file="../templates/includes.jsp"%>
<c:set var="localeCode" value="${pageContext.response.locale}" />
<div id="researchActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.reserachdata.info" />
      </div>
    </div>
  </div>
  <!-- existingData -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.existingData"><s:message code="dmp.edit.existingData" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select path="dmp.existingData" class="form-control" id="selectExistingData"
        onchange="switchViewIfSelected('selectExistingData', 'existingUsed');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="existingUsed">
          <s:message code="dmp.edit.existingData.existingUsed" />
        </sf:option>
        <sf:option value="notFound">
          <s:message code="dmp.edit.existingData.notFound" />
        </sf:option>
        <sf:option value="noSearch">
          <s:message code="dmp.edit.existingData.noSearch" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.existingData.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
      <!-- START contentExistingData -->
      <div id="contentExistingData">
        <!-- dataCitation -->
        <c:set var="input_vars" value="dmp.dataCitation;dmp.edit.dataCitation; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
        <!-- existingDataRelevance -->
        <c:set var="input_vars" value="dmp.existingDataRelevance;dmp.edit.existingDataRelevance; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
        <!-- existingDataIntegration -->
        <c:set var="input_vars" value="dmp.existingDataIntegration;dmp.edit.existingDataIntegration; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </div>
  </div>
  <!-- usedDataTypes -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.usedDataTypes"><s:message code="dmp.edit.usedDataTypes" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <div class="form-group">
        <c:forEach items="${ProjectForm.dataTypes}" var="dtype" varStatus="state">
          <div class="col-sm-3">
            <c:choose>
              <c:when test="${dtype.id == 20}">
                <label class="btn btn-default chkboxbtn"> <sf:checkbox path="dmp.usedDataTypes" value="${dtype.id}"
                    onchange="switchViewIfChecked('selectOtherDataTypes');" id="selectOtherDataTypes" /> <s:message
                    text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" />
                </label>
              </c:when>
              <c:otherwise>
                <label class="btn btn-default chkboxbtn"> <sf:checkbox path="dmp.usedDataTypes" value="${dtype.id}" /> <s:message
                    text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" />
                </label>
              </c:otherwise>
            </c:choose>
          </div>
        </c:forEach>
        <div class="col-sm-12">
          <s:message code="dmp.edit.usedDataTypes.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
      <div id="contentOtherDataTypes">
        <!-- otherDataTypes -->
        <c:set var="input_vars" value="dmp.otherDataTypes;dmp.edit.otherDataTypes; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </div>
  </div>
  <!-- dataReproducibility -->
  <c:set var="input_vars" value="dmp.dataReproducibility;dmp.edit.dataReproducibility; ; ;row" />
  <c:set var="valimages" value="${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- usedCollectionModes -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <b><s:message code="dmp.edit.usedCollectionModes" /></b>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img src="/DataWiz/static/images/${valimag2}" class="infoImages" /><img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <ul class="list-group">
        <li class="list-group-item">
          <div class="form-group">
            <div class="col-sm-6">
              <label for="dmp.usedCollectionModes"> <s:message code="dmp.edit.usedCollectionModes.present" />
              </label>
              <c:forEach items="${ProjectForm.collectionModes}" var="dtype">
                <c:if test="${dtype.investPresent}">
                  <c:choose>
                    <c:when test="${dtype.id == 1}">
                      <label class="btn btn-default chkboxbtn"><sf:checkbox path="dmp.usedCollectionModes" value="${dtype.id}"
                          onchange="switchViewIfChecked('selectCollectionModesIP')" id="selectCollectionModesIP" /> <s:message
                          text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                    </c:when>
                    <c:otherwise>
                      <label class="btn btn-default chkboxbtn"><sf:checkbox path="dmp.usedCollectionModes" value="${dtype.id}" /> <s:message
                          text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                    </c:otherwise>
                  </c:choose>
                </c:if>
              </c:forEach>
              <!-- otherCMIP -->
              <div id="contentCollectionModesIP">
                <c:set var="input_vars" value="dmp.otherCMIP;dmp.edit.otherCMIP; ; ;row" />
                <%@ include file="../templates/gen_textarea.jsp"%>
              </div>
            </div>
            <div class="col-sm-6">
              <label for="dmp.usedCollectionModes"> <s:message code="dmp.edit.usedCollectionModes.not.present" />
              </label>
              <c:forEach items="${ProjectForm.collectionModes}" var="dtype">
                <c:if test="${not dtype.investPresent}">
                  <c:choose>
                    <c:when test="${dtype.id == 2}">
                      <label class="btn btn-default" style="width: 100%; text-align: left;"><sf:checkbox path="dmp.usedCollectionModes"
                          value="${dtype.id}" onchange="switchViewIfChecked('selectCollectionModesINP')" id="selectCollectionModesINP" /> <s:message
                          text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                    </c:when>
                    <c:otherwise>
                      <label class="btn btn-default" style="width: 100%; text-align: left;"><sf:checkbox path="dmp.usedCollectionModes"
                          value="${dtype.id}" /> <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                    </c:otherwise>
                  </c:choose>
                </c:if>
              </c:forEach>
              <!-- otherCMINP -->
              <div id="contentCollectionModesINP">
                <c:set var="input_vars" value="dmp.otherCMINP;dmp.edit.otherCMINP; ; ;row" />
                <%@ include file="../templates/gen_textarea.jsp"%>
              </div>
            </div>
            <div class="col-sm-12">
              <s:message code="dmp.edit.usedCollectionModes.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
  <!--  measOccasions -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.measOccasions"><s:message code="dmp.edit.measOccasions" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.measOccasions">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="single">
          <s:message code="dmp.edit.measOccasions.single" />
        </sf:option>
        <sf:option value="multi">
          <s:message code="dmp.edit.measOccasions.multi" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.measOccasions.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- START Data Reproducibility -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row margin-bottom-0">
        <div class="col-xs-10 col-sm-11">
          <b><s:message code="dmp.edit.qualityAssurance" /></b>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img src="/DataWiz/static/images/${valimag2}" class="infoImages" /><img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <ul class="list-group">
        <!--  reliabilityTraining -->
        <li class="list-group-item"><c:set var="input_vars" value="dmp.reliabilityTraining;dmp.edit.reliabilityTraining; ; ;row" /> <%@ include
            file="../templates/gen_textarea.jsp"%> <!--  multipleMeasurements --> <c:set var="input_vars"
            value="dmp.multipleMeasurements;dmp.edit.multipleMeasurements; ; ;row" /> <%@ include file="../templates/gen_textarea.jsp"%>
          <!--  qualitityOther --> <c:set var="input_vars" value="dmp.qualitityOther;dmp.edit.qualitityOther; ; ;row" /> <%@ include
            file="../templates/gen_textarea.jsp"%></li>
      </ul>
    </div>
  </div>
  <!--  fileFormat -->
  <c:set var="input_vars" value="dmp.fileFormat;dmp.edit.fileFormat; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- END Data Reproducibility -->
  <!-- START Reasons for storage -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row margin-bottom-0">
        <div class="col-xs-10 col-sm-11">
          <b><s:message code="dmp.edit.storage.headline" /></b>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img src="/DataWiz/static/images/${valimag2}" class="infoImages" /><img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <ul class="list-group">
        <li class="list-group-item marginTop0">
          <!-- workingCopy --> <s:message text="workingCopy" var="dmp_var_name" /> <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          <!-- goodScientific --> <s:message text="goodScientific" var="dmp_var_name" /> <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          <!-- subsequentUse --> <s:message text="subsequentUse" var="dmp_var_name" /> <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          <!-- requirements --> <s:message text="requirements" var="dmp_var_name" /> <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          <!-- documentation --> <s:message text="documentation" var="dmp_var_name" /> <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          <s:message code="dmp.edit.storage.help" var="appresmess" /> <%@ include file="../templates/helpblock.jsp"%>
        </li>
      </ul>
    </div>
  </div>
  <!-- storageDuration -->
  <c:set var="input_vars" value="dmp.storageDuration;dmp.edit.storageDuration; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- deleteProcedure -->
  <c:set var="input_vars" value="dmp.deleteProcedure;dmp.edit.deleteProcedure; ; ;row" />
  <c:set var="valimages" value="${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- dataSelection -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.dataSelection"><s:message code="dmp.edit.dataSelection" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img src="/DataWiz/static/images/${valimag2}" class="infoImages" /><img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" id="selectDataSelection" onchange="switchViewIfSelected('selectDataSelection', 1);" path="dmp.dataSelection">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.dataSelection.help" />
        </div>
      </div>
      <div id="contentDataSelection">
        <!-- selectionTime -->
        <c:set var="input_vars" value="dmp.selectionTime;dmp.edit.selectionTime; ; ;row" />
        <c:set var="valimages" value="${valimag2}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
        <!-- selectionResp -->
        <c:set var="input_vars" value="dmp.selectionResp;dmp.edit.selectionResp; ; ;row" />
        <c:set var="valimages" value="${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </div>
  </div>
</div>