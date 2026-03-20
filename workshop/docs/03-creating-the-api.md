---
sidebar_position: 3
title: 3. Creating the API
---

# Creating the API

Now let's create our first OpenAPI specification. 

## OpenAPI Contract

Create a new file called `openapi.yaml` and add the following contents:

```yaml
openapi: 3.0.0
info:
  title: Sample API
  version: 1.0.0
paths:
  /hello:
    get:
      summary: Returns a greeting
      responses:
        '200':
          description: A successful response
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: Hello World
```

Notice the **Copy** button on the top right of the code block. This is a built-in feature of Docusaurus, perfect for Codelabs.
