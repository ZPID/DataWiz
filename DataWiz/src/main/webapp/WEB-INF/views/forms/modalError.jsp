<%@ include file="../templates/includes.jsp"%>
<c:url var="accessUrl" value="/project/${modalPid}/study/${modalStudyId}/record/${modalRecordId}/codebook" />
<div class="modal-content panel-primary">
  <div class="modal-header panel-heading">
    <button type="button" class="close" data-dismiss="modal">&times;</button>
    <h4 class="modal-title">Error</h4>
  </div>
  <div class="modal-body">Something went terribly wrong</div>
  <div class="modal-footer">
    <div class="form-group">
      <div class="col-sm-offset-0 col-md-12">
        <a href="${accessUrl}" type="submit" class="btn btn-success"> close </a>
      </div>
    </div>
  </div>
</div>
