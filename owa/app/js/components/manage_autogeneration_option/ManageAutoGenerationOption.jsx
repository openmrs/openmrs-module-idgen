import React, { Component } from 'react';
import {
  Button,
  Container,
  Row,
  Col,
  FormGroup,
  Label,
  Input,
  Table,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Alert
} from 'reactstrap';
import { MdDelete } from 'react-icons/lib/md';
import axios from 'axios';
import swal from 'sweetalert2';
import apiCall from '../../utilities/apiHelper';
import './css/manage-autogeneration.css';

class ManageAutoGenerationOption extends Component {
  constructor(props) {
    super(props);
    this.state = {
      genOptions: [],
      identifierTypes: [],
      locations: [],
      genOptionAutogenerateSources: [],
      modal: false,
      firstLevelCompleted: false,
      currentGenOption: {
        identifierType: '',
        currentLocation: '',
        autogenerateSource: '',
        autoGenerationEnabled: false,
        manualEntryEnabled: false
      },
      saveGenOptionStatus: 'new',
      errors: {},
      alertVisible: false
    };
    this.getGenDetails = this.getGenDetails.bind(this);
    this.toggle = this.toggle.bind(this);
    this.createGenOptions = this.createGenOptions.bind(this);
    this.deleteGenOption = this.deleteGenOption.bind(this);
    this.nextStage = this.nextStage.bind(this);
    this.onDismiss = this.onDismiss.bind(this);
    this.onChange = this.onChange.bind(this);
    this.saveNewAutoGenOptions = this.saveNewAutoGenOptions.bind(this);
    this.editCurrentGenOption = this.editCurrentGenOption.bind(this);
    this.loadAutoGenerationOptions = this.loadAutoGenerationOptions.bind(this);
    this.loadLocations = this.loadLocations.bind(this);
    this.loadIdentifierTypes = this.loadIdentifierTypes.bind(this);
    this.loadSources = this.loadSources.bind(this);
  }

  componentDidMount() {
    this.loadLocations();
    this.loadAutoGenerationOptions();
    this.loadIdentifierTypes();
    this.loadSources();
  }

  loadLocations() {
    apiCall(null, 'get', '/location').then(response => {
      this.setState({
        locations: response.results
      });
    });
  }

  loadAutoGenerationOptions() {
    apiCall(
      null,
      'get',
      '/idgen/autogenerationoption?v=full'
    ).then(response => {
      this.setState({
        genOptions: response.results
      });
    });
  }

  loadIdentifierTypes() {
    apiCall(null, 'get', '/patientidentifiertype?v=full').then(response => {
      this.setState({
        identifierTypes: response.results
      });
    });
  }

  loadSources() {
    apiCall(null, 'get', '/idgen/identifiersource').then(response => {
      this.setState({
        genOptionAutogenerateSources: response.results
      });
    });
  }

  getGenDetails(id) {
    const selectedGenOption = this.state.genOptions[id];
    this.setState({
      saveGenOptionStatus: 'edit',
      firstLevelCompleted: true,
      modal: true,
      currentGenOption: {
        id,
        uuid: selectedGenOption.uuid,
        identifierType: selectedGenOption.identifierType.uuid,
        currentLocation: selectedGenOption.location
          ? selectedGenOption.location.uuid
          : '',
        autogenerateSource: selectedGenOption.source.uuid,
        autoGenerationEnabled: selectedGenOption.automaticGenerationEnabled,
        manualEntryEnabled: selectedGenOption.manualEntryEnabled
      }
    });
  }

  editCurrentGenOption() {
    const { genOptions, currentGenOption } = this.state;
    const {
      id,
      uuid,
      identifierType,
      currentLocation,
      autogenerateSource,
      autoGenerationEnabled,
      manualEntryEnabled
    } = currentGenOption;
    console.log(uuid);
    apiCall(
      {
        uuid,
        identifierType: identifierType,
        location: currentLocation,
        source: autogenerateSource,
        automaticGenerationEnabled: autoGenerationEnabled,
        manualEntryEnabled
      },
      'post',
      `/idgen/autogenerationoption/${uuid}`
    ).then(res => {
      swal({
        title: 'Success',
        html: 'Edit successfull',
        type: 'success',
        allowOutsideClick: false
      }).then(() => {
        this.loadAutoGenerationOptions();
        this.setState({
          modal: false
        });
      });
    });
  }

  onChange(e) {
    let currentGenOption;
    if (
      e.target.name === 'autoGenerationEnabled' ||
      e.target.name === 'manualEntryEnabled'
    ) {
      currentGenOption = Object.assign({}, this.state.currentGenOption, {
        [e.target.name]: e.target.checked
      });
    } else {
      console.log(e.target.value);
      currentGenOption = Object.assign({}, this.state.currentGenOption, {
        [e.target.name]: e.target.value
      });
    }
    this.setState({
      alertVisible: false,
      currentGenOption
    });
  }

  deleteGenOption(id) {
    const display = this.state.genOptions[id].identifierType.display;
    swal({
      title: 'Confirm Delete?',
      text: `Do you really want to delete the "${display}" autogeneration option?`,
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#88af28',
      cancelButtonColor: '#ff3d3d',
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      reverseButtons: true,
      buttonsStyling: true,
      allowOutsideClick: false
    }).then(() => {
      const uuid = this.state.genOptions[id].uuid;
      apiCall(
        null,
        'delete',
        `/idgen/autogenerationoption/${uuid}?purge=true`
      ).then(
        res => {
          swal({
            title: 'Success',
            html: `"${display}" autogeneration option deleted successfully!`,
            type: 'success',
            allowOutsideClick: true
          });
          this.loadAutoGenerationOptions();
        },
        () => {
          swal({
            title: 'Delete not successful',
            html:
              'The Resource Does not Support the Requested Operation [Deleting of AutoGenerationOption is not supported]',
            type: 'error',
            allowOutsideClick: true
          });
        }
      );
    });
  }

  createGenOptions() {
    this.setState({
      saveGenOptionStatus: 'new',
      modal: true,
      firstLevelCompleted: false,
      currentGenOption: {
        identifierType: '',
        currentLocation: '',
        autogenerateSource: '',
        autoGenerationEnabled: false,
        manualEntryEnabled: false
      }
    });
  }

  toggle() {
    this.setState({
      modal: !this.state.modal
    });
  }

  nextStage() {
    const currentGenOption = this.state.currentGenOption;
    if (currentGenOption.identifierType === '') {
      this.setState({
        errors: {
          message: 'Please select an identifier type'
        },
        alertVisible: true
      });
    } else {
      this.setState({
        firstLevelCompleted: true,
        currentGenOption: {
          identifierType: currentGenOption.identifierType,
          currentLocation: currentGenOption.currentLocation,
          autogenerateSource: currentGenOption.autogenerateSource,
          autoGenerationEnabled: currentGenOption.autoGenerationEnabled,
          manualEntryEnabled: currentGenOption.manualEntryEnabled
        }
      });
    }
  }

  onDismiss() {
    this.setState({ alertVisible: false });
  }

  saveNewAutoGenOptions() {
    const { currentGenOption, genOptions } = this.state;
    if (
      currentGenOption.currentLocation === '' ||
      currentGenOption.autogenerateSource === ''
    ) {
      this.setState({
        errors: {
          message: 'All fields are required'
        },
        alertVisible: true
      });
    } else {
      const {
        id,
        identifierType,
        currentLocation,
        autogenerateSource,
        autoGenerationEnabled,
        manualEntryEnabled
      } = currentGenOption;
      apiCall(
        {
          identifierType: identifierType,
          location: currentLocation,
          source: autogenerateSource,
          automaticGenerationEnabled: autoGenerationEnabled,
          manualEntryEnabled
        },
        'post',
        '/idgen/autogenerationoption'
      ).then(res => {
        swal({
          title: 'Success',
          html: 'New AutoGeneration Option added successfully',
          type: 'success',
          allowOutsideClick: true
        });
        this.loadAutoGenerationOptions();
        this.setState({
          modal: false,
          currentGenOption: {
            firstLevelCompleted: false,
            identifierType: '',
            currentLocation: '',
            autogenerateSource: '',
            autoGenerationEnabled: false,
            manualEntryEnabled: false
          }
        });
      });
    }
  }

  render() {
    const {
      genOptions,
      currentGenOption,
      identifierTypes,
      saveGenOptionStatus,
      locations,
      genOptionAutogenerateSources,
      errors,
      firstLevelCompleted
    } = this.state;
    let allGenOptions;
    let allIdentifierTypes;
    let allLocations;
    let AllAutoGenerateSources;
    if (genOptions.length > 0) {
      allGenOptions = genOptions.map((genOption, i) => (
        <tr key={i}>
          <th scope="row">{i + 1}</th>
          <td onClick={() => this.getGenDetails(i)}>
            {genOption.identifierType.display}
          </td>
          <td onClick={() => this.getGenDetails(i)}>
            {genOption.location ? genOption.location.display : 'No location'}
          </td>
          <td onClick={() => this.getGenDetails(i)}>{genOption.source.name}</td>
          <td onClick={() => this.getGenDetails(i)}>
            {genOption.manualEntryEnabled.toString()}
          </td>
          <td onClick={() => this.getGenDetails(i)}>
            {genOption.automaticGenerationEnabled.toString()}
          </td>
          <td onClick={() => this.deleteGenOption(i)}>
            <MdDelete />
          </td>
        </tr>
      ));
    } else {
      allGenOptions = (
        <tr>
          <td colSpan="3">Nothing to show</td>
        </tr>
      );
    }

    if (identifierTypes.length > 0) {
      allIdentifierTypes = identifierTypes.map((identifierType, i) => (
        <option key={i} value={identifierType.uuid}>
          {identifierType.display}
        </option>
      ));
    } else {
      allIdentifierTypes = <option value="" />;
    }

    if (locations.length > 0) {
      allLocations = locations.map((location, i) => (
        <option key={i} value={location.uuid}>
          {location.display}
        </option>
      ));
    } else {
      allLocations = <option value="" />;
    }

    if (genOptionAutogenerateSources.length > 0) {
      AllAutoGenerateSources = genOptionAutogenerateSources.map(
        (genOptionAutogenerateSource, i) => (
          <option key={i} value={genOptionAutogenerateSource.uuid}>
            {genOptionAutogenerateSource.display}
          </option>
        )
      );
    } else {
      AllAutoGenerateSources = <option value="" />;
    }

    return (
      <div className="mao-content">
        <Container>
          <Row id="addNewCont">
            <Button id="addBtn" color="primary" onClick={this.createGenOptions}>
              Add New
            </Button>
          </Row>
          <Row id="tableCont">
            <Col sm="12" id="tableTitle">
              <h2>
                Showing 1 - {genOptions.length} of {genOptions.length}
              </h2>
            </Col>

            <Col className="tableContent" sm="12">
              <Row>
                <Table size="sm" striped responsive hover>
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Identifier Type</th>
                      <th>Location</th>
                      <th>Source Name</th>
                      <th>Manual Entry Enabled?</th>
                      <th>Automatic Generation Enabled?</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>{allGenOptions}</tbody>
                </Table>
              </Row>
              <Row>
                <div className="modalDiv">
                  <Modal
                    isOpen={this.state.modal}
                    toggle={this.toggle}
                    className={this.props.className}
                  >
                    <ModalHeader toggle={this.toggle}>
                      {!firstLevelCompleted || saveGenOptionStatus === 'new' ? (
                        'Add new'
                      ) : (
                        <span>Edit&nbsp;</span>
                      )}
                      &nbsp;
                      <span>Auto Generation Option </span>
                    </ModalHeader>
                    <ModalBody>
                      <Alert
                        color="danger"
                        isOpen={this.state.alertVisible}
                        toggle={this.onDismiss}
                      >
                        {errors.message}
                      </Alert>
                      {!firstLevelCompleted ? (
                        <div>
                          <FormGroup>
                            <Label for="identifier">Identifier Type: </Label>
                            <Input
                              id="identifier"
                              onChange={this.onChange}
                              type="select"
                              defaultValue={currentGenOption.identifierType}
                              name="identifierType"
                              className="myselect"
                            >
                              <option value="">Select Identifier type</option>
                              {allIdentifierTypes}
                            </Input>
                          </FormGroup>
                        </div>
                      ) : (
                        <div>
                          <FormGroup>
                            <Label for="location">Location: </Label>
                            <Input
                              id="location"
                              onChange={this.onChange}
                              type="select"
                              defaultValue={currentGenOption.currentLocation}
                              name="currentLocation"
                              className="myselect"
                              value={currentGenOption.currentLocation}
                            >
                              <option value="">Select a location</option>
                              {allLocations}
                            </Input>
                          </FormGroup>
                          <FormGroup>
                            <Label>Source to auto generate from: </Label>
                            <Input
                              onChange={this.onChange}
                              type="select"
                              defaultValue={currentGenOption.autogenerateSource}
                              name="autogenerateSource"
                              className="myselect"
                            >
                              <option value="">Select a source</option>
                              {AllAutoGenerateSources}
                            </Input>
                          </FormGroup>
                          <FormGroup>
                            <Label>Options: </Label>
                          </FormGroup>
                          <div className="optionsCont">
                            <FormGroup>
                              <Label check>
                                <Input
                                  onClick={this.onChange}
                                  name="autoGenerationEnabled"
                                  checked={
                                    currentGenOption.autoGenerationEnabled
                                  }
                                  type="checkbox"
                                />
                                <span> </span>
                                Automatic Generation Enabled?
                              </Label>
                            </FormGroup>
                            <FormGroup>
                              <Label check>
                                <Input
                                  onClick={this.onChange}
                                  name="manualEntryEnabled"
                                  checked={currentGenOption.manualEntryEnabled}
                                  type="checkbox"
                                />
                                <span> </span>
                                Manual Entry Enabled?
                              </Label>
                            </FormGroup>
                          </div>
                        </div>
                      )}
                    </ModalBody>
                    <ModalFooter>
                      {!firstLevelCompleted ? (
                        <Button
                          color="primary"
                          onClick={this.nextStage}
                          className="color-green"
                        >
                          <span>Next</span>
                        </Button>
                      ) : (
                        <Button
                          color="primary"
                          className="color-green"
                          onClick={
                            saveGenOptionStatus === 'new'
                              ? this.saveNewAutoGenOptions
                              : this.editCurrentGenOption
                          }
                        >
                          <span>Save</span>
                        </Button>
                      )}
                      <span> </span>
                      <Button color="secondary" onClick={this.toggle}>
                        Cancel
                      </Button>
                    </ModalFooter>
                  </Modal>
                </div>
              </Row>
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}

export default ManageAutoGenerationOption;
