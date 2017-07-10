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
      <label for="project.title"><s:message code="dmp.edit.projectname" /></label>
      <div class="well" style="">
        <s:message text="${ProjectForm.project.title}" />
      </div>
    </div>
  </div>
  <!-- projectaims -->
  <div class="form-group">
    <div class="col-sm-12">
      <label for="project.description"><s:message code="dmp.edit.projectAims" /></label>
      <div class="well" style="">
        <s:message text="${ProjectForm.project.description}" />
      </div>
    </div>
  </div>
  <!-- projectSponsors -->
  <div class="form-group">
    <div class="col-sm-12">
      <label for="project.funding"><s:message code="dmp.edit.projectSponsors" /></label>
      <div class="well" style="">
        <s:message text="${ProjectForm.project.funding}" />
      </div>
    </div>
  </div>
  <!-- duration -->
  <s:message text="duration" var="dmp_var_name" />
  <%@ include file="../templates/dmp_input.jsp"%>
  <!-- organizations -->
  <s:message text="organizations" var="dmp_var_name" />
  <%@ include file="../templates/dmp_input.jsp"%>
  <!-- PrimaryContributor -->
  <div class="form-group">
    <div class="col-sm-12">
      <label for="primaryContributor.firstName"><s:message code="dmp.edit.leader" /></label>
      <s:message var="primaryName"
        text="${ProjectForm.primaryContributor.title}&nbsp;${ProjectForm.primaryContributor.firstName}&nbsp;${ProjectForm.primaryContributor.lastName}" />
      <div class="well" style="">
        <s:message text="${fn:trim(primaryName)}" />
      </div>
    </div>
  </div>
  <!-- planAims -->
  <s:message text="planAims" var="dmp_var_name" />
  <%@ include file="../templates/textarea.jsp"%>
</div>