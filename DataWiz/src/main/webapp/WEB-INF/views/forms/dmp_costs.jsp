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
  <ul class="list-group">
    <!-- specificCosts -->
    <li class="list-group-item">
      <div class="form-group">
        <div class="col-sm-12">
          <label for="dmp.specificCosts"><s:message code="dmp.edit.specificCosts" /></label>
          <sf:select class="form-control" path="dmp.specificCosts" id="selectspecificCosts"
            onchange="switchViewIfSelectedMulti('selectspecificCosts', 'reference,lifecycle,other');">
            <sf:option value="no">
              <s:message code="dmp.edit.specificCosts.option0" />
            </sf:option>
            <sf:option value="reference">
              <s:message code="dmp.edit.specificCosts.option1" />
            </sf:option>
            <sf:option value="lifecycle">
              <s:message code="dmp.edit.specificCosts.option2" />
            </sf:option>
            <sf:option value="other">
              <s:message code="dmp.edit.specificCosts.option3" />
            </sf:option>
          </sf:select>
          <s:message code="dmp.edit.specificCosts.help" var="appresmess" />
          <%@ include file="../templates/helpblock.jsp"%>
        </div>
      </div>
      <div id="contentspecificCosts">
        <!-- specificCostsTxt -->
        <s:message text="specificCostsTxt" var="dmp_var_name" />
        <%@ include file="../templates/textarea.jsp"%>
        <!-- ariseCosts -->
        <s:message text="ariseCosts" var="dmp_var_name" />
        <%@ include file="../templates/textarea.jsp"%>
        <!-- bearCost -->
        <s:message text="bearCost" var="dmp_var_name" />
        <%@ include file="../templates/textarea.jsp"%>
      </div>
  </ul>
</div>