<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:url var="projectUrl" value="study" />
        <div class="row">

          <div class="col-sm-10">
            <h4>
              <s:message code="project.studies.headline" />
            </h4>
          </div>
          <c:if
            test="${principal.user.hasRole('PROJECT_ADMIN', ProjectForm.project.id, false) or
                  principal.user.hasRole('PROJECT_WRITER', ProjectForm.project.id, false) or principal.user.hasRole('ADMIN')}">
            <div class="col-sm-2 text-align-right">
              <a href="${projectUrl}" class="btn btn-success btn-sm"><s:message code="study.create.button" /></a>
            </div>
          </c:if>
        </div>
        <div>
          <s:message code="project.studies.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:choose>
        <c:when test="${principal.user.hasRole('PROJECT_ADMIN', ProjectForm.project.id, false)}">
          <c:set var="pRole" value="panel-primary" />
        </c:when>
        <c:when test="${principal.user.hasRole('PROJECT_WRITER', ProjectForm.project.id, false)}">
          <c:set var="pRole" value="panel-success" />
        </c:when>
        <c:when test="${principal.user.hasRole('PROJECT_READER', ProjectForm.project.id, false)}">
          <c:set var="pRole" value="panel-info" />
        </c:when>
        <c:when test="${principal.user.hasRole('ADMIN')}">
          <c:set var="pRole" value="panel-red" />
        </c:when>
        <c:otherwise>
          <c:set var="pRole" value="panel-default" />
        </c:otherwise>
      </c:choose>
      <div class="panel-group">
        <c:forEach items="${ProjectForm.studies}" var="cstud">
          <c:set var="pRole_set" value="${pRole}" />
          <c:if test="${pRole_set eq 'panel-default'}">
            <c:choose>
              <c:when test="${principal.user.hasRole('DS_WRITER', cstud.id, true)}">
                <c:set var="pRole_set" value="panel-success" />
              </c:when>
              <c:when test="${principal.user.hasRole('DS_READER', cstud.id, true)}">
                <c:set var="pRole_set" value="panel-info" />
              </c:when>
            </c:choose>
          </c:if>
          <div class="panel <c:out value="${pRole_set}"/>">
            <div class="panel-heading" onclick="location.href='<c:url value="study/${cstud.id}"/>';" style="cursor: pointer;">
              <div class="row">
                <div class="col-sm-12">
                  <s:message text="${cstud.title}" />
                </div>
              </div>
            </div>
            <div class="panel-body">
              <!-- Description -->
              <div class="row" style="padding-bottom: 10px;">
                <div class="col-xs-1 col-sm-2 text-align-right">
                  <strong><s:message code="project.panel.project.description" /></strong>
                </div>
                <div class="col-xs-12 col-sm-10">
                  <c:choose>
                    <c:when test="${not empty cstud.sAbstract}">
                      <s:message text="${cstud.sAbstract}" />
                    </c:when>
                    <c:otherwise>
                      <s:message code="study.panel.no.description" />
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
              <!-- Contributor -->
              <div class="row" style="padding-bottom: 10px;">
                <div class="col-xs-1 col-sm-2 text-align-right">
                  <strong><s:message code="project.panel.project.researcher" /></strong>
                </div>
                <div class="col-xs-12 col-sm-10 studyResearcherFilter">
                  <c:choose>
                    <c:when test="${not empty cstud.contributors && cstud.contributors.size() > 0 }">
                      <c:forEach items="${cstud.contributors}" var="contri" varStatus="contriloop">
                        <s:message text="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}" />
                        <c:if test="${contriloop.index < cstud.contributors.size()-1}">
                          <s:message text="&#044;" />
                        </c:if>
                      </c:forEach>
                    </c:when>
                    <c:otherwise>
                      <s:message code="project.panel.no.researcher" />
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
            <div class="panel-footer">
              <div class="row">
                <div class="col-sm-4 text-align-left" style="margin-top: 3px">
                  <c:forEach items="${ProjectForm.sharedUser}" var="user">
                    <c:if test="${user.id eq cstud.lastUserId}">
                      <javatime:format value="${cstud.timestamp}" style="MS" var="strDate" />
                      <c:choose>
                        <c:when test="${not empty user.lastName && not empty user.firstName}">
                          <s:message code="panel.last.commit" arguments="${strDate};${user.firstName} ${user.lastName}" htmlEscape="false"
                            argumentSeparator=";" />
                        </c:when>
                        <c:otherwise>
                          <s:message code="panel.last.commit" arguments="${strDate};${user.email}" htmlEscape="false" argumentSeparator=";" />
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </c:forEach>
                </div>
                <div class="col-sm-8 text-align-right">
                  <button class="btn btn-success btn-xs" onclick="location.href='<c:url value="study/${cstud.id}"/>';">Ansehen/Bearbeiten</button>
                </div>
              </div>
            </div>
          </div>
          <br />
        </c:forEach>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>
