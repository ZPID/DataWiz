<nav class="navbar navbar-default mainnavtop" data-spy="affix" data-offset-top="100" style="z-index: 10;">
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
        <li><c:set var="localeCode" value="${pageContext.response.locale}" /> <c:choose>
            <c:when test="${localeCode eq 'de'}">
              <div>
                <a href="<c:url value="?datawiz_locale=de" />"><img alt="" style="border: solid 1px red;"
                  src="<c:url value="/static/images/de.png" />"></a> <a href="<c:url value="?datawiz_locale=en" />"><img
                  alt="" src="<c:url value="/static/images/gb.png" />"></a>
              </div>
            </c:when>
            <c:when test="${localeCode eq 'en'}">
              <div>
                <a href="<c:url value="?datawiz_locale=de" />"><img alt=""
                  src="<c:url value="/static/images/de.png" />"></a> <a href="<c:url value="?datawiz_locale=en" />"><img
                  alt="" style="border: solid 1px red;" src="<c:url value="/static/images/gb.png" />"></a>
              </div>
            </c:when>
          </c:choose></li>
        <sec:authorize access="isAuthenticated()">
          <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
            aria-haspopup="true" aria-expanded="false"><c:out value="${principal.username}" /><span class="caret"></span></a>
            <ul class="dropdown-menu">
              <li><a href="<c:url value="/panel" />">Projekte</a></li>
              <li><a href="<c:url value="/usersettings" />">Eigene Daten</a></li>
              <!-- <li><a href="#">Placeholder</a></li> -->
              <li role="separator" class="divider"></li>
              <li><a href="<c:url value="/logout" />">Logout</a></li>
            </ul></li>
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
          <li><a href="<c:url value="/login" />">Login</a></li>
          <li><a href="<c:url value="/register" />">Register</a></li>
        </sec:authorize>
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