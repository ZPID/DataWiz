<%@ include file="../templates/includes.jsp"%>
<td><strong><s:message text="${currVAR.name}" /></strong></td>
<td><s:message code="spss.type.${currVAR.type}" /></td>
<td><s:message text="${currVAR.width}" /></td>
<td><s:message text="${currVAR.decimals}" /></td>
<td><s:message text="${currVAR.label}" /></td>
<td><c:forEach items="${currVAR.values}" var="val">
    <div>
      <s:message text="${val.value}&nbsp;=&nbsp;&quot;${val.label}&quot;" />
    </div>
  </c:forEach></td>
<td><c:choose>
    <c:when test="${currVAR.missingFormat eq 'SPSS_ONE_MISSVAL'}">
      <s:message text="${currVAR.missingVal1}" />
    </c:when>
    <c:when test="${currVAR.missingFormat eq 'SPSS_TWO_MISSVAL'}">
      <s:message text="${currVAR.missingVal1},&nbsp;${currVAR.missingVal2}" />
    </c:when>
    <c:when test="${currVAR.missingFormat eq 'SPSS_THREE_MISSVAL'}">
      <s:message text="${currVAR.missingVal1},&nbsp;${currVAR.missingVal2},&nbsp;${currVAR.missingVal3}" />
    </c:when>
    <c:when test="${currVAR.missingFormat eq 'SPSS_MISS_RANGE'}">
      <s:message text="${currVAR.missingVal1}&nbsp;-&nbsp;${currVAR.missingVal2}" />
    </c:when>
    <c:when test="${currVAR.missingFormat eq 'SPSS_MISS_RANGEANDVAL'}">
      <s:message text="${currVAR.missingVal1}&nbsp;-&nbsp;${currVAR.missingVal2},&nbsp;${currVAR.missingVal3}" />
    </c:when>
  </c:choose></td>
<c:choose>
  <c:when test="${selectedType eq 'CSV' && currVAR.id==0}">
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </c:when>
  <c:otherwise>
    <td><s:message text="${currVAR.columns}" /></td>
    <td><s:message code="spss.aligment.${currVAR.aligment}" /></td>
    <td><s:message code="spss.measureLevel.${currVAR.measureLevel}" /></td>
    <td><s:message code="spss.role.${currVAR.role}" /></td>
  </c:otherwise>
</c:choose>
<td>
  <table class="table" style="margin: 0px; padding: 0px; background-color: rgba(0, 0, 0, 0.0) !important;">
    <thead>
      <tr>
        <c:forEach items="${currVAR.attributes}" var="att">
          <th><s:message text="[${att.label}]" /></th>
        </c:forEach>
      </tr>
    </thead>
    <tbody>
      <tr>
        <c:forEach items="${currVAR.attributes}" var="att">
          <td><s:message text="${att.value}" /></td>
        </c:forEach>
      </tr>
    </tbody>
  </table>
</td>
<td><c:forEach items="${currVAR.dw_attributes}" var="att">
    <c:if test="${att.label eq 'dw_construct'}">
      <s:message text="${att.value}" />
    </c:if>
  </c:forEach></td>
<td><c:forEach items="${currVAR.dw_attributes}" var="att">
    <c:if test="${att.label eq 'dw_measocc'}">
      <s:message text="${att.value}" />
    </c:if>
  </c:forEach></td>
<td><c:forEach items="${currVAR.dw_attributes}" var="att">
    <c:if test="${att.label eq 'dw_instrument'}">
      <s:message text="${att.value}" />
    </c:if>
  </c:forEach></td>
<td><c:forEach items="${currVAR.dw_attributes}" var="att">
    <c:if test="${att.label eq 'dw_itemtext'}">
      <s:message text="${att.value}" />
    </c:if>
  </c:forEach></td>
<td><c:forEach items="${currVAR.dw_attributes}" var="att">
    <c:if test="${att.label eq 'dw_filtervar'}">
      <s:message text="${att.value}" />
    </c:if>
  </c:forEach></td>