# investment-tax-relief-subscription-frontend

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)[![Build Status](https://travis-ci.org/hmrc/investment-tax-relief-subscription-frontend.svg?branch=master)](https://travis-ci.org/hmrc/investment-tax-relief-subscription-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/investment-tax-relief-subscription-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/investment-tax-relief-subscription-frontend/_latestVersion)

Overview   
--------
   
This service is part of the investment tax relief digital service which implements the registration and subscription part of the service.
 
For a full list of the dependent microservices that comprise this service please see the readme for our [Submission Frontend Service](https://github.com/hmrc/investment-tax-relief-submission-frontend/)

 
Requirements
------------

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.


## Run the application


To update from Nexus and start all services from the RELEASE version instead of snapshot

```
sm --start TAVC_ALL -f
```


##To run the application locally execute the following:

Kill the service ```sm --stop ITR_SUBSC_FE``` and run:
```
sbt 'run 9637' 
```

  

## Test the application

To test the application execute

```
sbt test
```

License
---
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").


[JRE]: http://www.oracle.com/technetwork/java/javase/overview/index.html
[API]: https://en.wikipedia.org/wiki/Application_programming_interface
[URL]: https://en.wikipedia.org/wiki/Uniform_Resource_Locator
