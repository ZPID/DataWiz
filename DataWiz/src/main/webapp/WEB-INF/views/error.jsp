<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
    <div class="content-container">
        <div class="content-padding">
            <div class="page-header">
                <div class="row">
                    <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <strong><s:message text="${exceptionTitle}" htmlEscape="false"/></strong>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> <span
                            class="sr-only"></span>
                        <s:message text="${errormsg}" htmlEscape="false"/>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="alert alert-info">
                        <strong>Exception: </strong><s:message text="${exception}" htmlEscape="false"/>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 text-align-right">
                    <c:choose>
                        <c:when test="${closePageBTN}">
                            <button class="btn btn-info btn-sm" onclick="window.close()">
                                <s:message code="gen.close"/>
                            </button>
                        </c:when>
                        <c:when test="${not empty referrerURL}">
                            <a href="${referrerURL}" class="btn btn-info btn-sm">
                                <s:message code="back.to.panel"/>
                            </a>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="templates/footer.jsp" %>