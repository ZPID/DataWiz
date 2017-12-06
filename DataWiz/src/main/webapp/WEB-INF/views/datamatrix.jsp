<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <c:choose>
        <c:when test="${empty StudyForm or empty StudyForm.record or empty StudyForm.record.dataMatrix}">
          <div class="page-header">
            <div class="row">
              <div class="col-sm-12">
                <h4>
                  <s:message code="record.codebook.no.record.headline" />
                </h4>
              </div>
            </div>
            <div>
              <s:message code="record.codebook.no.record.info" />
            </div>
          </div>
        </c:when>
        <c:otherwise>
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
          <c:url var="accessUrl"
            value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}/data" />
          <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" id="studyFormDis">
            <table class="display table table-striped table-bordered table table-condensed matrixtable margin-bottom-0" id="lazyLoadFinalMatrix">
              <thead>
                <tr>                 
                  <c:forEach items="${StudyForm.record.variables}" var="var">
                    <th><s:message text="${var.name}" /></th>
                  </c:forEach>
                </tr>
              </thead>
            </table>
          </sf:form>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>