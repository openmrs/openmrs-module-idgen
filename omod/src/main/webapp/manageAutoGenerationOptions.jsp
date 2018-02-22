<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Auto Generation Options" otherwise="/login.htm" redirect="/module/idgen/manageautoGenerationOptions.form" />

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
                <th><spring:message code="Location.location"/></th>
				<th><spring:message code="idgen.sourceName"/></th>
				<th><spring:message code="idgen.manualEntryEnabled"/></th>
				<th><spring:message code="idgen.automaticGenerationEnabled"/></th>
                <th>&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${optionMap}" var="entry" varStatus="entryStatus">
                <c:forEach items="${entry.value}" var="option">
                    <tr>
                        <td>
                            <a href="editAutoGenerationOption.form?autoGenerationOption=${option.id}"><c:out value="${entry.key.name}"/></a>
                        </td>

                        <c:choose>
                            <c:when test="${!empty option.location}">
                                <td><c:out value="${option.location.name}"/></td>
                            </c:when>
                            <c:otherwise>
                                <td><spring:message code="general.none"/></td>
                            </c:otherwise>
                        </c:choose>

                        <td>${option.source.name}</td>
                        <td><spring:message code="general.${option.manualEntryEnabled}"/></td>
                        <td><spring:message code="general.${option.automaticGenerationEnabled}"/></td>
                        <td>
                            <a href="deleteAutoGenerationOption.form?autoGenerationOption=${option.id}">
                                <spring:message code="idgen.delete"/>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
			</c:forEach>
		</tbody>
	</table>
</div>

<br/>

<b class="boxHeader"><spring:message code="idgen.setupNewAutoGenerationOption"/></b>
<div class="box">
    <form action="editAutoGenerationOption.form" method="get">
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
        </table>
        <input type="submit" value="<spring:message code="general.add"/>"/>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>