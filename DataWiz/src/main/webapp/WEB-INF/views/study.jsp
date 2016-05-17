<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:choose>
          <c:when test="${empty StudyForm.study.id}">
            <h4>
              <s:message code="study.create.basis.headline" />
            </h4>
            <div>
              <s:message code="study.create.basis.info" />
            </div>
          </c:when>
          <c:otherwise>
            <h4>
              <s:message code="study.edit.basis.headline" arguments="${StudyForm.study.title}" />
            </h4>
            <div>
              <s:message code="study.edit.basis.info" />
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      <!-- Submenu -->
      <c:if test="${!hideMenu}">
        <ul class="nav nav-tabs">
          <li role="presentation" id="basisDataActiveClick" class="projectContentClick"><a><s:message
                code="study.submenu.basic.data" /></a></li>
          <c:if test="${not empty StudyForm.study.id}">
            <li role="presentation" id="designActiveClick" class="projectContentClick"><a><s:message
                  code="study.submenu.design" /></a></li>
            <li role="presentation" id="sampleActiveClick" class="projectContentClick"><a><s:message
                  code="study.submenu.sample" /></a></li>
            <li role="presentation" id="surveyActiveClick" class="projectContentClick"><a><s:message
                  code="study.submenu.survey" /></a></li>
            <li role="presentation" id="qualityActiveClick" class="projectContentClick"><a><s:message
                  code="study.submenu.quality" /></a></li>
            <li role="presentation" id="ethicalActiveClick" class="projectContentClick"><a><s:message
                  code="study.submenu.ethical" /></a></li>
          </c:if>
        </ul>
      </c:if>
      <c:url var="accessUrl" value="/access/${StudyForm.project.id}/study/${StudyForm.study.id}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal">
        <%@ include file="templates/message.jsp"%>
        <!-- START General Data Content -->
        <jsp:include page="forms/study_general.jsp" />
        <div id="designActiveContent" class="projectContent contentMargin">456</div>
        <div id="sampleActiveContent" class="projectContent contentMargin">789</div>
        <div id="surveyActiveContent" class="projectContent contentMargin">abc</div>
        <div id="qualityActiveContent" class="projectContent contentMargin">def</div>
        <div id="ethicalActiveContent" class="projectContent contentMargin">ghi</div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>