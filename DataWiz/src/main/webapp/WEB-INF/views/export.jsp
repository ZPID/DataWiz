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
      <sf:form action="${accessUrl}" commandName="ExportProjectForm" class="form-horizontal" role="form">
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
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">Project-Metadaten</div>
              <div class="col-sm-1">
                <sf:checkbox path="exportMetaData" />
              </div>
            </div></li>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">Datenmanagment</div>
              <div class="col-sm-1">
                <sf:checkbox path="exportDMP" />
              </div>
            </div></li>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">ProjectMaterialien</div>
              <div class="col-sm-1">
                <sf:checkbox path="exportProjectMaterial" />
              </div>
            </div></li>
          <li class="list-group-item" style="background-color: #eee;">
            <div class="form-group">
              <div class="col-sm-12">
                <label>Studien</label>
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
                    <div class="row">
                      <c:forEach items="${study.warnings}" var="studyWarn">
                        <div class="col-sm-11" style="text-align: center;">
                          <s:message text="${studyWarn}" />
                        </div>
                      </c:forEach>
                    </div>
                  </li>
                </c:if>
                <li class="list-group-item"><div class="row">
                    <div class="col-sm-11">Studien-Metadaten</div>
                    <div class="col-sm-1">
                      <sf:checkbox path="studies[${studyLoop.index}].exportMetaData" />
                    </div>
                  </div></li>
                <li class="list-group-item"><div class="row">
                    <div class="col-sm-11">Studien-Materialien</div>
                    <div class="col-sm-1">
                      <sf:checkbox path="studies[${studyLoop.index}].exportStudyMaterial" />
                    </div>
                  </div></li>
                <c:if test="${not empty study.records}">
                  <li class="list-group-item" style="background-color: #eee;">
                    <div>
                      <div class="form-group">
                        <div class="col-sm-12">
                          <label>Datensätze</label>
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
                              <div class="col-sm-11">Datensatz-Metadaten</div>
                              <div class="col-sm-1">
                                <sf:checkbox path="studies[${studyLoop.index}].records[${recordLoop.index}].exportMetaData" />
                              </div>
                            </div></li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">Codebook</div>
                              <div class="col-sm-1">
                                <sf:checkbox path="studies[${studyLoop.index}].records[${recordLoop.index}].exportCodebook" />
                              </div>
                            </div></li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">Datenmatrix</div>
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
        <sf:button>Wenn Sie das lesen, retten Sie mich aus den Fängen von MK!!!</sf:button>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>