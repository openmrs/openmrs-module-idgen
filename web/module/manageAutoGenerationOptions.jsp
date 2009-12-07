<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageautoGenerationOptions.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h3><spring:message code="idgen.autoGenerationConfiguration" /></h3>

<style>
	#sourceTable th, td {text-align:left; padding-right:10px; white-space:nowrap;}
	.underlineRow td {border-bottom: 1px solid black;}
	#sourceTable th {border-bottom: 1px solid black;}
</style>

<b class="boxHeader"><spring:message code="idgen.autoGenerationOptionHeader"/></b>
<div class="box">
	<table id="sourceTable" width="100%">
		<thead>
			<tr class="underlineRow">
				<th><spring:message code="PatientIdentifier.identifierType"/></th>
				<th><spring:message code="idgen.sourceName"/></th>
				<th><spring:message code="idgen.manualEntryEnabled"/></th>
				<th><spring:message code="idgen.automaticGenerationEnabled"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${optionMap}" var="entry" varStatus="entryStatus">
				<c:set var="canEdit" value="${!empty availableSources[entry.key]}"/>
				<tr>
					<td>
						<c:if test="${canEdit}">
							<a href="editAutoGenerationOption.form?identifierType=${entry.key.patientIdentifierTypeId}">
						</c:if>
						${entry.key.name}
						<c:if test="${canEdit}">
							</a>
						</c:if>
					</td>
					<c:choose>
						<c:when test="${empty entry.value}">
							<td><spring:message code="general.none"/></td>
							<td><spring:message code="general.true"/></td>
							<td><spring:message code="general.false"/></td>
						</c:when>
						<c:otherwise>
							<td>${entry.value.source.name}</td>
							<td><spring:message code="general.${entry.value.manualEntryEnabled}"/></td>
							<td><spring:message code="general.${entry.value.automaticGenerationEnabled}"/></td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>