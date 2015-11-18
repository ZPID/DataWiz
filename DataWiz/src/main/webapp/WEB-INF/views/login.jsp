<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="container">
    <div class="login-card">
      <div class="login-form">
        <c:url var="loginUrl" value="/login" />
        <sf:form action="${loginUrl}" commandName="UserDTO" StyleClass="form-horizontal">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <c:if test="${param.error != null}">
            <div class="alert alert-danger">
              <p>
                <c:out value="${error}" />
              </p>
            </div>
          </c:if>
          <c:if test="${param.logout != null}">
            <div class="alert alert-success">
              <p>
                <s:message code="login.logout" />
              </p>
            </div>
          </c:if>
          <div class="input-group input-sm">
            <sf:label cssClass="input-group-addon" path="email">
              <i class="fa fa-user"></i>
            </sf:label>
            <sf:input cssClass="form-control" path="email" placeholder="Enter Username" required="required" />
          </div>
          <div class="input-group input-sm">
            <sf:label cssClass="input-group-addon" path="password">
              <i class="fa fa-lock"></i>
            </sf:label>
            <sf:password cssClass="form-control" path="password" placeholder="Enter Password" required="required" />
          </div>
          <div class="form-actions">
            <sf:button type="submit" class="btn btn-block btn-primary btn-default">
              <s:message code="login.button" />
            </sf:button>
          </div>
        </sf:form>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>