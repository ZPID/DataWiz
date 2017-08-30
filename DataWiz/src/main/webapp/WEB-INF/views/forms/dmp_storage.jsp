<%@ include file="../templates/includes.jsp"%>
<div id="storageActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.storage.info" />
      </div>
    </div>
  </div>
  <!-- storageResponsible -->
  <c:set var="input_vars" value="dmp.storageResponsible;dmp.edit.storageResponsible; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- storageTechnologies -->
  <c:set var="input_vars" value="dmp.namingCon;dmp.edit.namingCon; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- storagePlaces -->
  <c:set var="input_vars" value="dmp.storagePlaces;dmp.edit.storagePlaces; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- storageBackups -->
  <c:set var="input_vars" value="dmp.storageBackups;dmp.edit.storageBackups; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- storageTransfer -->
  <c:set var="input_vars" value="dmp.storageTransfer;dmp.edit.storageTransfer; ; ;row" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- storageExpectedSize -->
  <c:set var="input_vars" value="dmp.storageExpectedSize;dmp.edit.storageExpectedSize; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- storageRequirements -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.storageRequirements"><s:message code="dmp.edit.storageRequirements" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.storageRequirements" id="selectstorageRequirements"
        onchange="switchViewIfSelected('selectstorageRequirements', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.storageRequirements.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="form-group" id="contentstorageRequirements">
    <div class="col-sm-12">
      <c:set var="input_vars" value="dmp.storageRequirementsTxt;dmp.edit.storageRequirementsTxt; ; ;row" />
      <%@ include file="../templates/gen_textarea.jsp"%>
    </div>
  </div>
  <!-- storageSuccession -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.storageSuccession"><s:message code="dmp.edit.storageSuccession" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.storageSuccession" id="selectstorageSuccession"
        onchange="switchViewIfSelected('selectstorageSuccession', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.storageSuccession.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="form-group" id="contentstorageSuccession">
    <div class="col-sm-12">
      <c:set var="input_vars" value="dmp.storageSuccessionTxt;dmp.edit.storageSuccessionTxt; ; ;row" />
      <%@ include file="../templates/gen_textarea.jsp"%>
    </div>
  </div>
</div>