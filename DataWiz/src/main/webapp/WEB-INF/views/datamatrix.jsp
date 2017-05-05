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
      <div id="copy"></div>
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
            <c:forEach items="${StudyForm.record.dataMatrix}" var="row">
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