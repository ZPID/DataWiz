<div class="nav-wrapper">
  <nav class="navbar navbar-default mainnavtop" data-spy="affix" data-offset-top="300" style="z-index: 10;"
    id="dwmainnavbar">
    <div class="container-fluid">
      <!-- Brand and toggle get grouped for better mobile display -->
      <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
          data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
          <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span
            class="icon-bar"></span>
        </button>
        <!-- <a class="navbar-brand" href="#">DataWiz</a> -->
      </div>
      <!-- Collect the nav links, forms, and other content for toggling -->
      <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="breadcrumb">
          <c:forEach var="map" items="${breadcrumpList}">
            <c:choose>
              <c:when test="${empty map.uri}">
                <li class="bractive"><s:message text="${map.name}" /></li>
              </c:when>
              <c:otherwise>
                <li><a href="<c:url value="${map.uri}" />"><s:message text="${map.name}" /></a></li>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </ul>
        <ul class="nav navbar-nav navbar-right">
          <sec:authorize access="isAuthenticated()">
            <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
              aria-haspopup="true" aria-expanded="false" id="dropdown_username"><c:out value="${principal.username}" /><span
                class="caret"></span></a>
              <ul class="dropdown-menu">
                <li><a href="<c:url value="/panel" />"><s:message code="navbar.sub.projects" /></a></li>
                <li><a href="<c:url value="/usersettings" />"><s:message code="navbar.sub.userdata" /></a></li>
                <!-- <li><a href="#">Placeholder</a></li> -->
                <li role="separator" class="divider"></li>
                <li><a href="<c:url value="/logout" />"><s:message code="navbar.sub.logout" /></a></li>
              </ul></li>
          </sec:authorize>
          <sec:authorize access="isAnonymous()">
            <li><a href="<c:url value="/login" />"><s:message code="navbar.main.login" /></a></li>
            <li><a href="<c:url value="/register" />"><s:message code="navbar.main.register" /></a></li>
          </sec:authorize>
          <%--           <li><div style="position: relative; top: 10px;">
              <c:set var="localeCode" value="${pageContext.response.locale}" />
              <c:choose>
                <c:when test="${localeCode eq 'de'}">
                  <div>
                    <a href="<c:url value="?datawiz_locale=de" />"><img alt="german" height="30px" style="border: solid 1px #ddd"
                      src="<c:url value="/static/images/Germany.png" />"></a> <a href="<c:url value="?datawiz_locale=en" />"><img alt="english"
                      height="30px" src="<c:url value="/static/images/USA.png" />"></a>
                  </div>
                </c:when>
                <c:when test="${localeCode eq 'en'}">
                  <div>
                    <a href="<c:url value="?datawiz_locale=de" />"><img alt="german" height="30px"
                      src="<c:url value="/static/images/Germany.png" />"></a> <a href="<c:url value="?datawiz_locale=en" />"><img alt="english"
                      height="30px" style="border: solid 1px white;" src="<c:url value="/static/images/USA.png" />"></a>
                  </div>
                </c:when>
              </c:choose>
            </div></li> --%>
          <c:if test="${isBetaVersion}">
            <li>
              <div style="position: absolute; left: 100%; top: -20px; padding-left: 20px;">
                <a
                  href="https://docs.google.com/forms/d/e/1FAIpQLSdRx1aiehMr7iUhUjRiZyueuWpDSPE3JURDSGCWtI1H8Ucghg/viewform?c=0&w=1"
                  target="_blank"><img alt="Logo" src="<c:url value='/static/images/feedback.png' />" height="100px"></a>
              </div>
            </li>
          </c:if>
          <!--         <li>
          <form class="navbar-form" role="search">
            <div class="form-group">
              <input type="text" class="form-control" placeholder="Search">
            </div>
            <button type="submit" class="btn btn-default">Submit</button>
          </form>
        </li> -->
        </ul>
      </div>
      <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
  </nav>
</div>