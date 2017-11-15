import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import Dropzone from 'react-dropzone'
import apiCall from '../../../utilities/apiHelper';
import download from '../../../utilities/download';
import './viewPatientIdentifierSources.css'

export default class ViewLocalPoolIdentifiers extends Component {

  constructor(props){
    super(props);
    this.state={
      uuid: props.identifierSource.uuid,
      sourceUuid: props.identifierSource.sourceUuid,
      usedIdentifiers: this.props.identifierSource.usedIdentifiers,
      availableIdentifiers: this.props.identifierSource.availableIdentifiers,
      numberToGenerate: '',
      comment: '',
      batchSize: '',
      fileContent: '',
      fileName: 'Drag and Drop or Click here'
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleExportIdentifiers = this.handleExportIdentifiers.bind(this);
    this.handleAddIdentifiersFromSource = this.handleAddIdentifiersFromSource.bind(this);
    this.handleAddIdentifiersFromFile = this.handleAddIdentifiersFromFile.bind(this);
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
        reader.onabort = () => this.props.handleAlerts("error", 'file reading was aborted');
        reader.onerror = () => this.props.handleAlerts("error", 'file reading has failed');

        reader.readAsBinaryString(file);
    });
}

  handleAddIdentifiersFromSource(){
    if(!this.state.batchSize || !this.state.batchSize.trim()){
      this.props.handleAlerts("error", "The batch size cannot be empty.");
    }else{
    let data = {
      batchSize: this.state.batchSize,
      operation: "uploadFromSource"
    }
    apiCall(data, 'post', '/idgen/identifiersource/' + this.state.uuid).then((response) => {
      if( !response.error ) {
        this.setState((prevState) =>{
          return {availableIdentifiers: parseInt(prevState.availableIdentifiers) + parseInt(this.state.batchSize)}
        });
        this.props.handleAlerts("success", "Identifiers successfully added"); 
      }else{
        if(response.error.message === "[null]"){
          this.props.handleAlerts("error", "Please select a source for the Pool.");
        }else{
          this.props.handleAlerts("error", response.error.message);
        }
      }
    });
  }
  }

  handleAddIdentifiersFromFile(){
    if(this.state.fileContent.length === 0){
      this.props.handleAlerts("error", "No file selected.");
    }else{
      try {
        let parsedJson = JSON.parse(this.state.fileContent);
        let data = {
          identifiers: parsedJson.identifiers.join(),
          operation: "uploadFromFile"
        }
        apiCall(data, 'post', '/idgen/identifiersource/' + this.state.uuid).then((response) => {
          if( !response.error ) {
            this.props.handleAlerts("success", "Identifiers successfully added"); 
          }else{
            this.props.handleAlerts("error", response.error.message);
          }
        });
      } catch (exception) {
        this.props.handleAlerts("error", "Invalid file content: " + this.state.fileContent);
      }
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
      if( response["error"] === undefined ) {
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
              <td className="viewPatientIdentifierSourcesTitle">Source Name: </td>
              <td>
                {this.props.identifierSource.sourceName}
              </td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Description:</td>
              <td>{this.props.identifierSource.description}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Identifier Type:</td>
              <td>{this.props.identifierSource.identifierTypeName}</td>
            </tr>
            <tr>
              <td className="viewPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td>{this.props.identifierSource.identifierTypeValidator}</td>
            </tr>
            <tr>
              <td  colSpan="2">
                <p>This Identifier Source manages a pool of pre-generated identifiers.</p>
                <p>
                  Quantity Consumed: {this.state.usedIdentifiers}<br /> 
                  Quantity Available: {this.state.availableIdentifiers}
                </p>   
              </td>
            </tr>
            <tr>
              <td colSpan="2">
                Fill identifiers using manual upload from file: <span className="requiredField">(Format: {'{ "identifiers": ["1","2","3"] }'})</span> 
                <div className="dropzone">
                  <Dropzone
                  accept="text/plain"  
                  className="viewPatientIdentifierSourcesUpload" 
                  onDrop={this.onDrop.bind(this)}>
                    <p>{this.state.fileName}</p>
                  </Dropzone>
                </div>
                <input type="submit" style={{marginLeft: '38%'}} onClick={this.handleAddIdentifiersFromFile} value="Upload" />
              </td>
            </tr>
            <tr>
              <td colSpan="2">
                The pool can also be filled by directly connecting to Identifier Source {this.props.identifierSource.sourceName}.<br />
                Quantity to upload from Generator for {this.props.identifierSource.identifierTypeName}:
                <input value={this.state.batchSize ? this.state.batchSize : ""} onChange={(event) => this.handleChange(event)} type="input" name="batchSize" />
                <input style={{marginLeft: '5px'}} type="submit" onClick={this.handleAddIdentifiersFromSource} value="Upload" />
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
