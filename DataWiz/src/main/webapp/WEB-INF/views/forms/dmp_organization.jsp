<%@ include file="../templates/includes.jsp"%>
<div id="organizationActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.organization.info" />
      </div>
    </div>
  </div>
  <ul class="list-group">
    <!-- frameworkNationality -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.frameworkNationality"><s:message code="dmp.edit.frameworkNationality" /></label>
          <sf:select class="form-control" path="dmp.frameworkNationality" id="selectframeworkNationality"
            onchange="switchViewIfSelected('selectframeworkNationality', 'international_specific');">
            <sf:option value="">
              <s:message code="dmp.edit.select.option.default" />
            </sf:option>
            <sf:option value="national">
              <s:message code="dmp.edit.frameworkNationality.national" />
            </sf:option>
            <sf:option value="international">
              <s:message code="dmp.edit.frameworkNationality.international" />
            </sf:option>
          </sf:select>
          <s:message code="dmp.edit.frameworkNationality.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
          <!-- frameworkNationalityTxt -->
          <div id="contentframeworkNationality">
            <s:message text="frameworkNationalityTxt" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
          </div>
        </div>
      </div>
    </li>
    <!-- responsibleUnit -->
    <li class="list-group-item">
      <s:message text="responsibleUnit" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- involvedInstitutions -->
    <li class="list-group-item">
      <s:message text="involvedInstitutions" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- involvedInformed -->
    <li class="list-group-item"><s:message text="involvedInformed" var="dmp_var_name" />
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.${dmp_var_name}">
            <s:message code="dmp.edit.${dmp_var_name}" />
          </label>
          <sf:select class="form-control" path="dmp.${dmp_var_name}" id="select${dmp_var_name}"
            onchange="switchViewIfSelected('select${dmp_var_name}', 1);">
            <%@ include file="../templates/optionYesNo.jsp"%>
          </sf:select>
          <s:message code="dmp.edit.${dmp_var_name}.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
          <div id="content${dmp_var_name}">
            <!-- contributionsDefined -->
            <s:message text="contributionsDefined" var="dmp_var_name" />
            <s:message text="1" var="dmp_explain_at" />
            <%@ include file="../templates/selectYesNoWithReason.jsp"%>
            <!-- givenConsent -->
            <s:message text="givenConsent" var="dmp_var_name" />
            <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          </div>
        </div>
      </div></li>
    <!-- managementWorkflow -->
    <li class="list-group-item">
      <s:message text="managementWorkflow" var="dmp_var_name" />
      <s:message text="1" var="dmp_explain_at" />
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
    <!-- staffDescription -->
    <li class="list-group-item">
      <s:message text="staffDescription" var="dmp_var_name" /> 
      <s:message text="1" var="dmp_explain_at" />
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
    <!-- funderRequirements -->
    <li class="list-group-item">
      <s:message text="funderRequirements" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- providerRequirements -->
    <li class="list-group-item contentExistingData">
      <s:message text="providerRequirements" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- repoPolicies -->
    <li class="list-group-item contentPublStrategy0 contentPublStrategy1">
      <s:message text="repoPolicies" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- repoPoliciesResponsible -->
    <li class="list-group-item contentPublStrategy0 contentPublStrategy1">
      <s:message text="repoPoliciesResponsible" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- planningAdherence -->
    <li class="list-group-item">
      <s:message text="planningAdherence" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
  </ul>
</div>