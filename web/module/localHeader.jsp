<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery.cookie.js"/>
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery.ui-1.5/ui/packed/ui.core.packed.js" />
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery.ui-1.5/ui/packed/ui.tabs.packed.js" />
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery.ui-1.5/ui/packed/ui.draggable.packed.js" />
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery.ui-1.5/themes/flora/flora.tabs.css" />
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/page.css"/>
<openmrs:htmlInclude file="/moduleResources/cohortreports/jquery/table.css"/>

<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	<openmrs:hasPrivilege privilege="Edit Patient Searches">
		<li <c:if test='<%= request.getRequestURI().contains("managePatientSearches") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/cohortreports/managePatientSearches.list">
				<spring:message code="cohortreports.patientSearch.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Edit Patient Searches">
		<li <c:if test='<%= request.getRequestURI().contains("manageIndicatorMacros") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/cohortreports/manageIndicatorMacros.list">
				<spring:message code="cohortreports.indicatorMacros.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Reports">
		<li <c:if test='<%= request.getRequestURI().contains("manageCohortReports") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/cohortreports/manageCohortReports.list">
				<spring:message code="cohortreports.cohortReport.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Reports">
		<li <c:if test='<%= request.getRequestURI().contains("manageCohortReportExports") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/cohortreports/manageCohortReportExports.list">
				<spring:message code="cohortreports.cohortreportexports.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>