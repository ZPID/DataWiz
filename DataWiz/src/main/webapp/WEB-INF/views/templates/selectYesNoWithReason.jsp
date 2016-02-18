<div class="form-group">
  <div class="col-sm-12">
    <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
    <sf:select class="form-control" path="dmp.${dmp_var_name}" id="select${dmp_var_name}"
      onchange="switchViewIfSelected('select${dmp_var_name}', ${dmp_explain_at});">
      <%@ include file="optionYesNo.jsp"%>
    </sf:select>
    <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
    <%@ include file="helpblock.jsp"%>
    <div class="form-group" id="content${dmp_var_name}">
      <div class="col-sm-12">       
        <s:message text="${dmp_var_name}Txt" var="dmp_var_name" />
        <%@ include file="../templates/textarea.jsp"%>
      </div>
    </div>
  </div>
</div>