<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageIdentifierSources.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<style>
	.requiredField {font-weight:bold; color:red;}
</style>

<h3>
	<c:choose>
		<c:when test="${empty source.id}">
			<spring:message code="general.new" />: <spring:message code="idgen.${source.class.name}" />
		</c:when>
		<c:otherwise>
			<spring:message code="general.edit" />: ${source.name}
		</c:otherwise>
	</c:choose>
	<spring:message code="idgen.general.for"/> ${source.identifierType.name}
</h3>

<form:form modelAttribute="source" method="post" action="saveIdentifierSource.form">
	<form:errors path="*" cssClass="error"/><br/>
	<table>
		<tr>
			<th align="right">
				<span class="requiredField">*</span>
				<spring:message code="general.name" />:
			</th>
			<td><form:input path="name" size="50" /><form:errors path="name" cssClass="error" /></td>
		</tr>
		<tr>
			<th align="right" valign="top"><spring:message code="general.description" />:</th>
			<td><form:textarea path="description" cols="50" rows="2" /><form:errors path="description" cssClass="error" /></td>
		</tr>
		<tr>
			<th align="right"><spring:message code="idgen.checkDigitAlgorithm" />:</th>
			<td>
				<c:choose>
					<c:when test="${!empty source.identifierType.validator}">
						${source.identifierType.validator}
					</c:when>
					<c:otherwise>
						<spring:message code="general.none" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>		
		<tr>
			<th align="right"><spring:message code="idgen.regexFormat" />:</th>
			<td>
				<c:choose>
					<c:when test="${!empty source.identifierType.format}">
						${source.identifierType.format}
					</c:when>
					<c:otherwise>
						<spring:message code="general.none" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>		
		
		<c:if test="${source.class.name == 'org.openmrs.module.idgen.SequentialIdentifierGenerator'}">
			<tr>
				<th align="right">
					<span class="requiredField">*</span>
					<spring:message code="idgen.baseCharacterSet" />:
				</th>
				<td><form:input path="baseCharacterSet" size="80" /><form:errors path="baseCharacterSet" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right" valign="top">
					<span class="requiredField">*</span>
					<spring:message code="idgen.firstIdentifierBase" />:
				</th>
				<td>
					<c:choose>
						<c:when test="${source.initialized}">
							<form:hidden path="firstIdentifierBase"/>
							${source.firstIdentifierBase}
							<spring:message code="idgen.inUseUnableToModify" />
						</c:when>
						<c:otherwise>
							<form:input path="firstIdentifierBase" size="20" /><form:errors path="firstIdentifierBase" cssClass="error" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.prefix" />:</th>
				<td><form:input path="prefix" size="10" /><form:errors path="prefix" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.suffix" />:</th>
				<td><form:input path="suffix" size="10" /><form:errors path="suffix" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.length" />:</th>
				<td><form:input path="length" size="10" /><form:errors path="length" cssClass="error" /></td>
			</tr>
		</c:if>
		<c:if test="${source.class.name == 'org.openmrs.module.idgen.RemoteIdentifierSource'}">
			<tr>
				<th align="right">
					<span class="requiredField">*</span>
					<spring:message code="idgen.url" />:
				</th>
				<td>
					<form:input path="url" size="50" /><form:errors path="url" cssClass="error" />
				</td>
			</tr>
		</c:if>
		<c:if test="${source.class.name == 'org.openmrs.module.idgen.IdentifierPool'}">
			<tr>
				<th align="right"><spring:message code="idgen.poolSource" />:</th>
				<td>
					<c:choose>
						<c:when test="${!empty otherCompatibleSources}">
							<form:select path="source">
								<form:option value=""></form:option>
								<form:options items="${otherCompatibleSources}" itemValue="id" itemLabel="name"/>
							</form:select>
						</c:when>
						<c:otherwise>
							<spring:message code="idgen.noCompatibleSources"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.order" />:</th>
				<td>
					<form:radiobutton path="sequential" value="${false}" /> <spring:message code="idgen.random" />
					<form:radiobutton path="sequential" value="${true}" /> <spring:message code="idgen.sequential" />
					<form:errors path="sequential" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.autoGeneratePool" />:</th>
				<td>
					<form:radiobutton path="autoGenerate" value="${false}" /> <spring:message code="idgen.false" />
					<form:radiobutton path="autoGenerate" value="${true}" /> <spring:message code="idgen.true" />
					<form:errors path="autoGenerate" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.batchSize" />:</th>
				<td><form:input path="batchSize" size="50" /><form:errors path="batchSize" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.minPoolSize" />:</th>
				<td><form:input path="minPoolSize" size="50" /><form:errors path="minPoolSize" cssClass="error" /></td>
			</tr>
		</c:if>
	</table>
	<br/>
	<input type="submit" value="<spring:message code="general.save" />"/>
	<input type="button" value="<spring:message code="general.cancel" />" onclick="document.location.href='manageIdentifierSources.form';"/>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>