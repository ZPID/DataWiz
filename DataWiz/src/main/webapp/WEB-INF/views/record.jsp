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
              <s:message code="record.create.headline" />
            </h4>
            <div>
              <s:message code="record.create.info" />
            </div>
          </c:when>
          <c:otherwise>
            <div class="row">
              <div class="col-sm-12 text-align-right">
                <!-- Trigger the modal with a button -->
                <button type="button" class="btn btn-primary btn-sm btn-xs-block" data-toggle="modal" data-target="#uploadModal">
                  <s:message code="record.upload.new.file" />
                </button>
                <button type="button" class="btn btn-primary btn-sm btn-xs-block" data-toggle="modal" data-target="#exportModal">
                  <s:message code="export.show.modal.button" />
                </button>
                <button type="button" class="btn btn-primary btn-sm btn-xs-block" data-toggle="modal" data-target="#historyModal">
                  <s:message code="record.history.show" />
                </button>
                <c:if
                  test="${principal.user.hasRole('PROJECT_ADMIN', StudyForm.project.id, false) or
                  ((principal.user.hasRole('PROJECT_WRITER', StudyForm.project.id, false) 
                  or principal.user.hasRole('DS_WRITER', StudyForm.study.id, true)) and  StudyForm.record.createdBy eq principal.user.email) 
                  or principal.user.hasRole('ADMIN')}">
                  <a class="btn btn-danger btn-sm btn-xs-block"
                    href="<c:url value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/deleteRecord" />"
                    onclick="return confirm('<s:message code="record.delete.popup.msg" />');"><s:message code="record.delete.button" /></a>
                </c:if>
              </div>
              <div class="col-sm-12">
                <h4>
                  <c:choose>
                    <c:when test="${not empty StudyForm.record.recordName}">
                      <s:message code="record.edit.headline" arguments="${StudyForm.record.recordName}" />
                    </c:when>
                    <c:otherwise>
                      <s:message code="record.edit.headline" arguments=" " />
                    </c:otherwise>
                  </c:choose>
                </h4>
              </div>
              <div class="col-sm-12">
                <s:message code="record.edit.info" />
              </div>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:url var="accessUrl" value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" method="POST" enctype="multipart/form-data" id="studyFormDis">
        <c:if
          test="${!principal.user.hasRole('PROJECT_ADMIN', StudyForm.project.id, false) and
                  !principal.user.hasRole('PROJECT_WRITER', StudyForm.project.id, false) and
                  !principal.user.hasRole('ADMIN') and 
                  !principal.user.hasRole('DS_WRITER', StudyForm.study.id, true)}">
          <input type="hidden" id="disStudyContent" value="disabled" />
        </c:if>
        <input type="hidden" id="csrf" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <!-- records[0].recordName -->
        <c:set var="input_vars" value="record.recordName;record.recordName;required; ;row" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- study.sAbstract -->
        <c:set var="input_vars" value="record.description;record.description; ; ;row" />
        <%@ include file="templates/gen_textarea.jsp"%>
        <div class="form-group">
          <div class="col-xs-6">
            <a href="<c:url value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/records" />" class="btn btn-default btn-sm"><s:message
                code="record.back.to.overview" /></a>
          </div>
          <div class="col-xs-6 text-align-right">
            <sf:button class="btn btn-success btn-sm" name="saveMetaData">
              <s:message code="gen.submit" />
            </sf:button>
          </div>
        </div>
        <!-- UploadModal -->
        <div class="modal fade" id="uploadModal" role="dialog">
          <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content panel-primary">
              <div class="modal-header panel-heading">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">
                  <s:message code="record.modal.upload" />
                </h4>
              </div>
              <div class="modal-body">
                <div class="well">
                  <s:message code="record.modal.upload.info" />
                </div>
                <div class="form-group">
                  <div class="col-sm-12">
                    <sf:label class="control-label" path="selectedFileType">
                      <s:message code="record.selectedFileType" />
                    </sf:label>
                    <sf:select path="selectedFileType" class="form-control">
                      <c:if test="${isSPSSLibLoaded}">
                        <sf:option value="SPSS">
                          <s:message code="record.selectedFileType.spss" />
                        </sf:option>
                      </c:if>
                      <sf:option value="CSV">
                        <s:message code="record.selectedFileType.csv" />
                      </sf:option>
                    </sf:select>
                    <s:message code="record.selectedFileType.help" var="appresmess" />
                    <%@ include file="templates/helpblock.jsp"%>
                  </div>
                </div>
                <c:if test="${isSPSSLibLoaded}">
                  <div id="spssSelected">
                    <div class="form-group">
                      <div class="col-sm-12">
                        <sf:label class="control-label" path="spssFile">
                          <s:message code="record.spssFile.label" />
                        </sf:label>
                        <div>
                          <sf:label class="btn btn-primary form-control" path="spssFile">
                            <input id="spssFile" name="spssFile" type="file" style="display: none;" accept=".sav"
                              onchange="shortFilename('upload-spss-file-info',$(this).val());" />
                            <s:message code="record.spssFile.button" />
                          </sf:label>
                          <div class='form-control text-align-center' id="upload-spss-file-info"></div>
                        </div>
                        <s:message code="record.spssFile.help" var="appresmess" />
                        <%@ include file="templates/helpblock.jsp"%>
                      </div>
                    </div>
                  </div>
                </c:if>
                <div id="csvSelected">
                  <div class="form-group">
                    <div class="col-sm-12">
                      <sf:label class="control-label" path="csvFile">
                        <s:message code="record.csvFile.label" />
                      </sf:label>
                      <sf:label class="btn btn-primary form-control" path="csvFile">
                        <input id="csvFile" name="csvFile" type="file" style="display: none;" accept=".csv,.txt,.dat"
                          onchange="shortFilename('upload-csv-file-info',$(this).val());" />
                        <s:message code="record.csvFile.button" />
                      </sf:label>
                      <div class='form-control text-align-center' id="upload-csv-file-info"></div>
                      <s:message code="record.csvFile.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                  <div class="form-group">
                    <!-- headerRow -->
                    <div class="col-sm-12">
                      <label class="control-label"><sf:checkbox path="headerRow" /> <s:message code="record.headerRow" /> </label>
                      <s:message code="record.headerRow.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                  <div class="form-group">
                    <!-- seperator -->
                    <div class="col-sm-6">
                      <sf:label class="control-label" path="csvSeperator">
                        <s:message code="record.csvSeperator" />
                      </sf:label>
                      <sf:select path="csvSeperator" class="form-control">
                        <sf:option value=",">
                          <s:message code="record.csvSeperator.comma" />
                        </sf:option>
                        <sf:option value=";">
                          <s:message code="record.csvSeperator.semicolon" />
                        </sf:option>
                        <sf:option value="t">
                          <s:message code="record.csvSeperator.tab" />
                        </sf:option>
                        <sf:option value=".">
                          <s:message code="record.csvSeperator.point" />
                        </sf:option>
                        <sf:option value=":">
                          <s:message code="record.csvSeperator.colon" />
                        </sf:option>
                        <sf:option value=" ">
                          <s:message code="record.csvSeperator.space" />
                        </sf:option>
                      </sf:select>
                      <s:message code="record.csvSeperator.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                    <!-- quote chars -->
                    <div class="col-sm-6">
                      <sf:label class="control-label" path="csvQuoteChar">
                        <s:message code="record.quote.characters" />
                      </sf:label>
                      <sf:select path="csvQuoteChar" class="form-control">
                        <sf:option value="q">
                          <s:message code="record.quote.characters.quote" />
                        </sf:option>
                        <sf:option value="s">
                          <s:message code="record.quote.characters.single.quote" />
                        </sf:option>
                      </sf:select>
                      <s:message code="record.quote.characters.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                    <!-- quote chars -->
                    <%-- <div class="col-sm-4">
                      <sf:label class="control-label" path="csvDecChar">
                        <s:message code="record.csvDecChar" />
                      </sf:label>
                      <sf:select path="csvDecChar" class="form-control">
                        <sf:option value=".">
                          <s:message code="record.csvSeperator.point" />
                        </sf:option>
                        <sf:option value=",">
                          <s:message code="record.csvSeperator.comma" />
                        </sf:option>
                      </sf:select>
                      <s:message code="record.csvDecChar.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%> 
                  </div>--%>
                  </div>
                  <%-- <div class="form-group">
                    <div class="col-sm-12">
                      <sf:label class="control-label" path="codeBookFile">
                        <s:message code="record.codeBookFile" />
                      </sf:label>
                      <sf:label class="btn btn-primary form-control" path="codeBookFile">
                        <input name="codeBookFile" id="codeBookFile" type="file" style="display: none;" accept=".dw"
                          onchange="shortFilename('upload-codebook-file-info',$(this).val());" />
                        <s:message code="record.csvFile.button" />
                      </sf:label>
                      <div class='form-control text-align-center' id="upload-codebook-file-info"></div>
                      <s:message code="record.codeBookFile.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div> --%>
                </div>
                <c:set var="input_vars" value="newChangeLog;record.changeLog;required; ;row" />
                <%@ include file="templates/gen_textarea.jsp"%>
              </div>
              <div class="modal-footer">
                <div class="row">
                  <div class="col-sm-6 text-align-left">
                    <button class="btn btn-default btn-sm" data-dismiss="modal">
                      <s:message code="gen.close" />
                    </button>
                  </div>
                  <div class="col-sm-6 text-align-right">
                    <sf:button class="btn btn-success btn-sm" name="upload">
                      <s:message code="gen.submit" />
                    </sf:button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- HistoryModal -->
        <div class="modal fade" id="historyModal" role="dialog">
          <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content panel-primary">
              <div class="modal-header panel-heading">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">
                  <s:message code="record.history.modal.headline" />
                </h4>
              </div>
              <div class="modal-body">
                <div class="well">
                  <s:message code="record.modal.history.info" />
                </div>
                <ul class="list-group">
                  <c:set var="verslength" value="${fn:length(StudyForm.records)}" />
                  <c:forEach items="${StudyForm.records}" var="recVersion" varStatus="verCount">
                    <li class="list-group-item">
                      <div class="form-group">
                        <div class="col-sm-12 text-align-right">
                          <c:url var="versionUrl"
                            value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${recVersion.versionId}/codebook" />
                          <a class="btn btn-default btn-sm" href="${versionUrl}"><s:message code="record.history.modal.select" /></a>
                        </div>
                        <div class="col-sm-3 text-align-right text-bold">
                          <s:message code="record.history.modal.number" />
                        </div>
                        <div class="col-sm-9">
                          <s:message text="${verslength- verCount.index}" />
                        </div>
                        <div class="col-sm-3 text-align-right text-bold">
                          <s:message code="record.history.modal.versionnumber" />
                        </div>
                        <div class="col-sm-9">
                          <s:message text="${recVersion.versionId}" />
                        </div>
                        <div class="col-sm-3 text-align-right text-bold">
                          <s:message code="record.history.modal.changedBy" />
                        </div>
                        <div class="col-sm-9">
                          <s:message text="${recVersion.changedBy}" />
                        </div>
                        <div class="col-sm-3 text-align-right text-bold">
                          <s:message code="record.history.modal.changedDate" />
                        </div>
                        <div class="col-sm-9">
                          <javatime:format value="${recVersion.changed}" style="MS" />
                        </div>
                        <div class="col-sm-3 text-align-right text-bold">
                          <s:message code="record.history.modal.changeLog" />
                        </div>
                        <div class="col-sm-9" style="text-align: justify;">
                          <s:message text="${recVersion.changeLog}" htmlEscape="true" />
                        </div>
                      </div>
                    </li>
                  </c:forEach>
                </ul>
              </div>
              <div class="modal-footer">
                <div class="row">
                  <div class="col-md-12 text-align-left">
                    <button class="btn btn-default btn-sm" data-dismiss="modal">
                      <s:message code="gen.close" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<c:url var="exportUrl"
  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}" />
<div class="modal fade" id="exportModal" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content panel-primary">
      <div class="modal-header panel-heading">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">
          <s:message code="record.export.modal.title" />
        </h4>
      </div>
      <div class="modal-body">
        <div class="well">
          <s:message code="record.modal.export.info" />
        </div>
        <ul class="list-group">
          <li class="list-group-item"><b><s:message code="record.export.modal.csv" /></b>
            <ul class="list-group">
              <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/CSVMatrix', '_blank')" data-dismiss="modal"><s:message
                  code="record.export.modal.csvmatrix" /></li>
              <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/CSVCodebook', '_blank')"
                data-dismiss="modal"><s:message code="record.export.modal.csvcodebook" /></li>
              <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/CSVZIP', '_blank')" data-dismiss="modal"><s:message
                  code="record.export.modal.csvboth" /></li>
            </ul></li>
          <li class="list-group-item"><b><s:message code="record.export.modal.spss" /></b>
            <ul class="list-group">
              <c:choose>
                <c:when test="${disableSPSSExport or not isSPSSLibLoaded}">
                  <li class="list-group-item btn btn-default btn-sm disabled"><s:message code="record.export.modal.sav" /></li>
                </c:when>
                <c:otherwise>
                  <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/SPSS', '_blank')" data-dismiss="modal"><s:message
                      code="record.export.modal.sav" /></li>
                </c:otherwise>
              </c:choose>
            </ul></li>
          <li class="list-group-item"><b><s:message code="record.export.modal.json" /></b>
            <ul class="list-group">
              <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/JSON', '_blank')" data-dismiss="modal"><s:message
                  code="record.export.modal.json.file" /></li>
            </ul></li>
          <li class="list-group-item"><b><s:message code="record.export.modal.pdf" /></b>
            <ul class="list-group">
              <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/PDF?attachments=false', '_blank')"
                data-dismiss="modal"><s:message code="record.export.modal.pdf.withoutAtt" /></li>
              <li class="list-group-item btn btn-default btn-sm" onclick="window.open('${exportUrl}/export/PDF?attachments=true', '_blank')"
                data-dismiss="modal"><s:message code="record.export.modal.pdf.withAtt" /></li>
            </ul> <s:message code="record.export.modal.pdf.help" var="appresmess" /> <%@ include file="templates/helpblock.jsp"%></li>
        </ul>
      </div>
      <div class="modal-footer">
        <div class="row">
          <div class="col-sm-12 text-align-left">
            <button class="btn btn-default btn-sm" data-dismiss="modal">
              <s:message code="gen.close" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>