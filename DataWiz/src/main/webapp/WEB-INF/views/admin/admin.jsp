<%@ include file="../templates/header.jsp" %>
<%@ include file="../templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="../templates/breadcrump.jsp" %>
    <div class="content-padding">
      <div class="row">
        <div class="col-lg-4 col-md-6 col-sm-6 col-xs-12">
          <h3>Statistiken</h3>
          <ul class="list-group">
            <li class="list-group-item">
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">Anzahl Nutzer:</div>
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2" style="text-align: left;">
                  <strong><s:message text="${userCount}"/></strong>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4" style="text-align: right;">
                  <a href="<c:url value="/admin/list/user" />">Anzeigen</a>
                </div>
              </div>
            </li>
            <li class="list-group-item">
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">Anzahl Projekte:</div>
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2" style="text-align: left;">
                  <strong><s:message text="${projectCount}"/></strong>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4" style="text-align: right;">
                  <a href="<c:url value="/admin/list/project" />">Anzeigen</a>
                </div>
              </div>
            </li>
            <li class="list-group-item">
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">Anzahl Studien:</div>
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2" style="text-align: left;">
                  <strong><s:message text="${studyCount}"/></strong>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4" style="text-align: right;">

                </div>
              </div>
            </li>
            <li class="list-group-item">
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">Anzahl Datensätze (Versionen):</div>
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2" style="text-align: left;">
                  <strong><s:message text="${recordCount} (${versionCount})"/></strong>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4" style="text-align: right;">

                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="../templates/footer.jsp" %>