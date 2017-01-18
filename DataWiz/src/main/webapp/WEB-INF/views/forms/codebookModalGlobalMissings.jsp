<%@ include file="../templates/includes.jsp"%>
<c:url var="accessUrl"
  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.previousRecordVersion.id}" />
<sf:form action="${accessUrl}" commandName="VarValues" class="form-horizontal" role="form"
  onsubmit="return checkMissingForm()">
  <div class="modal-content panel-primary">
    <div class="modal-header panel-heading">
      <button type="button" class="close" data-dismiss="modal">&times;</button>
      <h4 class="modal-title">Missings</h4>
    </div>
    <div class="modal-body">
      <ul class="list-group">
        <c:forEach var="i" begin="0" end="2">
          <li class="list-group-item">
            <div class="row">
              <div class="col-sm-12">
                <c:choose>
                  <c:when test="${i == 0}">
                    <sf:label path="viewVars[${i}].missingFormat">
                      <s:message code="spss.type.SPSS_FMT_A" />
                    </sf:label>
                    <c:set value="" var="jsValueFunc" />
                  </c:when>
                  <c:when test="${i == 1}">
                    <sf:label path="viewVars[${i}].missingFormat">
                      <s:message code="spss.type.SPSS_FMT_F" />
                    </sf:label>
                    <c:set value="checkNumberField" var="jsValueFunc" />
                  </c:when>
                  <c:when test="${i == 2}">
                    <sf:label path="viewVars[${i}].missingFormat">
                      <s:message code="spss.type.SPSS_FMT_DATE" />
                    </sf:label>
                    <c:set value="checkDateField" var="jsValueFunc" />
                  </c:when>
                </c:choose>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-4">
                <sf:select path="viewVars[${i}].missingFormat" class="form-control"
                  onchange="changeMissingFields(${i});" id="missingFormat_${i}">
                  <sf:option value="SPSS_NO_MISSVAL">
                    <s:message code="spss.missings.SPSS_NO_MISSVAL" />
                  </sf:option>
                  <sf:option value="SPSS_ONE_MISSVAL">
                    <s:message code="spss.missings.SPSS_ONE_MISSVAL" />
                  </sf:option>
                  <sf:option value="SPSS_TWO_MISSVAL">
                    <s:message code="spss.missings.SPSS_TWO_MISSVAL" />
                  </sf:option>
                  <sf:option value="SPSS_THREE_MISSVAL">
                    <s:message code="spss.missings.SPSS_THREE_MISSVAL" />
                  </sf:option>
                  <c:if test="${i != 0}">
                    <sf:option value="SPSS_MISS_RANGE">
                      <s:message code="spss.missings.SPSS_MISS_RANGE" />
                    </sf:option>
                    <sf:option value="SPSS_MISS_RANGEANDVAL">
                      <s:message code="spss.missings.SPSS_MISS_RANGEANDVAL" />
                    </sf:option>
                  </c:if>
                </sf:select>
              </div>
              <div class="col-sm-2">
                <sf:input path="viewVars[${i}].missingVal1" class="form-control hideonload" id="missingVal1_${i}"
                  onkeyup="${jsValueFunc}('missingVal1_${i}')" />
              </div>
              <div class="col-sm-1 hideonload seperatormodal" id="missingSep1_${i}">
                <s:message text="," />
              </div>
              <div class="col-sm-2">
                <sf:input path="viewVars[${i}].missingVal2" class="form-control hideonload" id="missingVal2_${i}"
                  onkeyup="${jsValueFunc}('missingVal2_${i}')" />
              </div>
              <div class="col-sm-1 hideonload seperatormodal" id="missingSep2_${i}">
                <s:message text="," />
              </div>
              <div class="col-sm-2">
                <sf:input path="viewVars[${i}].missingVal3" class="form-control hideonload" id="missingVal3_${i}"
                  onkeyup="${jsValueFunc}('missingVal3_${i}')" />
              </div>
            </div>
          </li>
        </c:forEach>
      </ul>
    </div>
    <div class="modal-footer">
      <div class="form-group">
        <div class="col-sm-offset-0 col-md-12">
          <button class="btn btn-default" data-dismiss="modal">Close</button>
          <sf:button type="submit" class="btn btn-success" name="setGlobalMissings">
            <s:message code="gen.submit" />
          </sf:button>
        </div>
      </div>
    </div>
  </div>
</sf:form>