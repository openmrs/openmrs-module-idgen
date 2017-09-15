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

  //show the modal
  toggle() {
    this.setState({
      modal: !this.state.modal
    });
  }

  //handle clicks on the buttons
  handleClick(event){
    if(event.target.name === 'configureButton'){
      this.setState({
        modalContent: <ConfigureSource />
      });
    }else{
      this.setState({
        modalContent: <ViewSource />
      });
    }
    this.toggle()
  }

  render() {

    return (

      <div className="mpisContainer">
        <div className="mpisHeader">Existing Identifier Sources</div>
        <Table size="sm" responsive className="mpisTable">
          <thead>
            <tr>
              <th>Identifier Type</th>
              <th>Source Type</th>
              <th>Source Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {
              Object.keys(this.state.identifierSources).map(index => {
                return (
                  <tr className="mpisRow" key={index}>
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
        <Modal isOpen={this.state.modal} toggle={this.toggle}>
          <ModalHeader toggle={this.toggle}>Modal title</ModalHeader>
          <ModalBody>
            {
              this.state.modalContent
            }
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