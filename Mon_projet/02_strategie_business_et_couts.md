# Stratégie Commerciale & Modèle Économique : AUTOCLOSER AI

## 1. Modèle Économique sur 5 Ans (MRR)

*   **Année 1 (Amorçage) :** 100 E-commerçants cibles. Objectif : ~1 000 000 FCFA / mois.
*   **Année 2-3 (Scalabilité) :** Expansion en Afrique Francophone (Sénégal, CI, Mali). 1000 commerçants. Objectif : ~10 000 000 FCFA / mois via l'ajout de modules Premium.
*   **Année 4-5 (Enterprise) :** Offre en "Marque Blanche" pour de grosses entreprises (Banques, Assurances). 

## 2. Gestion des Paiements (Bypass Stripe)

Les commerçants africains n'utilisant pas tous la carte bancaire, l'abonnement mensuel au SaaS sera réglé via des agrégateurs locaux :
*   **FedaPay, PayTech ou CinetPay** (Support de Wave, Orange Money, MTN, etc.).
*   En cas de non-paiement mensuel, le bot se désactive automatiquement de la session WhatsApp du commerçant.

## 3. Réduction des Coûts : Le Plan "Zéro Frais" (Alternative à AWS)

Le plus gros risque financier d'une startup IA est le coût d'hébergement des GPU. Voici le plan pour débuter avec 0 FCFA :

*   **Phase 1 (Le MVP) : Le Serveur Maison (Coût: 0 FCFA)**
    Toute l'architecture (Ollama, Redis, API) tourne sur l'ordinateur de développement local. On utilise **Cloudflare Tunnels** ou **Ngrok** pour l'exposer sur Internet et recevoir les webhooks de WhatsApp. 
    *Variante :* Utiliser l'API gratuite de **Groq** (qui propose Llama 3) pour les premiers jours le temps d'encaisser les premiers clients.
*   **Phase 2 (Croissance) : Les GPU Serverless ou Low-Cost (Coût: ~5 000 à 50 000 FCFA)**
    Dès que l'application nécessite le cloud, on évite AWS. On utilise **RunPod** ou **Vast.ai** (location de RTX 3090 à prix cassé), ou des plateformes Serverless comme **Modal.com** (paiement à la seconde de calcul uniquement quand un client envoie un message).
*   **Phase 3 (Mise à l'échelle) : Bare-Metal**
    Location d'un serveur physique dédié avec GPU chez **Hetzner** (~75 000 FCFA / mois) offrant une marge énorme sur le volume.

## 4. Stratégie Marketing & Communication

*   **L'Affiliation B2B :** Offrir 30% des revenus récurrents aux formateurs e-commerce / dropshipping pour qu'ils obligent leurs élèves à utiliser AutoCloser AI.
*   **Freemium Viral :** L'IA ajoute la signature *"Powered by AutoCloser AI"* à la fin des discussions sur les versions gratuites.
*   **Preuve Sociale par l'Exemple :** Montrer des vidéos réelles du bot en train de clôturer une vente à 3h du matin sans intervention humaine.
