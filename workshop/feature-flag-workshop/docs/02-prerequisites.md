---
sidebar_position: 2
title: 2. Prerequisites
---

# Prerequisites

Before you begin this lab, you'll need to set up a few tools on your machine.

## Required Tools

### Skills

| Skill                                     | Level        | 
|-------------------------------------------|--------------|
| [Java](https://www.oracle.com/java/)      | intermediate |   
| [Maven](https://www.maven.apache.org/)    | novice       |
| [Quarkus](https://quarkus.io)             | novice       |
| [REST API](https://www.maven.apache.org/) | proficient   |

### Icons & Conventions

Before starting, we will use the following icons during the workshop. Let us check their meaning:

🛠️ An action to perform,  
📝 A file to modify,  
👀 Something to observe,  
✅ Validate something,  
ℹ️ Some information.

### Tools

#### If you want to execute this workshop on your desktop with [DevContainers](https://containers.dev/)

I stored a configuration to set the project up in DevContainer. You can check it out in the project [``.devcontainer/devcontainer.json``](https://github.com/alexandre-touret/feature-flag-workshop/tree/main/.devcontainer) file.

If you want to know more about DevContainers, you can check out this [documentation](https://containers.dev/).

You **MUST** have set up these tools first:

* [Docker](https://docs.docker.com/)
* An IDE: ([IntelliJ IDEA](https://www.jetbrains.com/idea) or [VSCode](https://code.visualstudio.com/)).

🛠️ You can validate your environment running these commands:

**Docker**

```jshelllanguage
$ docker version
    Client:
    Docker Engine -Community
    Version:
    27.4.1
    API version:1.47
    Go version:go1.22.10
    Git commit:b9d17ea
    Built:Tue Dec 17 15:45:46 2024
    OS/Arch:linux/amd64
    Context:default

```

#### If you want to execute this workshop on your desktop (without DevContainer) 

You **MUST** have sat up these tools first:

* [Java 21+](https://adoptium.net/temurin/releases/?version=21)
* [Maven 3.9](https://www.maven.apache.org/)
* [Docker](https://docs.docker.com/)
* Any IDE ([IntelliJ IDEA](https://www.jetbrains.com/idea), [VSCode](https://code.visualstudio.com/), [Netbeans](https://netbeans.apache.org/),...) you want.

🛠️ You can validate your environment running these commands:

**Java**

```jshelllanguage
$ java -version
openjdk version "21.0.1" 2023 - 10 - 17 LTS
OpenJDK Runtime Environment Temurin-21.0.1 + 12 (build 21.0.1 + 12 - LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.1+12(build21.0.1+12-LTS,mixed mode,sharing)
```

**Maven**

```jshelllanguage
$ mvn --version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /home/alexandre/.sdkman/candidates/maven/current
Java version: 21.0.5, vendor: Eclipse Adoptium, runtime: /home/alexandre/.sdkman/candidates/java/21.0.5-tem
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "5.15.167.4-microsoft-standard-wsl2", arch: "amd64", family: "unix"
```

**Docker**

```jshelllanguage
$ docker version
Client:
Version:           27.2.1-rd
API version:       1.45 (downgraded from 1.47)
Go version:        go1.22.7
Git commit:        cc0ee3e
Built:             Tue Sep 10 15:41:39 2024
OS/Arch:           linux/amd64
Context:           default


```

**If you don't want to bother with a local setup**
It's strongly recommended to use [Github Codespaces](https://github.com/features/codespaces). You must create an account
first and [fork this repository](https://github.com/alexandre-touret/feature-flag-workshop/fork).

You can then open this project in either your local VS Code or directly in your browser.

**For the rest of this workshop, I will assume you will use GitHub CodeSpaces.**

## Environment Setup

> aside positive
> ℹ️ **What will you do and learn in this chapter?**
>
> You will set up the environment in Github Codespaces and understand how to run it

### 🛠  Open Github CodeSpaces

* Log on [GitHub](https://github.com/) and
  [fork this repository](https://github.com/alexandre-touret/feature-flag-workshop/fork).
* Click on ``Code>Codespaces>Create a codespace`` on the ``main`` branch

![start codespace](./assets/start_build_codespace.png)

When a message invites you making a URL public, select and validate it.

Wait until the codeSpace is ready.

![build codespace](./assets/build_codespace.png)

During the first startup, the maven build is automatically started. Please wait until it is completely finished.

### 🛠 Start the app

In a new terminal, start the Quarkus Dev environment:

```jshelllanguage
$ ./mvnw quarkus:dev
```

👀 Wait a while until you get the following output:

TODO

ℹ️ All the stack is provided through the [Quarkus Dev Services](https://quarkus.io/guides/dev-services).
You don't therefore have to bother yourself about setting it up.

✅ Now validate your setup browsing the Quarkus DEV-UI. 

Go to the VS Code Port panel.

Select the port tab:

![Port VSCODE](./assets/port_vscode.png)

And now, go the URL which exposes the 8080 port:

![start-8080](./assets/start_dev_ui.png)

and add the ``/q/dev-ui`` suffix.

For instance: ``https://laughing-giggle-x5x4rqxpwfv5pj-8080.app.github.dev/q/dev-ui``

ℹ️ You can also browse the dev-ui to the ``Extensions>SmallRye OpenAPI``.

![Small Rye](./assets/dev_ui_extensions.png)
