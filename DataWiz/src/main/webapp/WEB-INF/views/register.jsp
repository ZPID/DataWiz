<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp" %>
    <div class="content-padding">
      <div class="page-header">
        <s:message code="register.headline" var="headline_head"/>
        <s:message code="register.headline.info" var="headline_info"/>
        <%@ include file="templates/pages_headline.jsp" %>
      </div>
      <c:url var="loginUrl" value="/register"/>
      <sf:form action="${loginUrl}" modelAttribute="UserDTO" class="form-horizontal" method="post">
        <div class="container-fluid" style="margin-top: 30px">
          <div class="col-lg-offset-3 col-lg-6 col-md-offset-1 col-md-10 col-sd-12 col-xs-12">
            <div class="form-group">
              <label for="firstName"><s:message code="gen.firstName"/></label>
              <div>
                <s:message code="gen.firstName.ph" var="reg_first"/>
                <sf:input path="firstName" class="form-control" placeholder="${reg_first}"/>
                <sf:errors path="firstName" cssClass="alert alert-danger" element="div" htmlEscape="false"/>
              </div>
            </div>
            <div class="form-group">
              <label for="lastName"><s:message code="gen.lastName"/></label>
              <div>
                <s:message code="gen.lastName.ph" var="reg_last"/>
                <sf:input path="lastName" class="form-control" placeholder="${reg_last}"/>
                <sf:errors path="lastName" cssClass="alert alert-danger" element="div" htmlEscape="false"/>
              </div>
            </div>
            <div class="form-group">
              <label class="required" for="email"><s:message code="gen.email"/></label>
              <div>
                <s:message code="gen.email.ph" var="reg_mail"/>
                <sf:input path="email" class="form-control" placeholder="${reg_mail}" type="email" required="required"/>
                <sf:errors path="email" cssClass="alert alert-danger" element="div" htmlEscape="false"/>
              </div>
            </div>
            <div class="form-group">
              <label class="required" for="password"><s:message code="gen.password"/></label>
              <div>
                <s:message code="gen.password.ph" var="reg_password"/>
                <sf:input path="password" class="form-control" placeholder="${reg_password}" type="password"
                          required="required"/>
                <sf:errors path="password" cssClass="alert alert-danger" element="div" htmlEscape="false"/>
              </div>
            </div>
            <div class="form-group">
              <div>
                <s:message code="gen.password.retype.ph" var="reg_password_retype"/>
                <sf:input path="password_retyped" class="form-control" placeholder="${reg_password_retype}"
                          type="password" required="required"/>
                <sf:errors path="password" cssClass="alert alert-danger" element="div" htmlEscape="false"/>
              </div>
            </div>
            <div class="form-group">
              <div class="required">
                <sf:checkbox path="checkedGTC" value="true"/>
                <c:url var="gtc_link" value="/static/html/terms.html"/>
                <span><s:message code="reg.gtc.checkbox" arguments="${gtc_link}"/></span>
                <sf:errors path="checkedGTC" cssClass="alert alert-danger" element="div" htmlEscape="false"/>
              </div>
            </div>
            <c:if test="${captcha_enabled}">
              <div class="form-group">
                <div class="g-recaptcha" data-sitekey="${captcha_site}"></div>
                <c:if test="${not empty captcha_err}">
                  <div class="alert alert-danger">
                    <s:message text="${captcha_err}"/>
                  </div>
                </c:if>
              </div>
            </c:if>
            <div class="form-group text-align-right">
              <button type="submit" class="btn btn-info btn-sm">
                <s:message code="gen.submit"/>
              </button>
            </div>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>

<%@ include file="templates/footer.jsp" %>
