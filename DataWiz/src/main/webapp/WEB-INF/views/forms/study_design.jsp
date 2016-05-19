<%@ include file="../templates/includes.jsp"%>
<div id="designActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.objectives -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.objectives"><s:message code="study.objectives" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.objectives}" varStatus="loop">
          <div class="row margin-bottom-10">
            <div class="col-sm-10">
              <sf:textarea rows="1" path="study.objectives[${loop.index}].objective" class="form-control " />
            </div>
            <div class="col-sm-2">
              <sf:select class="form-control col-sm-2" path="study.objectives[${loop.index}].type">
                <sf:option value="">
                  <s:message code="gen.select" />
                </sf:option>
                <sf:option value="primary">
                  <s:message code="study.objectives.primary" />
                </sf:option>
                <sf:option value="secondary">
                  <s:message code="study.objectives.secondary" />
                </sf:option>
                <sf:option value="exploratory">
                  <s:message code="study.objectives.exploratory" />
                </sf:option>
              </sf:select>
            </div>
          </div>
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.objectives.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.relTheorys -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.relTheorys"><s:message code="study.relTheorys" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.relTheorys}" varStatus="loop">
          <sf:textarea rows="1" path="study.relTheorys[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.relTheorys.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.repMeasures -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.repMeasures"><s:message code="study.repMeasures" /></label>
      <sf:select path="study.repMeasures" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="single">
          <s:message code="study.repMeasures.single" />
        </sf:option>
        <sf:option value="multiple">
          <s:message code="study.repMeasures.multiple" />
        </sf:option>
      </sf:select>
      <s:message code="study.repMeasures.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.measOccName -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.measOccName"><s:message code="study.measOccName" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.measOccName}" varStatus="loop">
          <sf:textarea rows="1" path="study.measOccName[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.measOccName.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.timeDim -->
  <c:set var="input_vars" value="study.timeDim;study.timeDim;required; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.intervention -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.surveyIntervention"><s:message code="study.intervention" /></label>
      <div class="panel panel-default panel-body margin-bottom-0 form-group-clean">
        <label class="btn btn-default col-sm-3"> <sf:checkbox path="study.surveyIntervention" /> <s:message
            code="study.intervention.survey" />
        </label> <label class="btn btn-default col-sm-3 col-sm-offset-1"> <sf:checkbox path="study.testIntervention" />
          <s:message code="study.intervention.test" />
        </label> <label class="btn btn-default col-sm-3 col-sm-offset-1"> <sf:checkbox
            path="study.experimentalIntervention" /> <s:message code="study.intervention.experimental" />
        </label>
      </div>
      <s:message code="study.intervention.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.interTypeExp -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.interTypeExp"><s:message code="study.interTypeExp" /></label>
      <sf:select path="study.interTypeExp" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="experimental">
          <s:message code="study.interTypeExp.experimental" />
        </sf:option>
        <sf:option value="quasiexperimental">
          <s:message code="study.interTypeExp.quasiexperimental" />
        </sf:option>
      </sf:select>
      <s:message code="study.interTypeExp.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.interTypeDes -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.interTypeDes"><s:message code="study.interTypeDes" /></label>
      <sf:select path="study.interTypeDes" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="repeatedmeasures">
          <s:message code="study.interTypeDes.repeatedmeasures" />
        </sf:option>
        <sf:option value="groupcomparison">
          <s:message code="study.interTypeDes.groupcomparison" />
        </sf:option>
        <sf:option value="mixeddesign">
          <s:message code="study.interTypeDes.mixeddesign" />
        </sf:option>
      </sf:select>
      <s:message code="study.interTypeDes.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.interTypeLab -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.interTypeLab"><s:message code="study.interTypeLab" /></label>
      <sf:select path="study.interTypeLab" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="laboratory">
          <s:message code="study.interTypeLab.laboratory" />
        </sf:option>
        <sf:option value="field">
          <s:message code="study.interTypeLab.field" />
        </sf:option>
      </sf:select>
      <s:message code="study.interTypeLab.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.randomization -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.randomization"><s:message code="study.randomization" /></label>
      <sf:select path="study.randomization" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="randomized">
          <s:message code="study.randomization.randomized" />
        </sf:option>
        <sf:option value="nonrandomized">
          <s:message code="study.randomization.nonrandomized" />
        </sf:option>
      </sf:select>
      <s:message code="study.randomization.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
  <!-- study.interArms -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.interArms"><s:message code="study.interArms" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.interArms}" varStatus="loop">
          <sf:textarea rows="1" path="study.interArms[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.interArms.help" var="appresmess" />
      <c:if test="${not empty appresmess}">
        <div class="row help-block">
          <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
          <div class="col-sm-11">
            <s:message text="${appresmess}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>
</div>