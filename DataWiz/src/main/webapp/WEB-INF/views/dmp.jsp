<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
  <div class="content-container">
    <%@ include file="templates/breadcrump.jsp"%>
    <%@ include file="templates/submenu.jsp"%>
    <div class="content-padding">      
      <div class="page-header">
        <h4>
          <s:message code="project.edit.headline" />
        </h4>
        <div>
          <s:message code="project.edit.info" />
        </div>
      </div>
      <ul class="nav nav-tabs">
        <li role="presentation" id="administratriveActiveClick" class="projectContentClick"><a><s:message
              code="dmp.submenu.administrative" /></a></li>
        <li role="presentation" id="researchActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.research" /></a></li>
        <li role="presentation" id="metadataActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.metadata" /></a></li>
        <li role="presentation" id="accessActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.access" /></a></li>
        <li role="presentation" id="storageActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.storage" /></a></li>
        <li role="presentation" id="organizationActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.organization" /></a></li>
        <li role="presentation" id="ethicalActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.ethical" /></a></li>
        <li role="presentation" id="costsActiveClick" class="projectContentClick"><a><s:message code="dmp.submenu.costs" /></a></li>
      </ul>      
      <div id="administratriveActiveContent" class="projectContent">
      123
      </div>      
      <div id="researchActiveContent" class="projectContent">
      456
      </div>      
      <div id="metadataActiveContent" class="projectContent">
      789
      </div>
      <div id="accessActiveContent" class="projectContent">
      1234
      </div>
      <div id="storageActiveContent" class="projectContent">
      5678
      </div>      
      <div id="organizationActiveContent" class="projectContent">
      12345
      </div>      
      <div id="ethicalActiveContent" class="projectContent">
      67890
      </div>
      <div id="costsActiveContent" class="projectContent">
      1234567890
      </div>


    </div>
  </div>
</div>
<%@ include file="templates/footer.jsp"%>