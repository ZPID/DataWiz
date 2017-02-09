<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:choose>
          <c:when test="${empty StudyForm.record.id}">
            <h4>
              <s:message code="study.create.basis.headline" />
            </h4>
            <div>
              <s:message code="study.create.basis.info" />
            </div>
          </c:when>
          <c:otherwise>
            <div class="row">
              <div class="col-sm-12">
                <h4>
                  <s:message code="study.edit.basis.headline" arguments="${StudyForm.record.recordName}" />
                </h4>
              </div>
              <%-- <div class="col-sm-3">
                <c:url var="accessUrl"
                  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/switchEditMode" />
                <c:choose>
                  <c:when test="${empty disStudyContent || disStudyContent eq 'disabled' }">
                    <a href="${accessUrl}" class="btn btn-success">Checkin</a>
                  </c:when>
                  <c:otherwise>
                    <a href="${accessUrl}" class="btn btn-danger">CheckOut</a>
                  </c:otherwise>
                </c:choose>
                <!-- Trigger the modal with a button -->
                <button type="button" class="btn btn-info" data-toggle="modal" data-target="#uploadModal">Upload
                  File</button>
                <button type="button" class="btn btn-warning" data-toggle="modal" data-target="#historyModal">History</button>
              </div> --%>
            </div>
            <div>
              <s:message code="study.edit.basis.info" />
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:if test="${not empty StudyForm.record.dataMatrix}">
        <div class="pre-xy-scrollable">
          <table class="table table-striped table-bordered table-condensed matrixtable">
            <thead>
              <tr>
                <c:forEach items="${StudyForm.record.variables}" var="var">
                  <th><s:message text="${var.name}" /></th>
                </c:forEach>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${StudyForm.record.dataMatrix}" var="row" varStatus="rowNum">
                <tr>
                  <c:forEach items="${row}" var="value">
                    <td><s:message text="${value}" /></td>
                  </c:forEach>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:if>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>