<%@ include file="../templates/includes.jsp"%>
<c:set var="localeCode" value="${pageContext.response.locale}" />
<div id="surveyActiveContent" class="studyContent">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="study.surveydata.info" />
      </div>
    </div>
  </div>
  <!-- study.responsibility -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label " for="study.responsibility"><s:message code="study.responsibility" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select path="study.responsibility" class="form-control" id="selectResponsibility"
        onchange="switchViewIfSelected('selectResponsibility', 'OTHER');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="PRIMARY">
          <s:message code="study.responsibility.primary" />
        </sf:option>
        <sf:option value="OTHER">
          <s:message code="study.responsibility.other" />
        </sf:option>
      </sf:select>
      <div id="contentResponsibility">
        <!-- study.responsibilityOther -->
        <c:set var="input_vars" value="study.responsibilityOther;study.responsibilityOther; ; ;row; ; ;margin-bottom-0" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <s:message code="study.responsibility.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label" for="study.collStart"><s:message code="study.collTime" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <div class="row input-daterange" id="datepicker">
        <!-- study.collStart -->
        <div class="col-sm-6">
          <div class="input-group">
            <span class="input-group-addon"><s:message code="study.collStart" /></span>
            <sf:input class="input-sm form-control" path="study.collStart" />
          </div>
        </div>
        <!-- study.collEnd -->
        <div class="col-sm-6">
          <div class="input-group">
            <span class="input-group-addon"><s:message code="study.collEnd" /></span>
            <sf:input class="input-sm form-control" path="study.collEnd" />
          </div>
        </div>
      </div>
      <s:message code="study.collTime.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.collMode -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label" for="study.usedCollectionModes"><s:message
              code="study.usedCollectionModes" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <div class="col-sm-6">
          <sf:label path="study.usedCollectionModes">
            <s:message code="study.usedCollectionModes.present" />
          </sf:label>
          <c:forEach items="${StudyForm.collectionModes}" var="dtype">
            <c:if test="${dtype.investPresent}">
              <c:choose>
                <c:when test="${dtype.id == 1}">
                  <label class="btn btn-default chkboxbtn"><sf:checkbox path="study.usedCollectionModes"
                      value="${dtype.id}" onchange="switchViewIfChecked('selectCollectionModesIP')"
                      id="selectCollectionModesIP" /> <s:message
                      text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                </c:when>
                <c:otherwise>
                  <label class="btn btn-default chkboxbtn"><sf:checkbox path="study.usedCollectionModes"
                      value="${dtype.id}" /> <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" />
                  </label>
                </c:otherwise>
              </c:choose>
            </c:if>
          </c:forEach>
          <!-- study.otherCMIP -->
          <div id="contentCollectionModesIP">
            <c:set var="input_vars" value="study.otherCMIP;study.otherCMIP; ; ;row margin-bottom-0" />
            <%@ include file="../templates/gen_textarea.jsp"%>
          </div>
        </div>
        <div class="col-sm-6">
          <label for="dmp.usedCollectionModes"> <s:message code="study.usedCollectionModes.not.present" />
          </label>
          <c:forEach items="${StudyForm.collectionModes}" var="dtype">
            <c:if test="${not dtype.investPresent}">
              <c:choose>
                <c:when test="${dtype.id == 2}">
                  <label class="btn btn-default" style="width: 100%; text-align: left;"><sf:checkbox
                      path="study.usedCollectionModes" value="${dtype.id}"
                      onchange="switchViewIfChecked('selectCollectionModesINP')" id="selectCollectionModesINP" /> <s:message
                      text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                </c:when>
                <c:otherwise>
                  <label class="btn btn-default" style="width: 100%; text-align: left;"><sf:checkbox
                      path="study.usedCollectionModes" value="${dtype.id}" /> <s:message
                      text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                </c:otherwise>
              </c:choose>
            </c:if>
          </c:forEach>
          <!-- study.otherCMINP -->
          <div id="contentCollectionModesINP">
            <c:set var="input_vars" value="study.otherCMINP;study.otherCMINP; ; ;row margin-bottom-0" />
            <%@ include file="../templates/gen_textarea.jsp"%>
          </div>
        </div>
      </div>
      <s:message code="study.usedCollectionModes.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.sampMethod-->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label " for="study.sampMethod"><s:message code="study.sampMethod" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <sf:select path="study.sampMethod" class="form-control" id="selectSampMethod"
        onchange="switchViewIfSelected('selectSampMethod', 'OTHER');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="ACCRUING">
          <s:message code="study.sampMethod.accruing" />
        </sf:option>
        <sf:option value="CENSUS">
          <s:message code="study.sampMethod.census" />
        </sf:option>
        <sf:option value="RANDOM">
          <s:message code="study.sampMethod.random" />
        </sf:option>
        <sf:option value="CLUSTER">
          <s:message code="study.sampMethod.cluster" />
        </sf:option>
        <sf:option value="STRATIFIED">
          <s:message code="study.sampMethod.stratified" />
        </sf:option>
        <sf:option value="QUOTA">
          <s:message code="study.sampMethod.quota" />
        </sf:option>
        <sf:option value="OTHER">
          <s:message code="study.sampMethod.other" />
        </sf:option>
      </sf:select>
      <div id="contentSampMethod">
        <!-- study.sampMethodOther -->
        <c:set var="input_vars" value="study.sampMethodOther;study.sampMethodOther; ; ;row; ; ;margin-bottom-0" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <s:message code="study.responsibility.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.recruiting -->
  <c:set var="input_vars" value="study.recruiting;study.recruiting; ; ;row; ; ;" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sourFormat-->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <sf:label path="study.usedSourFormat"><s:message code="study.usedSourFormat" /></sf:label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <div class="panel panel-default panel-body">
        <c:forEach items="${StudyForm.sourFormat}" var="dtype">
          <c:choose>
            <c:when test="${dtype.id == 41}">
              <label class="btn btn-default chkboxbtn"><sf:checkbox path="study.usedSourFormat"
                  value="${dtype.id}" onchange="switchViewIfChecked('selectUsedSourFormat')" id="selectUsedSourFormat" />
                <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
            </c:when>
            <c:otherwise>
              <label class="btn btn-default chkboxbtn"><sf:checkbox path="study.usedSourFormat"
                  value="${dtype.id}" /> <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <!-- study.sourFormatOther -->
        <div id="contentUsedSourFormat">
          <c:set var="input_vars" value="study.otherSourFormat;study.otherSourFormat; ; ;row margin-bottom-0" />
          <%@ include file="../templates/gen_textarea.jsp"%>
        </div>
        <s:message code="study.usedSourFormat.help" var="appresmess" />
        <%@ include file="../templates/helpblock.jsp"%>
      </div>
    </div>
  </div>
  <!-- study.sourTrans -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label " for="study.sourTrans"><s:message code="study.sourTrans" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select path="study.sourTrans" class="form-control" id="selectSourTrans"
        onchange="switchViewIfSelected('selectSourTrans', 'COMPLEX');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="SIMPLE">
          <s:message code="study.sourTrans.simple" />
        </sf:option>
        <sf:option value="COMPLEX">
          <s:message code="study.sourTrans.complex" />
        </sf:option>
      </sf:select>
      <div id="contentSourTrans">
        <!-- study.otherSourTrans -->
        <c:set var="input_vars" value="study.otherSourTrans;study.otherSourTrans; ; ;row; ; ;margin-bottom-0" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <s:message code="study.sourTrans.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.specCirc -->
  <c:set var="input_vars" value="study.specCirc;study.specCirc; ; ;row; ; ;" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.transDescr -->
  <c:set var="input_vars" value="study.transDescr;study.transDescr; ; ;row; ; ;" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.qualInd-->
  <c:set var="input_vars" value="study.qualInd;study.qualInd; ; ;row; ; ;" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.qualLim -->
  <c:set var="input_vars" value="study.qualLim;study.qualLim; ; ;row; ; ;" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
</div>