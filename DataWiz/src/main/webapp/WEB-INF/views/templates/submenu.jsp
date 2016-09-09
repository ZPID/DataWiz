<c:if test="${!hideMenu}">
  <c:choose>
    <c:when test="${studySubMenu}">
      <ul class="nav nav-tabs nav-justified">
        <li role="presentation" class="<c:out value="${subnaviActive eq 'STUDY' ? 'active' : ''}" /> "><a
          href="<c:url value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}" /> "><s:message
              code="submenu.studydoc" /></a></li>
        <c:if test="${not empty StudyForm.project.id}">
          <li role="presentation" class="<c:out value="${subnaviActive eq 'DATA' ? 'active' : ''}" /> "><a
            href="<c:url value="/project/${StudyForm.project.id}" />"><s:message code="submenu.record" /></a></li>
          <li role="presentation" class="<c:out value="${subnaviActive eq 'STUDMATERIAL' ? 'active' : ''}" /> "><a
            href="<c:url value="/project/${StudyForm.project.id}" />"><s:message code="submenu.studymaterial" /></a></li>
        </c:if>
      </ul>
    </c:when>
    <c:otherwise>
      <ul class="nav nav-tabs nav-justified">
        <li role="presentation" class="<c:out value="${subnaviActive eq 'PROJECT' ? 'active' : ''}" /> "><a
          href="<c:url value="/project/${ProjectForm.project.id}" /> "><s:message code="submenu.project" /></a></li>
        <li role="presentation" class="<c:out value="${subnaviActive eq 'DMP' ? 'active' : ''}" /> "><a
          href="<c:url value="/dmp/${ProjectForm.project.id}" />"><s:message code="submenu.dmp" /></a></li>
        <c:if test="${not empty ProjectForm.project.id}">
          <li role="presentation" class="<c:out value="${subnaviActive eq 'STUDIES' ? 'active' : ''}" /> "><a
            href="<c:url value="/project/${ProjectForm.project.id}/studies" /> "><s:message
                code="project.submenu.studies" /></a></li>
          <li role="presentation" class="<c:out value="${subnaviActive eq 'MATERIAL' ? 'active' : ''}" /> "><a
            href="<c:url value="/project/${ProjectForm.project.id}/material" /> "><s:message
                code="project.submenu.material" /></a></li>
          <li role="presentation" class="<c:out value="${subnaviActive eq 'ACCESS' ? 'active' : ''}" /> "><a
            href="<c:url value="/access/${ProjectForm.project.id}" />"><s:message code="submenu.sharing" /></a></li>
        </c:if>
        <li role="presentation" class="<c:out value="${subnaviActive eq 'EXPORT' ? 'active' : ''}" /> "><a
          href="<c:url value="/export/${ProjectForm.project.id}" />"><s:message code="submenu.export" /></a></li>
        <%--         <li role="presentation"><a href="#"><s:message code="submenu.knowlegdebase" /></a></li> --%>
      </ul>
    </c:otherwise>
  </c:choose>
</c:if>