<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="user.settings.headline" />
        </h4>
        <div>
          <s:message code="user.settings.info" />
        </div>
      </div>
      <c:url var="accessUrl" value="/usersettings" />
      <sf:form action="${accessUrl}" commandName="UserDTO" class="form-horizontal" role="form">
        <%@ include file="templates/message.jsp"%>
        <div class="form-group">
          <label class="control-label col-sm-2" for="firstName"><s:message code="gen.title" /></label>
          <div class="col-lg-2 col-md-3 col-sm-4 col-xs-12" >
            <s:message code="gen.title.ph" var="reg_first" />
            <sf:input path="title" class="form-control" placeholder="${reg_first}" />
            <sf:errors path="title" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2" for="firstName"><s:message code="gen.first.name" /></label>
          <div class="col-sm-8">
            <s:message code="gen.first.name.ph" var="reg_first" />
            <sf:input path="firstName" class="form-control" placeholder="${reg_first}" />
            <sf:errors path="firstName" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2" for="lastName"><s:message code="gen.last.name" /></label>
          <div class="col-sm-8">
            <s:message code="gen.last.name.ph" var="reg_last" />
            <sf:input path="lastName" class="form-control" placeholder="${reg_last}" />
            <sf:errors path="lastName" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2 required" for="email"><s:message code="gen.mail" /></label>
          <div class="col-sm-8">
            <s:message code="gen.mail.ph" var="reg_mail" />
            <sf:input path="email" class="form-control" placeholder="${reg_mail}" type="email" required="required" />
            <sf:errors path="email" cssClass="alert alert-danger" element="div" htmlEscape="false" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2 required" for="password"><s:message code="gen.password" /></label>
          <div class="col-sm-10">
            <s:message code="gen.password.ph" var="reg_password" />
            <sf:input path="password" class="form-control" placeholder="${reg_password}" type="password"
              required="required" />
            <sf:errors path="password" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <div class="control-label col-sm-2"></div>
          <div class="col-sm-10">
            <s:message code="gen.password.retype.ph" var="reg_password_retype" />
            <sf:input path="password_retyped" class="form-control" placeholder="${reg_password_retype}" type="password"
              required="required" />
            <sf:errors path="password" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <div class="control-label col-sm-2 required"></div>
          <div class="col-sm-10">
            <sf:checkbox path="checkedGTC" value="true" />
            <span><s:message code="reg.gtc.checkbox" /></span>
            <sf:errors path="checkedGTC" cssClass="alert alert-danger" element="div" />
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
<%@ include file="templates/footer.jsp"%>