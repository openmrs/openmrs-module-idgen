import React, {Component} from 'react';
import {Table, Button, Modal, ModalHeader, ModalBody, ModalFooter} from 'reactstrap';
import EditLocalPoolIdentifiers from './edit_patient_identifier_sources/localPoolIdentifiers';
import EditLocalIdentifierGenerator from './edit_patient_identifier_sources/localIdentifierGenerator';
import EditRemoteIdentifierSource from './edit_patient_identifier_sources/remoteIdentifierSource';
import ViewLocalPoolIdentifiers from './view_patient_identifier_sources/localPoolIdentifiers';
import ViewLocalIdentifierGenerator from './view_patient_identifier_sources/localIdentifierGenerator';
import ViewRemoteIdentifierSource from './view_patient_identifier_sources/remoteIdentifierSource';
import apiCall from '../../utilities/apiHelper';
import Toast from '../../utilities/toast';

export default class ExistingIdentifierSources extends Component {

  constructor(props) {
    super(props);
    this.state = {
      modal: false,
      modalContent: '',
      identifierSource: '',
      identifierSources: [],
      saveButtonDisplay: 'block'
    };

    this.toggle = this.toggle.bind(this);
    this.handleClick = this.handleClick.bind(this);
    this.handleUpdate = this.handleUpdate.bind(this);
  }

  componentDidMount(){
    this.fetchIdentifierSources();
  }

  componentWillReceiveProps(nextProps){
    let existingSources = this.state.identifierSources;
    existingSources.push(nextProps.newIdentifierSource)
    this.setState({
      identifierSources: existingSources
    });
  }

  toggle() {
    this.setState((prevState) => { return {
      modal: !prevState.modal
    }});
  }

  handleUpdateName(index, name){
    let existingSources = this.state.identifierSources;
    existingSources[index].name = name;
    this.setState({identifierSources: existingSources});
  }

  handleAlerts(alertType, message){
    if(alertType === "success"){
      this.toast.toastSuccess(message);
    }else{
      this.toast.toastError(message);
    }
  }

  handleUpdate() {
    this.updateForm.handleSubmit();
    this.toggle();
  }

  fetchIdentifierSources(){
    apiCall(null, 'get', '/idgen/identifiersource?v=custom:uuid,name,description,identifierType,class').then((response) => {
      this.setState({identifierSources: response.results});
    });
  }

  handleClick(event, identifierSource, index){
    if(event.target.name === 'configureButton' && identifierSource.class == "org.openmrs.module.idgen.SequentialIdentifierGenerator"){
      apiCall(
        null,
        'get', 
        '/idgen/identifiersource/'+identifierSource.uuid+'/?v=custom:uuid,identifierType,name,description,baseCharacterSet,firstIdentifierBase,prefix,suffix,minLength,maxLength,initialized'
      ).then((response) => {
        response.identifierTypeName = response.identifierType.name;
        response.identifierTypeValidator = response.identifierType.validator;
        response.index = index;
        delete response.identifierType;
        
        this.setState({
          modalContent: 1,
          saveButtonDisplay: 'block',
          identifierSource: response
        }, ()=>this.toggle());
      });
    }else if(event.target.name === 'configureButton' && identifierSource.class == "org.openmrs.module.idgen.RemoteIdentifierSource"){
      apiCall(
        null,
        'get', 
        '/idgen/identifiersource/'+identifierSource.uuid+'/?v=custom:uuid,name,description,identifierType,url,user,password'
      ).then((response) => {
        response.identifierTypeName = response.identifierType.name;
        response.identifierTypeValidator = response.identifierType.validator;
        response.index = index;
        delete response.identifierType;
        
        this.setState({
          modalContent: 2,
          saveButtonDisplay: 'block',
          identifierSource: response
        }, ()=>this.toggle());
      });
    }else if(event.target.name === 'configureButton' && identifierSource.class == "org.openmrs.module.idgen.IdentifierPool"){
      apiCall(
        null,
        'get', 
        '/idgen/identifiersource/'+identifierSource.uuid+'/?v=custom:uuid,name,identifierType,description,source,sequential,refillWithScheduledTask,batchSize,minPoolSize'
      ).then((response) => { 
        response.identifierTypeName = response.identifierType.name;
        response.identifierTypeUuid = response.identifierType.uuid;
        response.identifierTypeValidator = response.identifierType.validator;
        response.index = index;
        delete response.identifierType;
        
        response.sourceName = response.source != null ? response.source.name : null;
        response.sourceUuid = response.source != null ? response.source.uuid : null;
        delete response.source;
        
        this.setState({
          modalContent: 3,
          saveButtonDisplay: 'block',
          identifierSource: response
        }, ()=>this.toggle());
      });
    }else if (event.target.name === 'viewButton' && identifierSource.class == "org.openmrs.module.idgen.SequentialIdentifierGenerator"){
      apiCall(
        null,
        'get', 
        '/idgen/identifiersource/'+identifierSource.uuid+'/?v=custom:uuid,identifierType,name,description,baseCharacterSet,firstIdentifierBase,prefix,suffix,minLength,maxLength,reservedIdentifiers'
      ).then((response) => {
        response.identifierTypeName = response.identifierType.name;
        response.identifierTypeValidator = response.identifierType.validator;
        response.index = index;
        delete response.identifierType;
        
        this.setState({
          modalContent: 4,
          saveButtonDisplay: 'none',
          identifierSource: response
        }, ()=>this.toggle());
      });
    }
    else if (event.target.name === 'viewButton' && identifierSource.class == "org.openmrs.module.idgen.RemoteIdentifierSource"){
      apiCall(
        null,
        'get', 
        '/idgen/identifiersource/'+identifierSource.uuid+'/?v=custom:uuid,name,description,identifierType,url,user,password'
      ).then((response) => {
        response.identifierTypeName = response.identifierType.name;
        response.identifierTypeValidator = response.identifierType.validator;
        response.index = index;
        delete response.identifierType;
        
        this.setState({
          modalContent: 5,
          saveButtonDisplay: 'none',
          identifierSource: response
        }, ()=>this.toggle());
      });
    }
    else if (event.target.name === 'viewButton' && identifierSource.class == "org.openmrs.module.idgen.IdentifierPool"){
      apiCall(
        null,
        'get', 
        '/idgen/identifiersource/' + 
        identifierSource.uuid + 
        '/?v=custom:uuid,name,identifierType,description,source,sequential,refillWithScheduledTask,batchSize,minPoolSize,usedIdentifiers,availableIdentifiers'
      ).then((response) => {
        response.identifierTypeName = response.identifierType.name;
        response.identifierTypeValidator = response.identifierType.validator;
        response.index = index;
        delete response.identifierType;
        
        response.sourceName = response.source != null ? response.source.name : null;
        response.sourceUuid = response.source != null ? response.source.uuid : null;
        delete response.source;
        
        this.setState({
          modalContent: 6,
          saveButtonDisplay: 'none',
          identifierSource: response
        }, ()=>this.toggle());
      });
    }
  }

  setSourceType(source){
    switch (source){
      case "org.openmrs.module.idgen.SequentialIdentifierGenerator":
        return "Local Identifier Generator";
        break;
      case "org.openmrs.module.idgen.RemoteIdentifierSource":
        return "Remote Identifier Source";
        break;
      case "org.openmrs.module.idgen.IdentifierPool":
        return "Local Pool of Identifiers";
        break;      
    }
  }

  render() {

    let modalComponent;
    switch (this.state.modalContent){
        case 1:
          modalComponent = <EditLocalIdentifierGenerator
          handleUpdateName={this.handleUpdateName.bind(this)} 
          handleAlerts={this.handleAlerts.bind(this)} 
          ref={updateForm => (this.updateForm = updateForm)} 
          identifierSource={this.state.identifierSource} />
          break;
        case 2:
          modalComponent = <EditRemoteIdentifierSource
          handleUpdateName={this.handleUpdateName.bind(this)} 
          handleAlerts={this.handleAlerts.bind(this)} 
          ref={updateForm => (this.updateForm = updateForm)} 
          identifierSource={this.state.identifierSource} />
          break;
        case 3:
          modalComponent = <EditLocalPoolIdentifiers
          handleUpdateName={this.handleUpdateName.bind(this)} 
          handleAlerts={this.handleAlerts.bind(this)} 
          ref={updateForm => (this.updateForm = updateForm)} 
          identifierSource={this.state.identifierSource} />
          break;
        case 4:
          modalComponent = <ViewLocalIdentifierGenerator 
          handleAlerts={this.handleAlerts.bind(this)}
          ref={updateForm => (this.updateForm = updateForm)} 
          identifierSource={this.state.identifierSource} />
          break;
        case 5:
          modalComponent = <ViewRemoteIdentifierSource
          handleAlerts={this.handleAlerts.bind(this)} 
          ref={updateForm => (this.updateForm = updateForm)} 
          identifierSource={this.state.identifierSource} />
          break;
        case 6:
          modalComponent = <ViewLocalPoolIdentifiers
          handleAlerts={this.handleAlerts.bind(this)} 
          ref={updateForm => (this.updateForm = updateForm)} 
          identifierSource={this.state.identifierSource} />
          break;
        default:
          modalComponent = this.state.modalContent
          break;
    }

    return (
      <div className="managePatientIdentifierSourcesContainer">
        <div className="managePatientIdentifierSourcesHeader">Existing Identifier Sources</div>
        <Toast ref={toast => (this.toast = toast)} />
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
                if(this.state.identifierSources[index].identifierType != null){
                  return (
                    <tr className="managePatientIdentifierSourcesRow" key={index}>
                      <td>{this.state.identifierSources[index].identifierType != null ? this.state.identifierSources[index].identifierType.name : ""}</td>
                      <td>{this.state.identifierSources[index].class != null ? this.setSourceType(this.state.identifierSources[index].class) : ""}</td>
                      <td>{this.state.identifierSources[index].name != null ? this.state.identifierSources[index].name : ""}</td>
                      <td>
                        <Button onClick={(event) => this.handleClick(event, this.state.identifierSources[index], index)} name="configureButton" color="success">Configure</Button>
                      </td>
                      <td>
                        <Button onClick={(event) =>this.handleClick(event, this.state.identifierSources[index], index)} name="viewButton" color="success">View</Button>
                      </td>
                    </tr>
                  );
                }
                
              })
            }
          </tbody>
        </Table>
        <Modal keyboard={false} className="modal-dialog modal-lg" isOpen={this.state.modal} toggle={this.toggle}>
          <ModalHeader toggle={this.toggle}>Existing Identifier Sources</ModalHeader>
          <ModalBody>
              {modalComponent}
          </ModalBody>
          <ModalFooter>
            <Button color="primary" style={{ display: `${ this.state.saveButtonDisplay }` }} onClick={this.handleUpdate}>Update</Button>{' '}
            <Button color="secondary" onClick={this.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}
