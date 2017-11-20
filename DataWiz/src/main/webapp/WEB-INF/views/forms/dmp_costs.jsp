<%@ include file="../templates/includes.jsp"%>
<div id="costsActiveContent" class="projectContent">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.costs.info" />
      </div>
    </div>
  </div>
  <!-- specificCosts -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="row">
        <div class="col-xs-10 col-sm-11">
          <label for="dmp.specificCosts"><s:message code="dmp.edit.specificCosts" /></label>
        </div>
        <div class="col-xs-2 col-sm-1 text-nowrap text-align-right">
          <img src="/DataWiz/static/images/${valimag1}" class="infoImages" /> <img src="/DataWiz/static/images/${valimag2}" class="infoImages" />
        </div>
      </div>
      <sf:select class="form-control" path="dmp.specificCosts" id="selectspecificCosts"
        onchange="switchViewIfSelectedMulti('selectspecificCosts', 'reference,lifecycle,other');">
        <sf:option value="no">
          <s:message code="dmp.edit.specificCosts.no" />
        </sf:option>
        <sf:option value="reference">
          <s:message code="dmp.edit.specificCosts.reference" />
        </sf:option>
        <sf:option value="lifecycle">
          <s:message code="dmp.edit.specificCosts.lifecycle" />
        </sf:option>
        <sf:option value="other">
          <s:message code="dmp.edit.specificCosts.other" />
        </sf:option>
      </sf:select>
      <s:message code="dmp.edit.specificCosts.help" var="appresmess" />
      <%@ include file="../templates/helpblock.jsp"%>
    </div>
  </div>
  <div id="contentspecificCosts">
    <!-- specificCostsTxt -->
    <c:set var="input_vars" value="dmp.specificCostsTxt;dmp.edit.specificCostsTxt; ; ;row" />
    <c:set var="valimages" value="${valimag1};${valimag2};${valimag3}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
    <!-- bearCost -->
    <c:set var="input_vars" value="dmp.bearCost;dmp.edit.bearCost; ; ;row" />
    <c:set var="valimages" value="${valimag1};${valimag2}" />
    <%@ include file="../templates/gen_textarea.jsp"%>
  </div>
</div>