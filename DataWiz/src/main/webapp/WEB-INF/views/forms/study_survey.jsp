<%@ include file="../templates/includes.jsp"%>
<c:set var="localeCode" value="${pageContext.response.locale}" />
<div id="surveyActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.responsibility -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.responsibility"><s:message code="study.responsibility" /></label>
      <sf:select path="study.completeSel" class="form-control">
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
      <!-- study.responsibilityOther -->
      <c:set var="input_vars" value="study.responsibilityOther;study.responsibilityOther; ; ;row; ; ;margin-bottom-0" />
      <%@ include file="../templates/gen_textarea.jsp"%>
      <s:message code="study.responsibility.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label" for="study.collStart"><s:message code="study.collTime" /></label>
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
      <label class="control-label" for="study.usedCollectionModes"><s:message code="study.usedCollectionModes" /></label>
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
            <c:set var="input_vars" value="study.otherCMIP;study.otherCMINP; ; ;row margin-bottom-0" />
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
      <label class="control-label " for="study.sampMethod"><s:message code="study.sampMethod" /></label>
      <sf:select path="study.completeSel" class="form-control">
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
      <!-- study.sampMethodOther -->
      <c:set var="input_vars" value="study.sampMethodOther;study.sampMethodOther; ; ;row; ; ;margin-bottom-0" />
      <%@ include file="../templates/gen_textarea.jsp"%>
      <s:message code="study.responsibility.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.sampMethodOther -->
  <c:set var="input_vars" value="study.recruiting;study.recruiting; ; ;row; ; ;" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sourFormat-->
  <div class="form-group">
    <div class="col-sm-12">
      <sf:label path="study.usedSourFormat">
        <s:message code="study.usedSourFormat" />
      </sf:label>
      <c:forEach items="${StudyForm.sourFormat}" var="dtype">
        <c:choose>
          <c:when test="${dtype.id == 41}">
            <label class="btn btn-default chkboxbtn"><sf:checkbox path="study.usedSourFormat"
                value="${dtype.id}" onchange="switchViewIfChecked('selectUsedSourFormat')"
                id="selectUsedSourFormat" /> <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" />
            </label>
          </c:when>
          <c:otherwise>
            <label class="btn btn-default chkboxbtn"><sf:checkbox path="study.usedSourFormat"
                value="${dtype.id}" /> <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
          </c:otherwise>
        </c:choose>
      </c:forEach>
      <!-- study.sourFormatOther -->
      <div id="contentUsedSourFormat">
        <c:set var="input_vars" value="study.otherCMIP;study.otherCMIP; ; ;row margin-bottom-0" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <s:message code="study.usedSourFormat.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
</div>