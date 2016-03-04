<ul class="nav nav-tabs nav-justified">
  <li role="presentation" class="<c:out value="${subnaviActive eq 'PROJECT' ? 'active' : ''}" /> "><a
    href="<c:url value="/project/${ProjectForm.project.id}" /> "><s:message code="submenu.project" /></a></li>
  <li role="presentation" class="<c:out value="${subnaviActive eq 'DMP' ? 'active' : ''}" /> "><a
    href="<c:url value="/dmp/${ProjectForm.project.id}" />"><s:message code="submenu.dmp" /></a></li>
  <c:if test="${not empty ProjectForm.project.id}">
    <li role="presentation" class="<c:out value="${subnaviActive eq 'ACCESS' ? 'active' : ''}" /> "><a
      href="<c:url value="/access/${ProjectForm.project.id}" />"><s:message code="submenu.sharing" /></a></li>
  </c:if>
  <li role="presentation"><a href="#"><s:message code="submenu.export" /></a></li>
  <li role="presentation"><a href="#"><s:message code="submenu.knowlegdebase" /></a></li>
</ul>