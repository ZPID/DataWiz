<%@ include file="templates/header.jsp"%>
<%@ include file="templates/navbar.jsp"%>
<div id="mainWrapper">
	<div class="content-container">
		<%@ include file="templates/breadcrump.jsp"%>
		<%@ include file="templates/submenu.jsp"%>
		<div class="content-padding">
			<c:choose>
				<c:when
					test="${empty StudyForm or empty StudyForm.record or empty StudyForm.record.variables}">
					<div class="page-header">
						<div class="row">
							<div class="col-sm-12">
								<h4>
									<s:message code="record.codebook.no.record.headline" />
								</h4>
							</div>
						</div>
						<div>
							<s:message code="record.codebook.no.record.info" />
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="page-header">
						<div class="row">
							<div class="col-sm-12">
								<h4>
									<s:message code="record.codebook.headline" />
								</h4>
							</div>
						</div>
						<div>
							<s:message code="record.codebook.info" />
						</div>
					</div>
					<c:url var="accessUrl"
						value="/project/${StudyForm.project.id}/study/${StudyForm.study.id}/record/${StudyForm.record.id}/version/${StudyForm.record.versionId}/codebook" />
					<sf:form action="${accessUrl}" modelAttribute="StudyForm"
						class="form-horizontal" id="studyFormDis">
						<c:set var="allowEdit" value="true" />
						<c:if
							test="${!principal.user.hasRole('PROJECT_ADMIN', StudyForm.project.id, false) and
                  					!principal.user.hasRole('PROJECT_WRITER', StudyForm.project.id, false) and
                  					!principal.user.hasRole('ADMIN') and 
                  					!principal.user.hasRole('DS_WRITER', StudyForm.study.id, true)}">
							<input type="hidden" id="disStudyContent" value="disabled" />
							<c:set var="allowEdit" value="false" />
						</c:if>
						<!-- Messages -->
						<%@ include file="templates/message.jsp"%>
						<c:if test="${not empty errorCodeBookMSG}">
							<div class="panel panel-danger">
								<div class="panel-heading">
									<s:message code="record.errors.found" />
									<a data-toggle="collapse" href="#collapse2"><s:message
											code="record.errors.show" /></a>
								</div>
								<div id="collapse2" class="panel-collapse collapse">
									<div class="panel-body panel-danger">${errorCodeBookMSG}</div>
								</div>
							</div>
						</c:if>
						<c:if test="${not empty warnCodeBookMSG}">
							<div class="panel panel-warning">
								<div class="panel-heading">
									<s:message code="record.warnings.found" />
									<a data-toggle="collapse" href="#collapse1"><s:message
											code="record.warnings.show" /></a>
								</div>
								<div id="collapse1" class="panel-collapse collapse">
									<div class="panel-body">${warnCodeBookMSG}</div>
								</div>
							</div>
						</c:if>
						<div class="form-group">
							<div class="col-sm-12">
								<div class="well" style="padding: 0px; margin: 0px;">
									<div class="row col sm-12">
										<div class="col-sm-3 margin-top-7">
											<strong><s:message code="record.select.vars.info" /></strong>
										</div>
										<div class="col-sm-2">
											<div class="col-sm-2 text-align-right margin-top-7">
												<strong><s:message code="record.select.vars.from" /></strong>
											</div>
											<div class="col-sm-10 text-align-left">
												<sf:input type="number"
													onkeyup="this.value=this.value.replace(/[^\d]/,'')"
													required="required" class="form-control" path="pageLoadMin"
													min="0" />
											</div>
										</div>
										<div class="col-sm-2">
											<div class="col-sm-2 text-align-right margin-top-7">
												<strong><s:message code="record.select.vars.to" /></strong>
											</div>
											<div class="col-sm-10 text-align-left">
												<sf:input type="number" class="form-control"
													path="pageLoadMax"
													onkeyup="this.value=this.value.replace(/[^\d]/,'')"
													required="required" min="0"
													max="${StudyForm.record.numberOfVariables}" />
											</div>
										</div>
										<div class="col-sm-3">
											<div class="col-sm-8 text-align-right margin-top-7">
												<strong><s:message code="record.select.vars.max" /></strong>
											</div>
											<div class="col-sm-4 text-align-left margin-top-7">
												<s:message text="${StudyForm.record.numberOfVariables}" />
											</div>
										</div>
										<div class="col-sm-2 text-align-right">
											<sf:button class="btn btn-success - btn-sm"
												name="setNumofVars">
												<s:message code="record.select.vars.show" />
											</sf:button>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12">
										<div class="divTable">
											<div class="divTableHeading">
												<div class="divTableRow">
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.name" />
													</div>
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.type" />
													</div>
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.label" />
													</div>
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.values" />
													</div>
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.missings" />
													</div>
													<div
														class="codebookTableHide divTableCell th-width-60 hidefirst">
														<s:message code="dataset.import.report.codebook.width" />
													</div>
													<div
														class="codebookTableHide divTableCell th-width-102 hidefirst">
														<s:message code="dataset.import.report.codebook.dec" />
													</div>
													<div
														class="codebookTableHide divTableCell th-width-100 hidefirst">
														<s:message code="dataset.import.report.codebook.cols" />
													</div>
													<div
														class="codebookTableHide divTableCell th-width-100 hidefirst">
														<s:message code="dataset.import.report.codebook.aligment" />
													</div>
													<div
														class="codebookTableHide divTableCell th-width-100 hidefirst">
														<s:message
															code="dataset.import.report.codebook.measureLevel" />
													</div>
													<div
														class="codebookTableHide divTableCell th-width-100 hidefirst">
														<s:message code="dataset.import.report.codebook.role" />
													</div>
													<div class="divTableCell th-width-30"
														onclick="$('.codebookTableHide').toggle();">...</div>
													<c:forEach items="${StudyForm.record.attributes}" var="val"
														varStatus="attnameloop">
														<div class="divTableCell th-width-100">
															<s:message text="[${fn:substringAfter(val.value, '@')}]" />
														</div>
													</c:forEach>
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.construct" />
													</div>
													<div class="divTableCell th-width-200">
														<s:message code="dataset.import.report.codebook.measocc" />
													</div>
													<div class="divTableCell th-width-200">
														<s:message
															code="dataset.import.report.codebook.instrument" />
													</div>
													<div class="divTableCell th-width-300">
														<s:message code="dataset.import.report.codebook.itemtext" />
													</div>
													<div class="divTableCell th-width-100">
														<s:message code="dataset.import.report.codebook.filtervar" />
													</div>
													<div></div>
												</div>
											</div>
											<div class="divTableBody">
												<c:forEach items="${StudyForm.record.variables}" var="var"
													varStatus="loop" begin="${StudyForm.pageLoadMin-1}"
													end="${StudyForm.pageLoadMax-1}">
													<div class="divTableRow">
														<div class="divTableCell th-width-200">
															<strong><sf:input class="form-control varNames"
																	path="record.variables[${loop.index}].name"
																	id="varNameId_${loop.index}" /></strong>
														</div>
														<div class="divTableCell th-width-200">
															<c:set var="simplifiedType"
																value="${StudyForm.record.simplifyVarTypes(var.type)}" />
															<s:message code="spss.type.${simplifiedType}" />
															<c:if test="${simplifiedType ne var.type}">(<s:message
																	code="spss.type.${var.type}" />)</c:if>
														</div>
														<div class="divTableCell th-width-200">
															<sf:textarea class="form-control"
																path="record.variables[${loop.index}].label" />
														</div>
														<div style="cursor: pointer;"
															class="divTableCell th-width-200"
															onclick="showAjaxModal('${accessUrl}/modal?varId=${var.id}&modal=values', ${var.id}, 'values', event, this);">
															<c:forEach items="${var.values}" var="val">
																<div>
																	<s:message
																		text="${val.value}&nbsp;=&nbsp;&quot;${val.label}&quot;" />
																	<br />
																</div>
															</c:forEach>
														</div>
														<div style="cursor: pointer;"
															class="divTableCell th-width-200"
															onclick="showAjaxModal('${accessUrl}/modal?varId=${var.id}&modal=missings', ${var.id}, 'missings', event, this);">
															<c:choose>
																<c:when
																	test="${var.missingFormat eq 'SPSS_ONE_MISSVAL'}">
																	<s:message text="${var.missingVal1}" />
																</c:when>
																<c:when
																	test="${var.missingFormat eq 'SPSS_TWO_MISSVAL'}">
																	<s:message
																		text="${var.missingVal1},&nbsp;${var.missingVal2}" />
																</c:when>
																<c:when
																	test="${var.missingFormat eq 'SPSS_THREE_MISSVAL'}">
																	<s:message
																		text="${var.missingVal1},&nbsp;${var.missingVal2},&nbsp;${var.missingVal3}" />
																</c:when>
																<c:when test="${var.missingFormat eq 'SPSS_MISS_RANGE'}">
																	<s:message
																		text="${var.missingVal1}&nbsp;-&nbsp;${var.missingVal2}" />
																</c:when>
																<c:when
																	test="${var.missingFormat eq 'SPSS_MISS_RANGEANDVAL'}">
																	<s:message
																		text="${var.missingVal1}&nbsp;-&nbsp;${var.missingVal2},&nbsp;${var.missingVal3}" />
																</c:when>
															</c:choose>
														</div>
														<div
															class="codebookTableHide divTableCell th-width-60 hidefirst">
															<s:message text="${var.width}" />
														</div>
														<div
															class="codebookTableHide divTableCell th-width-102 hidefirst">
															<s:message text="${var.decimals}" />
														</div>
														<div
															class="codebookTableHide divTableCell th-width-100 hidefirst">
															<s:message text="${var.columns}" />
														</div>
														<div
															class="codebookTableHide divTableCell th-width-100 hidefirst">
															<s:message code="spss.aligment.${var.aligment}" />
														</div>
														<div
															class="codebookTableHide divTableCell th-width-100 hidefirst">
															<s:message code="spss.measureLevel.${var.measureLevel}" />
														</div>
														<div
															class="codebookTableHide divTableCell th-width-100 hidefirst">
															<s:message code="spss.role.${var.role}" />
														</div>
														<div class="divTableCell th-width-30">&nbsp;&nbsp;&nbsp;</div>
														<c:forEach items="${StudyForm.record.attributes}"
															var="val" varStatus="attnameloop">
															<div class="divTableCell th-width-100">
																<c:forEach items="${var.attributes}" var="att">
																	<c:if
																		test="${fn:startsWith(att.label, fn:substringAfter(val.value, '@'))}">
																		<s:message text="${att.value}" />
																		<br />
																	</c:if>
																</c:forEach>
															</div>
														</c:forEach>
														<div class="divTableCell th-width-200">
															<c:forEach items="${var.dw_attributes}" var="val"
																varStatus="attloop">
																<c:if test="${val.label == 'dw_construct'}">
																	<c:choose>
																		<c:when test="${not empty StudyForm.study.constructs}">
																			<sf:select class="form-control"
																				path="record.variables[${loop.index}].dw_attributes[${attloop.index}].value">
																				<sf:option value="">
																					<s:message code="record.codebook.no.construct" />
																				</sf:option>
																				<sf:options items="${StudyForm.study.constructs}"
																					itemLabel="name" itemValue="name" />
																			</sf:select>
																		</c:when>
																		<c:otherwise>
																			<s:message
																				code="record.codebook.study.constructs.empty" />
																		</c:otherwise>
																	</c:choose>
																	<c:set var="contains" value="false" />
																	<c:forEach items="${StudyForm.study.constructs}"
																		var="construct">
																		<c:if test="${construct.name eq val.value}">
																			<c:set var="contains" value="true" />
																		</c:if>
																	</c:forEach>
																	<c:if test="${not contains && val.value ne ''}">
																		<div style="color: red;">
																			<s:message code="record.codebook.construct.missing"
																				arguments="${val.value}" />
																		</div>
																	</c:if>
																</c:if>
															</c:forEach>
														</div>
														<div class="divTableCell th-width-200">
															<c:forEach items="${var.dw_attributes}" var="val"
																varStatus="attloop">
																<c:if test="${val.label == 'dw_measocc'}">
																	<c:choose>
																		<c:when test="${not empty StudyForm.study.measOcc}">
																			<sf:select class="form-control"
																				path="record.variables[${loop.index}].dw_attributes[${attloop.index}].value">
																				<sf:option value="">
																					<s:message code="record.codebook.no.measocc" />
																				</sf:option>
																				<sf:options items="${StudyForm.study.measOcc}"
																					itemLabel="text" itemValue="text" />
																			</sf:select>
																		</c:when>
																		<c:otherwise>
																			<s:message code="record.codebook.study.measOcc.empty" />
																		</c:otherwise>
																	</c:choose>
																	<c:set var="contains" value="false" />
																	<c:forEach items="${StudyForm.study.measOcc}"
																		var="construct">
																		<c:if test="${construct.text eq val.value}">
																			<c:set var="contains" value="true" />
																		</c:if>
																	</c:forEach>
																	<c:if test="${not contains && val.value ne ''}">
																		<div style="color: red;">
																			<s:message code="record.codebook.measocc.missing"
																				arguments="${val.value}" />
																		</div>
																	</c:if>
																</c:if>
															</c:forEach>
														</div>
														<div class="divTableCell th-width-200">
															<c:forEach items="${var.dw_attributes}" var="val"
																varStatus="attloop">
																<c:if test="${val.label == 'dw_instrument'}">
																	<c:choose>
																		<c:when
																			test="${not empty StudyForm.study.instruments}">
																			<sf:select class="form-control"
																				path="record.variables[${loop.index}].dw_attributes[${attloop.index}].value">
																				<sf:option value="">
																					<s:message code="record.codebook.no.instrument" />
																				</sf:option>
																				<sf:options items="${StudyForm.study.instruments}"
																					itemLabel="title" itemValue="title" />
																			</sf:select>
																		</c:when>
																		<c:otherwise>
																			<s:message
																				code="record.codebook.study.instruments.empty" />
																		</c:otherwise>
																	</c:choose>
																	<c:set var="contains" value="false" />
																	<c:forEach items="${StudyForm.study.instruments}"
																		var="construct">
																		<c:if test="${construct.title eq val.value}">
																			<c:set var="contains" value="true" />
																		</c:if>
																	</c:forEach>
																	<c:if test="${not contains && val.value ne ''}">
																		<div style="color: red;">
																			<s:message code="record.codebook.instrument.missing"
																				arguments="${val.value}" />
																		</div>
																	</c:if>
																</c:if>
															</c:forEach>
														</div>
														<div class="divTableCell th-width-300">
															<c:forEach items="${var.dw_attributes}" var="val"
																varStatus="attloop">
																<c:if test="${val.label == 'dw_itemtext'}">
																	<sf:textarea class="form-control"
																		path="record.variables[${loop.index}].dw_attributes[${attloop.index}].value" />
																</c:if>
															</c:forEach>
														</div>
														<div class="divTableCell th-width-100">
															<c:forEach items="${var.dw_attributes}" var="val"
																varStatus="attloop">
																<c:if test="${val.label == 'dw_filtervar'}">
																	<sf:select class="form-control"
																		path="record.variables[${loop.index}].dw_attributes[${attloop.index}].value">
																		<sf:option value="">
																			<s:message code="gen.no" />
																		</sf:option>
																		<sf:option value="1">
																			<s:message code="gen.yes" />
																		</sf:option>
																	</sf:select>
																</c:if>
															</c:forEach>
														</div>
														<div></div>
													</div>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
								<c:if test="${allowEdit}">
									<div class="row">
										<div class="col-sm-12">
											<div class="btn btn-default btn-sm"
												onclick="showGlobalAjaxModal('${accessUrl}/modal?varId=-1&modal=values');">
												<s:message code="record.codebook.set.values" />
											</div>
											<div class="btn btn-default btn-sm"
												onclick="showGlobalAjaxModal('${accessUrl}/modal?varId=-1&modal=missings');">
												<s:message code="record.codebook.set.missings" />
											</div>
										</div>
									</div>
								</c:if>
							</div>
						</div>
						<s:message code="dataset.import.report.codebook.help"
							var="appresmess" />
						<%@ include file="templates/helpblock.jsp"%>
						<c:if test="${allowEdit}">
							<c:set var="input_vars"
								value="newChangeLog;record.changeLog;required; ;row" />
							<%@ include file="templates/gen_textarea.jsp"%>
							<div class="form-group">
								<div class="col-sm-12">
									<sf:label class="control-label" path="ignoreValidationErrors">
										<s:message code="codebook.submit.disable.validation" />&nbsp;										
									</sf:label>
									<sf:checkbox path="ignoreValidationErrors" />
									<s:message code="codebook.submit.disable.validation.help"
										var="appresmess" />
									<%@ include file="templates/helpblock.jsp"%>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-6 text-align-left">
									<a href="${accessUrl}" class="btn btn-default btn-sm"><s:message
											code="codebook.cancel.save" /></a>
								</div>
								<div class="col-sm-6 text-align-right">
									<button type="submit" class="btn btn-success btn-sm"
										name="saveCodebook">
										<s:message code="codebook.submit.save" />
									</button>
								</div>
							</div>
						</c:if>
					</sf:form>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
<c:if test="${allowEdit}">
	<div class="modal fade" id="valueModal" role="dialog">
		<div class="modal-dialog">
			<!-- content is dynamically loading with ajax -->
		</div>
	</div>
	<div class="modal fade" id="errorModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content panel-primary">
				<div class="modal-header panel-heading">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">
						<s:message code="record.codebook.submit.timeout.modal.head" />
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<div class="col-sm-12">
							<s:message code="record.codebook.submit.timeout.modal.info" />
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<div class="form-group">
						<div class="col-sm-offset-0 col-md-12">
							<a href="${accessUrl}" class="btn btn-success"> <s:message
									code="gen.yes" />
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>
<%@ include file="templates/footer.jsp"%>