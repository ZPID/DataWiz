<%@ include file="../templates/header.jsp" %>
<%@ include file="../templates/navbar.jsp" %>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="../templates/breadcrump.jsp" %>
    <div>
      <%@ include file="../templates/message.jsp" %>
      <c:choose>
        <c:when test="${tabletype eq 'user'}">
          <table class="table table-striped">
            <thead>
            <tr>
              <th>ID</th>
              <th>Status</th>
              <th><s:message code="gen.title"/></th>
              <th><s:message code="gen.firstName"/></th>
              <th><s:message code="gen.lastName"/></th>
              <th><s:message code="gen.email"/></th>
              <th><s:message code="gen.institution"/></th>
              <th><s:message code="gen.city"/></th>
              <th><s:message code="gen.country"/></th>
              <th><s:message code="gen.orcid"/></th>
              <th>regDate</th>
              <th>lastLogin</th>
              <th>Projekte</th>
              <th>Edit</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${userlist}" var="user" varStatus="count">
              <tr>
                <td><strong><s:message text="${user.id}"/></strong></td>
                <td><strong><s:message text="${user.accountState}"/></strong></td>
                <td><s:message text="${user.title}"/></td>
                <td><s:message text="${user.firstName}"/></td>
                <td><s:message text="${user.lastName}"/></td>
                <td>
                  <c:if test="${not empty user.email}">
                    <a href="mailto:<s:message text="${user.email}" />" target="_blank"><s:message text="${user.email}"/></a>
                  </c:if>
                </td>
                <td><s:message text="${user.institution}"/></td>
                <td><s:message text="${user.city}"/></td>
                <td><s:message text="${user.country}"/></td>
                <td>
                  <c:if test="${not empty user.orcid}">
                    <a href="https://orcid.org/<s:message text="${user.orcid}" />" target="_blank"><s:message text="${user.orcid}"/></a>
                  </c:if>
                </td>
                <td>
                  <s:message text="${user.regDate}"/>
                </td>
                <td>
                  <s:message text="${user.lastLogin}"/>
                </td>
                <td style="text-align: right;">
                  <a href='<c:url value="/admin/list/project/${user.id}" />'><span class="glyphicon glyphicon-search" aria-hidden="true"></span></a>
                </td>
                <td style="text-align: right;">
                  <a href="javascript:void(0)" onclick="openDetailModal('user', ${user.id});">
                    <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                  </a>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
          <script>
              $('.table').DataTable();
          </script>
        </c:when>
        <c:when test="${tabletype eq 'project'}">
          <table class="table table-striped">
            <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Ersteller</th>
              <th>Abstract</th>
              <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${projectlist}" var="project" varStatus="count">
              <tr>
                <td><strong><s:message text="${project.id}"/></strong></td>
                <td><strong><s:message text="${project.title}"/></strong></td>
                <td><c:forEach items="${userlist}" var="user">
                  <c:if test="${user.id eq project.ownerId }">
                    <strong><s:message text="${user.email}"/></strong>
                  </c:if>
                </c:forEach></td>
                <td><s:message text="${project.description}"/></td>
                <td style="text-align: center;"><a
                    href='<c:url value="/project/${project.id}/" />'><span
                    class="glyphicon glyphicon-edit" aria-hidden="true"></span></a></td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
          <script>
              $('.table').DataTable();
          </script>
        </c:when>
      </c:choose>
    </div>
  </div>
</div>
<div id="detailModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <form action="<c:url value="/admin/save/user" />" class="form-horizontal" method="post" id="userSubmitForm" autocomplete="off">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" id="modal_uid" name="modal_uid"/>
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Modal Header</h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="modal_title" class="col-sm-2 control-label"><s:message code="gen.title"/></label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="modal_title" name="modal_title"/>
            </div>
          </div>
          <div class="form-group">
            <label for="modal_first_name" class="col-sm-2 control-label"><s:message code="gen.firstName"/></label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="modal_first_name" name="modal_first_name"/>
            </div>
          </div>
          <div class="form-group">
            <label for="modal_last_name" class="col-sm-2 control-label"><s:message code="gen.lastName"/></label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="modal_last_name" name="modal_last_name"/>
            </div>
          </div>
          <div class="form-group">
            <label for="modal_email" class="col-sm-2 control-label"><s:message code="gen.email"/></label>
            <div class="col-sm-10">
              <input type="email" class="form-control" id="modal_email" name="modal_email"/>
            </div>
          </div>
          <div class="form-group">
            <label for="modal_sec_email" class="col-sm-2 control-label"><s:message code="gen.secEmail"/></label>
            <div class="col-sm-10">
              <input type="email" class="form-control" id="modal_sec_email" name="modal_sec_email"/>
            </div>
          </div>
          <div class="form-group">
            <label for="modal_password" class="col-sm-2 control-label"><s:message code="gen.password"/></label>
            <div class="col-sm-10">
              <input type="password" class="form-control" id="modal_password" name="modal_password" autocomplete="off"/>
            </div>
          </div>
          <div class="form-group">
            <label for="modal_account_state" class="col-sm-2 control-label">Status</label>
            <div class="col-sm-10">
              <select class="form-control" id="modal_account_state" name="modal_account_state">
                <option value="ACTIVE">ACTIVE</option>
                <option value="EXPIRED">EXPIRED</option>
                <option value="LOCKED">LOCKED</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6" style="text-align: left;">
              <button type="submit" name="deleteUser" class="btn btn-sm btn-danger">
                <s:message code="gen.delete"/>
              </button>
            </div>
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
              <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">
                <s:message code="gen.close"/>
              </button>
              <button type="submit" name="saveUser" class="btn btn-sm btn-primary">
                <s:message code="gen.submit"/>
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>
<script>
    function openDetailModal(type, id) {
        $.ajax({
            url: '../detail/' + type + '/' + id,
            type: 'get',
            data: {},
            success: function(response) {
                $('#modal_uid').val(response[0].id);
                $('#modal_title').val(response[0].title);
                $('#modal_first_name').val(response[0].firstName);
                $('#modal_last_name').val(response[0].lastName);
                $('#modal_email').val(response[0].email);
                $('#modal_sec_email').val(response[0].secEmail);
                $('#modal_password').val(response[0].password);
                $('#modal_account_state').val(response[0].account_state);
                // Display Modal
                $('#detailModal').modal('show');
            },
            error: function(response) {
                console.log(response);
            }
        });
    }
</script>
<%@ include file="../templates/footer.jsp" %>