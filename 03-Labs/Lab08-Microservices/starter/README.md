<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 08 - Microservices Architecture - Starter Code

This is the starter code for Lab 08. Follow the instructions in the main README to complete the implementation.

## Structure

This starter provides the basic project structure for all three services:

### 1. Client Service (Port 9081)
- Basic Maven project structure
- Liberty configuration
- Database configuration
- TODO: Implement domain model, repository, and REST API

### 2. Account Service (Port 9082)
- Basic Maven project structure
- Liberty configuration
- Database configuration
- TODO: Implement domain model, repository, REST client, and REST API

### 3. API Gateway / BFF (Port 9080)
- Basic Maven project structure
- Liberty configuration
- TODO: Implement REST clients, aggregation service, controllers, and JSP views

## Getting Started

1. Review the main Lab08 README for detailed instructions
2. Start with the Client Service
3. Then implement the Account Service
4. Finally, create the BFF/API Gateway

## What's Provided

- Maven POM files with dependencies
- Liberty server configuration
- Database configuration (PostgreSQL)
- Basic directory structure
- Docker Compose configuration

## What You Need to Implement

- Domain models
- Repository interfaces and implementations
- Application services
- REST resources
- REST clients (MicroProfile Rest Client)
- Fault tolerance patterns
- Web controllers and JSP views
- Health checks

Good luck!