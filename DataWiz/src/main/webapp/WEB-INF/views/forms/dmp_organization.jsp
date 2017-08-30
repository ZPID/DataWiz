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
  <!-- frameworkNationality -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.frameworkNationality"><s:message code="dmp.edit.frameworkNationality" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.frameworkNationality" id="selectframeworkNationality"
        onchange="switchViewIfSelected('selectframeworkNationality', 'international');">
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
        <c:set var="input_vars" value="dmp.frameworkNationalityTxt;dmp.edit.frameworkNationalityTxt; ; ;row" />
        <c:set var="valimages" value="${valimag2}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
    </div>
  </div>
  <!-- responsibleUnit -->
  <c:set var="input_vars" value="dmp.responsibleUnit;dmp.edit.responsibleUnit; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- involvedInstitutions -->
  <c:set var="input_vars" value="dmp.involvedInstitutions;dmp.edit.involvedInstitutions; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- involvedInformed -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.involvedInformed"><s:message code="dmp.edit.involvedInformed" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.involvedInformed" id="selectinvolvedInformed"
        onchange="switchViewIfSelected('selectinvolvedInformed', 1);">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.involvedInformed.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
      <div id="contentinvolvedInformed">
        <!-- contributionsDefined -->
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-xs-10 col-sm-11">
                <label for="dmp.contributionsDefined"><s:message code="dmp.edit.contributionsDefined" /></label>
              </div>
              <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
                <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
              </div>
            </div>
            <sf:select class="form-control" path="dmp.contributionsDefined" id="selectcontributionsDefined"
              onchange="switchViewIfSelected('selectcontributionsDefined', '1');">
              <sf:option value="0">
                <s:message code="gen.no" />
              </sf:option>
              <sf:option value="1">
                <s:message code="gen.yes" />
              </sf:option>
            </sf:select>
            <s:message code="dmp.edit.contributionsDefined.help" var="appresmess" />
            <%@ include file="../templates/helpblock.jsp"%>
          </div>
        </div>
        <div id="contentcontributionsDefined">
          <c:set var="input_vars" value="dmp.contributionsDefinedTxt;dmp.edit.contributionsDefinedTxt; ; ;row" />
          <c:set var="valimages" value="${valimag2}" />
          <%@ include file="../templates/gen_textarea.jsp"%>
        </div>
        <!-- givenConsent -->
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-xs-10 col-sm-11">
                <label for="dmp.givenConsent"><s:message code="dmp.edit.givenConsent" /></label>
              </div>
              <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
                <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
              </div>
            </div>
            <sf:select class="form-control" path="dmp.givenConsent">
              <sf:option value="0">
                <s:message code="gen.no" />
              </sf:option>
              <sf:option value="1">
                <s:message code="gen.yes" />
              </sf:option>
            </sf:select>
            <s:message code="dmp.edit.givenConsent.help" var="appresmess" />
            <%@ include file="../templates/helpblock.jsp"%>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- managementWorkflow -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.managementWorkflow"><s:message code="dmp.edit.managementWorkflow" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.managementWorkflow" id="selectmanagementWorkflow"
        onchange="switchViewIfSelected('selectmanagementWorkflow', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.managementWorkflow.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentmanagementWorkflow">
    <c:set var="input_vars" value="dmp.managementWorkflowTxt;dmp.edit.managementWorkflowTxt; ; ;row" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- staffDescription -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.staffDescription"><s:message code="dmp.edit.staffDescription" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.staffDescription" id="selectstaffDescription"
        onchange="switchViewIfSelected('selectstaffDescription', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.staffDescription.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentstaffDescription">
    <c:set var="input_vars" value="dmp.staffDescriptionTxt;dmp.edit.staffDescriptionTxt; ; ;row" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- funderRequirements -->
  <c:set var="input_vars" value="dmp.funderRequirements;dmp.edit.funderRequirements; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- planningAdherence -->
  <c:set var="input_vars" value="dmp.planningAdherence;dmp.edit.planningAdherence; ; ;row" />
  <c:set var="valimages" value="${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
</div>