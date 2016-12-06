<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:choose>
          <c:when test="${empty StudyForm.record.id}">
            <h4>
              <s:message code="study.create.basis.headline" />
            </h4>
            <div>
              <s:message code="study.create.basis.info" />
            </div>
          </c:when>
          <c:otherwise>
            <div class="row">
              <div class="col-sm-9">
                <h4>
                  <s:message code="study.edit.basis.headline" arguments="${StudyForm.record.recordName}" />
                </h4>
              </div>
              <div class="col-sm-3">
                <c:url var="accessUrl"
                  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/switchEditMode" />
                <c:choose>
                  <c:when test="${empty disStudyContent || disStudyContent eq 'disabled' }">
                    <a href="${accessUrl}" class="btn btn-success">Checkin</a>
                  </c:when>
                  <c:otherwise>
                    <a href="${accessUrl}" class="btn btn-danger">CheckOut</a>
                  </c:otherwise>
                </c:choose>
                <!-- Trigger the modal with a button -->
                <button type="button" class="btn btn-info" data-toggle="modal" data-target="#uploadModal">Upload
                  File</button>
                <button type="button" class="btn btn-warning" data-toggle="modal" data-target="#historyModal">History</button>
              </div>
            </div>
            <div>
              <s:message code="study.edit.basis.info" />
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      <c:url var="accessUrl"
        value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.records[0].id}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" role="form">
        <c:set var="selectedType" value="${StudyForm.selectedFileType}" scope="request" />
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <div class="form-group">
          <div class="col-sm-12">
            <sf:label path="record.variables">
              <s:message code="dataset.import.report.codebook" />
            </sf:label>
            <div class="pre-x-scrollable">
              <table class="table table-striped table-bordered">
                <thead>
                  <tr>
                    <th><s:message code="dataset.import.report.codebook.name" /></th>
                    <th><s:message code="dataset.import.report.codebook.type" /></th>
                    <th><s:message code="dataset.import.report.codebook.width" /></th>
                    <th><s:message code="dataset.import.report.codebook.dec" /></th>
                    <th><s:message code="dataset.import.report.codebook.label" /></th>
                    <th><s:message code="dataset.import.report.codebook.values" /></th>
                    <th><s:message code="dataset.import.report.codebook.missings" /></th>
                    <th><s:message code="dataset.import.report.codebook.cols" /></th>
                    <th><s:message code="dataset.import.report.codebook.aligment" /></th>
                    <th><s:message code="dataset.import.report.codebook.measureLevel" /></th>
                    <th><s:message code="dataset.import.report.codebook.role" /></th>
                    <th><s:message code="dataset.import.report.codebook.userAtt" /></th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach items="${StudyForm.previousRecordVersion.variables}" var="var">
                    <tr>
                      <td><strong><s:message text="${var.name}" /></strong></td>
                      <td><s:message code="spss.type.${var.type}" /></td>
                      <td><s:message text="${var.width}" /></td>
                      <td><s:message text="${var.decimals}" /></td>
                      <td><s:message text="${var.label}" /></td>
                      <td><c:forEach items="${var.values}" var="val">
                          <div>
                            <s:message text="${val.value}&nbsp;=&nbsp;&quot;${val.label}&quot;" />
                          </div>
                        </c:forEach></td>
                      <td><c:choose>
                          <c:when test="${var.missingFormat eq 'SPSS_ONE_MISSVAL'}">
                            <s:message text="${var.missingVal1}" />
                          </c:when>
                          <c:when test="${var.missingFormat eq 'SPSS_TWO_MISSVAL'}">
                            <s:message text="${var.missingVal1},&nbsp;${var.missingVal2}" />
                          </c:when>
                          <c:when test="${var.missingFormat eq 'SPSS_THREE_MISSVAL'}">
                            <s:message text="${var.missingVal1},&nbsp;${var.missingVal2},&nbsp;${var.missingVal3}" />
                          </c:when>
                          <c:when test="${var.missingFormat eq 'SPSS_MISS_RANGE'}">
                            <s:message text="${var.missingVal1}&nbsp;-&nbsp;${var.missingVal2}" />
                          </c:when>
                          <c:when test="${var.missingFormat eq 'SPSS_MISS_RANGEANDVAL'}">
                            <s:message text="${var.missingVal1}&nbsp;-&nbsp;${var.missingVal2},&nbsp;${var.missingVal3}" />
                          </c:when>
                        </c:choose></td>
                      <td><s:message text="${var.columns}" /></td>
                      <td><s:message code="spss.aligment.${var.aligment}" /></td>
                      <td><s:message code="spss.measureLevel.${var.measureLevel}" /></td>
                      <td><s:message code="spss.role.${var.role}" /></td>
                      <td>
                        <table class="table"
                          style="margin: 0px; padding: 0px; background-color: rgba(0, 0, 0, 0.0) !important;">
                          <thead>
                            <tr>
                              <c:forEach items="${var.attributes}" var="att">
                                <th><s:message text="[${att.label}]" /></th>
                              </c:forEach>
                            </tr>
                          </thead>
                          <tbody>
                            <tr>
                              <c:forEach items="${var.attributes}" var="att">
                                <td><s:message text="${att.value}" /></td>
                              </c:forEach>
                            </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <s:message code="dataset.import.report.codebook.help" var="appresmess" />
            <%@ include file="templates/helpblock.jsp"%>
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12 text-right">
            <a href="${accessUrl}" class="btn btn-default"> <s:message code="dataset.cancel.import" />
            </a>
            <button type="submit" class="btn btn-success" name="saveWithNewCodeBook">
              <s:message code="dataset.submit.import" />
            </button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>