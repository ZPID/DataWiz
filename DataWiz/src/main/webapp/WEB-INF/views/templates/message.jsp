<sf:errors element="div" class="alert alert-danger" role="alert" htmlEscape="false" />
<c:if test="${not empty infoMSG}">
  <div class="alert alert-info" role="info">
    <s:message text="${infoMSG}" />
  </div>
</c:if>
<c:if test="${not empty errorMSG}">
  <div class="alert alert-danger" role="alert">
    <s:message text="${errorMSG}" />
  </div>
</c:if>
<c:if test="${not empty warnMSG}">
  <div class="alert alert-warning" role="warning">
    <s:message text="${warnMSG}" />
  </div>
</c:if>
<c:if test="${not empty successMSG}">
  <div class="alert alert-success" role="success">
    <s:message text="${successMSG}" />
  </div>
</c:if>
<c:if test="${not empty saveState && saveState != '' && not empty saveStateMsg}">
  <div
    class="alert <c:out value="${saveState eq 'SUCCESS' ? 'alert-success' : 
                                                saveState eq 'ERROR' ? 'alert-danger' : 
                                                saveState eq 'INFO' ? 'alert-warning' : 'alert-info'}"/>"
    role="alert">
    <c:out value="${saveStateMsg}" />
  </div>
</c:if>