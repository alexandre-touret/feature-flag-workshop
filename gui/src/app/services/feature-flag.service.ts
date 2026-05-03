import {Injectable} from '@angular/core';
import {OpenFeature, ProviderEvents} from '@openfeature/web-sdk';

@Injectable({
  providedIn: 'root'
})
export class FeatureFlagService {
  private client = OpenFeature.getClient();

  async setContext(email: string, country: string) {
    await OpenFeature.setContext({
      targetingKey: email,
      clientEmail: email,
      clientCountry: country
    });
  }

  isDiscountEnabled(): boolean {
    return this.client.getBooleanValue('discount-enabled', false);
  }

  onConfigurationChanged(callback: (eventDetails: any) => void) {
    this.client.addHandler(ProviderEvents.ConfigurationChanged, callback);
  }

  onContextChanged(callback: (eventDetails: any) => void) {
    this.client.addHandler(ProviderEvents.ContextChanged, callback);
  }
}
