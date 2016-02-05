<%@ include file="../templates/includes.jsp"%>
<div id="ethicalActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.ethical.info" />
      </div>
    </div>
  </div>
  <ul class="list-group">
    <!-- dataProtection -->
    <li class="list-group-item"><s:message text="dataProtection" var="dmp_var_name" />
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
          <sf:select class="form-control" path="dmp.${dmp_var_name}" id="select${dmp_var_name}"
            onchange="switchViewIfSelected('select${dmp_var_name}', 1);">
            <%@ include file="../templates/optionYesNo.jsp"%>
          </sf:select>
          <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
          <div id="content${dmp_var_name}">
            <!-- protectionRequirements -->
            <s:message text="protectionRequirements" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
            <!-- consentObtained -->
            <s:message text="consentObtained" var="dmp_var_name" />
            <div class="form-group">
              <div class="col-sm-12">
                <label for="dmp.${dmp_var_name}"><s:message code="dmp.edit.${dmp_var_name}" /></label>
                <sf:select class="form-control" path="dmp.${dmp_var_name}" id="select${dmp_var_name}"
                  onchange="switchViewIfSelectedMulti('select${dmp_var_name}', '0,1');">
                  <%@ include file="../templates/optionYesNo.jsp"%>
                </sf:select>
                <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
                <%@ include file="../templates/helpblock.jsp"%>
                <div class="contentconsentObtained0">
                  <!-- consentObtainedTxt -->
                  <s:message text="consentObtainedTxt" var="dmp_var_name" />
                  <%@ include file="../templates/textarea.jsp"%>
                </div>
                <div class="contentconsentObtained1">
                  <!-- sharingConsidered -->
                  <s:message text="sharingConsidered" var="dmp_var_name" />
                  <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>                        
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </li>
    <!-- contributionsDefined -->
    <li class="list-group-item">
      <s:message text="irbApproval" var="dmp_var_name" />
      <s:message text="0" var="dmp_explain_at" />
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
    <!-- sensitiveDataIncluded -->
    <li class="list-group-item">
      <s:message text="sensitiveDataIncluded" var="dmp_var_name" />
      <s:message text="1" var="dmp_explain_at" />
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
    <!-- externalCopyright -->
    <li class="list-group-item">
      <s:message text="externalCopyright" var="dmp_var_name" />
      <s:message text="1" var="dmp_explain_at" />
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
    <!-- internalCopyright -->
    <li class="list-group-item">
      <s:message text="internalCopyright" var="dmp_var_name" />
      <s:message text="1" var="dmp_explain_at" />
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
  </ul>
</div>