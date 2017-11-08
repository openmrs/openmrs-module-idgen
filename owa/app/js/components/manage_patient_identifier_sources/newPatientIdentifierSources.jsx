import React, {Component} from 'react';
import { Table, Input, Button, Form, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import LocalIdentifierGenerator from './new_patient_identifier_sources/localIdentifierGenerator';
import LocalPoolIdentifiers from './new_patient_identifier_sources/localPoolIdentifiers';
import RemoteIdentifierSource from './new_patient_identifier_sources/remoteIdentifierSource';
import apiCall from '../../utilities/apiHelper';
import Toast from '../../utilities/toast';

export default class NewPatientIdentifierSources extends Component {

  constructor(props) {
    super(props);
    this.state = {
      modal: false,
      modalContent: 1,
      title: 'New Patient Identifier Source',
      identifierType: 'Please select an identifier Type',
      identifierTypes: [],
      identifierTypeValidator: '',
      identifierTypeUuid: ''
    };

    this.toggle = this.toggle.bind(this);
    this.getSource = this.getSource.bind(this);
    this.getIdentifier = this.getIdentifier.bind(this);
    this.handleSave = this.handleSave.bind(this);
  }

  toggle() {
    this.setState((prevState) => { return {
      modal: !prevState.modal
    }});
  }

  componentDidMount(){
    this.fetchIdentifierTypes();
  }

  handleAlerts(alertType, message){
    if(alertType === "success"){
      this.toast.toastSuccess(message);
    }else{
      this.toast.toastError(message);
    }
  }

  handleSave(){
    this.saveForm.handleSubmit();
    this.toggle();
  }

  handleNewIdentifierSource(identifierSource){
    this.props.handleNewIdentifierSource(identifierSource);
  }

  fetchIdentifierTypes(){
    apiCall(null, 'get', '/patientidentifiertype?v=full').then((response) => {
      this.setState({
        identifierTypes: response.results, 
        identifierType: response.results[0].display,
        identifierTypeValidator: response.results[0].validator,
        identifierTypeUuid: response.results[0].uuid});
    });
  }

  getIdentifier(){
    let options = document.getElementById("identifierType").options
    let selectedIndex = document.getElementById("identifierType").selectedIndex
    Object.keys(this.state.identifierTypes).map(index => {
      if(this.state.identifierTypes[index].uuid === options[selectedIndex].value){
        this.setState({
          identifierTypeValidator: this.state.identifierTypes[index].validator
        });
      }
    })
    this.setState({
      identifierType: options[selectedIndex].text,
      identifierTypeUuid: options[selectedIndex].value
    }, () => {
      this.getSource()
    });

  }

  getSource() {
    switch(document.getElementById("sourceType").value){
      case "org.openmrs.module.idgen.SequentialIdentifierGenerator":
        this.setState({
          modalContent: 1,
          title: "Local Identifier Generator"
        });
        break;
      case "org.openmrs.module.idgen.RemoteIdentifierSource":
        this.setState({
          modalContent: 2,
          title: "Remote Identifier Source"
        });
        break;
      case "org.openmrs.module.idgen.IdentifierPool":
        this.setState({
          modalContent: 3,
          title: "Local Pool Identifiers"
        });
        break;
      default:
        this.setState({
          modalContent: 'Please select a Source Type',
          title: "New Patient Identifier Source"
        });
    }
  }

  render(){

    let modalComponent;
    switch(this.state.modalContent){
      case 1:
        modalComponent = <LocalIdentifierGenerator 
        ref={saveForm => (this.saveForm = saveForm)}
        handleNewIdentifierSource={this.handleNewIdentifierSource.bind(this)} 
        identifierTypeUuid={this.state.identifierTypeUuid} 
        validator={this.state.identifierTypeValidator}
        handleAlerts={this.handleAlerts.bind(this)}  
        title={this.state.identifierType} />
        break;
      case 2:
        modalComponent = <RemoteIdentifierSource 
        ref={saveForm => (this.saveForm = saveForm)}
        handleNewIdentifierSource={this.handleNewIdentifierSource.bind(this)} 
        identifierTypeUuid={this.state.identifierTypeUuid} 
        validator={this.state.identifierTypeValidator}
        handleAlerts={this.handleAlerts.bind(this)}  
        title={this.state.identifierType} />
        break;
      case 3:
        modalComponent = <LocalPoolIdentifiers 
        ref={saveForm => (this.saveForm = saveForm)}
        handleNewIdentifierSource={this.handleNewIdentifierSource.bind(this)} 
        identifierTypeUuid={this.state.identifierTypeUuid} 
        validator={this.state.identifierTypeValidator}
        handleAlerts={this.handleAlerts.bind(this)}  
        title={this.state.identifierType} />
        break;
      default:
        modalComponent = this.state.modalContent
        break;
    }

    return (

      <div className="managePatientIdentifierSourcesContainer">
        <div className="managePatientIdentifierSourcesHeader">Add a new Patient Identifier Source</div>
        <Toast ref={toast => (this.toast = toast)} />
        <div>
          <Table size="sm" responsive>
            <thead>
              <tr>
                <th>Field</th>
                <th>Value</th>
              </tr>
            </thead>
            <tbody>
              <tr className="managePatientIdentifierSourcesRow">
                <td>Identifier Type:</td>
                <td>
                  <Input type="select" className="managePatientIdentifierSourcesSelect" name="identifierType" id="identifierType"
                         onChange={this.getIdentifier}>
                    {
                      Object.keys(this.state.identifierTypes).map(index => {
                        return(<option key={index} value={this.state.identifierTypes[index].uuid}>{this.state.identifierTypes[index].display}</option>);
                      })
                    } 
                  </Input>
                </td>
              </tr>
              <tr className="managePatientIdentifierSourcesRow">
                <td>Source Type:</td>
                <td>
                  <Input type="select" className="managePatientIdentifierSourcesSelect" name="sourceType" id="sourceType"
                         onChange={this.getSource}>
                    <option value="org.openmrs.module.idgen.SequentialIdentifierGenerator">Local Identifier
                      Generator
                    </option>
                    <option value="org.openmrs.module.idgen.RemoteIdentifierSource">Remote Identifier Source
                    </option>
                    <option value="org.openmrs.module.idgen.IdentifierPool">Local Pool of Identifiers</option>
                  </Input>
                </td>
              </tr>
              <tr className="managePatientIdentifierSourcesRow">
                <td><Button onClick={this.toggle} color="success">Add</Button></td>
                <td></td>
              </tr>
            </tbody>
          </Table>
        </div>
        <Modal className="modal-dialog modal-lg" isOpen={this.state.modal} toggle={this.toggle}>
          <ModalHeader toggle={this.toggle}>{this.state.title}</ModalHeader>
          <ModalBody>
            {modalComponent}
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.handleSave}>Save</Button>{' '}
            <Button color="secondary" onClick={this.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
);
  }
}
