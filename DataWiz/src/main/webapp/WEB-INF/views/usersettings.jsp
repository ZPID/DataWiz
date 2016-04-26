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
          <label class="control-label col-sm-3" for="firstName"><s:message code="gen.title" /></label>
          <div class="col-lg-2 col-md-3 col-sm-4 col-xs-12">
            <s:message code="gen.title.ph" var="reg_first" />
            <sf:input path="title" class="form-control" placeholder="${reg_first}" />
            <sf:errors path="title" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3" for="firstName"><s:message code="gen.first.name" /></label>
          <div class="col-sm-6">
            <s:message code="gen.first.name.ph" var="reg_first" />
            <sf:input path="firstName" class="form-control" placeholder="${reg_first}" />
            <sf:errors path="firstName" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3" for="lastName"><s:message code="gen.last.name" /></label>
          <div class="col-sm-6">
            <s:message code="gen.last.name.ph" var="reg_last" />
            <sf:input path="lastName" class="form-control" placeholder="${reg_last}" />
            <sf:errors path="lastName" cssClass="alert alert-danger" element="div" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3 required" for="email"><s:message code="gen.mail" /></label>
          <div class="col-sm-6">
            <s:message code="gen.mail.ph" var="reg_mail" />
            <sf:input path="email" class="form-control" placeholder="${reg_mail}" type="email" required="required" />
            <sf:errors path="email" cssClass="alert alert-danger" element="div" htmlEscape="false" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3" for="email"><s:message code="gen.mail" /></label>
          <div class="col-sm-6">
            <s:message code="gen.mail.ph" var="reg_mail" />
            <sf:input path="email" class="form-control" placeholder="${reg_mail}" type="email" required="required" />
            <sf:errors path="email" cssClass="alert alert-danger" element="div" htmlEscape="false" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3" for="password"><s:message code="gen.password" /></label>
          <div class="col-sm-6">
            <div class="form-group user-pswd-button">
              <div class="col-sm-8">
                <div class="btn btn-default btn-sm">‰ndern</div>
              </div>
            </div>
            <div id="user-pswd-content" style="display: none;">
              <div class="form-group">
                <label class="control-label col-sm-3 required" for="password">Aktuelles Passwort</label>
                <div class="col-sm-9">
                  <s:message code="gen.password.ph" var="reg_password" />
                  <sf:input path="password" class="form-control" placeholder="${reg_password}" type="password" />
                  <sf:errors path="password" cssClass="alert alert-danger" element="div" />
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-sm-3 required" for="password">Neues Passwort</label>
                <div class="col-sm-9">
                  <s:message code="gen.password.ph" var="reg_password" />
                  <sf:input path="password" class="form-control" placeholder="${reg_password}" type="password"
                    id="pwdcheckin" />
                  <sf:errors path="password" cssClass="alert alert-danger" element="div" />
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-sm-3" for="password_retyped">Passwort St‰rke</label>
                <div class="col-sm-9">                  
                  <div class="progress progress_custom">
                    <div class="progress-bar" role="progressbar" aria-valuenow="80"
                      aria-valuemin="0" aria-valuemax="100" style="width: 0%" id="pwdcheckstr">
                      <span>Passwort eingeben</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-sm-3 required" for="password_retyped">Passwort wiederholen</label>
                <div class="col-sm-9">
                  <s:message code="gen.password.retype.ph" var="reg_password" />
                  <sf:input path="password_retyped" class="form-control" placeholder="${reg_password}" type="password" />
                  <sf:errors path="password" cssClass="alert alert-danger" element="div" />
                </div>
              </div>
              <div class="form-group user-pswd-button" style="display: none;">
                <div class="col-sm-8">
                  <div class="btn btn-default btn-sm">schlieﬂen</div>
                </div>
              </div>
            </div>
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