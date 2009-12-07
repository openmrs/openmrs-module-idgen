/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.extension.html;

import org.openmrs.module.Extension;

/**
 * Provides a check
 */
public class CreatePatientIdGenCheckBoxBody extends Extension {

	/**
	 * Extension#getMediaType()
	 */
	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}

	/**
	 * @see Extension#getOverrideContent(String)
	 */
	@Override
	public String getOverrideContent(String bodyContent) {

		StringBuilder sb = new StringBuilder();
		String id = "idgen_checkbox_" + System.currentTimeMillis();
		
		sb.append("<td>");
		sb.append("<input type=\"checkbox\" id="+id+" name=\"AUTO_GEN_ID\" value=\"TEMPID_WILL_BE_REPLACED-8\" onclick=\"");
		sb.append("		var txtNode=this.parentNode.parentNode.getElementsByTagName('input')[0];");
		sb.append("		changeTextBox2Hidden(txtNode);");
		sb.append("\" /> ");
		sb.append("</td>");
		sb.append("<script>");
		sb.append("		if ((getElementsByClass(document.body,'error','div').length>0) { ");
		sb.append("			var txtNode=document.getElementById('"+id+"').parentNode.parentNode.getElementsByTagName('input')[0]; ");
		sb.append("			if (txtNode.value=='TEMPID_WILL_BE_REPLACED-8') { ");
		sb.append("				document.getElementById('"+id+"').checked=true; changeTextBox2Hidden(txtNode);");
		sb.append("			}");
		sb.append("		}");
		sb.append("</script>");
		sb.append("");

		return sb.toString();
	}
}
