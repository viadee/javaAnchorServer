# javaAnchorServer

[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)
[![Build Status](https://travis-ci.org/viadee/javaAnchorServer.svg?branch=master)](https://travis-ci.org/viadee/javaAnchorServer)
[![Sonarcloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.viadee.anchorj.server:anchorj-server&metric=coverage)](https://de.viadee.anchorj.server:anchorj-server&metric=coverage) 

This is a server to provide Anchor-Explanations for machine learning models. 
It uses the [Java implementation of anchors](https://github.com/viadee/javaAnchorExplainer) 
as well as [adapters](https://github.com/viadee/javaAnchorAdapters) for accessing the data from an H2O server 
and optionally distributing the execution on a spark cluster. 
In conjunction with [javaAnchorFrontend](https://github.com/viadee/javaAnchorFrontend) an explanation can be created and
viewed easily, efficiently and fast: 

![Titanic Explanation](https://user-images.githubusercontent.com/5667523/51996301-bde31580-24b4-11e9-9c75-6205546d1463.png)
A description how to read this table can be found in the [ReadMe of javaAnchorFrontend](https://github.com/viadee/javaAnchorFrontend).

At this point, only access to data from the H2O and its models is supported. How this can be extended is described in [...].

The project is modular and each area can be easily replaced. It is also possible to extend the application so that it does not act as a server but as a command line application.

## Structure
The project is divided into 3 main parts:
- Access: api
- Business
- Access to data: dao
- Execution of the anchor algorithm: anchor

![Server Architecture](https://user-images.githubusercontent.com/5667523/51995074-51ffad80-24b2-11e9-9d98-731837be79b0.png)


### Access
In the current version, access is via REST. 
This task is performed by the controller module. 
It accepts a request, extracts the required data from the request, and forwards the order to the business layer (business module).
