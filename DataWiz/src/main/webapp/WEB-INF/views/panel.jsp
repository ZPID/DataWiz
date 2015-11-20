<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <div class="login-card">
      <div class="login-form">
        <c:url var="projectUrl" value="/project" />
        <sf:form action="${projectUrl}" commandName="ProjectForm" StyleClass="form-horizontal">
          <c:forEach items="${ProjectForm.projects}" var="project">
            <div><c:out value="${project.name}"></c:out></div>
          </c:forEach>
        </sf:form>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>