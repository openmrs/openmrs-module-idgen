import React, {Component} from 'react';
import {Button, Table, Input} from 'reactstrap';

export default class ViewSource extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
          disabled: true
        };

        this.toggleDisable = this.toggleDisable.bind(this);
    }

    toggleDisable() {
        this.setState((prevState) => { return {
            disabled: !prevState.disabled
        }});
    }

  render() {
    return (
      <div>
        <Table size="sm" responsive >
          <thead></thead>
          <tbody>
            <tr>
              <td>Name: </td>
              <td><Input type="text" name="text" id="name" disabled={this.state.disabled} size="sm" /></td>
              <td><a onClick={this.toggleDisable} href="#">(Edit)</a></td>
            </tr>
            <tr>
              <td>Description: </td>
              <td><Input type="textarea" name="description" disabled={this.state.disabled} id="description"
                         size="sm" /></td>
            </tr>
            <tr>
              <td>Check Digit Algorithm: </td>
              <td><Input type="text" name="checkdigit" disabled={this.state.disabled} id="checkdigit" size="sm" /></td>
            </tr>
            <tr>
              <td>Regular Expression Format: </td>
              <td><Input type="text" name="regex" id="regex" disabled={this.state.disabled} size="sm" /></td>
            </tr>
            <tr>
              <td>Base Character Set: </td>
              <td><Input type="text" name="basecharacterset" id="basecharacterset" disabled={this.state.disabled}
                         size="sm" /></td>
            </tr>
            <tr>
              <td>First Identifier Base: </td>
              <td><Input type="text" name="firstidentifierbase" id="firstidentifierbase" disabled={this.state.disabled}
                         size="sm" /></td>
            </tr>
            <tr>
              <td>Prefix: </td>
              <td><Input type="text" name="prefix" id="prefix" disabled={this.state.disabled} size="sm" /></td>
            </tr>
            <tr>
              <td>Suffix: </td>
              <td><Input type="text" name="suffix" id="suffix" disabled={this.state.disabled} size="sm" /></td>
            </tr>
            <tr>
              <td>Min Length: </td>
              <td><Input type="text" name="minlength" id="minlength" disabled={this.state.disabled} size="sm" /></td>
            </tr>
            <tr>
              <td>Max Length: </td>
              <td><Input type="text" name="maxlength" id="maxlength" disabled={this.state.disabled} size="sm" /></td>
            </tr>
          </tbody>
        </Table>

        <Table size="sm" responsive >
        <thead></thead>
        <tbody>
        <tr>
          <td>Upload from file:</td>
          <td>
            <form action="reserveIdentifiersFromFile.form" method="post" encType="multipart/form-data">
              <a href="#">0 defined </a>
              <input type="hidden" name="source" value="1" />
              <input type="file" name="inputFile" />
              <input type="submit" value="Upload" />
            </form>
          </td>
        </tr>
        </tbody>
      </Table>

      <Table size="sm" responsive >
        <thead></thead>
        <tbody>
          <tr>
            <td><b>Export Identifiers:</b></td>
            <td></td>
          </tr>
          <tr>
            <td>Number to generate to file: </td>
            <td><Input type="text" name="numberToGenerate" id="numberToGenerate" /></td>
          </tr>
          <tr>
            <td>Comment: </td>
            <td><Input type="textarea" name="comment" id="comment" size="sm" /></td>
          </tr>
          <tr>
            <td><input type="submit" value="Export" /></td>
            <td><input type="hidden" name="source" id="source" value="1" /></td>
          </tr>
        </tbody>
      </Table>
      </div>
    )
  }
}
