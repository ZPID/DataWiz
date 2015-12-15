<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <div class="login-card">
      <ul class="nav nav-tabs nav-justified">
        <li role="presentation" class="active"><a href="#">Projekt</a></li>
        <li role="presentation" id="studies_active_click"><a href="#">DMP</a></li>
        <li role="presentation"><a href="#">Sharing</a></li>
        <li role="presentation"><a href="#">Wissensbasis</a></li>
      </ul>
      <div>
        <div class="page-header">
          <h4>
            <s:message code="project.edit.headline" />
          </h4>
          <div>
            <s:message code="project.edit.info" />
          </div>
        </div>
        <!-- Submenu -->
        <ul class="nav nav-tabs">
          <li role="presentation" id="metaActiveClick" class="active projectContentClick"><a><s:message
                code="project.submenu.metadata" /></a></li>
          <li role="presentation" id="studiesActiveClick" class="projectContentClick"><a><s:message code="project.submenu.studies" /></a></li>
          <li role="presentation" id="materialsActiveClick" class="projectContentClick"><a><s:message
                code="project.submenu.material" /></a></li>
        </ul>
        <div id="metaActiveContent" class="projectContent">
          <div class="well">
            <s:message code="project.edit.metadata.info" />
          </div>
          <c:url var="projectUrl" value="/project/${ProjectForm.project.id}" />
          <sf:form action="${projectUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
            <c:if test="${not empty saveState && saveState != '' && not empty saveStateMsg}">
              <div
                class="alert <c:out value="${saveState eq 'SUCCESS' ? 'alert-success' : 
                                                saveState eq 'ERROR' ? 'alert-danger' : 
                                                saveState eq 'INFO' ? 'alert-warning' : 'alert-info'}"/>"
                role="alert">
                <c:out value="${saveStateMsg}" />
              </div>
            </c:if>
            <!-- Projectname -->
            <div class="form-group">
              <label class="control-label col-sm-2 required" for="project.title"><s:message code="project.edit.title" /></label>
              <div class="col-sm-10">
                <s:message code="project.edit.title.ph" var="reg_first" />
                <sf:input path="project.title" class="form-control" placeholder="${reg_first}" />
                <sf:errors path="project.title" cssClass="alert alert-danger" element="div" />
              </div>
            </div>
            <!-- PrimaryContributor -->
            <div class="form-group">
              <label class="control-label col-sm-2" for="project.title"><s:message code="project.edit.primaryContributors" /></label>
              <div class="col-sm-10">
                <ul class="list-group">
                  <li class="list-group-item">
                    <div class="form-group">
                      <div class="form-group row">
                        <!-- PrimaryContributor title -->
                        <label for="inputKey" class="col-md-2 control-label">Titel</label>
                        <div class="col-md-3">
                          <s:message code="project.edit.title.ph" var="reg_first" />
                          <sf:input path="primaryContributor.title" class="form-control" placeholder="${reg_first}" />
                        </div>
                        <!-- PrimaryContributor ORCID -->
                        <label for="inputKey" class="col-md-2 control-label">ORCID</label>
                        <div class="col-md-3">
                          <s:message code="project.edit.title.ph" var="reg_first" />
                          <sf:input path="primaryContributor.orcid" class="form-control" placeholder="${reg_first}" />
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="form-group row">
                        <!-- PrimaryContributor firstName -->
                        <label for="inputKey" class="col-md-2 control-label">Vorname</label>
                        <div class="col-md-3">
                          <s:message code="project.edit.title.ph" var="reg_first" />
                          <sf:input path="primaryContributor.firstName" class="form-control" placeholder="${reg_first}" />
                        </div>
                        <!-- PrimaryContributor lastName -->
                        <label for="inputValue" class="col-md-2 control-label">Nachname</label>
                        <div class="col-md-3">
                          <s:message code="project.edit.title.ph" var="reg_first" />
                          <sf:input path="primaryContributor.lastName" class="form-control" placeholder="${reg_first}" />
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="form-group row">
                        <!-- PrimaryContributor institution -->
                        <label for="inputKey" class="col-md-2 control-label">Institut</label>
                        <div class="col-md-8">
                          <s:message code="project.edit.title.ph" var="reg_first" />
                          <sf:input path="primaryContributor.institution" class="form-control" placeholder="${reg_first}" />
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="form-group row">
                        <!-- Contributors department-->
                        <label for="inputKey" class="col-md-2 control-label">Abteilung</label>
                        <div class="col-md-8">
                          <s:message code="project.edit.title.ph" var="reg_first" />
                          <sf:input path="primaryContributor.department" class="form-control" placeholder="${reg_first}" />
                        </div>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <!-- Contributors -->
            <div class="form-group">
              <label class="control-label col-sm-2" for="project.title"><s:message code="project.edit.contributors" /></label>
              <div class="col-sm-10">
                <ul class="list-group">
                  <li class="list-group-item">
                    <div class="row">
                      <div class="col-md-5"></div>
                      <div class="col-md-5"></div>
                      <div class="col-md-2">
                        <sf:button type="submit" name="addContributor" class="btn btn-success btn-xs">
                          hinzuf�gen
                        </sf:button>
                      </div>
                    </div>
                  </li>
                  <c:forEach items="${ProjectForm.contributors}" var="contri" varStatus="coloop">
                    <li class="list-group-item">
                      <div class="form-group">
                        <!-- Contributors title -->
                        <div class="form-group row">
                          <label for="inputKey" class="col-md-2 control-label">Titel</label>
                          <div class="col-md-3">
                            <s:message code="project.edit.title.ph" var="reg_first" />
                            <sf:input path="contributors[${coloop.count-1}].title" class="form-control" placeholder="${reg_first}" />
                          </div>
                          <label for="inputKey" class="col-md-2 control-label">ORCID</label>
                          <div class="col-md-3">
                            <s:message code="project.edit.title.ph" var="reg_first" />
                            <sf:input path="contributors[${coloop.count-1}].orcid" class="form-control" placeholder="${reg_first}" />
                            <sf:errors path="contributors[${coloop.count-1}].orcid" cssClass="alert alert-danger" element="div"
                              htmlEscape="false" />
                          </div>
                          <div class="col-md-1">
                            <sf:button type="button" class="btn btn-danger btn-xs">
                                l�schen
                            </sf:button>
                          </div>
                        </div>
                      </div> <!-- Contributors first and lastname-->
                      <div class="form-group">
                        <div class="form-group row">
                          <label for="inputKey" class="col-md-2 control-label">Vorname</label>
                          <div class="col-md-3">
                            <s:message code="project.edit.title.ph" var="reg_first" />
                            <sf:input path="contributors[${coloop.count-1}].firstName" class="form-control" placeholder="${reg_first}" />
                          </div>
                          <label for="inputValue" class="col-md-2 control-label">Nachname</label>
                          <div class="col-md-3">
                            <s:message code="project.edit.title.ph" var="reg_first" />
                            <sf:input path="contributors[${coloop.count-1}].lastName" class="form-control" placeholder="${reg_first}" />
                          </div>
                        </div>
                      </div> <!-- Contributors institution-->
                      <div class="form-group">
                        <div class="form-group row">
                          <label for="inputKey" class="col-md-2 control-label">Institut</label>
                          <div class="col-md-8">
                            <s:message code="project.edit.title.ph" var="reg_first" />
                            <sf:input path="contributors[${coloop.count-1}].institution" class="form-control" placeholder="${reg_first}" />
                          </div>
                        </div>
                      </div> <!-- Contributors department-->
                      <div class="form-group">
                        <div class="form-group row">
                          <label for="inputKey" class="col-md-2 control-label">Abteilung</label>
                          <div class="col-md-8">
                            <s:message code="project.edit.title.ph" var="reg_first" />
                            <sf:input path="contributors[${coloop.count-1}].department" class="form-control" placeholder="${reg_first}" />
                          </div>
                        </div>
                      </div>
                    </li>
                  </c:forEach>
                </ul>
              </div>
            </div>
            <!-- Description -->
            <div class="form-group">
              <label class="control-label col-sm-2" for="project.description"><s:message code="project.edit.description" /></label>
              <div class="col-sm-10">
                <s:message code="project.edit.description.ph" var="reg_first" />
                <sf:textarea rows="5" path="project.description" class="form-control" placeholder="${reg_first}" />
                <sf:errors path="project.description" cssClass="alert alert-danger" element="div" />
              </div>
            </div>
            <!-- Tags -->
            <div class="form-group">
              <label class="control-label col-sm-2" for="project.description"><s:message code="project.edit.tags" /></label>
              <div class="col-sm-10">
                <div id="tagging"></div>
                <sf:input type="hidden" path="tags" />
                <sf:errors path="project.description" cssClass="alert alert-danger" element="div" />
              </div>
            </div>
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
        <div id="studiesActiveContent" class="projectContent">Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy
          eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et
          ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
          consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
          vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
          amet.</div>
        <div id="materialsActiveContent" class="projectContent">Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam
          nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo
          dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit
          amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam
          voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem
          ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et
          dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,
          no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy
          eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et
          ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</div>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>