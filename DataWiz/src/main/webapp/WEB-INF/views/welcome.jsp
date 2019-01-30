<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <div class="content-padding">
      <div class="row">
        <div class="col-lg-9 col-md-9 col-sm-12 col-xs-12" style="font-size: 16px; line-height: normal; text-align: justify;">
          <div id="ckedit">
            <%--<s:message code="welcome.text.main"/>--%>
          </div>
          <script>
              getWelcomePage('${localeCode}');
          </script>
          <br/>
          <sec:authorize access="isAuthenticated()">
            <c:if test="${principal.user.hasRole('ADMIN')}">
              <div id="welcomeTxtBtn" class="btn btn-sm btn-info" onclick="saveWelcomePage('${localeCode}');">Save</div>
            </c:if>
          </sec:authorize>
        </div>
        <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
          <ul class="list-group" style="border: 1px solid #352071" id="lp_mp_sub_menu">
            <c:url var="panelUrl" value="/panel"/>
            <li class="list-group-item" style="border: 1px solid #352071">
              <a href="${panelUrl}" style="font-size: 15px;">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"
                      style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
                <s:message code="welcome.sidemenu.first.link.text"/>
              </a>
              <div style="margin-top: 5px">
                <s:message code="welcome.sidemenu.first.link.desc"/>
              </div>
            </li>
            <li class="list-group-item" style="border: 1px solid #352071">
              <a href="https://datawizkb.leibniz-psychology.org/index.php/project-start/" style="font-size: 15px;" target="_blank">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"
                      style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
                <s:message code="welcome.sidemenu.second.link.text"/>
              </a>
              <div style="margin-top: 5px">
                <s:message code="welcome.sidemenu.second.link.desc"/>
              </div>
            </li>
            <li class="list-group-item" style="border: 1px solid #352071"><a
                href="https://datawizkb.leibniz-psychology.org/" style="font-size: 15px;"
                target="_blank"><span
                class="glyphicon glyphicon-chevron-right" aria-hidden="true"
                style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
              <s:message code="welcome.sidemenu.third.link.text"/></a>
              <div style="margin-top: 5px">
                <s:message code="welcome.sidemenu.third.link.desc"/>
              </div>
            </li>
            <li class="list-group-item" style="border: 1px solid #352071"><a
                href="https://github.com/ZPID/DataWiz/" style="font-size: 15px;" target="_blank"><span
                class="glyphicon glyphicon-chevron-right" aria-hidden="true"
                style="font-size: 10px; background-color: #352071; border-radius: 10px; color: white; padding: 3px; top: -1px"></span>
              <s:message code="welcome.sidemenu.fourth.link.text"/></a>
              <div style="margin-top: 5px">
                <s:message code="welcome.sidemenu.fourth.link.desc"/>
              </div>
            </li>
          </ul>
          <div style="background-color: #0093be; padding: 30px 40px 30px 40px; color: white">
            <s:message code="welcome.contact.html"/>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-lg-9 col-md-9 col-sm-12 col-xs-12"></div>
        <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12"></div>
      </div>
    </div>
  </div>
</div>
<sec:authorize access="isAuthenticated()">
  <c:if test="${principal.user.hasRole('ADMIN')}">
    <script>
        BalloonEditor.create(document.querySelector('#ckedit'));
    </script>
  </c:if>
</sec:authorize>
<%@ include file="templates/footer.jsp" %>