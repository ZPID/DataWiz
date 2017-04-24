<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <div class="row">
          <div class="col-sm-12">
            <h4>
              <s:message code="record.codebook.headline" />
            </h4>
          </div>
        </div>
        <div>
          <s:message code="record.codebook.info" />
        </div>
      </div>
      <c:url var="accessUrl"
        value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}/codebook" />
      <sf:form action="${accessUrl}" commandName="StudyForm" class="form-horizontal">
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-sm-12 browser_wrapper">
                <table class="table table-striped table-bordered scrollTable" id="fixedHeaderTable">
                  <thead>
                    <tr>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.name" /></th>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.type" /></th>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.label" /></th>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.values" /></th>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.missings" /></th>
                      <th class="codebookTableHide th-width-60 hidefirst"><s:message
                          code="dataset.import.report.codebook.width" /></th>
                      <th class="codebookTableHide th-width-102 hidefirst"><s:message
                          code="dataset.import.report.codebook.dec" /></th>
                      <th class="codebookTableHide th-width-100 hidefirst"><s:message
                          code="dataset.import.report.codebook.cols" /></th>
                      <th class="codebookTableHide th-width-100 hidefirst"><s:message
                          code="dataset.import.report.codebook.aligment" /></th>
                      <th class="codebookTableHide th-width-100 hidefirst"><s:message
                          code="dataset.import.report.codebook.measureLevel" /></th>
                      <th class="codebookTableHide th-width-100 hidefirst"><s:message
                          code="dataset.import.report.codebook.role" /></th>
                      <th class="th-width-30" onclick="$('.codebookTableHide').toggle();fixTableHeaderWidth();">...</th>
                      <c:forEach items="${StudyForm.record.attributes}" var="val" varStatus="attnameloop">
                        <th class="th-width-100"><s:message text="[${fn:substringAfter(val.value, '@')}]" /></th>
                      </c:forEach>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.construct" /></th>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.measocc" /></th>
                      <th class="th-width-200"><s:message code="dataset.import.report.codebook.instrument" /></th>
                      <th class="th-width-300"><s:message code="dataset.import.report.codebook.itemtext" /></th>
                      <th class="th-width-100"><s:message code="dataset.import.report.codebook.filtervar" /></th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody class="scrollTableTbody">
                    <c:forEach items="${StudyForm.record.variables}" var="var" varStatus="loop">
                      <tr>
                        <td class="th-width-200"><strong><sf:input class="form-control varNames"
                              path="record.variables[${loop.count-1}].name" id="varNameId_${loop.count-1}" /></strong></td>
                        <td class="th-width-200"><c:set var="simplifiedType"
                            value="${StudyForm.record.simplifyVarTypes(var.type)}" /> <s:message
                            code="spss.type.${simplifiedType}" /> <c:if test="${simplifiedType ne var.type}">(<s:message
                              code="spss.type.${var.type}" />)</c:if></td>
                        <td class="th-width-200"><sf:textarea class="form-control"
                            path="record.variables[${loop.count-1}].label" /></td>
                        <td style="cursor: pointer;" class="th-width-200"
                          onclick="showAjaxModal('${accessUrl}/modal?varId=${var.id}&modal=values');"><c:forEach
                            items="${var.values}" var="val">
                            <div>
                              <s:message text="${val.value}&nbsp;=&nbsp;&quot;${val.label}&quot;" />
                              <br />
                            </div>
                          </c:forEach></td>
                        <td style="cursor: pointer;" class="th-width-200"
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
                              <s:message
                                text="${var.missingVal1}&nbsp;-&nbsp;${var.missingVal2},&nbsp;${var.missingVal3}" />
                            </c:when>
                          </c:choose></td>
                        <td class="codebookTableHide th-width-60 hidefirst"><s:message text="${var.width}" /></td>
                        <td class="codebookTableHide th-width-102 hidefirst"><s:message text="${var.decimals}" /></td>
                        <td class="codebookTableHide th-width-100 hidefirst"><s:message text="${var.columns}" /></td>
                        <td class="codebookTableHide th-width-100 hidefirst"><s:message
                            code="spss.aligment.${var.aligment}" /></td>
                        <td class="codebookTableHide th-width-100 hidefirst"><s:message
                            code="spss.measureLevel.${var.measureLevel}" /></td>
                        <td class="codebookTableHide th-width-100 hidefirst"><s:message
                            code="spss.role.${var.role}" /></td>
                        <td class="th-width-30">&nbsp;&nbsp;&nbsp;</td>
                        <c:forEach items="${StudyForm.record.attributes}" var="val" varStatus="attnameloop">
                          <td class="th-width-100"><c:forEach items="${var.attributes}" var="att">
                              <c:if test="${fn:startsWith(att.label, fn:substringAfter(val.value, '@'))}">
                                <s:message text="${att.value}" /><br />
                              </c:if>
                            </c:forEach></td>
                        </c:forEach>
                        <td class="th-width-200"><c:forEach items="${var.dw_attributes}" var="val"
                            varStatus="attloop">
                            <c:if test="${val.label == 'dw_construct'}">
                              <c:choose>
                                <c:when test="${not empty StudyForm.study.constructs}">
                                  <sf:select class="form-control"
                                    path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                                    <sf:option value="">
                                      <s:message code="record.codebook.no.construct" />
                                    </sf:option>
                                    <sf:options items="${StudyForm.study.constructs}" itemLabel="name" itemValue="name" />
                                  </sf:select>
                                </c:when>
                                <c:otherwise>
                                  <s:message code="record.codebook.study.constructs.empty" />
                                </c:otherwise>
                              </c:choose>
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
                        <td class="th-width-200"><c:forEach items="${var.dw_attributes}" var="val"
                            varStatus="attloop">
                            <c:if test="${val.label == 'dw_measocc'}">
                              <c:choose>
                                <c:when test="${not empty StudyForm.study.measOcc}">
                                  <sf:select class="form-control"
                                    path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                                    <sf:option value="">
                                      <s:message code="record.codebook.no.measocc" />
                                    </sf:option>
                                    <sf:options items="${StudyForm.study.measOcc}" itemLabel="text" itemValue="text" />
                                  </sf:select>
                                </c:when>
                                <c:otherwise>
                                  <s:message code="record.codebook.study.measOcc.empty" />
                                </c:otherwise>
                              </c:choose>
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
                        <td class="th-width-200"><c:forEach items="${var.dw_attributes}" var="val"
                            varStatus="attloop">
                            <c:if test="${val.label == 'dw_instrument'}">
                              <c:choose>
                                <c:when test="${not empty StudyForm.study.instruments}">
                                  <sf:select class="form-control"
                                    path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value">
                                    <sf:option value="">
                                      <s:message code="record.codebook.no.instrument" />
                                    </sf:option>
                                    <sf:options items="${StudyForm.study.instruments}" itemLabel="title"
                                      itemValue="title" />
                                  </sf:select>
                                </c:when>
                                <c:otherwise>
                                  <s:message code="record.codebook.study.instruments.empty" />
                                </c:otherwise>
                              </c:choose>
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
                        <td class="th-width-300"><c:forEach items="${var.dw_attributes}" var="val"
                            varStatus="attloop">
                            <c:if test="${val.label == 'dw_itemtext'}">
                              <sf:textarea class="form-control"
                                path="record.variables[${loop.count-1}].dw_attributes[${attloop.count-1}].value" />
                            </c:if>
                          </c:forEach></td>
                        <td class="th-width-100"><c:forEach items="${var.dw_attributes}" var="val"
                            varStatus="attloop">
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
                        <td></td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12">
                <div class="btn btn-default btn-sm"
                  onclick="showGlobalAjaxModal('${accessUrl}/modal?varId=-1&modal=values');">
                  <s:message code="record.codebook.set.values" />
                </div>
                <div class="btn btn-default btn-sm"
                  onclick="showGlobalAjaxModal('${accessUrl}/modal?varId=-1&modal=missings');">
                  <s:message code="record.codebook.set.missings" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <s:message code="dataset.import.report.codebook.help" var="appresmess" />
        <%@ include file="templates/helpblock.jsp"%>
        <c:set var="input_vars" value="newChangeLog;record.changeLog;required; ;row" />
        <%@ include file="templates/gen_textarea.jsp"%>
        <div class="form-group">
          <div class="col-sm-6 text-align-left">
            <a href="${accessUrl}" class="btn btn-default btn-sm"><s:message code="codebook.cancel.save" /></a>
          </div>
          <div class="col-sm-6 text-align-right">
            <button type="submit" class="btn btn-success btn-sm" name="saveCodebook">
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
    <!-- content is dynamically loading with ajax -->
  </div>
</div>
<div class="modal fade" id="errorModal" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content panel-primary">
      <div class="modal-header panel-heading">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">
          <s:message code="record.codebook.submit.timeout.modal.head" />
        </h4>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <div class="col-sm-12">
            <s:message code="record.codebook.submit.timeout.modal.info" />
          </div>
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
<%@ include file="templates/footer.jsp"%>