import React, {Component} from 'react';
import {Table, Button, Modal, ModalHeader, ModalBody, ModalFooter} from 'reactstrap';
import ViewSource from './viewSource';
import ConfigureSource from './configureSource';

export default class ExistingIdentifierSources extends Component {

  constructor(props) {
    super(props);
    this.state = {
      modal: false,
      modalContent: '',
      identifierSources: [{
        identifierType: ['OpenMRS ID'],
        sourceType: ['Local Identifier Generator'],
        sourceName: ['New Source']
      }]
    };

    this.toggle = this.toggle.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  toggle() {
    this.setState((prevState) => { return {
      modal: !prevState.modal
    }});
  }

  handleClick(event){
    if(event.target.name === 'configureButton'){
      this.setState({
        modalContent: 1
      });
    }else{
      this.setState({
        modalContent: 2
      });
    }
    this.toggle()
  }

  render() {

    let modalComponent;
    switch (this.state.modalContent){
        case 1:
          modalComponent = <ConfigureSource />
          break;
        case 2:
            modalComponent = <ViewSource />
            break;
        default:
          modalComponent = this.state.modalContent
          break;
    }

    return (

      <div className="managePatientIdentifierSourcesContainer">
        <div className="managePatientIdentifierSourcesHeader">Existing Identifier Sources</div>
        <Table size="sm" responsive>
          <thead>
            <tr>
              <th>Identifier Type</th>
              <th>Source Type</th>
              <th>Source Name</th>
              <th>Actions</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {
              Object.keys(this.state.identifierSources).map(index => {
                return (
                  <tr className="managePatientIdentifierSourcesRow" key={index}>
                    <td>{this.state.identifierSources[index].identifierType}</td>
                    <td>{this.state.identifierSources[index].sourceType}</td>
                    <td>{this.state.identifierSources[index].sourceName}</td>
                    <td>
                      <Button onClick={this.handleClick} name="configureButton" color="success">Configure</Button>
                    </td>
                    <td>
                      <Button onClick={this.handleClick} name="viewButton" color="success">View</Button>
                    </td>
                  </tr>
                );
              })
            }
          </tbody>
        </Table>
        <Modal className="modal-dialog modal-lg" isOpen={this.state.modal} toggle={this.toggle}>
          <ModalHeader toggle={this.toggle}>Existing Identifier Sources</ModalHeader>
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
