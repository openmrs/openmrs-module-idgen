<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageIdentifierSources.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<style>
	#sourceTable th, td {text-align:left; padding-right:10px; white-space:nowrap;}
	.underlineRow td {border-bottom: 1px solid black;}
</style>

<h3><spring:message code="idgen.source.identifierType.name"/>: <spring:message code="idgen.source.name"/></h3>

<table id="sourceTable">
	<tr>
		<th><spring:message code="idgen.sourceName" /></th>
		<td>
			<spring:message code="idgen.source.name"/>
			&nbsp;&nbsp;
			(<a href="editIdentifierSource.form?source=${source.id}"><spring:message code="general.edit" />)
		</td>
	</tr>
	<tr>
		<th><spring:message code="general.description" />:</th>
		<td><spring:message code="idgen.source.description"/></td>
	</tr>
	<tr>
		<th><spring:message code="PatientIdentifier.identifierType" /></th>
		<td><spring:message code="idgen.source.identifierType.name"/></td>
	</tr>
	<tr>
		<th><spring:message code="idgen.checkDigitAlgorithm" />:</td>
		<td>
			<c:choose>
				<c:when test="${!empty source.identifierType.validator}">
					<spring:message code="idgen.source.identifierType.validator"/>
				</c:when>
				<c:otherwise>
					<spring:message code="general.none" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
</table>
<br/>

<c:if test="${source['class'].name == 'org.openmrs.module.idgen.SequentialIdentifierGenerator'}">

	<spring:message code="idgen.idSourceGeneratesIds"/><br/><br/>
	
	<table id="sourceTable">
		<tr>
			<th><spring:message code="idgen.baseCharacterSet"/>:</th>
			<td><spring:message code="idgen.source.baseCharacterSet"/></td>
		</tr>
		<tr>
			<th><spring:message code="idgen.firstIdentifierBase"/>:</th>
			<td><spring:message code="idgen.source.firstIdentifierBase"/></td>
		</tr>
		<tr>
			<th><spring:message code="idgen.prefix"/>:</th>
			<td><spring:message code="igden.source.prefix"/></td>
		</tr>
		<tr>
			<th><spring:message code="idgen.suffix"/>:</th>
			<td><spring:message code="idgen.source.suffix"/></td>
		</tr>
		<tr>
			<th><spring:message code="idgen.minLength"/>:</th>
			<td><spring:message code="idgen.source.minLength"/></td>
		</tr>
        <tr>
            <th><spring:message code="idgen.maxLength"/>:</th>
            <td><spring:message code="idgen.source.maxLength"/></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<th><spring:message code="idgen.reservedIdentifiers"/>:</th>
			<td>
				<form action="reserveIdentifiersFromFile.form" method="post" enctype="multipart/form-data">
					<a href="exportReservedIdentifiers.form?source=${source.id}"><spring:message code="idgen.lengthOfReservedIdentifiers"/> <spring:message code="idgen.defined"/></a>
					&nbsp;&nbsp;
					<spring:message code="idgen.uploadReservedIdentifiers"/>: 
					<input type="hidden" name="source" value="${source.id}"/>
					<input type="file" name="inputFile"/>
					<input type="submit" value="Upload"/>
				</form>
			</td>
		</tr>
	</table>
</c:if>

<c:if test="${source['class'].name == 'org.openmrs.module.idgen.RemoteIdentifierSource'}">
	<spring:message code="idgen.idSourceConnectsToURL"/><br/>
	<spring:message code="idgen.source.url"/>
</c:if>

<c:if test="${source['class'].name == 'org.openmrs.module.idgen.IdentifierPool'}">

	<spring:message code="idgen.idSourceManagesPoolOfIds"/><br/><br/>
	
	<spring:message code="idgen.quantityConsumed"/><br/>
	<spring:message code="idgen.quantityAvailable"/>
	
	<br/><br/>
	
	<form action="addIdentifiersFromFile.form" method="post" enctype="multipart/form-data">
		<spring:message code="idgen.poolCanBeFilled"/>
		<input type="hidden" name="source" value="${source.id}"/>
		<input type="file" name="inputFile"/>
		<input type="submit" value="Upload"/>
	</form>
	
	<c:if test="${!empty source.source}">
		<br/>
		<form action="addIdentifiersFromSource.form" method="post" enctype="multipart/form-data">
			<spring:message code="idgen.poolCanAlsoBeFilled"/><br/>
			<spring:message code="idgen.quantityToUpload"/>
			<input type="hidden" name="source" value="${source.id}"/>
			<input type="input" name="batchSize"/>
			<input type="submit" value="Upload"/>
		</form>
	</c:if>
	<br/>
	
</c:if>
<hr/>
<form action="exportIdentifiers.form">
	<b><spring:message code="idgen.exportIds"/></b>
	<c:set var="available" value="t"/>
	<c:if test="${source['class'].name == 'org.openmrs.module.idgen.IdentifierPool'}">
		<c:if test="${fn:length(source.availableIdentifiers) == 0}">
			<c:set var="available" value="f"/>
			<spring:message code="idgen.exportNoneAvailable"/>
		</c:if>
	</c:if>
	<br/>
	<c:if test="${available == 't'}">
		<input type="hidden" name="source" value="${source.id}"/>
		<table>
			<tr>
				<td><spring:message code="idgen.numberToGenerate" /></td>
				<td><input type="text" name="numberToGenerate" value=""/></td>
			</tr>
			<tr>
				<td><spring:message code="idgen.comment" /></td>
				<td><textarea name="comment" rows="3" cols="20"/></textarea></td>
			</tr>
		</table>
		<input type="submit" value="<spring:message code="idgen.export" />"/>	
	</c:if>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>