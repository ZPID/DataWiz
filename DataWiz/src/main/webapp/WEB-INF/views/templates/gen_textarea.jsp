<%-- 
 String input_vars are seperated with ';' and is now splitted into an array
 input_vars[0] = var name in dmp or form file
 input_vars[1] = var name in i18n file
 input_vars[2] = css classes for the label
 input_vars[3] = css classes for the input container
 input_vars[4] = dss classes for the help block
 input_vars[5] = dss classes for the input field
 input_vars[6] = optional type arguments - password for example
 input_vars[7] = css classes for form-group div
 --%>
<c:set var="input_vars" value="${fn:split(input_vars, ';')}" />
<s:message text="" var="input_class" />
<div class="form-group ${input_vars[7]}">
  <div class="col-sm-12">
    <s:message code="${input_vars[1]}" var="labeltxt" />
    <div class="row">
      <div class="col-sm-11">
        <c:if test="${not empty labeltxt}">
          <label class="control-label ${input_vars[2]}" for="${input_vars[0]}"><s:message text="${labeltxt}" /></label>
        </c:if>
      </div>
      <div class="col-sm-1 text-align-right">
        <c:set var="valimages" value="${fn:split(valimages, ';')}" />
        <c:forEach items="${valimages}" var="imglabel">
          <c:if test="${imglabel ne '' && imglabel ne ' '}">
            <img src="/DataWiz/static/images/${imglabel}" class="infoImages" />
          </c:if>
        </c:forEach>
      </div>
    </div>
    <div class="${input_vars[3]}">
      <s:bind path="${input_vars[0]}">
        <c:choose>
          <c:when test="${status.error}">
            <sf:textarea rows="5" path="${input_vars[0]}" class="form-control ${input_vars[5]}"
              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip"
              type="${input_vars[6]}" />
          </c:when>
          <c:otherwise>
            <sf:textarea rows="5" path="${input_vars[0]}" class="form-control ${input_vars[5]}" type="${input_vars[6]}" />
          </c:otherwise>
        </c:choose>
      </s:bind>
      <s:message code="${input_vars[1]}.help" var="appresmess" />
    </div>
    <c:if test="${not empty appresmess}">
      <div class="help-block ${input_vars[4]}">
        <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
        <div class="col-sm-11">
          <s:message text="${appresmess}" />
        </div>
      </div>
    </c:if>
  </div>
</div>
<c:set var="valimages" value="" />
<c:set var="input_vars" value="" />