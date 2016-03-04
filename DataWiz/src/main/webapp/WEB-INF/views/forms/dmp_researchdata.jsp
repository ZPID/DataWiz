<%@ include file="../templates/includes.jsp"%>
<c:set var="localeCode" value="${pageContext.response.locale}" />
<div id="researchActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.reserachdata.info" />
      </div>
    </div>
  </div>
  <!-- existingData -->
  <ul class="list-group">
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.existingData"><s:message code="dmp.edit.existingData" /></label>
          <sf:select path="dmp.existingData" class="form-control" id="selectExistingData"
            onchange="switchViewIfSelected('selectExistingData', 'existingUsed');">
            <sf:option value="">
              <s:message code="dmp.edit.select.option.default" />
            </sf:option>
            <sf:option value="existingUsed">
              <s:message code="dmp.edit.existingData.option1" />
            </sf:option>
            <sf:option value="notFound">
              <s:message code="dmp.edit.existingData.option2" />
            </sf:option>
            <sf:option value="noSearch">
              <s:message code="dmp.edit.existingData.option3" />
            </sf:option>
          </sf:select>
          <s:message code="dmp.edit.existingData.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
          <!-- START contentExistingData -->
          <div id="contentExistingData">
            <!-- dataCitation -->
            <s:message text="dataCitation" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
            <!-- existingDataRelevance -->
            <s:message text="existingDataRelevance" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
            <!-- existingDataIntegration -->
            <s:message text="existingDataIntegration" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
          </div>
        </div>
      </div>
    </li>
    <!-- usedDataTypes -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.usedDataTypes"><s:message code="dmp.edit.usedDataTypes" /></label>
          <div class="form-group">
            <c:forEach items="${ProjectForm.dataTypes}" var="dtype" varStatus="state">
              <div class="col-sm-4">
                <c:choose>
                  <c:when test="${dtype.id == 20}">
                    <label class="btn btn-default chkboxbtn"> <sf:checkbox path="dmp.usedDataTypes" value="${dtype.id}"
                        onchange="switchViewIfChecked('selectOtherDataTypes');" id="selectOtherDataTypes" /> <s:message
                        text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" />
                    </label>
                  </c:when>
                  <c:otherwise>
                    <label class="btn btn-default chkboxbtn"> <sf:checkbox path="dmp.usedDataTypes" value="${dtype.id}" /> <s:message
                        text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" />
                    </label>
                  </c:otherwise>
                </c:choose>
              </div>
            </c:forEach>
            <div class="col-sm-12">
              <s:message code="dmp.edit.usedDataTypes.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <div id="contentOtherDataTypes">
            <!-- otherDataTypes -->
            <s:message text="otherDataTypes" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
          </div>
        </div>
      </div>
    </li>
    <!-- dataReproducibility -->
    <li class="list-group-item"><s:message text="dataReproducibility" var="dmp_var_name" /> <%@ include
        file="../templates/textarea.jsp"%></li>
    <!-- usedCollectionModes -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <h6>
            <b><s:message code="dmp.edit.usedCollectionModes" /></b>
          </h6>
          <div class="form-group">
            <div class="col-sm-6">
              <ul class="list-group">
                <li class="list-group-item"><label for="dmp.usedCollectionModes"> <s:message
                      code="dmp.edit.usedCollectionModes.present" />
                </label> <c:forEach items="${ProjectForm.collectionModes}" var="dtype">
                    <c:if test="${dtype.investPresent}">
                      <c:choose>
                        <c:when test="${dtype.id == 1}">
                          <label class="btn btn-default chkboxbtn"><sf:checkbox path="dmp.usedCollectionModes" value="${dtype.id}"
                              onchange="switchViewIfChecked('selectCollectionModesIP')" id="selectCollectionModesIP" /> <s:message
                              text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                        </c:when>
                        <c:otherwise>
                          <label class="btn btn-default chkboxbtn"><sf:checkbox path="dmp.usedCollectionModes" value="${dtype.id}" />
                            <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </c:forEach> <!-- otherCMIP -->
                  <div id="contentCollectionModesIP">
                    <s:message text="otherCMIP" var="dmp_var_name" />
                    <%@ include file="../templates/textarea.jsp"%>
                  </div></li>
              </ul>
            </div>
            <div class="col-sm-6">
              <ul class="list-group">
                <li class="list-group-item"><label for="dmp.usedCollectionModes"> <s:message
                      code="dmp.edit.usedCollectionModes.not.present" />
                </label> <c:forEach items="${ProjectForm.collectionModes}" var="dtype">
                    <c:if test="${not dtype.investPresent}">
                      <c:choose>
                        <c:when test="${dtype.id == 2}">
                          <label class="btn btn-default" style="width: 100%; text-align: left;"><sf:checkbox
                              path="dmp.usedCollectionModes" value="${dtype.id}" onchange="switchViewIfChecked('selectCollectionModesINP')"
                              id="selectCollectionModesINP" /> <s:message text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                        </c:when>
                        <c:otherwise>
                          <label class="btn btn-default" style="width: 100%; text-align: left;"><sf:checkbox
                              path="dmp.usedCollectionModes" value="${dtype.id}" /> <s:message
                              text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /> </label>
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </c:forEach> <!-- otherCMINP -->
                  <div id="contentCollectionModesINP">
                    <s:message text="otherCMINP" var="dmp_var_name" />
                    <%@ include file="../templates/textarea.jsp"%>
                  </div></li>
              </ul>
            </div>
            <div class="col-sm-12">
              <s:message code="dmp.edit.usedCollectionModes.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
          <!--  measOccasions -->
          <div class="form-group">
            <div class="col-sm-12">
              <label for="dmp.measOccasions"><s:message code="dmp.edit.measOccasions" /></label>
              <sf:select class="form-control" path="dmp.measOccasions">
                <s:message code="dmp.edit.select.option.default" var="select_opt" />
                <sf:option value="" label="${select_opt}" disabled="true" />
                <s:message code="dmp.edit.measOccasions.select1" var="select_opt" />
                <sf:option value="single" label="${select_opt}" />
                <s:message code="dmp.edit.measOccasions.select2" var="select_opt" />
                <sf:option value="multi" label="${select_opt}" />
              </sf:select>
              <s:message code="dmp.edit.measOccasions.help" var="appresmess" />
              <%@ include file="../templates/helpblock.jsp"%>
            </div>
          </div>
        </div>
      </div>
    </li>
    <!-- START Data Reproducibility -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <h6>
            <b><s:message code="dmp.edit.qualityAssurance" /></b>
          </h6>
          <!--  reliabilityTraining -->
          <s:message text="reliabilityTraining" var="dmp_var_name" />
          <%@ include file="../templates/textarea.jsp"%>
          <!--  multipleMeasurements -->
          <s:message text="multipleMeasurements" var="dmp_var_name" />
          <%@ include file="../templates/textarea.jsp"%>
          <!--  qualitityOther -->
          <s:message text="qualitityOther" var="dmp_var_name" />
          <%@ include file="../templates/textarea.jsp"%>
        </div>
      </div>
    </li>
    <!--  fileFormat -->
    <li class="list-group-item"><s:message text="fileFormat" var="dmp_var_name" /> <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- END Data Reproducibility -->
    <!-- START Reasons for storage -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <h6>
            <b><s:message code="dmp.edit.storage.headline" /></b>
          </h6>
          <!-- workingCopy -->
          <s:message text="workingCopy" var="dmp_var_name" />
          <s:message text="1" var="dmp_explain_at" />
          <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          <!-- goodScientific -->
          <s:message text="goodScientific" var="dmp_var_name" />
          <s:message text="1" var="dmp_explain_at" />
          <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          <!-- subsequentUse -->
          <s:message text="subsequentUse" var="dmp_var_name" />
          <s:message text="1" var="dmp_explain_at" />
          <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          <!-- requirements -->
          <s:message text="requirements" var="dmp_var_name" />
          <s:message text="1" var="dmp_explain_at" />
          <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          <!-- documentation -->
          <s:message text="documentation" var="dmp_var_name" />
          <s:message text="1" var="dmp_explain_at" />
          <%@ include file="../templates/selectYesNoWithReason.jsp"%>
          <s:message code="dmp.edit.storage.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
    </li>
    <!-- dataSelection -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.dataSelection"><s:message code="dmp.edit.dataSelection" /></label>
          <sf:select class="form-control" id="selectDataSelection" onchange="switchViewIfSelected('selectDataSelection', 1);"
            path="dmp.dataSelection">
            <sf:option value="0">
              <s:message code="gen.no" />
            </sf:option>
            <sf:option value="1">
              <s:message code="gen.yes" />
            </sf:option>
          </sf:select>
          <div class="row help-block">
            <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
            <div class="col-sm-11">
              <s:message code="dmp.edit.dataSelection.help" />
            </div>
          </div>
          <div id="contentDataSelection">
            <!-- selectionTime -->
            <s:message text="selectionTime" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
            <!-- selectionResp -->
            <s:message text="selectionResp" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
            <!-- selectionSoftware -->
            <s:message text="selectionSoftware" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
            <!-- selectionCriteria -->
            <s:message text="selectionCriteria" var="dmp_var_name" />
            <%@ include file="../templates/textarea.jsp"%>
          </div>
        </div>
      </div>
    </li>
    <!-- storageDuration -->
    <li class="list-group-item"><s:message text="storageDuration" var="dmp_var_name" /> <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- deleteProcedure -->
    <li class="list-group-item"><s:message text="deleteProcedure" var="dmp_var_name" /> <%@ include file="../templates/textarea.jsp"%>
    </li>
  </ul>
</div>