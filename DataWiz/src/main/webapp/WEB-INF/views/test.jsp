<%@ include file="templates/header.jsp"%>
<div id="mainWrapper">
  <div class="login-container">
    <div class="login-card">
      <div class="login-form">
        <c:url var="loginUrl" value="/dashboard" />
        <sf:form action="${loginUrl}" commandName="DataWizUser" StyleClass="form-horizontal">
          <sf:input path="firstName" />
          <div class="has-error">
            <sf:errors path="firstName" class="help-inline" />
          </div>
          <sf:button>sdfsd</sf:button>
        </sf:form>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>