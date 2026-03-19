---
sidebar_position: 4
title: 4. Testing the API
---

# Testing the API

With our specification defined, we can now use mock tools to test it.

## Running a Mock Server

Use the popular tool `prism` to start a mock server from your OpenAPI spec.

```bash
npx @stoplight/prism-cli mock openapi.yaml
```

Once the server is running, you can test it using `curl`:

```bash
curl http://127.0.0.1:4010/hello
```

You should see the mock response defined in your spec:

```json
{
  "message": "Hello World"
}
```
