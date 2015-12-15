<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="login_container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="login-card">
      <div class="login-form">
        <c:url var="loginUrl" value="/login" />
        <sf:form action="${loginUrl}" commandName="UserDTO" StyleClass="form-horizontal">
          <c:if test="${param.error != null}">
            <div class="alert alert-danger">
              <p>
                <c:out value="${error}" />
              </p>
            </div>
          </c:if>
          <c:choose>
            <c:when test="${param.logout != null}">
              <div class="alert alert-success">
                <p>
                  <s:message code="login.logout" />
                </p>
              </div>
            </c:when>
            <c:when test="${param.activated != null}">
              <div class="alert alert-success">
                <p>
                  <s:message code="login.activated.success" htmlEscape="false" />
                </p>
              </div>
            </c:when>
            <c:when test="${param.activationmail != null}">
              <div class="alert alert-success">
                <p>
                  <s:message code="login.activation.mail.send" htmlEscape="false" />
                </p>
              </div>
            </c:when>
          </c:choose>
          <div class="input-group input-sm">
            <sf:label cssClass="input-group-addon" path="email">
              <i class="fa fa-envelope"></i>
            </sf:label>
            <s:message code="login.enter.mail" var="place_user" />
            <sf:input cssClass="form-control" path="email" placeholder="${place_user}" required="required" />
          </div>
          <div class="input-group input-sm">
            <sf:label cssClass="input-group-addon" path="password">
              <i class="fa fa-lock"></i>
            </sf:label>
            <s:message code="login.enter.password" var="place_pwd" />
            <sf:password cssClass="form-control" path="password" placeholder="${place_pwd}" required="required" />
          </div>
          <div class="input-group input-sm">
            <div class="checkbox">
              <label><input type="checkbox" id="remember-me" name="remember-me"> <s:message code="login.remember.me" /></label>
            </div>
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