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
                <%-- TODO <c:choose>
                  <c:when test="${empty disStudyContent || disStudyContent eq 'disabled' }">
                    <a href="${accessUrl}" class="btn btn-success">Checkin</a>
                  </c:when>
                  <c:otherwise>
                    <a href="${accessUrl}" class="btn btn-danger">CheckOut</a>
                  </c:otherwise>
                </c:choose> --%>
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
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
      <c:url var="accessUrl"
        value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}?${_csrf.parameterName}=${_csrf.token}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" method="POST"
        enctype="multipart/form-data" role="form">
        <!-- records[0].recordName -->
        <c:set var="input_vars" value="record.recordName;record.recordName;required; ;row" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- study.sAbstract -->
        <c:set var="input_vars" value="record.description;record.description; ; ;row" />
        <%@ include file="templates/gen_textarea.jsp"%>
        <div class="form-group">
          <div class="col-sm-offset-0 col-md-12">
            <button type="reset" class="btn btn-default">
              <s:message code="gen.reset" />
            </button>
            <sf:button type="submit" class="btn btn-success">
              <s:message code="gen.submit" />
            </sf:button>
          </div>
        </div>
        <!-- UploadModal -->
        <div class="modal fade" id="uploadModal" role="dialog">
          <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content panel-info">
              <div class="modal-header panel-heading">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">
                  <s:message code="record.modal.upload" />
                </h4>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <div class="col-sm-12">
                    <sf:label class="control-label" path="selectedFileType">
                      <s:message code="record.selectedFileType" />
                    </sf:label>
                    <sf:select path="selectedFileType" class="form-control">
                      <sf:option value="SPSS">
                        <s:message code="record.selectedFileType.spss" />
                      </sf:option>
                      <sf:option value="CSV">
                        <s:message code="record.selectedFileType.csv" />
                      </sf:option>
                    </sf:select>
                    <s:message code="record.selectedFileType.help" var="appresmess" />
                    <%@ include file="templates/helpblock.jsp"%>
                  </div>
                </div>
                <div id="spssSelected">
                  <div class="form-group">
                    <div class="col-sm-12">
                      <sf:label class="control-label" path="spssFile">
                        <s:message code="record.spssFile.label" />
                      </sf:label>
                      <div>
                        <sf:label class="btn btn-primary form-control" path="spssFile">
                          <sf:input path="spssFile" type="file" style="display: none;" accept=".sav,.por"
                            onchange="shortFilename('upload-spss-file-info',$(this).val());" />
                          <s:message code="record.spssFile.button" />
                        </sf:label>
                        <div class='form-control' id="upload-spss-file-info" style="text-align: center;"></div>
                      </div>
                      <s:message code="record.spssFile.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                </div>
                <div id="csvSelected">
                  <div class="form-group">
                    <div class="col-sm-12">
                      <sf:label class="control-label" path="csvFile">
                        <s:message code="record.csvFile.label" />
                      </sf:label>
                      <sf:label class="btn btn-primary form-control" path="csvFile">
                        <sf:input path="csvFile" type="file" style="display: none;" accept=".csv,.txt,.dat"
                          onchange="shortFilename('upload-csv-file-info',$(this).val());" />
                        <s:message code="record.csvFile.button" />
                      </sf:label>
                      <div class='form-control' id="upload-csv-file-info" style="text-align: center;"></div>
                      <s:message code="record.csvFile.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                  <div class="form-group">
                    <!-- headerRow -->
                    <div class="col-sm-12">
                      <sf:label class="control-label" path="headerRow">
                        <s:message code="record.headerRow" />
                      </sf:label>
                      <sf:checkbox path="headerRow" />
                      <s:message code="record.headerRow.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                  <div class="form-group">
                    <!-- seperator -->
                    <div class="col-sm-4">
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
                    <div class="col-sm-4">
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
                    <div class="col-sm-4">
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
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="col-sm-12">
                      <sf:label class="control-label" path="codeBookFile">
                        <s:message code="record.codeBookFile" />
                      </sf:label>
                      <sf:label class="btn btn-primary form-control" path="codeBookFile">
                        <sf:input path="codeBookFile" type="file" style="display: none;" accept=".dw"
                          onchange="shortFilename('upload-codebook-file-info',$(this).val());" />
                        <s:message code="record.csvFile.button" />
                      </sf:label>
                      <div class='form-control' id="upload-codebook-file-info" style="text-align: center;"></div>
                      <s:message code="record.codeBookFile.help" var="appresmess" />
                      <%@ include file="templates/helpblock.jsp"%>
                    </div>
                  </div>
                </div>
                <c:set var="input_vars" value="newChangeLog;record.changeLog;required; ;row" />
                <%@ include file="templates/gen_textarea.jsp"%>
              </div>
              <div class="modal-footer">
                <div class="form-group">
                  <div class="col-sm-offset-0 col-md-12">
                    <button class="btn btn-default" data-dismiss="modal">Close</button>
                    <sf:button type="submit" class="btn btn-success" name="upload">
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
            <div class="modal-content panel-warning">
              <div class="modal-header panel-heading">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">History</h4>
              </div>
              <div class="modal-body">History</div>
              <div class="modal-footer">
                <div class="form-group">
                  <div class="col-sm-offset-0 col-md-12">
                    <button class="btn btn-default" data-dismiss="modal">Close</button>
                    <sf:button type="submit" class="btn btn-success" name="history">
                      <s:message code="gen.submit" />
                    </sf:button>
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
<%@ include file="templates/footer.jsp"%>