# Feature Flag Workshop

## Purpose
This repository contains the source code and documentation for the **Feature Flag Workshop**. The goal of this workshop is to provide hands-on experience with feature flags (also known as feature toggles), demonstrating how they can be used to safely decouple deployment from release, enable progressive rollouts, and facilitate A/B testing.

The workshop uses [OpenFeature](https://openfeature.dev/) as a vendor-agnostic abstraction layer to manage feature flags across both a Java backend and an Angular frontend, with [UnKeash](https://getunleash.io/) used as the underlying feature flag management platform.

## Structure
The project is divided into three main components:

- **`/api`**: A Java backend built with [Quarkus](https://quarkus.io/). It handles the core business logic, including a mock database for managing musical instruments and customer orders.
- **`/gui`**: A frontend web application built with [Angular](https://angular.dev/). It provides a user interface to browse the instrument catalog and observe feature flags in action.
- **`/workshop`**: Contains the Markdown source files for the workshop documentation, built and hosted using Docusaurus.

## Ready to go?
To get started, follow the full step-by-step instructions available on the workshop website:

👉 Go to [https://blog.touret.info/feature-flag-workshop/](https://blog.touret.info/feature-flag-workshop/)
