import {ApplicationConfig, importProvidersFrom, provideExperimentalZonelessChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';

import {routes} from './app.routes';
import {userInterceptor} from './interceptors/user.interceptor';
import {OpenFeatureModule} from '@openfeature/angular-sdk';
import {GoFeatureFlagWebProvider} from '@openfeature/go-feature-flag-web-provider';

export const appConfig: ApplicationConfig = {
  providers: [
    provideExperimentalZonelessChangeDetection(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([userInterceptor])),
    provideAnimationsAsync(),
    importProvidersFrom(
      OpenFeatureModule.forRoot({
        provider: new GoFeatureFlagWebProvider({
          endpoint: 'http://localhost:1031'
        }),
      })
    )
  ]
};
