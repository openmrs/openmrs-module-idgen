<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	<openmrs:hasPrivilege privilege="Manage Identifier Sources">
		<li <c:if test='<%= request.getRequestURI().contains("manageIdentifierSources") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/idgen/manageIdentifierSources.list">
				<spring:message code="idgen.manage.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Auto Generation Options">
		<li <c:if test='<%= request.getRequestURI().contains("manageAutoGenerationOptions") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/idgen/manageAutoGenerationOptions.list">
				<spring:message code="idgen.autoGenerationOptionHeader"/>
			</a>
		</li>
	</openmrs:hasPrivilege>	
	<openmrs:hasPrivilege privilege="Manage Identifier Sources">
		<li <c:if test='<%= request.getRequestURI().contains("viewLogEntries") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/idgen/viewLogEntries.list">
				<spring:message code="idgen.viewLogEntries"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Identifier Types">
		<li <c:if test='<%= request.getRequestURI().contains("patientIdentifierType") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/admin/patients/patientIdentifierType.list">
				<spring:message code="PatientIdentifierType.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>