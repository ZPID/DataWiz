<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="study.create.basis.headline" />
        </h4>
        <div>
          <s:message code="study.create.basis.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:if test="${not empty StudyForm.record.dataMatrix}">
        <table class="table table-striped table-bordered table-condensed scrollTable" id="fixedHeaderTable">
          <thead>
            <tr>
              <c:forEach items="${StudyForm.record.variables}" var="var">
                <th><s:message text="${var.name}" /></th>
              </c:forEach>
            </tr>
          </thead>
          <tbody class="scrollTableTbody">
            <c:forEach items="${StudyForm.record.dataMatrix}" var="row" varStatus="rowNum">
              <tr>
                <c:forEach items="${row}" var="value" varStatus="valloop">
                  <td><s:message text="${value}" /></td>
                </c:forEach>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:if>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>