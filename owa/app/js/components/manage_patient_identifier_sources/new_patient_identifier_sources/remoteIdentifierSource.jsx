import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import './newPatientIdentifierSources.css'

export default class RemoteIdentifierSource extends Component {

  constructor(props){
    super(props);
    this.state = {
      name: '',
      description: '',
      url: '',
      user: '',
      password: '',
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

    if(this.state.name === "" || this.state.url === ""){
      this.props.handleAlerts("error", "The name and the url must be filled in.");
      return;
    }

    let data = {
      name: this.state.name,
      description: this.state.description,
      identifierType: this.state.identifierType,
      url: this.state.url,
      sourceType: 'RemoteIdentifierSource',
      user: this.state.user,
      password: this.state.password
    };

    apiCall(data, 'post', '/idgen/identifiersource?v=full').then((response) => {
      if( !response.error ) {
        this.props.handleAlerts("success", "Successfull Created: " + this.state.name);
        response.class = "org.openmrs.module.idgen.RemoteIdentifierSource";
        this.props.handleNewIdentifierSource(response); 
      }else{
        this.props.handleAlerts("error", response.error.message);
      }
    });
  }

  render() {

    return (
      <div>
        <div className="newPatientIdentifierSourcesTitle">New: Remote Identifier Source for {this.props.title}</div>
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
                <Input id="name" value={this.state.name ? this.state.name : ""} onChange={(event) => this.handleChange(event)} name="name"  className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Description:</td>
              <td><Input type="textarea" value={this.state.description ? this.state.description : ""} onChange={(event) => this.handleChange(event)} id="description" name="description" className="newPatientIdentifierSourcesInput" size="sm" /></td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td>{this.props.validator ? this.props.validator : "None"}</td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Regular Expression Format:</td>
              <td>None</td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle"><span className="requiredField">*</span> URL:</td>
              <td>
                <Input id="url" value={this.state.url ? this.state.url : ""} onChange={(event) => this.handleChange(event)} name="url" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Username:</td>
              <td>
                <Input id="user" value={this.state.user ? this.state.user : ""} onChange={(event) => this.handleChange(event)} name="user" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Password:</td>
              <td>
                <Input id="password" value={this.state.password ? this.state.password : ""} onChange={(event) => this.handleChange(event)} name="password" className="newPatientIdentifierSourcesInput" size="sm" type="password" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}
