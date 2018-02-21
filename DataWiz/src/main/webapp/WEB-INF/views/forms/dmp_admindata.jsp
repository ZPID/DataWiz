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
        <sf:hidden path="project.title" />
      </div>
    </div>
  </div>
  <!-- projectaims -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="project.description"><s:message code="dmp.edit.projectAims" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img
            src="/DataWiz/static/images/${valimag2}" class="infoImages" /><img src="/DataWiz/static/images/${valimag3}"
            class="infoImages" />
        </div>
      </div>
      <div class="well" style="">
        <s:message text="${ProjectForm.project.description}" />
        <sf:hidden path="project.description" />
      </div>
    </div>
  </div>
  <!-- projectSponsors -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="project.funding"><s:message code="dmp.edit.projectSponsors" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <div class="well" style="">
        <s:message text="${ProjectForm.project.funding}" />
        <sf:hidden path="project.funding" />
      </div>
    </div>
  </div>
  <!-- duration -->
  <c:set var="input_vars" value="dmp.duration;dmp.edit.duration; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- organizations -->
  <c:set var="input_vars" value="dmp.organizations;dmp.edit.organizations; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- PrimaryContributor -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="primaryContributor"><s:message code="dmp.edit.leader" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <s:message var="primaryName"
        text="${ProjectForm.primaryContributor.title}&nbsp;${ProjectForm.primaryContributor.firstName}&nbsp;${ProjectForm.primaryContributor.lastName}" />
      <div class="well" style="">
        <s:message text="${fn:trim(primaryName)}" />
        <sf:hidden path="primaryContributor.title" />
        <sf:hidden path="primaryContributor.firstName" />
        <sf:hidden path="primaryContributor.lastName" />
      </div>
    </div>
  </div>
  <!-- planAims -->
  <c:set var="input_vars" value="dmp.planAims;dmp.edit.planAims; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
</div>