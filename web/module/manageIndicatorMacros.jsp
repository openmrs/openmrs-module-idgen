<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patient Searches" otherwise="/login.htm" redirect="/module/cohortreports/manageIndicatorMacros.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#macroTable").dataTable( {
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

<h2><spring:message code="cohortreports.indicatorMacros.manage"/></h2>
<a href="editIndicatorMacro.form">Add new...</a>
<br/><br/>
<b class="boxHeader"><spring:message code="cohortreports.indicatorMacros"/></b>
<div class="box">
	<c:choose>
		<c:when test="${empty macroPrefix || empty macroSuffix}">
			<form method="post" action="saveMacroPrefixSuffix.form">
			
				You must specify a macro prefix and macro suffix prior to specifying Indicator Macros.
				This will allow you to refer to Indicator Macros either in a Cohort Report Specification,
				or within another macro, by referring to it as macroPrefix + name + macroSuffix.<br/>
				A typical use is to define <b>macroPrefix</b> as <b>#</b>, <b>macroSuffix</b> as <b>#</b>, 
				and then create a macro named  <b>males</b>.  Then, you could refer to this macro as <b>#males#</b><br/><br/>
				
				Macro Prefix: <input type="text" size="5" name="macroPrefix" value="${macroPrefix}" />
				Macro Suffix: <input type="text" size="5" name="macroSuffix" value="${macroSuffix}" />
				<input type="submit" value="Save"/>
			</form>
			</br/>
		</c:when>
		<c:otherwise>
			<table id="macroTable" width="100%">
				<thead>
					<tr>
						<th align="left"></th>
						<th align="left"><spring:message code="general.name" /></th>
						<th align="left">Specification</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="m" items="${macros}" varStatus="varStatus">
						<tr class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">	
							<td style="padding-left:5px; padding-right:5px;">
								<a href="editIndicatorMacro.form?name=${m.key}">
									<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
								</a>
							</td>
							<td valign="top" style="white-space:nowrap; padding-right:20px; font-weight:bold;">${m.key}</td>
							<td width="100%" valign="top">${m.value}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
