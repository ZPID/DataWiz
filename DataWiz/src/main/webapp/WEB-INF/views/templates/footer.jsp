<footer class="footer">
  <div class="container">
    <p class="text-muted">
      <%
        out.println("bufferSize: " + out.getBufferSize() + " remaining: " + out.getRemaining() + " used: "
            + (out.getBufferSize() - out.getRemaining()) + " autoFlush: " + out.isAutoFlush());
      %>
    </p>
  </div>
</footer>
<script src="<c:url value='/static/js/jquery-2.2.0.min.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/bootstrap.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/bootstrap-dialog.min.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/sniperwolf-taggingJS.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/dropzone.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/jquery-sortable.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/datepicker/js/bootstrap-datepicker.min.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/app.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/modalform.js' />" type="text/javascript"></script>
<script src="<c:url value='/static/js/dwfilter.js' />" type="text/javascript"></script>
</body>
</html>