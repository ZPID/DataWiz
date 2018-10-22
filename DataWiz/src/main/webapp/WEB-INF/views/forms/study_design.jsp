<%@ include file="../templates/includes.jsp" %>
<div id="designActiveContent" class="studyContent">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="study.designdata.info"/>
      </div>
    </div>
  </div>
  <!-- study.objectives -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label"><s:message code="study.objectives"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.objectives}" varStatus="loop">
          <div class="row margin-bottom-10">
            <div class="col-xs-9 col-sm-10">
              <s:bind path="study.objectives[${loop.index}].text">
                <c:choose>
                  <c:when test="${status.error}">
                    <sf:textarea rows="1" path="study.objectives[${loop.index}].text" class="form-control margin-bottom-10 redborder"
                                 title="${status.errorMessage}" data-toggle="tooltip"/>
                  </c:when>
                  <c:otherwise>
                    <sf:textarea rows="1" path="study.objectives[${loop.index}].text" class="form-control margin-bottom-10"/>
                  </c:otherwise>
                </c:choose>
              </s:bind>
            </div>
            <div class="col-xs-3 col-sm-2">
              <sf:select class="form-control col-sm-2" path="study.objectives[${loop.index}].objectivetype">
                <sf:option value="">
                  <s:message code="gen.select"/>
                </sf:option>
                <sf:option value="PRIMARY">
                  <s:message code="study.objectives.primary"/>
                </sf:option>
                <sf:option value="SECONDARY">
                  <s:message code="study.objectives.secondary"/>
                </sf:option>
                <sf:option value="EXPLORATORY">
                  <s:message code="study.objectives.exploratory"/>
                </sf:option>
              </sf:select>
            </div>
          </div>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addObjectives" onclick="setScrollPosition();">
              <s:message code="gen.add"/>
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.objectives.help" var="appresmess"/>
      <%@ include file="../templates/helpblock.jsp" %>
    </div>
  </div>
  <!-- study.relTheorys -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label"><s:message code="study.relTheorys"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.relTheorys}" varStatus="loop">
          <s:bind path="study.relTheorys[${loop.index}].text">
            <c:choose>
              <c:when test="${status.error}">
                <sf:textarea rows="1" path="study.relTheorys[${loop.index}].text" class="form-control margin-bottom-10 redborder"
                             title="${status.errorMessage}" data-toggle="tooltip"/>
              </c:when>
              <c:otherwise>
                <sf:textarea rows="1" path="study.relTheorys[${loop.index}].text" class="form-control margin-bottom-10"/>
              </c:otherwise>
            </c:choose>
          </s:bind>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addRelTheorys" onclick="setScrollPosition();">
              <s:message code="gen.add"/>
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.relTheorys.help" var="appresmess"/>
      <%@ include file="../templates/helpblock.jsp" %>
    </div>
  </div>
  <!-- study.repMeasures -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label " for="study.repMeasures"><s:message code="study.repMeasures"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <sf:select path="study.repMeasures" class="form-control" id="selectrepMeasures"
                 onchange="switchViewIfSelected('selectrepMeasures', 'MULTIPLE');">
        <sf:option value="">
          <s:message code="gen.select"/>
        </sf:option>
        <sf:option value="SINGLE">
          <s:message code="study.repMeasures.single"/>
        </sf:option>
        <sf:option value="MULTIPLE">
          <s:message code="study.repMeasures.multiple"/>
        </sf:option>
      </sf:select>
      <s:message code="study.repMeasures.help" var="appresmess"/>
      <%@ include file="../templates/helpblock.jsp" %>
    </div>
  </div>
  <!-- study.timeDim -->
  <div id="contentrepMeasures">
    <c:set var="input_vars" value="study.timeDim;study.timeDim; ; ;row"/>
    <c:set var="valimages" value="${valimag1}"/>
    <%@ include file="../templates/gen_textarea.jsp" %>
  </div>
  <!-- study.intervention -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label"><s:message code="study.intervention"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0 form-group-clean">
        <label class="btn btn-default col-sm-3"> <sf:checkbox path="study.surveyIntervention"
                                                              onchange="switchViewIfChecked('selectSurveyIntervention')" id="selectSurveyIntervention"/>
          <s:message code="study.intervention.survey"/>
        </label> <label class="btn btn-default col-sm-3 col-sm-offset-1"> <sf:checkbox path="study.testIntervention"/> <s:message
          code="study.intervention.test"/>
      </label> <label class="btn btn-default col-sm-3 col-sm-offset-1"> <sf:checkbox path="study.experimentalIntervention"
                                                                                     onchange="switchViewIfChecked('selectExperimentalIntervention')"
                                                                                     id="selectExperimentalIntervention"/> <s:message
          code="study.intervention.experimental"/>
      </label>
      </div>
      <s:message code="study.intervention.help" var="appresmess"/>
      <%@ include file="../templates/helpblock.jsp" %>
    </div>
  </div>
  <div class="contentExperimentalIntervention">
    <!-- study.interTypeExp -->
    <div class="form-group">
      <div class="col-sm-12">
        <div class="row">
          <div class="col-xs-10 col-sm-11">
            <label class="control-label " for="study.interTypeExp"><s:message code="study.interTypeExp"/></label>
          </div>
          <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
            <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
          </div>
        </div>
        <sf:select path="study.interTypeExp" class="form-control">
          <sf:option value="">
            <s:message code="gen.select"/>
          </sf:option>
          <sf:option value="EXPERIMENTAL">
            <s:message code="study.interTypeExp.experimental"/>
          </sf:option>
          <sf:option value="QUASIEXPERIMENTAL">
            <s:message code="study.interTypeExp.quasiexperimental"/>
          </sf:option>
        </sf:select>
        <s:message code="study.interTypeExp.help" var="appresmess"/>
        <%@ include file="../templates/helpblock.jsp" %>
      </div>
    </div>
    <!-- study.interTypeDes -->
    <div class="form-group">
      <div class="col-sm-12">
        <div class="row">
          <div class="col-xs-10 col-sm-11">
            <label class="control-label " for="study.interTypeDes"><s:message code="study.interTypeDes"/></label>
          </div>
          <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
            <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
          </div>
        </div>
        <sf:select path="study.interTypeDes" class="form-control">
          <sf:option value="">
            <s:message code="gen.select"/>
          </sf:option>
          <sf:option value="REPEATEDMEASURES">
            <s:message code="study.interTypeDes.repeatedmeasures"/>
          </sf:option>
          <sf:option value="GROUPCOMPARISON">
            <s:message code="study.interTypeDes.groupcomparison"/>
          </sf:option>
          <sf:option value="MIXEDDESIGN">
            <s:message code="study.interTypeDes.mixeddesign"/>
          </sf:option>
        </sf:select>
        <s:message code="study.interTypeDes.help" var="appresmess"/>
        <%@ include file="../templates/helpblock.jsp" %>
      </div>
    </div>
    <!-- study.interTypeLab -->
    <div class="form-group">
      <div class="col-sm-12">
        <div class="row">
          <div class="col-xs-10 col-sm-11">
            <label class="control-label " for="study.interTypeLab"><s:message code="study.interTypeLab"/></label>
          </div>
          <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
            <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
          </div>
        </div>
        <sf:select path="study.interTypeLab" class="form-control">
          <sf:option value="">
            <s:message code="gen.select"/>
          </sf:option>
          <sf:option value="LABORATORY">
            <s:message code="study.interTypeLab.laboratory"/>
          </sf:option>
          <sf:option value="FIELD">
            <s:message code="study.interTypeLab.field"/>
          </sf:option>
        </sf:select>
        <s:message code="study.interTypeLab.help" var="appresmess"/>
        <%@ include file="../templates/helpblock.jsp" %>
      </div>
    </div>
    <!-- study.randomization -->
    <div class="form-group">
      <div class="col-sm-12">
        <div class="row">
          <div class="col-xs-10 col-sm-11">
            <label class="control-label " for="study.randomization"><s:message code="study.randomization"/></label>
          </div>
          <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
            <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
          </div>
        </div>
        <sf:select path="study.randomization" class="form-control">
          <sf:option value="">
            <s:message code="gen.select"/>
          </sf:option>
          <sf:option value="RANDOMIZED">
            <s:message code="study.randomization.randomized"/>
          </sf:option>
          <sf:option value="NONRANDOMIZED">
            <s:message code="study.randomization.nonrandomized"/>
          </sf:option>
        </sf:select>
        <s:message code="study.randomization.help" var="appresmess"/>
        <%@ include file="../templates/helpblock.jsp" %>
      </div>
    </div>
    <!-- study.interArms -->
    <div class="form-group">
      <div class="col-sm-12">
        <div class="row">
          <div class="col-xs-10 col-sm-11">
            <label class="control-label"><s:message code="study.interArms"/></label>
          </div>
          <div class="col-xs-2 col-sm-1 text-align-right">
            <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
          </div>
        </div>
        <div class="panel panel-default panel-body margin-bottom-0">
          <c:forEach items="${StudyForm.study.interArms}" varStatus="loop">
            <s:bind path="study.interArms[${loop.index}].text">
              <c:choose>
                <c:when test="${status.error}">
                  <sf:textarea rows="1" path="study.interArms[${loop.index}].text" class="form-control margin-bottom-10 redborder"
                               title="${status.errorMessage}" data-toggle="tooltip"/>
                </c:when>
                <c:otherwise>
                  <sf:textarea rows="1" path="study.interArms[${loop.index}].text" class="form-control margin-bottom-10"/>
                </c:otherwise>
              </c:choose>
            </s:bind>
          </c:forEach>
          <div class="input-group-btn">
            <sf:button class="btn btn-sm btn-success" name="addInterArms" onclick="setScrollPosition();">
              <s:message code="gen.add"/>
            </sf:button>
          </div>
        </div>
        <s:message code="study.interArms.help" var="appresmess"/>
        <%@ include file="../templates/helpblock.jsp" %>
      </div>
    </div>
  </div>
  <!-- study.measOccName -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label "><s:message code="study.measOcc"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:if test="${not empty StudyForm.study.measOcc}">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
              <tr>
                <th class="col-sm-10"><s:message code="study.measOcc.time"/></th>
                <th class="col-sm-1 contentExperimentalIntervention"><s:message code="study.measOcc.dim"/></th>
                <th class="col-sm-1"><s:message code="study.measOcc.sort"/></th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${StudyForm.study.measOcc}" varStatus="loop">
                <tr>
                  <td><s:bind path="study.measOcc[${loop.index}].text">
                    <c:choose>
                      <c:when test="${status.error}">
                        <sf:textarea rows="1" path="study.measOcc[${loop.index}].text" class="form-control margin-bottom-10 redborder"
                                     title="${status.errorMessage}" data-toggle="tooltip"/>
                      </c:when>
                      <c:otherwise>
                        <sf:textarea rows="1" path="study.measOcc[${loop.index}].text" class="form-control margin-bottom-10"/>
                      </c:otherwise>
                    </c:choose>
                  </s:bind></td>
                  <td style="text-align: center;" class="contentExperimentalIntervention">
                    <div class="checkbox form-group-clean">
                      <label><sf:checkbox path="study.measOcc[${loop.index}].timetable"/></label>
                    </div>
                  </td>
                  <td><sf:input path="study.measOcc[${loop.index}].sort" class="form-control"/></td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addMeasOccName" onclick="setScrollPosition();">
              <s:message code="gen.add"/>
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.measOcc.help" var="appresmess"/>
      <%@ include file="../templates/helpblock.jsp" %>
    </div>
  </div>
  <!-- study.surveyType -->
  <div class="contentSurveyIntervention">
    <div class="form-group">
      <div class="col-sm-12">
        <div class="row">
          <div class="col-xs-10 col-sm-11">
            <label class="control-label " for="study.surveyType"><s:message code="study.surveyType"/></label>
          </div>
          <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
            <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
          </div>
        </div>
        <sf:select path="study.surveyType" class="form-control">
          <sf:option value="">
            <s:message code="gen.select"/>
          </sf:option>
          <sf:option value="HARDLYINSTRUMENT">
            <s:message code="study.surveyType.hardlyinstrument"/>
          </sf:option>
          <sf:option value="PARTIALLYINSTRUMENT">
            <s:message code="study.surveyType.partiallyinstrument"/>
          </sf:option>
          <sf:option value="FULLYINSTRUMENT">
            <s:message code="study.surveyType.fullyinstrument"/>
          </sf:option>
          <sf:option value="MIXEDINSTRUMENT">
            <s:message code="study.surveyType.mixedinstrument"/>
          </sf:option>
        </sf:select>
        <s:message code="study.surveyType.help" var="appresmess"/>
        <%@ include file="../templates/helpblock.jsp" %>
      </div>
    </div>
  </div>
  <!-- study.constructs -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label "><s:message code="study.constructs"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <input type="hidden" value="${StudyForm.study.constructs.size()}" id="constructSize"/>
        <c:forEach items="${StudyForm.study.constructs}" varStatus="loop">
          <div class="panel panel-default panel-body margin-bottom-10">
            <!-- study.constructs.name -->
            <div class="form-group margin-bottom-0">
              <div class="col-sm-6 margin-bottom-0">
                <label class="control-label " for="study.constructs[${loop.index}].name"><s:message code="study.constructs.name"/>&nbsp;<s:message
                    text="${loop.index + 1}"/></label>
                <s:bind path="study.constructs[${loop.index}].name">
                  <c:choose>
                    <c:when test="${status.error}">
                      <sf:textarea rows="1" path="study.constructs[${loop.index}].name" class="form-control redborder" title="${status.errorMessage}"
                                   data-toggle="tooltip"/>
                    </c:when>
                    <c:otherwise>
                      <sf:textarea rows="1" path="study.constructs[${loop.index}].name" class="form-control "/>
                    </c:otherwise>
                  </c:choose>
                </s:bind>
                <s:message code="study.constructs.name.help" var="appresmess"/>
                <%@ include file="../templates/helpblock.jsp" %>
              </div>
              <!-- study.constructs.type -->
              <div class="col-sm-6 margin-bottom-0">
                <label class="control-label " for="study.constructs[${loop.index}].type"><s:message code="study.constructs.type"/></label>
                <s:bind path="study.constructs[${loop.index}].type">
                  <c:choose>
                    <c:when test="${status.error}">
                      <sf:select path="study.constructs[${loop.index}].type" class="form-control redborder" id="selectConstructType${loop.index}"
                                 onchange="switchViewIfSelected('selectConstructType${loop.index}', 'OTHER');">
                        <sf:option value="">
                          <s:message code="gen.select"/>
                        </sf:option>
                        <sf:option value="INDEPENDENT">
                          <s:message code="study.constructs.type.independent"/>
                        </sf:option>
                        <sf:option value="DEPENDENT">
                          <s:message code="study.constructs.type.dependent"/>
                        </sf:option>
                        <sf:option value="CONTROL">
                          <s:message code="study.constructs.type.control"/>
                        </sf:option>
                        <sf:option value="OTHER">
                          <s:message code="study.constructs.type.other"/>
                        </sf:option>
                      </sf:select>
                      <s:message code="study.constructs.type.help" var="appresmess"/>
                    </c:when>
                    <c:otherwise>
                      <sf:select path="study.constructs[${loop.index}].type" class="form-control" id="selectConstructType${loop.index}"
                                 onchange="switchViewIfSelected('selectConstructType${loop.index}', 'OTHER');">
                        <sf:option value="">
                          <s:message code="gen.select"/>
                        </sf:option>
                        <sf:option value="INDEPENDENT">
                          <s:message code="study.constructs.type.independent"/>
                        </sf:option>
                        <sf:option value="DEPENDENT">
                          <s:message code="study.constructs.type.dependent"/>
                        </sf:option>
                        <sf:option value="CONTROL">
                          <s:message code="study.constructs.type.control"/>
                        </sf:option>
                        <sf:option value="OTHER">
                          <s:message code="study.constructs.type.other"/>
                        </sf:option>
                      </sf:select>
                      <s:message code="study.constructs.type.help" var="appresmess"/>
                    </c:otherwise>
                  </c:choose>
                </s:bind>
                <%@ include file="../templates/helpblock.jsp" %>
              </div>
            </div>
            <!-- study.constructs.other -->
            <div class="form-group margin-bottom-0" id="contentConstructType${loop.index}">
              <div class="col-sm-6 col-sm-offset-6">
                <label class="control-label " for="study.constructs[${loop.index}].other"><s:message code="study.constructs.other"/></label>
                <s:bind path="study.constructs[${loop.index}].other">
                  <c:choose>
                    <c:when test="${status.error}">
                      <sf:textarea rows="1" path="study.constructs[${loop.index}].other" class="form-control redborder" title="${status.errorMessage}"
                                   data-toggle="tooltip"/>
                    </c:when>
                    <c:otherwise>
                      <sf:textarea rows="1" path="study.constructs[${loop.index}].other" class="form-control "/>
                    </c:otherwise>
                  </c:choose>
                </s:bind>
                <s:message code="study.constructs.other.help" var="appresmess"/>
                <%@ include file="../templates/helpblock.jsp" %>
              </div>
            </div>
          </div>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addConstruct" onclick="setScrollPosition();">
              <s:message code="gen.add"/>
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.constructs.help" var="appresmess"/>
      <%@ include file="../templates/helpblock.jsp" %>
    </div>
  </div>
  <!-- study.instrument -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label class="control-label"><s:message code="study.instruments"/></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages"/> <img src="/DataWiz/static/images/${valimag2}" class="infoImages"/>
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <div class="panel-group" id="accordion">
          <c:forEach items="${StudyForm.study.instruments}" var="form" varStatus="loop">
            <s:message var="redborderErr" text=""/>
            <s:bind path="study.instruments[${loop.index}].*">
              <c:if test="${status.error}">
                <s:message var="redborderErr" text="redborder"/>
              </c:if>
            </s:bind>
            <div class="form-group">
              <div class="col-sm-12">
                <label class="control-label"><s:message code="study.instrument"/>&nbsp;<s:message
                    text="${loop.index + 1}"/></label>
                <div class="panel panel-default ${redborderErr}">
                  <div class="panel-heading" data-target="#panel_coll_${loop.index}" data-toggle="collapse" data-parent="#accordion">
                    <div class="row">
                      <div class="col-sm-11">
                        <!-- study.instrument.title -->
                        <c:set var="input_vars" value="study.instruments[${loop.index}].title;study.instruments.title; ; ;row margin-bottom-0"/>
                        <%@ include file="../templates/gen_input.jsp" %>
                      </div>
                      <div class="col-sm-1">
                        <i class="indicator glyphicon glyphicon glyphicon-plus"></i>
                      </div>
                    </div>
                  </div>
                  <div class="panel-collapse collapse" id="panel_coll_${loop.index}">
                    <div class="panel-body">
                      <!-- study.instrument.author -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].author;study.instruments.author; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.citation -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].citation;study.instruments.citation; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.summary -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].summary;study.instruments.summary; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.theoHint -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].theoHint;study.instruments.theoHint; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.structure -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].structure;study.instruments.structure; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.construction -->
                      <c:set var="input_vars"
                             value="study.instruments[${loop.index}].construction;study.instruments.construction; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.objectivity -->
                      <c:set var="input_vars"
                             value="study.instruments[${loop.index}].objectivity;study.instruments.objectivity; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.reliability -->
                      <c:set var="input_vars"
                             value="study.instruments[${loop.index}].reliability;study.instruments.reliability; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.validity -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].validity;study.instruments.validity; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                      <!-- study.instruments.norm -->
                      <c:set var="input_vars" value="study.instruments[${loop.index}].norm;study.instruments.norm; ; ;row margin-bottom-0"/>
                      <%@ include file="../templates/gen_textarea.jsp" %>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </c:forEach>
        </div>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addInstrument" onclick="setScrollPosition();">
              <s:message code="gen.add"/>
            </sf:button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- study.description -->
  <c:set var="input_vars" value="study.description;study.description; ; ;row"/>
  <%@ include file="../templates/gen_textarea.jsp" %>
</div>