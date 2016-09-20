/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.vs;

import java.io.Serializable;
import java.util.Date;

public class ReportResultStats implements Serializable {

  private static final long serialVersionUID = 3619992356966058838L;

  private final int passedCount;

  private final int failedCount;

  private final int warningCount;

  private final int totalCount;

  private final Date dateTime;

  public ReportResultStats(int passedCount, int failedCount, int warrningCount, int totalCount,
                           Date dateTime) {
    this.passedCount = passedCount;
    this.failedCount = failedCount;
    this.warningCount = warrningCount;
    this.totalCount = totalCount;
    this.dateTime = new Date(dateTime.getTime());
  }

  public int getPassedCount() {
    return passedCount;
  }

  public int getFailedCount() {
    return failedCount;
  }

  public int getWarningCount() {
    return warningCount;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public Date getDateTime() {
    return new Date(this.dateTime.getTime());
  }
}
