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
      <div class="row">
        <div class="col-sm-12">
          <div class="well marginTop1">
            <s:message code="record.modal.global.values.info" />
          </div>
        </div>
      </div>
      <div class="form-group">
        <div class="col-sm-12">
          <div class="col-sm-3 text-align-center">
            <s:message code="record.codebook.modal.type" />
          </div>
          <div class="col-sm-3 text-align-center">
            <s:message code="record.codebook.modal.value" />
          </div>
          <div class="col-sm-1"></div>
          <div class="col-sm-3 text-align-center">
            <s:message code="record.codebook.modal.label" />
          </div>
          <div class="col-sm-1"></div>
        </div>
      </div>
      <ul class="list-group valvar_wrap">
        <li class="list-group-item" id="values0">
          <div class="row">
            <div class="col-sm-3">
              <sf:select path="values[0].id" class="form-control" id="values0id" onchange="checkType(0, null);">
                <sf:option value="1">
                  <s:message code="spss.type.SPSS_FMT_A" />
                </sf:option>
                <sf:option value="5">
                  <s:message code="spss.type.SPSS_FMT_F" />
                </sf:option>
              </sf:select>
            </div>
            <div class="col-sm-3">
              <sf:input id="values0val" class="form-control" path="values[0].value" onkeyup="checkType(0, null);" />
            </div>
            <div class="col-sm-1">
              <s:message text="=" />
            </div>
            <div class="col-sm-3">
              <sf:input id="values0label" class="form-control" path="values[0].label" />
            </div>
            <div class="col-sm-1">
              <button class="btn btn-danger" onclick="delVarValues(0);return false;">
                <span class="glyphicon glyphicon-remove" aria-hidden="false"></span>
              </button>
            </div>
          </div>
        </li>
      </ul>
      <div class="row">
        <div class="col-sm-12 text-align-right">
          <button class="btn btn-success"
            onclick="addGlobalValueLabel('<s:message code="spss.type.SPSS_FMT_A"/>', '<s:message code="spss.type.SPSS_FMT_F"/>', '<s:message code="spss.type.SPSS_FMT_DATE"/>');return false;">
            <span class="glyphicon glyphicon-plus" aria-hidden="false"></span>
          </button>
        </div>
      </div>
    </div>
    <div class="modal-footer">
      <div class="row">
        <div class="col-md-6 text-align-left">
          <button class="btn btn-default btn-sm" data-dismiss="modal">
            <s:message code="gen.close" />
          </button>
        </div>
        <div class="col-md-6 text-align-right">
          <sf:button type="submit" class="btn btn-success btn-sm" name="setValues">
            <s:message code="record.codebook.modal.values.set" />
          </sf:button>
        </div>
      </div>
    </div>
  </div>
</sf:form>