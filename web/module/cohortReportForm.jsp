<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/cohortreports/managePatientSearches.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/interface/DWRCohortService.js" />

<script type="text/javascript">

	var $j = jQuery.noConflict();
	
	$j(document).ready(function() {
		$j("#shortcuts").draggable();
		setResizingTextArea(".rowQuery", 1, 60, 5, 60);
		$j("#reportTabs > ul").tabs({ cookie: { expires: 30 } });	
 	});
 	
 	function setResizingTextArea(el, blurRows, blurCols, focusRows, focusCols) {
 		$j(el)
 			.each(function() { $(this).rows = blurRows; $(this).cols = blurCols; })
 			.focus(function() { $(this).rows = focusRows; $(this).cols = focusCols; })
 			.blur(function() { $(this).rows = blurRows; $(this).cols = blurCols; });
 	} 
	
	function deleteTableRow(tableRow) {
		tableRow.parentNode.removeChild(tableRow);
	}
	
	function addAnotherParameter() {
		var row = document.createElement("tr");
		var cell = document.createElement("td");
		var input = document.createElement("input");
		var toFocus = input;
		input.setAttribute("type", "text");
		input.setAttribute("name", "parameterName");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("size", "40");
		input.setAttribute("name", "parameterLabel");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		var sel = document.createElement("select");
		sel.setAttribute("name", "parameterClass");
		var opt = document.createElement("option");
		sel.appendChild(opt);
		<c:forEach var="clazz" items="${parameterClasses}">
			opt = document.createElement("option");
			<c:if test="${param.clazz == clazz}">
				opt.setAttribute("selected", "true");
			</c:if>
			opt.setAttribute("value", "${clazz.name}");
			opt.innerHTML = "${clazz.simpleName}";
			sel.appendChild(opt);
		</c:forEach>
		cell.appendChild(sel);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		var link = document.createElement("span");
		link.className = "voidButton";
		link.setAttribute("onClick", "deleteTableRow(this.parentNode.parentNode)");
		link.innerHTML = "X";
		cell.appendChild(link); 
		row.appendChild(cell);
		
		document.getElementById("parametersTable").appendChild(row);
		toFocus.focus();
	}
	
	function addAnotherRow() {
		var row = document.createElement("tr");
		row.setAttribute("valign", "top");
		var cell = document.createElement("td");
		var input = document.createElement("input");
		var toFocus = input;
		input.setAttribute("type", "text");
		input.setAttribute("name", "rowName");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("size", "40");
		input.setAttribute("name", "rowDescription");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		input = document.createElement("textarea");
		input.setAttribute("class", "rowQuery");
		input.setAttribute("name", "rowQuery");
		input.setAttribute("onFocus", "showShortcuts(this)");
		setResizingTextArea(input, 1, 60, 5, 60);
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		var link = document.createElement("span");
		link.className = "voidButton";
		link.setAttribute("onClick", "deleteTableRow(this.parentNode.parentNode)");
		link.innerHTML = "X";
		cell.appendChild(link); 
		row.appendChild(cell);
		
		document.getElementById("rowsTable").appendChild(row);
		toFocus.focus();
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

	function handleMacroShortcut(text) {
		if (shortcutTarget != null) {
			shortcutTarget.focus();
			if (shortcutTarget.value != '')
				shortcutTarget.value += ' ';
			shortcutTarget.value += '('+text+')';
		}
	}
</script>
<h2>
	<c:if test="${empty command.reportId}"><spring:message code="cohortreports.cohortReport.enter" /></c:if>
	<c:if test="${!empty command.reportId}"><spring:message code="cohortreports.cohortReport.edit" />: ${command.name}</c:if>
</h2>

<spring:hasBindErrors name="command">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<div id="reportTabs">
	<ul>
		<li><a href="#reportTab1"><span style="color:white;"><spring:message code="cohortreports.cohortReport.details" /></span></a></li>
		<c:if test="${!empty command.reportId}">
			<li><a href="#reportTab2"><span style="color:white;"><spring:message code="cohortreports.cohortReport.parameters" /></span></a></li>
			<li><a href="#reportTab3"><span style="color:white;"><spring:message code="cohortreports.cohortReport.indicators" /></span></a></li>
			<openmrs:extensionPoint pointId="org.openmrs.report.cohortReportFormTab" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<li>
						<a href="#reportExtensionTab${extension.tabId}">
							<span style="color:white;"><spring:message code="${extension.tabName}"/></span>
						</a>
					</li>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
		</c:if>
	</ul>
	
	<form method="post">
		<div id="reportTab1">
			<span style="color:blue;">
				<c:if test="${empty command.reportId}"><spring:message code="cohortreports.cohortReport.help.newDetails" /></c:if>
				<c:if test="${!empty command.reportId}"><spring:message code="cohortreports.cohortReport.help.existingDetails" /></c:if>
			</span><br/><br/>
			<table>
				<spring:bind path="command.name">
					<tr>
						<th align="left"><spring:message code="general.name" /></th>
						<td>
							<input type="text" size="40" name="${status.expression}" value="${status.value}"/>
							<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
						</td>
					</tr>
				</spring:bind>
				<spring:bind path="command.description">
					<tr valign="top">
						<th align="left"><spring:message code="general.description" /></th>
						<td>
							<textarea rows="3" cols="60" name="${status.expression}">${status.value}</textarea>
							<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
						</td>
					</tr>
				</spring:bind>
			</table>
			<br/>
			<input type="submit" value="<spring:message code="general.save"/>"/>
			<input type="button" value="<spring:message code="general.cancel"/>" onclick="document.location.href='manageCohortReports.list';"/>
		</div>
		<c:if test="${!empty command.reportId}">
			<div id="reportTab2">
				<spring:bind path="command.parameters">
					<c:if test="${status.errorMessage != ''}"><div class="error">${status.errorMessage}</div></c:if>
				</spring:bind>
				<span style="color:blue;"><spring:message code="cohortreports.cohortReport.help.parameters" /></span><br/><br/>
				<table id="parametersTable">
					<tr>
						<th style="padding-right:20px;"><spring:message code="cohortreports.parameter.name" /></th>
						<th style="padding-right:20px;"><spring:message code="cohortreports.parameter.label" /></th>
						<th style="padding-right:20px;"><spring:message code="cohortreports.parameter.type" /></th>
					</tr>
					<c:forEach var="parameter" items="${command.parameters}">
						<tr>
							<td><input type="text" name="parameterName" value="${parameter.name}"/></td>
							<td><input type="text" size="40" name="parameterLabel" value="${parameter.label}"/></td>
							<td>
								<select name="parameterClass">
									<option value=""></option>
									<c:forEach var="clazz" items="${parameterClasses}">
										<option <c:if test="${parameter.clazz == clazz}">selected="true"</c:if> value="${clazz.name}">
											${clazz.simpleName}
										</option>
									</c:forEach>
								</select>
							</td>
							<td>
								<span class="voidButton" onClick="deleteTableRow(this.parentNode.parentNode)">X</span>
							</td>
						</tr>
					</c:forEach>
				</table>
				<a onClick="addAnotherParameter()"><spring:message code="cohortreports.parameter.add" /></a>
				<br/><br/>
				<input type="submit" value="<spring:message code="general.save"/>"/>
				<input type="button" value="<spring:message code="general.cancel"/>" onclick="document.location.href='manageCohortReports.list';"/>
			</div>
		
			<div id="reportTab3">
			
				<table width="100%"><tr valign="top">
					<th align="left" nowrap>Filter to apply to all indicators:</th>
					<spring:bind path="command.filter">
						<td align="left" width="100%"><textarea name="${status.expression}" class="rowQuery" onFocus="showShortcuts(this)">${status.value}</textarea></td>
						<c:if test="${status.errorMessage != ''}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</table>
				<br/>
				<table width="100%"><tr valign="top">
					<td width="100%">
						<spring:bind path="command.rows">
							<c:if test="${status.errorMessage != ''}"><div class="error">${status.errorMessage}</div></c:if>
						</spring:bind>
						
						<table id="rowsTable" width="100%">
							<tr>
								<th style="text-align:left; border-bottom: 1px solid black; white-space:nowrap; padding-right:20px;">
									<spring:message code="cohortreports.cohortReport.indicatorName" />
									(<spring:message code="cohortreports.cohortReport.indicatorName.example" />)
								</th>
								<th style="text-align:left; border-bottom: 1px solid black; white-space:nowrap; padding-right:20px;">
									<spring:message code="cohortreports.cohortReport.indicatorDescription" />
									(<spring:message code="cohortreports.cohortReport.indicatorDescription.example" />)
								</th>
								<th width="100%" style="text-align:left; border-bottom: 1px solid black; white-space:nowrap; padding-right:20px;">
									<spring:message code="cohortreports.cohortReport.indicatorSpecification" />
									(<spring:message code="cohortreports.cohortReport.indicatorSpecification.example" />)
								</th>
								<th colspan="2" style="text-align:left; border-bottom: 1px solid black; white-space:nowrap;">Actions</th>
							</tr>
							<c:forEach var="row" items="${command.rows}">
								<tr valign="top">
									<td><input type="text" name="rowName" value="${row.name}"/></td>
									<td><input type="text" size="40" name="rowDescription" value="${row.description}"/></td>
									<td><textarea name="rowQuery" class="rowQuery" onFocus="showShortcuts(this)">${row.query}</textarea></td>
									<td align="center"><a href="#" onClick="deleteTableRow(this.parentNode.parentNode)">[X]</a></td>
								</tr>
							</c:forEach>
						</table>
						<a onClick="addAnotherRow()"><spring:message code="cohortreports.cohortReport.indicator.add" /></a>
					</td>
					<td width="*" nowrap>
			
						<div id="shortcuts" style="border: 1px black solid; padding: 5px; background-color: #f0f0f0">
							<span style="font-weight:bold;"><spring:message code="cohortreports.cohortReport.operators" />:<br/></span><br/>
							<span class="button" onClick="handleShortcut('(')">&nbsp;(&nbsp;</span>
							<span class="button" onClick="handleShortcut(')')">&nbsp;)&nbsp;</span>
							<span class="button" onClick="handleShortcut('AND')"><spring:message code="cohortreports.cohortReport.operator.and" /></span>
							<span class="button" onClick="handleShortcut('OR')"><spring:message code="cohortreports.cohortReport.operator.or" /></span>
							<span class="button" onClick="handleShortcut('NOT')"><spring:message code="cohortreports.cohortReport.operator.not" /></span>
							<br/>
								
							<c:if test="${fn:length(macros) > 0}">
								<br/>
								<b><spring:message code="cohortreports.indicatorMacros" />:</b>
								<c:forEach var="macro" items="${macros}">
									<br/><a class="shortcut" onClick="handleMacroShortcut('${macroPrefix}${macro.key}${macroSuffix}')" title="${macro.value}">${macro.key}</a>
								</c:forEach>
								<br/>
							</c:if>
						</div>
					</td>
				</tr></table>
				<input type="submit" value="<spring:message code="general.save"/>"/>
				<input type="button" value="<spring:message code="general.cancel"/>" onclick="document.location.href='manageCohortReports.list';"/>
			</div>
		</c:if>
	</form>
	<c:if test="${!empty command.reportId}">	
		<openmrs:extensionPoint pointId="org.openmrs.report.cohortReportFormTab" type="html">
			<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
				<div id="reportExtensionTab${extension.tabId}">
					<c:choose>
						<c:when test="${extension.portletUrl == '' || extension.portletUrl == null}">
							portletId is null: '${extension.extensionId}'
						</c:when>
						<c:otherwise>
							<openmrs:portlet url="${extension.portletUrl}" id="${extension.tabId}" moduleId="${extension.moduleId}" parameters="reportId=${command.reportId}" />
						</c:otherwise>
					</c:choose>
				</div>
			</openmrs:hasPrivilege>
		</openmrs:extensionPoint>
	</c:if>
</div>
<br/>

<%@ include file="/WEB-INF/template/footer.jsp" %>