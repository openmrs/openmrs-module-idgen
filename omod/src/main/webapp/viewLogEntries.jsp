<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Identifier Sources" otherwise="/login.htm" redirect="/module/idgen/viewLogEntries.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/idgen/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/idgen/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/moduleResources/idgen/page.css"/>
<openmrs:htmlInclude file="/moduleResources/idgen/table.css"/>

<style>
	#logEntryTable th, td {text-align:left; padding-right:10px; white-space:nowrap;}
	#searchTable th, td {text-align:left; padding-right:10px; white-space:nowrap;}
</style>


<script type="text/javascript" charset="utf-8">

	var $j = jQuery.noConflict(); 

	$j(document).ready(function() {
		$j("#logEntryTable").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		} );
	} );	
</script>

<b class="boxHeader"><spring:message code="idgen.logSearchOptions" /></b>
<div class="box">
	<form method="get">
		<table width="100%" id="searchTable">
			<tr>
				<th><spring:message code="idgen.source"/>:</th>
				<td>
					<select name="source">
						<option value=""></option>
						<c:forEach items="${identifierSources}" var="idSource">
							<option value="${idSource.id}"<c:if test="${source == idSource}"> selected</c:if>>${idSource.name}</option>
						</c:forEach>
					</select>
				</td>
				<th><spring:message code="idgen.identifierContains"/>:</th>
				<td width="100%"><input type="text" name="identifier" value="${identifier}"/></td>
			</tr>
			<tr>
				<th><spring:message code="idgen.generatedBetween"/>:</th>
				<td>
					<openmrs:fieldGen type="java.util.Date" formFieldName="fromDate" val="${fromDate}" />
					-
					<openmrs:fieldGen type="java.util.Date" formFieldName="toDate" val="${toDate}" />
				</td>
				<th><spring:message code="idgen.commentContains"/>:</th>
				<td width="100%"><input type="text" name="comment" value="${comment}"/></td>
			</tr>
			<tr>
				<th><spring:message code="idgen.generatedBy"/>:</th>
				<td><openmrs_tag:userField formFieldName="generatedBy" initialValue="${generatedBy.userId}"/></td>
				<th></th><td></td>
			</tr>
			<tr><td colspan="4"><input type="submit" name="action" value="<spring:message code="general.search"/>"/></td></tr>
		</table>
	</form>
</div>
<br/>
<b class="boxHeader"><spring:message code="idgen.viewLogEntries" /></b>
<div class="box">
	<table id="logEntryTable" width="100%">
		<thead>
			<tr>
				<th><spring:message code="idgen.sourceName"/></th>
				<th><spring:message code="idgen.identifier"/></th>
				<th><spring:message code="idgen.dateGenerated"/></th>
				<th><spring:message code="idgen.generatedBy"/></th>
				<th width="100%"><spring:message code="idgen.comment"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty logEntries}">
				<c:forEach items="${logEntries}" var="entry" varStatus="entryStatus">
					<tr>
						<td>${entry.source.name}</td>
						<td>${entry.identifier}</td>
						<td><openmrs:formatDate date="${entry.dateGenerated}"/></td>
						<td>${entry.generatedBy.personName}</td>
						<td>${entry.comment}</td>
					</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>
</div>

<br/>

<%@ include file="/WEB-INF/template/footer.jsp"%>