/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import FaHome from 'react-icons/lib/fa/home'
import { Breadcrumb, BreadcrumbItem } from 'reactstrap';
import './breadCrumbs.css';

export default class BreadCrumbs extends React.Component {
  render() {
    return (
        <Breadcrumb>
          <BreadcrumbItem><a href="../../"><FaHome /></a></BreadcrumbItem>
          <BreadcrumbItem>IDGEN</BreadcrumbItem>
          <BreadcrumbItem active>
          {(this.props.activeTab == 2) ? "Auto-Generation Options" :
           (this.props.activeTab == 3) ? "View Log Entries" : "Manage Patient Identifier Sources"}
          </BreadcrumbItem>
        </Breadcrumb>
    )
  }
}
