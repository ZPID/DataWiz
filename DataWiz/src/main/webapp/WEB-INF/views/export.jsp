<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.export.headline" />
        </h4>
        <div>
          <s:message code="project.export.info" />
        </div>
      </div>
      <c:url var="accessUrl" value="/export/${ExportProjectForm.projectId}" />
      <sf:form action="${accessUrl}" modelAttribute="ExportProjectForm" class="form-horizontal" role="form" target="_blank">
        <ul class="list-group exportlist">
          <li class="list-group-item">
            <div class="row">
              <div class="col-sm-11">
                <b><s:message text="${ExportProjectForm.projectTitle}" /></b>
              </div>
              <div class="col-sm-1">
                <sf:checkbox class="li_chkbox li_checkbox_a1" path="exportFullProject" />
              </div>
            </div>
          </li>
          <c:if test="${not empty ExportProjectForm.warnings}">
            <li class="list-group-item list-group-item-danger">
              <ul class="row">
                <c:forEach items="${ExportProjectForm.warnings}" var="studyWarn">
                  <li class="col-sm-10 col-sm-offset-1"><s:message text="${studyWarn}" /></li>
                </c:forEach>
              </ul>
            </li>
          </c:if>
          <c:if test="${not empty ExportProjectForm.notices}">
            <li class="list-group-item list-group-item-info">
              <ul class="row">
                <c:forEach items="${ExportProjectForm.notices}" var="notice">
                  <li class="col-sm-10 col-sm-offset-1"><s:message text="${notice}" /></li>
                </c:forEach>
              </ul>
            </li>
          </c:if>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">
                <s:message code="export.view.project.meta" />
              </div>
              <div class="col-sm-1">
                <sf:checkbox path="exportMetaData" />
              </div>
            </div></li>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">
                <s:message code="export.view.project.dmp" />
              </div>
              <div class="col-sm-1">
                <sf:checkbox path="exportDMP" />
              </div>
            </div></li>
          <li class="list-group-item"><c:choose>
              <c:when test="${empty ExportProjectForm.material}">
                <div class="row">
                  <div class="col-sm-11">
                    <s:message code="export.view.project.mat" arguments="(Keine Materialien vorhanden)" />
                  </div>
                </div>
              </c:when>
              <c:otherwise>
                <div class="row">
                  <div class="col-sm-6">
                    <s:message code="export.view.project.mat" arguments="(${fn:length(ExportProjectForm.material)})" />
                    <strong data-target="#tableColapse" data-toggle="collapse" style="cursor: pointer;"><s:message
                        code="export.view.project.mat.show" /></strong>
                  </div>
                  <div class="col-sm-5">
                    <div id="sizeWarn" class="text-danger">
                      <s:message code="export.error.filesize" />
                    </div>
                  </div>
                  <div class="col-sm-1">
                    <sf:checkbox path="exportProjectMaterial" />
                  </div>
                  <c:set var="globalSizeWarn" value="false" />
                </div>
                <div class="row collapse" id="tableColapse" style="padding-top: 20px;">
                  <div class="col-sm-10 col-sm-offset-1">
                    <div class="well well-sm">
                      <s:message code="export.error.filesize.info" />
                    </div>
                    <table class="table table-striped">
                      <tr>
                        <th><s:message code="export.view.filename" /></th>
                        <th><s:message code="export.view.filesize" /></th>
                      </tr>
                      <c:set var="totalSize" value="0" />
                      <c:forEach items="${ExportProjectForm.material}" var="file">
                        <c:set var="sizeWarn" value="false" />
                        <c:if test="${file.fileSize>(20000000)}">
                          <c:set var="sizeWarn" value="true" />
                        </c:if>
                        <tr <c:if test="${sizeWarn}">class="danger text-danger"</c:if>>
                          <td><a href="<c:url value='/project/${ExportProjectForm.projectId}/download/${file.id}' />" target="_blank"><s:message
                                text="${file.fileName}" /></a></td>
                          <td><c:choose>
                              <c:when test="${file.fileSize>(1000000)}">
                                <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1000000}" />
                                <s:message code="export.view.mb" />
                              </c:when>
                              <c:otherwise>
                                <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1000}" />
                                <s:message code="export.view.kb" />
                              </c:otherwise>
                            </c:choose></td>
                        </tr>
                        <c:if test="${sizeWarn}">
                          <c:set var="globalSizeWarn" value="true" />
                        </c:if>
                        <c:set var="totalSize" value="${totalSize + file.fileSize}" />
                      </c:forEach>
                      <tr>
                        <td class="text-right"><s:message code="export.view.totalsize" /></td>
                        <td><c:choose>
                            <c:when test="${totalSize>(1000000)}">
                              <fmt:formatNumber type="number" maxFractionDigits="2" value="${totalSize/1000000}" />
                              <s:message code="export.view.mb" />
                            </c:when>
                            <c:otherwise>
                              <fmt:formatNumber type="number" maxFractionDigits="2" value="${totalSize/1000}" />
                              <s:message code="export.view.kb" />
                            </c:otherwise>
                          </c:choose></td>
                      </tr>
                    </table>
                  </div>
                </div>
                <c:if test="${not globalSizeWarn}">
                  <script type="text/javascript">
																			document.getElementById('sizeWarn').style.display = 'none';
																		</script>
                </c:if>
              </c:otherwise>
            </c:choose></li>
          <li class="list-group-item" style="background-color: #eee;">
            <div class="form-group">
              <div class="col-sm-12">
                <label><s:message code="export.view.studies" /></label>
              </div>
            </div> <c:forEach items="${ExportProjectForm.studies}" var="study" varStatus="studyLoop">
              <ul class="list-group li_head_u2" style="position: relative; right: -12px;">
                <li class="list-group-item">
                  <div class="row">
                    <div class="col-sm-11">
                      <b><s:message text="${study.studyTitle}" /></b>
                    </div>
                    <div class="col-sm-1">
                      <sf:checkbox path="studies[${studyLoop.index}].exportFullStudy" class="li_chkbox li_checkbox_a2" />
                    </div>
                  </div>
                </li>
                <c:if test="${not empty study.warnings}">
                  <li class="list-group-item list-group-item-danger">
                    <ul class="row" type="circle">
                      <c:forEach items="${study.warnings}" var="studyWarn">
                        <li class="col-sm-10 col-sm-offset-1"><s:message text="${studyWarn}" /></li>
                      </c:forEach>
                    </ul>
                  </li>
                </c:if>
                <c:if test="${not empty study.notices}">
                  <li class="list-group-item list-group-item-info">
                    <div>
                      <ul class="row" type="circle">
                        <c:forEach items="${study.notices}" var="notice">
                          <li class="col-sm-10 col-sm-offset-1"><s:message text="${notice}" /></li>
                        </c:forEach>
                      </ul>
                    </div>
                  </li>
                </c:if>
                <li class="list-group-item"><div class="row">
                    <div class="col-sm-11">
                      <s:message code="export.view.studymeta" />
                    </div>
                    <div class="col-sm-1">
                      <sf:checkbox path="studies[${studyLoop.index}].exportMetaData" />
                    </div>
                  </div></li>
                <li class="list-group-item"><c:choose>
                    <c:when test="${empty study.material}">
                      <div class="row">
                        <div class="col-sm-11">
                          <s:message code="export.view.project.mat" arguments="(Keine Materialien vorhanden)" />
                        </div>
                      </div>
                    </c:when>
                    <c:otherwise>
                      <div class="row">
                        <div class="col-sm-6">
                          <s:message code="export.view.project.mat" arguments="(${fn:length(study.material)})" />
                          <strong data-target="#tableColapse_${studyLoop.index}" data-toggle="collapse" style="cursor: pointer;"><s:message
                              code="export.view.project.mat.show" /></strong>
                        </div>
                        <div class="col-sm-5">
                          <div id="sizeWarn_${studyLoop.index}" class="text-danger">
                            <s:message code="export.error.filesize" />
                          </div>
                        </div>
                        <div class="col-sm-1">
                          <sf:checkbox path="studies[${studyLoop.index}].exportStudyMaterial" />
                        </div>
                        <c:set var="globalSizeWarn" value="false" />
                      </div>
                      <div class="row collapse" id="tableColapse_${studyLoop.index}" style="padding-top: 20px;">
                        <div class="col-sm-10 col-sm-offset-1">
                          <div class="well well-sm">
                            <s:message code="export.error.filesize.info" />
                          </div>
                          <table class="table table-striped">
                            <tr>
                              <th><s:message code="export.view.filename" /></th>
                              <th><s:message code="export.view.filesize" /></th>
                            </tr>
                            <c:set var="totalSize" value="0" />
                            <c:forEach items="${study.material}" var="file">
                              <c:set var="sizeWarn" value="false" />
                              <c:if test="${file.fileSize>(20000000)}">
                                <c:set var="sizeWarn" value="true" />
                              </c:if>
                              <tr <c:if test="${sizeWarn}">class="danger text-danger"</c:if>>
                                <td><a href="<c:url value='/project/${ExportProjectForm.projectId}/download/${file.id}' />" target="_blank"><s:message
                                      text="${file.fileName}" /></a></td>
                                <td><c:choose>
                                    <c:when test="${file.fileSize>(1000000)}">
                                      <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1000000}" /> MB
                          </c:when>
                                    <c:otherwise>
                                      <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1000}" /> KB
                          </c:otherwise>
                                  </c:choose></td>
                              </tr>
                              <c:if test="${sizeWarn}">
                                <c:set var="globalSizeWarn" value="true" />
                              </c:if>
                              <c:set var="totalSize" value="${totalSize + file.fileSize}" />
                            </c:forEach>
                            <tr>
                              <td class="text-right"><s:message code="export.view.totalsize" /></td>
                              <td><c:choose>
                                  <c:when test="${totalSize>(1000000)}">
                                    <fmt:formatNumber type="number" maxFractionDigits="2" value="${totalSize/1000000}" /> MB
                          </c:when>
                                  <c:otherwise>
                                    <fmt:formatNumber type="number" maxFractionDigits="2" value="${totalSize/1000}" /> KB
                          </c:otherwise>
                                </c:choose></td>
                            </tr>
                          </table>
                        </div>
                      </div>
                      <c:if test="${not globalSizeWarn}">
                        <script type="text/javascript">
																									document.getElementById('sizeWarn_${studyLoop.index}').style.display = 'none';
																								</script>
                      </c:if>
                    </c:otherwise>
                  </c:choose></li>
                <c:if test="${not empty study.records}">
                  <li class="list-group-item" style="background-color: #eee;">
                    <div>
                      <div class="form-group">
                        <div class="col-sm-12">
                          <label><s:message code="export.view.records" /></label>
                        </div>
                      </div>
                      <c:forEach items="${study.records}" var="record" varStatus="recordLoop">
                        <ul class="list-group li_head_u3" style="position: relative; right: -12px;">
                          <li class="list-group-item">
                            <div class="row">
                              <div class="col-sm-11">
                                <b><s:message text="${record.recordTitle}" /></b>
                              </div>
                              <div class="col-sm-1">
                                <sf:checkbox path="studies[${studyLoop.index}].records[${recordLoop.index}].exportFullRecord"
                                  class="li_chkbox li_checkbox_a3" />
                              </div>
                            </div>
                          </li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">
                                <s:message code="export.view.recordmeta" />
                              </div>
                              <div class="col-sm-1">
                                <sf:checkbox path="studies[${studyLoop.index}].records[${recordLoop.index}].exportMetaData" />
                              </div>
                            </div></li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">
                                <s:message code="export.view.codebook" />
                              </div>
                              <div class="col-sm-1">
                                <sf:checkbox path="studies[${studyLoop.index}].records[${recordLoop.index}].exportCodebook" />
                              </div>
                            </div></li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">
                                <s:message code="export.view.matrix" />
                              </div>
                              <div class="col-sm-1">
                                <sf:checkbox path="studies[${studyLoop.index}].records[${recordLoop.index}].exportMatrix" />
                              </div>
                            </div></li>
                        </ul>
                      </c:forEach>
                    </div>
                  </li>
                </c:if>
              </ul>
            </c:forEach>
          </li>
        </ul>
        <div class="row">
          <div class="col-sm-12 text-align-right">
            <sf:button type="submit" class="btn btn-success btn-sm" id="meta_submit">
              <s:message code="export.view.submit" />
            </sf:button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>