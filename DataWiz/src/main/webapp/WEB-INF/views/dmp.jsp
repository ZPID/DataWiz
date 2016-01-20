<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.edit.headline" />
        </h4>
        <div>
          <s:message code="project.edit.info" />
        </div>
      </div>
      <ul class="nav nav-tabs">
        <li role="presentation" id="administratriveActiveClick" class="projectContentClick"><a><s:message
              code="dmp.submenu.administrative" /></a></li>
        <li role="presentation" id="researchActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.research" /></a></li>
        <li role="presentation" id="metadataActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.metadata" /></a></li>
        <li role="presentation" id="accessActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.access" /></a></li>
        <li role="presentation" id="storageActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.storage" /></a></li>
        <li role="presentation" id="organizationActiveClick" class="projectContentClick"><a><s:message
              code="dmp.submenu.organization" /></a></li>
        <li role="presentation" id="ethicalActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.ethical" /></a></li>
        <li role="presentation" id="costsActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.costs" /></a></li>
      </ul>
      <c:url var="projectUrl" value="/dmp/${ProjectForm.project.id}" />
      <sf:form action="${projectUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <input type="hidden" id="jQueryMap" name="jQueryMap" value="${jQueryMap}" />
        <!-- Messages -->
        <c:if test="${not empty saveState && saveState != '' && not empty saveStateMsg && empty jQueryMap}">
          <div
            class="alert <c:out value="${saveState eq 'SUCCESS' ? 'alert-success' : 
                                                saveState eq 'ERROR' ? 'alert-danger' : 
                                                saveState eq 'INFO' ? 'alert-warning' : 'alert-info'}"/>"
            role="alert">
            <c:out value="${saveStateMsg}" />
          </div>
        </c:if>
        <div id="administratriveActiveContent" class="projectContent contentMargin">
          <div class="form-group">
            <div class="well marginTop1">
              <s:message code="project.edit.metadata.info" />
            </div>
          </div>
          <!-- Projectname -->
          <div class="form-group">
            <label class="required" for="project.title"><s:message code="dmp.edit.projectname" /></label>
            <div>
              <s:message code="project.edit.title.ph" var="reg_first" />
              <sf:input path="project.title" class="form-control" placeholder="${reg_first}" />
              <sf:errors path="project.title" cssClass="alert alert-danger" element="div" />
            </div>
          </div>
          <!-- Projectaims -->
          <div class="form-group">
            <label for="project.projectIdent"><s:message code="dmp.edit.projectaims" /></label>
            <sf:textarea rows="5" path="dmp.projectAims" class="form-control" disabled="" />
            <div class="row help-block">
              <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
              <div class="col-sm-11">
                <s:message code="dmp.edit.projectaims.help" />
              </div>
            </div>
          </div>
          <!-- Funding -->
          <div class="form-group">
            <label for="project.projectIdent"><s:message code="dmp.edit.funding" /></label>
            <sf:input path="project.funding" class="form-control" />
            <div class="row help-block">
              <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
              <div class="col-sm-11 ">
                <s:message code="dmp.edit.funding.help" />
              </div>
            </div>
          </div>
          <!-- duration -->
          <div class="form-group">
            <label for="project.projectIdent"><s:message code="dmp.edit.duration" /></label>
            <sf:input path="dmp.duration" class="form-control" disabled="" />
          </div>
          <!-- organizations -->
          <div class="form-group">
            <label for="project.projectIdent"><s:message code="dmp.edit.organizations" /></label>
            <sf:input path="dmp.organizations" class="form-control" disabled="" />
          </div>
          <!-- PrimaryContributor -->
          <div class="form-group">
            <label for="project.title"><s:message code="dmp.edit.leader" /></label>
            <div>
              <ul class="list-group">
                <li class="list-group-item">
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- PrimaryContributor title -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.title" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.title.ph" var="reg_first" />
                        <sf:input path="primaryContributor.title" class="form-control" placeholder="${reg_first}" />
                      </div>
                      <!-- PrimaryContributor ORCID -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.orcid" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.orcid.ph" var="reg_first" />
                        <sf:input path="primaryContributor.orcid" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- PrimaryContributor firstName -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.first.name" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.first.name.ph" var="reg_first" />
                        <sf:input path="primaryContributor.firstName" class="form-control" placeholder="${reg_first}" />
                      </div>
                      <!-- PrimaryContributor lastName -->
                      <label for="inputValue" class="col-md-2 control-label"><s:message code="gen.last.name" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.last.name.ph" var="reg_first" />
                        <sf:input path="primaryContributor.lastName" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- PrimaryContributor institution -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.intitution" /></label>
                      <div class="col-md-8">
                        <s:message code="gen.intitution.ph" var="reg_first" />
                        <sf:input path="primaryContributor.institution" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- Contributors department-->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.department" /></label>
                      <div class="col-md-8">
                        <s:message code="gen.department.ph" var="reg_first" />
                        <sf:input path="primaryContributor.department" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
          <!-- dmp-aims -->
          <div class="form-group">
            <label for="project.projectIdent"><s:message code="dmp.edit.dmpaims" /></label>
            <sf:textarea rows="5" path="dmp.planAims" class="form-control" disabled="" />
            <div class="row help-block">
              <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
              <div class="col-sm-11">
                <s:message code="dmp.edit.dmpaims.help" />
              </div>
            </div>
          </div>
        </div>
        <!-- START Research Data Content -->
        <div id="researchActiveContent" class="projectContent">
          <!-- Infotxt -->
          <div class="form-group">
            <div class="well marginTop1">
              <s:message code="project.edit.metadata.info" />
            </div>
          </div>
          <!-- START Existing Data -->
          <div class="form-group">
            <label for="dmp.existingData"><s:message code="dmp.edit.existingData" /></label>
            <ul class="list-group">
              <li class="list-group-item col-sm-12 " style="background-color: #eee;">
                <div class="form-group col-sm-12">
                  <sf:select path="dmp.existingData" class="form-control list-group-item-heading" id="selectExistingData"
                    onchange="switchViewIfSelected('selectExistingData', 1);">
                    <s:message code="dmp.edit.select.option.default" var="select_opt" />
                    <sf:option value="0" label="${select_opt}" disabled="true" />
                    <s:message code="dmp.edit.select.option1" var="select_opt" />
                    <sf:option value="1" label="${select_opt}" />
                    <s:message code="dmp.edit.select.option2" var="select_opt" />
                    <sf:option value="2" label="${select_opt}" />
                    <s:message code="dmp.edit.select.option3" var="select_opt" />
                    <sf:option value="3" label="${select_opt}" />
                  </sf:select>
                </div>
                <div class="row help-block col-sm-12">
                  <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                  <div class="col-sm-11">
                    <s:message code="dmp.edit.existingData.help" />
                  </div>
                </div> <!-- START contentExistingData -->
                <div id="contentExistingData">
                  <!-- existingDataRelevance -->
                  <div class="form-group row col-sm-12">
                    <label for="dmp.existingDataRelevance"><s:message code="dmp.edit.existingDataRelevance" /></label>
                    <sf:textarea rows="5" path="dmp.existingDataRelevance" class="form-control" disabled="" />
                    <div class="row help-block">
                      <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                      <div class="col-sm-11">
                        <s:message code="dmp.edit.existingDataRelevance.help" />
                      </div>
                    </div>
                  </div>
                  <!-- existingDataIntegration -->
                  <div class="form-group row col-sm-12">
                    <label for="dmp.existingDataIntegration"><s:message code="dmp.edit.existingDataIntegration" /></label>
                    <sf:textarea rows="5" path="dmp.existingDataIntegration" class="form-control" disabled="" />
                    <div class="row help-block">
                      <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                      <div class="col-sm-11">
                        <s:message code="dmp.edit.existingDataIntegration.help" />
                      </div>
                    </div>
                  </div>
                </div> <!-- END contentExistingData -->
              </li>
            </ul>
          </div>
          <!-- END Existing Data -->
          <!-- START Data Types -->
          <div class="form-group">
            <label for="dmp.usedDataTypes"><s:message code="dmp.edit.usedDataTypes" /></label>
            <ul class="list-group">
              <li class="list-group-item col-sm-12 " style="background-color: #eee;">
                <ul class="list-group col-sm-12">
                  <c:forEach items="${ProjectForm.dataTypes}" var="dtype">
                    <li class="list-group-item col-sm-4 "><c:choose>
                        <c:when test="${dtype.id == 0}">
                          <sf:checkbox path="dmp.usedDataTypes" label="${dtype.nameDE}" value="${dtype.id}"
                            onchange="switchViewIfChecked('selectOtherDataTypes')" id="selectOtherDataTypes" />
                        </c:when>
                        <c:otherwise>
                          <sf:checkbox path="dmp.usedDataTypes" label="${dtype.nameDE}" value="${dtype.id}" />
                        </c:otherwise>
                      </c:choose></li>
                  </c:forEach>
                </ul>
                <div class="row help-block col-sm-12">
                  <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                  <div class="col-sm-11">
                    <s:message code="dmp.edit.usedDataTypes.help" />
                  </div>
                </div> <!-- START contentExistingData -->
                <div id="contentOtherDataTypes">
                  <!-- existingDataRelevance -->
                  <div class="form-group row col-sm-12">
                    <label for="dmp.otherDataTypes"><s:message code="dmp.edit.otherDataTypes" /></label>
                    <sf:textarea rows="5" path="dmp.otherDataTypes" class="form-control" disabled="" />
                  </div>
                </div> <!-- END contentExistingData -->
              </li>
            </ul>
          </div>
          <!-- END Data Types -->
          <!-- Data Reproducibility -->
          <div class="form-group" >
            <label for="dmp.dataReproducibility"><s:message code="dmp.edit.dataReproducibility" /></label>
            <sf:textarea rows="5" path="dmp.dataReproducibility" class="form-control" disabled="" />
            <div class="row help-block">
              <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
              <div class="col-sm-11">
                <s:message code="dmp.edit.dataReproducibility.help" />
              </div>
            </div>
          </div>
          <!-- START Collecting/Generating Data -->
          <div class="form-group">
            <label for="dmp.usedDataTypes"><s:message code="dmp.edit.usedDataTypes" /></label>
            <ul class="list-group">
              <li class="list-group-item col-sm-12 " style="background-color: #eee;">
                <ul class="list-group col-sm-12">
                  <c:forEach items="${ProjectForm.dataTypes}" var="dtype">
                    <li class="list-group-item col-sm-6 "><c:choose>
                        <c:when test="${dtype.id == 10}">
                          <sf:checkbox path="dmp.usedDataTypes" label="${dtype.nameDE}" value="${dtype.id}"
                            onchange="switchViewIfChecked('selectCollectionModes')" id="selectCollectionModes" />
                        </c:when>
                        <c:otherwise>
                          <sf:checkbox path="dmp.usedDataTypes" label="${dtype.nameDE}" value="${dtype.id}" />
                        </c:otherwise>
                      </c:choose></li>
                  </c:forEach>
                </ul>
                <div class="row help-block col-sm-12">
                  <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
                  <div class="col-sm-11">
                    <s:message code="dmp.edit.usedDataTypes.help" />
                  </div>
                </div> <!-- START contentExistingData -->
                <div id="contentCollectionModes">
                  <!-- existingDataRelevance -->
                  <div class="form-group row col-sm-12">
                    <label for="dmp.otherDataTypes"><s:message code="dmp.edit.otherDataTypes" /></label>
                    <sf:textarea rows="5" path="dmp.otherDataTypes" class="form-control" disabled="" />
                  </div>
                </div> <!-- END contentExistingData -->
              </li>
            </ul>
          </div>
          <!-- Collecting/Generating Data -->











        </div>
        <!-- END Research Data Content -->
        <div id="metadataActiveContent" class="projectContent">789</div>
        <div id="accessActiveContent" class="projectContent">1234</div>
        <div id="storageActiveContent" class="projectContent">5678</div>
        <div id="organizationActiveContent" class="projectContent">12345</div>
        <div id="ethicalActiveContent" class="projectContent">67890</div>
        <div id="costsActiveContent" class="projectContent">1234567890</div>
        <!-- Buttons -->
        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <button type="reset" class="btn btn-default">
              <s:message code="gen.reset" />
            </button>
            <sf:button type="submit" class="btn btn-success" id="meta_submit">
              <s:message code="gen.submit" />
            </sf:button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>