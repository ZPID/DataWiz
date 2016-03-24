<sf:errors element="div" class="alert alert-danger" role="alert" htmlEscape="false" />
<c:if test="${not empty errorMSG}">
  <div class="alert alert-danger" role="alert">
    <c:out value="${errorMSG}" />
  </div>
</c:if>
<c:if test="${not empty infoMSG}">
  <div class="alert alert-info" role="alert">
    <c:out value="${infoMSG}" />
  </div>
</c:if>