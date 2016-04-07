<sf:errors element="div" class="alert alert-danger" role="alert" htmlEscape="false" />
<c:if test="${not empty errorMSG}">
  <div class="alert alert-danger" role="alert">
    <s:message text="${errorMSG}" />
  </div>
</c:if>
<c:if test="${not empty infoMSG}">
  <div class="alert alert-info" role="alert">
    <s:message text="${infoMSG}" />
  </div>
</c:if>