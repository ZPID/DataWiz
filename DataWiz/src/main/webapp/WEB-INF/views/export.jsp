<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.export.headline"/>
        </h4>
        <div>
          <s:message code="project.export.info" />
        </div>
      </div>
      <c:url var="accessUrl" value="/export/${ProjectForm.project.id}" />
      <sf:form action="${accessUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>