jQuery(document).ready(function() {
	
	var patientId = jQuery("input[name='patientId']").val();
	var jsonData;
	var idTypesAdded = [];
	
	// Get data from the server for building out new data into the table
	jQuery.getJSON("../../module/idgen/editPatientIdentifiers.form", { patientId: patientId }, function(data) {
		
		jsonData = data;

		// Remove the add identifier button
		jQuery("#identifiers").siblings(".smallButton:last").remove();
		
		// Re-organize the headers of the table so that type comes first and identifier comes third
		jQuery("#identifiers tr:first").each(function () {
			var identifierText = jQuery(this).children("td:eq(0)").html();
			var identifierTypeText = jQuery(this).children("td:eq(2)").html();
			jQuery(this).children("td:eq(0)").html(identifierTypeText);
			jQuery(this).children("td:eq(2)").html(identifierText);
	    });
		
		// For each identifier row added by default on the page, re-organize the cells and add the generation cell. Remove any with no identifiers
		jQuery("#identifiersTbody").children("tr").each(function () {
			if (this.id != 'identifierRow' && jQuery(this).find("input[name='identifier']").val() == '') {
				jQuery(this).remove();
			};
			var identifierTypeId = jQuery(this).find("select[name='identifierType'] option:selected").val();
			var identifierTypeName = jQuery(this).find("select[name='identifierType'] option:selected").html();
			idTypesAdded.push(identifierTypeId);
			jQuery(this).children("td:eq(1)").remove();
			jQuery(this).children("td:eq(0)").before('<td valign="top"><span class="identifierTypeName">'+identifierTypeName+'</span><input type="hidden" class="identifierTypeHidden" name="identifierType" value="'+identifierTypeId+'"/></td>');		
			jQuery(this).children("td:eq(2)").addClass("identifierInput");
			if (jQuery("#idgenColumnHeader").length > 0) {
				jQuery(this).children("td:eq(1)").before('<td valign="top" align="center"><span class="autoGenerationHidden">N/A</span><input class="autoGenerationOption" type="checkbox" value=""/></td>');
			}
			addIdentifierRow(jsonData.allIdentifiers[identifierTypeId], patientId, this);
		});
	
		// Create and insert select list for adding new identifiers
		jQuery("#identifiers").parent().append('<select id="newIdentifierTypeSelector" name="newIdentifierTypeSelector"><option value="">'+data.translations.add+'</option></select>');
		jQuery.each(data.allIdentifiers, function(i, item) {
			jQuery("#newIdentifierTypeSelector").append('<option value="' + item.typeId + '">'+sanitizeHtml(item.typeName)+'</option>');
		});
		jQuery("#newIdentifierTypeSelector").change(function(event) {
			var v = jQuery(this).val();
			if (v != '') {
				addIdentifierRow(data.allIdentifiers[v], patientId);
			}
			jQuery(this).val('');
		});
		
		// Run through any default identifiers.  If one isn't already in the page of a particular type, then add a blank row
		jQuery.each(data.defaultIdentifiers, function(i, item) {
			var found = false;
			for (var i=0; i<idTypesAdded.length; i++) {
				if (idTypesAdded[i] == item.typeId) {
					found = true;
				}
			};
			if (!found) {
				addIdentifierRow(item, patientId);
			}
		});
	});
});

// item: typeId, typeName, manualEntryEnabled, autogenerationEnabled, sourceId, location, identifier, preferred, saved
function addIdentifierRow(item, patientId, row) {
	if (!row) {
		addIdentifier(item.identifier, item.typeId, item.location, item.preferred, item.saved);
		row = jQuery("#identifiersTbody tr:last");
	}

	jQuery(row).find(".identifierTypeName").html(item.typeName);
	jQuery(row).find(".identifierTypeHidden").val(item.typeId);
	var identifierInput = jQuery(row).find("input[name='identifier']");

	if (!item.sourceId || item.sourceId == '' || jQuery(identifierInput).val() != '') {
		jQuery(row).find(".autoGenerationOption").hide();
		jQuery(row).find(".autoGenerationHidden").show();
	}
	else {
		jQuery(row).find(".autoGenerationOption").show();
		jQuery(row).find(".autoGenerationHidden").hide();
		
		if (item.sourceId != '') {
			
			jQuery(row).find(".autoGenerationOption").click(function(event) {
				if (this.checked) {
					var comment = patientId ? 'Edit Patient' : 'New Patient';
					jQuery.get("../../module/idgen/generateIdentifier.form?source="+item.sourceId+"&comment="+comment, function(data){
						jQuery(identifierInput).val(data.identifiers[0]).addClass("readOnlyInput").attr("readOnly", "true");
					});
				}
				else {
					jQuery(identifierInput).removeClass("readOnlyInput").val("").removeAttr("readOnly");
				}
			});
		}
	}
	if (!item.manualEntryEnabled) {
		jQuery(identifierInput).attr("readOnly", "true").addClass("readOnlyInput");
	}
}
