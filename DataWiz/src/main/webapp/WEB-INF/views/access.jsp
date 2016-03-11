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
            <div class="table-responsive">
              <table class="table table-condensed table-hover">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Zugriffsrechte</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach items="${ProjectForm.sharedUser}" var="user" varStatus="uState">
                    <tr>
                      <td class="col-sm-3"><c:if test="${not empty user.title}">
                          <c:out value="${user.title}" />&nbsp;
                        </c:if> <c:if test="${not empty user.firstName}">
                          <c:out value="${user.firstName}" />&nbsp;
                        </c:if> <c:out value="${user.lastName}" /></td>
                      <td class="col-sm-3"><c:out value="${user.email}" /></td>
                      <td class="col-sm-6">
                        <ul class="list-group">
                          <c:forEach items="${user.globalRoles}" var="role" varStatus="rState">
                            <c:forEach items="${ProjectForm.studies}" var="cStudy">
                              <c:if test="${cStudy.id eq role.studyId}">
                                <s:message text="${cStudy.title}" var="studyTitle" />
                              </c:if>
                            </c:forEach>
                            <li class="list-group-item">
                              <div class="row">
                                <div class="col-sm-12">
                                  <div class="col-sm-10">
                                    <s:message code="roles.${role.type}" arguments="${studyTitle}" />
                                  </div>
                                  <div class="col-sm-1">
                                    <c:if test="${!(role.userId eq principal.user.id)}">
                                      <a href="" class="label label-danger" style="vertical-align: bottom;">x</a>
                                    </c:if>
                                  </div>
                                </div>
                              </div>
                            </li>
                          </c:forEach>
                        </ul>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>
