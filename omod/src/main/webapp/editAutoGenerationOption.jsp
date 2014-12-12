<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="frm" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Auto Generation Options" otherwise="/login.htm" redirect="/module/idgen/manageAutoGenerationOptions.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<style>
	.requiredField {font-weight:bold; color:red;}
</style>

<h3><spring:message code="idgen.autoGenerationOptionHeader" /></h3>

<frm:form modelAttribute="option" method="post" action="saveAutoGenerationOption.form">
	<frm:errors path="*" cssClass="error"/><br/>
	<table>
		<tr>
			<th align="right"><spring:message code="PatientIdentifier.identifierType" />:</th>
			<td>${option.identifierType}</td>
		</tr>
        <tr>
            <th align="right" valign="top"><spring:message code="idgen.location" />:</th>
            <td>
                <frm:select path="location">
                    <frm:option value=""></frm:option>
                    <frm:options items="${availableLocations}" itemValue="id" itemLabel="name"/>
                </frm:select>
            </td>
        </tr>
		<tr>
			<th align="right" valign="top"><spring:message code="idgen.sourceToAutogenerateFrom" />:</th>
			<td>
				<frm:select path="source">
					<frm:option value=""></frm:option>
					<frm:options items="${availableSources}" itemValue="id" itemLabel="name"/>
				</frm:select>
			</td>
		</tr>
		<tr>
			<th align="right" valign="top"><spring:message code="idgen.options" />:</th>
			<td>
				<frm:checkbox path="automaticGenerationEnabled" value="${true}" /> <spring:message code="idgen.automaticGenerationEnabled" />
				<frm:errors path="automaticGenerationEnabled" cssClass="error" />
				<br/>
				<frm:checkbox path="manualEntryEnabled" value="${true}" /> <spring:message code="idgen.manualEntryEnabled" />
				<frm:errors path="manualEntryEnabled" cssClass="error" />
			</td>
		</tr>
	</table>
	<br/>
	<input type="submit" value="<spring:message code="general.save" />"/>
	<input type="button" value="<spring:message code="general.cancel" />" onclick="document.location.href='manageAutoGenerationOptions.form';"/>
</frm:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>