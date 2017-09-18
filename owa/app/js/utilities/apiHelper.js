/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import * as request from 'superagent';

/**
 * ApiCall - The base for all API call
 * @param {object} data
 * @param {string} type
 * @param {string} url
 * @returns {object}
 */
export default function apiCall(data, type, url) {
  const contextPath = location.href.split('/')[3];
  const BASE_URL = `/${contextPath}/ws/rest/v1/${url}`;
  return new Promise((resolve, reject) => {
    request[type](BASE_URL)
      .send(data)
      .set('Content-Type','application/json')
      .end((err, res) => {
        if (res) {
          return resolve(res.body);
        }
        return reject(err);
      });
  });
}