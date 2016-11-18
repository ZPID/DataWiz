<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="dataset.import.report.headline" />
        </h4>
        <div>
          <s:message code="dataset.import.report.info" />
        </div>
      </div>
      <c:url var="accessUrl"
        value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.records[0].id}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" role="form">
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <c:if test="${not empty StudyForm.importMatrix}">
          <div class="form-group">
            <div class="col-sm-12">
              <sf:label path="importMatrix">
                <s:message code="dataset.import.report.raw.table" />
              </sf:label>
              <div class="pre-xy-scrollable">
                <table class="table table-striped table-bordered table-condensed matrixtable">
                  <tbody>
                    <c:forEach items="${StudyForm.importMatrix}" var="row" varStatus="rowNum">
                      <tr>
                        <td><s:message text="${rowNum.index+1}" /></td>
                        <c:forEach items="${row}" var="value">
                          <td><s:message text="${value}" /></td>
                        </c:forEach>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
              <s:message code="dataset.import.report.raw.table.help" var="appresmess" />
              <%@ include file="templates/helpblock.jsp"%>
            </div>
          </div>
        </c:if>
        <br />
        <c:if test="${not empty StudyForm.warnings}">
          <div class="form-group">
            <div class="col-sm-12">
              <sf:label path="warnings">
                <s:message code="dataset.import.report.warnings" />
              </sf:label>
              <div class="alert alert-warning margin-bottom-0">
                <c:forEach items="${StudyForm.warnings}" var="warning">
                  <div>
                    <s:message text="${warning}" />
                  </div>
                </c:forEach>
              </div>
              <s:message code="dataset.import.report.warnings.help" var="appresmess" />
              <%@ include file="templates/helpblock.jsp"%>
            </div>
          </div>
        </c:if>
        <c:if test="${not empty StudyForm.errors}">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="alert alert-danger" role="alert">
                <h5>Parsing Errors:</h5>
                <c:forEach items="${StudyForm.errors}" var="error">
                  <div>
                    <s:message text="${error}" />
                  </div>
                </c:forEach>
              </div>
            </div>
          </div>
        </c:if>
        <c:if test="${not empty StudyForm.record.variables}">
          <div class="form-group">
            <div class="col-sm-12">
              <sf:label path="record.variables">
                <s:message code="dataset.import.report.codebook" />
              </sf:label>
              <div class="pre-xy-scrollable">
                <table class="table table-striped table-bordered">
                  <thead>
                    <tr>
                      <th><s:message code="dataset.import.report.codebook.name" /></th>
                      <th><s:message code="dataset.import.report.codebook.type" /></th>
                      <th><s:message code="dataset.import.report.codebook.width" /></th>
                      <th><s:message code="dataset.import.report.codebook.dec" /></th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach items="${StudyForm.record.variables}" var="var">
                      <tr>
                        <td><strong><s:message text="${var.name}" /></strong></td>
                        <td><s:message code="spss.type.${var.type}" /></td>
                        <td><s:message text="${var.width}" /></td>
                        <td><s:message text="${var.decimals}" /></td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
              <s:message code="dataset.import.report.codebook.help" var="appresmess" />
              <%@ include file="templates/helpblock.jsp"%>
            </div>
          </div>
        </c:if>
        <c:if test="${not empty StudyForm.record.variables && not empty StudyForm.record.dataMatrix}">
          <div class="form-group">
            <div class="col-sm-12">
              <sf:label path="record.variables">
                <s:message code="dataset.import.report.result.table" />
              </sf:label>
              <div class="pre-xy-scrollable">
                <table class="table table-striped table-bordered table table-condensed matrixtable">
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
              <s:message code="dataset.import.report.result.table.help" var="appresmess" />
              <%@ include file="templates/helpblock.jsp"%>
            </div>
          </div>
        </c:if>
        <!-- Check against previous version -->
        <c:if test="${not empty StudyForm.previousRecordVersion.variables}">
          <div class="form-group">
            <div class="col-sm-12">
              <sf:label path="record.variables">
                <%-- <s:message code="dataset.import.report.codebook" /> --%>
                Check das ab!
              </sf:label>
              <ul class="list-group margin-bottom-0">
                <c:set var="newList" value="${StudyForm.record.variables}" />
                <c:set var="oldList" value="${StudyForm.previousRecordVersion.variables}" />
                <c:forEach items="${oldList}" var="var" varStatus="loop">
                  <li class="list-group-item">
                    <div class="row">
                      <div class="col-sm-12">
                        <div class="pre-xy-scrollable">
                          <table class="table table-striped table-bordered margin-bottom-0">
                            <thead>
                              <tr>
                                <td colspan="3">Importierte Metadaten</td>
                                <td colspan="2">Erweiterte Metadaten</td>
                              </tr>
                              <tr>
                                <th width="20px">Position</th>
                                <th><s:message code="dataset.import.report.codebook.name" /></th>
                                <th><s:message code="dataset.import.report.codebook.type" /></th>
                                <th><s:message code="dataset.import.report.codebook.width" /></th>
                                <th><s:message code="dataset.import.report.codebook.dec" /></th>
                              </tr>
                            </thead>
                            <tbody>
                              <tr>
                                <td rowspan="2"><s:message text="${loop.count}" /></td>
                                <td><strong><s:message text="${var.name}" /></strong></td>
                                <td><s:message code="spss.type.${var.type}" /></td>
                                <td><s:message text="${var.width}" /></td>
                                <td><s:message text="${var.decimals}" /></td>
                              </tr>
                              <c:choose>
                                <c:when test="${loop.count <=  fn:length(newList)}">
                                  <tr class="${StudyForm.compList[loop.count-1].bootstrapItemColor}">
                                    <td><strong><s:message text="${newList[loop.count-1].name}" /></strong></td>
                                    <td><s:message code="spss.type.${newList[loop.count-1].type}" /></td>
                                    <td><s:message text="${newList[loop.count-1].width}" /></td>
                                    <td><s:message text="${newList[loop.count-1].decimals}" /></td>
                                  </tr>
                                </c:when>
                                <c:otherwise>
                                  <tr class="warning">
                                    <td colspan="4"><strong><s:message code="warning.var.removed"
                                          arguments="${var.name},${var.type}" /></strong></td>
                                  </tr>
                                </c:otherwise>
                              </c:choose>
                            </tbody>
                          </table>
                        </div>
                      </div>
                      <div class="col-sm-12">
                        <strong><s:message text="${StudyForm.compList[loop.count-1].message}" /></strong>
                      </div>
                    </div>
                  </li>
                </c:forEach>
                <c:if test="${fn:length(newList) > fn:length(oldList)}">
                  <c:forEach items="${newList}" var="var" varStatus="loop">
                    <c:if test="${loop.count > fn:length(oldList)}">
                      <li class="list-group-item">
                        <div class="row">
                          <div class="col-sm-10">
                            <div class="pre-xy-scrollable">
                              <table class="table table-striped table-bordered">
                                <thead>
                                  <tr>
                                    <th width="20px">Position</th>
                                    <th><s:message code="dataset.import.report.codebook.name" /></th>
                                    <th><s:message code="dataset.import.report.codebook.type" /></th>
                                    <th><s:message code="dataset.import.report.codebook.width" /></th>
                                    <th><s:message code="dataset.import.report.codebook.dec" /></th>
                                  </tr>
                                </thead>
                                <tbody>
                                  <tr>
                                    <td rowspan="2"><s:message text="${loop.count}" /></td>
                                    <td><strong><s:message text="${var.name}" /></strong></td>
                                    <td><s:message code="spss.type.${var.type}" /></td>
                                    <td><s:message text="${var.width}" /></td>
                                    <td><s:message text="${var.decimals}" /></td>
                                  </tr>
                                  <tr class="${StudyForm.compList[loop.count-1].bootstrapItemColor}">
                                    <td colspan="4"><strong><s:message code="warning.var.removed"
                                          arguments="${var.name},${var.type}" /></strong></td>
                                  </tr>
                                </tbody>
                              </table>
                            </div>
                          </div>
                          <div class="col-sm-2">
                            <s:message text="${StudyForm.compList[loop.count-1].message}" />
                            <br />
                            <s:message text="${StudyForm.compList[loop.count-1].varStatus}" />
                          </div>
                        </div>
                      </li>
                    </c:if>
                  </c:forEach>
                </c:if>
              </ul>
              <s:message code="dataset.import.report.codebook.help" var="appresmess" />
              <%@ include file="templates/helpblock.jsp"%>
            </div>
          </div>
        </c:if>
        <div class="form-group">
          <div class="col-sm-offset-9 col-sm-3">
            <a href="${accessUrl}" class="btn btn-default"> <s:message code="gen.reset" />
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