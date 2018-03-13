<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <div class="content-padding">
      <div class="row">
        <div class="col-lg-9 col-md-9 col-sm-12 col-xs-12"
          style="font-size: 20px; line-height: normal; text-align: justify;">
          <p>DataWiz ist ein Assistenzsystem, das Wissenschaftlern und Wissenschaftlerinnen helfen soll, ihre Daten
            von Forschungsbeginn an fachgerecht aufzubereiten und zu dokumentieren. Das Tool hilft dabei,
            Datenmanagementpläne zu erstellen, eigene Forschungsdaten zu verwalten und es ermöglicht auch, Daten im Team
            zu teilen und später der Fachgemeinschaft zur Nachnutzung zur Verfügung zu stellen. DataWiz ist eine
            kostenfreie Open-Science-Webanwendung.</p>
          <div style="width: 100%; text-align: center; margin-top: 90px" class="col-lg-12">
            <c:url value="/static/images/microsite_img/" var="img_psych" />
            <img alt="Forschungskreis" src="${img_psych}Kreisgrafik.png" style="width: 100%; max-width: 531px">
          </div>
        </div>
        <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
          <ul class="list-group" style="border: 1px solid #352071" id="lp_mp_sub_menu">
            <c:url var="panelUrl" value="/panel" />
            <li class="list-group-item" style="border: 1px solid #352071"><a href="${panelUrl}"
              style="font-size: 15px;"><span class="glyphicon glyphicon-chevron-right" aria-hidden="true"
                style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
                Projektübersicht</a>
              <div style="margin-top: 5px">Setzen Sie Ihre Arbeit an einem bestehenden Projekt fort oder legen Sie
                ein neues Projekt an.</div></li>
            <li class="list-group-item" style="border: 1px solid #352071"><a href="https://datawiz.zpid.de/"
              style="font-size: 15px;" target="_blank"><span class="glyphicon glyphicon-chevron-right"
                aria-hidden="true"
                style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
                Wissensbasis</a>
              <div style="margin-top: 5px">Link zur DataWiz Knowledge Base. Bitte beachten Sie, dass die Inhalte
                ständig erweitert und aktualisiert werden.</div></li>
            <li class="list-group-item" style="border: 1px solid #352071"><a
              href="https://github.com/Leibniz-Psychology/DataWiz/" style="font-size: 15px;" target="_blank"><span
                class="glyphicon glyphicon-chevron-right" aria-hidden="true"
                style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
                DataWiz GitHub Repositorium</a>
              <div style="margin-top: 5px">
                <b>(In Arbeit)</b> Link zum Quellcode von DataWiz. Dieser steht zur Einsicht, oder eigenen
                Verwendung/Erweiterung frei zur Verfügung.
              </div></li>

          </ul>
          <c:if test="${loadMicrositeContent}">
            <div style="background-color: #0093be; padding: 30px 40px 30px 40px; color: white">
              <h4>Ansprechpartner</h4>
              <p>
                <span class="darkblue">Forschungsdatenzentrum für die Psychologie</span><br> Universitätsring 15,<br>
                54296 Trier
              </p>
              <p>
                <b>Inhaltlich:</b><br />PD Dr. Erich Weichselgartner<br />Bereichsleiter Archivierungs- und
                Veröffentlichungsdienste<br /> <span class="glyphicon glyphicon-earphone" aria-hidden="true"></span> <a
                  style="color: white; text-decoration: none;" href="callto:+49 (0)651 201-2056">+49 (0) 651
                  201-2056</a><br />
              </p>
              <p>
                <b>Technisch:</b><br />M.Sc. Ronny B&ouml;lter<br /> <span class="glyphicon glyphicon-earphone"
                  aria-hidden="true"></span> <a style="color: white; text-decoration: none;"
                  href="callto:+49 (0)651 201-2045">+49 (0) 651 201-2045</a><br />
              </p>
              <p>
                <span class="glyphicon glyphicon-envelope" aria-hidden="true"></span> <a
                  style="color: white; text-decoration: none;" href="mailto:datawiz@leibniz-psychology.de"
                  title="E-Mail"> datawiz(at)leibniz-psychology.org</a><br /> <span class="glyphicon glyphicon-home"
                  aria-hidden="true"></span> <a style="color: white; text-decoration: none;"
                  href="http://www.leibniz-psychology.de/" target="blanc" title="www.leibniz-psychology.de">
                  www.leibniz-psychology.de</a>
              </p>
            </div>
          </c:if>
        </div>
      </div>
      <div class="row">
        <div class="col-lg-9 col-md-9 col-sm-12 col-xs-12"></div>
        <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12"></div>
      </div>
    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>