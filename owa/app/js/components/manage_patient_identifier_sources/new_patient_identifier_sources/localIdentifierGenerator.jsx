import React, {Component} from 'react';
import {Table, Button} from 'reactstrap';
import './newPatientIdentifierSources.css'

export default class LocalIdentifierGenerator extends Component {

  constructor(props) {
    super(props);
  }

  render() {

    return (
      <div>
        <div>New: Local Identifier Generator for {this.props.title}</div>
        <table>
          <thead>
            <tr>
              <td>Field</td>
              <td>Value</td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><span className="requiredField">*</span> Name:</td>
              <td>
                <input id="name" name="name" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Description:</td>
              <td>
                <textarea id="description" name="description" rows="2" cols="50"></textarea>
              </td>
            </tr>
            <tr>
              <td>Check Digit Algorithm:</td>
              <td>None</td>
            </tr>
            <tr>
              <td>Regular Expression Format:</td>
              <td>None</td>
            </tr>
            <tr>
              <td><span className="requiredField">*</span> Base Character Set:</td>
              <td>
                <input id="baseCharacterSet" name="baseCharacterSet" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td><span className="requiredField">*</span> First Identifier Base:</td>
              <td>
                <input id="firstIdentifierBase" name="firstIdentifierBase" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Prefix:</td>
              <td>
                <input id="prefix" name="prefix" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Suffix:</td>
              <td>
                <input id="suffix" name="suffix" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Min Length:</td>
              <td>
                <input id="minLength" name="minLength" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Max Length:</td>
              <td>
                <input id="maxLength" name="maxLength" size="50" type="text" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }
}