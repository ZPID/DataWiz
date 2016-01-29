<div id="metadataActiveContent" class="projectContent">
          <!-- Infotxt -->
          <div class="form-group">
            <div class="col-sm-12">
              <div class="well marginTop1">
                <s:message code="project.edit.metadata.info" />
              </div>
            </div>
          </div>
          <ul class="list-group">
            <!-- selectedMetaPurposes -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.selectedMetaPurposes"><s:message code="dmp.edit.selectedMetaPurposes" /></label>
                  <div class="form-group">
                    <div class="col-sm-12">
                      <c:forEach items="${ProjectForm.metaPurposes}" var="dtype">
                        <label class="btn btn-default col-sm-12" style="text-align: left;"><sf:checkbox
                            path="dmp.selectedMetaPurposes" value="${dtype.id}" /> <s:message
                            text="${localeCode eq 'de' ? dtype.nameDE : dtype.nameEN}" /></label>
                      </c:forEach>
                    </div>
                  </div>
                  <s:message code="dmp.edit.selectedMetaPurposes.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
            <!-- metaDescription -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.metaDescription"><s:message code="dmp.edit.metaDescription" /></label>
                  <sf:textarea rows="5" path="dmp.metaDescription" class="form-control" disabled="" />
                  <s:message code="dmp.edit.metaDescription.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
            <!-- metaDescription -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.metaDescription"><s:message code="dmp.edit.metaDescription" /></label>
                  <sf:textarea rows="5" path="dmp.metaDescription" class="form-control" disabled="" />
                  <s:message code="dmp.edit.metaDescription.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
            <!-- metaFramework -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.metaFramework"><s:message code="dmp.edit.metaFramework" /></label>
                  <sf:textarea rows="5" path="dmp.metaFramework" class="form-control" disabled="" />
                  <s:message code="dmp.edit.metaFramework.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
            <!-- metaGeneration -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.metaGeneration"><s:message code="dmp.edit.metaGeneration" /></label>
                  <sf:textarea rows="5" path="dmp.metaGeneration" class="form-control" disabled="" />
                  <s:message code="dmp.edit.metaGeneration.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
            <!-- metaMonitor -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.metaMonitor"><s:message code="dmp.edit.metaMonitor" /></label>
                  <sf:textarea rows="5" path="dmp.metaMonitor" class="form-control" disabled="" />
                  <s:message code="dmp.edit.metaMonitor.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
            <!-- metaFormat -->
            <li class="list-group-item">
              <div class="form-group">
                <div class="col-sm-12">
                  <label for="dmp.metaFormat"><s:message code="dmp.edit.metaFormat" /></label>
                  <sf:textarea rows="5" path="dmp.metaFormat" class="form-control" disabled="" />
                  <s:message code="dmp.edit.metaFormat.help" var="appresmess" />
                  <%@ include file="../templates/helpblock.jsp"%>
                </div>
              </div>
            </li>
          </ul>
        </div>