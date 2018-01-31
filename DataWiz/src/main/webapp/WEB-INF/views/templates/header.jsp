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
<link href="<c:url value='/static/css/theme.min.css' />" rel="stylesheet" />
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
  <header>
    <div class="header-bg" style="height: 301.5px;"></div>
    <div class="all-wrap">
      <div id="topline">
        <div class="header-content clearfix">
          <div class="topline-left">
            <c:url value="/static/images/microsite_img/leibniz-psychology.png" var="header_psych" />
            <a href="#"><img src="${header_psych}" alt="Leibniz Psychology"></a>
          </div>
          <div class="topline-right clearfix">
            <div class="topline-nav">
              <a href="mailto://datawiz@zpid.de">Kontakt</a>
            </div>
            <div class="topline-lang-nav">
              <c:set var="localeCode" value="${pageContext.response.locale}" />
              <c:url value="?datawiz_locale=de" var="locale_url_de" />
              <c:url value="?datawiz_locale=en" var="locale_url_en" />
              <ul>
                <c:choose>
                  <c:when test="${localeCode eq 'de'}">
                    <li><a href="${locale_url_de}" class="active">DE</a></li>
                    <li><a href="${locale_url_en}">EN</a></li>
                  </c:when>
                  <c:when test="${localeCode eq 'en'}">
                    <li><a href="${locale_url_de}">DE</a></li>
                    <li><a href="${locale_url_en}" class="active">EN</a></li>
                  </c:when>
                </c:choose>
              </ul>
            </div>
            <div class="search-button search-button-mob">
              <a class="open-search"></a>
            </div>
            <div class="mob-nav-icon"></div>
          </div>
        </div>
      </div>
      <div id="header">
        <div class="header-content clearfix">
          <div id="header-pic-logo" class="clearfix">
            <div class="header-pic">
              <c:url value="/static/images/microsite_img/header_cyan.jpg" var="header_cyan" />
              <img src="${header_cyan}" alt="DataWiz">
            </div>
            <div id="logo">
              <!--<a href="#" title="DataPsych"><img src="img/datapsych.png" alt="DataPsych"></a>-->
              <a href="#" title="DataPsych"><span class="lg lg-pt-1">Data</span><span class="lg lg-pt-2">Wiz</span></a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>