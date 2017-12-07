<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="login-container">
    <div class="login-card">
      <div class="login-form">
        <c:url var="loginUrl" value="/panel" />
        <sf:form action="${loginUrl}" modelAttribute="UserDTO" StyleClass="form-horizontal">
          <sf:input path="firstName" />
          <div class="has-error">
            <sf:errors path="firstName" class="help-inline" />
          </div>
          <sf:button>sdfsd</sf:button>
          <br />
          <sec:authorize access="hasRole('ADMIN')">          
          This content will only be visible to users who have
          the "supervisor" authority in their list of <tt>GrantedAuthority</tt>s.          
          </sec:authorize>
        </sf:form>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>