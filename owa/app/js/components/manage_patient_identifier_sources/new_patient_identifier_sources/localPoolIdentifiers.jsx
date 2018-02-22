import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import './newPatientIdentifierSources.css'

export default class LocalPoolIdentifiers extends Component {

  constructor(props){
    super(props);
    this.state = {
      name: '',
      description: '',
      sequential: '',
      refillWithScheduledTask: '',
      batchSize: '1000',
      minPoolSize: '500',
      poolIdentifier: '',
      identifierType: props.identifierTypeUuid,
      sources: []
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount(){
    this.fetchIdentifierSources();
  }

  fetchIdentifierSources(){
    apiCall(null, 'get', '/idgen/identifiersource?identifierType=' + this.state.identifierType).then((response) => {
      this.setState({
        sources: response.results,
        poolIdentifier: response.results[0].uuid
      });
    });
  }

  handleChange(event){
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleRadioChange(name) {
    if(name === "sequential"){
      this.setState({
        sequential: !this.state.sequential
      });
    }
    if(name === "refill"){
      this.setState({
        refillWithScheduledTask: !this.state.refillWithScheduledTask
      });
    }
  }

  handleSubmit(){

    if(this.state.name === ""){
      this.props.handleAlerts("error", "The name must be filled in.");
      return;
    }

    let data = {
      name: this.state.name,
      description: this.state.description,
      sourceUuid: this.state.poolIdentifier,
      sequential: this.state.sequential,
      refillWithScheduledTask: this.state.refillWithScheduledTask,
      batchSize: this.state.batchSize,
      minPoolSize: this.state.minPoolSize,
      identifierType: this.state.identifierType,
      sourceType: 'IdentifierPool'
    };

    apiCall(data, 'post', '/idgen/identifiersource?v=full').then((response) => {
      if( !response.error ) {
        this.props.handleAlerts("success", "Successfull Updated " + this.state.name);
        response.class = "org.openmrs.module.idgen.IdentifierPool";
        this.props.handleNewIdentifierSource(response); 
      }else{
        this.props.handleAlerts("error", response.error.message);
      }
    });
  }

  render() {

    return (
      <div>
        <div className="newPatientIdentifierSourcesTitle">New: Local Pool of Identifiers for {this.props.title}</div>
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
                <Input id="name" value={this.state.name ? this.state.name : ""} onChange={(event) => this.handleChange(event)} name="name" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Description:</td>
              <td><Input type="textarea" value={this.state.description ? this.state.description: ""} onChange={(event) => this.handleChange(event)} id="description" name="description" size="sm" className="newPatientIdentifierSourcesInput" /></td>
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
              <td className="newPatientIdentifierSourcesTitle">Pool Identifier Source:</td>
              <td>
                <Input id="poolIdentifier" name="poolIdentifier" className="newPatientIdentifierSourcesInput" size="sm" type="select">
                  <option  value=""></option>
                {
                  Object.keys(this.state.sources).map(index => {
                    return(<option key={index} value={this.state.sources[index].uuid}>{this.state.sources[index].display}</option>);
                  })
                }
                </Input>
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Order:</td>
              <td>
                <input id="sequential1" name="sequential" value="false" onChange={event => this.handleRadioChange("sequential")} checked="checked" type="radio" /> Random
                <input id="sequential2" name="sequential" value="true" onChange={event => this.handleRadioChange("sequential")} type="radio" /> Sequential
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">When to fill:</td>
              <td>
                <input id="refillWithScheduledTask1" name="refillWithScheduledTask" onChange={event => this.handleRadioChange("refill")} value="true" checked="checked" type="radio" /> Background task
                <input id="refillWithScheduledTask2" name="refillWithScheduledTask" onChange={event => this.handleRadioChange("refill")} value="false" type="radio" /> When you request an identifier
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Batch Size:</td>
              <td>
                <Input id="batchSize" value={this.state.batchSize ? this.state.batchSize : ""} onChange={(event) => this.handleChange(event)} name="batchSize" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="newPatientIdentifierSourcesTitle">Minimum Pool Size:</td>
              <td>
                <Input id="minPoolSize" value={this.state.minPoolSize ? this.state.minPoolSize:  ""} onChange={(event) => this.handleChange(event)} name="minPoolSize" className="newPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }

}
