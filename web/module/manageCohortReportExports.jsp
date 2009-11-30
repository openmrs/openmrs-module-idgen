<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patient Searches" otherwise="/login.htm" redirect="/module/cohortreports/manageCohortReportExports.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#definitionTable").dataTable( {
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

<h2><spring:message code="cohortreports.cohortreportexports.manage" /></h2>

<br/>
<form method="get" action="editCohortReportExport.form">
	<select name="reportSchemaId">
		<option value=""></option>
		<c:forEach items="${cohortReports}" var="entry">
			<option value="${entry.value.reportSchemaId}">${entry.value.name}</option>
		</c:forEach>
	</select>
	<input type="submit" value="Add new"/>
</form>
<br/>
<b class="boxHeader"><spring:message code="cohortreports.cohortreportexports.edit" /></b>
<form method="post" class="box">
<table id="definitionTable" width="100%">
	<thead>
		<tr>
			<th align="left"></th>
			<th align="left"><spring:message code="cohortreports.cohortReport" /></th>
			<th align="left"><spring:message code="general.name" /></th>
			<th align="left"><spring:message code="general.description" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="d" items="${definitions}" varStatus="varStatus">
			<tr class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">	
				<td style="padding-left:5px; padding-right:5px;">
					<a href="editCohortReportExport.form?id=${d.reportObjectId}">
						<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
					</a>
				</td>
				<td valign="top" style="white-space:nowrap; padding-right:20px;">${cohortReports[d.reportSchemaId].name}</td>
				<td valign="top" style="white-space:nowrap; padding-right:20px;">${d.name}</td>
				<td width="100%" valign="top">${d.description}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>
