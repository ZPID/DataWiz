<div id="accessActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="project.edit.metadata.info" />
      </div>
    </div>
  </div>
  <ul class="list-group">
    <!-- releaseObligation -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.releaseObligation"><s:message code="dmp.edit.releaseObligation" /></label>
          <sf:select class="form-control" path="dmp.releaseObligation">
            <%@ include file="../templates/selectyesno.jsp"%>
          </sf:select>
          <s:message code="dmp.edit.releaseObligation.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
    </li>
    <!-- expectedGroups-->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.expectedGroups"><s:message code="dmp.edit.expectedGroups" /></label>
          <sf:textarea rows="5" path="dmp.expectedGroups" class="form-control" disabled="" />
          <s:message code="dmp.edit.expectedGroups.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
    </li>
    <!-- expectedUsage -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.expectedUsage"><s:message code="dmp.edit.expectedUsage" /></label>
          <sf:textarea rows="5" path="dmp.expectedUsage" class="form-control" disabled="" />
          <s:message code="dmp.edit.expectedUsage.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
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
          <div class="form-group contentPublStrategy0 contentPublStrategy1">
            <div class="col-sm-12">
              <label for="dmp.searchableData"><s:message code="dmp.edit.searchableData" /></label>
              <sf:select class="form-control" path="dmp.searchableData">
                <%@ include file="../templates/selectyesno.jsp"%>
              </sf:select>
              <s:message code="dmp.edit.searchableData.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!-- accessReasonAuthor -->
          <div class="form-group contentPublStrategy1">
            <div class="col-sm-12">
              <label for="dmp.accessReasonAuthor"><s:message code="dmp.edit.accessReasonAuthor" /></label>
              <sf:textarea rows="5" path="dmp.accessReasonAuthor" class="form-control" disabled="" />
              <s:message code="dmp.edit.accessReasonAuthor.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
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
              <div class="form-group" id="contentNoAccessReason">
                <div class="col-sm-12">
                  <label for="dmp.noAccessReasonOther"><s:message code="dmp.edit.noAccessReasonOther" /></label>
                  <sf:textarea rows="5" path="dmp.noAccessReasonOther" class="form-control" disabled="" />
                  <s:message code="dmp.edit.noAccessReasonOther.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </div>
          </div>
          <!-- transferTime -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.transferTime"><s:message code="dmp.edit.transferTime" /></label>
              <sf:textarea rows="5" path="dmp.transferTime" class="form-control" disabled="" />
              <s:message code="dmp.edit.transferTime.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!-- sensitiveData -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.sensitiveData"><s:message code="dmp.edit.sensitiveData" /></label>
              <sf:textarea rows="5" path="dmp.sensitiveData" class="form-control" disabled="" />
              <s:message code="dmp.edit.sensitiveData.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!-- initialUsage -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.initialUsage"><s:message code="dmp.edit.initialUsage" /></label>
              <sf:textarea rows="5" path="dmp.initialUsage" class="form-control" disabled="" />
              <s:message code="dmp.edit.initialUsage.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!-- usageRestriction -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.usageRestriction"><s:message code="dmp.edit.usageRestriction" /></label>
              <sf:textarea rows="5" path="dmp.usageRestriction" class="form-control" disabled="" />
              <s:message code="dmp.edit.usageRestriction.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!-- accessCosts -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.accessCosts"><s:message code="dmp.edit.accessCosts" /></label>
              <sf:select class="form-control" path="dmp.accessCosts" id="selectAccessCosts"
                onchange="switchViewIfSelected('selectAccessCosts', 1);">
                <%@ include file="../templates/selectyesno.jsp"%>
              </sf:select>
              <s:message code="dmp.edit.accessCosts.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
              <!-- accessCostsTxt -->
              <div class="form-group" id="contentAccessCosts">
                <div class="col-sm-12">
                  <label for="dmp.accessCostsTxt"><s:message code="dmp.edit.accessCostsTxt" /></label>
                  <sf:textarea rows="5" path="dmp.accessCostsTxt" class="form-control" disabled="" />
                  <s:message code="dmp.edit.accessCostsTxt.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </div>
          </div>
          <!-- accessTermsImplementation -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.accessTermsImplementation"><s:message code="dmp.edit.accessTermsImplementation" /></label>
              <sf:textarea rows="5" path="dmp.accessTermsImplementation" class="form-control" disabled="" />
              <s:message code="dmp.edit.accessTermsImplementation.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!-- clarifiedRights -->
          <div class="form-group contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.clarifiedRights"><s:message code="dmp.edit.clarifiedRights" /></label>
              <sf:select class="form-control" path="dmp.clarifiedRights" id="selectClarifiedRights"
                onchange="switchViewIfSelected('selectClarifiedRights', 1);">
                <%@ include file="../templates/selectyesno.jsp"%>
              </sf:select>
              <s:message code="dmp.edit.clarifiedRights.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
              <!-- clarifiedRightsTxt -->
              <div class="form-group" id="contentClarifiedRights">
                <div class="col-sm-12">
                  <label for="dmp.clarifiedRightsTxt"><s:message code="dmp.edit.clarifiedRightsTxt" /></label>
                  <sf:textarea rows="5" path="dmp.clarifiedRightsTxt" class="form-control" disabled="" />
                  <s:message code="dmp.edit.clarifiedRightsTxt.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </div>
          </div>
          <!-- acquisitionAgreement -->
          <div class="form-group contentPublStrategy0 contentPublStrategy0">
            <div class="col-sm-12">
              <label for="dmp.acquisitionAgreement"><s:message code="dmp.edit.acquisitionAgreement" /></label>
              <sf:select class="form-control" path="dmp.acquisitionAgreement">
                <%@ include file="../templates/selectyesno.jsp"%>
              </sf:select>
              <s:message code="dmp.edit.acquisitionAgreement.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
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
              <div class="form-group" id="contentUsedPID">
                <div class="col-sm-12">
                  <label for="dmp.usedPIDTxt"><s:message code="dmp.edit.usedPIDTxt" /></label>
                  <sf:textarea rows="5" path="dmp.usedPIDTxt" class="form-control" disabled="" />
                  <s:message code="dmp.edit.usedPIDTxt.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </li>
  </ul>
</div>