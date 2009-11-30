<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patient Searches" otherwise="/login.htm" redirect="/module/cohortreports/managePatientSearches.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#patientSearchTable").dataTable( {
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

<h2><spring:message code="PatientSearch.manage.title" /></h2>

<br/>
<form method="get" action="editPatientSearch.form">
	<select name="type">
		<option value=""></option>
		<c:forEach items="${filterTypes}" var="filterTypeEntry">
			<option value="${filterTypeEntry.key.name}">${filterTypeEntry.value}</option>
		</c:forEach>
	</select>
	<input type="submit" value="Add new"/>
</form>
<br/>
<b class="boxHeader"><spring:message code="PatientSearch.manage.list.title" /></b>
<form method="post" class="box">
<table id="patientSearchTable" width="100%">
	<thead>
		<tr>
			<th align="left"></th>
			<th align="left"><spring:message code="general.name" /></th>
			<th align="left"><spring:message code="general.description" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="s" items="${searches}" varStatus="varStatus">
			<tr class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">	
				<td style="padding-left:5px; padding-right:5px;">
					<a href="editPatientSearch.form?id=${s.reportObjectId}">
						<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
					</a>
				</td>
				<td valign="top" style="white-space:nowrap; padding-right:20px; font-weight:bold;">${s.name}</td>
				<td width="100%" valign="top">${s.description}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>
