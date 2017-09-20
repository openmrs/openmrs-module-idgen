import React, { Component } from 'react';
import { Button, Container, Row, Col, FormGroup, Label, Input, Table,
    Modal, ModalHeader, ModalBody, ModalFooter, Alert } from 'reactstrap';
import { MdDelete } from 'react-icons/lib/md';
import './css/manage-autogeneration.css';

class ManageAutoGenerationOption extends Component {
  constructor(props) {
    super(props);
    this.state = {
      genOptions: [
        {
          idType: 'OpenMRS ID',
          location: 'Armani Hospital',
          sourceName: 'Generator for OpenMRS ID',
          manualEntry: false,
          autoGen: true
        },
        {
          idType: 'OpenMRS ID',
          location: 'Buruju Hospital',
          sourceName: 'Generator for OpenMRS ID',
          manualEntry: false,
          autoGen: false
        },
        {
          idType: 'OpenMRS ID',
          location: 'Laboratory',
          sourceName: 'Generator for OpenMRS ID',
          manualEntry: false,
          autoGen: true
        }
      ],
      identifierTypes: [
        { id: 1, name: 'OpenMRS ID'},
        { id: 2, name: 'OpenMRS Identification Number'},
        { id: 3, name: 'Old Identification Number '}
      ],
      locations: [
        { id: 1, name: 'Armani Hospital' },
        { id: 2, name: 'Buruju Hospital' },
        { id: 3, name: 'Ocean view Clinic' },
        { id: 4, name: 'Indiana Out patient ward' },
        { id: 5, name: 'Laboratory' }
      ],
      genOptionAutogenerateSources: [
        { id: 1, name: 'Generator for OpenMRS ID' },
        { id: 2, name: 'Generator for Something Else' }
      ],
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
  }

  getGenDetails(id) {
    const selectedGenOption = this.state.genOptions[id];
    this.setState({
      saveGenOptionStatus: 'edit',
      firstLevelCompleted: true,
      modal: true,
      currentGenOption: {
        id,
        identifierType: selectedGenOption.idType,
        currentLocation: selectedGenOption.location,
        autogenerateSource: selectedGenOption.sourceName,
        autoGenerationEnabled: selectedGenOption.autoGen,
        manualEntryEnabled: selectedGenOption.manualEntry
      },
    });
  }

  editCurrentGenOption() {
    const { genOptions, currentGenOption } = this.state;
    const { id, identifierType, currentLocation, autogenerateSource,
        autoGenerationEnabled, manualEntryEnabled } = currentGenOption;
    const selectedGenOption = genOptions[id];
    selectedGenOption.idType = identifierType;
    selectedGenOption.location = currentLocation;
    selectedGenOption.sourceName = autogenerateSource;
    selectedGenOption.autoGen = autoGenerationEnabled;
    selectedGenOption.manualEntry = manualEntryEnabled;
    genOptions[id] = selectedGenOption; 
    this.setState({
      genOptions,
      modal: false
    });
  }

  onChange(e) {
    let currentGenOption;
    if (e.target.name === 'autoGenerationEnabled' || e.target.name === 'manualEntryEnabled') {
      currentGenOption = Object.assign({}, this.state.currentGenOption, { [e.target.name]: e.target.checked});
    } else {
      currentGenOption = Object.assign({}, this.state.currentGenOption, { [e.target.name]: e.target.value});
    }
    this.setState({
      alertVisible: false,
      currentGenOption
    })
  }

  deleteGenOption(id) {
    console.log('delete', id);
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
        alertVisible: true,
      })
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
    if (currentGenOption.currentLocation === '' || currentGenOption.autogenerateSource === '') {
      this.setState({
        errors: {
          message: 'All fields are required',
        },
        alertVisible: true
      })
    } else {
      const newIdentifierType = {
        idType: currentGenOption.identifierType,
        location: currentGenOption.currentLocation,
        sourceName: currentGenOption.autogenerateSource,
        manualEntry: currentGenOption.manualEntryEnabled,
        autoGen: currentGenOption.autoGenerationEnabled
      }
      genOptions.push(newIdentifierType);
      this.setState({
        genOptions
      }, () => {
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
        })
      });
    }
  }

  render() {
    const { genOptions, currentGenOption, identifierTypes, saveGenOptionStatus,
      locations, genOptionAutogenerateSources, errors, firstLevelCompleted } = this.state;
    let allGenOptions;
    let allIdentifierTypes;
    let allLocations;
    let AllAutoGenerateSources;
    if (genOptions.length > 0) {
      allGenOptions = genOptions.map((genOption, i) => (
        <tr key={i}>
          <th scope="row">{i + 1}</th>
          <td onClick={() => this.getGenDetails(i)}>{genOption.idType}</td>
          <td onClick={() => this.getGenDetails(i)}>{genOption.location}</td>
          <td onClick={() => this.getGenDetails(i)}>{genOption.sourceName}</td>
          <td onClick={() => this.getGenDetails(i)}>{genOption.manualEntry.toString()}</td>
          <td onClick={() => this.getGenDetails(i)}>{genOption.autoGen.toString()}</td>
          <td onClick={() => this.deleteGenOption(i)}><MdDelete /></td>
        </tr>
      ));
    } else {
      allGenOptions = (
        <tr>
          <p>Nothing to show</p>
        </tr>
      )
    }

    if (identifierTypes.length > 0) {
      allIdentifierTypes = identifierTypes.map((identifierType, i) => (
        <option key={i} value={identifierType.name}>{identifierType.name}</option>
      ));
    } else {
      allIdentifierTypes = (
        <option value=""></option>
      );
    }

    if (locations.length > 0) {
      allLocations = locations.map((location, i) => (
        <option key={i} value={location.name}>{location.name}</option>
      ));
    } else {
      allLocations = (
        <option value=""></option>
      );
    }

    if (genOptionAutogenerateSources.length > 0) {
      AllAutoGenerateSources = genOptionAutogenerateSources.map((genOptionAutogenerateSource, i) => (
        <option key={i} value={genOptionAutogenerateSource.name}>
          {genOptionAutogenerateSource.name}
        </option>
      ));
    } else {
      AllAutoGenerateSources = (
        <option value=""></option>
      );     
    }

    return (
      <div className="mao-content">
        <Container>
          <Row id="addNewCont">
            <Button id="addBtn" color="primary" onClick={this.createGenOptions}>Add New</Button>
          </Row>
          <Row id="tableCont">
            <Col sm="12" id="tableTitle">
              <h2>Showing 1 - 10 of 10 </h2>
            </Col>

            <Col className="tableContent" sm="12">
              <Row id="tableDiv">
                <Col sm="6">
                  <FormGroup>
                    <Input type="select" name="select">
                      <option>10</option>
                      <option>20</option>
                      <option>30</option>
                      <option>40</option>
                      <option>50</option>
                    </Input>
                  </FormGroup>
                </Col>

                <Col className="searchDiv" sm="6">
                  <FormGroup>
                    <Label id="label" for="exampleEmail"> 
                      <span>Search: </span>
                      <Input 
                        className="searchInput"
                        type="text"
                        name="search"
                        id="search"
                        placeholder="Search"
                      />
                    </Label>
                  </FormGroup>
                </Col>
              </Row>
              <Row sm="12">
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
                <tbody>
                  {allGenOptions}
                </tbody>
              </Table>
              </Row>
              <Row>
                <div className="modalDiv">
                  <Modal isOpen={this.state.modal} toggle={this.toggle} className={this.props.className}>
                    <ModalHeader toggle={this.toggle}>
                      { !firstLevelCompleted || saveGenOptionStatus === 'new' ? 'Add new' :
                        (<span><h3>{currentGenOption.identifierType} | </h3> Edit </span> )
                      } <span>Auto Generation Option </span>
                    </ModalHeader>
                    <ModalBody>
                      <Alert
                        color="danger"
                        isOpen={this.state.alertVisible}
                        toggle={this.onDismiss}
                      >
                        {errors.message}
                      </Alert>
                      { !firstLevelCompleted ? (
                        <div>
                          <FormGroup>
                            <Label for="identifier">Identifier Type: </Label>
                            <Input
                              id="identifier"
                              onChange={this.onChange}
                              type="select"
                              defaultValue={currentGenOption.identifierType}
                              name="identifierType"
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
                                  checked={currentGenOption.autoGenerationEnabled}
                                  type="checkbox"
                                /><span> </span>
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
                                /><span> </span>
                                Manual Entry Enabled?
                              </Label>
                            </FormGroup>
                          </div>
                        </div>
                      )}
                      
                    </ModalBody>
                    <ModalFooter>
                      { !firstLevelCompleted ? (
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
                          onClick={saveGenOptionStatus === 'new' ? this.saveNewAutoGenOptions :
                            this.editCurrentGenOption}
                        >
                          <span>Save</span>
                        </Button>
                      ) }
                      <span> </span>
                      <Button color="secondary" onClick={this.toggle}>Cancel</Button>
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

