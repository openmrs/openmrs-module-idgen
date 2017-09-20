import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import './newPatientIdentifierSources.css'

export default class RemoteIdentifierSource extends Component {

  render() {

    return (
      <div>
        <div className="newPatientIdentifierSourcesTitle">New: Remote Identifier Source for {this.props.title}</div>
        <Table size="sm" responsive>
          <thead>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Field</td>
              <td className="newPatientIdentifierSourcesTitle">Value</td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> Name:</td>
              <td>
                <Input id="name" name="name"  className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Description:</td>
              <td><Input type="textarea" id="description" name="description" className="newPatientIdentifierSourcesInput" size="sm" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td><Input id="checkDigit" name="checkDigit" className="newPatientIdentifierSourcesInput" size="sm" type="text" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Regular Expression Format:</td>
              <td><Input id="regex" name="regex" className="newPatientIdentifierSourcesInput" size="sm" type="text" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> URL:</td>
              <td>
                <Input id="url" name="url" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Username:</td>
              <td>
                <Input id="user" name="user" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td>Password:</td>
              <td>
                <Input id="password" name="password" className="newPatientIdentifierSourcesInput" size="sm" type="password" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}
