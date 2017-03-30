<%@ include file="../templates/includes.jsp"%>
<c:url var="accessUrl"
  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}/codebook" />
<sf:form action="${accessUrl}" commandName="VarValues" class="form-horizontal" role="form"
  onsubmit="return checkValueMissingForm('${modalView}')">
  <sf:hidden path="id" />
  <input type="hidden" id="spssType" value="${VarValues.type}" />
  <c:set var="simplifiedType" value="${StudyForm.record.simplifyVarTypes(VarValues.type)}" />
  <sf:hidden path="type" value="${simplifiedType}" />
  <c:choose>
    <c:when test="${modalView eq 'values'}">
      <div class="modal-content panel-primary">
        <div class="modal-header panel-heading">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">
            <s:message code="record.codebook.modal.value.header" arguments="${VarValues.name}" />
          </h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="well marginTop1">
                <c:choose>
                  <c:when test="${simplifiedType == 'SPSS_FMT_F'}">
                    <s:message code="record.codebook.modal.value.info.number" />
                  </c:when>
                  <c:when test="${simplifiedType == 'SPSS_FMT_DATE'}">
                    <s:message code="record.codebook.modal.value.info.date" />
                  </c:when>
                  <c:otherwise>
                    <s:message code="record.codebook.modal.value.info.string" />
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <div class="col-sm-3">
                <s:message code="record.codebook.modal.value" />
              </div>
              <div class="col-sm-1"></div>
              <div class="col-sm-6">
                <s:message code="record.codebook.modal.label" />
              </div>
              <div class="col-sm-2"></div>
            </div>
          </div>
          <div class="valvar_wrap">
            <c:forEach items="${VarValues.values}" var="val" varStatus="loop">
              <div class="form-group" id="values${loop.count-1}">
                <sf:hidden path="values[${loop.count-1}].id" id="values${loop.count-1}id" />
                <div class="col-sm-12">
                  <div class="col-sm-3">
                    <c:choose>
                      <c:when test="${simplifiedType == 'SPSS_FMT_F'}">
                        <sf:input id="values${loop.count-1}val" class="form-control"
                          path="values[${loop.count-1}].value" onkeyup="checkNumberField('values${loop.count-1}val')" />
                      </c:when>
                      <c:when test="${simplifiedType == 'SPSS_FMT_DATE'}">
                        <sf:input id="values${loop.count-1}val" class="form-control uppercase"
                          path="values[${loop.count-1}].value" onkeyup="checkDateField('values${loop.count-1}val')" />
                      </c:when>
                      <c:otherwise>
                        <sf:input id="values${loop.count-1}val" class="form-control"
                          path="values[${loop.count-1}].value" />
                      </c:otherwise>
                    </c:choose>
                  </div>
                  <div class="col-sm-1">
                    <s:message text="=" />
                  </div>
                  <div class="col-sm-6">
                    <sf:input id="values${loop.count-1}label" class="form-control" path="values[${loop.count-1}].label" />
                  </div>
                  <div class="col-sm-2">
                    <button type="button" class="btn btn-danger" onclick="delVarValues(${loop.count-1});return false;">
                      <span class="glyphicon glyphicon-remove" aria-hidden="false"></span>
                    </button>
                  </div>
                </div>
              </div>
            </c:forEach>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <div class="col-sm-offset-10 col-sm-2">
                <button type="button" class="btn btn-success" onclick="addValueLabel();return false;">
                  <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="form-group">
            <div class="col-md-6 text-align-left">
              <button class="btn btn-default btn-sm" data-dismiss="modal">
                <s:message code="gen.close" />
              </button>
            </div>
            <div class="col-md-6 text-align-right">
              <sf:button class="btn btn-success btn-sm" name="setValues">
                <s:message code="record.codebook.modal.values.set" />
              </sf:button>
            </div>
          </div>
        </div>
      </div>
    </c:when>
    <c:when test="${modalView eq 'missings'}">
      <div class="modal-content panel-primary">
        <div class="modal-header panel-heading">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">
            <s:message code="record.codebook.modal.missings.header" arguments="${VarValues.name}" />
          </h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="well marginTop1">
                <c:choose>
                  <c:when test="${simplifiedType == 'SPSS_FMT_F'}">
                    <s:message code="record.codebook.modal.missings.info.number" />
                  </c:when>
                  <c:when test="${simplifiedType == 'SPSS_FMT_DATE'}">
                    <s:message code="record.codebook.modal.missings.info.date" />
                    <c:set value="uppercase" var="upperCaseCSS"></c:set>
                  </c:when>
                  <c:otherwise>
                    <s:message code="record.codebook.modal.missings.info.string" />
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <sf:select path="missingFormat" class="form-control" onchange="changeMissingFields(null);">
                <sf:option value="SPSS_NO_MISSVAL">
                  <s:message code="spss.missings.SPSS_NO_MISSVAL" />
                </sf:option>
                <sf:option value="SPSS_ONE_MISSVAL">
                  <s:message code="spss.missings.SPSS_ONE_MISSVAL" />
                </sf:option>
                <sf:option value="SPSS_TWO_MISSVAL">
                  <s:message code="spss.missings.SPSS_TWO_MISSVAL" />
                </sf:option>
                <sf:option value="SPSS_THREE_MISSVAL">
                  <s:message code="spss.missings.SPSS_THREE_MISSVAL" />
                </sf:option>
                <c:if test="${simplifiedType ne 'SPSS_FMT_A'}">
                  <sf:option value="SPSS_MISS_RANGE">
                    <s:message code="spss.missings.SPSS_MISS_RANGE" />
                  </sf:option>
                  <sf:option value="SPSS_MISS_RANGEANDVAL">
                    <s:message code="spss.missings.SPSS_MISS_RANGEANDVAL" />
                  </sf:option>
                </c:if>
              </sf:select>
              <script>
                changeMissingFields(null);
        	  </script>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <div class="col-sm-3" style="white-space: nowrap;">
                <sf:input path="missingVal1" class="form-control ${upperCaseCSS}"
                  onkeyup="checkMissingField('missingVal1');" />
              </div>
              <div class="col-sm-1" id="missingSep1" style="font-size: 26px;">,</div>
              <div class="col-sm-3">
                <sf:input path="missingVal2" class="form-control ${upperCaseCSS}"
                  onkeyup="checkMissingField('missingVal2');" />
              </div>
              <div class="col-sm-1" id="missingSep2" style="font-size: 26px;">,</div>
              <div class="col-sm-3">
                <sf:input path="missingVal3" class="form-control ${upperCaseCSS}"
                  onkeyup="checkMissingField('missingVal3');" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="form-group">
            <div class="col-sm-6 text-align-left">
              <button class="btn btn-default btn-sm" data-dismiss="modal">
                <s:message code="gen.close" />
              </button>
            </div>
            <div class="col-sm-6 text-align-right">
              <sf:button type="submit" class="btn btn-success btn-sm" name="setMissings">
                <s:message code="record.codebook.modal.missings.set" />
              </sf:button>
            </div>
          </div>
        </div>
      </div>
    </c:when>
  </c:choose>
</sf:form>