<%@ include file="../templates/includes.jsp"%>
<div id="storageActiveContent" class="projectContent">
  <!-- Infotxt -->
  <div class="form-group">
    <div class="col-sm-12">
      <div class="well marginTop1">
        <s:message code="dmp.edit.storage.info" />
      </div>
    </div>
  </div>
  <ul class="list-group">
    <!-- storageResponsible -->
    <li class="list-group-item">
       <s:message text="storageResponsible" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- storageTechnologies -->
    <li class="list-group-item">
       <s:message text="storageTechnologies" var="dmp_var_name" /> 
       <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- storagePlaces -->
    <li class="list-group-item">
      <s:message text="storagePlaces" var="dmp_var_name" /> 
       <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- storageBackups -->
    <li class="list-group-item">
      <s:message text="storageBackups" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- storageTransfer -->
    <li class="list-group-item">
      <s:message text="storageTransfer" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- storageExpectedSize -->
    <li class="list-group-item">
      <s:message text="storageExpectedSize" var="dmp_var_name" /> 
      <%@ include file="../templates/textarea.jsp"%>
    </li>
    <!-- storageExpectedSize -->
    <li class="list-group-item">
      <s:message text="storageRequirements" var="dmp_var_name" />
      <s:message text="1" var="dmp_explain_at" />  
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
    <!-- storageSuccession -->
    <li class="list-group-item">
      <s:message text="storageSuccession" var="dmp_var_name" />
      <s:message text="1" var="dmp_explain_at" />  
      <%@ include file="../templates/selectYesNoWithReason.jsp"%>
    </li>
  </ul>
</div>