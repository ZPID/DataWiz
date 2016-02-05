<div class="form-group">
  <div class="col-sm-12">
    <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
    <sf:select class="form-control" path="dmp.${dmp_var_name}">
      <%@ include file="optionYesNo.jsp"%>
    </sf:select>
    <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
    <%@ include file="helpblock.jsp"%>
  </div>
</div>