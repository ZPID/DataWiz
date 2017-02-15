<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <div class="row">
          <div class="col-sm-10">
            <h4>
              <s:message code="record.overview.headline" />
            </h4>
          </div>
          <div class="col-sm-2 text-align-right">
            <a href='<c:url value="record/"/>' class="btn btn-success"><s:message code="record.create.new.record" /></a>
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
                      <c:set var="date" value="${fn:split(rec.changed, 'T')}" />
                      <fmt:parseDate value="${date[0]}/${date[1]}" pattern="yyyy-MM-dd/HH:mm" var="parsedDate"
                        type="date" />
                      <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy - HH:mm" var="strDate" />
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
              <c:set var="date" value="${fn:split(rec.created, 'T')}" />
              <fmt:parseDate value="${date[0]}/${date[1]}" pattern="yyyy-MM-dd/HH:mm:ss" var="parsedDate" type="date" />
              <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy - HH:mm:ss" var="strDate" />
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
