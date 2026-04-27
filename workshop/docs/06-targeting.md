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

## 1. Segments

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

## 2. Bucketing & Progressive Rollouts

Deploying a new feature to 100% of your targeted users at once can be risky. What if a new heavy database query brings your application down?

**Bucketing** is the underlying mathematical mechanism that allows for **Progressive Rollouts** (like A/B testing or canary releases).

Instead of explicitly targeting specific user attributes, the feature flag engine takes the user's `targetingKey` and deterministically hashes it alongside the flag's name to assign them to a "bucket" (from 0 to 100). This ensures that a user who falls into the 10% bucket for a feature will consistently see that feature across sessions, avoiding a flickering UI.

### Implementing a Progressive Rollout

Let's implement a 25% progressive rollout for our `discount-enabled` flag. We want 25% of our regular users (who don't match the complex targeting) to receive the discount as a promotional test, while the remaining 75% continue to see the standard price.

📝 Open `api/src/main/docker/go-feature-flag/flags.yaml`.

🛠️ Find the `discount-enabled` flag and update its `defaultRule` to use percentages:

```yaml
  discount-enabled:
    variations:
      on: true
      off: false
    targeting:
      - query: clientCountry in ["FRANCE", "GERMANY", "UK"] and ew(clientEmail, "@musician.com")
        variation: on
    defaultRule:
      percentages:
        on: 25
        off: 75
```

### How it works behind the scenes

When a request comes in, GO Feature Flag evaluates the targeting rules. If the user doesn't match the complex query, it falls back to the `defaultRule`.

Then, it takes the `targetingKey` (in our case, the user's email) and hashes it along with the flag name (`discount-enabled`).
- If the resulting hash falls between 0 and 24, the evaluation returns `on`.
- If it falls between 25 and 100, the evaluation returns `off`.

Because the hashing algorithm is deterministic, `john.doe@gmail.com` will always fall into the same bucket and receive the exact same experience every time they log in.

### Testing the Bucketing

🛠️ Ensure the GO Feature Flag container is still running and restart Quarkus if necessary:

```bash
cd api
./mvnw clean quarkus:dev
```

🛠️ You can test this behavior by making requests to the API with different user emails. Open a new terminal and run:

```bash
http :8080/instruments User:'{"firstName":"test","lastName":"user1","email":"user1@example.com","country":"US"}' accept:"application/json"
```

👀 Try replacing the `email` with `user2@example.com`, `user3@example.com`, etc. Over a large enough sample, you will observe that approximately 1 out of every 4 users will evaluate to `hasDiscount: true`, while the rest will receive `hasDiscount: false`.

✅ For example, try to find an email that falls into the 25% bucket and notice how repeating the request with that exact email **always** yields the discount!
