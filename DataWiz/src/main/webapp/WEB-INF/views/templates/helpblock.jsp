<c:if test="${not empty appresmess}">
  <div class="row help-block">
    <div class="col-sm-1 glyphicon glyphicon-info-sign gylph-help"></div>
    <div class="col-sm-11">
      <c:out value="${appresmess}"></c:out>
    </div>
  </div>
</c:if>