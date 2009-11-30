<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/cohortreports/resources/reporting.tld" %>

<openmrs:require privilege="Edit Patient Searches" otherwise="/login.htm" redirect="/module/cohortreports/manageCohortReportExports.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#deleteButton').click(function(event){
			if (confirm('Are you sure you want to delete this Cohort Report Export?')) {
				document.location.href='deleteCohortReportExport.form?id=${id}';
			}
		} );
	} );	
</script>

<h2>
	<c:if test="${empty id}"><spring:message code="cohortreports.cohortreportexports.enter" /></c:if>
	<c:if test="${!empty id}"><spring:message code="cohortreports.cohortreportexports.edit" />: ${definition.name}</c:if>
</h2>

<form method="post" class="box" action="saveCohortReportExport.form">
	<input type="hidden" name="id" value="${id}">
	<input type="hidden" name="reportSchemaId" value="${reportSchemaId}">
	<table border=0 width="100%" >
		<tr>
			<th valign="top"><spring:message code="cohortreports.cohortReport" /></th>
			<td valign="top">${cohortReport.name}</td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="general.name" /></th>
			<td valign="top"><input type="text" id="name" name="name" size="50" value="${definition.name}"/></td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="general.description" /></th>
			<td valign="top"><textarea id="description" name="description" rows="2" cols="40">${definition.description}</textarea></td>
		</tr>
		<tr>
			<td colspan="2" valign="top" width="100%">
				<b><spring:message code="cohortreports.cohortreportexports.columnExports" /></b><br/>
				<table style="border:1px solid black; width:100%">
					<tr style="background-color:#8FABC7; color:white;">
						<td style="padding-right:20px;"><b><spring:message code="Cohort.title" /></b></td>
						<td style="padding-right:20px;"><b><spring:message code="general.description" /></b></td>
						<td style="padding-right:20px;"><b><spring:message code="DataExport.dataExport" /></b></td>
					</tr>
					<c:forEach items="${cohortDsd.columnKeys}" var="columnKey" varStatus="varStatus">
						<tr>
							<td valign="top">${columnKey}</td>
							<td valign="top">${cohortDsd.descriptions[columnKey]}</td>
							<td valign="top">
								<select name="dataExport.${columnKey}">
									<option value=""></option>
									<c:forEach items="${dataExports}" var="export" varStatus="exportStatus">
										<option value="${export.reportObjectId}" <c:if test="${definition.columnToExportMap[columnKey] == export.reportObjectId}">selected</c:if>>
											${export.name}
										</option>
									</c:forEach>
								</select>
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</table>
	<br>
	<input type="submit" value='<spring:message code="general.save"/>'>
	<input type="button"" value='<spring:message code="general.cancel"/>' onclick="document.location.href='manageCohortReportExports.form';">
	<c:if test="${!empty id}">
		<input type="button"" id="deleteButton" value='<spring:message code="general.delete"/>'">
	</c:if>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
