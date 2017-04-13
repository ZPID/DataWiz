<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:url var="projectUrl" value="/project" />
        <div class="row">
          <div class="col-sm-12 text-align-right">
            <a href="${projectUrl}/" class="btn btn-success btn-sm"><s:message code="project.create.button" /></a>
          </div>
          <div class="col-sm-12">
            <h4>
              <s:message code="projects.overview.headline" />
            </h4>
          </div>
        </div>
        <div>
          <s:message code="projects.overview.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <sf:form action="${projectUrl}" commandName="CProjectForm" class="form-inline">
        <div class="panel-group">
          <c:set var="firstIn" value="in" />
          <c:forEach items="${CProjectForm}" var="form" varStatus="loop">
            <c:choose>
              <c:when test="${principal.user.hasRole('PROJECT_ADMIN', form.project.id, false)}">
                <c:set var="pRole" value="panel-primary" />
              </c:when>
              <c:when test="${principal.user.hasRole('PROJECT_WRITER', form.project.id, false)}">
                <c:set var="pRole" value="panel-info" />
              </c:when>
              <c:when test="${principal.user.hasRole('PROJECT_READER', form.project.id, false)}">
                <c:set var="pRole" value="panel-warning" />
              </c:when>
              <c:when test="${principal.user.hasRole('ADMIN')}">
                <c:set var="pRole" value="panel-red" />
              </c:when>
              <c:otherwise>
                <c:set var="pRole" value="panel-default" />
              </c:otherwise>
            </c:choose>
            <div class="panel <c:out value="${pRole}"/> ">
              <div class="panel-heading" onclick="location.href='<c:url value="project/${form.project.id}"/>';"
                style="cursor: pointer;">
                <div class="row">
                  <div class="col-sm-12">
                    <div class="row">
                      <div class="col-sm-12 panel-title">
                        <s:message text="${form.project.title}" />
                      </div>
                      <div class="col-sm-12">
                        <c:if test="${not empty form.contributors && form.contributors.size() > 0 }">
                          <s:message text="&#040;" />
                          <c:forEach items="${form.contributors}" var="contri" varStatus="contriloop">
                            <s:message text="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}" />
                            <c:if test="${contriloop.index < form.contributors.size()-1}">
                              <s:message text="&#044;" />
                            </c:if>
                          </c:forEach>
                          <s:message text="&#041;" />
                        </c:if>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <c:set var="firstIn" value="" />
              <div class="panel-body">
                <div class="row">
                  <div class="col-sm-2 text-align-right">
                    <label for="${form.project.description}">Beschreibung:</label>
                  </div>
                  <div class="col-sm-10">
                    <c:choose>
                      <c:when test="${not empty form.project.description}">
                        <s:message text="${form.project.description}" />
                      </c:when>
                      <c:otherwise>
                        <s:message text="noch keine Projektbeschreibung vorhanden" />
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
                <div class="row">
                  <div class="col-sm-2 text-align-right">
                    <label for="${study}">Studienübersicht:</label>
                  </div>
                  <div class="col-sm-10">
                    <c:choose>
                      <c:when test="${empty form.studies}">
                        <s:message text="noch keine Studien vorhanden" />
                      </c:when>
                      <c:otherwise>
                        <div class="panel panel-default">
                          <div class="panel-heading" data-toggle="collapse" data-target="#panel_coll_${loop.count}">Show/hide
                            Studies</div>
                          <div class="panel-collapse collapse" id="panel_coll_${loop.count}">
                            <div class="list-group">
                              <c:forEach items="${form.studies}" var="study" varStatus="loop2">
                                <div class="list-group-item ">
                                  <h4 class="list-group-item-heading"
                                    onclick="location.href='<c:url value="project/${form.project.id}/study/${study.id}"/>';"
                                    style="cursor: pointer;">
                                    <c:out value="${study.title}"></c:out>
                                  </h4>
                                  <br />
                                  <c:out value="${study.sAbstract}"></c:out>
                                </div>
                              </c:forEach>
                            </div>
                          </div>
                        </div>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
              </div>
              <div class="panel-footer">
                <c:forEach items="${form.sharedUser}" var="user" varStatus="loopu">
                  <c:choose>
                    <c:when
                      test="${not empty user.lastName && not empty user.firstName && loopu.index < form.sharedUser.size()-1}">
                      <c:set var="shared" value="${shared} ${user.firstName} ${user.lastName}(${user.email}), " />
                    </c:when>
                    <c:when
                      test="${not empty user.lastName && not empty user.firstName && loopu.index >= form.sharedUser.size()-1}">
                      <c:set var="shared" value="${shared} ${user.firstName} ${user.lastName}(${user.email})" />
                    </c:when>
                    <c:when
                      test="${(empty user.lastName || empty user.firstName) && loopu.index < form.sharedUser.size()-1}">
                      <c:set var="shared" value="${shared} ${user.email}, " />
                    </c:when>
                    <c:otherwise>
                      <c:set var="shared" value="${shared} ${user.email}" />
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
                <div class="row">
                  <div class="col-sm-1">
                    <span class="label label-default" data-toggle="tooltip" data-placement="top"
                      title="<c:out value="${shared}" />"><s:message code="panel.shared" /> (<c:out
                        value="${form.sharedUser.size()}" />)</span>
                  </div>
                  <div class="col-sm-11 text-align-right">
                    <c:choose>
                      <c:when test="${not empty form.studies[0] && form.studies.size() > 0 }">
                        <c:forEach items="${form.sharedUser}" var="user">
                          <c:if test="${user.id eq form.studies[0].lastUserId}">
                            <javatime:format value="${form.studies[0].timestamp}" style="MS" var="strDate" />
                            <c:choose>
                              <c:when test="${not empty user.lastName && not empty user.firstName}">
                                <s:message code="panel.last.commit"
                                  arguments="${strDate};${user.firstName} ${user.lastName}" htmlEscape="false"
                                  argumentSeparator=";" />
                              </c:when>
                              <c:otherwise>
                                <s:message code="panel.last.commit" arguments="${strDate};${user.email}"
                                  htmlEscape="false" argumentSeparator=";" />
                              </c:otherwise>
                            </c:choose>
                          </c:if>
                        </c:forEach>
                      </c:when>
                      <c:otherwise>
                        <javatime:format value="${form.project.created}" style="MS" var="strDate" />
                        <s:message code="panel.created" arguments="${strDate}" htmlEscape="false" argumentSeparator=";" />
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
                <c:set var="shared" value="" />
              </div>
            </div>
          </c:forEach>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>