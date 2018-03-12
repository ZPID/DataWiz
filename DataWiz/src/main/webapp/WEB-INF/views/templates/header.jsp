<%@ include file="includes.jsp"%>
<!DOCTYPE html>
<c:set var="localeCode" value="${pageContext.response.locale}" />
<c:choose>
  <c:when test="${localeCode eq 'en'}">
    <html lang="en">
  </c:when>
  <c:when test="${localeCode eq 'de'}">
    <html lang="de">
  </c:when>
</c:choose>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<sec:csrfMetaTags />
<title><s:eval expression="@environment.getProperty('application.name')" /></title>
<s:eval expression="@environment.getProperty('external.microsite.connection')" var="loadMicrositeContent" />
<link href="<c:url value='/static/css/theme.min.css' />" rel="stylesheet" />
<link href="<c:url value='/static/css/dropzone.css' />" rel="stylesheet" />
<link href="<c:url value='/static/css/font-awesome.css' />" rel="stylesheet" type="text/css" />
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet" />
<%-- <link href="<c:url value='/static/css/microsite_css/fonts.css' />" rel="stylesheet" /> --%>
<%-- <link href="<c:url value='/static/css/microsite_css/microsite.css' />" rel="stylesheet" /> --%>
<c:if test="${loadMicrositeContent}">
  <link href="http://136.199.85.65/css/fonts" rel="stylesheet" type="text/css" />
  <link href="http://136.199.85.65/css/microsite?app=datawiz&bootstrap=0" rel="stylesheet" type="text/css" />
</c:if>
<link href="http://cdn.datatables.net/1.10.16/css/dataTables.bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="<c:url value='/static/js/datepicker/css/bootstrap-datepicker.min.css' />" rel="stylesheet" />
<s:eval expression="@environment.getProperty('application.favicon.url')" var="faviconUri" />
<link rel="icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
<link rel="shortcut icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
<s:eval expression="@environment.getProperty('datawiz.beta')" var="isBetaVersion" />
<script src="<c:url value='/static/js/jquery-2.2.0.min.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/bootstrap.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/bootstrap-dialog.min.js' />" type="text/javascript"></script>
<%-- <script src="<c:url value='/static/js/sniperwolf-taggingJS.js' />" type="text/javascript"></script> --%>
<script src="<c:url value='/static/js/dropzone.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/jquery-sortable.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/datepicker/js/bootstrap-datepicker.min.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/app.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/modalform.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/dwfilter.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/jquery.dataTables.min.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/dataTables.bootstrap.min.js' />" type="text/javascript"></script>
</head>
<body>
  <c:choose>
    <c:when test="${isBetaVersion}">
      <s:eval expression="@environment.getProperty('application.logo.url.beta')" var="logoUri" />
    </c:when>
    <c:otherwise>
      <s:eval expression="@environment.getProperty('application.logo.url')" var="logoUri" />
    </c:otherwise>
  </c:choose>
  <sec:authentication var="principal" property="principal" />
  <div class="loader"></div>
  <c:choose>
    <c:when test="${loadMicrositeContent}">
      <c:catch var="catchException">
        <c:import url="http://136.199.85.65/header/?app=datawiz&locale=${localeCode}&iframe=0&bootstrap=0" />
      </c:catch>
      <c:if test="${catchException != null}">
        <%@ include file="header_microsite.jsp"%>
      </c:if>
    </c:when>
    <c:otherwise><%@ include file="header_microsite.jsp"%></c:otherwise>
  </c:choose>