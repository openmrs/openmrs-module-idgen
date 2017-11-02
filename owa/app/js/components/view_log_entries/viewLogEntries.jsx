/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import ReactTable from 'react-table';
import moment from 'moment';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import 'react-table/react-table.css';
import {FaSearch} from 'react-icons/lib/fa';
import {Input} from 'reactstrap';
import apiCall from '../../utilities/apiHelper';

export default class LogEntries extends React.Component {
  constructor() {
    super();
    this.state = {
      logEntries: [],
      buttonBorder: true,
      identifierSources: [],
      filteredLogEntries: '',
      startDate: '',
      endDate: moment(new Date()),
      dateChanged: false,
      generatedByValue: '',
      generatedBy: '',
      sourceValue: '',
      source: '',
      loading: false,
      searchFilters: {
        identifier: '',
        comment: ''
      }
    };
    this.getIdentifierSources = this.getIdentifierSources.bind(this);
    this.advancedSearch = this.advancedSearch.bind(this);
    this.handleSearch = this.handleSearch.bind(this);
    this.handleChangeStart = this.handleChangeStart.bind(this);
    this.handleChangeEnd = this.handleChangeEnd.bind(this);
    this.resetSearchForm = this.resetSearchForm.bind(this);
  }

  getIdentifierSources() {
    apiCall(null, 'get', '/idgen/identifiersource?v=full').then((response) => {
      this.setState({identifierSources: response.results});
    });
  }

  advancedSearch() {
    this.setState({loading: true})
    const {identifier, comment} = this.state.searchFilters;
    const endDate = this.state.endDate.toISOString();
    const generatedBy = this.state.generatedBy;
    const source = this.state.source;
    let requestUrl = '/idgen/logentry?v=full&identifier=' + identifier + '&comment=' + comment + 
                      '&source=' + source + '&generatedBy=' + generatedBy + '&toDate=' + endDate;
    if (!this.state.startDate) {
      apiCall(null, 'get', requestUrl).then((response) => {
        this.setState({
          logEntries: response.results,
          loading: false
        });
      });
    } else {
      const startDate = this.state.startDate.toISOString();
      apiCall(null, 'get', requestUrl + '&fromDate=' + startDate).then((response) => {
        this.setState({
          logEntries: response.results, 
          loading: false
        });
      });
    }
  }

  handleSearch(event) {
    const {name, value} = event.target;
    if (name === "generatedBy") {
      this.setState({generatedByValue: value});
      for (var index = 0; index < this.state.logEntries.length; index++) {
        if (this.state.logEntries[index].generatedBy.username === value) {
          this.setState({generatedBy: this.state.logEntries[index].generatedBy.uuid});
          break;
        } 
        else {
          this.setState({generatedBy: value});
        }
      }
    } 
    else if (name === "source") {
      this.setState({sourceValue: value});
      for (var index = 0; index < this.state.logEntries.length; index++) {
        if (this.state.logEntries[index].source.name === value) {
          this.setState({source: this.state.logEntries[index].source.uuid});
          break;
        } 
        else {
          this.setState({source: value});
        }
      }
    } 
    else {
      this.setState({
        searchFilters: Object.assign({}, this.state.searchFilters, {[name]: value})
      });
    }
  }

  handleChangeStart(date) {
    this.setState({startDate: date});
  }

  handleChangeEnd(date) {
    this.setState({
      endDate: date,
      dateChanged: !this.state.dateChanged
    });
  }

  componentDidMount() {
    this.getIdentifierSources();
  }

  resetSearchForm() {
    this.setState({
      startDate: '',
      endDate: moment(new Date()),
      generatedByValue: '',
      generatedBy: '',
      sourceValue: '',
      source: '',
      buttonBorder: false,
      searchFilters: {
        identifier: '',
        comment: ''
      },
      dateChanged: false
    });
  }

  render() {
    let logEntries = this.state.logEntries
    if (this.state.filteredLogEntries) {
      logEntries = logEntries.filter(LogEntry => {
        let formattedDate = moment(LogEntry.dateGenerated).format('DD/MM/YYYY')
        return LogEntry.source.name.toLowerCase().includes(this.state.filteredLogEntries.toLowerCase())||
             LogEntry.identifier.toLowerCase().includes(this.state.filteredLogEntries.toLowerCase()) || 
             LogEntry.comment.toLowerCase().includes(this.state.filteredLogEntries.toLowerCase()) || 
             formattedDate.includes(this.state.filteredLogEntries) || 
             (LogEntry.generatedBy.username.toLowerCase()).includes(this.state.filteredLogEntries.toLowerCase())
      })
    }

    return (
      <div className="logs_table_area">
        <div className="advanced_wrapper">
          <form id="advancedSearchForm">
            <fieldset>
              <legend className="logSearchTitle">Select Log Entries To Display</legend>
              <div className="col-sm-6 col-md-4">
                <label className="search_lbl">Source Name</label>
                <Input
                  type="select"
                  id="source"
                  value={this.state.sourceValue}
                  name="source"
                  onChange={this.handleSearch}>
                  <option value="">--Select Source Name--</option>
                  {this.state.identifierSources
                  .map(identifierSource => <option>{identifierSource.name}</option>)}
                </Input>
              </div>
              <div className="col-sm-6 col-md-4">
                <label className="search_lbl">Identifier Contains</label>
                <Input
                  id="identifier"
                  name="identifier"
                  value={this.state.searchFilters.identifier}
                  onChange={this.handleSearch}/>
              </div>
              <div className="col-sm-6 col-md-4">
                <label className="search_lbl" name="gen_by">Generated By</label>
                <Input
                  value={this.state.generatedByValue}
                  name="generatedBy"
                  onChange={this.handleSearch}/>
              </div>
              <div className="col-sm-6 col-md-4">
                <label className="search_lbl">Comment Contains</label>
                <Input
                  id="comment"
                  name="comment"
                  value={this.state.searchFilters.comment}
                  onChange={this.handleSearch}/>
              </div>
              <div className="col-sm-12 col-md-8">
                <label className="search_lbl" name="gen_range">Generate Between</label>
                <br/>
                <div className="input-group date">
                  <DatePicker
                    className="form-control"
                    selected={this.state.startDate}
                    selectsStart
                    startDate={this.state.startDate}
                    endDate={this.state.endDate}
                    onChange={this.handleChangeStart}
                    placeholderText="From Date"
                    maxDate={moment()}
                    showMonthDropdown
                    showYearDropdown
                    dropdownMode="select"/>
                </div>
                <div className="input-group dateSeparator">
                  <span>-</span>
                </div>
                <div className="input-group date ">
                  <DatePicker
                    className="form-control"
                    selected={this.state.endDate}
                    selectsEnd
                    startDate={this.state.startDate}
                    endDate={this.state.endDate}
                    onChange={this.handleChangeEnd}
                    placeholderText="To Date"
                    maxDate={moment()}
                    showMonthDropdown
                    showYearDropdown
                    dropdownMode="select"/>
                </div>
              </div>
              <div className="clear"></div>
            </fieldset>
            <div className="btn-group" role="group">
              <input
                type="button"
                value="Load Log Entries"
                className=" col-sm-6 button confirm"
                onClick={this.advancedSearch}/> 
              {(this.state.sourceValue || this.state.searchFilters.identifier ||
              this.state.generatedByValue || this.state.searchFilters.comment ||
              this.state.startDate || this.state.dateChanged) &&
              < input type = "button" className = "col-sm-6 cancelbtn"
              value = "Clear Form" onClick = {this.resetSearchForm} 
              />
              }
            </div>
          </form>
        </div>
        <div className="input-group logs_filter">
          <input
            type="text"
            placeholder="Filter Log Entries"
            className="log_searchbox"
            defaultValue={this.state.filteredLogEntries}
            onChange={event => this.setState({filteredLogEntries: event.target.value})}/>
          <span className="input-group-btn">
            <button className="btn btn-secondary" type="button">< FaSearch/></button>
          </span>
        </div>
        <div className="table-responsive logs_table">
          <ReactTable
            data={logEntries}
            noDataText='No Log Entries to Display'
            minRows={5}
            showPageJump={true}
            showPageSizeOptions={false}
            pageSize={(logEntries.length < 20) ? logEntries.length : 20}
            filterAll={true}
            loading={this.state.loading}
            columns={[
            {
              accessor: 'source.name',
              Header: 'Source Name',
              width: undefined,
              minWidth: 110
            }, {
              accessor: 'identifier',
              Header: 'Identifier',
              width: undefined,
              minWidth: 100,
              maxWidth: 100,
              className: 'center'
            }, {
              accessor: date => moment(date.dateGenerated).format('DD/MM/YYYY'),
              Header: 'Date Generated',
              width: undefined,
              minWidth: 135,
              maxWidth: 160,
              id: 'dateGenerated',
              className: 'center'
            }, {
              accessor: 'generatedBy[username]',
              Header: 'Generated By',
              width: undefined,
              minWidth: 100,
              maxWidth: 150,
              className: 'center'
            }, {
              accessor: 'comment',
              Header: 'Comments',
              minWidth: 140
            }
          ]}/>
        </div>
      </div>
    )
  }
}
