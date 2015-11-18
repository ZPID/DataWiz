<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
        data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span
          class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">DataWiz</a>
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav">
        <li class="active"><a href="#">Link <span class="sr-only">(current)</span></a></li>
        <li><a href="login">Link</a></li>
        <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
          aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="#">Action</a></li>
            <li><a href="#">Another action</a></li>
            <li><a href="#">Something else here</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">Separated link</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">One more separated link</a></li>
          </ul></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li><c:set var="localeCode" value="${pageContext.response.locale}" /> <c:choose>
            <c:when test="${localeCode eq 'de'}">
              <a href="?datawiz_locale=en"><img alt="" src="static/images/de.png"></a>
            </c:when>
            <c:when test="${localeCode eq 'en'}">
              <a href="?datawiz_locale=de"><img alt="" src="static/images/gb.png"></a>
            </c:when>
          </c:choose></li>
        <sec:authorize access="isFullyAuthenticated()">
          <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
            aria-haspopup="true" aria-expanded="false">${principal.username}<span class="caret"></span></a>
            <ul class="dropdown-menu">
              <li><a href="#">Projekte</a></li>
              <li><a href="#">Eigene Daten</a></li>
              <li><a href="#">Placeholder</a></li>
              <li role="separator" class="divider"></li>
              <li><a href="logout">Logout</a></li>
            </ul></li>
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
          <li><a href="login">Login</a></li>
          <li><a href="register">Register</a></li>
        </sec:authorize>
        <li>
          <form class="navbar-form" role="search">
            <div class="form-group">
              <input type="text" class="form-control" placeholder="Search">
            </div>
            <button type="submit" class="btn btn-default">Submit</button>
          </form>
        </li>
      </ul>
    </div>
    <!-- /.navbar-collapse -->
  </div>
  <!-- /.container-fluid -->
</nav>