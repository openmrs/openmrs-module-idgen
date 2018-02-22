import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import download from '../../../utilities/download';
import './viewPatientIdentifierSources.css'

export default class ViewRemoteIdentifierSource extends Component {

  constructor(props){
    super(props);
    this.state={
      uuid: props.identifierSource.uuid,
      sourceUuid: '',
      numberToGenerate: '',
      comment: ''
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleExportIdentifiers = this.handleExportIdentifiers.bind(this);
  }

  handleChange(event){
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleExportIdentifiers(){
    let data={
      generateIdentifiers: true,
      numberToGenerate: this.state.numberToGenerate,
      comment: this.state.comment,
      sourceUuid: this.state.uuid
    }
    
    apiCall(data, 'post', '/idgen/identifiersource/').then((response) => {
      if( !response.error ) {
        download("identifiers.txt", JSON.stringify(response));
        this.props.handleAlerts("success", "Identifiers successfully generated"); 
      }else{
        this.props.handleAlerts("error", response.error.message);
      }
    });
  }

  render() {

    return (
      <div>
        <div className="viewPatientIdentifierSourcesTitle">{this.props.identifierSource.identifierTypeName}: {this.props.identifierSource.name}</div>
        <Table size="sm" responsive>
          <thead>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle"></td>
              <td className="viewPatientIdentifierSourcesTitle"></td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Source Name:</td>
              <td>
                {this.props.identifierSource.name}
              </td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Description:</td>
              <td>
                {this.props.identifierSource.description}
              </td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Identifier Type:</td>
              <td>{this.props.identifierSource.identifierTypeName}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td><span>{this.props.identifierSource.identifierTypeValidator}</span></td>
            </tr>
            <tr>
              <td colSpan="2">
                This Identifier Source connects to the following remote URL to retrieve new identifiers:<br />
                {this.props.identifierSource.url}
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
            <td><Input value={this.state.numberToGenerate ? this.state.numberToGenerate : ""} onChange={(event) => this.handleChange(event)} type="text" name="numberToGenerate" id="numberToGenerate" /></td>
          </tr>
          <tr>
            <td>Comment: </td>
            <td><Input value={this.state.comment ? this.state.comment : ""} onChange={(event) => this.handleChange(event)} type="textarea" name="comment" id="comment" size="sm" /></td>
          </tr>
          <tr>
            <td><input onClick={this.handleExportIdentifiers} type="submit" value="Export" /></td>
          </tr>
        </tbody>
      </Table>
      </div>
    )
  }
}
