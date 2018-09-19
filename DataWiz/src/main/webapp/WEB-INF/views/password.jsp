<%@ include file="templates/header.jsp" %>
<%@ include file="templates/navbar.jsp" %>
<div id="mainWrapper">
    <div class="content-container">
        <%@ include file="templates/breadcrump.jsp" %>
        <div class="content-padding">
            <div class="page-header">
                <h4>
                    <s:message code="reset.password.head"/>
                </h4>
            </div>
            <c:url var="loginUrl" value="/login/passwordrequest"/>
            <sf:form action="${loginUrl}" modelAttribute="UserDTO" class="form-horizontal" method="post">
                <div class="row" style="margin-top: 30px">
                    <div class="col-lg-offset-4 col-lg-4 col-md-offset-3 col-md-6 col-sd-offset-2 col-sd-8 col-xs-offset-1 col-xs-10">
                        <%@ include file="templates/message.jsp" %>
                        <c:choose>
                            <c:when test="${setemailview}">
                                <c:if test="${empty sendSuccess}">
                                    <div class="alert alert-info">
                                        <s:message code="reset.password.email"/>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <s:message code="gen.email.ph" var="reg_mail"/>
                                            <sf:input path="email" class="form-control" placeholder="${reg_mail}"
                                                      type="email"/>
                                            <sf:errors path="email" cssClass="alert alert-danger" element="div"
                                                       htmlEscape="false"/>
                                        </div>
                                    </div>
                                    <div class="alert alert-warning">
                                        <div class="row">
                                            <div class="col-sm-12">
                                                <div class="checkbox">
                                                    <sf:label path="secEmail">
                                                        <sf:checkbox path="secEmail" value="second"/>
                                                        <s:message code="reset.password.send.to.second"/>
                                                    </sf:label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-sm-12 text-align-right">
                                            <button type="submit" class="btn btn-success btn-sm">
                                                <s:message code="reset.password.submit"/>
                                            </button>
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="form-group">
                                    <div class="col-sm-12">
                                        <s:message code="gen.password.ph" var="reg_password"/>
                                        <sf:input path="password" class="form-control" placeholder="${reg_password}"
                                                  type="password"
                                                  required="required"/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-sm-12">
                                        <s:message code="gen.password.retype.ph" var="reg_password_retype"/>
                                        <sf:input path="password_retyped" class="form-control"
                                                  placeholder="${reg_password_retype}"
                                                  type="password" required="required"/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-sm-12 text-align-right">
                                        <button type="submit" class="btn btn-success btn-sm" name="setPassword">
                                            <s:message code="gen.submit"/>
                                        </button>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </sf:form>
        </div>
    </div>
</div>
<%@ include file="templates/footer.jsp" %>