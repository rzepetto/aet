### Tracking Progress

#### How to read AET Reports and real time progress

AET test reports are updated on real time basis and can be viewed on the console. This progress information is accessible in using two methods:

as a command line and with use of Jenkins job. To see progress

* log on Jenkins
* choose proper build execution from Build history panel and
* click Console Output.

For every test suite started the execution information is provided in the progress log:
```
[INFO] ********************************************************************************
[INFO] ********************** Job Setup finished at 10:14:43.249.**********************
[INFO] *** Suite is now processed by the system, progress will be available below. ****
[INFO] ********************************************************************************
```

During test processing detailed information about actual progress is displayed as in the following example:

```
...
[INFO] [06:34:20.680]: COLLECTED: [success: 0, total: 72] ::: COMPARED: [success: 0, total: 0]
[INFO] [06:34:31.686]: COLLECTED: [success: 1, total: 72] ::: COMPARED: [success: 1, total: 1]
[INFO] [06:34:35.689]: COLLECTED: [success: 2, total: 72] ::: COMPARED: [success: 1, total: 2]
[INFO] [06:34:36.691]: COLLECTED: [success: 2, total: 72] ::: COMPARED: [success: 2, total: 2]
[INFO] [06:34:43.695]: COLLECTED: [success: 3, total: 72] ::: COMPARED: [success: 2, total: 3]
[INFO] [06:34:44.698]: COLLECTED: [success: 3, total: 72] ::: COMPARED: [success: 3, total: 3]
...
```

where:

**collected** - shows results of collectors' work - how many artifacts have been successfully collected and what is the total number of all artifacts to be collected,

**compared** -  shows results of comparators' work - how many artifacts have been successfully compared and what is the total number of all artifacts to be compared. The total number of artifacts to be compared depends on collectors' work progress - increases when the number of successfully collected artifacts increase.

If there are problems during processing, warning information with some description of processing step and its parameters is displayed:
```
[WARN] CollectionStep: source named source with parameters: {} thrown exception. TestName: comparator-Source-Long-Response-FAILED UrlName: comparators/source/failed_long_response.jsp Url: http://192.168.123.100:9090/sample-site/sanity/comparators/source/failed_long_response.jsp
```

In this example source collector failed to collect necessary artifacts. This information is subsequently reflected in the progress log:
```
...
[INFO] [06:36:44.832]: COLLECTED: [success: 46, failed: 1, total: 72] ::: COMPARED: [success: 46, total: 46]
[INFO] [06:36:50.837]: COLLECTED: [success: 47, failed: 1, total: 72] ::: COMPARED: [success: 47, total: 47]
[INFO] [06:36:52.840]: COLLECTED: [success: 48, failed: 1, total: 72] ::: COMPARED: [success: 47, total: 48]
...
```

In the example above one artifact has failed during collection phase.

#### When tests successfully finish - command line

When the AET test processing completes the information about received reports and processing status - BUILD SUCCESS or BUILD FAILURE is shown on the console - as shown below:

```
[INFO] Received report message: ReportMessage{company=aet-demo-sanity, project=demo-sanity-test, testSuiteName=main, status=OK, environment=win7-ff16, domain=http://192.168.123.100:9090/sample-site/sanity/, correlationId=aet-demo-sanity-demo-sanity-test-main-1426570459612}
[INFO] [06:38:03.549]: COLLECTED: [success: 71, failed: 1, total: 72] ::: COMPARED: [success: 71, total: 71]
[INFO] Received report message: ReportMessage{company=aet-demo-sanity, project=demo-sanity-test, testSuiteName=main, status=OK, environment=win7-ff16, domain=http://192.168.123.100:9090/sample-site/sanity/, correlationId=aet-demo-sanity-demo-sanity-test-main-1426570459612}
[INFO] Total: 2 of 2 reports received.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3:45.645s
[INFO] Finished at: Tue Mar 17 06:38:03 CET 2015
[INFO] Final Memory: 14M/246M
[INFO] ------------------------------------------------------------------------
```

BUILD SUCCESS  - status means that test processing is successfully finished and reports are generated in target folder.

BUILD FAILURE  - status means that there were some technical problem during processing for example database is not responding and it is not possible to receive reports.

#### When test is successfully finished - Jenkins job

Jenkins console output presents the same information as described above, but if test suite is defined to generate xunit-report additional information such as Junit processing is logged on console:

```
[xUnit] [INFO] - Starting to record.
[xUnit] [INFO] - Processing JUnit
[xUnit] [INFO] - [JUnit] - 1 test report file(s) were found with the pattern 'test-suite/target/xunit-report.xml' relative to '/var/lib/jenkins/jobs/aet-sanity-test-integration/workspace' for the testing framework 'JUnit'.
[xUnit] [INFO] - Converting '/var/lib/jenkins/jobs/aet-sanity-test-integration/workspace/test-suite/target/xunit-report.xml' .
[xUnit] [INFO] - Check 'Failed Tests' threshold.
[xUnit] [INFO] - The new number of tests for this category exceeds the specified 'new unstable' threshold value.
[xUnit] [INFO] - Setting the build status to UNSTABLE
[xUnit] [INFO] - Stopping recording.
Build step 'Publish xUnit test result report' changed build result to UNSTABLE
Finished: UNSTABLE
```

The meaning of '*Successful*' and '*Failed*' build is quite different here, because final build status depends mainly on tests results and thresholds configuration. The build can result with BUILD SUCCESS status (which means that all workers - collectors, comparators, reporters finish their work and proper reports were generated), but final Jenkins build status can be for example UNSTABLE becase there were some new test failures.

A Jenkins build is considered as UNSTABLE (yellow) or FAILURE (red) if the new (tests that failed now, but did not fail in previous run) or total number of failed tests exceeds the specified thresholds. For example:when "yellow total" threshold is set to 0 and one or more test cases failed, then build is mark as UNSTABLE.
