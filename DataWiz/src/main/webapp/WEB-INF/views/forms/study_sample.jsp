<%@ include file="../templates/includes.jsp"%>
<div id="sampleActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.software -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.eligibilities"><s:message code="study.eligibilities" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.eligibilities}" varStatus="loop">
          <sf:textarea rows="1" path="study.eligibilities[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.eligibilities.help" var="appresmess" />
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