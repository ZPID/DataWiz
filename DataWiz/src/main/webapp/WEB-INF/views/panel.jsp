<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp" %>
    <div class="content-padding">
      <div class="page-header">
        <c:url var="projectUrl" value="/project"/>
        <div class="row text-align-right btn-line">
          <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
            <a href="${projectUrl}" class="btn btn-info btn-sm"><s:message code="project.create.button"/></a>
          </div>
        </div>
        <s:message code="projects.overview.headline" var="headline_head"/>
        <s:message code="projects.overview.info" var="headline_info"/>
        <%@ include file="templates/pages_headline.jsp" %>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp" %>
      <sf:form action="${projectUrl}" modelAttribute="CProjectForm" class="form-inline">
        <input type="hidden" value="<s:message code="project.panel.no.description" />" id="unsetDescription">
        <input type="hidden" value="<s:message code="study.panel.no.description" />" id="unsetStudyDescription">
        <input type="hidden" value="<s:message code="project.panel.no.researcher" />" id="unsetResearcher">
        <c:if test="${not empty CProjectForm}">
          <div class="row" id="filter_row">
            <s:message code="project.filter.placeholder" var="filterPH"/>
            <div class="col-sm-10">
              <input type="text" id="projectfilter" class="form-control" placeholder="${filterPH}" style="width: 100%">
            </div>
            <div class="col-sm-2">
              <select id="filterSelect" class="form-control" style="width: 100%">
                <option value="project"><s:message code="project.panel.select.project"/></option>
                <option value="study"><s:message code="project.panel.select.study"/></option>
              </select>
            </div>
          </div>
        </c:if>
        <div class="panel-group">
          <c:forEach items="${CProjectForm}" var="form" varStatus="loop">
            <c:choose>
              <c:when test="${principal.user.hasRole('PROJECT_ADMIN', form.project.id, false)}">
                <c:set var="pRole" value="panel-primary"/>
              </c:when>
              <c:when test="${principal.user.hasRole('PROJECT_WRITER', form.project.id, false)}">
                <c:set var="pRole" value="panel-success"/>
              </c:when>
              <c:when test="${principal.user.hasRole('PROJECT_READER', form.project.id, false)}">
                <c:set var="pRole" value="panel-info"/>
              </c:when>
              <c:when
                  test="${principal.user.hasRole('DS_READER', form.project.id, false) || principal.user.hasRole('DS_WRITER', form.project.id, false)}">
                <c:set var="pRole" value="panel-warning"/>
              </c:when>
              <c:when test="${principal.user.hasRole('ADMIN')}">
                <c:set var="pRole" value="panel-red"/>
              </c:when>
              <c:otherwise>
                <c:set var="pRole" value="panel-default"/>
              </c:otherwise>
            </c:choose>
            <div class="panel <s:message text="${pRole}"/> projectpanel">
              <c:choose>
                <c:when test="${pRole eq 'panel-default'}">
                  <div class="panel-heading projectTitle">
                    <strong><s:message text="${form.project.title}"/>&nbsp;<s:message
                        code="project.role.pending.info"/></strong>
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="panel-heading projectTitle"
                       onclick="location.href='<c:url value="project/${form.project.id}"/>';" style="cursor: pointer;">
                    <strong><s:message text="${form.project.title}"/></strong>
                  </div>
                </c:otherwise>
              </c:choose>
              <c:set var="firstIn" value=""/>
              <div class="panel-body">
                <div class="row" style="padding-bottom: 10px;">
                  <div class="col-xs-1 col-sm-2 text-align-right">
                    <strong><s:message code="project.panel.project.description"/></strong>
                  </div>
                  <div class="col-xs-12 col-sm-10 projectDescription">
                    <c:choose>
                      <c:when test="${not empty form.project.description}">
                        <s:message text="${form.project.description}"/>
                      </c:when>
                      <c:otherwise>
                        <s:message code="project.panel.no.description"/>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
                <div class="row" style="padding-bottom: 10px;">
                  <div class="col-xs-1 col-sm-2 text-align-right">
                    <strong><s:message code="project.panel.project.researcher"/></strong>
                  </div>
                  <div class="col-xs-12 col-sm-10 projectResearcher">
                    <c:choose>
                      <c:when test="${not empty form.contributors && form.contributors.size() > 0 }">
                        <c:forEach items="${form.contributors}" var="contri" varStatus="contriloop">
                          <s:message text="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}"/>
                          <c:if test="${contriloop.index < form.contributors.size()-1}">
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
                <!-- studies -->
                <div class="row" style="padding-bottom: 10px;">
                  <div class="col-xs-1 col-sm-2 text-align-right" style="padding-top: 12px;">
                    <strong><s:message code="project.panel.project.studies"/></strong>
                  </div>
                  <div class="col-xs-12 col-sm-10">
                    <c:choose>
                      <c:when test="${empty form.studies}">
                        <div class="marginTop2">
                          <s:message code="project.panel.no.studies"/>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <div class="panel panel-default">
                          <div class="panel-heading" data-toggle="collapse" data-target="#panel_coll_${loop.count}"
                               style="cursor: pointer;">
                            <strong><s:message code="project.panel.project.studies.showhide"/></strong>
                          </div>
                          <div class="panel-collapse collapse" id="panel_coll_${loop.count}">
                            <div class="panel-group">
                              <div class="panel-body">
                                <c:forEach items="${form.studies}" var="study" varStatus="loop2">
                                  <c:set var="sRole" value="${pRole}"/>
                                  <c:if test="${pRole eq 'panel-default' || pRole eq 'panel-warning'}">
                                    <c:choose>
                                      <c:when test="${principal.user.hasRole('DS_READER', study.id, true)}">
                                        <c:set var="sRole" value="panel-success"/>
                                      </c:when>
                                      <c:when test="${principal.user.hasRole('DS_WRITER', study.id, true)}">
                                        <c:set var="sRole" value="panel-info"/>
                                      </c:when>
                                    </c:choose>
                                  </c:if>
                                  <div class="bg_hoover panel  <s:message text="${sRole}"/> studyFilter"
                                       onclick="location.href='<c:url value="project/${form.project.id}/study/${study.id}"/>';"
                                       style="cursor: pointer;">
                                    <div class="panel-heading studyTitleFilter">
                                      <strong><s:message text="${study.title}"/></strong>
                                    </div>
                                    <div class="panel-body">
                                      <div class="row" style="padding-bottom: 10px;">
                                        <div class="col-xs-1 col-sm-2 text-align-right">
                                          <strong><s:message code="project.panel.project.description"/></strong>
                                        </div>
                                        <div class="col-xs-12 col-sm-10 studyDescriptionFilter">
                                          <c:choose>
                                            <c:when test="${not empty study.sAbstract}">
                                              <s:message text="${study.sAbstract}"/>
                                            </c:when>
                                            <c:otherwise>
                                              <s:message code="study.panel.no.description"/>
                                            </c:otherwise>
                                          </c:choose>
                                        </div>
                                      </div>
                                      <div class="row" style="padding-bottom: 10px;">
                                        <div class="col-xs-1 col-sm-2 text-align-right">
                                          <strong><s:message code="project.panel.project.researcher"/></strong>
                                        </div>
                                        <div class="col-xs-12 col-sm-10 studyResearcherFilter">
                                          <c:choose>
                                            <c:when
                                                test="${not empty study.contributors && study.contributors.size() > 0 }">
                                              <c:forEach items="${study.contributors}" var="contri"
                                                         varStatus="contriloop">
                                                <s:message
                                                    text="${contri.title}&nbsp;${contri.firstName}&nbsp;${contri.lastName}"/>
                                                <c:if test="${contriloop.index < study.contributors.size()-1}">
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
                                  </div>
                                </c:forEach>
                              </div>
                            </div>
                          </div>
                        </div>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
                <!-- sharedUser -->
                <div class="row">
                  <div class="col-xs-1 col-sm-2 text-align-right" style="padding-top: 10px;">
                    <strong><s:message code="project.panel.project.shared"/></strong>
                  </div>
                  <div class="col-xs-12 col-sm-10">
                    <c:choose>
                      <c:when test="${empty form.sharedUser or fn:length(form.sharedUser) eq 1}">
                        <div class="marginTop2">
                          <s:message code="project.panel.not.shared"/>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <div class="panel panel-default">
                          <div class="panel-heading" data-toggle="collapse" data-target="#panel_user_${loop.count}"
                               style="cursor: pointer;">
                            <strong><s:message code="project.panel.project.shared.showhide"/></strong>
                          </div>
                          <div class="panel-collapse collapse" id="panel_user_${loop.count}">
                            <div class="list-group">
                              <c:forEach items="${form.sharedUser}" var="user" varStatus="loop2">
                                <div class="list-group-item">
                                  <div class="row">
                                    <div class="col-xs-6 col-sm-6" style="padding-top: 10px">
                                      <s:message text="${user.firstName} ${user.lastName}"/>
                                      (<a href="mailto:${user.email}">${user.email}</a>)
                                    </div>
                                    <div class="col-xs-6 col-sm-6">
                                      <div class="list-group" style="margin-bottom: 0px; margin-top:">
                                        <c:forEach items="${user.globalRoles}" var="userRole">
                                          <c:if test="${userRole.type ne 'REL_ROLE'}">
                                            <div class="list-group-item">
                                              <s:message code="roles.${userRole.type}"/>
                                              <c:if
                                                  test="${userRole.type eq 'PROJECT_ADMIN' and form.project.ownerId == user.id}">
                                                <span class="reddot"></span>
                                              </c:if>
                                              <c:if
                                                  test="${userRole.type eq 'DS_READER' or userRole.type eq 'DS_WRITER'}">
                                                <c:forEach items="${form.studies}" var="study">
                                                  <c:if test="${userRole.studyId eq study.id}">
                                                    <s:message text="&quot; ${study.title}&quot; "/>
                                                  </c:if>
                                                </c:forEach>
                                              </c:if>
                                            </div>
                                          </c:if>
                                        </c:forEach>
                                      </div>
                                    </div>
                                  </div>
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
                <div class="row">
                  <div class="col-lg-10 col-md-9 col-sm-7 col-xs-12 text-align-left">
                    <javatime:format value="${form.project.created}" style="MS" var="strDate"/>
                    <s:message code="panel.created" arguments="${strDate}" htmlEscape="false" argumentSeparator=";"/>
                  </div>
                  <div class="col-lg-2 col-md-3 col-sm-5 col-xs-12 text-align-right">
                    <div class="btn btn-info btn-xs"
                         onclick="location.href='<c:url value="project/${form.project.id}/"/>';" style="cursor: pointer;">
                      <s:message code="panel.btn.view.edit"/>
                    </div>
                  </div>
                </div>
                <c:set var="shared" value=""/>
              </div>
            </div>
          </c:forEach>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp" %>