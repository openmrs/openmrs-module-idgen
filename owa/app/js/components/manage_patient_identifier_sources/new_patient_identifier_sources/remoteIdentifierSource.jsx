import React, {Component} from 'react';
import {Table, Button} from 'reactstrap';
import './newPatientIdentifierSources.css'

export default class RemoteIdentifierSource extends Component {

  render() {

    return (
      <div>
        <div>New: Remote Identifier Source for {this.props.title}</div>
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
              <td><textarea id="description" name="description" rows="2" cols="50"></textarea></td>
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
              <td><span className="requiredField">*</span> URL:</td>
              <td>
                <input id="url" name="url" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Username:</td>
              <td>
                <input id="user" name="user" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Password:</td>
              <td>
                <input id="password" name="password" size="50" type="password" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }
}