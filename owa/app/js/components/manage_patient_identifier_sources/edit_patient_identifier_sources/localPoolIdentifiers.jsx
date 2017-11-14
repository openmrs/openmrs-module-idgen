import React, {Component} from 'react';
import {Table, Button, Input} from 'reactstrap';
import apiCall from '../../../utilities/apiHelper';
import './editPatientIdentifierSources.css'

export default class EditLocalPoolIdentifiers extends Component {

  constructor(props){
    super(props);
    this.state = {
      index: props.identifierSource.index,
      uuid: props.identifierSource.uuid,
      name: props.identifierSource.name,
      description: props.identifierSource.description,
      identifierTypeValidator: props.identifierSource.identifierTypeValidator,
      identifierTypeUuid: props.identifierSource.identifierTypeUuid,
      sourceUuid: props.identifierSource.sourceUuid,
      sourceName: props.identifierSource.sourceName,
      sequential: props.identifierSource.sequential,
      refillWithScheduledTask: props.identifierSource.refillWithScheduledTask,
      batchSize: props.identifierSource.batchSize,
      minPoolSize: props.identifierSource.minPoolSize,
      sources: []
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.createSelectOptions = this.createSelectOptions.bind(this);
    this.handleSelectChange = this.handleSelectChange.bind(this);
  }

  componentDidMount(){
    this.createSelectOptions();
  }

  createSelectOptions(){
    apiCall(null, 'get', '/idgen/identifiersource?identifierType=' + this.state.identifierTypeUuid).then((response) => {
      let sources = response.results;
      let currentIndex = 0;
      Object.keys(sources).map(index => {
        if(sources[index].uuid === this.state.uuid){
          currentIndex = index;
        }
      });
      delete sources[currentIndex];
      this.setState({sources: response.results});
    });
  }

  handleChange(event){
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleSelectChange(){
    this.setState({
      sourceUuid: document.getElementById("sourceUuid").value
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
    let data = {
      name: this.state.name,
      description: this.state.description,
      sourceUuid: this.state.sourceUuid,
      sequential: this.state.sequential,
      refillWithScheduledTask: this.state.refillWithScheduledTask,
      batchSize: this.state.batchSize,
      minPoolSize: this.state.minPoolSize
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
        <div className="editPatientIdentifierSourcesTitle">Edit: Local Pool of Identifiers for {this.props.identifierSource.identifierTypeName}</div>
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
                <Input id="name" value={this.state.name ? this.state.name : ""} onChange={(event) => this.handleChange(event)} name="name" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Description:</td>
              <td><Input type="textarea" value={this.state.description ? this.state.description : ""} onChange={(event) => this.handleChange(event)} id="description" name="description" size="sm" className="editPatientIdentifierSourcesInput" /></td>
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
              <td className="editPatientIdentifierSourcesTitle">Pool Identifier Source:</td>
              <td>
                <Input 
                id="sourceUuid" 
                name="sourceUuid" 
                value={this.state.sourceUuid ? this.state.sourceUuid : ""} 
                onChange={this.handleSelectChange} 
                className="editPatientIdentifierSourcesInput" 
                size="sm" 
                type="select">
                  <option  value=""></option>
                  {
                    this.state.sources.map((options, index) => {
                      return(<option key={index} value={options.uuid ? options.uuid : ""}>{options.display}</option>);
                    })
                  }
                </Input>
                </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Order:</td>
              <td>
                <input id="sequential1" name="sequential" checked={this.state.sequential === false ? true : false} value={this.state.sequential} onChange={event => this.handleRadioChange("sequential")} type="radio" /> Random
                <input id="sequential2" name="sequential" checked={this.state.sequential === true ? true : false} value={this.state.sequential} onChange={event => this.handleRadioChange("sequential")} type="radio" /> Sequential
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">When to fill:</td>
              <td>
                <input id="refillWithScheduledTask1" name="refillWithScheduledTask" checked={this.state.refillWithScheduledTask === true ? true : false} value={this.state.refillWithScheduledTask} onChange={event => this.handleRadioChange("refill")} type="radio" /> Background task
                <input id="refillWithScheduledTask2" name="refillWithScheduledTask" checked={this.state.refillWithScheduledTask === false ? true : false} value={this.state.refillWithScheduledTask} onChange={event => this.handleRadioChange("refill")} type="radio" /> When you request an identifier
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Batch Size:</td>
              <td>
                <Input id="batchSize" value={this.state.batchSize ? this.state.batchSize : ""} onChange={(event) => this.handleChange(event)} name="batchSize" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
            <tr>
              <td className="editPatientIdentifierSourcesTitle">Minimum Pool Size:</td>
              <td>
                <Input id="minPoolSize" value={this.state.minPoolSize ? this.state.minPoolSize : ""} onChange={(event) => this.handleChange(event)} name="minPoolSize" className="editPatientIdentifierSourcesInput" size="sm" type="text" />
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    )
  }

}
