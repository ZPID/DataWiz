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
        <div class="browser_wrapper">
          <table class="table table-striped table-bordered table-condensed scrollTable" id="fixedHeaderTable">
            <thead>
              <tr>
                <c:forEach items="${StudyForm.record.variables}" var="var">
                  <th><s:message text="${var.name}" /></th>
                </c:forEach>
              </tr>
            </thead>
            <tbody class="scrollTableTbody">
              <c:forEach items="${StudyForm.record.dataMatrix}" var="row">
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