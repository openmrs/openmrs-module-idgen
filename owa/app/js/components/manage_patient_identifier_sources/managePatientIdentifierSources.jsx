import React, {Component} from 'react';
import NewPatientIdentifierSources from './newPatientIdentifierSources';
import ExistingIdentifierSources from './existingIdentifierSources';
import './managePatientIdentifierSources.css';

export default class ManagePatientIdentifierSources extends Component {

  render() {

    return (

      <div>
        <div className="mpisMainHeader">Manage Patient Identifier Sources</div>
        <ExistingIdentifierSources/>
        <NewPatientIdentifierSources/>
      </div>
    );
  }
}