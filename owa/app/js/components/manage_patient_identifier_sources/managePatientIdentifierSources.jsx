import React, {Component} from 'react';
import NewPatientIdentifierSources from './newPatientIdentifierSources';
import ExistingIdentifierSources from './existingIdentifierSources';
import './managePatientIdentifierSources.css';

export default class ManagePatientIdentifierSources extends Component {

  constructor(props){
    super(props);
    this.state = {
      newIdentifierSource: []
    }
  }

  handleNewIdentifierSource(identifierSource){
    this.setState({
      newIdentifierSource: identifierSource
    });
  }

  render() {

    return (

      <div>
        <div className="managePatientIdentifierSourcesMainHeader"></div>
        <ExistingIdentifierSources newIdentifierSource={this.state.newIdentifierSource}/>
        <NewPatientIdentifierSources handleNewIdentifierSource={this.handleNewIdentifierSource.bind(this)}/>
      </div>
    );
  }
}
