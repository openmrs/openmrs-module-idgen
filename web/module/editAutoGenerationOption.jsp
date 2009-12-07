<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageAutoGenerationOptions.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<style>
	.requiredField {font-weight:bold; color:red;}
</style>

<h3><spring:message code="idgen.autoGenerationOptionHeader" /></h3>

<form:form modelAttribute="option" method="post" action="saveAutoGenerationOption.form">
	<form:errors path="*" cssClass="error"/><br/>
	<table>
		<tr>
			<th align="right"><spring:message code="PatientIdentifier.identifierType" />:</th>
			<td>${option.identifierType}</td>
		</tr>
		<tr>
			<th align="right" valign="top"><spring:message code="idgen.sourceToAutogenerateFrom" />:</th>
			<td>
				<form:select path="source">
					<form:option value=""></form:option>
					<form:options items="${availableSources}" itemValue="id" itemLabel="name"/>
				</form:select>
			</td>
		</tr>
		<tr>
			<th align="right" valign="top"><spring:message code="idgen.options" />:</th>
			<td>
				<form:checkbox path="automaticGenerationEnabled" value="${true}" /> <spring:message code="idgen.automaticGenerationEnabled" />
				<form:errors path="automaticGenerationEnabled" cssClass="error" />
				<br/>
				<form:checkbox path="manualEntryEnabled" value="${true}" /> <spring:message code="idgen.manualEntryEnabled" />
				<form:errors path="manualEntryEnabled" cssClass="error" />
			</td>
		</tr>
	</table>
	<br/>
	<input type="submit" value="<spring:message code="general.save" />"/>
	<input type="button" value="<spring:message code="general.cancel" />" onclick="document.location.href='manageAutoGenerationOptions.form';"/>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>