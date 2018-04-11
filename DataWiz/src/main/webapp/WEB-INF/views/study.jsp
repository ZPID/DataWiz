<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<c:set var="valimag1" value="zpid.png" scope="session" />
<c:set var="valimag2" value="check-mark-20px.png" scope="session" />
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">
      <c:choose>
        <c:when test="${empty StudyForm.study.id}">
          <c:set var="allowEdit" value="true" />
          <s:message code="study.create.basis.headline" var="headline_head" />
          <s:message code="study.create.basis.info" var="headline_info" />
          <%@ include file="templates/pages_headline.jsp"%>
        </c:when>
        <c:otherwise>
          <div class="row text-align-right btn-line">
            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
              <c:set var="allowEdit" value="false" />
              <c:if
                test="${principal.user.hasRole('PROJECT_ADMIN', StudyForm.project.id, false) or
                        principal.user.hasRole('PROJECT_WRITER', StudyForm.project.id, false) or 
                        principal.user.hasRole('ADMIN') or 
                        principal.user.hasRole('DS_WRITER', StudyForm.study.id, true)}">
                <c:set var="allowEdit" value="true" />
                <c:url var="accessUrl"
                  value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/switchEditMode" />
                <c:choose>
                  <c:when test="${empty disStudyContent || disStudyContent eq 'disabled' }">
                    <a href="${accessUrl}" class="btn btn-success btn-sm"><s:message code="study.button.check.in" /></a>
                  </c:when>
                  <c:otherwise>
                    <a href="${accessUrl}" class="btn btn-warning btn-sm"><s:message code="study.button.check.out" /></a>
                  </c:otherwise>
                </c:choose>
                <c:if
                  test="${principal.user.hasRole('PROJECT_ADMIN', StudyForm.project.id, false) or
                        principal.user.hasRole('ADMIN')}">
                  <button class="btn btn-warning btn-sm" data-toggle="modal" data-target="#duplicateModal">
                    <s:message code="study.duplicate.btn" />
                  </button>
                  <button type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#deleteModal">
                    <s:message code="study.button.delete.study" />
                  </button>
                </c:if>
              </c:if>
              <div class="dropdown" style="display: inline-block">
                <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown"
                  role="button" aria-haspopup="true" aria-expanded="false">
                  <s:message code="study.export.btn.txt" />
                  <span class="caret"></span>
                </button>
                <ul class="dropdown-menu dropdown-menu-right">
                  <li><a href="<c:url value="${StudyForm.study.id}/exportStudy/PsychData" />" target="_blank"><span
                      class="glyphicon glyphicon-download" aria-hidden="true"></span>
                    <s:message code="study.export.pd.txt" /></a></li>
                  <li><a href="<c:url value="${StudyForm.study.id}/exportStudy/PreReg" />" target="_blank"><span
                      class="glyphicon glyphicon-download" aria-hidden="true"></span>
                    <s:message code="study.export.rr.txt" /></a></li>
                </ul>
              </div>
            </div>
          </div>
          <s:message code="study.edit.basis.headline" var="headline_head" arguments="${StudyForm.study.title}" />
          <s:message code="study.edit.basis.info" var="headline_info" />
          <%@ include file="templates/pages_headline.jsp"%>
        </c:otherwise>
      </c:choose>

      <!-- Submenu -->
      <c:if test="${!hideMenu}">
        <ul class="nav nav-tabs subnavtop" data-spy="affix" data-offset-top="400">
          <li role="presentation" id="basisDataActiveClick" class="studyContentClick"><a><s:message
                code="study.submenu.basic.data" /></a></li>
          <c:if test="${not empty StudyForm.study.id}">
            <li role="presentation" id="designActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.design" /></a></li>
            <li role="presentation" id="sampleActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.sample" /></a></li>
            <li role="presentation" id="surveyActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.survey" /></a></li>
            <li role="presentation" id="ethicalActiveClick" class="studyContentClick"><a><s:message
                  code="study.submenu.ethical" /></a></li>
          </c:if>
        </ul>
      </c:if>
      <c:url var="accessUrl" value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}" />
      <sf:form action="${accessUrl}" modelAttribute="StudyForm" class="form-horizontal" id="studyFormDis">
        <c:choose>
          <c:when test="${allowEdit}">
            <input type="hidden" id="disStudyContent" value="${disStudyContent}" />
          </c:when>
          <c:otherwise>
            <input type="hidden" id="disStudyContent" value="disabled" />
          </c:otherwise>
        </c:choose>
        <sf:hidden path="jQueryMap" />
        <sf:hidden path="delPos" />
        <sf:hidden path="scrollPosition" />
        <!-- Messages -->
        <%@ include file="templates/message.jsp"%>
        <!-- START General Data Content -->
        <jsp:include page="forms/study_general.jsp" />
        <!-- START Design Data Content -->
        <jsp:include page="forms/study_design.jsp" />
        <!-- START Sample Characteristics Content -->
        <jsp:include page="forms/study_sample.jsp" />
        <!-- START Survey Content -->
        <jsp:include page="forms/study_survey.jsp">
          <jsp:param value="${localeCode}" name="localeCode" />
        </jsp:include>
        <!-- START Ethical Content -->
        <jsp:include page="forms/study_ethical.jsp" />
        <!-- Buttons -->
        <hr />
        <c:if test="${allowEdit}">
          <div class="row">
            <div class="col-xs-6 text-align-left">
              <button type="reset" class="btn btn-default btn-sm">
                <s:message code="gen.reset" />
              </button>
            </div>
            <div class="col-xs-6 text-align-right">
              <sf:button type="submit" class="btn btn-success btn-sm">
                <s:message code="gen.submit" />
              </sf:button>
            </div>
          </div>
        </c:if>
      </sf:form>
    </div>
  </div>
</div>
<!-- Delete Modal -->
<div id="deleteModal" class="modal fade" role="dialog">
  <s:message code="study.delete.phrase" var="deletePhrase" />
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header ">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">
          <s:message code="study.delete.modal.head" arguments="${StudyForm.study.title}" />
        </h4>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <div class="col-sm-12">
            <div class="well marginTop1">
              <s:message code="study.delete.modal.info" />
            </div>
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-12">
            <label><s:message code="study.delete.modal.label" arguments="${deletePhrase}" /></label>
          </div>
          <div class="col-sm-12">
            <input class="form-control" type="text" id="deleteInputTXT" required="required" />
            <div class="alert alert-danger" style="display: none;" id="deleteAlert">
              <s:message code="study.delete.modal.error" />
            </div>
          </div>
        </div>
        <div class="row"></div>
      </div>
      <div class="modal-footer">
        <c:url var="accessUrl" value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/deleteStudy" />
        <a href="${accessUrl}" class="btn btn-warning btn-sm" id="deleteBTN"
          onclick="return checkDeletePhrase('${deletePhrase}')"><s:message code="study.delete.modal.final.del" /></a>
      </div>
    </div>
  </div>
</div>
<!-- duplicateModal -->
<div id="duplicateModal" class="modal fade" role="dialog">
  <s:message code="study.delete.phrase" var="deletePhrase" />
  <div class="modal-dialog">
    <!-- Modal content-->
    <c:url var="accessUrl" value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/duplicate" />
    <form action="${accessUrl}" class="form-horizontal" method="get">
      <div class="modal-content panel-primary">
        <div class="modal-header panel-heading">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">
            <s:message code="study.duplicate.title" arguments="${StudyForm.study.title}" />
          </h4>
        </div>
        <div class="modal-body">
          <div class="well">
            <s:message code="study.duplicate.modal.info" />
          </div>
          <select name="selected" class="form-control">
            <c:forEach items="${ProjectList}" var="itm">
              <c:choose>
                <c:when test="${itm.id eq StudyForm.project.id}">
                  <option value="${itm.id}" selected="selected">${itm.title}</option>
                </c:when>
                <c:otherwise>
                  <option value="${itm.id}">${itm.title}</option>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </select>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-warning btn-sm">
            <s:message code="study.duplicate.btn" />
          </button>
        </div>
      </div>
    </form>
  </div>
</div>

<%@ include file="templates/footer.jsp"%>