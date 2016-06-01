<%@ include file="../templates/includes.jsp"%>
<div id="basisDataActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.title -->
  <c:set var="input_vars" value="study.title;study.title;required; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.title -->
  <c:set var="input_vars" value="study.internalID;study.internalID; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.title -->
  <c:set var="input_vars" value="study.transTitle;study.transTitle; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.contributors -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label" for=""><s:message code="study.contributors" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.contributors}" var="contri">
          <c:set value="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}" var="contriName" />
          <div class="input-group margin-bottom-10">
            <input type="text" class="form-control" value="${fn:trim(contriName)}" disabled="disabled" /><span
              class="input-group-btn">
              <button class="btn btn-danger" type="button">
                <s:message code="gen.delete" />
              </button>
            </span>
          </div>
        </c:forEach>
        <div class="input-group">
          <sf:select path="hiddenVar" class="form-control">
            <sf:option value="0">
              <s:message code="gen.select" />
            </sf:option>
            <c:forEach items="${StudyForm.projectContributors}" var="contri">
              <c:set value="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}" var="contriName" />
              <sf:option value="${contri.id}">${fn:trim(contriName)}</sf:option>
            </c:forEach>
          </sf:select>
          <span class="input-group-btn">
            <button class="btn btn-success" type="button">
              <s:message code="gen.add" />
            </button>
          </span>
        </div>
      </div>
      <s:message code="study.contributors.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.sAbstract -->
  <c:set var="input_vars" value="study.sAbstract;study.sAbstract; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sAbstractTrans -->
  <c:set var="input_vars" value="study.sAbstractTrans;study.sAbstractTrans; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.completeSel -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label" for="study.completeSel"><s:message code="study.completeSel" /></label>
      <sf:select path="study.completeSel" class="form-control">
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
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.excerpt -->
  <c:set var="input_vars" value="study.excerpt;study.excerpt; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.prevWork -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.prevWork"><s:message code="study.prevWork" /></label>
      <sf:select path="study.completeSel" class="form-control">
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
      <s:message code="study.prevWork.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.prevWorkStr -->
  <c:set var="input_vars" value="study.prevWorkStr;study.prevWorkStr; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.software -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.software"><s:message code="study.software" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.software}" varStatus="loop">
          <sf:textarea rows="1" path="study.software[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.software.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.pubOnData -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.pubOnData"><s:message code="study.pubOnData" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.pubOnData}" varStatus="loop">
          <sf:textarea rows="1" path="study.pubOnData[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.pubOnData.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.conflInterests -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.conflInterests"><s:message code="study.conflInterests" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.conflInterests}" varStatus="loop">
          <sf:textarea rows="1" path="study.conflInterests[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.conflInterests.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
</div>