<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.studies.headline" />
        </h4>
        <div>
          <s:message code="project.studies.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:set var="pRole_g" value="panel-primary" />
      <c:if test="${principal.user.hasRole('PROJECT_READER', ProjectForm.project.id, false)}">
        <c:set var="pRole_g" value="panel-warning" />
      </c:if>
      <div class="panel-group">
        <c:forEach items="${ProjectForm.studies}" var="cstud">
          <c:set var="pRole" value="${pRole_g}" />
          <c:choose>
            <c:when test="${principal.user.hasRole('DS_WRITER', cstud.id, true)}">
              <c:set var="pRole" value="panel-primary" />
            </c:when>
          </c:choose>
          <div class="panel <c:out value="${pRole}"/>">
            <div class="panel-heading">
              <div class="row">
                <div class="col-sm-11">
                  <s:message text="${cstud.title}" />
                </div>
                <div class="col-sm-1">
                  <a href='<c:url value="study/${cstud.id}"/>' class="label label-success"><s:message
                      code="gen.edit" /></a>
                </div>
              </div>
            </div>
            <div class="panel-body">
              <a href="<c:url value="${ProjectForm.project.id}/study/${cstud.id}" />">${cstud.id} - </a>
            </div>
          </div>
          <br />
        </c:forEach>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>
