<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/cohortreports/resources/reporting.tld" %>

<openmrs:require privilege="Edit Patient Searches" otherwise="/login.htm" redirect="/module/cohortreports/managePatientSearches.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#deleteButton').click(function(event){
			if (confirm('Are you sure you want to delete this Patient Search?')) {
				document.location.href='deletePatientSearch.form?id=${id}';
			}
		} );
	} );	
</script>

<h2>
	<c:if test="${empty search.reportObjectId}"><spring:message code="cohortreports.patientSearch.enter" /></c:if>
	<c:if test="${!empty search.reportObjectId}"><spring:message code="cohortreports.patientSearch.edit" />: ${search.name}</c:if>
</h2>

<form method="post" class="box" action="savePatientSearch.form">
	<input type="hidden" name="id" value="${id}">
	<input type="hidden" name="type" value="${type.name}">
	<table border=0>
		<tr>
			<th valign="top"><spring:message code="general.name" /></th>
			<td valign="top"><input type="text" id="name" name="name" size="50" value="${search.name}"/></td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="general.description" /></th>
			<td valign="top"><textarea id="description" name="description" rows="2" cols="40">${search.description}</textarea></td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="PatientSearch.javaclass" /></th>
			<td valign="top">${search.patientSearch.filterClass.simpleName}</td>
		</tr>
		<tr>
			<th valign="top"><spring:message code="PatientSearch.searchArguments" /></th>
			<td>
				<table>
					<tr>
						<td rows="1"><b><spring:message code="general.name" /></b></td>
						<td rows="1"><b><spring:message code="PatientSearch.value" /></b></td>
						<td>--OR--</td>
						<td rows="1"><b>User specified</b></td>
					</tr>
					<c:forEach items="${arguments}" var="argument" varStatus="varStatus">
						<tr>
							<td valign="top">
								${argument.name}				
							</td>
							<td valign="top">
								<c:choose>
									<c:when test="${argument.name == 'sql'}">
										<rpt:widget id="argument${argument.name}" name="argument_${argument.name}" object="${filterInstance}" property="${argument.name}" defaultValue="${argument.value == '_dynamic_' ? '' : argument.value}" attributes="rows=10|cols=60"/>
									</c:when>
									<c:otherwise>
										<rpt:widget id="argument${argument.name}" name="argument_${argument.name}" object="${filterInstance}" property="${argument.name}" defaultValue="${argument.value == '_dynamic_' ? '' : argument.value}"/>
									</c:otherwise>
								</c:choose>
							</td>
							<td></td>
							<td valign="top"><input type="checkbox" name="dynamic_${argument.name}" value="t" ${argument.value == '_dynamic_' ? 'checked' : ''}/></td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</table>
	<br>
	<input type="submit" value='<spring:message code="PatientSearch.save"/>'>
	<input type="button"" value='<spring:message code="general.cancel"/>' onclick="document.location.href='managePatientSearches.form';">
	<input type="button"" id="deleteButton" value='<spring:message code="general.delete"/>'">
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
