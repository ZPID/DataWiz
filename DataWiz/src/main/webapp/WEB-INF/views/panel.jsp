<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="login-card">
      <div class="login-form">
        <c:url var="projectUrl" value="/project" />
        <sf:form action="${projectUrl}" commandName="CProjectForm" StyleClass="form-horizontal">
          <div class="panel-group" id="accordion">
            <c:forEach items="${CProjectForm}" var="form" varStatus="loop">
              <div
                class="panel <c:out value="${form.project.projectRole.type eq 'PROJECT_ADMIN' ? 'panel-primary' : 
                                             form.project.projectRole.type eq 'PROJECT_WRITER' ? 'panel-info' : 
                                             form.project.projectRole.type eq 'PROJECT_READER' ? 'panel-warning' : 'panel-danger'}"/> ">
                <div class="panel-heading accordion-toggle" data-toggle="collapse"
                  data-target="#panel_coll_${loop.count}" data-parent="#accordion">
                  <div class="row">
                    <div class="col-xs-8">
                      <h4 class="panel-title">
                        <c:out value="${form.project.title}" />
                      </h4>
                      <c:if test="${not empty form.contributors && form.contributors.size() > 0 }">
                        &#040;
                        <c:forEach items="${form.contributors}" var="contri" varStatus="contriloop">
                          <c:out value="${contri.title}" />
                          <c:out value="${contri.firstName}" />&nbsp;
                          <c:out value="${contri.lastName}" />
                          <c:if test="${contriloop.index < form.contributors.size()-1}">&#044;&nbsp;</c:if>
                        </c:forEach>
                        &#041;
                      </c:if>
                    </div>
                    <div class="col-xs-3"></div>
                    <div class="col-xs-1">
                      <i class="indicator glyphicon glyphicon-chevron-down  pull-right"></i>
                    </div>
                  </div>
                </div>
                <div class="panel-collapse collapse" id="panel_coll_${loop.count}">
                  <div class="panel-body">
                    <div class="list-group">
                      <c:forEach items="${form.studies}" var="study" varStatus="loop2">
                        <div class="list-group-item ">
                          <h4 class="list-group-item-heading">
                            <c:out value="${study.title}"></c:out>
                          </h4>
                          <c:out value="${study.id}"></c:out>
                          <br />
                          <c:out value="${study.version}"></c:out>
                          <br />
                          <c:out value="${study.timestamp}"></c:out>
                        </div>
                      </c:forEach>
                    </div>
                  </div>
                </div>
                <div class="panel-footer">
                  <c:forEach items="${form.sharedUser}" var="user" varStatus="loopu">
                    <c:choose>
                      <c:when test="${not empty user.lastName && not empty user.firstName && loopu.index < form.sharedUser.size()-1}">
                        <c:set var="shared" value="${shared} ${user.firstName} ${user.lastName}(${user.email}), " />
                      </c:when>
                      <c:when test="${not empty user.lastName && not empty user.firstName && loopu.index >= form.sharedUser.size()-1}">
                        <c:set var="shared" value="${shared} ${user.firstName} ${user.lastName}(${user.email})" />
                      </c:when>
                      <c:when test="${(empty user.lastName || empty user.firstName) && loopu.index < form.sharedUser.size()-1}">
                        <c:set var="shared" value="${shared} ${user.email}, " />
                      </c:when>
                      <c:otherwise>
                        <c:set var="shared" value="${shared} ${user.email}" />
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                  <div class="row">
                    <div class="col-xs-6">
                      <span class="label label-success" data-toggle="tooltip" data-placement="top"
                        title="<c:out value="${form.project.title}"></c:out>">Veröffentlicht</span> <span
                        class="label label-default" data-toggle="tooltip" data-placement="top"
                        title="<c:out value="${shared}" />">geteilt (<c:out value="${form.sharedUser.size()}" />)
                      </span> <a href="project/${form.project.id}" class="label label-primary">Bearbeiten</a>
                    </div>
                    <div class="col-xs-6">
                      <c:choose>
                        <c:when test="${not empty form.studies&& form.studies.size() > 0 }">
                          <c:forEach items="${form.sharedUser}" var="user">
                            <c:if test="${user.id eq form.studies[0].lastUserId}">
                              <fmt:formatDate value="${form.studies[0].timestamp}" pattern="dd/MM/yyyy HH:mm"
                                var="strDate" />
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
</div>
<%@ include file="templates/footer.jsp"%>