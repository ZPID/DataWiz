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
      <sf:form action="${accessUrl}" commandName="UserDTO" class="form-horizontal">
        <%@ include file="templates/message.jsp"%>
        <!-- input title -->
        <c:set var="input_vars"
          value="title;gen.title;col-sm-3;col-lg-2 col-md-3 col-sm-4 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input first name -->
        <c:set var="input_vars"
          value="firstName;gen.firstName;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input last name -->
        <c:set var="input_vars"
          value="lastName;gen.lastName;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input orcid -->
        <c:set var="input_vars"
          value="orcid;gen.orcid;col-sm-3;col-lg-2 col-md-3 col-sm-4 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input email -->
        <c:set var="input_vars"
          value="email;gen.email;col-sm-3 required;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input secEmail -->
        <c:set var="input_vars"
          value="secEmail;gen.secEmail;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- change pwassword -->
        <div class="form-group">
          <label class="control-label col-sm-3" for="password"><s:message code="gen.password" /></label>
          <s:hasBindErrors name="UserDTO">
            <c:if test="${errors.hasFieldErrors('password') or errors.hasFieldErrors('password_old')}">
              <input type="hidden" value="true" id="passwd_error">
            </c:if>
          </s:hasBindErrors>
          <div class="col-sm-6">
            <div class="form-group">
              <div class="col-sm-12">
                <div class="btn btn-info btn-sm user-pswd-button" style="display: none;">
                  <s:message code="gen.close" />
                </div>
                <div class="btn btn-info btn-sm user-pswd-button">
                  <s:message code="gen.change" />
                </div>
              </div>
            </div>
            <div id="user-pswd-content" style="display: none;" class="panel panel-default">
              <div class="panel-body">
                <!-- input password_old -->
                <c:set var="input_vars"
                  value="password_old;gen.password.old;col-sm-4 required;col-sm-8 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin; ;password" />
                <%@ include file="templates/gen_input.jsp"%>
                <!-- input password -->
                <c:set var="input_vars"
                  value="password;gen.password;col-sm-4 required;col-sm-8 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin;pwdcheckin;password" />
                <%@ include file="templates/gen_input.jsp"%>
                <!-- password strength -->
                <div class="form-group">
                  <label class="control-label col-sm-4" for="password_retyped"><s:message
                      code="gen.password.strength" /></label>
                  <div class="col-sm-8">
                    <div class="progress progress_custom">
                      <div class="progress-bar" role="progressbar" aria-valuenow="80" aria-valuemin="0"
                        aria-valuemax="100" style="width: 0%" id="pwdcheckstr">
                        <span><s:message code="gen.password.ph" /></span>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- input password_retyped -->
                <c:set var="input_vars"
                  value="password_retyped;gen.password.retype;col-sm-4 required;col-sm-8 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin; ;password" />
                <%@ include file="templates/gen_input.jsp"%>
              </div>
            </div>
          </div>
        </div>
        <!-- input phone -->
        <c:set var="input_vars"
          value="phone;gen.phone;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input fax -->
        <c:set var="input_vars"
          value="fax;gen.fax;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input homepage -->
        <c:set var="input_vars"
          value="homepage;gen.homepage;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input institution -->
        <c:set var="input_vars"
          value="institution;gen.institution;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input department -->
        <c:set var="input_vars"
          value="department;gen.department;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input street -->
        <c:set var="input_vars"
          value="street;gen.street;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input zip -->
        <c:set var="input_vars"
          value="zip;gen.zip;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input city -->
        <c:set var="input_vars"
          value="city;gen.city;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input state -->
        <c:set var="input_vars"
          value="state;gen.state;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- input country -->
        <c:set var="input_vars"
          value="country;gen.country;col-sm-3;col-lg-6 col-md-6 col-sm-6 col-xs-12;col-sm-offset-3 col-sm-7 helpblock_margin" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- back and submit -->
        <div class="form-group">
          <div class="col-xs-6">
            <button type="reset" class="btn btn-default btn-sm">
              <s:message code="gen.reset" />
            </button>
          </div>
          <div class="col-xs-6 text-align-right">
            <button type="submit" class="btn btn-success btn-sm">
              <s:message code="gen.submit" />
            </button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>