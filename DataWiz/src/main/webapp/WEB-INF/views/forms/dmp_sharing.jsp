<%@ include file="../templates/includes.jsp"%>
<div id="accessActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.sharing.info" />
      </div>
    </div>
  </div>
  <!-- releaseObligation -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.releaseObligation"><s:message code="dmp.edit.releaseObligation" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.releaseObligation">
        <sf:option value="0">
          <s:message code="gen.no" />
        </sf:option>
        <sf:option value="1">
          <s:message code="gen.yes" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.releaseObligation.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- expectedUsage -->
  <c:set var="input_vars" value="dmp.expectedUsage;dmp.edit.expectedUsage; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- publStrategy -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.publStrategy"><s:message code="dmp.edit.publStrategy" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag3}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.publStrategy" id="selectPublStrategy"
        onchange="switchViewIfSelectedMulti('selectPublStrategy', 'repository,author,nopubl');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="repository">
          <s:message code="dmp.edit.publStrategy.repository" />
        </sf:option>
        <sf:option value="author">
          <s:message code="dmp.edit.publStrategy.author" />
        </sf:option>
        <sf:option value="nopubl">
          <s:message code="dmp.edit.publStrategy.nopubl" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.publStrategy.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div class="form-group" id="contentPublStrategy">
    <div class="col-sm-12">
      <!-- depositName -->
      <div class="contentPublStrategy0">
        <c:set var="input_vars" value="dmp.depositName;dmp.edit.depositName; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- searchableData -->
      <div class="contentPublStrategy0 contentPublStrategy1">
        <c:set var="input_vars" value="dmp.searchableData;dmp.edit.searchableData; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- accessReasonAuthor -->
      <div class="contentPublStrategy1">
        <c:set var="input_vars" value="dmp.accessReasonAuthor;dmp.edit.accessReasonAuthor; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- noAccessReason -->
      <div class="form-group contentPublStrategy2">
        <div class="col-sm-12">
          <div class="row">
            <div class="col-xs-10 col-sm-11">
              <label for="dmp.noAccessReason"><s:message code="dmp.edit.noAccessReason" /></label>
            </div>
            <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
              <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /><img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
            </div>
          </div>
          <sf:select class="form-control" path="dmp.noAccessReason" id="selectNoAccessReason"
            onchange="switchViewIfSelected('selectNoAccessReason', 'other');">
            <sf:option value="">
              <s:message code="gen.select" />
            </sf:option>
            <sf:option value="dataprotection">
              <s:message code="dmp.edit.noAccessReason.dataprotection" />
            </sf:option>
            <sf:option value="confidentiality">
              <s:message code="dmp.edit.noAccessReason.confidentiality" />
            </sf:option>
            <sf:option value="other">
              <s:message code="dmp.edit.noAccessReason.other" />
            </sf:option>
          </sf:select>
          <s:message code="dmp.edit.noAccessReason.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
          <!-- noAccessReasonOther -->
          <div id="contentNoAccessReason">
            <s:message text="noAccessReasonOther" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
          </div>
        </div>
      </div>
      <!-- transferTime -->
      <div class="contentPublStrategy0">
        <c:set var="input_vars" value="dmp.transferTime;dmp.edit.transferTime; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- sensitiveData -->
      <div class="contentPublStrategy0">
        <c:set var="input_vars" value="dmp.sensitiveData;dmp.edit.sensitiveData; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- initialUsage -->
      <div class="contentPublStrategy0">
        <c:set var="input_vars" value="dmp.initialUsage;dmp.edit.initialUsage; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- usageRestriction -->
      <div class="contentPublStrategy0">
        <c:set var="input_vars" value="dmp.usageRestriction;dmp.edit.usageRestriction; ; ;row" />
        <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <!-- accessCosts -->
      <div class="contentPublStrategy0">
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-xs-10 col-sm-11">
                <label for="dmp.accessCosts"><s:message code="dmp.edit.accessCosts" /></label>
              </div>
              <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
                <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
                <img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
              </div>
            </div>
            <sf:select class="form-control" path="dmp.accessCosts">
              <sf:option value="0">
                <s:message code="gen.no" />
              </sf:option>
              <sf:option value="1">
                <s:message code="gen.yes" />
              </sf:option>
            </sf:select>
            <s:message code="dmp.edit.accessCosts.help" var="appresmess" />
            <%@ include file="../templates/helpblock.jsp"%>
          </div>
        </div>
      </div>
      <!-- clarifiedRights -->
      <div class="contentPublStrategy0">
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-xs-10 col-sm-11">
                <label for="dmp.clarifiedRights"><s:message code="dmp.edit.clarifiedRights" /></label>
              </div>
              <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
                <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
              </div>
            </div>
            <sf:select class="form-control" path="dmp.clarifiedRights">
              <sf:option value="0">
                <s:message code="gen.no" />
              </sf:option>
              <sf:option value="1">
                <s:message code="gen.yes" />
              </sf:option>
            </sf:select>
            <s:message code="dmp.edit.clarifiedRights.help" var="appresmess" />
            <%@ include file="../templates/helpblock.jsp"%>
          </div>
        </div>
      </div>
      <!-- acquisitionAgreement -->
      <div class="contentPublStrategy0">
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-xs-10 col-sm-11">
                <label for="dmp.acquisitionAgreement"><s:message code="dmp.edit.acquisitionAgreement" /></label>
              </div>
              <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
                <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
                <img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
              </div>
            </div>
            <sf:select class="form-control" path="dmp.acquisitionAgreement">
              <sf:option value="0">
                <s:message code="gen.no" />
              </sf:option>
              <sf:option value="1">
                <s:message code="gen.yes" />
              </sf:option>
            </sf:select>
            <s:message code="dmp.edit.acquisitionAgreement.help" var="appresmess" />
            <%@ include file="../templates/helpblock.jsp"%>
          </div>
        </div>
      </div>
      <!-- usedPID -->
      <div class="form-group contentPublStrategy0">
        <div class="col-sm-12">
          <div class="row">
            <div class="col-xs-10 col-sm-11">
              <label for="dmp.usedPID"><s:message code="dmp.edit.usedPID" /></label>
            </div>
            <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
              <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
              <img src="/DataWiz/static/images/${valimag3}" class="infoImages" />
            </div>
          </div>
          <sf:select class="form-control" path="dmp.usedPID" id="selectUsedPID" onchange="switchViewIfSelected('selectUsedPID', 'other');">
            <sf:option value="">
              <s:message code="gen.select" />
            </sf:option>
            <sf:option value="noPI">
              <s:message code="dmp.edit.usedPID.noPI" />
            </sf:option>
            <sf:option value="doi">
              <s:message code="dmp.edit.usedPID.doi" />
            </sf:option>
            <sf:option value="other">
              <s:message code="dmp.edit.usedPID.other" />
            </sf:option>
          </sf:select>
          <s:message code="dmp.edit.usedPID.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
          <!-- usedPIDTxt -->
          <div id="contentUsedPID">
            <s:message text="usedPIDTxt" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>