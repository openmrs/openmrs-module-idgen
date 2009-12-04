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
</ul>