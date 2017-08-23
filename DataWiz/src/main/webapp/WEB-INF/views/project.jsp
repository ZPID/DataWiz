<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<c:set var="valimag1" value="zpid.png" scope="session" />
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <c:choose>
          <c:when test="${empty ProjectForm.project.id}">
            <h4>
              <s:message code="project.create.headline" />
            </h4>
            <div>
              <s:message code="project.create.info" />
            </div>
          </c:when>
          <c:otherwise>
            <h4>
              <s:message code="project.edit.headline" />
            </h4>
            <div>
              <s:message code="project.edit.info" />
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      <c:url var="projectUrl" value="/project/${ProjectForm.project.id}" />
      <sf:form action="${projectUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <input type="hidden" id="jQueryMap" name="jQueryMap" value="${jQueryMap}" />
        <sf:hidden path="delPos" />
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <!-- Projectname -->
        <c:set var="input_vars" value="project.title;project.edit.title; ; ;row margin-bottom-0" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- Projectident -->
        <c:set var="input_vars" value="project.projectIdent;project.edit.projectIdent; ; ;row margin-bottom-0" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- Description -->
        <c:set var="input_vars" value="project.description;project.edit.description; ; ;row margin-bottom-0" />
        <%@ include file="templates/gen_textarea.jsp"%>
        <!-- Funding -->
        <c:set var="input_vars" value="project.funding;project.edit.funding; ; ;row margin-bottom-0" />
        <c:set var="valimages" value="${valimag1}" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- grant number -->
        <c:set var="input_vars" value="project.grantNumber;project.edit.grantNumber; ; ;row margin-bottom-0" />
        <%@ include file="templates/gen_input.jsp"%>
        <!-- PrimaryContributor -->
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-sm-11">
                <label class="control-label" for="project.title"><s:message
                    code="project.edit.primaryContributors" /></label>
              </div>
              <div class="col-sm-1 text-align-right">
                <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
              </div>
            </div>
            <ul class="list-group">
              <li class="list-group-item">
                <div class="form-group">
                  <!-- PrimaryContributor title -->
                  <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.title" /></label>
                  <div class="col-md-3">
                    <s:bind path="primaryContributor.title">
                      <c:choose>
                        <c:when test="${status.error}">
                          <sf:input path="primaryContributor.title" class="form-control" style="border: 1px solid red;"
                            title="${status.errorMessage}" data-toggle="tooltip" />
                        </c:when>
                        <c:otherwise>
                          <sf:input path="primaryContributor.title" class="form-control" />
                        </c:otherwise>
                      </c:choose>
                    </s:bind>
                  </div>
                  <!-- PrimaryContributor ORCID -->
                  <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.orcid" /></label>
                  <div class="col-md-3">
                    <s:bind path="primaryContributor.orcid">
                      <c:choose>
                        <c:when test="${status.error}">
                          <sf:input path="primaryContributor.orcid" class="form-control" style="border: 1px solid red;"
                            title="${status.errorMessage}" data-toggle="tooltip" />
                        </c:when>
                        <c:otherwise>
                          <sf:input path="primaryContributor.orcid" class="form-control" />
                        </c:otherwise>
                      </c:choose>
                    </s:bind>
                  </div>
                </div>
                <div class="form-group">
                  <!-- PrimaryContributor firstName -->
                  <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.firstName" /></label>
                  <div class="col-md-3">
                    <s:bind path="primaryContributor.firstName">
                      <c:choose>
                        <c:when test="${status.error}">
                          <sf:input path="primaryContributor.firstName" class="form-control"
                            style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                        </c:when>
                        <c:otherwise>
                          <sf:input path="primaryContributor.firstName" class="form-control" />
                        </c:otherwise>
                      </c:choose>
                    </s:bind>
                  </div>
                  <!-- PrimaryContributor lastName -->
                  <label for="inputValue" class="col-md-2 control-label"><s:message code="gen.lastName" /></label>
                  <div class="col-md-3">
                    <s:bind path="primaryContributor.lastName">
                      <c:choose>
                        <c:when test="${status.error}">
                          <sf:input path="primaryContributor.lastName" class="form-control"
                            style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                        </c:when>
                        <c:otherwise>
                          <sf:input path="primaryContributor.lastName" class="form-control" />
                        </c:otherwise>
                      </c:choose>
                    </s:bind>
                  </div>
                </div>
                <div class="form-group">
                  <!-- PrimaryContributor institution -->
                  <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.institution" /></label>
                  <div class="col-md-8">
                    <s:bind path="primaryContributor.institution">
                      <c:choose>
                        <c:when test="${status.error}">
                          <sf:input path="primaryContributor.institution" class="form-control"
                            style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                        </c:when>
                        <c:otherwise>
                          <sf:input path="primaryContributor.institution" class="form-control" />
                        </c:otherwise>
                      </c:choose>
                    </s:bind>
                  </div>
                </div>
                <div class="form-group">
                  <!-- Contributors department-->
                  <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.department" /></label>
                  <div class="col-md-8">
                    <s:bind path="primaryContributor.department">
                      <c:choose>
                        <c:when test="${status.error}">
                          <sf:input path="primaryContributor.department" class="form-control"
                            style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                        </c:when>
                        <c:otherwise>
                          <sf:input path="primaryContributor.department" class="form-control" />
                        </c:otherwise>
                      </c:choose>
                    </s:bind>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <!-- Contributors -->
        <div class="form-group">
          <div class="col-sm-12">
            <div class="row">
              <div class="col-sm-11">
                <label class="control-label" for="project.title"><s:message code="project.edit.contributors" /></label>
              </div>
              <div class="col-sm-1 text-align-right">
                <img src="/DataWiz/static/images/${valimag1}" class="infoImages" />
              </div>
            </div>
            <ul class="list-group">
              <c:forEach items="${ProjectForm.contributors}" var="contri" varStatus="coloop">
                <li class="list-group-item">
                  <div class="form-group">
                    <!-- Contributors title -->
                    <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.title" /></label>
                    <div class="col-md-4">
                      <s:bind path="contributors[${coloop.count-1}].title">
                        <c:choose>
                          <c:when test="${status.error}">
                            <sf:input path="contributors[${coloop.count-1}].title" class="form-control"
                              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                          </c:when>
                          <c:otherwise>
                            <sf:input path="contributors[${coloop.count-1}].title" class="form-control" />
                          </c:otherwise>
                        </c:choose>
                      </s:bind>
                    </div>
                    <label for="inputKey" class="col-md-1 control-label"><s:message code="gen.orcid" /></label>
                    <div class="col-md-4">
                      <s:bind path="contributors[${coloop.count-1}].orcid">
                        <c:choose>
                          <c:when test="${status.error}">
                            <sf:input path="contributors[${coloop.count-1}].orcid" class="form-control"
                              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                          </c:when>
                          <c:otherwise>
                            <sf:input path="contributors[${coloop.count-1}].orcid" class="form-control" />
                          </c:otherwise>
                        </c:choose>
                      </s:bind>
                    </div>
                  </div> <!-- Contributors first and lastname-->
                  <div class="form-group">
                    <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.firstName" /></label>
                    <div class="col-md-4">
                      <s:bind path="contributors[${coloop.count-1}].firstName">
                        <c:choose>
                          <c:when test="${status.error}">
                            <sf:input path="contributors[${coloop.count-1}].firstName" class="form-control"
                              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                          </c:when>
                          <c:otherwise>
                            <sf:input path="contributors[${coloop.count-1}].firstName" class="form-control" />
                          </c:otherwise>
                        </c:choose>
                      </s:bind>
                    </div>
                    <label for="inputValue" class="col-md-1 control-label"><s:message code="gen.lastName" /></label>
                    <div class="col-md-4">
                      <s:bind path="contributors[${coloop.count-1}].lastName">
                        <c:choose>
                          <c:when test="${status.error}">
                            <sf:input path="contributors[${coloop.count-1}].lastName" class="form-control"
                              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                          </c:when>
                          <c:otherwise>
                            <sf:input path="contributors[${coloop.count-1}].lastName" class="form-control" />
                          </c:otherwise>
                        </c:choose>
                      </s:bind>
                    </div>
                  </div> <!-- Contributors institution-->
                  <div class="form-group">
                    <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.institution" /></label>
                    <div class="col-md-9">
                      <s:bind path="contributors[${coloop.count-1}].institution">
                        <c:choose>
                          <c:when test="${status.error}">
                            <sf:input path="contributors[${coloop.count-1}].institution" class="form-control"
                              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                          </c:when>
                          <c:otherwise>
                            <sf:input path="contributors[${coloop.count-1}].institution" class="form-control" />
                          </c:otherwise>
                        </c:choose>
                      </s:bind>
                    </div>
                  </div> <!-- Contributors department-->
                  <div class="form-group">
                    <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.department" /></label>
                    <div class="col-md-9">
                      <s:bind path="contributors[${coloop.count-1}].department">
                        <c:choose>
                          <c:when test="${status.error}">
                            <sf:input path="contributors[${coloop.count-1}].department" class="form-control"
                              style="border: 1px solid red;" title="${status.errorMessage}" data-toggle="tooltip" />
                          </c:when>
                          <c:otherwise>
                            <sf:input path="contributors[${coloop.count-1}].department" class="form-control" />
                          </c:otherwise>
                        </c:choose>
                      </s:bind>
                    </div>
                    <div class="col-md-1 col-sm-12 text-align-right">
                      <sf:button type="submit" name="deleteContributor" class="btn btn-danger btn-sm"
                        onclick="document.getElementById('delPos').value=${coloop.count-1}">
                        <s:message code="gen.delete" />
                      </sf:button>
                    </div>
                  </div>
                </li>
              </c:forEach>
              <li class="list-group-item">
                <div class="row">
                  <div class="col-sm-12 text-align-right">
                    <sf:button type="submit" name="addContributor" class="btn btn-success btn-sm">
                      <s:message code="gen.add" />
                    </sf:button>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <!-- Tags -->
        <%-- <div class="form-group">
          <label class="control-label col-sm-2" for="project.description"><s:message code="project.edit.tags" /></label>
          <div class="col-sm-10">
            <div id="tagging"></div>
            <sf:input type="hidden" path="tags" />
            <sf:errors path="project.description" cssClass="alert alert-danger" element="div" />
          </div>
        </div> --%>
        <!-- Buttons -->
        <div class="row">
          <div class="col-xs-6 text-align-left">
            <a href="<c:url value="/panel" />" class="btn btn-default btn-sm"><s:message code="back.to.panel" /></a>
          </div>
          <div class="col-xs-6 text-align-right">
            <sf:button type="submit" class="btn btn-success btn-sm" id="meta_submit">
              <s:message code="gen.submit" />
            </sf:button>
          </div>
        </div>
      </sf:form>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>