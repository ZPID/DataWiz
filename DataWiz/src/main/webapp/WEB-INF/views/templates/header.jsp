<%@ include file="includes.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<sec:csrfMetaTags />
<title><s:eval expression="@environment.getProperty('application.name')" /></title>
<link href="<c:url value='/static/css/bootstrap.css' />" rel="stylesheet"></link>
<link href="<c:url value='/static/css/dropzone.css' />" rel="stylesheet"></link>
<link rel="stylesheet" type="text/css" href="<c:url value='/static/css/font-awesome.css' />" />
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
</head>
<body>
  <sec:authentication var="principal" property="principal" />
  <div id="logo">
    <s:eval expression="@environment.getProperty('application.logo.url')" var="urlUri" />
    <img alt="" src="<c:url value="${urlUri}" />">
  </div>