<%@ include file="../templates/includes.jsp"%>
<ol class="breadcrumb">
  <c:forEach var="map" items="${breadcrumpList}">
    <c:choose>
      <c:when test="${empty map.uri}">
        <li class="active"><c:out value="${map.name}" /></li>
      </c:when>
      <c:otherwise>
        <li><a href="<c:url value="${map.uri}" />"><c:out value="${map.name}" /></a></li>
      </c:otherwise>
    </c:choose>
  </c:forEach>
</ol>
<%-- ${requestScope['javax.servlet.forward.servlet_path']} --%>