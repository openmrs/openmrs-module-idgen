import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import './newPatientIdentifierSources.css'

export default class LocalPoolIdentifiers extends Component {

  render() {

    return (
      <div>
        <div className="newPatientIdentifierSourcesTitle">New: Local Pool of Identifiers for {this.props.title}</div>
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
                <Input id="name" name="name" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Description:</td>
              <td><Input type="textarea" id="description" name="description" size="sm" className="newPatientIdentifierSourcesInput" /></td>
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
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> Pool Identifier Source:</td>
              <td><Input id="poolIdentifier" name="poolIdentifier" className="newPatientIdentifierSourcesInput" size="sm" type="text" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Order:</td>
              <td>
                <input id="sequential1" name="sequential" value="false" checked="checked" type="radio" /> Random
                <input id="sequential2" name="sequential" value="true" type="radio" /> Sequential
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">When to fill:</td>
              <td>
                <input id="refillWithScheduledTask1" name="refillWithScheduledTask" value="true" checked="checked" type="radio" /> Background task
                <input id="refillWithScheduledTask2" name="refillWithScheduledTask" value="false" type="radio" /> When you request an identifier
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Batch Size:</td>
              <td>
                <Input id="batchSize" name="batchSize" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Minimum Pool Size:</td>
              <td>
                <Input id="minPoolSize" name="minPoolSize" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }

}
