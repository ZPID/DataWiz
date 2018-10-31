<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp" %>
    <%@ include file="templates/submenu.jsp" %>
    <div class="content-padding">
      <div class="page-header">
        <div class="row">
          <div class="col-xs-8 col-sm-9">
            <h4>
              <s:message code="record.overview.headline"/>
            </h4>
          </div>
          <c:if
              test="${principal.user.hasRole('PROJECT_ADMIN', StudyForm.project.id, false) or
                            principal.user.hasRole('PROJECT_WRITER', StudyForm.project.id, false) or 
                            principal.user.hasRole('ADMIN') or 
                            principal.user.hasRole('DS_WRITER', StudyForm.study.id, true)}">
            <div class="col-xs-4 col-sm-3 text-align-right">
              <a href='<c:url value="record/"/>' class="btn btn-success btn-sm"><s:message
                  code="record.create.new.record"/></a>
            </div>
          </c:if>

        </div>
        <div>
          <s:message code="record.overview.info"/>
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp" %>
      <div class="panel-group">
        <c:forEach items="${StudyForm.records}" var="rec">
          <div class="panel panel-primary">
            <div class="panel-heading projectContentClick" onclick="location.href='<c:url value="record/${rec.id}"/>';">
              <div class="row">
                <div class="col-sm-12">
                  <s:message text="${rec.recordName}"/>
                </div>
              </div>
            </div>
            <div class="panel-body">
              <form class="form-horizontal">
                <div class="form-group">
                  <div class="control-label col-sm-2">
                    <strong><s:message code="study.records.description"/></strong>
                  </div>
                  <div class="col-sm-10 margin-top-7">
                    <s:message text="${rec.description}"/>
                  </div>
                </div>
                <c:choose>
                  <c:when test="${not empty rec.changed}">
                    <div class="form-group">
                      <div class="control-label col-sm-2">
                        <strong><s:message code="study.records.version.info"/></strong>
                      </div>
                      <javatime:format value="${rec.changed}" style="MS" var="strDate"/>
                      <div class="col-sm-10 margin-top-7">
                        <s:message code="panel.last.commit" arguments="${strDate};${rec.changedBy}" htmlEscape="false" argumentSeparator=";"/>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="control-label col-sm-2">
                        <strong><s:message code="study.records.version.changelog"/></strong>
                      </div>
                      <div class="col-sm-10 margin-top-7">
                        <s:message text="${rec.changeLog}"/>
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="form-group">
                      <div class="col-sm-offset-2 col-sm-10">
                        <s:message code="study.records.version.noinfo"/>
                      </div>
                    </div>
                  </c:otherwise>
                </c:choose>
              </form>
            </div>
            <div class="panel-footer">
              <javatime:format value="${rec.created}" style="MS" var="strDate"/>
              <div class="row">
                <div class="col-lg-10 col-md-9 col-sm-7 col-xs-12 text-align-left">
                  <s:message code="record.first.submit" arguments="${strDate};${rec.createdBy}" htmlEscape="false" argumentSeparator=";"/>
                </div>
                <div class="col-lg-2 col-md-3 col-sm-5 col-xs-12 text-align-right">
                  <div class="btn btn-info btn-xs projectContentClick" onclick="location.href='<c:url value="record/${rec.id}"/>';">
                    <s:message code="panel.btn.view.edit"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp" %>
