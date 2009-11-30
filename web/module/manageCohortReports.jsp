<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/cohortreports/manageCohortReports.list" />

<%@ include file="localHeader.jsp" %>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#cohortReportTable").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		} );
	} );	
</script>

<h2><spring:message code="cohortreports.cohortReport.manage" /></h2>

<a href="cohortReport.form"><spring:message code="cohortreports.cohortReport.enter" /></a>

<br/>
<br/>

<div class="boxHeader">
	<b><spring:message code="cohortreports.cohortReport.edit" /></b>
</div>
<form method="post" class="box">
	<table id="cohortReportTable" width="100%">
		<thead>
			<tr>
				<th></th>
				<th><spring:message code="general.name" /></th>
				<th><spring:message code="general.description" /></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="reportSchema" items="${reportSchemas}" varStatus="status">
				<tr class="<c:choose><c:when test="${status.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
					<td style="padding-left:5px; padding-right:5px; white-space:nowrap;">
						<a href="${pageContext.request.contextPath}/admin/reports/runReport.form?reportId=${reportSchema.reportSchemaId}">
							<img src='<c:url value="/images/play.gif"/>' alt="run" border="0"/>
						</a>
						<a href="cohortReport.form?reportId=${reportSchema.reportSchemaId}">
							<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
						</a>
					</td>
					<td valign="top" style="white-space:nowrap; padding-right:20px;">${reportSchema.name}</td>
					<td valign="top" width="100%">${reportSchema.description}</td>
					<td valign="top"><a href="deleteCohortReport.form?id=${reportSchema.reportSchemaId}" onclick="return confirm('Are you sure you want to delete this report?');">
						<img src='<c:url value="/images/trash.gif"/>' alt="edit" border="0"/>
					</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>
