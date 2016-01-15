<ul class="nav nav-tabs nav-justified">
  <li role="presentation" class="<c:out value="${subnaviActive eq 'PROJECT' ? 'active' : ''}" /> "><a
    href="<c:url value="/project/${ProjectForm.project.id}" /> ">Projekt</a></li>
  <li role="presentation" class="<c:out value="${subnaviActive eq 'DMP' ? 'active' : ''}" /> "><a
    href="<c:url value="/dmp/${ProjectForm.project.id}" />">DMP</a></li>
  <li role="presentation"><a href="#">Sharing</a></li>
  <li role="presentation"><a href="#">Wissensbasis</a></li>
</ul>