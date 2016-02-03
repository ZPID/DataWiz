<div class="form-group">
  <div class="col-sm-12">
    <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
    <sf:select class="form-control" path="dmp.${dmp_var_name}" id="select${dmp_var_name}"
      onchange="switchViewIfSelected('select${dmp_var_name}', ${dmp_explain_at});">
      <%@ include file="selectyesno.jsp"%>
    </sf:select>
    <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
    <%@ include file="helpblock.jsp"%>
    <div class="form-group" id="content${dmp_var_name}">
      <div class="col-sm-12">
        <label for="dmp.${dmp_var_name}Txt"><s:message code="dmp.edit.${dmp_var_name}Txt" /></label>
        <sf:textarea rows="5" path="dmp.${dmp_var_name}Txt" class="form-control" disabled="" />
        <s:message code="dmp.edit.${dmp_var_name}Txt.help" var="appresmess" />
        <%@ include file="helpblock.jsp"%>
      </div>
    </div>
  </div>
</div>