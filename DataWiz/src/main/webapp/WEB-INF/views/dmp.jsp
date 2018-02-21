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
      <div class="row text-align-right btn-line">
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
          <div class="dropdown">
            <button type="button" class="btn btn-primary btn-sm" data-toggle="dropdown" role="button"
              aria-haspopup="true" aria-expanded="false">
              <s:message code="dmp.export.btn.txt" />
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
              <li><a href="<c:url value="/dmp/${ProjectForm.project.id}/exportDMP/BMBF" />" target="_blank"><s:message
                    code="dmp.export.btn.bmbf" /></a></li>
              <li><a href="<c:url value="/dmp/${ProjectForm.project.id}/exportDMP/H2020" />" target="_blank"><s:message
                    code="dmp.export.btn.h2020" /></a></li>
              <li><a href="<c:url value="/dmp/${ProjectForm.project.id}/exportDMP/DFG" />" target="_blank"><s:message
                    code="dmp.export.btn.dfg" /></a></li>
            </ul>
          </div>
        </div>
      </div>
      <c:choose>
        <c:when test="${empty ProjectForm.project.id}">
          <c:set var="allowEdit" value="true" />
          <s:message code="dmp.create.headline" var="headline_head" />
          <s:message code="dmp.create.info" var="headline_info" />
          <%@ include file="templates/pages_headline.jsp"%>
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
          <s:message code="dmp.edit.headline" var="headline_head" />
          <s:message code="dmp.edit.info" var="headline_info" />
          <%@ include file="templates/pages_headline.jsp"%>
        </c:otherwise>
      </c:choose>
      <div class="row">
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
          <ul class="nav nav-tabs subnavtop" data-spy="affix" data-offset-top="400">
            <li role="presentation" id="administratriveActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.administrative" /></a></li>
            <li role="presentation" id="researchActiveClick" class="projectContentClick "><a><s:message
                  code="dmp.submenu.research" /></a></li>
            <li role="presentation" id="metadataActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.metadata" /></a></li>
            <li role="presentation" id="accessActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.access" /></a></li>
            <li role="presentation" id="storageActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.storage" /></a></li>
            <li role="presentation" id="organizationActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.organization" /></a></li>
            <li role="presentation" id="ethicalActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.ethical" /></a></li>
            <li role="presentation" id="costsActiveClick" class="projectContentClick"><a><s:message
                  code="dmp.submenu.costs" /></a></li>
          </ul>
        </div>
      </div>
      <c:url var="dmpUrl" value="/dmp/${ProjectForm.project.id}" />
      <sf:form action="${dmpUrl}" modelAttribute="ProjectForm" class="form-horizontal" role="form" id="dmpForm"
        onsubmit="checkOnSubmit();">
        <sf:hidden path="pagePosi" id="pagePosi" />
        <sf:hidden path="dmp.id" />
        <sf:hidden path="project.id" />
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