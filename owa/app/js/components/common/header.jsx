/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import {Link, IndexLink} from 'react-router';
import apiCall from '../../utilities/apiHelper';
import imageFile from '../../../img/openmrs-with-title-small.png';
import { UncontrolledNavDropdown, DropdownToggle, DropdownMenu, DropdownItem, 
        Collapse, Navbar, NavbarToggler, NavbarBrand, Nav, NavItem, 
        NavLink } from 'reactstrap';
import {FaSignOut, FaUser,FaMapMarker} from 'react-icons/lib/fa';


const image = {
        src: imageFile,
        alt: 'my image', 
    };
export default class Header extends React.Component {
  constructor(props) {
    super(props);
    this.toggle = this.toggle.bind(this);
    this.state = {
      dropdownOpen: false,
      locationTags: [],
      currentLocationTag: "",
      currentUser: "",
      currentLogOutUrl: "",
    };
    this.getUri = this.getUri.bind(this);
  }

  toggle() {
    this.setState({
      dropdownOpen: !this.state.dropdownOpen,
    });
  }

  /**
   * Gets the OpenMrs configuration and sets the logout link
   * @memberOf Header
   */
  componentWillMount() {
    apiCall(null, 'get', '/location').then((response) => {
      this.setState({locationTags: response.results});
      this.setState({currentLocationTag: response.results[0].display});
      this.getUri();
    });

    apiCall(null, 'get', '/session').then((response) => {
      this.setState({currentUser: response.user.display});
    });
  }

  /**
   * Gets logout Url 
   * 
   * @returns {string} - logout url
   */  
  getUri() {
    this.state.locationTags.map((location) => {
      let url = location.links[0].uri;
      let arrUrl = url.split("/");
      let customUrl = `/${arrUrl[3]}/appui/header/logout.action?successUrl=${arrUrl[3]}`;
      this.setState({currentLogOutUrl: customUrl});
      return customUrl;
        
    });
  }
  render() {
    return (
        <div >
        <Navbar color="#009384" light toggleable>
          <NavbarToggler right onClick={this.toggle} />
          <NavbarBrand href="../../"><img src={image.src} alt={image.alt}/></NavbarBrand>
          <Collapse isOpen={this.state.isOpen} navbar>
            <Nav className="ml-auto" navbar>
              <NavItem>
                <Link to="" activeClassName="active">
                                    <UncontrolledNavDropdown>
                                        <DropdownToggle nav caret> <FaUser/>
                                         {' ' + this.state.currentUser}
                                        </DropdownToggle>
                                        <DropdownMenu>
                                          <DropdownItem header><a href="#">My Account</a></DropdownItem>
                                        </DropdownMenu>
                                      </UncontrolledNavDropdown>
                     
                    </Link>
              </NavItem>
              <NavItem>
                    <Link to="" activeClassName="active">
                                    <UncontrolledNavDropdown>
                                        <DropdownToggle nav caret> <FaMapMarker/>
                                         {' ' + this.state.currentLocationTag}
                                        </DropdownToggle>
                                        <DropdownMenu>
                                          {this.state.locationTags.map(location =><DropdownItem header><a href="#">{location.display}</a></DropdownItem>)}
                                        </DropdownMenu>
                                      </UncontrolledNavDropdown>
                     
                    </Link>
              </NavItem>
              <NavItem>
                    <Link to="" activeClassName="active">
                    <NavLink href={this.state.currentLogOutUrl}>Logout {' '} <FaSignOut/></NavLink>
                    </Link>
              </NavItem>
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    )
  }
}