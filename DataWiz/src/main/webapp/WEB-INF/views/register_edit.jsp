<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <ol class="breadcrumb">
      <c:url var="homeUrl" value="/" />
      <li><a href="${homeUrl}">Home</a></li>
      <li class="active">Register</li>
    </ol>
    <div class="login-card">
      <div class="login-form">
        <c:url var="loginUrl" value="/register" />
        <sf:form action="${loginUrl}" commandName="UserDTO" class="form-horizontal" method="post">
          <div class="form-group">
            <label class="control-label col-sm-2" for="firstName">First Name:</label>
            <div class="col-sm-10">
              <sf:input path="firstName" class="form-control" placeholder="Enter First Name" />
              <span class="help-block"><sf:errors path="firstName" class="help-inline" /></span>
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2" for="lastName">Last Name:</label>
            <div class="col-sm-10">
              <sf:input path="lastName" class="form-control" placeholder="Enter Last Name" />
              <span class="help-block"><sf:errors path="lastName" class="help-inline" /></span>
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2" for="email">*Email:</label>
            <div class="col-sm-10">
              <sf:input path="email" class="form-control" placeholder="Enter email" type="email" required="required" />
              <span class="help-block"><sf:errors path="email" class="help-inline" /></span>
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2" for="password">*Password:</label>
            <div class="col-sm-10">
              <sf:input path="password" class="form-control" placeholder="Enter Password" type="password"
                required="required" />
              <span class="help-block"><sf:errors path="password" class="help-inline" /></span>
            </div>
          </div>
          <div class="form-group">
            <label class="control-label col-sm-2">*Retype Password:</label>
            <div class="col-sm-10">
              <sf:input path="${password_check}" class="form-control" placeholder="Retype Password" type="password"
                required="required" />
              <span class="help-block"><sf:errors path="password" class="help-inline" /></span>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <button type="reset" class="btn btn-default">Reset</button>
              <button type="submit" class="btn btn-success">Submit</button>
            </div>
          </div>
        </sf:form>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>