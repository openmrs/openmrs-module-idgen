import React, {Component} from 'react';
import { Table, Input, Button, Form, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import LocalIdentifierGenerator from './new_patient_identifier_sources/localIdentifierGenerator'
import LocalPoolIdentifiers from './new_patient_identifier_sources/localPoolIdentifiers'
import RemoteIdentifierSource from './new_patient_identifier_sources/remoteIdentifierSource'

export default class NewPatientIdentifierSources extends Component {

  constructor(props) {
    super(props);
    this.state = {
      modal: false,
      modalContent: 'Please select a Source Type',
      title: 'New Patient Identifier Source',
      identifierType: 'Please select an identifier Type'
    };

    this.toggle = this.toggle.bind(this);
    this.getSource = this.getSource.bind(this);
    this.getIdentifier = this.getIdentifier.bind(this);
  }

  toggle() {
    this.setState((prevState) => { return {
      modal: !prevState.modal
    }});
  }

  getIdentifier(){
    let options = document.getElementById("identifierType").options
    let selectedIndex = document.getElementById("identifierType").selectedIndex
    this.setState({
      identifierType: options[selectedIndex].text
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
        modalComponent = <LocalIdentifierGenerator title={this.state.identifierType} />
        break;
      case 2:
        modalComponent = <RemoteIdentifierSource title={this.state.identifierType} />
        break;
      case 3:
        modalComponent = <LocalPoolIdentifiers title={this.state.identifierType} />
        break;
      default:
        modalComponent = this.state.modalContent
        break;
    }

    return (

      <div className="managePatientIdentifierSourcesContainer">
        <div className="managePatientIdentifierSourcesHeader">Add a new Patient Identifier Source</div>
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
                    <option value=""></option>
                    <option value="3">OpenMRS ID</option>
                    <option value="2">Old Identification Number</option>
                    <option value="1">OpenMRS Identification Number</option>
                  </Input>
                </td>
              </tr>
              <tr className="managePatientIdentifierSourcesRow">
                <td>Source Type:</td>
                <td>
                  <Input type="select" className="managePatientIdentifierSourcesSelect" name="sourceType" id="sourceType"
                         onChange={this.getSource}>
                    <option value=""></option>
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
            <Button color="primary" onClick={this.toggle}>Save</Button>{' '}
            <Button color="secondary" onClick={this.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
);
  }
}
