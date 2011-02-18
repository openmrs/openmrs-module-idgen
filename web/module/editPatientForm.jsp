<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Add Patients" otherwise="/login.htm" redirect="/admin/patients/shortPatientForm.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<script type="text/javascript">
	//variable to cache the id of the checkbox of the selected preferred patientIdentifier
	var prefIdentifierElementId = null;
	var numberOfClonedElements = 0;				
	var defaultLocationId = "${defaultLocation.locationId}";
	function addIdentifier(initialIdentifierSize, newIdentifierTypeSelectorObj) {		
		if (newIdentifierTypeSelectorObj) {
			var index = initialIdentifierSize+numberOfClonedElements;
			var tbody = document.getElementById('identifiersTbody');
			var row = document.getElementById('newIdentifierRow');
			var newrow = row.cloneNode(true);
			newrow.style.display = "";
			newrow.id = 'identifiers[' + index + ']';
			tbody.appendChild(newrow);			
			
			<openmrs:forEachRecord name="patientIdentifierType">
				if (newIdentifierTypeSelectorObj.value == '${record.patientIdentifierTypeId}') {
					<c:if test="${!empty autoGenerationOptions}">
						var autoGenOption = getElementsByClass(newrow, 'autoGenerationOption', 'input');
						var autoGenHidden = getElementsByClass(newrow, 'autoGenerationHidden', 'span');
						var idInputField = getElementsByClass(newrow, 'identifierInput', 'input');
						
						<c:set var="source" value="${autoGenerationOptions[record].source}"/>
						if ('${source}' == '') {
							$j(autoGenOption).hide();
							$j(autoGenHidden).show();
						}
						else {
							$j(autoGenOption).show();
							$j(autoGenOption).val("${source.id}");
							$j(autoGenHidden).hide();
						}
						if ('${autoGenerationOptions[record].manualEntryEnabled}' == 'false') {
							$j(idInputField).attr("readOnly", "true");
							$j(idInputField).addClass("readOnlyInput");
						}
					</c:if>
				}
			</openmrs:forEachRecord>			
						
			var inputs = newrow.getElementsByTagName("input");
			for (var x = 0; x < inputs.length; x++) {				
				if(inputs[x]){
					var input = inputs[x];
					if(input.type == 'text'){
						if (input.name == 'identifier')
							input.name = 'identifiers[' + index + '].identifier';											
						else if(input.name == 'identifierTypeName')
							input.value = newIdentifierTypeSelectorObj.options[newIdentifierTypeSelectorObj.selectedIndex].text;						
					}
					else if (input.type == 'radio' && input.name == 'preferred') {
						input.name = 'identifiers[' + index + '].preferred';
						input.id = 'identifiers[' + index + '].preferred';
					}else if (input && input.name == 'newIdentifier.voided' && input.type == 'checkbox') {
						//set the attributes of the corresponding hidden checkbox for voiding/unvoiding new identifiers
						input.name = 'identifiers[' + index + '].voided';
						input.id = 'identifiers[' + index + '].isVoided';
					}else if(input.type == "hidden" && input.name == 'identifierType'){
						input.name = 'identifiers[' + index + '].identifierType';
						input.value = newIdentifierTypeSelectorObj.value;					
					}else if (input && input.name == 'closeButton' && input.type == 'button') {
						//set the onclick event for this identifier's remove button,
						//so that we check the corresponding hidden checkbox to mark a removed identifier
						$j(input).click(function(){
							removeRow(this, 'identifiers[' + index + '].isVoided');
						});
					}
				}
			}
			
			var selects = newrow.getElementsByTagName("select");
			for (var i in selects) {
				var select = selects[i];
				if (select && select.name == "location") {
					select.name = 'identifiers[' + index + '].location';
				}
			}
			
			//reset the type selector to show "Add..." option
			$j("#newIdentifierTypeSelector").val(0);	
			numberOfClonedElements++;
		}
		
	}
	
	function updateAge() {
		var birthdateBox = document.getElementById('birthdate');
		var ageBox = document.getElementById('age');
		try {
			var birthdate = parseSimpleDate(birthdateBox.value, '<openmrs:datePattern />');
			var age = getAge(birthdate);
			if (age > 0)
				ageBox.innerHTML = "(" + age + ' <spring:message code="Person.age.years"/>)';
			else if (age == 1)
				ageBox.innerHTML = '(1 <spring:message code="Person.age.year"/>)';
			else if (age == 0)
				ageBox.innerHTML = '( < 1 <spring:message code="Person.age.year"/>)';
			else
				ageBox.innerHTML = '( ? )';
			ageBox.style.display = "";
		} catch (err) {
			ageBox.innerHTML = "";
			ageBox.style.display = "none";
		}
	}
	
	function updateEstimated() {
		var input = document.getElementById("birthdateEstimatedInput");
		if (input) {
			input.checked = false;
			input.parentNode.className = "";
		}
		else
			input.parentNode.className = "listItemChecked";
	}
	
	// age function borrowed from http://anotherdan.com/2006/02/simple-javascript-age-function/
	function getAge(d, now) {
		var age = -1;
		if (typeof(now) == 'undefined') now = new Date();
		while (now >= d) {
			age++;
			d.setFullYear(d.getFullYear() + 1);
		}
		return age;
	}
	
	function removeRow(btn, checkBoxId) {
		var parent = btn.parentNode;
		while (parent.tagName.toLowerCase() != "tr")
			parent = parent.parentNode;
		
		parent.style.display = "none";		
		if(checkBoxId && document.getElementById(checkBoxId)){
			document.getElementById(checkBoxId).checked = true;
			document.getElementById(checkBoxId).value = true;
		}
	}
	
	function removeHiddenRows() {
		
		var rows = document.getElementsByTagName("TR");
		var i = 0;
		while (i < rows.length) {
			//donot remove the hidden rows if they are for voided identifiers
			if (rows[i].id.startsWith('newIdentifierRow')) {
				rows[i].parentNode.removeChild(rows[i]);
			}
			else {
				i = i + 1;
			}
		}
	}	

	function autogenClicked(element) {
		var txtNode=element.parentNode.parentNode.getElementsByTagName('input')[3];
		if (element.checked) {
			txtNode.style.color = 'blue';
			txtNode.readOnly = true;
			var sourceId = $j(element).val();
			var pId = '${patient.patientId}';
			var comment = '${patient.patientId == null ? "New Patient" : "Edit Patient "}' + pId;
			$j.get("${pageContext.request.contextPath}/module/idgen/generateIdentifier.form?source="+sourceId+"&comment="+comment, function(data){
				$j(txtNode).val(data);
			});
		}
		else {
			txtNode.style.color = 'black';
			txtNode.readOnly = $j(txtNode).hasClass("readOnlyInput");
			txtNode.value = '';
		}
	}

	function getElementsByClass(node,searchClass,tag) {
		var classElements = new Array();
		var els = node.getElementsByTagName(tag);
		var elsLen = els.length;
		var pattern = new RegExp('(^|\\s)' + searchClass + '(\\s|$)');
		for (i = 0, j = 0; i < elsLen; i++) {
			if ( pattern.test(els[i].className) ) {
				classElements[j] = els[i];
				j++;
			}
		}
		return classElements;
	}
	
	//TODO i can't seem to references to this function, probabaly it should be removed
	function changeTextBox2Hidden( _el ) {
		var _parent = _el.parentNode;
		_parent.removeChild( _el );
		if ( _el.type == 'text' ) {
			_parent.innerHTML = '<label><spring:message code="idgen.autoGenerateLabel"/></label><input type=\"hidden\" name=\"' + _el.name + '\" value=\"TEMPID_WILL_BE_REPLACED-8\" />';
		} else {
			_parent.innerHTML = '<input type=\"text\" name=\"' + _el.name + '\" value=\"\" />';
		}
		_el = null;
	}
	
	/**
	 * Unchecks the current preferred patientIdentifier and checks the newly selected one
	 * whenever a user clicks the radio buttons for the patientidentifiers.
	 * @param radioElement the id of the radioButton for the selected identifier checkbox
	 */
	function updatePreferred(radioElement){	
		if(prefIdentifierElementId && document.getElementById(prefIdentifierElementId))
			document.getElementById(prefIdentifierElementId).checked = false;
		
		radioElement.checked = true;		
		setPrefIdentifierElementId(radioElement.id);
	}

    /**
	 * Caches the id of the checkbox of the selected preferred patientIdentifier
	 *	 
	 * @param elementId the id of the radioButton for the selected identifier checkbox
	 */	
	function setPrefIdentifierElementId(elementId){
		prefIdentifierElementId = elementId;			
	}

	/**
	 * Utility function that checks if a given string starts with a specified string	 
	 *
	 * @param radioElement the radioButton for the selected identifier checkbox
	 */
	String.prototype.startsWith = function(prefix) {
	    return this.indexOf(prefix) === 0;
	}

	function voidedBoxClicked(chk) {
		//do nothing
	}

	function preferredBoxClick(obj) {
		//do nothing
	}
	
</script>

<style>
	th { text-align: left } 
	th.headerCell {
		border-top: 1px lightgray solid; 
		xborder-right: 1px lightgray solid
	}
	td.inputCell {
		border-top: 1px lightgray solid;
		}
		td.inputCell th {
			font-weight: normal;
		}
	.lastCell {
		border-bottom: 1px lightgray solid;
	}
	.readOnlyInput {
		color: blue; background-color:lightgray;
	}	
	INPUT.identifierTypeName{
		border:none;
	}
</style>

<openmrs:globalProperty key="use_patient_attribute.tribe" defaultValue="false" var="showTribe"/>
<openmrs:globalProperty key="use_patient_attribute.mothersName" defaultValue="false" var="showMothersName"/>

<spring:hasBindErrors name="patientModel">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/><!-- ${fn:replace(error, '--', '\\-\\-')} -->
		</c:forEach>
	</div>
</spring:hasBindErrors>

<form:form  name="patientForm" method="post" action="shortPatientForm.form" onsubmit="removeHiddenRows()" modelAttribute="patientModel">
	<c:if test="${patientModel.patient.patientId == null}"><h2><spring:message code="Patient.create"/></h2></c:if>
	<c:if test="${patientModel.patient.patientId != null}"><h2><spring:message code="Patient.edit"/></h2></c:if>

	<c:if test="${patientModel.patient.patientId != null}">
		<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${patientModel.patient.patientId}">
			<spring:message code="patientDashboard.viewDashboard" />
		</a>
		<br />
	</c:if>
	
	<br/>
	
	<table cellspacing="0" cellpadding="7">
	<tr>	
		<th class="headerCell"><spring:message code="Person.name"/></th>
		<td class="inputCell">
			<table cellspacing="2">
				<thead>
					<openmrs:portlet url="nameLayout" id="namePortlet" size="columnHeaders" parameters="layoutShowTable=false|layoutShowExtended=false" />
				</thead>
				<spring:nestedPath path="personName">
					<openmrs:portlet url="nameLayout" id="namePortlet" size="inOneRow" parameters="layoutMode=edit|layoutShowTable=false|layoutShowExtended=false" />
				</spring:nestedPath>
			</table>
		</td>
	</tr>
	<tr>
		<th class="headerCell"><spring:message code="PatientIdentifier.title.endUser"/></th>
		<td class="inputCell">
			<table id="identifiers" cellspacing="2">
				<tr>
					<td></td>
					<c:if test="${!empty autoGenerationOptions}">
						<td><spring:message code="idgen.autoGenerate"/>&nbsp;</td>
					</c:if>
					<td><spring:message code="PatientIdentifier.identifier"/></td>
					<td><spring:message code="PatientIdentifier.location.identifier"/></td>
					<td><spring:message code="general.preferred"/></td>
					<td></td>
				</tr>
				<tbody id="identifiersTbody">
					<c:if test="${fn:length(patientModel.identifiers) > 0}">
					<c:forEach var="id" items="${patientModel.identifiers}" varStatus="varStatus">
					<spring:nestedPath path="identifiers[${varStatus.index}]">
					<tr id="existingIdentifiersRow[${varStatus.index}]">
					<td valign="top">
						<input name="identifierTypeName" class="identifierTypeName" value="${patientModel.identifiers[varStatus.index].identifierType.name}" />
					</td>
					<c:if test="${!empty autoGenerationOptions}">
							<td valign="top" align="center">
								<span class="autoGenerationHidden"><spring:message code="idgen.notApplicable" /></span>								
							</td>
					</c:if>
					<td valign="top">						
						<spring:bind path="identifier">
						<input class="identifierInput" type="text" size="20" name="${status.expression}" value="${status.value}" />					
						</spring:bind>
					</td>
					<td valign="top">
						<form:select path="location">
							<form:option value=""></form:option>
							<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
						</form:select>
					</td>
					<td valign="middle" align="center">
						<spring:bind path="preferred">
						<input type="hidden" name ="_${status.expression}" value="${status.value}"/>
						<input id="${status.expression}" type="radio" name="${status.expression}" value="true" onclick="updatePreferred(this)" <c:if test="${status.value}">checked=checked</c:if> />
						<c:if test="${status.value}">
							<script type="text/javascript">
								setPrefIdentifierElementId("${status.expression}");
							</script>
						</c:if>
						</spring:bind>						
					</td>
					<td valign="middle">
						<spring:bind path="voided">
						<input type="hidden" name="_${status.expression}" value=""/>		
						<input id="identifiers[${varStatus.index}].isVoided" type="checkbox" name="${status.expression}" value="false" style="display:none"/>						
						<input type="button" name="closeButton" onClick="return removeRow(this, 'identifiers[${varStatus.index}].isVoided');" class="closeButton" value='<spring:message code="general.remove"/>'/>
						</spring:bind>
					</td>
					</tr>
					</spring:nestedPath>
					</c:forEach>
					</c:if>
					<%-- The row from which to clone new identifiers --%>
					<tr id="newIdentifierRow" style="display: none">
					<td valign="top">
						<input type="text" name="identifierTypeName" size="20" class="identifierTypeName" value="" />						
						<input type="hidden" class="identifierTypeHidden" name="identifierType" value="" />
					</td>
					<c:if test="${!empty autoGenerationOptions}">
							<td valign="top" align="center">
								<span class="autoGenerationHidden"><spring:message code="idgen.notApplicable" /></span>
								<input class="autoGenerationOption" type="checkbox" value="" onclick="autogenClicked(this)" />
							</td>
					</c:if>
					<td valign="top">						
						<input class="identifierInput" type="text" size="20" name="identifier" value="" />
					</td>					
					<td valign="top">
						<select name="location">
							<option value=""></option>
							<openmrs:forEachRecord name="location">
								<option value="${record.locationId}"<c:if test="${record == defaultLocation}"> selected="selected"</c:if>>
									${record.name}
								</option>
							</openmrs:forEachRecord>
						</select>
					</td>
					<td valign="middle" align="center">
						<input type="radio" name="preferred" value="true" onclick="updatePreferred(this)" />
					</td>					
					<td valign="middle" align="center">
						<input type="checkbox" name="newIdentifier.voided" value="false" style="display: none"/>
						<input type="button" name="closeButton" onClick="return removeRow(this, null);" class="closeButton" value='<spring:message code="general.remove"/>'/>
					</td>
					</tr>
				</tbody>
			</table>
			<select id="newIdentifierTypeSelector" onchange="addIdentifier(${fn:length(patientModel.identifiers)}, this);">
				<option value=""><spring:message code="general.add"/>...</option>
				<openmrs:forEachRecord name="patientIdentifierType">
					<option value="${record.patientIdentifierTypeId}">
						${record.name}
					</option>
				</openmrs:forEachRecord>
			</select>
		</td>
	</tr>
	<tr>
		<th class="headerCell"><spring:message code="patientDashboard.demographics"/></th>
		<td class="inputCell">
			<table>
				<tr>
					<td><spring:message code="Person.gender"/></td>
					<td><spring:message code="Person.age"/></td>
					<td><spring:message code="Person.birthdate"/> <i style="font-weight: normal; font-size: 0.8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i></td>
					<c:if test="${showTribe == 'true'}">
						<td><spring:message code="Patient.tribe"/></td>
					</c:if>	
				</tr>
				<tr>
					<td style="padding-right: 3em">
						<spring:bind path="patient.gender">
								<openmrs:forEachRecord name="gender">
									<input type="radio" name="${status.expression}" id="${record.key}" value="${record.key}" <c:if test="${record.key == status.value}">checked</c:if> />
										<label for="${record.key}"> <spring:message code="Person.gender.${record.value}"/> </label>
								</openmrs:forEachRecord>
							<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
						</spring:bind>
					</td>
					<td style="padding-right: 3em">
						<span id="age"></span>
					</td>
					<td style="padding-right: 3em">
						<script type="text/javascript">
							function updateEstimated(txtbox) {
								var input = document.getElementById("birthdateEstimatedInput");
								if (input) {
									input.checked = false;
									input.parentNode.className = "";
								}
								else if (txtbox)
									txtbox.parentNode.className = "listItemChecked";
							}
						</script>
						<spring:bind path="patient.birthdate">			
							<input type="text" 
									name="${status.expression}" size="10" id="birthdate"
									value="${status.value}"
									onChange="updateAge(); updateEstimated(this);"
									onClick="showCalendar(this)" />
							<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if> 
						</spring:bind>
						
						<span id="birthdateEstimatedCheckbox" class="listItemChecked" style="padding: 5px;">
							<spring:bind path="patient.birthdateEstimated">
								<label for="birthdateEstimatedInput"><spring:message code="Person.birthdateEstimated"/></label>
								<input type="hidden" name="_${status.expression}">
								<input type="checkbox" name="${status.expression}" value="true" 
									   <c:if test="${status.value == true}">checked</c:if> 
									   id="birthdateEstimatedInput" 
									   onclick="if (!this.checked) updateEstimated()" />
								<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
							</spring:bind>
						</span>
						
						<script type="text/javascript">
							if (document.getElementById("birthdateEstimatedInput").checked == false)
								updateEstimated();
							updateAge();
						</script>
					</td><td>
					<c:if test="${showTribe == 'true'}">
						<td>
							<spring:nestedPath path="patient">

								<%-- 							
									===========================================================================================
																START RESTRICT PERSON TRIBE permission check
									===========================================================================================
									1.  If the "restrict_patient_attribute.tribe" global property is NOT set or is set to 'false' 
										then we display the field as a select box (per usual).
									
									2.  If the "restrict_patient_attribute.tribe" global property is set to 'true', then we 
										check whether the user is authorized to view or edit the tribe attribute.
									  
									NOTE:  	The following code could have been cleaner, but I wanted to separate the logic for 
											restricting tribes from the default behavior in order to make sure that systems 
											that don't care about the tribe permission were	not adversely affected by a bug 
											in the edit tribe restriction code.
								
								--%>		
								
								<openmrs:globalProperty key="restrict_patient_attribute.tribe" var="restrictTribe" defaultValue="false" />								
								<!-- Restrict tribe:  	${restrictTribe} -->
							
								<c:choose>
								
									<%--  Do not restrict tribe field by user permission --%>
									<c:when test="${!restrictTribe}">
										<spring:bind path="tribe">
											<select name="tribe">
												<option value=""></option>
												<openmrs:forEachRecord name="tribe">
													<option value="${record.tribeId}" <c:catch><c:if test="${record.name == status.value || status.value == record.tribeId}">selected</c:if></c:catch>>
														${record.name}
													</option>
												</openmrs:forEachRecord>
											</select>
											<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
										</spring:bind>
									</c:when>
									
									<%-- Restrict the tribe field by user permissions --%>
									<c:otherwise>
										<%-- Check if the user is authorized to edit the tribe or just view the tribe. --%>
										<c:set var="authorized" value="false"/>
										<openmrs:hasPrivilege privilege="Edit Person Tribe">
											<c:set var="authorized" value="true"/>
										</openmrs:hasPrivilege>										
										<!--  Authorized to edit tribe: 	${authorized} -->									

										<c:choose>		
											<%-- The user is authorized to EDIT the tribe attribute, allow edit of tribe. --%>
											<c:when test="${authorized}">										
												<spring:bind path="tribe">
													<select name="tribe">
														<option value=""></option>
														<openmrs:forEachRecord name="tribe">
															<option value="${record.tribeId}" <c:catch><c:if test="${record.name == status.value || status.value == record.tribeId}">selected</c:if></c:catch>>
																${record.name}
															</option>
														</openmrs:forEachRecord>
													</select>
													<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
												</spring:bind>
											</c:when>
											<%-- The user is NOT authorized to EDIT the tribe attribute, show value.--%>
											<c:otherwise>
												<spring:bind path="tribe">
													${status.value}					
												</spring:bind>				
											</c:otherwise>
										</c:choose>										
									</c:otherwise>
								</c:choose>		
								
								
								<%-- ===========================================================================================
																END RESTRICT PERSON TRIBE permission check
									=========================================================================================== --%>							



							</spring:nestedPath>
						</td>
					</c:if>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<th class="headerCell"><spring:message code="Person.address"/></th>
		<td class="inputCell">
			<spring:nestedPath path="personAddress">
				<openmrs:portlet url="addressLayout" id="addressPortlet" size="full" parameters="layoutShowTable=true|layoutShowExtended=false" />
			</spring:nestedPath>
		</td>
	</tr>
	
	<c:forEach var="relationshipMap" items="${relationshipsMap}">
		<c:choose>
			<c:when test="${fn:contains(relationshipMap.key, 'a')}" >
				<tr>
					<th class="headerCell">
						${relationshipMap.value.relationshipType.aIsToB}
					</th>
					<td class="inputCell">						
						<openmrs_tag:personField formFieldName="${relationshipMap.key}" searchLabelCode="Person.find" initialValue="${relationshipMap.value.personA.personId}" linkUrl="" callback="" canAddNewPerson="true" />
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<th class="headerCell">
						${relationshipMap.value.relationshipType.bIsToA}
					</th>
					<td class="inputCell">
						<openmrs_tag:personField formFieldName="${relationshipMap.key}" searchLabelCode="Person.find" initialValue="${relationshipMap.value.personB.personId}" linkUrl="" callback="" canAddNewPerson="true"/>
					</td>
				</tr>	
			</c:otherwise>
		</c:choose>
	</c:forEach>
	
	<c:forEach var="personAttribute" items="${patientModel.personAttributes}" varStatus="varStatus">	
		<c:set var="authorized" value="false" />
		<c:choose>
			<c:when test="${not empty personAttribute.attributeType.editPrivilege}">
				<openmrs:hasPrivilege privilege="${personAttribute.attributeType.editPrivilege.privilege}">
					<c:set var="authorized" value="true" />
				</openmrs:hasPrivilege>
			</c:when>
			<c:otherwise>
				<c:set var="authorized" value="true" />
			</c:otherwise>
		</c:choose>
		
		<tr>
			<th class="headerCell"><spring:message code="PersonAttributeType.${fn:replace(personAttribute.attributeType.name, ' ', '')}" text="${personAttribute.attributeType.name}"/></th>
			<td class="inputCell">
				<c:choose>
					<c:when test="${authorized == true}">
						<spring:nestedPath path="personAttributes[${varStatus.index}]">
						<spring:bind path="personAttributeId">
							<input type="hidden" name="${status.expression}" value="${status.value}"/>
						</spring:bind>
						<spring:bind path="value">
						<openmrs:fieldGen 
							type="${personAttribute.attributeType.format}" 
							formFieldName="${status.expression}"
							val="${personAttribute.hydratedObject}" 
							parameters="optionHeader=[blank]|showAnswers=${personAttribute.attributeType.foreignKey}|isNullable=false" /> <%-- isNullable=false so booleans don't have 'unknown' radiobox --%>
						</spring:bind>
						</spring:nestedPath>
					</c:when>					
				</c:choose>
			</td>
		</tr>	
	</c:forEach>
	<tr>
		<th class="headerCell lastCell"><spring:message code="Person.dead"/></th>
		<td class="inputCell lastCell">
			<spring:message code="Person.dead.checkboxInstructions"/>
			<spring:bind path="patient.dead">
				<input type="hidden" name="_${status.expression}"/>
				<input type="checkbox" name="${status.expression}" 
					   <c:if test="${status.value == true}">checked</c:if>
					   onclick="personDeadClicked(this)" id="personDead"
				/>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
			<script type="text/javascript">
				function personDeadClicked(input) {
					if (input.checked) {
						document.getElementById("deathInformation").style.display = "";
					}
					else {
						document.getElementById("deathInformation").style.display = "none";
						document.getElementById("deathDate").value = "";
						var cause = document.getElementById("causeOfDeath");
						if (cause != null)
							cause.value = "";
					}
				}
			</script>
			<br/>
			<div id="deathInformation">
				<b><spring:message code="Person.deathDate"/>:</b>

				<spring:bind path="patient.deathDate">
					<input type="text" name="${status.expression}" size="10" 
						   value="${status.value}" onClick="showCalendar(this)" 
						   id="deathDate" />
					<i style="font-weight: normal; font-size: 0.8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
				&nbsp; &nbsp; 
				<spring:message code="Person.causeOfDeath"/>
				<openmrs:globalProperty key="concept.causeOfDeath" var="conceptCauseOfDeath" />
				<openmrs:globalProperty key="concept.otherNonCoded" var="conceptOther" />
				<spring:bind path="patient.causeOfDeath">
					<openmrs:fieldGen type="org.openmrs.Concept" formFieldName="patient.causeOfDeath" val="${status.value}" parameters="showAnswers=${conceptCauseOfDeath}|showOther=${conceptOther}|otherValue=${causeOfDeathOther}" />
					<%--<input type="text" name="causeOfDeath" value="${status.value}" id="causeOfDeath"/>--%>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
				<script type="text/javascript">				
					//set up death info fields
					personDeadClicked(document.getElementById("personDead"));
				</script>
			</div>
		</td>
	</tr>
	</table>
	
	<input type="hidden" name="patientId" value="${param.patientId}" />
	
	<br />
	<input type="submit" value="<spring:message code="general.save" />" name="action" id="addButton"> &nbsp; &nbsp; 
	<input type="button" value="<spring:message code="general.back" />" onclick="history.go(-1);">
</form:form>

<script type="text/javascript">
	document.forms[0].elements[0].focus();
	updateAge();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>
