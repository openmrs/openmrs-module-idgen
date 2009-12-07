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

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;

/**
 * Provides a check
 */
public class CreatePatientIdGenCheckBoxHeader extends Extension {

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
		
		sb.append("<th>" + Context.getMessageSourceService().getMessage("idgen.autoGenerateHeader") + "</th>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("		function getElementsByClass(node,searchClass,tag) {");
		sb.append("			var classElements = new Array();");
		sb.append("			var els = node.getElementsByTagName(tag);");
		sb.append("			var elsLen = els.length;");
		sb.append("			var pattern = new RegExp('(^|\\s)' + searchClass + '(\\s|$)');");
		sb.append("			for (i = 0, j = 0; i < elsLen; i++) {");
		sb.append("				if ( pattern.test(els[i].className) ) {");
		sb.append("					classElements[j] = els[i];");
		sb.append("					j++;");
		sb.append("				}");
		sb.append("			}");
		sb.append("			return classElements;");
		sb.append("		}");
		sb.append("		function changeTextBox2Hidden( _el ) {");
		sb.append("			var _parent = _el.parentNode;");
		sb.append("			_parent.removeChild( _el );");
		sb.append("			if ( _el.type == 'text' ) {");
		sb.append("				_parent.innerHTML = '<label> " + Context.getMessageSourceService().getMessage("idgen.autoGenerateLabel") + "</label>");
		sb.append("									<input type=\"hidden\" name=\"' + _el.name + '\" value=\"TEMPID_WILL_BE_REPLACED-8\" />';");
		sb.append("			} else {");
		sb.append("				_parent.innerHTML = '<input type=\"text\" name=\"' + _el.name + '\" value=\"\" />';");
		sb.append("			}");
		sb.append("			_el = null;");
		sb.append("		}");
		sb.append("</script>");

		return sb.toString();
	}
}