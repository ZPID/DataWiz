<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="record.datamatrix.headline" />
        </h4>
        <div>
          <s:message code="record.datamatrix.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:if test="${not empty StudyForm.record.dataMatrix}">
        <c:url var="accessUrl"
          value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}/data" />
        <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" id="studyFormDis">
          <div class="well" style="padding: 0px; margin: 0px;">
            <div class="row col sm-12">
              <div class="col-sm-3 margin-top-7">
                <strong><s:message code="record.select.vars.info" /></strong>
              </div>
              <div class="col-sm-2">
                <div class="col-sm-2 text-align-right margin-top-7">
                  <strong><s:message code="record.select.vars.from" /></strong>
                </div>
                <div class="col-sm-10 text-align-left">
                  <sf:input class="form-control" path="pageLoadMin" type="number"
                    onkeyup="this.value=this.value.replace(/[^\d]/,'')" required="required" min="0" />
                </div>
              </div>
              <div class="col-sm-2">
                <div class="col-sm-2 text-align-right margin-top-7">
                  <strong><s:message code="record.select.vars.to" /></strong>
                </div>
                <div class="col-sm-10 text-align-left">
                  <sf:input class="form-control" path="pageLoadMax" type="number"
                    onkeyup="this.value=this.value.replace(/[^\d]/,'')" required="required" min="0"
                    max="${StudyForm.record.numberOfCases}" />
                </div>
              </div>
              <div class="col-sm-3">
                <div class="col-sm-8 text-align-right margin-top-7">
                  <strong><s:message code="record.select.vars.max" /></strong>
                </div>
                <div class="col-sm-4 text-align-left margin-top-7">
                  <s:message text="${StudyForm.record.numberOfCases}" />
                </div>
              </div>
              <div class="col-sm-2 text-align-right">
                <sf:button class="btn btn-success - btn-sm" name="setNumofVars">
                  <s:message code="record.select.vars.show" />
                </sf:button>
              </div>
            </div>
          </div>
        </sf:form>
        <div class="divTable">
          <div class="divTableHeading">
            <div class="divTableRow">
              <c:forEach items="${StudyForm.record.variables}" var="var">
                <div class="divTableCell">
                  <s:message text="${var.name}" />
                </div>
              </c:forEach>
            </div>
          </div>
          <div class="divTableBody">
            <c:forEach items="${StudyForm.record.dataMatrix}" var="row" begin="${StudyForm.pageLoadMin-1}"
              end="${StudyForm.pageLoadMax-1}">
              <div class="divTableRow">
                <c:forEach items="${row}" var="value">
                  <div class="divTableCell">
                    <s:message text="${value}" />
                  </div>
                </c:forEach>
              </div>
            </c:forEach>
          </div>
        </div>
      </c:if>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>