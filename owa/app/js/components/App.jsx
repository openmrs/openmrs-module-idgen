/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import 'sweetalert2/dist/sweetalert2.min.css';
import Header from './common/header';
import BodyLayout from './bodyLayout';
import BreadCrumbs from './breadcrumbs/breadcrumbs';

export default class App extends React.Component {
  render() {
    return (
      <div>
        <Header />
        <BreadCrumbs />
        <div id="body-wrapper">
          <BodyLayout />
        </div>
      </div>
    );
  }
}
