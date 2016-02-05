<%@ include file="../templates/includes.jsp"%>
<div id="administratriveActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="well marginTop1">
      <s:message code="dmp.edit.admindata.info" />
    </div>
  </div>
  <!-- Projectname -->
  <div class="form-group">
    <label class="required" for="project.title"><s:message code="dmp.edit.projectname" /></label>
    <div>
      <s:message code="project.edit.title.ph" var="placeholder" />
      <sf:input path="project.title" class="form-control" placeholder="${placeholder}" />
      <sf:errors path="project.title" cssClass="alert alert-danger" element="div" />
    </div>
  </div>
  <!-- Projectaims -->
  <div class="form-group">
    <label for="project.projectIdent"><s:message code="dmp.edit.projectaims" /></label>
    <sf:textarea rows="5" path="dmp.projectAims" class="form-control" disabled="" />
    <div class="row help-block">
      <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
      <div class="col-sm-11">
        <s:message code="dmp.edit.projectaims.help" />
      </div>
    </div>
  </div>
  <!-- Funding -->
  <div class="form-group">
    <label for="project.projectIdent"><s:message code="dmp.edit.funding" /></label>
    <sf:input path="project.funding" class="form-control" />
    <div class="row help-block">
      <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
      <div class="col-sm-11 ">
        <s:message code="dmp.edit.funding.help" />
      </div>
    </div>
  </div>
  <!-- duration -->
  <div class="form-group">
    <label for="project.projectIdent"><s:message code="dmp.edit.duration" /></label>
    <sf:input path="dmp.duration" class="form-control" disabled="" />
  </div>
  <!-- organizations -->
  <div class="form-group">
    <label for="project.projectIdent"><s:message code="dmp.edit.organizations" /></label>
    <sf:input path="dmp.organizations" class="form-control" disabled="" />
  </div>
  <!-- PrimaryContributor -->
  <div class="form-group">
    <label for="project.title"><s:message code="dmp.edit.leader" /></label>
    <div>
      <ul class="list-group">
        <li class="list-group-item">
          <div class="form-group">
            <div class="form-group row">
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
          <div class="form-group">
            <div class="form-group row">
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
          <div class="form-group">
            <div class="form-group row">
              <!-- PrimaryContributor institution -->
              <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.intitution" /></label>
              <div class="col-md-8">
                <s:message code="gen.intitution.ph" var="placeholder" />
                <sf:input path="primaryContributor.institution" class="form-control" placeholder="${placeholder}" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <div class="form-group row">
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
  <!-- dmp-aims -->
  <div class="form-group">
    <label for="project.projectIdent"><s:message code="dmp.edit.dmpaims" /></label>
    <sf:textarea rows="5" path="dmp.planAims" class="form-control" disabled="" />
    <div class="row help-block">
      <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
      <div class="col-sm-11">
        <s:message code="dmp.edit.dmpaims.help" />
      </div>
    </div>
  </div>
</div>