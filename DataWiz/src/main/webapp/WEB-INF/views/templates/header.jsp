<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<c:set var="localeCode" value="${pageContext.response.locale}"/>
<html lang="${localeCode}">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <sec:csrfMetaTags/>
  <title><s:eval expression="@environment.getProperty('application.name')"/></title>
  <s:eval expression="@environment.getProperty('external.microsite.connection')" var="loadMicrositeContent"/>
  <s:eval expression="@environment.getProperty('google.recaptcha.enabled')" var="captcha_enabled"/>
  <s:eval expression="@environment.getProperty('google.recaptcha.key.site')" var="captcha_site"/>
  <link href="<c:url value='/static/css/theme.min.css' />" rel="stylesheet"/>
  <link href="<c:url value='/static/css/dropzone.css' />" rel="stylesheet"/>
  <link href="<c:url value='/static/css/font-awesome.css' />" rel="stylesheet" type="text/css"/>
  <link href="<c:url value='/static/css/app.css' />" rel="stylesheet"/>
  <link href="<c:url value='/static/css/sidemenu.css' />" rel="stylesheet"/>
  <c:choose>
    <c:when test="${loadMicrositeContent}">
      <c:catch var="catchException">
        <c:import url="http://136.199.85.65/header/?app=datawiz&locale=${localeCode}&iframe=0&bootstrap=0"
                  var="ms_header_content"/>
        <c:import url="http://136.199.85.65/footer/?app=datawiz&locale=${localeCode}&iframe=0&bootstrap=0"
                  var="ms_footer_content"/>
        <link href="http://136.199.85.65/css/fonts" rel="stylesheet"/>
        <link href="http://136.199.85.65/css/microsite?app=datawiz&amp;bootstrap=0" rel="stylesheet"/>
      </c:catch>
      <c:if test="${catchException != null}">
        <link href="<c:url value='/static/css/microsite_css/fonts.css' />" rel="stylesheet"/>
        <link href="<c:url value='/static/css/microsite_css/microsite.css' />" rel="stylesheet"/>
      </c:if>
    </c:when>
    <c:otherwise>
      <link href="<c:url value='/static/css/microsite_css/fonts.css' />" rel="stylesheet"/>
      <link href="<c:url value='/static/css/microsite_css/microsite.css' />" rel="stylesheet"/>
    </c:otherwise>
  </c:choose>
  <link href="<c:url value='/static/css/dataTables.bootstrap.min.css' />" rel="stylesheet"/>
  <link href="<c:url value='/static/js/datepicker/css/bootstrap-datepicker.min.css' />" rel="stylesheet"/>
  <s:eval expression="@environment.getProperty('application.favicon.url')" var="faviconUri"/>
  <link rel="icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
  <link rel="shortcut icon" href="<c:url value="${faviconUri}" />" type="image/x-icon">
  <s:eval expression="@environment.getProperty('datawiz.beta')" var="isBetaVersion"/>
  <script src="<c:url value='/static/js/jquery-2.2.0.min.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/bootstrap.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/bootstrap-dialog.min.js' />" type="text/javascript"></script>
  <%-- <script src="<c:url value='/static/js/sniperwolf-taggingJS.js' />" type="text/javascript"></script> --%>
  <script src="<c:url value='/static/js/dropzone.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/jquery-sortable.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/datepicker/js/bootstrap-datepicker.min.js' />" type="text/javascript"></script>
  <script src="https://cdn.ckeditor.com/ckeditor5/11.1.1/balloon/ckeditor.js"></script>
  <script src="<c:url value='/static/js/app.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/modalform.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/dwfilter.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/sidemenu.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/dataTables.min.js' />" type="text/javascript"></script>
  <script src="<c:url value='/static/js/dataTables.bootstrap.min.js' />" type="text/javascript"></script>
  <c:if test="${captcha_enabled}">
    <script src='https://www.google.com/recaptcha/api.js'></script>
  </c:if>

  <script type="text/javascript">
      var _paq = _paq || [];
      _paq.push(["setDoNotTrack", true]);
      _paq.push(['trackPageView']);
      _paq.push(['enableLinkTracking']);
      (function() {
          var u = "//pwk.clubs-project.eu/";
          _paq.push(['setTrackerUrl', u + 'log.php']);
          _paq.push(['setSiteId', '8']);
          var d = document, g = d.createElement('script'), s = d.getElementsByTagName('script')[0];
          g.type = 'text/javascript';
          g.async = true;
          g.defer = true;
          g.src = u + 'log.js';
          s.parentNode.insertBefore(g, s);
      })();
  </script>
</head>
<body>
<c:choose>
  <c:when test="${isBetaVersion}">
    <s:eval expression="@environment.getProperty('application.logo.url.beta')" var="logoUri"/>
  </c:when>
  <c:otherwise>
    <s:eval expression="@environment.getProperty('application.logo.url')" var="logoUri"/>
  </c:otherwise>
</c:choose>
<sec:authentication var="principal" property="principal"/>
<!-- <div class="loader"></div> -->
<c:choose>
  <c:when test="${empty ms_header_content}">
    <%@ include file="header_microsite.jsp" %>
  </c:when>
  <c:otherwise>
    ${ms_header_content}
  </c:otherwise>
</c:choose>