<%@ include file="../templates/includes.jsp"%>
<c:url var="accessUrl"
  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}/codebook" />
<sf:form action="${accessUrl}" commandName="VarValues" class="form-horizontal" role="form"
  onsubmit="return checkValueForm()">
  <sf:hidden path="id" />
  <div class="modal-content panel-primary">
    <div class="modal-header panel-heading">
      <button type="button" class="close" data-dismiss="modal">&times;</button>
      <h4 class="modal-title">
        <s:message code="record.codebook.modal.global.values.header" />
      </h4>
    </div>
    <div class="modal-body">
      <div class="form-group">
        <div class="col-sm-12">
          <div class="col-sm-3">
            <s:message code="record.codebook.modal.type" />
          </div>
          <div class="col-sm-3">
            <s:message code="record.codebook.modal.value" />
          </div>
          <div class="col-sm-1"></div>
          <div class="col-sm-4">
            <s:message code="record.codebook.modal.label" />
          </div>
          <div class="col-sm-1"></div>
        </div>
      </div>
      <div class="valvar_wrap">
        <div class="form-group" id="values0">
          <div class="col-sm-12">
            <div class="col-sm-3">
              <sf:select path="values[0].id" class="form-control" id="values0id" onchange="checkType(0, null);">
                <sf:option value="1">
                  <s:message code="spss.type.SPSS_FMT_A" />
                </sf:option>
                <sf:option value="5">
                  <s:message code="spss.type.SPSS_FMT_F" />
                </sf:option>
                <sf:option value="20">
                  <s:message code="spss.type.SPSS_FMT_DATE" />
                </sf:option>
              </sf:select>
            </div>
            <div class="col-sm-3">
              <sf:input id="values0val" class="form-control" path="values[0].value" onkeyup="checkType(0, null);" />
            </div>
            <div class="col-sm-1">
              <s:message text="=" />
            </div>
            <div class="col-sm-4">
              <sf:input id="values0label" class="form-control" path="values[0].label" />
            </div>
            <div class="col-sm-1">
              <button class="btn btn-danger" onclick="delVarValues(0);return false;">X</button>
            </div>
          </div>
        </div>
      </div>
      <div class="form-group">
        <div class="col-sm-12">
          <button class="btn btn-success"
            onclick="addGlobalValueLabel('<s:message code="spss.type.SPSS_FMT_A"/>', '<s:message code="spss.type.SPSS_FMT_F"/>', '<s:message code="spss.type.SPSS_FMT_DATE"/>');return false;">+</button>
        </div>
      </div>
    </div>
    <div class="modal-footer">
      <div class="form-group">
        <div class="col-sm-offset-0 col-md-12">
          <button class="btn btn-default" data-dismiss="modal">
            <s:message code="gen.close" />
          </button>
          <sf:button type="submit" class="btn btn-success" name="setValues">
            <s:message code="record.codebook.modal.values.set" />
          </sf:button>
        </div>
      </div>
    </div>
  </div>
</sf:form>