import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import './newPatientIdentifierSources.css'

export default class LocalIdentifierGenerator extends Component {

  render() {

    return (
      <div>
        <div className="newPatientIdentifierSourcesTitle">New: Local Identifier Generator for {this.props.title}</div>
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
                <Input id="name" name="name" className="newPatientIdentifierSourcesInput" type="text" size="sm" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Description:</td>
              <td>
                <Input type="textarea"  id="description" name="description" size="sm" className="newPatientIdentifierSourcesInput" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td><Input id="checkDigit" name="checkDigit" className="newPatientIdentifierSourcesInput" size="sm" type="text" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Regular Expression Format:</td>
              <td><Input id="regex" name="regex" className="newPatientIdentifierSourcesInput" type="text" size="sm" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> Base Character Set:</td>
              <td>
                <Input id="baseCharacterSet" name="baseCharacterSet" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> First Identifier Base:</td>
              <td>
                <Input id="firstIdentifierBase" name="firstIdentifierBase" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Prefix:</td>
              <td>
                <Input id="prefix" name="prefix" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Suffix:</td>
              <td>
                <input id="suffix" name="suffix" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Min Length:</td>
              <td>
                <Input id="minLength" name="minLength" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Max Length:</td>
              <td>
                <Input id="maxLength" name="maxLength" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}
