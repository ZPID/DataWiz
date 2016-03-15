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
        <div class="form-group">
          <div class="col-sm-12">
            <ul class="list-group">
              <c:forEach items="${ProjectForm.sharedUser}" var="user" varStatus="uState">
                <li class="list-group-item">
                  <div class="row">
                    <div class="col-sm-2">
                      <c:if test="${not empty user.title}">
                        <c:out value="${user.title}" />&nbsp;
                        </c:if>
                      <c:if test="${not empty user.firstName}">
                        <c:out value="${user.firstName}" />&nbsp;
                        </c:if>
                      <c:out value="${user.lastName}" />
                    </div>
                    <div class="col-sm-2">
                      <c:out value="${user.email}" />
                    </div>
                    <div class="col-sm-8">
                      <c:if test="${user.id eq principal.user.id || principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                        <ul class="list-group">
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
                                    <div class="col-sm-12">
                                      <div class="col-sm-11">
                                        <s:message code="roles.${role.type}" arguments="${studyTitle}" />
                                      </div>
                                      <div class="col-sm-1">
                                        <c:if
                                          test="${!(user.id eq principal.user.id) && principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                                          <a
                                            href="<c:url value="/access/${ProjectForm.project.id}/delete/${user.id}/${role.roleId}/${role.studyId}" />"
                                            class="btn btn-danger btn-sm" style="vertical-align: bottom;"> <span
                                            class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                                          </a>
                                        </c:if>
                                      </div>
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
              <c:if test="${principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                <!-- add role to excisting user -->
                <li class="list-group-item">
                  <div class="row">
                    <div class="col-sm-4">
                      <sf:select path="newRole.userId" class="form-control" id="accessMailChange" onchange="showHideNewRole();">
                        <sf:option value="0">Bitte wählen</sf:option>
                        <sf:options items="${ProjectForm.sharedUser}" itemLabel="email" itemValue="id" />
                      </sf:select>
                    </div>
                    <div class="col-sm-3">
                      <sf:select path="newRole.type" class="form-control" id="accessChangeSel" onchange="showHideNewRole();">
                        <sf:option value="0">Bitte wählen</sf:option>
                        <c:forEach items="${ProjectForm.roleList}" var="role" varStatus="roleLoop">
                          <sf:option value="${role}">
                            <s:message code="roles.${role}"></s:message>
                          </sf:option>
                        </c:forEach>
                      </sf:select>
                    </div>
                    <div class="col-sm-4">
                      <sf:select path="newRole.studyId" class="form-control" id="accessChange">
                        <sf:option value="0">Bitte wählen</sf:option>
                        <sf:options items="${ProjectForm.studies}" itemLabel="title" itemValue="id" />
                      </sf:select>
                    </div>
                    <div class="col-sm-1">
                      <c:if
                        test="${!(user.id eq principal.user.id) && principal.user.hasProjectRole('PROJECT_ADMIN', ProjectForm.project.id)}">
                        <sf:button type="submit" name="addRole" class="btn btn-success">
                          <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
                        </sf:button>
                      </c:if>
                    </div>
                  </div>
                </li>
                <!-- add new user to project -->
                <li class="list-group-item">
                  <div class="input-group">
                    <sf:input path="" class="form-control" placeholder="Enter Email" />
                    <span class="input-group-btn"> <sf:button class="btn btn-success" type="submit" name="addUser">Invite</sf:button>
                    </span>
                  </div>
                </li>
              </c:if>
            </ul>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>
