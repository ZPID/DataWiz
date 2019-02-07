<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp" %>
    <%@ include file="templates/submenu.jsp" %>
    <div class="content-padding">
      <div class="row text-align-right btn-line">
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
          <c:url var="projectUrl" value="study"/>
          <c:if
              test="${principal.user.hasRole('PROJECT_ADMIN', ProjectForm.project.id, false) or
                  principal.user.hasRole('PROJECT_WRITER', ProjectForm.project.id, false) or principal.user.hasRole('ADMIN')}">
            <a href="${projectUrl}" class="btn btn-success btn-sm"><s:message code="study.create.button"/></a>
          </c:if>
        </div>
      </div>
      <s:message code="project.studies.headline" var="headline_head"/>
      <s:message code="project.studies.info" var="headline_info"/>
      <%@ include file="templates/pages_headline.jsp" %>
      <!-- Messages -->
      <%@ include file="templates/message.jsp" %>
      <c:choose>
        <c:when test="${principal.user.hasRole('PROJECT_ADMIN', ProjectForm.project.id, false)}">
          <c:set var="pRole" value="panel-primary"/>
        </c:when>
        <c:when test="${principal.user.hasRole('PROJECT_WRITER', ProjectForm.project.id, false)}">
          <c:set var="pRole" value="panel-success"/>
        </c:when>
        <c:when test="${principal.user.hasRole('PROJECT_READER', ProjectForm.project.id, false)}">
          <c:set var="pRole" value="panel-info"/>
        </c:when>
        <c:when test="${principal.user.hasRole('ADMIN')}">
          <c:set var="pRole" value="panel-red"/>
        </c:when>
        <c:otherwise>
          <c:set var="pRole" value="panel-default"/>
        </c:otherwise>
      </c:choose>
      <div class="panel-group">
        <c:forEach items="${ProjectForm.studies}" var="cstud">
          <c:set var="pRole_set" value="${pRole}"/>
          <c:if test="${pRole_set eq 'panel-default'}">
            <c:choose>
              <c:when test="${principal.user.hasRole('DS_WRITER', cstud.id, true)}">
                <c:set var="pRole_set" value="panel-success"/>
              </c:when>
              <c:when test="${principal.user.hasRole('DS_READER', cstud.id, true)}">
                <c:set var="pRole_set" value="panel-info"/>
              </c:when>
            </c:choose>
          </c:if>
          <div class="panel <c:out value="${pRole_set}"/>">
            <div class="panel-heading" onclick="location.href='<c:url value="study/${cstud.id}?_s=${cstud.id}"/>';"
                 style="cursor: pointer;">
              <div class="row">
                <div class="col-sm-12">
                  <s:message text="${cstud.title}"/>
                </div>
              </div>
            </div>
            <div class="panel-body">
              <!-- Description -->
              <div class="row" style="padding-bottom: 10px;">
                <div class="col-xs-1 col-sm-2 text-align-right">
                  <strong><s:message code="project.panel.project.description"/></strong>
                </div>
                <div class="col-xs-12 col-sm-10">
                  <c:choose>
                    <c:when test="${not empty cstud.sAbstract}">
                      <s:message text="${cstud.sAbstract}"/>
                    </c:when>
                    <c:otherwise>
                      <s:message code="study.panel.no.description"/>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
              <!-- Contributor -->
              <div class="row" style="padding-bottom: 10px;">
                <div class="col-xs-1 col-sm-2 text-align-right">
                  <strong><s:message code="project.panel.project.researcher"/></strong>
                </div>
                <div class="col-xs-12 col-sm-10 studyResearcherFilter">
                  <c:choose>
                    <c:when test="${not empty cstud.contributors && cstud.contributors.size() > 0 }">
                      <c:forEach items="${cstud.contributors}" var="contri" varStatus="contriloop">
                        <s:message text="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}"/>
                        <c:if test="${contriloop.index < cstud.contributors.size()-1}">
                          <s:message text="&#044;"/>
                        </c:if>
                      </c:forEach>
                    </c:when>
                    <c:otherwise>
                      <s:message code="project.panel.no.researcher"/>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
            <div class="panel-footer">
              <div class="row">
                <div class="col-sm-4 text-align-left" style="margin-top: 3px">
                  <javatime:format value="${cstud.timestamp}" style="MS" var="strDate"/>
                  <c:set var="u_name" value=""/>
                  <c:forEach items="${ProjectForm.sharedUser}" var="user">
                    <c:if test="${user.id eq cstud.lastUserId}">
                      <c:choose>
                        <c:when test="${not empty user.lastName && not empty user.firstName}">
                          <c:set var="u_name" value="${user.firstName} ${user.lastName}"/>
                        </c:when>
                        <c:when test="${not empty user.lastName && empty user.firstName}">
                          <c:set var="u_name" value="${user.firstName}"/>
                        </c:when>
                        <c:when test="${not empty user.firstName && empty user.lastName}">
                          <c:set var="u_name" value="${user.lastName}"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="u_name" value="${user.email}"/>
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </c:forEach>
                  <c:if test="${empty u_name}">
                    <s:message code="panel.last.commit.unknown" var="u_name"/>
                  </c:if>
                  <s:message code="panel.last.commit" arguments="${strDate}|${u_name}" htmlEscape="false" argumentSeparator="|"/>
                </div>
                <div class="col-sm-8 text-align-right">
                  <button class="btn btn-info btn-xs" onclick="location.href='<c:url value="study/${cstud.id}?_s=${cstud.id}"/>';">
                    <s:message code="panel.btn.view.edit"/>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <br/>
        </c:forEach>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp" %>
