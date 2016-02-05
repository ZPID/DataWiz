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
  <ul class="list-group">
    <!-- releaseObligation -->
    <li class="list-group-item">
      <s:message text="releaseObligation" var="dmp_var_name" /> 
      <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
    </li>
    <!-- expectedGroups-->
    <li class="list-group-item">
      <s:message text="expectedGroups" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- expectedUsage -->
    <li class="list-group-item">
      <s:message text="expectedUsage" var="dmp_var_name" />
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- publStrategy -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.publStrategy"><s:message code="dmp.edit.publStrategy" /></label>
          <sf:select class="form-control" path="dmp.publStrategy" id="selectPublStrategy"
            onchange="switchViewIfSelectedMulti('selectPublStrategy', 'repository,author,nopubl');">
            <sf:option value="">
              <s:message code="dmp.edit.select.option.default" />
            </sf:option>
            <sf:option value="repository">
              <s:message code="dmp.edit.publStrategy.option1" />
            </sf:option>
            <sf:option value="author">
              <s:message code="dmp.edit.publStrategy.option2" />
            </sf:option>
            <sf:option value="nopubl">
              <s:message code="dmp.edit.publStrategy.option3" />
            </sf:option>
          </sf:select>
          <s:message code="dmp.edit.publStrategy.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
      <div class="form-group" id="contentPublStrategy">
        <div class="col-sm-12">
          <!-- searchableData -->
          <div class="contentPublStrategy0 contentPublStrategy1">
            <s:message text="searchableData" var="dmp_var_name" /> 
            <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          </div>
          <!-- accessReasonAuthor -->
          <div class="contentPublStrategy1">
            <s:message text="accessReasonAuthor" var="dmp_var_name" /> 
            <%@ include file="../templates/textarea.jsp"%>
          </div>
          <!-- noAccessReason -->
          <div class="form-group contentPublStrategy2">
            <div class="col-sm-12">
              <label for="dmp.noAccessReason"><s:message code="dmp.edit.noAccessReason" /></label>
              <sf:select class="form-control" path="dmp.noAccessReason" id="selectNoAccessReason"
                onchange="switchViewIfSelected('selectNoAccessReason', 'other');">
                <sf:option value="">
                  <s:message code="dmp.edit.select.option.default" />
                </sf:option>
                <sf:option value="dataprotection">
                  <s:message code="dmp.edit.noAccessReason.option1" />
                </sf:option>
                <sf:option value="confidentiality">
                  <s:message code="dmp.edit.noAccessReason.option2" />
                </sf:option>
                <sf:option value="other">
                  <s:message code="dmp.edit.noAccessReason.option3" />
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
            <s:message text="transferTime" var="dmp_var_name" /> 
            <%@ include file="../templates/textarea.jsp"%>
          </div>
          <!-- sensitiveData -->
          <div class="contentPublStrategy0">
            <s:message text="sensitiveData" var="dmp_var_name" /> 
            <%@ include file="../templates/textarea.jsp"%>
          </div>
          <!-- initialUsage -->
          <div class="contentPublStrategy0">
            <s:message text="initialUsage" var="dmp_var_name" /> 
            <%@ include file="../templates/textarea.jsp"%>
          </div>
          <!-- usageRestriction -->
          <div class="contentPublStrategy0">
            <s:message text="usageRestriction" var="dmp_var_name" /> 
            <%@ include file="../templates/textarea.jsp"%>
          </div>
          <!-- accessCosts -->
          <div class="contentPublStrategy0">
            <s:message text="accessCosts" var="dmp_var_name" />
            <s:message text="1" var="dmp_explain_at" />  
            <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          </div>
          <!-- accessTermsImplementation -->
          <div class="contentPublStrategy0">
            <s:message text="accessTermsImplementation" var="dmp_var_name" /> 
            <%@ include file="../templates/textarea.jsp"%>
          </div>
          <!-- clarifiedRights -->
          <div class="contentPublStrategy0">
            <s:message text="clarifiedRights" var="dmp_var_name" />
            <s:message text="1" var="dmp_explain_at" />  
            <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          </div>
          <!-- acquisitionAgreement -->
          <div class="contentPublStrategy0">
            <s:message text="acquisitionAgreement" var="dmp_var_name" />
            <%@ include file="../templates/selectYesNoWithoutReason.jsp"%>
          </div>
          <!-- usedPID -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.usedPID"><s:message code="dmp.edit.usedPID" /></label>
              <sf:select class="form-control" path="dmp.usedPID" id="selectUsedPID"
                onchange="switchViewIfSelected('selectUsedPID', 'other');">
                <sf:option value="">
                  <s:message code="dmp.edit.select.option.default" />
                </sf:option>
                <sf:option value="DOI">
                  <s:message code="dmp.edit.usedPID.option1" />
                </sf:option>
                <sf:option value="other">
                  <s:message code="dmp.edit.usedPID.option2" />
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
    </li>
  </ul>
</div>