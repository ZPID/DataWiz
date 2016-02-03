<div id="metadataActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.metadata.info" />
      </div>
    </div>
  </div>
  <ul class="list-group">
    <!-- selectedMetaPurposes -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.selectedMetaPurposes"><s:message code="dmp.edit.selectedMetaPurposes" /></label>
          <div class="form-group">
            <div class="col-sm-12">
              <c:forEach items="${ProjectForm.metaPurposes}" var="dtype">
                <label class="btn btn-default col-sm-12" style="text-align: left;"><sf:checkbox
                    path="dmp.selectedMetaPurposes" value="${dtype.id}" /> <s:message
                    text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /></label>
              </c:forEach>
            </div>
          </div>
          <s:message code="dmp.edit.selectedMetaPurposes.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
    </li>
    <!-- metaDescription -->
    <li class="list-group-item">
      <s:message text="metaDescription" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- metaFramework -->
    <li class="list-group-item">
      <s:message text="metaFramework" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- metaGeneration -->
    <li class="list-group-item">
      <s:message text="metaGeneration" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- metaMonitor -->
    <li class="list-group-item">
      <s:message text="metaMonitor" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- metaFormat -->
    <li class="list-group-item">
      <s:message text="metaFormat" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
  </ul>
</div>