<%@ include file="../templates/includes.jsp"%>
<thead>
  <tr>
    <c:choose>
      <c:when test="${selectedType eq 'CSV'}">
        <td colspan="5"><s:message code="dataset.import.imported.data" /></td>
        <td colspan="13"><s:message code="dataset.import.extended.data" /></td>
      </c:when>
      <c:otherwise>
        <td colspan="13"><s:message code="dataset.import.imported.data" /></td>
        <td colspan="5"><s:message code="dataset.import.extended.data" /></td>
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
    <th><s:message code="dataset.import.report.codebook.construct" /></th>
    <th><s:message code="dataset.import.report.codebook.measocc" /></th>
    <th><s:message code="dataset.import.report.codebook.instrument" /></th>
    <th><s:message code="dataset.import.report.codebook.itemtext" /></th>
    <th><s:message code="dataset.import.report.codebook.filtervar" /></th>
  </tr>
</thead>