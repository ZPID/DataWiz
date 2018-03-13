<div id="lp_ms_header">
  <div class="lp_ms_header_row" id="lp_ms_header_1">
    <div class="" id="lp_ms_header_1_col_left">
      <div id="lp_ms_header_1_lp_logo"></div>
    </div>
    <div class="" id="lp_ms_header_1_col_right">
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
            <li id="lp_ms_header_1_contact"><a href="mailto://datawiz@zpid.de">Contact</a></li>
            <li><a href="${locale_url_de}">DE</a></li>
            <li><a href="${locale_url_en}" class="active">EN</a></li>
          </c:when>
        </c:choose>
      </ul>
    </div>
  </div>
  <div class="lp_ms_header_2">
    <div class="lp_ms_header_row" id="lp_ms_header_2">
      <div id="lp_ms_header_2_image">
        <div id="lp_ms_header_2_logo">
          <div id="lp_ms_header_2_logo_img">
            <a href="/DataWiz/"><span id="lp_ms_header_2_logo_bold">Data</span><span id="lp_ms_header_2_logo_thin">Wiz</span></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
