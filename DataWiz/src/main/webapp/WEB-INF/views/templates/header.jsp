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
<link href="<c:url value='/static/css/microsite_css/fonts.css' />" rel="stylesheet" />
<link href="<c:url value='/static/css/microsite_css/style.css' />" rel="stylesheet" />
<link href="<c:url value='/static/css/bootstrap.css' />" rel="stylesheet" />
<link href="<c:url value='/static/css/dropzone.css' />" rel="stylesheet" />
<link href="<c:url value='/static/css/font-awesome.css' />" rel="stylesheet" type="text/css" />
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet" />
<link href="<c:url value='/static/js/DataTables/datatables.min.css' />" rel="stylesheet" type="text/css" />
<link href="<c:url value='/static/js/datepicker/css/bootstrap-datepicker.min.css' />" rel="stylesheet" />
<s:eval expression="@environment.getProperty('application.favicon.url')" var="faviconUri" />
<link rel="icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
<link rel="shortcut icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
<s:eval expression="@environment.getProperty('datawiz.beta')" var="isBetaVersion" />
</head>
<body>
  <sec:authentication var="principal" property="principal" />
  <div class="loader"></div>
  <div id="logo">
    <c:choose>
      <c:when test="${isBetaVersion}">
        <s:eval expression="@environment.getProperty('application.logo.url.beta')" var="logoUri" />
      </c:when>
      <c:otherwise>
        <s:eval expression="@environment.getProperty('application.logo.url')" var="logoUri" />
      </c:otherwise>
    </c:choose>
    <img alt="Logo" src="<c:url value="${logoUri}" />">    
  </div>