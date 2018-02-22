import React, {Component} from 'react';
import {Button, Table, Input} from 'reactstrap';
import Dropzone from 'react-dropzone'
import apiCall from '../../../utilities/apiHelper';
import download from '../../../utilities/download';

export default class ViewLocalIdentifierGenerator extends React.Component {

  constructor(props){
    super(props);
    this.state={
      uuid: props.identifierSource.uuid,
      sourceUuid: '',
      numberToGenerate: '',
      comment: '',
      reservedIdentifiers: props.identifierSource.reservedIdentifiers,
      fileContent: '',
      fileName: 'Drag and Drop or Click here'
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleExportIdentifiers = this.handleExportIdentifiers.bind(this);
    this.handleAddReservedIdentifiers = this.handleAddReservedIdentifiers.bind(this);
    this.handleDownloadReservedIdentifiers = this.handleDownloadReservedIdentifiers.bind(this);
    this.handleExportIdentifiers = this.handleExportIdentifiers.bind(this);
  }

  handleChange(event){
    this.setState({
      [event.target.name]: event.target.value
    });
  }
  
  onDrop(acceptedFiles, rejectedFiles) {
    acceptedFiles.forEach(file => {
        const reader = new FileReader();
        reader.onload = () => {
            const fileAsBinaryString = reader.result;
            this.state.fileName = file.name;
            this.setState({fileContent: fileAsBinaryString});
        };
        reader.onabort = () => console.error('file reading was aborted');
        reader.onerror = () => console.error('file reading has failed');

        reader.readAsBinaryString(file);
    });
}

  handleAddReservedIdentifiers(){
    if(this.state.fileContent.length === 0){
      this.props.handleAlerts("error", "No file selected.");
    }else{
      let reservedIdentifiers = this.state.fileContent.split('\n');

      try {
        let data = {
          reservedIdentifiers: reservedIdentifiers.join()
        }
        apiCall(data, 'post', '/idgen/identifiersource/' + this.state.uuid).then((response) => {
          if( !response.error ) {
            let found = 0;
            let currentReservedIdentifiers = this.state.reservedIdentifiers;
            for(var counter in reservedIdentifiers){
              if(currentReservedIdentifiers.indexOf(reservedIdentifiers[counter]) === -1){
                currentReservedIdentifiers.push(reservedIdentifiers[counter]);
                found = 1;
              }
            }
            if(found === 0){
              this.props.handleAlerts("error", "Reserved identifier(s) already exist!");
            }else{
              this.setState({
                reservedIdentifiers: currentReservedIdentifiers
              });
              this.props.handleAlerts("success", "Identifiers successfully added");
            }
          }else{
            this.props.handleAlerts("error", response.error.message);
          }
        });
      } catch (exception) {
        this.props.handleAlerts("error", "Invalid file content: " + this.state.fileContent);
      }
    } 
  }

  handleDownloadReservedIdentifiers(){
    if(this.state.reservedIdentifiers.length >0){
      let output = '';
      for(var index in this.state.reservedIdentifiers){
        output += this.state.reservedIdentifiers[index] + '\n';
      }
      download("reservedIdentifiers.txt", output);
    }
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
        <Table size="sm" responsive >
          <thead></thead>
          <tbody>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Source Name: </td>
              <td>{this.props.identifierSource.name}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Description: </td>
              <td>{this.props.identifierSource.description}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Identifier Type: </td>
              <td>{this.props.identifierSource.identifierTypeName}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Check Digit Algorithm: </td>
              <td>{this.props.identifierSource.identifierTypeValidator}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle" colSpan="2">
                This Identifier Source generates sequential identifiers with the following configuration:</td>
            </tr>
            <tr>
              <td>Base Character Set: </td>
              <td>{this.props.identifierSource.baseCharacterSet}</td>
            </tr>
            <tr>
              <td>First Identifier Base: </td>
              <td>{this.props.identifierSource.firstIdentifierBase}</td>
            </tr>
            <tr>
              <td>Prefix: </td>
              <td>{this.props.identifierSource.prefix}</td>
            </tr>
            <tr>
              <td>Suffix: </td>
              <td>{this.props.identifierSource.suffix}</td>
            </tr>
            <tr>
              <td>Min Length: </td>
              <td>{this.props.identifierSource.minLength}</td>
            </tr>
            <tr>
              <td>Max Length: </td>
              <td>{this.props.identifierSource.maxLength}</td>
            </tr>
          </tbody>
        </Table>
        <Table size="sm" responsive >
        <thead></thead>
        <tbody>
        <tr>
          <td>Reserved Identifiers: <a href="javascript:void(0)" onClick={this.handleDownloadReservedIdentifiers}>
            {this.state.reservedIdentifiers.length} defined. 
            </a>
            <span style={{marginLeft: '1%'}}>Upload from file: <span className="requiredField">(Format: One identifier per line)</span></span>
            <br/>
            <div className="dropzone">
              <Dropzone
              accept="text/plain"  
              className="viewPatientIdentifierSourcesUpload" 
              onDrop={this.onDrop.bind(this)}>
                <p>{this.state.fileName}</p>
              </Dropzone>
            </div>
            <input type="submit" style={{marginLeft: '40%'}} onClick={this.handleAddReservedIdentifiers} value="Upload" />
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
