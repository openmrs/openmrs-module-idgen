<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageIdentifierSources.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<style>
	.requiredField {font-weight:bold; color:red;}
</style>

<h3><spring:message code="idgen.manage.title" /> <spring:message code="idgen.general.for"/> ${source.identifierType.name}</h3>

<form:form modelAttribute="source" method="post" action="saveIdentifierSource.form">
	<form:errors path="*" cssClass="error"/><br/>
	<table>
		<tr>
			<td align="right">
				<span class="requiredField">*</span>
				<spring:message code="general.name" />:
			</td>
			<td><form:input path="name" size="50" /><form:errors path="name" cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message code="general.description" />:</td>
			<td><form:textarea path="description" cols="50" rows="2" /><form:errors path="description" cssClass="error" /></td>
		</tr>
		<c:if test="${source.class.name == 'org.openmrs.module.idgen.SequentialIdentifierGenerator'}">
			<tr>
				<td align="right" valign="top"><spring:message code="idgen.initialSequenceValue" />:</td>
				<td>
					<c:choose>
						<c:when test="${source.initialized}">
							<form:hidden path="initialSequenceValue"/>
							${source.initialSequenceValue}
							<spring:message code="idgen.inUseUnableToModify" />
						</c:when>
						<c:otherwise>
							<form:input path="initialSequenceValue" size="20" /><form:errors path="initialSequenceValue" cssClass="error" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<td align="right"><spring:message code="idgen.prefix" />:</td>
				<td><form:input path="prefix" size="50" /><form:errors path="prefix" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right"><spring:message code="idgen.minSequenceLength" />:</td>
				<td><form:input path="minSequenceLength" size="50" /><form:errors path="minSequenceLength" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right">
					<span class="requiredField">*</span>
					<spring:message code="idgen.validCharacters" />:
				</td>
				<td><form:input path="validCharacters" size="80" /><form:errors path="validCharacters" cssClass="error" /></td>
			</tr>
		</c:if>
		<c:if test="${source.class.name == 'org.openmrs.module.idgen.RemoteIdentifierSource'}">
			<tr>
				<td align="right">
					<span class="requiredField">*</span>
					<spring:message code="idgen.url" />:
				</td>
				<td>
					<form:input path="url" size="50" /><form:errors path="url" cssClass="error" />
					<button>Test Connection</button>
				</td>
			</tr>
		</c:if>
		<c:if test="${source.class.name == 'org.openmrs.module.idgen.IdentifierPool'}">
			<tr>
				<td align="right"><spring:message code="idgen.poolSource" />:</td>
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
				<td align="right"><spring:message code="idgen.batchSize" />:</td>
				<td><form:input path="batchSize" size="50" /><form:errors path="batchSize" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right"><spring:message code="idgen.minPoolSize" />:</td>
				<td><form:input path="minPoolSize" size="50" /><form:errors path="minPoolSize" cssClass="error" /></td>
			</tr>
		</c:if>
	</table>
	<br/>
	<input type="submit"/>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>