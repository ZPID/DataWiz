<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="login-card">
      <div class="login-form">
        <c:url var="projectUrl" value="/project" />
        <sf:form action="${projectUrl}" commandName="ProjectForm" StyleClass="form-horizontal">
          <div class="panel-group" id="accordion">
            <c:forEach items="${ProjectForm.projects}" var="project" varStatus="loop">
              <div
                class="panel <c:out value="${project.projectRole.type eq 'PROJECT_ADMIN' ? 'panel-primary' : 
                                             project.projectRole.type eq 'PROJECT_WRITER' ? 'panel-info' : 
                                             project.projectRole.type eq 'PROJECT_READER' ? 'panel-warning' : 'panel-danger'}"/>">
                <div class="panel-heading accordion-toggle" data-toggle="collapse"
                  data-target="#panel_coll_${loop.count}" data-parent="#accordion">
                  <div class="row">
                    <div class="col-xs-8">
                      <h4 class="panel-title">
                        <c:out value="${project.name}"></c:out>
                      </h4>
                      (Hans Peter Müller, Joachin Test, Klaus Meier)
                    </div>
                    <div class="col-xs-3"></div>
                    <div class="col-xs-1">
                      <i class="indicator glyphicon glyphicon-chevron-down  pull-right"></i>
                    </div>
                  </div>
                </div>
                <div class="panel-collapse collapse" id="panel_coll_${loop.count}">
                  <div class="panel-body">
                    <c:out value="${project.description}"></c:out>
                  </div>
                </div>
                <div class="panel-footer">
                  <div class="row">
                    <div class="col-xs-6">
                      <span class="label label-success" data-toggle="tooltip" data-placement="top"
                        title="<c:out value="${project.name}"></c:out>">Veröffentlicht</span> <span
                        class="label label-default" data-toggle="tooltip" data-placement="top"
                        title="<c:out value="${project.name}" />">geteilt (4)</span> <a href="project/${project.id}"
                        class="label
                      label-primary">Bearbeiten</a>
                    </div>
                    <div class="col-xs-6">last update 12.03.2015 - 15:03 by sjkdkdkd@asdad.de</div>
                  </div>
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