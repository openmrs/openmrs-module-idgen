<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageIdentifierSources.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h3><spring:message code="idgen.manage.title" /></h3>

<style>
	#sourceTable th, td {text-align:left; padding-right:10px; white-space:nowrap;}
	.underlineRow td {border-bottom: 1px solid black;}
	#sourceTable th {border-bottom: 1px solid black;}
</style>

<c:if test="${!empty sourcesByType}">

	<b class="boxHeader"><spring:message code="idgen.existingIdentifierSources"/></b>
	<div class="box">
		<table id="sourceTable" width="100%">
			<thead>
				<tr class="underlineRow">
					<th><spring:message code="PatientIdentifier.identifierType"/></th>
					<th><spring:message code="idgen.sourceType"/></th>
					<th><spring:message code="idgen.sourceName"/></th>
					<th width="100%"><spring:message code="idgen.actions"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${sourcesByType}" var="entry" varStatus="entryStatus">

					<c:forEach items="${entry.value}" var="source" varStatus="status">
					
						<tr class="${entryStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'} ${status.last ? 'underlineRow' : '' }">
						
							<td><c:out value="${entry.key.name}"/></td>
							<td><spring:message code="idgen.${source['class'].name}"/></td>
							<td><c:out value="${source.name}"/></td>
							<td>
								<button style="height:20px; font-size:8pt; vertical-align:middle;" onclick="document.location.href='editIdentifierSource.form?source=${source.id}';">
									<spring:message code="idgen.configure"/>
								</button>
								<button style="height:20px; font-size:8pt; vertical-align:middle;" onclick="document.location.href='viewIdentifierSource.form?source=${source.id}';">
									<spring:message code="general.view"/>
								</button>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:if>

<br/>

<b class="boxHeader"><spring:message code="idgen.setupNewIdentifierSource"/></b>
<div class="box">
	<form action="editIdentifierSource.form" method="get">
		<table>
			<tr>
				<td><spring:message code="PatientIdentifier.identifierType"/>:</td>
				<td>
					<select name="identifierType">
						<option value=""></option>
						<c:forEach items="${identifierTypes}" var="pit">
							<option value="${pit.patientIdentifierTypeId}"><c:out value="${pit.name}"/></option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td><spring:message code="idgen.sourceType"/>:</td>
				<td>
					<select name="sourceType">
						<option value=""></option>
						<c:forEach items="${sourceTypes}" var="type">
							<option value="${type.name}"><spring:message code="idgen.${type.name}"/></option>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>
		<input type="submit" value="<spring:message code="general.add"/>"/>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>