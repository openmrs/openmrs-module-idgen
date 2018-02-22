import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import './editPatientIdentifierSources.css'

export default class EditLocalIdentifierGenerator extends Component {

  constructor(props){
    super(props);
    this.state = {
      index: props.identifierSource.index,
      initialized: props.identifierSource.initialized,
      uuid: props.identifierSource.uuid,
      name: props.identifierSource.name,
      description: props.identifierSource.description,
      identifierTypeValidator: props.identifierSource.identifierTypeValidator,
      baseCharacterSet: props.identifierSource.baseCharacterSet,
      firstIdentifierBase: props.identifierSource.firstIdentifierBase,
      prefix: props.identifierSource.prefix,
      suffix: props.identifierSource.suffix,
      minLength: props.identifierSource.minLength,
      maxLength: props.identifierSource.maxLength
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleFirstIdentifierBase(){
    if(this.state.initialized){
      return(this.state.firstIdentifierBase + " In Use - Unable to Modify ")
    }
    return(<Input 
    id="firstIdentifierBase" 
    value={this.state.firstIdentifierBase ? this.state.firstIdentifierBase : ""} 
    onChange={(event) => this.handleChange(event)} 
    name="firstIdentifierBase" 
    className="editPatientIdentifierSourcesInput" 
    size="sm" 
    type="text" />);
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
      baseCharacterSet: this.state.baseCharacterSet,
      firstIdentifierBase: this.state.firstIdentifierBase,
      prefix: this.state.prefix,
      suffix: this.state.suffix,
      minLength: this.state.minLength,
      maxLength: this.state.maxLength
    };

    apiCall(data, 'post', '/idgen/identifiersource/' + this.state.uuid).then((response) => {
      if( !response.error) {
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
        <div className="editPatientIdentifierSourcesTitle">Edit: Local Identifier Generator for {this.props.identifierSource.identifierTypeName}</div>
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
                <Input id="name" value={this.state.name ? this.state.name : ""} onChange={(event) => this.handleChange(event)} name="name" className="editPatientIdentifierSourcesInput" type="text" size="sm" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Description:</td>
              <td>
                <Input type="textarea" value={this.state.description ? this.state.description:  ""} onChange={(event) => this.handleChange(event)} id="description" name="description" size="sm" className="editPatientIdentifierSourcesInput" />
              </td>
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
              <td className="editPatientIdentifierSourcesTitle"><span className="requiredField">*</span> Base Character Set:</td>
              <td>
                <Input id="baseCharacterSet" value={this.state.baseCharacterSet ? this.state.baseCharacterSet : ""} onChange={(event) => this.handleChange(event)} name="baseCharacterSet" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle"><span className="requiredField">*</span> First Identifier Base:</td>
              <td>
                {this.handleFirstIdentifierBase()}
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Prefix:</td>
              <td>
                <Input id="prefix" value={this.state.prefix ? this.state.prefix : ""} onChange={(event) => this.handleChange(event)} name="prefix" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Suffix:</td>
              <td>
                <Input id="suffix" value={this.state.suffix ? this.state.suffix :  ""} onChange={(event) => this.handleChange(event)} name="suffix" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Min Length:</td>
              <td>
                <Input id="minLength" value={this.state.minLength ? this.state.minLength : ""} onChange={(event) => this.handleChange(event)} name="minLength" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Max Length:</td>
              <td>
                <Input id="maxLength" value={this.state.maxLength ? this.state.maxLength : ""} onChange={(event) => this.handleChange(event)} name="maxLength" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }
}
