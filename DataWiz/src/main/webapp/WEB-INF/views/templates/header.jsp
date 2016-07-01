<%@ include file="includes.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<sec:csrfMetaTags />
<title><s:eval expression="@environment.getProperty('application.name')" /></title>
<link href="<c:url value='/static/css/bootstrap.css' />" rel="stylesheet"></link>
<link href="<c:url value='/static/css/dropzone.css' />" rel="stylesheet"></link>
<link href="<c:url value='/static/css/font-awesome.css' />" rel="stylesheet" type="text/css" />
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
<link href="<c:url value='/static/js/datepicker/css/bootstrap-datepicker.min.css' />" rel="stylesheet"></link>
<s:eval expression="@environment.getProperty('application.favicon.url')" var="faviconUri" />
<link rel="icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
<link rel="shortcut icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
</head>
<body>
  <sec:authentication var="principal" property="principal" />
  <div class="loader"></div>
  <div id="logo">
    <s:eval expression="@environment.getProperty('application.logo.url')" var="logoUri" />
    <img alt="" src="<c:url value="${logoUri}" />">
  </div>