<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <div class="row">
          <div class="col-sm-11">
            <h4>
              <s:message code="record.codebook.headline" />
            </h4>
          </div>
          <div class="col-sm-1">
            <button type="button" class="btn btn-warning" data-toggle="modal" data-target="#exportModal">EXPORT</button>
          </div>
        </div>
        <div>
          <s:message code="record.codebook.info" />
        </div>
      </div>
      <c:url var="accessUrl"
        value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal" role="form">
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
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.name" /></th>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.type" /></th>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.label" /></th>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.values" /></th>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.missings" /></th>
                    <th class="codebookTableHide th-width-60"><s:message
                        code="dataset.import.report.codebook.width" /></th>
                    <th class="codebookTableHide th-width-100"><s:message code="dataset.import.report.codebook.dec" /></th>
                    <th class="codebookTableHide th-width-60"><s:message code="dataset.import.report.codebook.cols" /></th>
                    <th class="codebookTableHide th-width-100"><s:message
                        code="dataset.import.report.codebook.aligment" /></th>
                    <th class="codebookTableHide th-width-100"><s:message
                        code="dataset.import.report.codebook.measureLevel" /></th>
                    <th class="codebookTableHide th-width-100"><s:message
                        code="dataset.import.report.codebook.role" /></th>
                    <th class="th-width-20" onclick="$('.codebookTableHide').toggle();">...</th>
                    <c:forEach items="${StudyForm.record.attributes}" var="val" varStatus="attnameloop">
                      <th class="th-width-100"><s:message text="[${fn:substringAfter(val.value, '@')}]" /></th>
                    </c:forEach>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.construct" /></th>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.measocc" /></th>
                    <th class="th-width-200"><s:message code="dataset.import.report.codebook.instrument" /></th>
                    <th class="th-width-300"><s:message code="dataset.import.report.codebook.itemtext" /></th>
                    <th class="th-width-100"><s:message code="dataset.import.report.codebook.filtervar" /></th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach items="${StudyForm.record.variables}" var="var" varStatus="loop">
                    <tr>
                      <td><strong><sf:input class="form-control varNames"
                            path="record.variables[${loop.count-1}].name" id="varNameId_${loop.count-1}" /></strong></td>
                      <td><c:set var="simplifiedType" value="${StudyForm.record.simplifyVarTypes(var.type)}" /> <s:message
                          code="spss.type.${simplifiedType}" /> <c:if test="${simplifiedType ne var.type}">(<s:message
                            code="spss.type.${var.type}" />)</c:if></td>
                      <td><sf:textarea class="form-control" path="record.variables[${loop.count-1}].label" /></td>
                      <td style="cursor: pointer;"
                        onclick="showAjaxModal('${accessUrl}/modal?varId=${var.id}&modal=values');"><c:forEach
                          items="${var.values}" var="val">
                          <div>
                            <s:message text="${val.value}&nbsp;=&nbsp;&quot;${val.label}&quot;" />
                            <br />
                          </div>
                        </c:forEach></td>
                      <td style="cursor: pointer;"
                        onclick="showAjaxModal('${accessUrl}/modal?varId=${var.id}&modal=missings');"><c:choose>
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
                      <td class="codebookTableHide"><s:message text="${var.width}" /></td>
                      <td class="codebookTableHide"><s:message text="${var.decimals}" /></td>
                      <td class="codebookTableHide"><s:message text="${var.columns}" /></td>
                      <td class="codebookTableHide"><s:message code="spss.aligment.${var.aligment}" /></td>
                      <td class="codebookTableHide"><s:message code="spss.measureLevel.${var.measureLevel}" /></td>
                      <td class="codebookTableHide"><s:message code="spss.role.${var.role}" /></td>
                      <td></td>
                      <c:forEach items="${StudyForm.record.attributes}" var="val" varStatus="attnameloop">
                        <td><c:forEach items="${var.attributes}" var="att">
                            <c:if test="${fn:substringAfter(val.value, '@') == att.label}">
                              <s:message text="${att.value}" />
                            </c:if>
                          </c:forEach></td>
                      </c:forEach>
                      <td><c:forEach items="${var.dw_attributes}" var="val" varStatus="attloop">
                          <c:if test="${val.label == 'dw_construct'}">
                            <sf:select class="form-control"
                              path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                              <sf:option value="">Kein Konstrukt</sf:option>
                              <sf:options items="${StudyForm.study.constructs}" itemLabel="name" itemValue="name" />
                            </sf:select>
                            <c:set var="contains" value="false" />
                            <c:forEach items="${StudyForm.study.constructs}" var="construct">
                              <c:if test="${construct.name eq val.value}">
                                <c:set var="contains" value="true" />
                              </c:if>
                            </c:forEach>
                            <c:if test="${not contains && val.value ne ''}">
                              <div style="color: red;">
                                <s:message code="record.codebook.construct.missing" arguments="${val.value}" />
                              </div>
                            </c:if>
                          </c:if>
                        </c:forEach></td>
                      <td><c:forEach items="${var.dw_attributes}" var="val" varStatus="attloop">
                          <c:if test="${val.label == 'dw_measocc'}">
                            <sf:select class="form-control"
                              path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                              <sf:option value="">Kein Messzeitpunkt</sf:option>
                              <sf:options items="${StudyForm.study.measOcc}" itemLabel="text" itemValue="text" />
                            </sf:select>
                            <c:set var="contains" value="false" />
                            <c:forEach items="${StudyForm.study.measOcc}" var="construct">
                              <c:if test="${construct.text eq val.value}">
                                <c:set var="contains" value="true" />
                              </c:if>
                            </c:forEach>
                            <c:if test="${not contains && val.value ne ''}">
                              <div style="color: red;">
                                <s:message code="record.codebook.measocc.missing" arguments="${val.value}" />
                              </div>
                            </c:if>
                          </c:if>
                        </c:forEach></td>
                      <td><c:forEach items="${var.dw_attributes}" var="val" varStatus="attloop">
                          <c:if test="${val.label == 'dw_instrument'}">
                            <sf:select class="form-control"
                              path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                              <sf:option value="">Kein Instrument</sf:option>
                              <sf:options items="${StudyForm.study.instruments}" itemLabel="title" itemValue="title" />
                            </sf:select>
                            <c:set var="contains" value="false" />
                            <c:forEach items="${StudyForm.study.instruments}" var="construct">
                              <c:if test="${construct.title eq val.value}">
                                <c:set var="contains" value="true" />
                              </c:if>
                            </c:forEach>
                            <c:if test="${not contains && val.value ne ''}">
                              <div style="color: red;">
                                <s:message code="record.codebook.instrument.missing" arguments="${val.value}" />
                              </div>
                            </c:if>
                          </c:if>
                        </c:forEach></td>
                      <td><c:forEach items="${var.dw_attributes}" var="val" varStatus="attloop">
                          <c:if test="${val.label == 'dw_itemtext'}">
                            <sf:textarea class="form-control"
                              path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value" />
                          </c:if>
                        </c:forEach></td>
                      <td><c:forEach items="${var.dw_attributes}" var="val" varStatus="attloop">
                          <c:if test="${val.label == 'dw_filtervar'}">
                            <sf:select class="form-control"
                              path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                              <sf:option value="">
                                <s:message code="gen.no" />
                              </sf:option>
                              <sf:option value="1">
                                <s:message code="gen.yes" />
                              </sf:option>
                            </sf:select>
                          </c:if>
                        </c:forEach></td>
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
          <div class="col-sm-12">
            <div class="btn btn-default" onclick="showGlobalAjaxModal('${accessUrl}/modal?varId=-1&modal=values');">
              <s:message code="record.codebook.set.values" />
            </div>
            <div class="btn btn-default" onclick="showGlobalAjaxModal('${accessUrl}/modal?varId=-1&modal=missings');">
              <s:message code="record.codebook.set.missings" />
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12 text-right">
            <a href="${accessUrl}/codebook" class="btn btn-default"><s:message code="codebook.cancel.save" /></a>
            <button type="submit" class="btn btn-success" name="saveCodebook">
              <s:message code="codebook.submit.save" />
            </button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<div class="modal fade" id="valueModal" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
  </div>
</div>
<div class="modal fade" id="errorModal" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content panel-primary">
      <div class="modal-header panel-heading">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">YOU FAILED</h4>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <div class="col-sm-12">Bist du der Depp des Tages?</div>
        </div>
      </div>
      <div class="modal-footer">
        <div class="form-group">
          <div class="col-sm-offset-0 col-md-12">
            <a href="${accessUrl}/codebook" class="btn btn-success"> <s:message code="gen.yes" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="exportModal" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content panel-primary">
      <div class="modal-header panel-heading">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Export</h4>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <div class="col-sm-12">
            <a href="${accessUrl}/export/CSVMatrix" target="_blank">CSVMatrix</a><br /> <a
              href="${accessUrl}/export/CSVCodebook" target="_blank">CSVCodebook</a><br /> <a
              href="${accessUrl}/export/JSON" target="_blank">JSON</a>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <div class="form-group">
          <div class="col-sm-offset-0 col-md-12"></div>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>