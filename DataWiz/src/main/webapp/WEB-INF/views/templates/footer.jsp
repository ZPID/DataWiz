<c:choose>
  <c:when test="${loadMicrositeContent}">
    <c:catch var="catchException">
      <c:import url="http://136.199.85.65/footer/?app=datawiz&locale=${localeCode}&iframe=0&bootstrap=0" />
    </c:catch>
    <c:if test="${catchException != null}">
      <%@ include file="footer_microsite.jsp"%>
    </c:if>
  </c:when>
  <c:otherwise><%@ include file="footer_microsite.jsp"%></c:otherwise>
</c:choose>
<div class="dwgoup"></div>
<script src="<c:url value='/static/js/app.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/modalform.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/dwfilter.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/importReport.js' />" type="text/javascript"></script>
</body>
</html>