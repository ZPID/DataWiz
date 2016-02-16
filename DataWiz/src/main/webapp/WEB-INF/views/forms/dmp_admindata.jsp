<%@ include file="../templates/includes.jsp"%>
<div id="administratriveActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- Projectname -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="required" for="project.title"><s:message code="dmp.edit.projectname" /></label>
      <div>
        <s:message code="project.edit.title.ph" var="placeholder" />
        <sf:input path="project.title" class="form-control" placeholder="${placeholder}" />
        <sf:errors path="project.title" cssClass="alert alert-danger" element="div" />
      </div>
    </div>
  </div>
  <!-- projectaims -->
  <s:message text="projectAims" var="dmp_var_name" />
  <%@ include file="../templates/textarea.jsp"%>
  <!-- projectSponsors -->
  <s:message text="projectSponsors" var="dmp_var_name" />
  <%@ include file="../templates/dmp_input.jsp"%>
  <!-- duration -->
  <s:message text="duration" var="dmp_var_name" />
  <%@ include file="../templates/dmp_input.jsp"%>
  <!-- organizations -->
  <s:message text="organizations" var="dmp_var_name" />
  <%@ include file="../templates/dmp_input.jsp"%>
  <!-- PrimaryContributor -->
  <div class="form-group">
    <div class="col-sm-12">
      <label for="project.title"><s:message code="dmp.edit.leader" /></label>
      <div>
        <ul class="list-group">
          <li class="list-group-item">
            <div class="row">
              <div class="form-group">
                <!-- PrimaryContributor title -->
                <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.title" /></label>
                <div class="col-md-3">
                  <s:message code="gen.title.ph" var="placeholder" />
                  <sf:input path="primaryContributor.title" class="form-control" placeholder="${placeholder}" />
                </div>
                <!-- PrimaryContributor ORCID -->
                <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.orcid" /></label>
                <div class="col-md-3">
                  <s:message code="gen.orcid.ph" var="placeholder" />
                  <sf:input path="primaryContributor.orcid" class="form-control" placeholder="${placeholder}" />
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group">
                <!-- PrimaryContributor firstName -->
                <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.first.name" /></label>
                <div class="col-md-3">
                  <s:message code="gen.first.name.ph" var="placeholder" />
                  <sf:input path="primaryContributor.firstName" class="form-control" placeholder="${placeholder}" />
                </div>
                <!-- PrimaryContributor lastName -->
                <label for="inputValue" class="col-md-2 control-label"><s:message code="gen.last.name" /></label>
                <div class="col-md-3">
                  <s:message code="gen.last.name.ph" var="placeholder" />
                  <sf:input path="primaryContributor.lastName" class="form-control" placeholder="${placeholder}" />
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group">
                <!-- PrimaryContributor institution -->
                <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.intitution" /></label>
                <div class="col-md-8">
                  <s:message code="gen.intitution.ph" var="placeholder" />
                  <sf:input path="primaryContributor.institution" class="form-control" placeholder="${placeholder}" />
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group">
                <!-- Contributors department-->
                <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.department" /></label>
                <div class="col-md-8">
                  <s:message code="gen.department.ph" var="placeholder" />
                  <sf:input path="primaryContributor.department" class="form-control" placeholder="${placeholder}" />
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
  <!-- planAims -->
  <s:message text="planAims" var="dmp_var_name" />
  <%@ include file="../templates/textarea.jsp"%>
</div>