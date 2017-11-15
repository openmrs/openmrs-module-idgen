import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import './newPatientIdentifierSources.css'

export default class LocalIdentifierGenerator extends Component {

  constructor(props){
    super(props);
    this.state = {
      uuid: '',
      name: '',
      description: '',
      baseCharacterSet: '',
      firstIdentifierBase: '',
      prefix: '',
      suffix: '',
      minLength: '',
      maxLength: '',
      identifierType: props.identifierTypeUuid
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event){
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleSubmit(){

    if(this.state.name === "" || this.state.baseCharacterSet === "" || this.state.firstIdentifierBase === ""){
      this.props.handleAlerts("error", "The name, Base Character Set and the First Identifier Base must be filled in.");
      return;
    }

    let data = {
      name: this.state.name,
      description: this.state.description,
      baseCharacterSet: this.state.baseCharacterSet,
      firstIdentifierBase: this.state.firstIdentifierBase,
      prefix: this.state.prefix,
      suffix: this.state.suffix,
      minLength: this.state.minLength,
      maxLength: this.state.maxLength,
      identifierType: this.state.identifierType,
      sourceType: 'SequentialIdentifierGenerator'
    };

    apiCall(data, 'post', '/idgen/identifiersource?v=full').then((response) => {
      if( !response.error ) {
        this.props.handleAlerts("success", "Successfull Created: " + this.state.name);
        response.class = "org.openmrs.module.idgen.SequentialIdentifierGenerator"; 
        this.props.handleNewIdentifierSource(response);
      }else{
        this.props.handleAlerts("error", response.error.message);
      }  
    });
  }

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
                <Input id="name" value={this.state.name ? this.state.name : ""} onChange={(event) => this.handleChange(event)} name="name" className="newPatientIdentifierSourcesInput" type="text" size="sm" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Description:</td>
              <td>
                <Input type="textarea" value={this.state.description ? this.state.description: ""} onChange={(event) => this.handleChange(event)} id="description" name="description" size="sm" className="newPatientIdentifierSourcesInput" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td>{this.props.validator ? this.props.validator:  "None"}</td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Regular Expression Format:</td>
              <td>None</td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> Base Character Set:</td>
              <td>
                <Input id="baseCharacterSet" value={this.state.baseCharacterSet ? this.state.baseCharacterSet : ""} onChange={(event) => this.handleChange(event)} name="baseCharacterSet" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> First Identifier Base:</td>
              <td>
                <Input id="firstIdentifierBase" value={this.state.firstIdentifierBase ? this.state.firstIdentifierBase : ""} onChange={(event) => this.handleChange(event)} name="firstIdentifierBase" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Prefix:</td>
              <td>
                <Input id="prefix" value={this.state.prefix ? this.state.prefix : ""} onChange={(event) => this.handleChange(event)} name="prefix" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Suffix:</td>
              <td>
                <input id="suffix" value={this.state.suffix ? this.state.suffix : ""} onChange={(event) => this.handleChange(event)} name="suffix" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Min Length:</td>
              <td>
                <Input id="minLength" value={this.state.minLength ? this.state.minLength : ""} onChange={(event) => this.handleChange(event)} name="minLength" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Max Length:</td>
              <td>
                <Input id="maxLength" value={this.state.maxLength ? this.state.maxLength : ""} onChange={(event) => this.handleChange(event)} name="maxLength" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}
