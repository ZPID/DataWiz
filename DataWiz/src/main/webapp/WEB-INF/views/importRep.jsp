<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="study.create.basis.headline" />
        </h4>
        <div>
          <s:message code="study.create.basis.info" />
        </div>
      </div>
      <c:url var="accessUrl"
        value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.records[0].id}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" role="form">
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <div class="pre-scrollable">
          <table class="table table-striped table-bordered">
            <tbody>
              <c:forEach items="${StudyForm.importMatrix}" var="row">
                <tr>
                  <c:forEach items="${row}" var="value">
                    <td><s:message text="${value}" /></td>
                  </c:forEach>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <br />
        <c:if test="${not empty varNameDouble}">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="alert alert-warning" role="alert">
                <h5>Parsing Warnings:</h5>
                <c:forEach items="${varNameDouble}" var="doubleStr">
                  <div>
                    <s:message text="${doubleStr}" />
                  </div>
                </c:forEach>
              </div>
            </div>
          </div>
        </c:if>
        <div class="pre-scrollable">
          <table class="table table-striped table-bordered">
            <thead>
              <tr>
                <th>Name</th>
                <th>Typ</th>
                <th>Breite</th>
                <th>Dezimalstellen</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${StudyForm.record.variables}" var="var">
                <tr>
                  <td><s:message text="${var.name}" /></td>
                  <td><s:message text="${var.type}" /></td>
                  <td><s:message text="${var.width}" /></td>
                  <td><s:message text="${var.decimals}" /></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <br />
        <div class="pre-scrollable">
          <table class="table table-striped table-bordered">
            <thead>
              <tr>
                <c:forEach items="${StudyForm.record.variables}" var="var">
                  <th><s:message text="${var.name}" /></th>
                </c:forEach>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${StudyForm.record.dataMatrix}" var="row">
                <tr>
                  <c:forEach items="${row}" var="value">
                    <td><s:message text="${value}" /></td>
                  </c:forEach>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-9 col-sm-3">
              <a href="${accessUrl}" class="btn btn-default">
                <s:message code="gen.reset" />
              </a>
              <button type="submit" class="btn btn-success" name="saveWithNewCodeBook">
                <s:message code="gen.submit" />
              </button>
              <button type="submit" class="btn btn-success" name="saveWithOldCodeBook">
                <s:message code="gen.submit" />
              </button>
            </div>
          </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>