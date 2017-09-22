<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="login-card content-padding">
      <div class="login-form">
        <c:url var="loginUrl" value="/register" />
        <sf:form action="${loginUrl}" commandName="UserDTO" class="form-horizontal" method="post">
          <div class="form-group">
            <label class="control-label col-sm-2" for="firstName"><s:message code="gen.firstName" /></label>
            <div class="col-sm-10">
              <s:message code="gen.firstName.ph" var="reg_first" />
              <sf:input path="firstName" class="form-control" placeholder="${reg_first}" />
              <sf:errors path="firstName" cssClass="alert alert-danger" element="div" htmlEscape="false" />
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2" for="lastName"><s:message code="gen.lastName" /></label>
            <div class="col-sm-10">
              <s:message code="gen.lastName.ph" var="reg_last" />
              <sf:input path="lastName" class="form-control" placeholder="${reg_last}" />
              <sf:errors path="lastName" cssClass="alert alert-danger" element="div" htmlEscape="false" />
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2 required" for="email"><s:message code="gen.email" /></label>
            <div class="col-sm-10">
              <s:message code="gen.email.ph" var="reg_mail" />
              <sf:input path="email" class="form-control" placeholder="${reg_mail}" type="email" required="required" />
              <sf:errors path="email" cssClass="alert alert-danger" element="div" htmlEscape="false" />
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2 required" for="password"><s:message code="gen.password" /></label>
            <div class="col-sm-10">
              <s:message code="gen.password.ph" var="reg_password" />
              <sf:input path="password" class="form-control" placeholder="${reg_password}" type="password" required="required" />
              <sf:errors path="password" cssClass="alert alert-danger" element="div" htmlEscape="false" />
            </div>
          </div>
          <div class="form-group">
            <div class="control-label col-sm-2"></div>
            <div class="col-sm-10">
              <s:message code="gen.password.retype.ph" var="reg_password_retype" />
              <sf:input path="password_retyped" class="form-control" placeholder="${reg_password_retype}" type="password" required="required" />
              <sf:errors path="password" cssClass="alert alert-danger" element="div" htmlEscape="false" />
            </div>
          </div>
          <div class="form-group">
            <div class="control-label col-sm-2 required"></div>
            <div class="col-sm-10">
              <sf:checkbox path="checkedGTC" value="true" />
              <c:url var="gtc_link" value="/static/html/terms.html" />
              <span><s:message code="reg.gtc.checkbox" arguments="${gtc_link}" /></span>
              <sf:errors path="checkedGTC" cssClass="alert alert-danger" element="div" htmlEscape="false" />
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <button type="reset" class="btn btn-default">
                <s:message code="gen.reset" />
              </button>
              <button type="submit" class="btn btn-success">
                <s:message code="gen.submit" />
              </button>
            </div>
          </div>
        </sf:form>
      </div>
    </div>
  </div>
</div>

<%@ include file="templates/footer.jsp"%>
