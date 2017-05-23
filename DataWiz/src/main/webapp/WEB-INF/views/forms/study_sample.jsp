<%@ include file="../templates/includes.jsp"%>
<div id="sampleActiveContent" class="studyContent">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="study.sampledata.info" />
      </div>
    </div>
  </div>
  <!-- study.eligibilities -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label " for="study.eligibilities"><s:message code="study.eligibilities" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.eligibilities}" varStatus="loop">
          <s:bind path="study.eligibilities[${loop.index}].text">
            <c:choose>
              <c:when test="${status.error}">
                <sf:textarea rows="1" path="study.eligibilities[${loop.index}].text"
                  class="form-control margin-bottom-10 redborder" title="${status.errorMessage}" data-toggle="tooltip" />
              </c:when>
              <c:otherwise>
                <sf:textarea rows="1" path="study.eligibilities[${loop.index}].text"
                  class="form-control margin-bottom-10" />
              </c:otherwise>
            </c:choose>
          </s:bind>
        </c:forEach>
        <div class="row text-align-right">
          <div class="col-sm-12">
            <sf:button class="btn btn-sm btn-success" name="addEligibilities" onclick="setScrollPosition();">
              <s:message code="gen.add" />
            </sf:button>
          </div>
        </div>
      </div>
      <s:message code="study.eligibilities.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.population -->
  <c:set var="input_vars" value="study.population;study.population; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sampleSize -->
  <c:set var="input_vars" value="study.sampleSize;study.sampleSize; ; ;row" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.powerAnalysis -->
  <c:set var="input_vars" value="study.powerAnalysis;study.powerAnalysis; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.intSampleSize -->
  <c:set var="input_vars" value="study.intSampleSize;study.intSampleSize; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.obsUnit -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <label class="control-label " for="study.obsUnit"><s:message code="study.obsUnit" /></label>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img
            src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select path="study.obsUnit" class="form-control" id="selectObsUnit"
        onchange="switchViewIfSelected('selectObsUnit', 'OTHER');">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="INDIVIDUALS">
          <s:message code="study.obsUnit.individuals" />
        </sf:option>
        <sf:option value="DYADS">
          <s:message code="study.obsUnit.dyads" />
        </sf:option>
        <sf:option value="FAMILIES">
          <s:message code="study.obsUnit.families" />
        </sf:option>
        <sf:option value="GROUPS">
          <s:message code="study.obsUnit.groups" />
        </sf:option>
        <sf:option value="ORGANIZATIONS">
          <s:message code="study.obsUnit.organizations" />
        </sf:option>
        <sf:option value="OTHER">
          <s:message code="study.obsUnit.other" />
        </sf:option>
      </sf:select>
      <div id="contentObsUnit">
        <!-- study.obsUnitOther -->
        <c:set var="input_vars" value="study.obsUnitOther;study.obsUnitOther; ; ;row margin-bottom-0" />
        <%@ include file="../templates/gen_textarea.jsp"%>
      </div>
      <s:message code="study.obsUnit.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <!-- study.multilevel -->
  <c:set var="input_vars" value="study.multilevel;study.multilevel; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sex-->
  <c:set var="input_vars" value="study.sex;study.sex; ; ;row" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.age-->
  <c:set var="input_vars" value="study.age;study.age; ; ;row" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.specGroups-->
  <c:set var="input_vars" value="study.specGroups;study.specGroups; ; ;row" />
  <c:set var="valimages" value="${valimag1}" />
  <%@ include file="../templates/gen_input.jsp"%>
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-sm-11">
          <span class="control-label"><strong><s:message code="study.geograph" /></strong></span>
        </div>
        <div class="col-sm-1 text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
        </div>
      </div>
      <div class="panel panel-default panel-body margin-bottom-0">
        <!-- study.country-->
        <c:set var="input_vars" value="study.country;study.country; ; ;row" />
        <%@ include file="../templates/gen_input.jsp"%>
        <!-- study.city-->
        <c:set var="input_vars" value="study.city;study.city; ; ;row" />
        <%@ include file="../templates/gen_input.jsp"%>
        <!-- study.region-->
        <c:set var="input_vars" value="study.region;study.region; ; ;row" />
        <%@ include file="../templates/gen_input.jsp"%>
      </div>
    </div>
  </div>
  <!-- study.missings-->
  <c:set var="input_vars" value="study.missings;study.missings; ; ;row" />
  <c:set var="valimages" value="${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.dataRerun-->
  <c:set var="input_vars" value="study.dataRerun;study.dataRerun; ; ;row" />
  <c:set var="valimages" value="${valimag1};${valimag2}" />
  <%@ include file="../templates/gen_textarea.jsp"%>
</div>