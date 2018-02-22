/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React, {Component} from 'react';
import SweetAlert from 'sweetalert2-react';

export default class Toast extends Component {
    
    constructor(props) {
        super(props);
        this.state={
            show: false,
            title: '',
            type: 'info',
            message: '' 
        }
        this.toastSuccess = this.toastSuccess.bind(this);
        this.toastError = this.toastError.bind(this);
        this.addAlert = this.addAlert.bind(this);
    }

    addAlert(title, message, type){
        this.setState({title: title, message: message, type:type, show: true});
    };

    toastSuccess(message) {
        this.addAlert("Success", message, "success");
    }

    toastError(message) {
        this.addAlert("Error", message, "error");
    }

    render() {
        return (
            <div>
                <SweetAlert
                show={this.state.show}
                type={this.state.type}
                title={this.state.title}
                text={this.state.message}
                onConfirm={() => this.setState({ show: false })}
                onEscapeKey={() => this.setState({ show: false })}
                onOutsideClick={() => this.setState({ show: false })}
              />
            </div>
        );
    }
}
