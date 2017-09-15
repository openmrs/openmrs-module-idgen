import React, {Component} from 'react';
import {Table, Button} from 'reactstrap';
import './newPatientIdentifierSources.css'

export default class LocalPoolIdentifiers extends Component {

  render() {

    return (
      <div>
        <div>New: Local Pool of Identifiers for {this.props.title}</div>
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
              <td><span className="requiredField">*</span> Pool Identifier Source:</td>
              <td>No other compatible sources available</td>
            </tr>
            <tr>
              <td>Order:</td>
              <td>
                <input id="sequential1" name="sequential" value="false" checked="checked" type="radio" /> Random
                <input id="sequential2" name="sequential" value="true" type="radio" /> Sequential
              </td>
            </tr>
            <tr>
              <td>When to fill:</td>
              <td>
                <input id="refillWithScheduledTask1" name="refillWithScheduledTask" value="true" checked="checked" type="radio" /> Background task
                <input id="refillWithScheduledTask2" name="refillWithScheduledTask" value="false" type="radio" /> When you request an identifier
              </td>
            </tr>
            <tr>
              <td>Batch Size:</td>
              <td>
                <input id="batchSize" name="batchSize" value="1000" size="50" type="text" />
              </td>
            </tr>
            <tr>
              <td>Minimum Pool Size:</td>
              <td>
                <input id="minPoolSize" name="minPoolSize" value="500" size="50" type="text" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }
}