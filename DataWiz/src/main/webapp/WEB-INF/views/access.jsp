<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.access.headline" />
        </h4>
        <div>
          <s:message code="project.access.info" />
        </div>
      </div>
      <c:url var="accessUrl" value="/access/${ProjectForm.project.id}" />
      <sf:form action="${accessUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <div class="form-group form-group-clean">
          <div class="col-md-12">
            <%@ include file="templates/message.jsp"%>
            <div class="panel panel-default">
              <div class="panel-heading">
                <s:message code="roles.edit.role.head" />
              </div>
              <div class="panel-body">
                <ul class="list-group">
                  <!-- existing project users -->
                  <c:forEach items="${ProjectForm.sharedUser}" var="user" varStatus="uState">
                    <li class="list-group-item">
                      <div class="row">
                        <div class="col-md-2 col-xs-6">
                          <c:if test="${not empty user.title}">
                            <sf:label path="sharedUser[${uState.index}].title">
                              <s:message text="${user.title}" />&nbsp;
                        </sf:label>
                          </c:if>
                          <c:if test="${not empty user.firstName}">
                            <sf:label path="sharedUser[${uState.index}].firstName">
                              <s:message text="${user.firstName}" />&nbsp;
                        </sf:label>
                          </c:if>
                          <c:if test="${not empty user.lastName}">
                            <sf:label path="sharedUser[${uState.index}].lastName">
                              <s:message text="${user.lastName}" />&nbsp;
                        </sf:label>
                          </c:if>
                        </div>
                        <div class="col-md-2 col-xs-6">
                          <s:message text="${user.email}" />
                        </div>
                        <div class="col-md-8 col-xs-12">
                          <c:if
                            test="${user.id eq principal.user.id || principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                            <ul class="list-group">
                              <!-- user roles -->
                              <c:forEach items="${user.globalRoles}" var="role" varStatus="rState">
                                <c:choose>
                                  <c:when test="${!(role.type eq 'REL_ROLE')}">
                                    <c:forEach items="${ProjectForm.studies}" var="cStudy">
                                      <c:if test="${cStudy.id eq role.studyId}">
                                        <s:message text="${cStudy.title}" var="studyTitle" />
                                      </c:if>
                                    </c:forEach>
                                    <li class="list-group-item">
                                      <div class="row">
                                        <div
                                          class="col-md-10 col-xs-10 <s:message text="${ProjectForm.project.ownerId eq user.id ? 'reddot' : ''}" />">
                                          <b><s:message code="roles.${role.type}" /> <c:if
                                              test="${role.type eq 'DS_WRITER' || role.type eq 'DS_READER'}">
                                          &nbsp;&quot;<s:message text="${studyTitle}" />&quot;
                                        </c:if></b>
                                        </div>
                                        <div class="col-md-2 col-xs-2">
                                          <c:if
                                            test="${!(user.id eq ProjectForm.project.ownerId) &&!(user.id eq principal.user.id) 
                                                    && principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                                            <a
                                              href="<c:url value="/access/${ProjectForm.project.id}/delete/${user.id}/${role.roleId}/${role.studyId}" />"
                                              class="btn btn-danger btn-xs" style="vertical-align: bottom;"> <s:message
                                                code="roles.del.role" />
                                            </a>
                                          </c:if>
                                        </div>
                                      </div>
                                    </li>
                                  </c:when>
                                  <c:when test="${(role.type eq 'REL_ROLE') && user.globalRoles.size() == 1}">
                                    <li class="list-group-item list-group-item-warning"><s:message code="roles.delete.user" /> <a
                                      href="<c:url value="/access/${ProjectForm.project.id}/deleteUser/${user.id}" />"><s:message
                                          code="roles.delete.user.link" /></a></li>
                                  </c:when>
                                </c:choose>
                              </c:forEach>
                            </ul>
                          </c:if>
                        </div>
                      </div>
                    </li>
                  </c:forEach>
                  <!--  -->
                  <c:if test="${principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id) || principal.user.hasRole('ADMIN')}">
                    <!-- add role to existing user -->
                    <li class="list-group-item"><sf:label path="delMail">
                        <s:message code="roles.add.role.label" />
                      </sf:label>
                      <div class="row">
                        <div class="col-md-3">
                          <sf:select path="newRole.userId" class="form-control" id="accessMailChange" onchange="showHideNewRole();">
                            <sf:option value="0">
                              <s:message code="gen.select" />
                            </sf:option>
                            <sf:options items="${ProjectForm.sharedUser}" itemLabel="email" itemValue="id" />
                          </sf:select>
                        </div>
                        <div class="col-md-3">
                          <sf:select path="newRole.type" class="form-control" id="accessChangeSel" onchange="showHideNewRole();">
                            <sf:option value="0">
                              <s:message code="gen.select" />
                            </sf:option>
                            <c:forEach items="${ProjectForm.roleList}" var="role" varStatus="roleLoop">
                              <sf:option value="${role}">
                                <s:message code="roles.${role}"></s:message>
                              </sf:option>
                            </c:forEach>
                          </sf:select>
                        </div>
                        <div class="col-md-3">
                          <sf:select path="newRole.studyId" class="form-control" id="accessChange">
                            <sf:option value="0">
                              <s:message code="gen.select" />
                            </sf:option>
                            <sf:options items="${ProjectForm.studies}" itemLabel="title" itemValue="id" />
                          </sf:select>
                        </div>
                        <div class="col-md-3">
                          <c:if
                            test="${!(user.id eq principal.user.id) && principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                            <sf:button type="submit" name="addRole" class="btn btn-success">
                              <s:message code="roles.add.role" />
                            </sf:button>
                          </c:if>
                        </div>
                      </div></li>
                  </c:if>
                </ul>
                <s:message code="roles.edit.role.help" var="appresmess" /><%@ include file="templates/helpblock.jsp"%>
              </div>
            </div>
            <c:if test="${principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id) || principal.user.hasRole('ADMIN')}">
              <!-- add new user to project -->
              <div class="panel panel-default">
                <div class="panel-heading">
                  <s:message code="roles.add.user.head" />
                </div>
                <div class="panel-body">
                  <div class="row">
                    <div class="col-md-12 col-xs-12">
                      <div class="col-md-6 col-xs-12">
                        <sf:label path="delMail">
                          <s:message code="roles.add.user" />
                        </sf:label>
                        <div class="input-group">
                          <sf:input path="delMail" class="form-control" placeholder="Enter Email" />
                          <span class="input-group-btn"> <sf:button class="btn btn-success" type="submit" name="addUser">
                              <s:message code="gen.invite" />
                            </sf:button>
                          </span>
                        </div>
                      </div>
                      <!-- pending Invites -->
                      <div class="col-md-6 col-xs-12">
                        <sf:label path="delMail">
                          <s:message code="roles.add.pending" />
                        </sf:label>
                        <ul class="list-group">
                          <c:if test="${fn:length(ProjectForm.pendingMails) gt 0}">
                            <c:forEach items="${ProjectForm.pendingMails}" var="pendingMail">
                              <li class="list-group-item">
                                <div class="row">
                                  <div class="col-md-5">
                                    <s:message text="${pendingMail}" />
                                  </div>
                                  <div class="col-md-4">
                                    <a class="btn btn-success btn-xs"
                                      href="<c:url value="/access/${ProjectForm.project.id}/resendInvite/${pendingMail}" />"><s:message
                                        code="roles.add.resend" /></a>
                                  </div>
                                  <div class="col-md-3">
                                    <a class="btn btn-danger btn-xs"
                                      href="<c:url value="/access/${ProjectForm.project.id}/deleteInvite/${pendingMail}" />"><s:message
                                        code="roles.add.delete" /></a>
                                  </div>
                                </div>
                              </li>
                            </c:forEach>
                          </c:if>
                          <c:if test="${fn:length(ProjectForm.pendingMails) eq 0}">
                            <li class="list-group-item"><s:message code="roles.no.entry" /></li>
                          </c:if>
                        </ul>
                      </div>
                      <s:message code="roles.add.user.help" var="appresmess" /><%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                </div>
              </div>
            </c:if>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>
