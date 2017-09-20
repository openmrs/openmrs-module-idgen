import React, {Component} from 'react';
import { Table, Input  } from 'reactstrap';

export default class ConfigureSource extends React.Component {

  render() {
    return (
      <div>
        <Table size="sm" responsive >
          <thead></thead>
          <tbody>
          <tr>
            <td>Name: </td>
            <td><Input type="text" name="text" id="name" size="sm" /></td>
            <td></td>
          </tr>
          <tr>
            <td>Description: </td>
            <td><Input type="textarea" name="description" id="description" size="sm" /></td>
          </tr>
          <tr>
            <td>Check Digit Algorithm: </td>
            <td><Input type="text" name="checkdigit" id="checkdigit" size="sm" /></td>
          </tr>
          <tr>
            <td>Regular Expression Format: </td>
            <td><Input type="text" name="regex" id="regex" size="sm" /></td>
          </tr>
          <tr>
            <td>Base Character Set: </td>
            <td><Input type="text" name="basecharacterset" id="basecharacterset" size="sm" /></td>
          </tr>
          <tr>
            <td>First Identifier Base: </td>
            <td><Input type="text" name="firstidentifierbase" id="firstidentifierbase" size="sm" /></td>
          </tr>
          <tr>
            <td>Prefix: </td>
            <td><Input type="text" name="prefix" id="prefix" size="sm" /></td>
          </tr>
          <tr>
            <td>Suffix: </td>
            <td><Input type="text" name="suffix" id="suffix" size="sm" /></td>
          </tr>
          <tr>
            <td>Min Length: </td>
            <td><Input type="text" name="minlength" id="minlength" size="sm" /></td>
          </tr>
          <tr>
            <td>Max Length: </td>
            <td><Input type="text" name="maxlength" id="maxlength" size="sm" /></td>
          </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}

