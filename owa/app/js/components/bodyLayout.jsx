/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { TabContent, TabPane, Nav, NavItem, NavLink, 
        Card, Button, CardTitle, CardText, Row, Col, Form, FormGroup, 
        Label, Input, FormText } from 'reactstrap';
import classnames from 'classnames';
import LogEntries from './view_log_entries/viewLogEntries'
import ManagePatientIdentifierSources from './manage_patient_identifier_sources/managePatientIdentifierSources'
import ManageAutoGenerationOption from './manage_autogeneration_option/ManageAutoGenerationOption';

export default class BodyLayout extends React.Component {
  render() {
    return (
      <div>
        <Nav tabs>
          <NavItem>
            <NavLink
              className={classnames({
              active: this.props.activeTab === '1'
            })}
              onClick={() => {
              this.props.showTab('1');
            }}>
              Manage Patient Identifier Sources
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={classnames({
              active: this.props.activeTab === '2'
            })}
              onClick={() => {
              this.props.showTab('2');
            }}>
              Auto-Generation Options
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={classnames({
              active: this.props.activeTab === '3'
            })}
              onClick={() => {
              this.props.showTab('3');
            }}>
              View Log Entries
            </NavLink>
          </NavItem>
        </Nav>
        <TabContent activeTab={this.props.activeTab}>
          <TabPane tabId="1">
            <Row>
              <Col sm="12">
                <ManagePatientIdentifierSources />
              </Col>
            </Row>
          </TabPane>
          <TabPane tabId="2">
            <Row>
              <Col sm="12">
                <ManageAutoGenerationOption />
              </Col>
            </Row>
          </TabPane>
          <TabPane tabId="3">
            <Row>
              <Col sm="12">
                <LogEntries/>
              </Col>
            </Row>
          </TabPane>
        </TabContent>
      </div>
    );
  }
}
