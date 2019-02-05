<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
    <div class="content-container">
        <div class="content-padding">
            <div class="row">
                <div class="col-lg-9 col-md-9 col-sm-12 col-xs-12"
                     style="font-size: 16px; line-height: normal; text-align: justify;">
                    <div id="ckedit">
                        <%--<s:message code="welcome.text.main"/>--%>
                    </div>
                    <span style="display: none">
                        <span itemprop="headline">DataWiz ist ein kostenfreies Datenmanagementsystem, das dabei hilft, Forschungsdaten aufzubereiten.</span>
                        <span itemprop="headline">DataWiz is a free data management system that helps prepare research data.</span>
                    </span>
                    <script>
                        getWelcomePage('${localeCode}');
                    </script>
                    <br/>
                    <sec:authorize access="isAuthenticated()">
                        <c:if test="${principal.user.hasRole('ADMIN')}">
                            <div id="welcomeTxtBtn" class="btn btn-sm btn-info"
                                 onclick="saveWelcomePage('${localeCode}');">Save
                            </div>
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
                            <a href="https://datawizkb.leibniz-psychology.org/index.php/project-start/"
                               style="font-size: 15px;" target="_blank">
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
                                href="https://github.com/ZPID/DataWiz/" style="font-size: 15px;" target="_blank"
                                itemprop="discussionUrl"><span
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
    <span style="display:none;" id="schemo_org_creative_items">
            <span itemprop="image">https://datawiz.leibniz-psychology.org/DataWiz/static/images/microsite_img/leibniz-psychology.png</span>
			<span itemprop="keywords">data management,open science,documentation,datawiz,research,psychology,zpid,data</span>
			<span itemprop="dateCreated">Nov 01, 2015</span>
			<span itemprop="datePublished">Apr 24, 2017</span>
			<span itemprop="dateModified">Jan 04, 2019</span>
			<span itemprop="publisher" itemtype="https://schema.org/Organization" itemscope>
				<span itemprop="name">Leibniz Institute for Psychology Information</span>
				<span itemprop="address" itemtype="http://schema.org/PostalAddress" itemscope>
					<span itemprop="addressCountry">Germany</span>
					<span itemprop="addressLocality">Trier</span>
					<span itemprop="addressRegion">Rhineland-Palatinate</span>
					<span itemprop="postalCode">54296</span>
					<span itemprop="streetAddress">Universit&auml;tsring 15</span>
				</span>
				<span itemprop="email">info (at) leibniz-psychology.org</span>
				<span itemprop="faxNumber">+49 (0)651 201-2071</span>
				<span itemprop="telephone">+49 (0)651 201-2877</span>
				<span style="display:none;" itemprop="memberOf" itemtype="https://schema.org/Organization" itemscope>
					<span itemprop="name">Leibniz-Gemeinschaft</span>
					<span itemprop="address" itemtype="http://schema.org/PostalAddress" itemscope>
						<span itemprop="addressCountry">Germany</span>
						<span itemprop="addressLocality">Berlin</span>
						<span itemprop="addressRegion">Berlin</span>
						<span itemprop="postalCode">10115</span>
						<span itemprop="streetAddress">Chausseestra&szlig;e 111</span>
					</span>
					<span itemprop="email">info(at)leibniz-gemeinschaft.de</span>
					<span itemprop="faxNumber">+49 (0)30 20 60 49 - 55</span>
					<span itemprop="telephone">+49 (0)30 20 60 49 - 0</span>
				</span>
				<span itemprop="url">https://www.leibniz-psychology.org</span>
			</span>
			<span itemprop="author" itemtype="https://schema.org/Person" itemscope>
				<span itemprop="name">Ronny B&ouml;lter</span>
				<span itemprop="address" itemtype="http://schema.org/PostalAddress" itemscope>
					<span itemprop="addressCountry">Germany</span>
					<span itemprop="addressLocality">Trier</span>
					<span itemprop="addressRegion">Rhineland-Palatinate</span>
					<span itemprop="postalCode">54296</span>
					<span itemprop="streetAddress">Universit&auml;tsring 15</span>
				</span>
				<span itemprop="email">rb (at) leibniz-psychology.org</span>
				<span itemprop="telephone">+49 (0)651 201-2045</span>
				<span itemprop="sameAs">https://leibniz-psychology.org/mitarbeiter/profil-ronny-boelter/</span>
				<span itemprop="sameAs">https://orcid.org/0000-0003-2571-0933</span>
				<span itemprop="sameAs">https://github.com/RBoelter</span>
			</span>
            <span itemprop="author" itemtype="https://schema.org/Person" itemscope>
				<span itemprop="name">Martin Kerwer</span>
				<span itemprop="address" itemtype="http://schema.org/PostalAddress" itemscope>
					<span itemprop="addressCountry">Germany</span>
					<span itemprop="addressLocality">Trier</span>
					<span itemprop="addressRegion">Rhineland-Palatinate</span>
					<span itemprop="postalCode">54296</span>
					<span itemprop="streetAddress">Universit&auml;tsring 15</span>
				</span>
				<span itemprop="email">mk (at) leibniz-psychology.org</span>
				<span itemprop="telephone">+49 (0)651 201-2869</span>
                <span itemprop="sameAs">https://leibniz-psychology.org/mitarbeiter/profil-martin-kerwer/</span>
			</span>
		</span>
</div>
<sec:authorize access="isAuthenticated()">
    <c:if test="${principal.user.hasRole('ADMIN')}">
        <script>
            BalloonEditor.create(document.querySelector('#ckedit'));
        </script>
    </c:if>
</sec:authorize>
<%@ include file="templates/footer.jsp" %>