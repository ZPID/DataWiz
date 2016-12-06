<%@ include file="../templates/includes.jsp"%>
<thead>
  <tr>
    <c:choose>
      <c:when test="${selectedType eq 'CSV'}">
        <td colspan="5"><s:message code="dataset.import.imported.data" /></td>
        <td colspan="8"><s:message code="dataset.import.extended.data" /></td>
      </c:when>
      <c:otherwise>
        <td colspan="12"><s:message code="dataset.import.imported.data" /></td>
        <td colspan="1"><s:message code="dataset.import.extended.data" /></td>
      </c:otherwise>
    </c:choose>
  </tr>
  <tr>
    <th><s:message code="dataset.import.report.codebook.position" /></th>
    <th><s:message code="dataset.import.report.codebook.name" /></th>
    <th><s:message code="dataset.import.report.codebook.type" /></th>
    <th><s:message code="dataset.import.report.codebook.width" /></th>
    <th><s:message code="dataset.import.report.codebook.dec" /></th>
    <th><s:message code="dataset.import.report.codebook.label" /></th>
    <th><s:message code="dataset.import.report.codebook.values" /></th>
    <th><s:message code="dataset.import.report.codebook.missings" /></th>
    <th><s:message code="dataset.import.report.codebook.cols" /></th>
    <th><s:message code="dataset.import.report.codebook.aligment" /></th>
    <th><s:message code="dataset.import.report.codebook.measureLevel" /></th>
    <th><s:message code="dataset.import.report.codebook.role" /></th>
    <th><s:message code="dataset.import.report.codebook.userAtt" /></th>
  </tr>
</thead>