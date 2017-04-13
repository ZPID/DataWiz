<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <div class="row">
          <div class="col-sm-12 text-align-right">
            <a href='<c:url value="record/"/>' class="btn btn-success btn-sm"><s:message
                code="record.create.new.record" /></a>
          </div>
          <div class="col-sm-12">
            <h4>
              <s:message code="record.overview.headline" />
            </h4>
          </div>
        </div>
        <div>
          <s:message code="record.overview.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <div class="panel-group">
        <c:forEach items="${StudyForm.records}" var="rec">
          <div class="panel panel-primary projectContentClick"
            onclick="location.href='<c:url value="record/${rec.id}"/>';">
            <div class="panel-heading">
              <div class="row">
                <div class="col-sm-12">
                  <s:message text="${rec.recordName}" />
                </div>
              </div>
            </div>
            <div class="panel-body">
              <form class="form-horizontal">
                <div class="form-group">
                  <label class="control-label col-sm-2" for="${rec.description}"><s:message
                      code="study.records.description" /></label>
                  <div class="col-sm-10 margin-top-7">
                    <s:message text="${rec.description}" />
                  </div>
                </div>
                <c:choose>
                  <c:when test="${not empty rec.changed}">
                    <div class="form-group">
                      <label class="control-label col-sm-2" for="${rec.changed}"><s:message
                          code="study.records.version.info" /></label>
                      <javatime:format value="${rec.changed}" style="MS" var="strDate" />
                      <div class="col-sm-10 margin-top-7">
                        <s:message code="panel.last.commit" arguments="${strDate};${rec.changedBy}" htmlEscape="false"
                          argumentSeparator=";" />
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="control-label col-sm-2" for="${rec.changeLog}"><s:message
                          code="study.records.version.changelog" /></label>
                      <div class="col-sm-10 margin-top-7">
                        <s:message text="${rec.changeLog}" />
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="form-group">
                      <div class="col-sm-offset-2 col-sm-10">
                        <s:message code="study.records.version.noinfo" />
                      </div>
                    </div>
                  </c:otherwise>
                </c:choose>
              </form>
            </div>
            <div class="panel-footer">
              <javatime:format value="${rec.created}" style="MS" var="strDate" />
              <div class="row">
                <div class="col-sm-12 clearfix">
                  <div class="pull-right">
                    <s:message code="record.first.submit" arguments="${strDate};${rec.createdBy}" htmlEscape="false"
                      argumentSeparator=";" />
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
<%@ include file="templates/footer.jsp"%>
