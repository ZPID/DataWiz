<%@ include file="../templates/includes.jsp"%>
<div id="surveyActiveContent" class="projectContent contentMargin">
  <!-- Infotext -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.admindata.info" />
      </div>
    </div>
  </div>
  <!-- study.responsibility -->
  <div class="form-group">
    <div class="col-sm-12">
      <label class="control-label " for="study.responsibility"><s:message code="study.responsibility" /></label>
      <sf:select path="study.completeSel" class="form-control">
        <sf:option value="">
          <s:message code="gen.select" />
        </sf:option>
        <sf:option value="PRIMARY">
          <s:message code="study.responsibility.primary" />
        </sf:option>
        <sf:option value="OTHER">
          <s:message code="study.responsibility.other" />
        </sf:option>
      </sf:select>
      <!-- study.obsUnitOther -->
      <c:set var="input_vars" value="study.responsibilityOther;study.responsibilityOther; ; ;row margin-bottom-0" />
      <%@ include file="../templates/gen_textarea.jsp"%>
      <s:message code="study.responsibility.help" var="appresmess" />
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
  <!-- TODO -->
  <div class="input-daterange input-group" id="datepicker">
    <sf:input class="input-sm form-control" path="study.collStart" /> <span class="input-group-addon">to</span> <input
      type="text" class="input-sm form-control" name="end" />
  </div>
  <!-- TODO -->
</div>