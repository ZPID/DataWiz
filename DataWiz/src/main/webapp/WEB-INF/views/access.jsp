<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.access.headline" />
        </h4>
        <div>
          <s:message code="project.access.info" />
        </div>
      </div>
      <c:url var="accessUrl" value="/access/${ProjectForm.project.id}" />
      <sf:form action="${accessUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <c:forEach items="${ProjectForm.sharedUser}" var="user" varStatus="state">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="col-sm-3">
                <sf:label path="sharedUser[${state.index}].firstName">firstName</sf:label>
                <sf:input path="sharedUser[${state.index}].firstName" disabled="true"/>
              </div>
              <div class="col-sm-3">
                <sf:label path="sharedUser[${state.index}].lastName">lastName</sf:label>
                <sf:input path="sharedUser[${state.index}].lastName" disabled="true"/>
              </div>
              <div class="col-sm-3">
                <sf:label path="sharedUser[${state.index}].email">Email</sf:label>
                <sf:input path="sharedUser[${state.index}].email" disabled="true"/>
              </div>
              <div class="col-sm-3">
                <sf:label path="sharedUser[${state.index}].globalRoles[0].type">Role</sf:label>
                <sf:input path="sharedUser[${state.index}].globalRoles[0].type" disabled="true"/>
              </div>
            </div>
          </div>
        </c:forEach>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>
