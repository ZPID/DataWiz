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
      <s:message text="${ProjectForm.project.id}" var="projectId" />
      <c:url var="accessUrl" value="/export/${projectId}" />
      <sf:form action="${accessUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <c:set var="exportCount" value="0" />
        <ul class="list-group exportlist">
          <li class="list-group-item">
            <div class="row">
              <div class="col-sm-11">
                <sf:label path="project.title">
                  <s:message text="${ProjectForm.project.title}" />
                </sf:label>
              </div>
              <div class="col-sm-1">
                <sf:checkbox class="li_chkbox li_checkbox_a1" path="exportList[${exportCount}].export" />
                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                  value="PROJECT_EXPORT_FULL" type="hidden" /> <input id="exportList${exportCount}.projectId"
                  name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" />
              </div>
            </div>
          </li>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">Project-Metadaten</div>
              <div class="col-sm-1">
                <c:set var="exportCount" value="${exportCount + 1}" />
                <sf:checkbox path="exportList[${exportCount}].export" />
                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                  value="PROJECT_EXPORT_METADATA" type="hidden" /> <input id="exportList${exportCount}.projectId"
                  name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" />
              </div>
            </div></li>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">Datenmanagment</div>
              <div class="col-sm-1">
                <c:set var="exportCount" value="${exportCount + 1}" />
                <sf:checkbox path="exportList[${exportCount}].export" />
                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                  value="PROJECT_EXPORT_DMP" type="hidden" /> <input id="exportList${exportCount}.projectId"
                  name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" />
              </div>
            </div></li>
          <li class="list-group-item"><div class="row">
              <div class="col-sm-11">ProjectMaterialien</div>
              <div class="col-sm-1">
                <c:set var="exportCount" value="${exportCount + 1}" />
                <sf:checkbox path="exportList[${exportCount}].export" />
                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                  value="PROJECT_EXPORT_MATERIAL" type="hidden" /> <input id="exportList${exportCount}.projectId"
                  name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" />
              </div>
            </div></li>
          <li class="list-group-item" style="background-color: #eee;">
            <div class="form-group">
              <div class="col-sm-12">
                <label>Studien</label>
              </div>
            </div> <c:forEach items="${ProjectForm.studies}" var="study">
              <s:message text="${study.id}" var="studyId" />
              <ul class="list-group li_head_u2" style="position: relative; right: -12px;">
                <li class="list-group-item">
                  <div class="row">
                    <div class="col-sm-11">
                      <sf:label path="project.title">
                        <s:message text="${study.title}" />
                      </sf:label>
                    </div>
                    <div class="col-sm-1">
                      <c:set var="exportCount" value="${exportCount + 1}" />
                      <sf:checkbox path="exportList[${exportCount}].export" class="li_chkbox li_checkbox_a2" />
                      <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                        value="STUDY_EXPORT_FULL" type="hidden" /> <input id="exportList${exportCount}.projectId"
                        name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" /> <input
                        id="exportList${exportCount}.studyId" name="exportList[${exportCount}].studyId"
                        value="${studyId}" type="hidden" />
                    </div>
                  </div>
                </li>
                <li class="list-group-item"><div class="row">
                    <div class="col-sm-11">Studien-Metadaten</div>
                    <div class="col-sm-1">
                      <c:set var="exportCount" value="${exportCount + 1}" />
                      <sf:checkbox path="exportList[${exportCount}].export" />
                      <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                        value="STUDY_EXPORT_METADATA" type="hidden" /> <input id="exportList${exportCount}.projectId"
                        name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" /> <input
                        id="exportList${exportCount}.studyId" name="exportList[${exportCount}].studyId"
                        value="${studyId}" type="hidden" />
                    </div>
                  </div></li>
                <li class="list-group-item"><div class="row">
                    <div class="col-sm-11">Studien-Materialien</div>
                    <div class="col-sm-1">
                      <c:set var="exportCount" value="${exportCount + 1}" />
                      <sf:checkbox path="exportList[${exportCount}].export" />
                      <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                        value="STUDY_EXPORT_MATERIAL" type="hidden" /> <input id="exportList${exportCount}.projectId"
                        name="exportList[${exportCount}].projectId" value="${projectId}" type="hidden" /> <input
                        id="exportList${exportCount}.studyId" name="exportList[${exportCount}].studyId"
                        value="${studyId}" type="hidden" />
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
                      <c:forEach items="${study.records}" var="record">
                        <s:message text="${record.id}" var="recordId" />
                        <s:message text="${record.versionId}" var="versionId" />
                        <ul class="list-group li_head_u3" style="position: relative; right: -12px;">
                          <li class="list-group-item">
                            <div class="row">
                              <div class="col-sm-11">
                                <sf:label path="project.title">
                                  <s:message text="${record.recordName}" />
                                </sf:label>
                              </div>
                              <div class="col-sm-1">
                                <c:set var="exportCount" value="${exportCount + 1}" />
                                <sf:checkbox path="exportList[${exportCount}].export" class="li_chkbox li_checkbox_a3" />
                                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                                  value="RECORD_EXPORT_FULL" type="hidden" /> <input
                                  id="exportList${exportCount}.projectId" name="exportList[${exportCount}].projectId"
                                  value="${projectId}" type="hidden" /> <input id="exportList${exportCount}.studyId"
                                  name="exportList[${exportCount}].studyId" value="${studyId}" type="hidden" /> <input
                                  id="exportList${exportCount}.recordId" name="exportList[${exportCount}].recordId"
                                  value="${recordId}" type="hidden" /><input id="exportList${exportCount}.versionId"
                                  name="exportList[${exportCount}].versionId" value="${versionId}" type="hidden" />
                              </div>
                            </div>
                          </li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">Datensatz-Metadaten</div>
                              <div class="col-sm-1">
                                <c:set var="exportCount" value="${exportCount + 1}" />
                                <sf:checkbox path="exportList[${exportCount}].export" />
                                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                                  value="RECORD_EXPORT_METADATA" type="hidden" /> <input
                                  id="exportList${exportCount}.projectId" name="exportList[${exportCount}].projectId"
                                  value="${projectId}" type="hidden" /> <input id="exportList${exportCount}.studyId"
                                  name="exportList[${exportCount}].studyId" value="${studyId}" type="hidden" /> <input
                                  id="exportList${exportCount}.recordId" name="exportList[${exportCount}].recordId"
                                  value="${recordId}" type="hidden" /><input id="exportList${exportCount}.versionId"
                                  name="exportList[${exportCount}].versionId" value="${versionId}" type="hidden" />
                              </div>
                            </div></li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">Codebook</div>
                              <div class="col-sm-1">
                                <c:set var="exportCount" value="${exportCount + 1}" />
                                <sf:checkbox path="exportList[${exportCount}].export" />
                                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                                  value="RECORD_EXPORT_CODEBOOK" type="hidden" /> <input
                                  id="exportList${exportCount}.projectId" name="exportList[${exportCount}].projectId"
                                  value="${projectId}" type="hidden" /> <input id="exportList${exportCount}.studyId"
                                  name="exportList[${exportCount}].studyId" value="${studyId}" type="hidden" /> <input
                                  id="exportList${exportCount}.recordId" name="exportList[${exportCount}].recordId"
                                  value="${recordId}" type="hidden" /><input id="exportList${exportCount}.versionId"
                                  name="exportList[${exportCount}].versionId" value="${versionId}" type="hidden" />
                              </div>
                            </div></li>
                          <li class="list-group-item"><div class="row">
                              <div class="col-sm-11">Datenmatrix</div>
                              <div class="col-sm-1">
                                <c:set var="exportCount" value="${exportCount + 1}" />
                                <sf:checkbox path="exportList[${exportCount}].export" />
                                <input id="exportList${exportCount}.state" name="exportList[${exportCount}].state"
                                  value="RECORD_EXPORT_MATRIX" type="hidden" /> <input
                                  id="exportList${exportCount}.projectId" name="exportList[${exportCount}].projectId"
                                  value="${projectId}" type="hidden" /> <input id="exportList${exportCount}.studyId"
                                  name="exportList[${exportCount}].studyId" value="${studyId}" type="hidden" /> <input
                                  id="exportList${exportCount}.recordId" name="exportList[${exportCount}].recordId"
                                  value="${recordId}" type="hidden" /> <input id="exportList${exportCount}.versionId"
                                  name="exportList[${exportCount}].versionId" value="${versionId}" type="hidden" />
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