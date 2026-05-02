---
sidebar_position: 7
title: 7. Feature flag on the client side
---

:::info
 ℹ️ What will you do and learn in this chapter?
- Integrate Feature Flagging in an Angular App
- Subscribe to real-time configuration changes via Provider Events
:::

# Client-Side Feature Flagging

In the previous chapters, we configured feature flags on the backend side to control prices and business logic. Now, let's explore how to integrate OpenFeature and Go Feature Flag into our frontend (GUI) to toggle UI elements like the discount banner.

## Setup

First, make sure to stop the running GUI process by typing ``CTRL+C`` in its terminal.

🛠️ Install OpenFeature and the Go Feature Flag Angular Web Provider SDK:

```bash
$ cd gui
$ npm install @openfeature/go-feature-flag-web-provider
$ npm install @openfeature/angular-sdk
```

📝 Open the file `gui/src/app/app.config.ts`.
🛠️ Add the OpenFeature provider initialization to the `providers` array:

```typescript
provideAnimationsAsync(),
importProvidersFrom(
  OpenFeatureModule.forRoot({
    provider: new GoFeatureFlagWebProvider({
      endpoint: 'http://localhost:1031/'
    }),
  })
)
```

And ensure the required imports are declared at the top of the file:

```typescript
import {OpenFeatureModule} from '@openfeature/angular-sdk';
import {GoFeatureFlagWebProvider} from '@openfeature/go-feature-flag-web-provider';
```

## Evaluating Flags in Components

Now that our OpenFeature client is configured, let's use it in our component to dynamically display the discount banner based on the user's context.

📝 Open `gui/src/app/pages/instruments/instrument-list/instrument-list.component.ts`.

🛠️ Inject the `UserService` into the `InstrumentListComponent` so we can retrieve the current user's details for context evaluation:

```typescript
export class InstrumentListComponent implements OnInit {
  private instrumentService = inject(InstrumentService);
  private userService = inject(UserService); // New injection
  private dialog = inject(MatDialog);
```

🛠️ Update the `initFeatureFlags()` method to set the OpenFeature context and retrieve the value of our `discount-enabled` flag:

From:
```typescript
private async initFeatureFlags() {
  // TODO: Implement OpenFeature initialization
  // For now, manually toggle or keep false
  this.showDiscountBanner.set(false);
}
```

To:
```typescript
  private async initFeatureFlags() {
    const user = this.userService.getCurrentUser();
    if (!user) return;

    await OpenFeature.setContext({
      targetingKey: user.email,
      clientEmail: user.email,
      clientCountry: user.country
    });

    const client = OpenFeature.getClient();
    const discountEnabled = client.getBooleanValue('discount-enabled', false);
    this.showDiscountBanner.set(discountEnabled);
  }
```

Don't forget to declare these missing imports:

```typescript
import {OpenFeature} from '@openfeature/web-sdk';
import {UserService} from '../../../services/user.service';
```

🛠️ Restart the frontend server:

```bash
$ npm start
```

✅ Open the application in your browser. Change your user email (e.g. to `test@musician.com`) and country (e.g. to `UK`) via the user settings. Refresh the page and you should see the discount banner appear based on your targeting rules!

## Event Management

:::info
 ℹ️ Go Feature Flag's web provider offers real-time updates. By listening to provider events, we can dynamically update the UI state when a feature flag is modified on the server without requiring a page refresh.
:::

Currently, you must refresh the page to see changes if a feature flag is modified on the backend. Let's automatically update the banner when the provider's configuration is changed.

📝 In `instrument-list.component.ts`, update `initFeatureFlags` to add an event handler:

```typescript
  private async initFeatureFlags() {
    const user = this.userService.getCurrentUser();
    if (!user) return;

    await OpenFeature.setContext({
      targetingKey: user.email,
      clientEmail: user.email,
      clientCountry: user.country
    });

    const client = OpenFeature.getClient();

    // Add event listener for real-time updates
    client.addHandler(ProviderEvents.ConfigurationChanged, () => {
      const discountEnabled = client.getBooleanValue('discount-enabled', false);
      this.showDiscountBanner.set(discountEnabled);
    });

    // Initial evaluation
    const discountEnabled = client.getBooleanValue('discount-enabled', false);
    this.showDiscountBanner.set(discountEnabled);
  }
```

And remember to add the missing `ProviderEvents` import:

```typescript
import {OpenFeature, ProviderEvents} from '@openfeature/web-sdk';
```

### Testing Events

🛠️ To test it, keep your browser open and modify the `api/src/main/docker/go-feature-flag/canary-flags.yaml` file (or whichever flag file your proxy is currently using). For instance, change the `clientEmail` targeting suffix from `musician.com` to `producer.com`.

👀 Without refreshing the page, your browser's UI should automatically update! If you open your browser's Developer Tools console, you might see log messages like:

```bash
OpenFeature Provider Configuration Changed:
Object { message: "flag configuration have changed", flagsChanged: (1) […], clientName: undefined, domain: undefined, providerName: "_GoFeatureFlagWebProvider" }
```
