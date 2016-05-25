<%@ include file="../templates/includes.jsp"%>
<div id="sampleActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.eligibilities -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.eligibilities"><s:message code="study.eligibilities" /></label>
      <div class="panel panel-default panel-body margin-bottom-0">
        <c:forEach items="${StudyForm.study.eligibilities}" varStatus="loop">
          <sf:textarea rows="1" path="study.eligibilities[${loop.index}].text" class="form-control margin-bottom-10" />
        </c:forEach>
        <div class="input-group-btn">
          <button class="btn btn-sm btn-success" type="button">
            <s:message code="gen.add" />
          </button>
        </div>
      </div>
      <s:message code="study.eligibilities.help" var="appresmess" />
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
  <!-- study.population -->
  <c:set var="input_vars" value="study.population;study.population; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sampleSize -->
  <c:set var="input_vars" value="study.sampleSize;study.sampleSize; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.powerAnalysis -->
  <c:set var="input_vars" value="study.powerAnalysis;study.powerAnalysis; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.intSampleSize -->
  <c:set var="input_vars" value="study.intSampleSize;study.intSampleSize; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.obsUnit -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.obsUnit"><s:message code="study.obsUnit" /></label>
      <sf:select path="study.completeSel" class="form-control">
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
      <!-- study.obsUnitOther -->
      <c:set var="input_vars" value="study.obsUnitOther;study.obsUnitOther; ; ;row margin-bottom-0" />
      <%@ include file="../templates/gen_textarea.jsp"%>
      <s:message code="study.obsUnit.help" var="appresmess" />
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
  <!-- study.multilevel -->
  <c:set var="input_vars" value="study.multilevel;study.multilevel; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.sex-->
  <c:set var="input_vars" value="study.sex;study.sex; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.age-->
  <c:set var="input_vars" value="study.age;study.age; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <!-- study.specGroups-->
  <c:set var="input_vars" value="study.specGroups;study.specGroups; ; ;row" />
  <%@ include file="../templates/gen_input.jsp"%>
  <div class="form-group">
    <div class="col-sm-12">
      <span class="control-label"><strong><s:message code="study.geograph" /></strong></span>
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
  <%@ include file="../templates/gen_textarea.jsp"%>
  <!-- study.dataRerun-->
  <c:set var="input_vars" value="study.dataRerun;study.dataRerun; ; ;row" />
  <%@ include file="../templates/gen_textarea.jsp"%>
</div>