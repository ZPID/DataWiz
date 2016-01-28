
<div id="researchActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="well marginTop1">
      <s:message code="project.edit.metadata.info" />
    </div>
  </div>
  <!-- START Existing Data -->
  <ul class="list-group">
    <li class="list-group-item form-group"><label for="dmp.existingData"><s:message code="dmp.edit.existingData" /></label> <sf:select
        path="dmp.existingData" class="form-control" id="selectExistingData" onchange="switchViewIfSelected('selectExistingData', 1);">
        <s:message code="dmp.edit.select.option.default" var="select_opt" />
        <sf:option value="0" label="${select_opt}" disabled="true" />
        <s:message code="dmp.edit.select.option1" var="select_opt" />
        <sf:option value="1" label="${select_opt}" />
        <s:message code="dmp.edit.select.option2" var="select_opt" />
        <sf:option value="2" label="${select_opt}" />
        <s:message code="dmp.edit.select.option3" var="select_opt" />
        <sf:option value="3" label="${select_opt}" />
      </sf:select>
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.existingData.help" />
        </div>
      </div> <!-- START contentExistingData -->
      <div id="contentExistingData">
        <!-- existingDataRelevance -->
        <div class="form-group row col-sm-12">
          <label for="dmp.existingDataRelevance"><s:message code="dmp.edit.existingDataRelevance" /></label>
          <sf:textarea rows="5" path="dmp.existingDataRelevance" class="form-control" disabled="" />
          <div class="row help-block">
            <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
            <div class="col-sm-11">
              <s:message code="dmp.edit.existingDataRelevance.help" />
            </div>
          </div>
        </div>
        <!-- existingDataIntegration -->
        <div class="form-group row col-sm-12">
          <label for="dmp.existingDataIntegration"><s:message code="dmp.edit.existingDataIntegration" /></label>
          <sf:textarea rows="5" path="dmp.existingDataIntegration" class="form-control" disabled="" />
          <div class="row help-block">
            <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
            <div class="col-sm-11">
              <s:message code="dmp.edit.existingDataIntegration.help" />
            </div>
          </div>
        </div>
      </div></li>
    <!-- END Existing Data -->
    <!-- START Data Types -->
    <li class="list-group-item form-group"><label for="dmp.usedDataTypes"><s:message code="dmp.edit.usedDataTypes" /></label>
      <ul class="list-group col-sm-12">
        <c:forEach items="${ProjectForm.dataTypes}" var="dtype">
          <li class="list-group-item col-sm-4"><c:choose>
              <c:when test="${dtype.id == 0}">
                <label class="btn btn-default" style="width: 100%"><sf:checkbox path="dmp.usedDataTypes" value="${dtype.id}"
                    onchange="switchViewIfChecked('selectOtherDataTypes')" id="selectOtherDataTypes" /> <s:message
                    text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /></label>
              </c:when>
              <c:otherwise>
                <label class="btn btn-default" style="width: 100%"><sf:checkbox path="dmp.usedDataTypes" value="${dtype.id}" /> <s:message
                    text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /></label>
              </c:otherwise>
            </c:choose></li>
        </c:forEach>
      </ul>
      <div class="row help-block col-sm-12">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.usedDataTypes.help" />
        </div>
      </div> <!-- START contentExistingData -->
      <div id="contentOtherDataTypes">
        <!-- existingDataRelevance -->
        <div class="form-group row col-sm-12">
          <label for="dmp.otherDataTypes"><s:message code="dmp.edit.otherDataTypes" /></label>
          <sf:textarea rows="5" path="dmp.otherDataTypes" class="form-control" disabled="" />
        </div>
      </div></li>
    <!-- END Data Types -->
    <!-- START Data Reproducibility -->
    <li class="list-group-item form-group"><label for="dmp.dataReproducibility"><s:message code="dmp.edit.dataReproducibility" /></label>
      <sf:textarea rows="5" path="dmp.dataReproducibility" class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.dataReproducibility.help" />
        </div>
      </div></li>
    <!-- END Data Reproducibility -->
    <!-- START Collecting/Generating Data -->
    <li class="list-group-item form-group"><h6>
        <b><s:message code="dmp.edit.collection.mode" /></b>
      </h6>
      <div class="form-group col-sm-12">
        <ul class="list-group col-sm-6">
          <li class="list-group-item"><label for="dmp.usedCollectionModes"> <s:message code="dmp.edit.collection.mode.present" />
          </label></li>
          <c:forEach items="${ProjectForm.collectionModes}" var="dtype">
            <c:if test="${dtype.investPresent}">
              <li class="list-group-item"><c:choose>
                  <c:when test="${dtype.id == 1}">
                    <sf:checkbox path="dmp.usedCollectionModes" label="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}"
                      value="${dtype.id}" onchange="switchViewIfChecked('selectCollectionModesIP')" id="selectCollectionModesIP" />
                  </c:when>
                  <c:otherwise>
                    <sf:checkbox path="dmp.usedCollectionModes" label="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}"
                      value="${dtype.id}" />
                  </c:otherwise>
                </c:choose></li>
            </c:if>
          </c:forEach>
          <li class="list-group-item col-sm-12" id="contentCollectionModesIP">
            <!-- existingDataRelevance -->
            <div class="form-group">
              <div class="col-sm-12">
                <label for="dmp.otherDataTypes"><s:message code="dmp.edit.collection.mode.present.other" /></label>
                <sf:textarea rows="5" path="dmp.otherCMIP" class="form-control" disabled="" />
              </div>
            </div>
          </li>
        </ul>
        <ul class="list-group col-sm-6">
          <li class="list-group-item"><label for="dmp.usedCollectionModes"> <s:message
                code="dmp.edit.collection.mode.not.present" />
          </label></li>
          <c:forEach items="${ProjectForm.collectionModes}" var="dtype">
            <c:if test="${not dtype.investPresent}">
              <li class="list-group-item"><c:choose>
                  <c:when test="${dtype.id == 2}">
                    <sf:checkbox path="dmp.usedCollectionModes" label="${dtype.nameDE}" value="${dtype.id}"
                      onchange="switchViewIfChecked('selectCollectionModesINP')" id="selectCollectionModesINP" />
                  </c:when>
                  <c:otherwise>
                    <sf:checkbox path="dmp.usedCollectionModes" label="${dtype.nameDE}" value="${dtype.id}" />
                  </c:otherwise>
                </c:choose></li>
            </c:if>
          </c:forEach>
          <li class="list-group-item col-sm-12" id="contentCollectionModesINP">
            <!-- existingDataRelevance -->
            <div class="form-group">
              <div class="col-sm-12">
                <label for="dmp.otherDataTypes"><s:message code="dmp.edit.collection.mode.not.present.other" /></label>
                <sf:textarea rows="5" path="dmp.otherCMINP" class="form-control" disabled="" />
              </div>
            </div>
          </li>
        </ul>
        <div class="row help-block col-sm-12">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message code="dmp.edit.collection.mode.help" />
          </div>
        </div>
      </div> <!--  START Measurement Occasions -->
      <div class="form-group row col-sm-12">
        <label for="dmp.measOccasions"><s:message code="dmp.edit.meas.occasions" /></label>
        <sf:select class="form-control" path="dmp.measOccasions">
          <s:message code="dmp.edit.select.option.default" var="select_opt" />
          <sf:option value="" label="${select_opt}" disabled="true" />
          <s:message code="dmp.edit.meas.occasions.select1" var="select_opt" />
          <sf:option value="single" label="${select_opt}" />
          <s:message code="dmp.edit.meas.occasions.select2" var="select_opt" />
          <sf:option value="multi" label="${select_opt}" />
        </sf:select>
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message code="dmp.edit.meas.occasions.help" />
          </div>
        </div>
      </div> <!--  END Measurement Occasions --></li>
    <!-- END Collecting/Generating Data -->
    <!-- START Data Reproducibility -->
    <li class="list-group-item form-group"><h6>
        <b><s:message code="dmp.edit.reliability.training" /></b>
      </h6> <label for="dmp.reliabilityTraining"><s:message code="dmp.edit.dataReproducibility" /></label> <sf:textarea rows="5"
        path="dmp.reliabilityTraining" class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.reliability.training.help" />
        </div>
      </div> <label for="dmp.multipleMeasurements"><s:message code="dmp.edit.multible.measurement" /></label> <sf:textarea rows="5"
        path="dmp.multipleMeasurements" class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.multible.measurement.help" />
        </div>
      </div> <label for="dmp.qualitityOther"><s:message code="dmp.edit.quality.other" /></label> <sf:textarea rows="5" path="dmp.qualitityOther"
        class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.quality.other.help" />
        </div>
      </div></li>
    <li class="list-group-item form-group"><label for="dmp.fileFormat"><s:message code="dmp.edit.file.format" /></label> <sf:textarea
        rows="5" path="dmp.fileFormat" class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.file.format.help" />
        </div>
      </div></li>
    <!-- END Data Reproducibility -->
    <!-- START Reasons for storage -->
    <li class="list-group-item form-group"><h6>
        <b><s:message code="dmp.edit.storage.headline" /></b>
      </h6>
      <div class="col-sm-12">
        <!-- Working Copy -->
        <div class="form-group">
          <label for="dmp.workingCopy"><s:message code="dmp.edit.storage.working.copy" /></label>
          <sf:select class="form-control" id="selectStorageWC" onchange="switchViewIfSelected('selectStorageWC', 1);" path="dmp.workingCopy">
            <sf:option value="0">
              <s:message code="gen.no" />
            </sf:option>
            <sf:option value="1">
              <s:message code="gen.yes" />
            </sf:option>
          </sf:select>
          <sf:textarea rows="5" class="form-control" path="dmp.workingCopyTxt" id="contentStorageWC" />
        </div>
        <!-- good practice -->
        <div class="form-group">
          <label for="dmp.goodScientific"><s:message code="dmp.edit.storage.good.practice" /></label>
          <sf:select class="form-control" id="selectGoodScientific" onchange="switchViewIfSelected('selectGoodScientific', 1);"
            path="dmp.goodScientific">
            <sf:option value="0">
              <s:message code="gen.no" />
            </sf:option>
            <sf:option value="1">
              <s:message code="gen.yes" />
            </sf:option>
          </sf:select>
          <sf:textarea rows="5" class="form-control" path="dmp.goodScientificTxt" id="contentGoodScientific" />
        </div>
        <!-- subsequent use -->
        <div class="form-group">
          <label for="dmp.subsequentUse"><s:message code="dmp.edit.storage.subsequent.use" /></label>
          <sf:select class="form-control" id="selectSubsequentUse" onchange="switchViewIfSelected('selectSubsequentUse', 1);"
            path="dmp.subsequentUse">
            <sf:option value="0">
              <s:message code="gen.no" />
            </sf:option>
            <sf:option value="1">
              <s:message code="gen.yes" />
            </sf:option>
          </sf:select>
          <sf:textarea rows="5" class="form-control" path="dmp.subsequentUseTxt" id="contentSubsequentUse" />
        </div>
        <!-- requirements -->
        <div class="form-group">
          <label for="dmp.requirements"><s:message code="dmp.edit.storage.requirements" /></label>
          <sf:select class="form-control" id="selectDataRequirements" onchange="switchViewIfSelected('selectDataRequirements', 1);"
            path="dmp.requirements">
            <sf:option value="0">
              <s:message code="gen.no" />
            </sf:option>
            <sf:option value="1">
              <s:message code="gen.yes" />
            </sf:option>
          </sf:select>
          <sf:textarea rows="5" class="form-control" path="dmp.requirementsTxt" id="contentDataRequirements" />
        </div>
        <!-- documentation -->
        <div class="form-group">
          <label for="dmp.documentation"><s:message code="dmp.edit.storage.documentation" /></label>
          <sf:select class="form-control" id="selectDataDocumentation" onchange="switchViewIfSelected('selectDataDocumentation', 1);"
            path="dmp.documentation">
            <sf:option value="0">
              <s:message code="gen.no" />
            </sf:option>
            <sf:option value="1">
              <s:message code="gen.yes" />
            </sf:option>
          </sf:select>
          <sf:textarea rows="5" class="form-control" path="dmp.documentationTxt" id="contentDataDocumentation" />
        </div>
      </div>
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.storage.help" />
        </div>
      </div></li>
    <!--  END Reasons for storage -->
    <!-- Start Data Selection -->
    <li class="list-group-item form-group"><label for="dmp.dataSelection"><s:message code="dmp.edit.data.selection" /></label> <sf:select
        class="form-control" id="selectDataSelection" onchange="switchViewIfSelected('selectDataSelection', 1);" path="dmp.dataSelection">
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
          <s:message code="dmp.edit.data.selection.help" />
        </div>
      </div>
      <div class="col-sm-12" id="contentDataSelection">
        <div class="form-group">
          <label for="dmp.selectionTime"><s:message code="dmp.edit.data.selection.time" /></label>
          <sf:textarea rows="5" class="form-control" path="dmp.selectionTime" id="contentDataDocumentation" />
        </div>
        <div class="form-group">
          <label for="dmp.selectionResp"><s:message code="dmp.edit.data.selection.responsible" /></label>
          <sf:textarea rows="5" class="form-control" path="dmp.selectionResp" id="contentDataDocumentation" />
        </div>
        <div class="form-group">
          <label for="dmp.selectionSoftware"><s:message code="dmp.edit.data.selection.tools" /></label>
          <sf:textarea rows="5" class="form-control" path="dmp.selectionSoftware" id="contentDataDocumentation" />
        </div>
        <div class="form-group">
          <label for="dmp.selectionCriteria"><s:message code="dmp.edit.data.selection.criteria" /></label>
          <sf:textarea rows="5" class="form-control" path="dmp.selectionCriteria" id="contentDataDocumentation" />
        </div>
      </div></li>
    <!-- END Data Selection -->
    <!-- START Storage -->
    <li class="list-group-item form-group"><label for="dmp.storageDuration"><s:message code="dmp.edit.data.storage.time" /></label> <sf:textarea
        rows="5" path="dmp.storageDuration" class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.data.storage.time.help" />
        </div>
      </div></li>
    <li class="list-group-item form-group"><label for="dmp.deleteProcedure"><s:message code="dmp.edit.data.storage.end" /></label> <sf:textarea
        rows="5" path="dmp.deleteProcedure" class="form-control" disabled="" />
      <div class="row help-block">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message code="dmp.edit.data.storage.end.help" />
        </div>
      </div></li>
    <!-- END Storage -->
  </ul>
</div>