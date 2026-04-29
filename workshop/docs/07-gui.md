---
sidebar_position: 7
title: 7. Feature flag on the client side
---
*   Goal: Hide/Show UI elements related to the discount ("Promo" badges, crossed-out prices).
*   Frontend (GUI):
    *   Integration of the OpenFeature Web SDK.
    *   Use of a synchronized Provider (FlagSmith offers a compatible JS SDK).
    *   Creation of an Angular *ifFeature directive or a Guard to protect components.
*   Problematic: Managing the desynchronization between the Backend (price) and Frontend (display) flags.
*   Semantic Versioning (SemVer) : Montrer comment activer une fonctionnalité uniquement pour les utilisateurs ayant une version d'application clientVersion >= "1.2.0". C'est un cas d'usage extrêmement courant en feature flagging (surtout côté mobile).
