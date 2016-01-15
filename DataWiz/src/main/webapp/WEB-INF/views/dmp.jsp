<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.edit.headline" />
        </h4>
        <div>
          <s:message code="project.edit.info" />
        </div>
      </div>
      <ul class="nav nav-tabs">
        <li role="presentation" id="administratriveActiveClick" class="projectContentClick"><a><s:message
              code="dmp.submenu.administrative" /></a></li>
        <li role="presentation" id="researchActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.research" /></a></li>
        <li role="presentation" id="metadataActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.metadata" /></a></li>
        <li role="presentation" id="accessActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.access" /></a></li>
        <li role="presentation" id="storageActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.storage" /></a></li>
        <li role="presentation" id="organizationActiveClick" class="projectContentClick"><a><s:message
              code="dmp.submenu.organization" /></a></li>
        <li role="presentation" id="ethicalActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.ethical" /></a></li>
        <li role="presentation" id="costsActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.costs" /></a></li>
      </ul>
      <c:url var="projectUrl" value="/dmp/${ProjectForm.project.id}" />
      <sf:form action="${projectUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <input type="hidden" id="jQueryMap" name="jQueryMap" value="${jQueryMap}" />
        <div id="administratriveActiveContent" class="projectContent contentMargin">
          <div class="form-group">
            <div class="well marginTop1">
              <s:message code="project.edit.metadata.info" />
            </div>
          </div>
          <!-- Messages -->
          <c:if test="${not empty saveState && saveState != '' && not empty saveStateMsg && empty jQueryMap}">
            <div
              class="alert <c:out value="${saveState eq 'SUCCESS' ? 'alert-success' : 
                                                saveState eq 'ERROR' ? 'alert-danger' : 
                                                saveState eq 'INFO' ? 'alert-warning' : 'alert-info'}"/>"
              role="alert">
              <c:out value="${saveStateMsg}" />
            </div>
          </c:if>
          <!-- Projectname -->
          <div class="form-group">
            <label class="required" for="project.title"><s:message code="dmp.edit.projectname" /></label>
            <div>
              <s:message code="project.edit.title.ph" var="reg_first" />
              <sf:input path="project.title" class="form-control" placeholder="${reg_first}" />
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
                <s:message code="dmp.edit.projectaims.ph" />
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
                <s:message code="dmp.edit.funding.ph" />
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
                        <s:message code="gen.title.ph" var="reg_first" />
                        <sf:input path="primaryContributor.title" class="form-control" placeholder="${reg_first}" />
                      </div>
                      <!-- PrimaryContributor ORCID -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.orcid" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.orcid.ph" var="reg_first" />
                        <sf:input path="primaryContributor.orcid" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- PrimaryContributor firstName -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.first.name" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.first.name.ph" var="reg_first" />
                        <sf:input path="primaryContributor.firstName" class="form-control" placeholder="${reg_first}" />
                      </div>
                      <!-- PrimaryContributor lastName -->
                      <label for="inputValue" class="col-md-2 control-label"><s:message code="gen.last.name" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.last.name.ph" var="reg_first" />
                        <sf:input path="primaryContributor.lastName" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- PrimaryContributor institution -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.intitution" /></label>
                      <div class="col-md-8">
                        <s:message code="gen.intitution.ph" var="reg_first" />
                        <sf:input path="primaryContributor.institution" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- Contributors department-->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.department" /></label>
                      <div class="col-md-8">
                        <s:message code="gen.department.ph" var="reg_first" />
                        <sf:input path="primaryContributor.department" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <!-- dmp-aims -->
            <div class="form-group">
              <label for="project.projectIdent"><s:message code="dmp.edit.dmpaims" /></label>
              <sf:textarea rows="5" path="dmp.planAims" class="form-control" disabled="" />
              <div class="row help-block">
                <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                <div class="col-sm-11">
                  <s:message code="dmp.edit.dmpaims.ph" />
                </div>
              </div>
            </div>
          </div>
          <!-- Buttons -->
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <button type="reset" class="btn btn-default">
                <s:message code="gen.reset" />
              </button>
              <sf:button type="submit" class="btn btn-success" id="meta_submit">
                <s:message code="gen.submit" />
              </sf:button>
            </div>
          </div>






        </div>
        <div id="researchActiveContent" class="projectContent">456</div>
        <div id="metadataActiveContent" class="projectContent">789</div>
        <div id="accessActiveContent" class="projectContent">1234</div>
        <div id="storageActiveContent" class="projectContent">5678</div>
        <div id="organizationActiveContent" class="projectContent">12345</div>
        <div id="ethicalActiveContent" class="projectContent">67890</div>
        <div id="costsActiveContent" class="projectContent">1234567890</div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>