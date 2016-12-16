<%@ include file="../templates/includes.jsp"%>
<c:url var="accessUrl"
  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.previousRecordVersion.id}" />
<sf:form action="${accessUrl}" commandName="VarValues" class="form-horizontal" role="form">
  <sf:hidden path="id" />
  <c:choose>
    <c:when test="${modalView eq 'values'}">
      <div class="modal-content panel-primary">
        <div class="modal-header panel-heading">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Values</h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="col-sm-3">WERT</div>
              <div class="col-sm-1"></div>
              <div class="col-sm-6">Beschriftung</div>
              <div class="col-sm-2"></div>
            </div>
          </div>
          <div class="valvar_wrap">
            <c:forEach items="${VarValues.values}" var="val" varStatus="loop">
              <div class="form-group" id="values${loop.count-1}">
                <sf:hidden path="values[${loop.count-1}].id" id="values${loop.count-1}id" />
                <div class="col-sm-12">
                  <div class="col-sm-3">
                    <sf:input id="values${loop.count-1}val" class="form-control" path="values[${loop.count-1}].value" />
                  </div>
                  <div class="col-sm-1">
                    <s:message text="=" />
                  </div>
                  <div class="col-sm-6">
                    <sf:input id="values${loop.count-1}label" class="form-control" path="values[${loop.count-1}].label" />
                  </div>
                  <div class="col-sm-2">
                    <button class="btn btn-danger" onclick="delVarValues(${loop.count-1});return false;">X</button>
                  </div>
                </div>
              </div>
            </c:forEach>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <button class="btn btn-success" onclick="addValueLabel(4);return false;">+</button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="form-group">
            <div class="col-sm-offset-0 col-md-12">
              <button class="btn btn-default" data-dismiss="modal">Close</button>
              <sf:button type="submit" class="btn btn-success" name="setValues">
                <s:message code="gen.submit" />
              </sf:button>
            </div>
          </div>
        </div>
      </div>
    </c:when>
    <c:when test="${modalView eq 'missings'}">
      <div class="modal-content panel-primary">
        <div class="modal-header panel-heading">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Fehlende Werte</h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <div class="col-sm-12">
              <sf:select path="missingFormat" class="form-control" onchange="changeMissingFields();">
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
                <sf:option value="SPSS_MISS_RANGE">
                  <s:message code="spss.missings.SPSS_MISS_RANGE" />
                </sf:option>
                <sf:option value="SPSS_MISS_RANGEANDVAL">
                  <s:message code="spss.missings.SPSS_MISS_RANGEANDVAL" />
                </sf:option>
              </sf:select>
              <script>
              	changeMissingFields();
			  </script>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-12">
              <div class="col-sm-3" style="white-space: nowrap;">
                <sf:input path="missingVal1" class="form-control" />
              </div>
              <div class="col-sm-1" id="missingSep1" style="font-size: 26px;">,</div>
              <div class="col-sm-3">
                <sf:input path="missingVal2" class="form-control" />
              </div>
              <div class="col-sm-1" id="missingSep2" style="font-size: 26px;">,</div>
              <div class="col-sm-3">
                <sf:input path="missingVal3" class="form-control" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="form-group">
            <div class="col-sm-offset-0 col-md-12">
              <button class="btn btn-default" data-dismiss="modal">Close</button>
              <sf:button type="submit" class="btn btn-success" name="setMissings">
                <s:message code="gen.submit" />
              </sf:button>
            </div>
          </div>
        </div>
      </div>
    </c:when>
    <c:when test="${modalView eq 'type'}">
      <div class="modal-content panel-primary">
        <div class="modal-header panel-heading">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Type</h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <div class="col-sm-6">
              <div>
                <sf:label path="type">
                  <s:message code="spss.type.SPSS_FMT_A" />
                </sf:label>
                <sf:radiobutton path="type" value="SPSS_FMT_A" />
              </div>
              <div>
                <sf:label path="type">
                  <s:message code="spss.type.SPSS_FMT_F" />
                </sf:label>
                <sf:radiobutton path="type" value="SPSS_FMT_F" />
              </div>
              <div>
                <sf:label path="type">
                  <s:message code="spss.type.SPSS_FMT_DOLLAR" />
                </sf:label>
                <sf:radiobutton path="type" value="" />
              </div>
            </div>
            <div class="col-sm-6">
              <sf:select path="type">
                <sf:option value="SPSS_FMT_DOLLAR">
                  <s:message code="spss.type.SPSS_FMT_DOLLAR" />
                </sf:option>
              </sf:select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="form-group">
            <div class="col-sm-offset-0 col-md-12">
              <button class="btn btn-default" data-dismiss="modal">Close</button>
              <sf:button type="submit" class="btn btn-success" name="setType">
                <s:message code="gen.submit" />
              </sf:button>
            </div>
          </div>
        </div>
      </div>
    </c:when>
  </c:choose>
</sf:form>