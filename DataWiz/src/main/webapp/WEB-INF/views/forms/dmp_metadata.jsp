<%@ include file="../templates/includes.jsp"%>
<c:set var="localeCode" value="${pageContext.response.locale}" />
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
          <div class="row">
            <div class="col-xs-10 col-sm-11">
              <label for="dmp.selectedMetaPurposes"><s:message code="dmp.edit.selectedMetaPurposes" /></label>
            </div>
            <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
              <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <c:forEach items="${ProjectForm.metaPurposes}" var="dtype">
                <label class="btn btn-default col-sm-12 chkboxbtn"><sf:checkbox path="dmp.selectedMetaPurposes" value="${dtype.id}" /> <s:message
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
      <div>
        <c:set var="input_vars" value="dmp.metaDescription;dmp.edit.metaDescription; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </li>
    <!-- metaFramework -->
    <li class="list-group-item">
      <div>
        <c:set var="input_vars" value="dmp.metaFramework;dmp.edit.metaFramework; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </li>
    <!-- metaGeneration -->
    <li class="list-group-item">
      <div>
        <c:set var="input_vars" value="dmp.metaGeneration;dmp.edit.metaGeneration; ; ;row" />
        <c:set var="valimages" value="${valimag1}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </li>
    <!-- metaMonitor -->
    <li class="list-group-item">
      <div>
        <c:set var="input_vars" value="dmp.metaMonitor;dmp.edit.metaMonitor; ; ;row" />
        <c:set var="valimages" value="${valimag1}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </li>
    <!-- metaFormat -->
    <li class="list-group-item">
      <div>
        <c:set var="input_vars" value="dmp.metaFormat;dmp.edit.metaFormat; ; ;row" />
        <c:set var="valimages" value="${valimag1}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </li>
  </ul>
</div>