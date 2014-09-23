<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="frm" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/manageIdentifierSources.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<style>
	.requiredField {font-weight:bold; color:red;}
</style>

<h3>
	<c:choose>
		<c:when test="${empty source.id}">
			<spring:message code="general.new" />: <spring:message code="idgen.${source['class'].name}" />
		</c:when>
		<c:otherwise>
			<spring:message code="general.edit" />: ${source.name}
		</c:otherwise>
	</c:choose>
	<spring:message code="idgen.general.for"/> ${source.identifierType.name}
</h3>

<frm:form modelAttribute="source" method="post" action="saveIdentifierSource.form">
	<frm:errors path="*" cssClass="error"/><br/>
	<table>
		<tr>
			<th align="right">
				<span class="requiredField">*</span>
				<spring:message code="general.name" />:
			</th>
			<td><frm:input path="name" size="50" /><frm:errors path="name" cssClass="error" /></td>
		</tr>
		<tr>
			<th align="right" valign="top"><spring:message code="general.description" />:</th>
			<td><frm:textarea path="description" cols="50" rows="2" /><frm:errors path="description" cssClass="error" /></td>
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
		
		<c:if test="${source['class'].name == 'org.openmrs.module.idgen.SequentialIdentifierGenerator'}">
			<tr>
				<th align="right">
					<span class="requiredField">*</span>
					<spring:message code="idgen.baseCharacterSet" />:
				</th>
				<td><frm:input path="baseCharacterSet" size="80" /><frm:errors path="baseCharacterSet" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right" valign="top">
					<span class="requiredField">*</span>
					<spring:message code="idgen.firstIdentifierBase" />:
				</th>
				<td>
					<c:choose>
						<c:when test="${source.initialized}">
							<frm:hidden path="firstIdentifierBase"/>
							${source.firstIdentifierBase}
							<spring:message code="idgen.inUseUnableToModify" />
						</c:when>
						<c:otherwise>
							<frm:input path="firstIdentifierBase" size="20" /><frm:errors path="firstIdentifierBase" cssClass="error" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.prefix" />:</th>
				<td><frm:input path="prefix" size="10" /><frm:errors path="prefix" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.suffix" />:</th>
				<td><frm:input path="suffix" size="10" /><frm:errors path="suffix" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.minLength" />:</th>
				<td><frm:input path="minLength" size="10" /><frm:errors path="minLength" cssClass="error" /></td>
			</tr>
            <tr>
                <th align="right"><spring:message code="idgen.maxLength" />:</th>
                <td><frm:input path="maxLength" size="10" /><frm:errors path="maxLength" cssClass="error" /></td>
            </tr>
        </c:if>
		<c:if test="${source['class'].name == 'org.openmrs.module.idgen.RemoteIdentifierSource'}">
			<tr>
				<th align="right">
					<span class="requiredField">*</span>
					<spring:message code="idgen.url" />:
				</th>
				<td>
					<frm:input path="url" size="50" /><frm:errors path="url" cssClass="error" />
				</td>
			</tr>
            <tr>
                <th align="right"><spring:message code="idgen.user" />:</th>
                <td><frm:input path="user" size="50" /><frm:errors path="user" cssClass="error" /></td>
            </tr>
            <tr>
                <th align="right"><spring:message code="idgen.password" />:</th>
                <td><frm:password path="password" size="20" /><frm:errors path="password" cssClass="error" /></td>
            </tr>
		</c:if>
		<c:if test="${source['class'].name == 'org.openmrs.module.idgen.IdentifierPool'}">
			<tr>
				<th align="right"><spring:message code="idgen.poolSource" />:</th>
				<td>
					<c:choose>
						<c:when test="${!empty otherCompatibleSources}">
							<frm:select path="source">
								<frm:option value=""></frm:option>
								<frm:options items="${otherCompatibleSources}" itemValue="id" itemLabel="name"/>
							</frm:select>
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
					<frm:radiobutton path="sequential" value="${false}" /> <spring:message code="idgen.random" />
					<frm:radiobutton path="sequential" value="${true}" /> <spring:message code="idgen.sequential" />
					<frm:errors path="sequential" cssClass="error" /></td>
			</tr>
            <tr>
                <th align="right"><spring:message code="idgen.scheduledFill" />:</th>
                <td>
                    <frm:radiobutton path="refillWithScheduledTask" value="${true}" /> <spring:message code="idgen.scheduledFill.scheduled" />
                    <frm:radiobutton path="refillWithScheduledTask" value="${false}" /> <spring:message code="idgen.scheduledFill.onDemand" />
                    <frm:errors path="refillWithScheduledTask" cssClass="error" /></td>
            </tr>
            <tr>
				<th align="right"><spring:message code="idgen.batchSize" />:</th>
				<td><frm:input path="batchSize" size="50" /><frm:errors path="batchSize" cssClass="error" /></td>
			</tr>
			<tr>
				<th align="right"><spring:message code="idgen.minPoolSize" />:</th>
				<td><frm:input path="minPoolSize" size="50" /><frm:errors path="minPoolSize" cssClass="error" /></td>
			</tr>
		</c:if>
	</table>
	<br/>
	<input type="submit" value="<spring:message code="general.save" />"/>
	<input type="button" value="<spring:message code="general.cancel" />" onclick="document.location.href='manageIdentifierSources.form';"/>
</frm:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>