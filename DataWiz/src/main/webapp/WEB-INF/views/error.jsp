<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="login_container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="login-card">
      <div class="login-form">
        <div class="alert alert-danger" role="alert">
          <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> <span class="sr-only">Error:</span>
          <c:out value="${errormsg}" escapeXml="false"></c:out>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>