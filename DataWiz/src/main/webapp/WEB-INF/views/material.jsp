<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="loadstate" style="display: none;">
    <div class="well" id="loadstatebar">
        <div id="loadstateloading" style="display: none;">
            <s:message code="material.upload.loading"/>
        </div>
        <div id="loadstateworking">
            <s:message code="material.upload.pending"/>
        </div>
        <div class="progress">
            <div class="progress-bar" role="progressbar" aria-valuenow="70" aria-valuemin="0" aria-valuemax="100"></div>
        </div>
    </div>
</div>
<div id="mainWrapper">
    <div class="content-container">
        <%@ include file="templates/breadcrump.jsp" %>
        <%@ include file="templates/submenu.jsp" %>
        <div class="content-padding">
            <div class="page-header">
                <s:message code="project.material.headline" var="headline_head"/>
                <s:message code="project.material.info" var="headline_info"/>
                <%@ include file="templates/pages_headline.jsp" %>
            </div>
            <!-- Messages -->
            <%@ include file="templates/message.jsp" %>
            <c:set var="allowEdit" value="false"/>
            <c:if test="${principal.user.hasRole('PROJECT_ADMIN', ProjectForm.project.id, false) or
                principal.user.hasRole('PROJECT_WRITER', ProjectForm.project.id, false) or
                principal.user.hasRole('ADMIN') or
                (not empty studyId && studyId > 0 && principal.user.hasRole('DS_WRITER', studyId, true))}">
                <c:set var="allowEdit" value="true"/>
                <div class="well">
                    <input id="genDelete" type="hidden" value="<s:message code="gen.delete" />"/>
                    <input id="maxFiles" type="hidden" value="<s:message code="upload.max.files.exceeded" />"/>
                    <input id="responseError" type="hidden" value="<s:message code="upload.response.error" />"/>
                    <input id="defaultMsg" type="hidden" value="<s:message code="upload.default.message" />"/>
                    <input id="uploadPid" type="hidden" value="<s:message text="${projectId}" />"/>
                    <c:choose>
                        <c:when test="${not empty studyId && studyId > 0}">
                            <c:url var="uploadUrl" value="/project/${projectId}/study/${studyId}/upload"/>
                        </c:when>
                        <c:otherwise>
                            <c:url var="uploadUrl" value="/project/${projectId}/upload"/>
                        </c:otherwise>
                    </c:choose>
                    <div class="form-group">
                            <%--@elvariable id="ProjectForm" type="de.zpid.datawiz.form.ProjectForm"--%>
                        <sf:form action="${uploadUrl}" class="dropzone form-horizontal" method="POST" modelAttribute="ProjectForm"
                                 role="form" enctype="multipart/form-data" id="my-dropzone"></sf:form>
                    </div>
                        <%--<div class="row">
                            <div class="col-xs-6">
                                <button class="btn btn-danger btn-sm" id="dz-reset-button">
                                    <s:message code="material.dropzone.reset.btn"/>
                                </button>
                            </div>
                            <div class="col-xs-6 text-align-right">
                                <button class="btn btn-success btn-sm" id="dz-upload-button">
                                    <s:message code="material.dropzone.upload.btn"/>
                                </button>
                            </div>
                        </div>--%>
                </div>
            </c:if>
            <!-- Start list for downloadfiles -->
            <ul class="list-group" id="file">
                <c:forEach items="${ProjectForm.files}" var="file">
                    <c:set var="ctype" value="${fn:split(file.contentType, '/')}"/>
                    <c:set var="namearr" value="${fn:split(file.fileName, '.')}"/>
                    <c:set var="basename" value="${namearr[fn:length(namearr)-1]}"/>
                    <li class="list-group-item">
                        <!-- Image -->
                        <div class="row">
                            <div class="col-md-4 col-xs-12" style="text-align: center;">
                                <a href="<c:url value='download/${file.id}' />" target="_blank">
                                    <c:choose>
                                        <c:when test="${(ctype[0]=='image' || ctype[0]=='IMAGE') && basename != 'ico' && basename != 'gif'}">
                                            <img style="max-width: 100%;" alt="${file.fileName}"
                                                 src="<c:url value='img/${file.id}' />"
                                                 onerror="this.src='<c:url value="/static/images/fileformat/_blank.png"/>'"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img alt="${fn:toLowerCase(basename)}"
                                                 src="<c:url value="/static/images/fileformat/${fn:toLowerCase(basename)}.png" />"
                                                 onerror="this.src='<c:url value="/static/images/fileformat/_blank.png"/>'"/>
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                            </div>
                            <div class="col-md-8 col-xs-12">
                                <!-- filename -->
                                <div class="row">
                                    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-2 text-nowrap"><s:message code="gen.file.filename"/></label>
                                    <div class="col-xs-12 col-sm-12 col-md-9 col-lg-10 text-align-left">
                                        <s:message text="${file.fileName}"/>
                                    </div>
                                </div>
                                <!-- filesize  and date-->
                                <div class="row marginTop1">
                                    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-2"><s:message code="gen.file.filesize"/></label>
                                    <div class="col-xs-12 col-sm-12 col-md-9 col-lg-10">
                                        <c:out value="${file.fileSize}"/>
                                        Byte (
                                        <c:choose>
                                            <c:when test="${file.fileSize>(1024*1024)}">
                                                <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1024/1024}"/>MB
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber type="number" maxFractionDigits="2" value="${file.fileSize/1024}"/>KB
                                            </c:otherwise>
                                        </c:choose>
                                        )
                                    </div>
                                </div>
                                <div class="row marginTop1">
                                    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-2"><s:message code="gen.file.uploaded"/></label>
                                    <div class="col-xs-12 col-sm-12 col-md-9 col-lg-10">
                                        <javatime:format value="${file.uploadDate}" style="MS" var="strDate"/>
                                        <s:message text="${strDate}"/>
                                    </div>
                                </div>
                                <!-- checksums -->
                                <div class="row marginTop1">
                                    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-2"><s:message code="gen.file.checksum"/></label>
                                    <div class="col-xs-12 col-sm-12 col-md-9 col-lg-8">
                                        <s:message text="${file.md5checksum}"/> <b>(<s:message code="gen.file.md5"/>) </b>
                                    </div>
                                </div>
                                <div class="row">
                                    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-2"></label>
                                    <div class="col-xs-12 col-sm-12 col-md-9 col-lg-8">
                                        <s:message text="${file.sha1Checksum}"/> <b>(<s:message code="gen.file.sha"/>) </b>
                                    </div>
                                </div>
                                <div class="row marginTop1">
                                    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-2"><s:message code="study.records.description"/></label>
                                    <div class="col-xs-12 col-sm-12 col-md-9 col-lg-10">
                                        <c:choose>
                                            <c:when test="${allowEdit}">
                                                <textarea id="desc_${file.id}" class="form-control">${file.description}</textarea>
                                                <div class="btn btn-sm btn-info save-description marginTop1" onclick="saveMatDesc('${file.id}');"><s:message
                                                        code="material.save.desc"/></div>
                                                <a class="btn btn-danger btn-sm marginTop1 pull-right" data-toggle="tooltip" title="Delete Document"
                                                   href="<c:url value='delDoc/${file.id}' />">
                                                    <s:message code="material.file.delete"/></a>
                                            </c:when>
                                            <c:otherwise>
                                                <s:message text="${file.description}"/>
                                            </c:otherwise>
                                        </c:choose>
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
<script src="<c:url value='/static/js/dropzone_init.js' />" type="text/javascript"></script>
<%@ include file="templates/footer.jsp" %>
