<c:choose>
  <c:when test="${empty ms_footer_content}">
    <%@ include file="footer_microsite.jsp"%>
  </c:when>
  <c:otherwise>
    ${ms_footer_content}
  </c:otherwise>
</c:choose>
<div class="dwgoup"></div>
<script src="<c:url value='/static/js/app.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/modalform.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/dwfilter.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/importReport.js' />" type="text/javascript"></script>
</body>
</html>