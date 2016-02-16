<%@ include file="includes.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<sec:csrfMetaTags />
<title>ZPID - DataWiz</title>
<link href="<c:url value='/static/css/bootstrap.css' />" rel="stylesheet"></link>
<link href="<c:url value='/static/css/dropzone.css' />" rel="stylesheet"></link>
<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
</head>
<body>
  <sec:authentication var="principal" property="principal" />
  <div id="logo">
    <img alt="" src="<c:url value="/static/images/dwlogo.png" />">
  </div>