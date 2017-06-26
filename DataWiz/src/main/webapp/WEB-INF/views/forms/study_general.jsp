<%@ include file="../templates/includes.jsp"%>
<div id="basisDataActiveContent" class="studyContent">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="study.generaldata.info" />
      </div>
    </div>
  </div>
  <!-- study.title -->
  <c:set var="input_vars" value="study.title;study.title;required; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.internalID -->
  <c:set var="input_vars" value="study.internalID;study.internalID; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.transTitle -->
  <c:set var="input_vars" value="study.transTitle;study.transTitle; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.contributors -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label" for=""><s:message code="study.contributors" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.contributors}" var="contri" varStatus="coloop">
          <c:set value="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}" var="contriName" />
          <div class="input-group margin-bottom-10">
            <span class="form-control"><s:message text="${fn:trim(contriName)}" /></span><span class="input-group-btn">
              <sf:button class="btn btn-danger" name="deleteContri"
                onclick="document.getElementById('delPos').value=${coloop.count-1}; setScrollPosition();">
                <s:message code="gen.delete" />
              </sf:button>
            </span>
          </div>
        </c:forEach>
        <c:choose>
          <c:when test="${not empty StudyForm.projectContributors && fn:length(StudyForm.projectContributors) gt 0}">
            <div class="input-group">
              <sf:select path="hiddenVar" class="form-control">
                <sf:option value="-1">
                  <s:message code="gen.select" />
                </sf:option>
                <c:forEach items="${StudyForm.projectContributors}" var="contri" varStatus="coloop">
                  <c:set value="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}" var="contriName" />
                  <sf:option value="${coloop.index}">
                    <s:message text="${fn:trim(contriName)}" />
                  </sf:option>
                </c:forEach>
              </sf:select>
              <span class="input-group-btn"> <sf:button class="btn btn-success" name="addContri"
                  onclick="setScrollPosition();">
                  <s:message code="gen.add" />
                </sf:button>
              </span>
            </div>
          </c:when>
          <c:otherwise>
            <s:message code="study.contributors.no.project.contributors" />
          </c:otherwise>
        </c:choose>
      </div>
      <s:message code="study.contributors.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.sAbstract -->
  <c:set var="input_vars" value="study.sAbstract;study.sAbstract; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sAbstractTrans -->
  <c:set var="input_vars" value="study.sAbstractTrans;study.sAbstractTrans; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.completeSel -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label" for="study.completeSel"><s:message code="study.completeSel" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <sf:select path="study.completeSel" class="form-control" id="selectCompleteSel"
        onchange="switchViewIfSelected('selectCompleteSel','EXCERPT');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="COMPLETE">
          <s:message code="study.completeSel.complete" />
        </sf:option>
        <sf:option value="EXCERPT">
          <s:message code="study.completeSel.excerpt" />
        </sf:option>
      </sf:select>
      <s:message code="study.completeSel.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.excerpt -->
  <div id="contentCompleteSel">
    <c:set var="valimages" value="${valimag1}" />
    <c:set var="input_vars" value="study.excerpt;study.excerpt; ; ;row" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- study.prevWork -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.prevWork"><s:message code="study.prevWork" /></label>
      <sf:select path="study.prevWork" class="form-control" id="selectPrevWork"
        onchange="switchViewIfSelected('selectPrevWork', 'OTHER');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="REPLICATION">
          <s:message code="study.prevWork.replication" />
        </sf:option>
        <sf:option value="FOLLOWUP">
          <s:message code="study.prevWork.followup" />
        </sf:option>
        <sf:option value="OTHER">
          <s:message code="study.prevWork.other" />
        </sf:option>
        <sf:option value="NORELATION">
          <s:message code="study.prevWork.norelation" />
        </sf:option>
      </sf:select>
      <div id="contentPrevWork">
        <!-- study.prevWorkStr -->
        <c:set var="input_vars" value="study.prevWorkStr;study.prevWorkStr; ; ;row" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <s:message code="study.prevWork.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.software -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.software"><s:message code="study.software" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.software}" varStatus="loop">
          <s:bind path="study.software[${loop.index}].text">
            <c:choose>
              <c:when test="${status.error}">
                <sf:textarea rows="1" path="study.software[${loop.index}].text"
                  class="form-control margin-bottom-10 redborder" title="${status.errorMessage}" data-toggle="tooltip" />
              </c:when>
              <c:otherwise>
                <sf:textarea rows="1" path="study.software[${loop.index}].text" class="form-control margin-bottom-10" />
              </c:otherwise>
            </c:choose>
          </s:bind>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addSoftware" onclick="setScrollPosition();">
              <s:message code="gen.add" />
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.software.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.pubOnData -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label " for="study.pubOnData"><s:message code="study.pubOnData" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.pubOnData}" varStatus="loop">
          <s:bind path="study.pubOnData[${loop.index}].text">
            <c:choose>
              <c:when test="${status.error}">
                <sf:textarea rows="1" path="study.pubOnData[${loop.index}].text"
                  class="form-control margin-bottom-10 redborder" title="${status.errorMessage}" data-toggle="tooltip" />
              </c:when>
              <c:otherwise>
                <sf:textarea rows="1" path="study.pubOnData[${loop.index}].text" class="form-control margin-bottom-10" />
              </c:otherwise>
            </c:choose>
          </s:bind>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addPubOnData" onclick="setScrollPosition();">
              <s:message code="gen.add" />
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.pubOnData.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.conflInterests -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.conflInterests"><s:message code="study.conflInterests" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.conflInterests}" varStatus="loop">
          <s:bind path="study.conflInterests[${loop.index}].text">
            <c:choose>
              <c:when test="${status.error}">
                <sf:textarea rows="1" path="study.conflInterests[${loop.index}].text"
                  class="form-control margin-bottom-10 redborder" title="${status.errorMessage}" data-toggle="tooltip" />
              </c:when>
              <c:otherwise>
                <sf:textarea rows="1" path="study.conflInterests[${loop.index}].text"
                  class="form-control margin-bottom-10" />
              </c:otherwise>
            </c:choose>
          </s:bind>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addConflInterests" onclick="setScrollPosition();">
              <s:message code="gen.add" />
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.conflInterests.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
</div>