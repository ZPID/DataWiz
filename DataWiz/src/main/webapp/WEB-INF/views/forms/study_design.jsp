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
                <sf:option value="PRIMARY">
                  <s:message code="study.objectives.primary" />
                </sf:option>
                <sf:option value="SECONDARY">
                  <s:message code="study.objectives.secondary" />
                </sf:option>
                <sf:option value="EXPLORATORY">
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
        <sf:option value="SINGLE">
          <s:message code="study.repMeasures.single" />
        </sf:option>
        <sf:option value="MULTIPLE">
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
        <sf:option value="EXPERIMENTAL">
          <s:message code="study.interTypeExp.experimental" />
        </sf:option>
        <sf:option value="QUASIEXPERIMENTAL">
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
        <sf:option value="REPEATEDMEASURES">
          <s:message code="study.interTypeDes.repeatedmeasures" />
        </sf:option>
        <sf:option value="GROUPCOMPARISON">
          <s:message code="study.interTypeDes.groupcomparison" />
        </sf:option>
        <sf:option value="MIXEDDESIGN">
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
        <sf:option value="LABORATORY">
          <s:message code="study.interTypeLab.laboratory" />
        </sf:option>
        <sf:option value="FIELD">
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
        <sf:option value="RANDOMIZED">
          <s:message code="study.randomization.randomized" />
        </sf:option>
        <sf:option value="NONRANDOMIZED">
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
  <!-- study.measOccName -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.measOccName"><s:message code="study.measOccName" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th class="col-sm-10">Zeitpunkt</th>
                <th class="col-sm-1">Zeitdimension</th>
                <th class="col-sm-1">Sortierung</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${StudyForm.study.measOccName}" varStatus="loop">
                <tr>
                  <td><sf:textarea rows="1" path="study.measOccName[${loop.index}].text" class="form-control" /></td>
                  <td style="text-align: center;">
                    <div class="checkbox form-group-clean">
                      <label><sf:checkbox path="study.measOccName[${loop.index}].timetable" /></label>
                    </div>
                  </td>
                  <td><sf:input path="study.measOccName[${loop.index}].sort" class="form-control" /></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
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
  <!-- study.surveyType -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.surveyType"><s:message code="study.surveyType" /></label>
      <sf:select path="study.surveyType" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="HARDLYINSTRUMENT">
          <s:message code="study.surveyType.hardlyinstrument" />
        </sf:option>
        <sf:option value="PARTIALLYINSTRUMENT">
          <s:message code="study.surveyType.partiallyinstrument" />
        </sf:option>
        <sf:option value="FULLYINSTRUMENT">
          <s:message code="study.surveyType.fullyinstrument" />
        </sf:option>
      </sf:select>
      <s:message code="study.surveyType.help" var="appresmess" />
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
  <!-- study.constructs -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.constructs"><s:message code="study.constructs" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.constructs}" varStatus="loop">
          <div class="panel panel-default panel-body margin-bottom-10">
            <!-- study.constructs.name -->
            <div class="form-group margin-bottom-0">
              <div class="col-sm-6 margin-bottom-0">
                <label class="control-label " for="study.constructs[${loop.index}].name"><s:message
                    code="study.constructs.name" />&nbsp;<s:message text="${loop.index + 1}" /></label>
                <sf:textarea rows="1" path="study.constructs[${loop.index}].name" class="form-control" />
                <s:message code="study.constructs.name.help" var="appresmess" />
                <c:if test="${not empty appresmess}">
                  <div class="row help-block margin-bottom-0">
                    <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                    <div class="col-sm-11">
                      <s:message text="${appresmess}" />
                    </div>
                  </div>
                </c:if>
              </div>
              <!-- study.constructs.type -->
              <div class="col-sm-6 margin-bottom-0">
                <label class="control-label " for="study.constructs[${loop.index}].type"><s:message
                    code="study.constructs.type" /></label>
                <sf:select path="study.constructs[${loop.index}].type" class="form-control">
                  <sf:option value="">
                    <s:message code="gen.select" />
                  </sf:option>
                  <sf:option value="INDEPENDENT">
                    <s:message code="study.constructs.type.independent" />
                  </sf:option>
                  <sf:option value="DEPENDENT">
                    <s:message code="study.constructs.type.dependent" />
                  </sf:option>
                  <sf:option value="CONTROL">
                    <s:message code="study.constructs.type.control" />
                  </sf:option>
                  <sf:option value="OTHER">
                    <s:message code="study.constructs.type.other" />
                  </sf:option>
                </sf:select>
                <s:message code="study.constructs.type.help" var="appresmess" />
                <c:if test="${not empty appresmess}">
                  <div class="row help-block margin-bottom-0">
                    <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                    <div class="col-sm-11">
                      <s:message text="${appresmess}" />
                    </div>
                  </div>
                </c:if>
              </div>
            </div>
            <!-- study.constructs.other -->
            <div class="form-group margin-bottom-0 margin-bottom-0">
              <div class="col-sm-6 col-sm-offset-6 margin-bottom-0">
                <label class="control-label " for="study.constructs[${loop.index}].other"><s:message
                    code="study.constructs.other" /></label>
                <sf:textarea rows="1" path="study.constructs[${loop.index}].other" class="form-control" />
                <s:message code="study.constructs.other.help" var="appresmess" />
                <c:if test="${not empty appresmess}">
                  <div class="row help-block margin-bottom-0">
                    <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                    <div class="col-sm-11">
                      <s:message text="${appresmess}" />
                    </div>
                  </div>
                </c:if>
              </div>
            </div>
          </div>
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.constructs.help" var="appresmess" />
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
  <!-- study.instrument -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.instruments"><s:message code="study.instruments" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <div class="panel-group" id="accordion">
          <c:forEach items="${StudyForm.study.instruments}" var="form" varStatus="loop">
            <div class="form-group">
              <div class="col-sm-12">
                <label class="control-label " for="study.instruments"><s:message code="study.instrument" />&nbsp;<s:message
                    text="${loop.index + 1}" /></label>
                <div class="panel panel-default">
                  <div class="panel-heading" data-target="#panel_coll_${loop.index}" data-toggle="collapse"
                    data-parent="#accordion">
                    <div class="row">
                      <div class="col-sm-11">
                        <!-- study.instrument.title -->
                        <c:set var="input_vars"
                          value="study.instruments[${loop.index}].title;study.instruments.title; ; ;row margin-bottom-0" />
                        <%@ include file="../templates/gen_input.jsp"%>
                      </div>
                      <div class="col-sm-1">
                        <i class="indicator glyphicon glyphicon glyphicon-plus"></i>
                      </div>
                    </div>
                  </div>
                  <div class="panel-collapse collapse" id="panel_coll_${loop.index}">
                    <div class="panel-body">
                      <!-- study.instrument.author -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].author;study.instruments.author; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.citation -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].citation;study.instruments.citation; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.summary -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].summary;study.instruments.summary; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.theoHint -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].theoHint;study.instruments.theoHint; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.structure -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].structure;study.instruments.structure; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.construction -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].construction;study.instruments.construction; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.objectivity -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].objectivity;study.instruments.objectivity; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.reliability -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].reliability;study.instruments.reliability; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.validity -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].validity;study.instruments.validity; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                      <!-- study.instruments.norm -->
                      <c:set var="input_vars"
                        value="study.instruments[${loop.index}].norm;study.instruments.norm; ; ;row margin-bottom-0" />
                      <%@ include file="../templates/gen_textarea.jsp"%>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </c:forEach>
        </div>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
    </div>
  </div>
  <!-- study.description -->
  <c:set var="input_vars" value="study.description;study.description; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
</div>