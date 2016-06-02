<%@ include file="../templates/includes.jsp"%>
<div id="ethicalActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.irb -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.irb"><s:message code="study.irb" /></label>
      <sf:select path="study.irb" class="form-control" id="selectIrb" onchange="switchViewIfSelected('selectIrb', 1);">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="study.irb.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div id="contentIrb">
    <!-- study.irbName -->
    <c:set var="input_vars" value="study.irbName;study.irbName; ; ;row; ; ;" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- study.consent -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.consent"><s:message code="study.consent" /></label>
      <sf:select path="study.consent" class="form-control" id="selectConsent"
        onchange="switchViewIfSelected('selectConsent', 1);">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="study.consent.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div id="contentConsent">
    <!-- study.consentShare -->
    <div class="form-group">
      <div class="col-sm-12">
        <label class="control-label " for="study.consentShare"><s:message code="study.consentShare" /></label>
        <sf:select path="study.consentShare" class="form-control">
          <sf:option value="0">
            <s:message code="gen.no" />
          </sf:option>
          <sf:option value="1">
            <s:message code="gen.yes" />
          </sf:option>
        </sf:select>
        <s:message code="study.consentShare.help" var="appresmess" />
        <%@ include file="../templates/helpblock.jsp"%>
      </div>
    </div>
  </div>
  <!-- study.persDataColl -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.persDataColl"><s:message code="study.persDataColl" /></label>
      <sf:select path="study.persDataColl" class="form-control" id="selectPersDataColl"
        onchange="switchViewIfSelected('selectPersDataColl', 1);">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="study.persDataColl.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div id="contentPersDataColl">
    <!-- study.persDataPres -->
    <div class="form-group">
      <div class="col-sm-12">
        <label class="control-label " for="study.persDataPres"><s:message code="study.persDataPres" /></label>
        <sf:select path="study.persDataPres" class="form-control" id="selectPersDataPres"
          onchange="switchViewIfSelected('selectPersDataPres', 'ANONYMOUS');">
          <sf:option value="">
            <s:message code="gen.select" />
          </sf:option>
          <sf:option value="ANONYMOUS">
            <s:message code="study.persDataPres.anonymous" />
          </sf:option>
          <sf:option value="NON_ANONYMOUS">
            <s:message code="study.persDataPres.non_anonymous" />
          </sf:option>
        </sf:select>
        <s:message code="study.persDataPres.help" var="appresmess" />
        <%@ include file="../templates/helpblock.jsp"%>
      </div>
    </div>
  </div>
  <div id="contentPersDataPres">
    <!-- study.anonymProc -->
    <c:set var="input_vars" value="study.anonymProc;study.anonymProc; ; ;row; ; ;" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
  <!-- study.copyright -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.copyright"><s:message code="study.copyright" /></label>
      <sf:select path="study.copyright" class="form-control" id="selectCopyright"
        onchange="switchViewIfSelected('selectCopyright', 1);">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="study.copyright.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div id="contentCopyright">
    <!-- study.copyrightHolder -->
    <c:set var="input_vars" value="study.copyrightHolder;study.copyrightHolder; ; ;row; ; ;" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
</div>