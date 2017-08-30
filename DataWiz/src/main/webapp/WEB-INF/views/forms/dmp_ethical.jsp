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
  <!-- irbApproval -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.irbApproval"><s:message code="dmp.edit.irbApproval" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.irbApproval" id="selectirbApproval" onchange="switchViewIfSelected('selectirbApproval', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.irbApproval.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentirbApproval">
    <c:set var="input_vars" value="dmp.irbApprovalTxt;dmp.edit.irbApprovalTxt; ; ;row" />
    <c:set var="valimages" value="${valimag1}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- consentObtained -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.consentObtained"><s:message code="dmp.edit.consentObtained" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.consentObtained" id="selectconsentObtained"
        onchange="switchViewIfSelectedMulti('selectconsentObtained', '0,1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.consentObtained.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
      <div class="contentconsentObtained0">
        <!-- consentObtainedTxt -->
        <c:set var="input_vars" value="dmp.consentObtainedTxt;dmp.edit.consentObtainedTxt; ; ;row" />
        <c:set var="valimages" value="${valimag1}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <div class="contentconsentObtained1">
        <!-- sharingConsidered -->
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-xs-10 col-sm-11">
                <label for="dmp.sharingConsidered"><s:message code="dmp.edit.sharingConsidered" /></label>
              </div>
              <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
                <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
                <img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
              </div>
            </div>
            <sf:select class="form-control" path="dmp.sharingConsidered">
              <sf:option value="0">
                <s:message code="gen.no" />
              </sf:option>
              <sf:option value="1">
                <s:message code="gen.yes" />
              </sf:option>
            </sf:select>
            <s:message code="dmp.edit.sharingConsidered.help" var="appresmess" />
            <%@ include file="../templates/helpblock.jsp"%>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- dataProtection -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.dataProtection"><s:message code="dmp.edit.dataProtection" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.dataProtection" id="selectdataProtection"
        onchange="switchViewIfSelected('selectdataProtection', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.dataProtection.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentdataProtection">
    <c:set var="input_vars" value="dmp.protectionRequirements;dmp.edit.protectionRequirements; ; ;row" />
    <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- sensitiveDataIncluded -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.sensitiveDataIncluded"><s:message code="dmp.edit.sensitiveDataIncluded" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.sensitiveDataIncluded" id="selectsensitiveDataIncluded"
        onchange="switchViewIfSelected('selectsensitiveDataIncluded', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.sensitiveDataIncluded.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentsensitiveDataIncluded">
    <c:set var="input_vars" value="dmp.sensitiveDataIncludedTxt;dmp.edit.sensitiveDataIncludedTxt; ; ;row" />
    <c:set var="valimages" value="${valimag2};${valimag3}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- internalCopyright -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.internalCopyright"><s:message code="dmp.edit.internalCopyright" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.internalCopyright" id="selectinternalCopyright"
        onchange="switchViewIfSelected('selectinternalCopyright', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.internalCopyright.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentinternalCopyright">
    <c:set var="input_vars" value="dmp.internalCopyrightTxt;dmp.edit.internalCopyrightTxt; ; ;row" />
    <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- externalCopyright -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.externalCopyright"><s:message code="dmp.edit.externalCopyright" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.externalCopyright" id="selectexternalCopyright"
        onchange="switchViewIfSelected('selectexternalCopyright', '1');">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.externalCopyright.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="contentexternalCopyright">
    <c:set var="input_vars" value="dmp.externalCopyrightTxt;dmp.edit.externalCopyrightTxt; ; ;row" />
    <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
</div>