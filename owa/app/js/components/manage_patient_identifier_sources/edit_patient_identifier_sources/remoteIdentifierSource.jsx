import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import './editPatientIdentifierSources.css'

export default class EditRemoteIdentifierSource extends Component {

  constructor(props){
    super(props);
    this.state = {
      index: props.identifierSource.index,
      uuid: props.identifierSource.uuid,
      name: props.identifierSource.name,
      description: props.identifierSource.description,
      identifierTypeValidator: props.identifierSource.identifierTypeValidator,
      url: props.identifierSource.url,
      user: props.identifierSource.user,
      password: props.identifierSource.password
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
    let data = {
      name: this.state.name,
      description: this.state.description,
      url: this.state.url,
      user: this.state.user,
      password: this.state.password
    };

    apiCall(data, 'post', '/idgen/identifiersource/' + this.state.uuid).then((response) => {
      if( !response.error ) {
        this.props.handleUpdateName(this.state.index, this.state.name);
        this.props.handleAlerts("success", "Successfull Updated " + this.state.name); 
      }else{
        this.props.handleAlerts("error", response.error.message);
      }
    });
  }

  render() {

    return (
      <div>
        <div className="editPatientIdentifierSourcesTitle">Edit: Remote Identifier Source for {this.props.identifierSource.identifierTypeName}</div>
        <Table size="sm" responsive>
          <thead>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Field</td>
              <td className="editPatientIdentifierSourcesTitle">Value</td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="editPatientIdentifierSourcesTitle"><span className="requiredField">*</span> Name:</td>
              <td>
                <Input id="name" value={this.state.name ? this.state.name : ""} onChange={(event) => this.handleChange(event)} name="name"  className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Description:</td>
              <td><Input type="textarea" value={this.state.description ? this.state.description: ""} onChange={(event) => this.handleChange(event)} id="description" name="description" className="editPatientIdentifierSourcesInput" size="sm" /></td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Check Digit Algorithm:</td>
              <td>{this.state.identifierTypeValidator ? this.state.identifierTypeValidator : "None"}</td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Regular Expression Format:</td>
              <td><span>None</span></td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle"><span className="requiredField">*</span> URL:</td>
              <td>
                <Input id="url" value={this.state.url ? this.state.url : ""} onChange={(event) => this.handleChange(event)} name="url" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Username:</td>
              <td>
                <Input id="user" value={this.state.user ? this.state.user : ""} onChange={(event) => this.handleChange(event)} name="user" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Password:</td>
              <td>
                <Input id="password" value={this.state.password ? this.state.password : ""} onChange={(event) => this.handleChange(event)} name="password" className="editPatientIdentifierSourcesInput" size="sm" type="password" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}
