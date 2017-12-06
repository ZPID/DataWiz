<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<c:set var="valimag1" value="H2020.png" scope="session" />
<c:set var="valimag2" value="BMBF.png" scope="session" />
<c:set var="valimag3" value="DFG.png" scope="session" />
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:choose>
          <c:when test="${empty ProjectForm.project.id}">
            <c:set var="allowEdit" value="true" />
            <h4>
              <s:message code="dmp.create.headline" />
            </h4>
            <div>
              <s:message code="dmp.create.info" />
            </div>
          </c:when>
          <c:otherwise>
            <c:set var="allowEdit" value="false" />
            <c:choose>
              <c:when
                test="${principal.user.hasRole('PROJECT_ADMIN', ProjectForm.project.id, false) or
                        principal.user.hasRole('PROJECT_WRITER', ProjectForm.project.id, false) or 
                        principal.user.hasRole('ADMIN')}">
                <c:set var="allowEdit" value="true" />
              </c:when>
              <c:otherwise>
                <input type="hidden" value="disabled" id="disProjectContent" />
              </c:otherwise>
            </c:choose>
            <div class="row">
              <div class="col-xs-4 col-sm-6">
                <h4>
                  <s:message code="dmp.edit.headline" />
                </h4>
              </div>
              <div class="col-xs-8 col-sm-6 text-align-right">
                (Export currently in work)
                <div style="display: inline-block;">
                  <a class="btn btn-primary btn-sm" href="<c:url value="/dmp/exportDMP/BMBF/${ProjectForm.project.id}" />" target="_blank">BMBF</a>
                </div>
                <div style="display: inline-block;">
                  <a class="btn btn-primary btn-sm" href="<c:url value="/dmp/exportDMP/H2020/${ProjectForm.project.id}" />" target="_blank">H2020</a>
                </div>
                <div style="display: inline-block;">
                  <a class="btn btn-primary btn-sm" href="<c:url value="/dmp/exportDMP/DFG/${ProjectForm.project.id}" />" target="_blank">DFG</a>
                </div>
                <div style="display: inline-block;">
                  <a class="btn btn-primary btn-sm" href="<c:url value="/dmp/exportDMP/PsychData/${ProjectForm.project.id}" />" target="_blank">PsychData</a>
                </div>
                <div style="display: inline-block;">
                  <a class="btn btn-primary btn-sm" href="<c:url value="/dmp/exportDMP/PreReg/${ProjectForm.project.id}" />" target="_blank">Präregistrierung</a>
                </div>
              </div>
            </div>
            <div>
              <s:message code="dmp.edit.info" />
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      <ul class="nav nav-tabs subnavtop" data-spy="affix" data-offset-top="400">
        <li role="presentation" id="administratriveActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.administrative" /></a></li>
        <li role="presentation" id="researchActiveClick" class="projectContentClick "><a><s:message code="dmp.submenu.research" /></a></li>
        <li role="presentation" id="metadataActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.metadata" /></a></li>
        <li role="presentation" id="accessActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.access" /></a></li>
        <li role="presentation" id="storageActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.storage" /></a></li>
        <li role="presentation" id="organizationActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.organization" /></a></li>
        <li role="presentation" id="ethicalActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.ethical" /></a></li>
        <li role="presentation" id="costsActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.costs" /></a></li>
      </ul>
      <c:url var="dmpUrl" value="/dmp/${ProjectForm.project.id}" />
      <sf:form action="${dmpUrl}" commandName="ProjectForm" class="form-horizontal" role="form" id="dmpForm" onsubmit="checkOnSubmit();">
        <sf:hidden path="pagePosi" id="pagePosi" />
        <input type="hidden" id="jQueryMap" name="jQueryMap" value="${jQueryMap}" />
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <!-- START Administration Data Content -->
        <jsp:include page="forms/dmp_admindata.jsp" />
        <!-- START Research Data Content -->
        <jsp:include page="forms/dmp_researchdata.jsp">
          <jsp:param value="${localeCode}" name="localeCode" />
        </jsp:include>
        <!-- START Meta Data Content -->
        <jsp:include page="forms/dmp_metadata.jsp" />
        <!-- START Data Sharing Content -->
        <jsp:include page="forms/dmp_sharing.jsp" />
        <!-- START Storage Content -->
        <jsp:include page="forms/dmp_storage.jsp" />
        <!-- START organization Content -->
        <jsp:include page="forms/dmp_organization.jsp" />
        <!-- START ethical Content -->
        <jsp:include page="forms/dmp_ethical.jsp" />
        <!-- START costs Content -->
        <jsp:include page="forms/dmp_costs.jsp" />
        <!-- Buttons -->
        <div class="row">
          <div class="col-xs-6 text-align-left">
            <a href="<c:url value="/panel" />" class="btn btn-default btn-sm"><s:message code="back.to.panel" /></a>
          </div>
          <div class="col-xs-6 text-align-right">
            <c:if test="${allowEdit}">
              <sf:button type="submit" class="btn btn-success btn-sm">
                <s:message code="gen.submit" />
              </sf:button>
            </c:if>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>