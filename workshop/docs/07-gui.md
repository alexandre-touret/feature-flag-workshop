---
sidebar_position: 7
title: 7. Feature flag on the client side
---

:::info
 ℹ️ What will you do and learn in this chapter?
- Integrate Feature Flagging in an Angular App
:::

*   Goal: Hide/Show UI elements related to the discount ("Promo" badges, crossed-out prices).
*   Frontend (GUI):
    *   Integration of the OpenFeature Web SDK.
    *   Use of a synchronized Provider (FlagSmith offers a compatible JS SDK).
    *   Creation of an Angular *ifFeature directive or a Guard to protect components.
*   Problematic: Managing the desynchronization between the Backend (price) and Frontend (display) flags.
*   Semantic Versioning (SemVer) : Montrer comment activer une fonctionnalité uniquement pour les utilisateurs ayant une         MCP
     version d'application clientVersion >= "1.2.0". C'est un cas d'usage extrêmement courant en feature flagging (surtout côté       • context7 Connected
     mobile).


## Setup

Stop the GUI typing ``CTRL+C``.

Install OpenFeature and the Go Feature Flag Angular SDK.

```bash
$ cd gui
$ npm install @openfeature/go-feature-flag-web-provider
$ npm install @openfeature/angular-sdk
```

In the file ``/src/app.config.js``, Add the OpenFeature import declaration

```typescript
provideAnimationsAsync(),
   importProvidersFrom(
     OpenFeatureModule.forRoot({
       provider: new GoFeatureFlagWebProvider({
         endpoint: 'http://localhost:1031'
       }),
     })
   )
```

Declare then the imports:

```typescript

import {OpenFeatureModule} from '@openfeature/angular-sdk';
import {GoFeatureFlagWebProvider} from '@openfeature/go-feature-flag-web-provider';
```

In the file ``/src/pages/instruments/instrument-list/instrument-list.component.ts``
Add the ``UserService`` as a variable of the ``InstrumentListComponent``:


```typescript
export class InstrumentListComponent implements OnInit {
  private instrumentService = inject(InstrumentService);
  private userService = inject(UserService);
  private dialog = inject(MatDialog);
```

Change then the method ``initFeatureFlags()``:

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

Declare then these imports:

```typescript
import {OpenFeature} from '@openfeature/web-sdk';
import {UserService} from '../../../services/user.service';
```

Start the front end :

```bash
$ npm start
```
Change your user email and country. Refresh the page and you should see the discount button.

## Event management
