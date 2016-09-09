<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <div class="page-header">
        <h4>
          <s:message code="project.studies.headline" />
        </h4>
        <div>
          <s:message code="project.studies.info" />
        </div>
      </div>
      <!-- Messages -->
      <%@ include file="templates/message.jsp"%>
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
                        src="<c:url value='img/${file.id}' />">
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
                      <a class="btn btn-primary btn-sm" href="<c:url value='download/${file.id}' />"
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
                      <a class="btn btn-danger btn-sm" href="<c:url value='delDoc/${file.id}' />"><span
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
<%@ include file="templates/footer.jsp"%>
