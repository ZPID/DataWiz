<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <s:message code="login.headline" var="headline_head" />
        <s:message code="login.headline.info" var="headline_info" />
        <%@ include file="templates/pages_headline.jsp"%>
      </div>
      <div class="row" style="margin-top: 30px">
        <div
          class="col-lg-offset-4 col-lg-4 col-md-offset-3 col-md-6 col-sd-offset-2 col-sd-8 col-xs-offset-1 col-xs-10">
          <c:url var="loginUrl" value="/login" />
          <sf:form action="${loginUrl}" modelAttribute="UserDTO" StyleClass="form-horizontal">
            <c:if test="${isBetaVersion}">
              <div class="alert alert-danger">
                <strong><s:message code="datawiz.is.beta.info" /> </strong>
              </div>
            </c:if>
            <%@ include file="templates/message.jsp"%>
            <c:if test="${param.error != null}">
              <div class="alert alert-danger">
                <s:message text="${error}" />
              </div>
              <div class="alert alert-info">
                <s:message code="login.password.reset" arguments="${loginUrl}/passwordrequest" />
              </div>
            </c:if>
            <c:choose>
              <c:when test="${param.logout != null}">
                <div class="alert alert-success">
                  <s:message code="login.logout" />
                </div>
              </c:when>
              <c:when test="${param.activated != null}">
                <div class="alert alert-success">
                  <s:message code="login.activated.success" />
                </div>
              </c:when>
              <c:when test="${param.activationmail != null}">
                <div class="alert alert-success">
                  <s:message code="login.activation.mail.send" />
                </div>
              </c:when>
            </c:choose>
            <div class="row form-group">
              <div class="input-group">
                <span class="input-group-addon info"><span class="glyphicon glyphicon-envelope"></span></span>
                <s:message code="gen.email.ph" var="place_user" />
                <sf:input cssClass="form-control" path="email" placeholder="${place_user}" required="required" />
              </div>
            </div>
            <div class="row form-group">
              <div class="input-group">
                <span class="input-group-addon info"><span class="glyphicon glyphicon-lock"></span></span>
                <s:message code="gen.password.ph" var="place_pwd" />
                <sf:password cssClass="form-control" path="password" placeholder="${place_pwd}" required="required" />
              </div>
            </div>
            <div class="row form-group">
              <div class="input-group">
                <div class="checkbox">
                  <label><input type="checkbox" id="remember-me" name="remember-me"> <s:message
                      code="login.remember.me" /></label>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-lg-12 col-md-12 col-sd-12 col-xs-12">
                <sf:button type="submit" class="btn btn-block btn-info btn-default">
                  <s:message code="login.button" />
                </sf:button>
              </div>
            </div>
          </sf:form>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>