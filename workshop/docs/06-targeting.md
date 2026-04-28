---
sidebar_position: 6
title: 6. Targeting and context
---
:::info
 ℹ️ What will you do and learn in this chapter?
- Fundamentals of Feature-Flagging: segments, variants, context, evaluations, bucketing
- How to kill a feature flag and control it live without any redeployment
- How to make A/B testing with Go Feature Flag and OpenFeature
- How to implement a progressive rollout (canary deployment) with Go Feature Flag and OpenFeature
:::

# Advanced Targeting & Rollouts

In the previous chapters, we introduced the core components of feature flagging:
- **Variants**: The different predefined values a flag can return (e.g., `true`/`false`, or numerical discounts like `0.1`/`0.5`).
- **Context**: The contextual data we pass during an **Evaluation** (e.g., `clientCountry`, `targetingKey`).
- **Evaluations**: The process where the feature flag engine compares the Context against its rules to determine the correct Variant.

While targeting users by a single attribute like their country is a great start, enterprise feature flagging often requires more advanced strategies. Managing duplicate rules across dozens of flags is a maintenance nightmare, and rolling out a feature to 100% of a target audience immediately can be risky.

This is where **Segments** and **Bucketing** come into play.

## Segments

A **Segment** allows you to define a specific group of users with a complex rule set just once, and then reference that segment across multiple feature flags.

⚠️ **Note:** Segments are not implemented yet in the context of this workshop, so we will only explain the concept here. However, we can achieve the same behavior by writing complex queries directly in our targeting rules!

### Complex Targeting

Let's say we want to restrict the discount feature to a specific group of users—specifically, customers who have an email address ending in `@musician.com` and who are located in our active European markets.

📝 Open the file `api/src/main/docker/go-feature-flag/flags.yaml`.

🛠️ Update the `discount-enabled` flag to include a query combining the country and email checks. **Note:** Go Feature Flag uses `ew()` for "ends with":

```yaml
flags:
  welcome-message:
    variations:
      on: true
      off: false
    defaultRule:
      variation: on

  discount-enabled:
    variations:
      on: true
      off: false
    targeting:
      - query: clientCountry in ["FRANCE", "GERMANY", "UK"] and clientEmail ew "musician.com"
        variation: on
    defaultRule:
      variation: off

  discount-amount:
    variations:
      10-percent: 0.1
      20-percent: 0.2
      50-percent: 0.5
    targeting:
      - query: clientCountry eq "GERMANY"
        variation: 50-percent
      - query: clientCountry eq "UK"
        variation: 20-percent
    defaultRule:
      variation: 10-percent
```

📝 Check then the evaluation with the Go Feature Flag API to validate it parsed correctly without errors:

```bash
http POST http://localhost:1031/v1/allflags \
  user:='{"key": "client-fr-1", "custom": {"clientCountry": "FRANCE", "clientEmail":"user@musician.com"}}'
```

👀 You should get all the flags:

```json
{
    "flags": {
        "discount-amount": {
            "errorCode": "",
            "reason": "TARGETING_MATCH",
            "timestamp": 1777295145,
            "trackEvents": true,
            "value": 0.2,
            "variationType": "20-percent"
        },
        "discount-enabled": {
            "errorCode": "",
            "reason": "TARGETING_MATCH",
            "timestamp": 1777295145,
            "trackEvents": true,
            "value": true,
            "variationType": "on"
        },
        "welcome-message": {
            "errorCode": "",
            "reason": "STATIC",
            "timestamp": 1777295145,
            "trackEvents": true,
            "value": true,
            "variationType": "on"
        }
    },
    "valid": true
}
```

📝 Now if you use another email address domain:

```bash
http POST http://localhost:1031/v1/allflags \
  user:='{"key": "client-fr-1", "custom": {"clientCountry": "FRANCE", "clientEmail":"user@somewhereelse.com"}}'

```
```json
{
    "flags": {
        "discount-amount": {
            "errorCode": "",
            "reason": "TARGETING_MATCH",
            "timestamp": 1777295407,
            "trackEvents": true,
            "value": 0.2,
            "variationType": "20-percent"
        },
        "discount-enabled": {
            "errorCode": "",
            "reason": "DEFAULT",
            "timestamp": 1777295407,
            "trackEvents": true,
            "value": false,
            "variationType": "off"
        },
        "welcome-message": {
            "errorCode": "",
            "reason": "STATIC",
            "timestamp": 1777295407,
            "trackEvents": true,
            "value": true,
            "variationType": "on"
        }
    },
    "valid": true
}

```


### Updating the Context in the API

To make this complex targeting work, we need to ensure the `clientEmail` is sent during the flag evaluation.

📝 Open `api/src/main/java/info/touret/musicstore/infrastructure/featureflag/adapter/DiscountAdapter.java`.

🛠️ Update the `EvaluationContext` to include the user's email as the `clientEmail` attribute:

```java
openFeatureAPIClient.setEvaluationContext(new MutableContext()
        .add("clientCountry", user.country())
        .add("targetingKey", user.email())
        .add("clientEmail", user.email())); // New attribute for targeting
```

✅ Now, let's validate that only users from FRANCE, GERMANY, or the UK **AND** with an email ending in `@musician.com` evaluate to `true` for the `discount-enabled` flag.

🛠️ Test it with a standard user (should NOT get the discount):

```bash
http :8080/instruments User:'{"firstName":"john","lastName":"Doe","email":"john.doe@gmail.com","country":"FRANCE"}' accept:"application/json"
```

👀 The `hasDiscount` field in the response should not be filled.

🛠️ Now test it with a premium musician (should get the discount):

```bash
http :8080/instruments User:'{"firstName":"Eric","lastName":"Clapton","email":"eric.clapton@musician.com","country":"UK"}' accept:"application/json"
```

👀 The `hasDiscount` field should be filled and set to `true`. The `price` should be reduced.

🛠️ Update the unit tests:

Go to the ``api/src/test/java/info/touret/musicstore/infrastructure/featureflag/adapter/DiscountAdapterTest.java`` class.

Update all the user objects creation in every test method. Add ``musician.com`` as domain name.For instance:


```java
var userGB = new User("John", "Doe", "john.doe@musician.com", "UK");
```

Create then another test method to check the newly implemented rule:

```java
@Test
void should_return_discount_not_enabled_with_unknown_mail_successfully() {
    var userGermany = new User("John", "Doe", "john.doe@test.com", "GERMANY");
    assertEquals(instrument.price(), discountAdapter.applyDiscount(instrument, userGermany).value().price());
}
```

You should get this output in the Quarkus console:

```bash
 --
All 59 tests are passing (0 skipped), 4 tests were run in 2257ms. Tests completed at 15:28:05 due to changes to DiscountAdapterTest.class.
```

## Bucketing & Progressive Rollouts

Deploying a new feature to 100% of your targeted users at once can be risky. What if a new heavy database query brings your application down?

### What is Bucketing?
**Bucketing** is the underlying mathematical mechanism that allows for **Progressive Rollouts** and **A/B testing**.

Instead of explicitly targeting specific user attributes, the feature flag engine takes the user's `bucketingKey` (which is typically set to `targetingKey` by default if not specified explicitly) and deterministically hashes it alongside the flag's name to assign them to a "bucket" (from 0 to 100). This ensures that a user who falls into the 10% bucket for a feature will consistently see that feature across sessions, avoiding a flickering UI.

If you want to know more, you can [check out this documentation](https://gofeatureflag.org/docs/configure_flag/custom-bucketing).

:::info What is A/B Testing?
**A/B Testing** is an experiment where two or more variants of a page or feature are shown to users at random, and statistical analysis is used to determine which variation performs better for a given conversion goal.

Using bucketing, we can assign exactly 50% of our users to the "A" group (e.g., the old feature) and 50% to the "B" group (e.g., the new feature). By measuring how each group behaves, we can make data-driven decisions!
:::

### Implementing a Progressive Rollout / A/B Test

Let's implement an A/B test (or 50/50 progressive rollout) for our `welcome-message` flag and our `discount-enabled` flag. We want 50% of our users to see the new variation and 50% to see the old one.

📝 Create a a specific configuration file for A/B testing: `api/src/main/docker/go-feature-flag/abtesting-flags.yaml`.

Let's configure it:

```yaml
welcome-message:
  variations:
    on: true
    off: false
  defaultRule:
    variation: off

discount-enabled:
  variations:
    on: true
    off: false
  targeting:
    - query: clientCountry in ["FRANCE", "GERMANY", "UK"] and clientEmail ew "musician.com"
      percentage:
        off: 50
        on: 50
  defaultRule:
    variation: off

discount-amount:
  variations:
    10-percent: 0.1
    20-percent: 0.2
    50-percent: 0.5
  targeting:
    - query: clientCountry eq "GERMANY"
      variation: 50-percent
    - query: clientCountry eq "UK"
      variation: 20-percent
  defaultRule:
    variation: 10-percent
```

### Switching Configuration in the Proxy

To tell Go Feature Flag to use this new configuration file instead of the default `flags.yaml`, we need to update the proxy configuration.

📝 Open `api/src/main/docker/compose-devservices.yml`.
🛠️ Change the `volumes` configuration uncommenting the different lines:

```yaml
services:
  go-feature-flag:
    image: gofeatureflag/go-feature-flag:trixie
    ports:
      - "1031:1031"
    volumes:
      - ./go-feature-flag/flags.yaml:/goff/flags.yaml
      - ./go-feature-flag/abtesting-flags.yaml:/goff/abtesting-flags.yaml
      - ./go-feature-flag/canary-flags.yaml:/goff/canary-flags.yaml
      - ./go-feature-flag/proxy.yaml:/goff/goff-proxy.yaml
    environment:
      - POLLING_INTERVAL=1000
```


📝 Open `api/src/main/docker/compose-test-devservices.yml`.
🛠️ Change the `volumes` configuration uncommenting the different lines:

```yaml
services:
  go-feature-flag:
    image: gofeatureflag/go-feature-flag:trixie
    ports:
      - "1032:1031"
    volumes:
      - ./go-feature-flag/flags.yaml:/goff/flags.yaml
      - ./go-feature-flag/abtesting-flags.yaml:/goff/abtesting-flags.yaml
      - ./go-feature-flag/canary-flags.yaml:/goff/canary-flags.yaml
      - ./go-feature-flag/proxy.yaml:/goff/goff-proxy.yaml
    environment:
      - POLLING_INTERVAL=1000

```

📝 Open `api/src/main/docker/go-feature-flag/proxy.yaml`.

🛠️ Change the `path` to point to the A/B testing configuration:

```yaml
pollingInterval: 1000 # The relay-proxy will poll the file every second to check for changes
retrievers:
  - kind: file
    path: /goff/abtesting-flags.yaml # Changed from flags.yaml to abtesting-flags.yaml
```

The Go Feature Flag Relay Proxy will automatically detect this change and load the new configuration within 1 second.

### How it works behind the scenes

When a request comes in, GO Feature Flag evaluates the targeting rules. It takes the `bucketingKey` (in our case, the user's email since we mapped it to `targetingKey`) and hashes it along with the flag name.
- If the resulting hash falls between 0 and 49, the evaluation returns `on`.
- If it falls between 50 and 100, the evaluation returns `off`.

Because the hashing algorithm is deterministic, `john.doe@musician.com` will always fall into the same bucket and receive the exact same experience every time they log in.

### Testing the Bucketing

🛠️ Ensure the GO Feature Flag container is still running and restart Quarkus if necessary:

```bash
$ cd api
$ ./mvnw clean quarkus:dev
```

🛠️ You can test this behavior by making requests to the API with different user emails. Open a new terminal and run:

```bash
$ http :8080/instruments User:'{"firstName":"test","lastName":"user1","email":"user1@musician.com","country":"UK"}' accept:"application/json"
```

👀 Run the [K6 command](https://k6.io/) to check how the bucketing works:

```bash
$ cd ../infrastructure/scripts
$  k6 run k6-discount-enabled-test.js
```

## Canary Deployment

:::info What is a Canary Deployment?
A **Canary Deployment** is a release strategy where a new feature is deployed to a small, isolated subset of users (the "canaries") before rolling it out to the entire infrastructure. This minimizes the impact of potential bugs or performance issues.

If the canary release goes well, the percentage of users seeing the feature is gradually increased (e.g., from 10% to 25%, to 50%, to 100%) until everyone has access. It's an excellent way to validate stability in a production environment.
:::

### Implementing a Canary Deployment

Go Feature Flag supports scheduled progressive rollouts natively!
We will implement a scenario where:
1. Internal testers (`@musician.com` emails) ALWAYS have the new feature enabled (100%).
2. The regular population of users gets a **time-based progressive rollout** of the new feature. Over the course of 4 days, the percentage of users seeing the new variation will smoothly and automatically scale from a small percentage up to a larger one!

🛠 Create a specific configuration file for canary deployments: `api/src/main/docker/go-feature-flag/canary-flags.yaml`.

Add the following content:

```yaml
welcome-message:
  variations:
    on: true
    off: false
  targeting:
    - query: clientEmail ew "@musician.com"
      variation: on
  defaultRule:
    progressiveRollout:
      initial:
        variation: on
        percentage: 0
        date: 2026-04-28T05:00:00.100Z
      end:
        variation: on
        percentage: 100
        date: 2026-04-28T23:00:00.100Z

discount-enabled:
  variations:
    on: true
    off: false
  targeting:
    - query: clientCountry in ["FRANCE", "GERMANY", "UK"] and clientEmail ew "musician.com"
      progressiveRollout:
        initial:
          variation: on
          percentage: 20
          date: 2026-04-28T05:00:00.100Z
        end:
          variation: on
          percentage: 80
          date: 2026-04-28T23:00:00.100Z
  defaultRule:
    variation: off

discount-amount:
  variations:
    10-percent: 0.1
    20-percent: 0.2
    50-percent: 0.5
  targeting:
    - query: clientCountry eq "GERMANY"
      variation: 50-percent
    - query: clientCountry eq "UK"
      variation: 20-percent
  defaultRule:
    variation: 10-percent


```

:::warning
Update the date used in this file with the current date
:::

### Switching Configuration in the Proxy

Once again, we need to instruct the Relay Proxy to load this specific file.

📝 Open `api/src/main/docker/go-feature-flag/proxy.yaml`.

🛠️ Change the `path` to point to the Canary configuration:

```yaml
pollingInterval: 1000 # The relay-proxy will poll the file every second to check for changes
retrievers:
  - kind: file
    path: /goff/canary-flags.yaml # Changed to canary-flags.yaml
```

The Go Feature Flag Relay Proxy will automatically update its configuration!

### Testing the Canary Release

You can test this setup using the Go Feature Flag REST API. Since the progressive rollout is time-dependent, the evaluation result changes dynamically based on the current date relative to the configured `initial` and `end` dates!

🛠️ Run the same K6 script again:

```bash
$ cd ../infrastructure/scripts
$  k6 run k6-discount-enabled-test.js
```
