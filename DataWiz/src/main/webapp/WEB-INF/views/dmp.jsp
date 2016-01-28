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
        <!-- START Administration Data Content -->
        <%@ include file="forms/admindata.jsp"%>
        <!-- START Research Data Content -->
        <%@ include file="forms/researchdata.jsp"%>
        <!-- START Meta Data Content -->
        <div id="metadataActiveContent" class="projectContent">
          <!-- Infotxt -->
          <div class="form-group">
            <div class="col-sm-12">
              <div class="well marginTop1">
                <s:message code="project.edit.metadata.info" />
              </div>
            </div>
          </div>
          <ul class="list-group">
            <!-- releaseObligation -->
            <li class="list-group-item"></li>
          </ul>
        </div>
        <!-- START Data Sharing Content -->
        <%@ include file="forms/sharing.jsp"%>

        <div id="storageActiveContent" class="projectContent">5678</div>
        <div id="organizationActiveContent" class="projectContent">12345</div>
        <div id="ethicalActiveContent" class="projectContent">67890</div>
        <div id="costsActiveContent" class="projectContent">1234567890</div>
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
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>