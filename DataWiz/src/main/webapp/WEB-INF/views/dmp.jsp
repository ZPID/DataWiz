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
      <ul class="nav nav-tabs subnavtop">
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
      <c:url var="dmpUrl" value="/dmp/${ProjectForm.project.id}" />
      <sf:form action="${dmpUrl}" commandName="ProjectForm" class="form-horizontal" role="form" id="dmpForm" onsubmit="checkOnSubmit();">
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
        <jsp:include page="forms/dmp_admindata.jsp" />
        <!-- START Research Data Content -->
        <jsp:include page="forms/dmp_researchdata.jsp">
          <jsp:param value="${localeCode}" name="localeCode"/>
        </jsp:include>
        <!-- START Meta Data Content -->
        <jsp:include page="forms/dmp_metadata.jsp" />
        <!-- START Data Sharing Content -->
        <jsp:include page="forms/dmp_sharing.jsp" />
        <!-- START Storage Content -->
        <jsp:include page="forms/dmp_storage.jsp"/>
        <!-- START organization Content -->
        <jsp:include page="forms/dmp_organization.jsp"/>
        <!-- START ethical Content -->
        <jsp:include page="forms/dmp_ethical.jsp"/>
        <!-- START costs Content -->
        <jsp:include page="forms/dmp_costs.jsp"/>
        <!-- Buttons -->
        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <button type="reset" class="btn btn-default">
              <s:message code="gen.reset" />
            </button>
            <sf:button type="submit" class="btn btn-success">
              <s:message code="gen.submit" />
            </sf:button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
  <div class="modal fade" data-toggle="draftModal" data-target="#draftModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <div>
                   <p>You are about to leave the deal. Would you like to save as a draft?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary">Save changes</button>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
<%@ include file="templates/footer.jsp"%>