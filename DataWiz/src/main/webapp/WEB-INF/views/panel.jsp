<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="content-padding">
      <c:if test="${not empty errorMSG}">
        <div class="alert alert-danger" role="alert">
          <c:out value="${errorMSG}" />
        </div>
      </c:if>
      <c:if test="${not empty infoMSG}">
        <div class="alert alert-danger" role="alert">
          <c:out value="${infoMSG}" />
        </div>
      </c:if>
      <c:url var="projectUrl" value="/project" />
      <sf:form action="${projectUrl}" commandName="CProjectForm" StyleClass="form-horizontal">
        <div class="panel-group" id="accordion">
          <div class="panel">
            <div class="row">
              <div class="col-xs-12 clearfix">
                <a href="${projectUrl}/" class="btn btn-success pull-right"><s:message code="project.create.button" /></a>
              </div>
            </div>
          </div>
          <c:forEach items="${CProjectForm}" var="form" varStatus="loop">
            <c:set var="pRole" value="" />
            <c:forEach items="${principal.user.globalRoles}" var="cRole">
              <c:if test="${form.project.id eq cRole.projectId}">
                <c:choose>
                  <c:when
                    test="${empty pRole && !(pRole eq 'PROJECT_ADMIN' && pRole eq 'PROJECT_WRITER' && pRole eq 'PROJECT_READER') && 
                                (cRole.type eq 'DS_READER' || cRole.type eq 'DS_WRITER')}">
                    <c:set var="pRole" value="panel-success" />
                  </c:when>
                  <c:when test="${empty pRole && cRole.type eq 'PROJECT_ADMIN'}">
                    <c:set var="pRole" value="panel-primary" />
                  </c:when>
                  <c:when test="${empty pRole && cRole.type eq 'PROJECT_WRITER'}">
                    <c:set var="pRole" value="panel-info" />
                  </c:when>
                  <c:when test="${empty pRole && cRole.type eq 'PROJECT_READER'}">
                    <c:set var="pRole" value="panel-warning" />
                  </c:when>
                </c:choose>
              </c:if>
            </c:forEach>
            <div class="panel <c:out value="${pRole}"/> ">
              <div class="panel-heading accordion-toggle" data-toggle="collapse" data-target="#panel_coll_${loop.count}"
                data-parent="#accordion">
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
                    <i class="indicator glyphicon glyphicon glyphicon-plus  pull-right"></i>
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
                        <c:out value="${study.timestamp}"></c:out>
                      </div>
                    </c:forEach>
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
                  <div class="col-xs-6">
                    <span class="label label-success" data-toggle="tooltip" data-placement="top"
                      title="<c:out value="${form.project.title}"></c:out>"><s:message code="panel.published" /></span>
                    <span class="label label-default" data-toggle="tooltip" data-placement="top"
                      title="<c:out value="${shared}" />"><s:message code="panel.shared" /> (<c:out
                        value="${form.sharedUser.size()}" />)</span> <a href="project/${form.project.id}"
                      class="label label-primary"><s:message code="gen.edit" /></a>
                  </div>
                  <div class="col-xs-6">
                    <c:choose>
                      <c:when test="${not empty form.studies[0] && form.studies.size() > 0 }">
                        <c:forEach items="${form.sharedUser}" var="user">
                          <c:if test="${user.id eq form.studies[0].lastUserId}">
                            <c:set var="date" value="${fn:split(form.studies[0].timestamp, 'T')}" />
                            <fmt:parseDate value="${date[0]}/${date[1]}" pattern="yyyy-MM-dd/HH:mm:ss" var="parsedDate"
                              type="date" />
                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy - HH:mm:ss" var="strDate" />
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
                        <c:set var="date" value="${fn:split(form.project.created, 'T')}" />
                        <fmt:parseDate value="${date[0]}/${date[1]}" pattern="yyyy-MM-dd/HH:mm:ss" var="parsedDate"
                          type="date" />
                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy - HH:mm:ss" var="strDate" />
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