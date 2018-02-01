<div id="lp_ms_header">
  <div class="row lp_ms_header_row" id="lp_ms_header_1">
    <div class="col-lg-8 col-md-7 col-sm-6 col-xs-12" id="lp_ms_header_1_col_left">
      <div onclick="window.open('https://leibniz-psychology.org','_blank','')" id="lp_ms_header_1_lp_logo"></div>
    </div>
    <div class="col-lg-4 col-md-5 col-sm-6 col-xs-12" id="lp_ms_header_1_col_right">
      <c:set var="localeCode" value="${pageContext.response.locale}" />
      <c:url value="?datawiz_locale=de" var="locale_url_de" />
      <c:url value="?datawiz_locale=en" var="locale_url_en" />
      <ul>
        <c:choose>
          <c:when test="${localeCode eq 'de'}">
            <li id="lp_ms_header_1_contact"><a href="mailto://datawiz@zpid.de">Kontakt</a></li>
            <li><a href="${locale_url_de}" class="active">DE</a></li>
            <li><a href="${locale_url_en}">EN</a></li>
          </c:when>
          <c:when test="${localeCode eq 'en'}">
            <li id="lp_ms_header_1_contact"><a href="mailto://datawiz@zpid.de">Kontakt</a></li>
            <li><a href="${locale_url_de}">DE</a></li>
            <li><a href="${locale_url_en}" class="active">EN</a></li>
          </c:when>
        </c:choose>
      </ul>
    </div>
  </div>
  <div id="lp_ms_header_2">
    <div class="row lp_ms_header_row">
      <div id="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        <div id="lp_ms_header_2_image">
          <div id="lp_ms_header_2_logo">
            <!-- <a href=" title="DataPsych"><img src="img/datapsych.png" alt="DataPsych"></a> -->
            <a href="/DataWiz/"><span id="lp_ms_header_2_logo_bold">Data</span><span id="lp_ms_header_2_logo_thin">Wiz</span></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>