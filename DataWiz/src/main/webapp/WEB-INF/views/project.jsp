<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
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
      <!-- Submenu -->
      <c:if test="${!hideMenu}">
        <ul class="nav nav-tabs">
          <li role="presentation" id="metaActiveClick" class="projectContentClick"><a><s:message
                code="project.submenu.metadata" /></a></li>
          <c:if test="${not empty ProjectForm.project.id}">
            <li role="presentation" id="workersActiveClick" class="projectContentClick"><a><s:message
                  code="project.submenu.workers" /></a></li>
            <li role="presentation" id="studiesActiveClick" class="projectContentClick"><a><s:message
                  code="project.submenu.studies" /></a></li>
            <li role="presentation" id="materialsActiveClick" class="projectContentClick"><a><s:message
                  code="project.submenu.material" /></a></li>
          </c:if>
        </ul>
      </c:if>
      <c:url var="projectUrl" value="/project/${ProjectForm.project.id}" />
      <sf:form action="${projectUrl}" commandName="ProjectForm" class="form-horizontal" role="form">
        <input type="hidden" id="jQueryMap" name="jQueryMap" value="${jQueryMap}" />
        <sf:hidden path="delPos" />
        <!-- Start Page Projectdata -->
        <div id="metaActiveContent" class="projectContent">
          <div class="form-group">
            <div class="col-sm-12">
              <div class="well marginTop1">
                <s:message code="project.edit.metadata.info" />
              </div>
            </div>
          </div>
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
          <!-- Projectname -->
          <div class="form-group">
            <label class="control-label col-sm-2 required" for="project.title"><s:message
                code="project.edit.title" /></label>
            <div class="col-sm-10">
              <s:message code="project.edit.title.ph" var="reg_first" />
              <sf:input path="project.title" class="form-control" placeholder="${reg_first}" />
              <sf:errors path="project.title" cssClass="alert alert-danger" element="div" />
            </div>
          </div>
          <!-- Projectident -->
          <div class="form-group">
            <label class="control-label col-sm-2" for="project.projectIdent"><s:message
                code="project.edit.projectIdent" /></label>
            <div class="col-sm-10">
              <s:message code="project.edit.projectIdent.ph" var="reg_first" />
              <sf:input path="project.projectIdent" class="form-control" placeholder="${reg_first}" />
              <sf:errors path="project.projectIdent" cssClass="alert alert-danger" element="div" />
            </div>
          </div>
          <!-- PrimaryContributor -->
          <div class="form-group">
            <label class="control-label col-sm-2" for="project.title"><s:message
                code="project.edit.primaryContributors" /></label>
            <div class="col-sm-10">
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
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.firstName" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.firstName.ph" var="reg_first" />
                        <sf:input path="primaryContributor.firstName" class="form-control" placeholder="${reg_first}" />
                      </div>
                      <!-- PrimaryContributor lastName -->
                      <label for="inputValue" class="col-md-2 control-label"><s:message code="gen.lastName" /></label>
                      <div class="col-md-3">
                        <s:message code="gen.lastName.ph" var="reg_first" />
                        <sf:input path="primaryContributor.lastName" class="form-control" placeholder="${reg_first}" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="form-group row">
                      <!-- PrimaryContributor institution -->
                      <label for="inputKey" class="col-md-2 control-label"><s:message code="gen.institution" /></label>
                      <div class="col-md-8">
                        <s:message code="gen.institution.ph" var="reg_first" />
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
          <!-- Funding -->
          <div class="form-group">
            <label class="control-label col-sm-2" for="project.funding"><s:message code="project.edit.funding" /></label>
            <div class="col-sm-10">
              <s:message code="project.edit.funding.ph" var="reg_first" />
              <sf:input path="project.funding" class="form-control" placeholder="${reg_first}" />
              <sf:errors path="project.funding" cssClass="alert alert-danger" element="div" />
            </div>
          </div>
          <!-- grant number -->
          <div class="form-group">
            <label class="control-label col-sm-2" for="project.grantNumber"><s:message
                code="project.edit.grantNumber" /></label>
            <div class="col-sm-10">
              <s:message code="project.edit.grantNumber.ph" var="reg_first" />
              <sf:input path="project.grantNumber" class="form-control" placeholder="${reg_first}" />
              <sf:errors path="project.grantNumber" cssClass="alert alert-danger" element="div" />
            </div>
          </div>
          <!-- Description -->
          <div class="form-group">
            <label class="control-label col-sm-2" for="project.description"><s:message
                code="project.edit.description" /></label>
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
        </div>
        <!-- End Page Projectdata -->
        <!-- Start Page Contributor -->
        <div id="workersActiveContent" class="projectContent">
          <!-- Contributors -->
          <div class="form-group">
            <div class="col-sm-12">
              <div class="well marginTop1">
                <s:message code="project.edit.metadata.info" />
              </div>
            </div>
          </div>
          <!-- Messages -->
          <c:if test="${not empty saveState && saveState != '' && not empty saveStateMsg && jQueryMap == 'contri'}">
            <div
              class="alert <c:out value="${saveState eq 'SUCCESS' ? 'alert-success' : 
                                                saveState eq 'ERROR' ? 'alert-danger' : 
                                                saveState eq 'INFO' ? 'alert-warning' : 'alert-info'}"/>"
              role="alert">
              <c:out value="${saveStateMsg}" />
            </div>
          </c:if>
          <ul class="list-group">
            <li class="list-group-item">
              <div class="row">
                <div class="col-md-6"></div>
                <div class="col-md-5"></div>
                <div class="col-md-1">
                  <sf:button type="submit" name="addContributor" class="btn btn-success btn-sm">
                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                  </sf:button>
                </div>
              </div>
            </li>
            <c:forEach items="${ProjectForm.contributors}" var="contri" varStatus="coloop">
              <li class="list-group-item">
                <div class="list-group">
                  <div class="row">
                    <div class="col-md-6"></div>
                    <div class="col-md-5"></div>
                    <div class="col-md-1">
                      <sf:button type="submit" name="deleteContributor" class="btn btn-danger btn-sm"
                        onclick="document.getElementById('delPos').value=${coloop.count-1}">
                        <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
                      </sf:button>
                    </div>
                  </div>
                </div>
                <div class="form-group">
                  <!-- Contributors title -->
                  <div class="form-group row">
                    <label for="inputKey" class="col-md-2 control-label">Titel</label>
                    <div class="col-md-4">
                      <s:message code="project.edit.title.ph" var="reg_first" />
                      <sf:input path="contributors[${coloop.count-1}].title" class="form-control"
                        placeholder="${reg_first}" />
                    </div>
                    <label for="inputKey" class="col-md-1 control-label">ORCID</label>
                    <div class="col-md-4">
                      <s:message code="project.edit.title.ph" var="reg_first" />
                      <sf:input path="contributors[${coloop.count-1}].orcid" class="form-control"
                        placeholder="${reg_first}" />
                      <sf:errors path="contributors[${coloop.count-1}].orcid" cssClass="alert alert-danger"
                        element="div" htmlEscape="false" />
                    </div>
                  </div>
                </div> <!-- Contributors first and lastname-->
                <div class="form-group">
                  <div class="form-group row">
                    <label for="inputKey" class="col-md-2 control-label">Vorname</label>
                    <div class="col-md-4">
                      <s:message code="project.edit.title.ph" var="reg_first" />
                      <sf:input path="contributors[${coloop.count-1}].firstName" class="form-control"
                        placeholder="${reg_first}" />
                    </div>
                    <label for="inputValue" class="col-md-1 control-label">Nachname</label>
                    <div class="col-md-4">
                      <s:message code="project.edit.title.ph" var="reg_first" />
                      <sf:input path="contributors[${coloop.count-1}].lastName" class="form-control"
                        placeholder="${reg_first}" />
                    </div>
                  </div>
                </div> <!-- Contributors institution-->
                <div class="form-group">
                  <div class="form-group row">
                    <label for="inputKey" class="col-md-2 control-label">Institut</label>
                    <div class="col-md-9">
                      <s:message code="project.edit.title.ph" var="reg_first" />
                      <sf:input path="contributors[${coloop.count-1}].institution" class="form-control"
                        placeholder="${reg_first}" />
                    </div>
                  </div>
                </div> <!-- Contributors department-->
                <div class="form-group">
                  <div class="form-group row">
                    <label for="inputKey" class="col-md-2 control-label">Abteilung</label>
                    <div class="col-md-9">
                      <s:message code="project.edit.title.ph" var="reg_first" />
                      <sf:input path="contributors[${coloop.count-1}].department" class="form-control"
                        placeholder="${reg_first}" />
                    </div>
                  </div>
                </div>
              </li>
            </c:forEach>
          </ul>
          <!-- Buttons -->
          <div class="form-group">
            <div class="col-sm-offset-0 col-sm-12">
              <button type="reset" class="btn btn-default">
                <s:message code="gen.reset" />
              </button>
              <sf:button type="submit" class="btn btn-success" id="worker_submit">
                <s:message code="gen.submit" />
              </sf:button>
            </div>
          </div>
        </div>
        <!-- End Page Contributor -->
      </sf:form>
      <!-- Start Page Studies -->
      <div id="studiesActiveContent" class="projectContent">
        <div class="well">
          <s:message code="project.edit.metadata.info" />
        </div>
        <c:forEach items="${ProjectForm.studies}" var="cstud">
          <a href="<c:url value="${ProjectForm.project.id}/study/${cstud.id}" />"><b>${cstud.id} -
              ${cstud.title}</b></a>
          <br />
        </c:forEach>
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et
        dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet
        clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
        consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed
        diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
        takimata sanctus est Lorem ipsum dolor sit amet.
      </div>
      <!-- End Page Studies -->
      <!-- Start Page Material -->
      <div id="materialsActiveContent" class="projectContent">
        <div class="well">
          <s:message code="project.edit.metadata.info" />
        </div>
        <!-- Messages -->
        <c:if test="${not empty saveState && saveState != '' && not empty saveStateMsg && jQueryMap == 'material'}">
          <div
            class="alert <c:out value="${saveState eq 'SUCCESS' ? 'alert-success' : 
                                                saveState eq 'ERROR' ? 'alert-danger' : 
                                                saveState eq 'INFO' ? 'alert-warning' : 'alert-info'}"/>"
            role="alert">
            <c:out value="${saveStateMsg}" />
          </div>
        </c:if>
        <div class="well">
          <input id="genDelete" type="hidden" value="<s:message code="gen.delete" />" /> <input id="maxFiles"
            type="hidden" value="<s:message code="upload.max.files.exceeded" />" /> <input id="responseError"
            type="hidden" value="<s:message code="upload.response.error" />" /> <input id="defaultMsg" type="hidden"
            value="<s:message code="upload.default.message" />" /> <input id="uploadPid" type="hidden"
            value="<s:message text="${ProjectForm.project.id}" />" />
          <c:url var="uploadUrl" value="/project/${ProjectForm.project.id}/upload" />
          <sf:form action="${uploadUrl}" class="dropzone form-horizontal" method="POST" commandName="ProjectForm"
            role="form" enctype="multipart/form-data" id="my-dropzone"></sf:form>
          <div>
            <button class="btn btn-success" id="dz-upload-button">Upload File</button>
            <button class="btn btn-danger" id="dz-reset-button">Reset Dropzone</button>
          </div>
        </div>
        <!-- Start list for downloadfiles -->
        <ul class="list-group" id="file">
          <c:forEach items="${ProjectForm.files}" var="file">
            <c:set var="ctype" value="${fn:split(file.contentType, '/')}" />
            <c:set var="namearr" value="${fn:split(file.fileName, '.')}" />
            <c:set var="basename" value="${namearr[fn:length(namearr)-1]}" />
            <li class="list-group-item">
              <!-- Image -->
              <div class="form-group row">
                <div class="col-md-2">
                  <c:choose>
                    <c:when test="${(ctype[0]=='image' || ctype[0]=='IMAGE') && basename != 'ico' && basename != 'gif'}">
                      <img style="padding-left: 14px" alt="${file.fileName}"
                        src="${ProjectForm.project.id}/img/${file.id}">
                    </c:when>
                    <c:otherwise>
                      <img alt="" src="<c:url value="/static/images/fileformat/${fn:toLowerCase(basename)}.png" />">
                    </c:otherwise>
                  </c:choose>
                </div>
                <div class="col-md-10">
                  <!-- downloadicon -->
                  <div class="row">
                    <div class="col-md-11"></div>
                    <div class="col-md-1">
                      <a class="btn btn-primary btn-sm" href="${ProjectForm.project.id}/download/${file.id}"
                        target="_blank"> <span class="glyphicon glyphicon-save-file" aria-hidden="true"></span></a>
                    </div>
                  </div>
                  <!-- filename -->
                  <div class="row">
                    <label class="control-label col-md-1" for=""><s:message code="gen.file.filename" /></label>
                    <div class="col-md-10">
                      <c:out value="${file.fileName}"></c:out>
                    </div>
                  </div>
                  <!-- filesize  and date-->
                  <div class="row">
                    <label class="control-label col-md-1" for=""><s:message code="gen.file.filesize" /></label>
                    <div class="col-md-4">
                      <c:out value="${file.fileSize}" />
                      Byte (
                      <c:choose>
                        <c:when test="${file.fileSize>(1024*1024)}">
                          <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1024/1024}" />MB
                          </c:when>
                        <c:otherwise>
                          <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1024}" />KB
                          </c:otherwise>
                      </c:choose>
                      )
                    </div>
                    <label class="control-label col-md-2" for=""><s:message code="gen.file.uploaded" /></label>
                    <div class="col-md-5">
                      <c:out value="${file.uploadDate}"></c:out>
                    </div>
                  </div>
                  <!-- checksums -->
                  <div class="row">
                    <label class="control-label col-md-1" for=""><s:message code="gen.file.checksum" /></label>
                    <div class="col-md-4">
                      <c:out value="${file.md5checksum}"></c:out>
                      <b>(<s:message code="gen.file.md5" />)
                      </b>
                    </div>
                    <div class="col-md-6">
                      <c:out value="${file.sha1Checksum}"></c:out>
                      <b>(<s:message code="gen.file.sha" />)
                      </b>
                    </div>
                    <!-- deleteicon -->
                    <div class="col-md-1">
                      <a class="btn btn-danger btn-sm" href="${ProjectForm.project.id}/delDoc/${file.id}"><span
                        class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
                    </div>
                  </div>
                </div>
              </div>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>