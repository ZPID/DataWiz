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
          <li role="presentation" id="basisDataActiveClick" class="studyContentClick"><a><s:message
                code="study.submenu.basic.data" /></a></li>
          <c:if test="${not empty StudyForm.study.id}">
            <li role="presentation" id="designActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.design" /></a></li>
            <li role="presentation" id="sampleActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.sample" /></a></li>
            <li role="presentation" id="surveyActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.survey" /></a></li>
            <li role="presentation" id="ethicalActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.ethical" /></a></li>
          </c:if>
        </ul>
      </c:if>
      <c:url var="accessUrl" value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal">
        <input type="hidden" id="jQueryMap" name="jQueryMap" value="${jQueryMap}" />
        <sf:hidden path="delPos" />
        <sf:hidden path="scrollPosition"/>
        <%@ include file="templates/message.jsp"%>
        <!-- START General Data Content -->
        <jsp:include page="forms/study_general.jsp" />
        <!-- START Design Data Content -->
        <jsp:include page="forms/study_design.jsp" />
        <!-- START Sample Characteristics Content -->
        <jsp:include page="forms/study_sample.jsp" />
        <!-- START Survey Content -->
        <jsp:include page="forms/study_survey.jsp">
          <jsp:param value="${localeCode}" name="localeCode" />
        </jsp:include>
        <!-- START Ethical Content -->
        <jsp:include page="forms/study_ethical.jsp" />
        <!-- Buttons -->
        <div class="form-group">
          <div class="col-sm-offset-0 col-md-12">
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
</div>
<%@ include file="templates/footer.jsp"%>