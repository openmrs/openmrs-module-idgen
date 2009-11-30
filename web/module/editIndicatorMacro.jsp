<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/cohortreports/manageIndicatorMacros.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/interface/DWRCohortService.js" />

<script type="text/javascript">

	var $j = jQuery.noConflict();
	
	$j(document).ready(function() {
		$j("#shortcuts").draggable();
		setResizingTextArea(".rowQuery", 1, 60, 5, 60);

		$j('#deleteButton').click(function(event){
			if (confirm('Are you sure you want to delete this Indicator Macro?')) {
				document.location.href='saveIndicatorMacro.form?originalName=${name}';
			}
		} );
 	});
 	
 	function setResizingTextArea(el, blurRows, blurCols, focusRows, focusCols) {
 		$j(el)
 			.each(function() { $(this).rows = blurRows; $(this).cols = blurCols; })
 			.focus(function() { $(this).rows = focusRows; $(this).cols = focusCols; })
 			.blur(function() { $(this).rows = blurRows; $(this).cols = blurCols; });
 	} 

	var shortcutTarget = null;
	
	function showShortcuts(target) {
		shortcutTarget = target;
	}
	
	function handleShortcut(text) {
		if (shortcutTarget != null) {
			shortcutTarget.focus();
			if (shortcutTarget.value != '')
				shortcutTarget.value += ' ';
			shortcutTarget.value += text;
		}
	}

	function handleParameterShortcut(text) {
		if (shortcutTarget != null) {
			shortcutTarget.focus();
			if (shortcutTarget.value != '') {
				shortcutTarget.value = shortcutTarget.value.replace('?',text);
			}
		}
	}
	
	function testQuery(button) {
		$j(button.parentNode.parentNode).find("textarea").each(
			function() {
				var el = document.getElementById('cohortResult');
				cohort_setPatientIds(null);
				showDiv('cohortResult');
				DWRCohortService.evaluateCohortDefinition($(this).value, null, showCohortResult);
			}
		);
	}
	
	function showCohortResult(cohort) {
		var el = document.getElementById('cohortResult');
		cohort_setPatientIds(cohort.commaSeparatedPatientIds);
		showDiv('cohortResult');
	}
</script>
<h2>
	<c:if test="${empty name}"><spring:message code="cohortreports.indicatorMacros.enter"/></c:if>
	<c:if test="${!empty name}"><spring:message code="cohortreports.indicatorMacros.edit"/>: ${name}</c:if>
</h2>


<div id="cohortResult" style="display: none; position: absolute; z-index: 5; border: 2px black solid; background-color: #e0f0d0">
	<div style="float: right">
		<a onClick="hideDiv('cohortResult')">[X]</a>
	</div>
	<openmrs:portlet url="cohort" parameters="linkUrl="/>
	<b><u><spring:message code="cohortreports.cohortReport.preview" /></u></b>
</div>
<form method="post" action="saveIndicatorMacro.form">
	<table><tr valign="top">
		<td>
			<c:if test="${param.success == 'false'}"><span style="font-weight:bold; color:red;">
				The macro specification is invalid.  Please fix and try again.
			</span><br/></c:if>
			<span style="color:blue;"><spring:message code="cohortreports.cohortReport.help.indicatorMacros" /></span><br/>
			<b>Name</b><br/>
			<input type="hidden" name="originalName" value="${name}"/>
			<input type="text" name="name" value="${name}"/>
			<br/><br/>
			<b>Specification</b><br/>
			<textarea name="specification" rows="5" cols="60" class="rowQuery" onFocus="showShortcuts(this);">${specification}</textarea>
		</td>
		<td width="*">

			<div id="shortcuts" style="border: 1px black solid; padding: 5px; background-color: #f0f0f0">
				<span style="font-weight:bold;"><spring:message code="cohortreports.cohortReport.operators" />:<br/></span><br/>
				<span class="button" onClick="handleShortcut('(')">&nbsp;(&nbsp;</span>
				<span class="button" onClick="handleShortcut(')')">&nbsp;)&nbsp;</span>
				<span class="button" onClick="handleShortcut('AND')"><spring:message code="cohortreports.cohortReport.operator.and" /></span>
				<span class="button" onClick="handleShortcut('OR')"><spring:message code="cohortreports.cohortReport.operator.or" /></span>
				<span class="button" onClick="handleShortcut('NOT')"><spring:message code="cohortreports.cohortReport.operator.not" /></span>
				<br/>
				
				<br/>
				<b><spring:message code="cohortreports.commonParameters"/>:</b>
				<c:forEach var="p" items="startDate,endDate,location">
					<br/><a class="shortcut" onClick="handleParameterShortcut('${p}')" title="${p}">${p}</a>
				</c:forEach>
				<br/>
					
				<c:if test="${fn:length(macros) > 0}">
					<br/>
					<b><spring:message code="cohortreports.indicatorMacros"/>:</b>
					<c:forEach var="macro" items="${macros}">
						<br/><a class="shortcut" onClick="handleShortcut('${macroPrefix}${macro.key}${macroSuffix}')" title="${macro.value}">${macro.key}</a>
					</c:forEach>
					<br/>
				</c:if>
		
				<c:if test="${fn:length(patientSearches) > 0}">
					<br/>
					<b><spring:message code="cohortreports.cohortReport.savedPatientSearches" />:</b>
					<c:forEach var="search" items="${patientSearches}">
						<br/><a class="shortcut" onClick="handleShortcut('[${search.key}]')" title="${search.value}">${search.key}</a>
					</c:forEach>
					<br/>
				</c:if>
			</div>
		</td>
	</tr></table>
	
	<input type="submit" value="<spring:message code="general.save"/>"/>
	<input type="button" value="<spring:message code="general.cancel"/>" onclick="document.location.href='manageIndicatorMacros.list';"/>
	<c:if test="${!empty name}">
		<input type="button"" id="deleteButton" value='<spring:message code="general.delete"/>'">
	</c:if>
</form>
<br/>

<%@ include file="/WEB-INF/template/footer.jsp" %>