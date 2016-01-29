<div class="form-group">
  <div class="col-sm-12">
    <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
    <sf:textarea rows="5" path="dmp.${dmp_var_name}" class="form-control" disabled="" />
    <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
    <%@ include file="helpblock.jsp"%>
  </div>
</div>