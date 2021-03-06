<div class="form-group">
  <div class="col-sm-12">
    <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
    <s:bind path="dmp.${dmp_var_name}">
      <c:choose>
        <c:when test="${status.error}">
          <img src="<c:url value="/static/images/warning.png"/>" class="error_tooltip pull-right" title="${status.errorMessage}" />
          <sf:input path="dmp.${dmp_var_name}" class="form-control" disabled="" style="border: 1px solid red;" />
        </c:when>
        <c:otherwise>
          <sf:input path="dmp.${dmp_var_name}" class="form-control" disabled="" />
        </c:otherwise>
      </c:choose>
    </s:bind>
    <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
    <%@ include file="helpblock.jsp"%>
    <!-- <div id="charNum" class="pull-right"></div>  -->
  </div>
</div>