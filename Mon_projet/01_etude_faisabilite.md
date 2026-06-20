# Étude de Faisabilité et Architecture Technique : AUTOCLOSER AI

**Date :** Juin 2026
**Type de projet :** SaaS B2B Omnicanal avec IA Locale
**Objectif :** Automatisation du tunnel de vente et de la qualification client sur les réseaux sociaux.

---

## 1. Architecture Technique (Microservices)

L'architecture repose exclusivement sur des modèles d'IA open-source et gratuits de droits auto-hébergés.

*   **API Gateway & Worker :** Node.js (NestJS) ou Python (FastAPI). Gère les webhooks entrants (WhatsApp) et la logique métier.
*   **Message Broker :** Redis (+ BullMQ) pour lisser la charge des requêtes entrantes sans faire planter l'IA.
*   **Base de Données Principale :** PostgreSQL hébergé via Supabase (avec l'extension `pgvector` pour le RAG).
*   **Moteurs d'IA Locaux :**
    *   *LLM :* Ollama (Llama 3 ou Mistral) pour le texte.
    *   *Vision :* Llama 3.2 Vision pour analyser les reçus de paiement (Wave, Orange Money).
    *   *STT (Audio -> Texte) :* Faster-Whisper.
    *   *TTS (Texte -> Audio) :* Kokoro-82M.
*   **Passerelle WhatsApp :** Evolution API (Conteneur Docker).

---

## 2. Ressources Humaines & Délais (MVP)

**Équipe idéale au lancement :** 1 à 2 développeurs polyvalents (Backend / IA / DevOps).

**Délai de réalisation (MVP) : 3 à 4 mois**
*   *Mois 1 :* Infrastructure Docker, Supabase, Webhooks WhatsApp (Evolution API).
*   *Mois 2 :* Intégration Ollama, système RAG (Catalogue de produits).
*   *Mois 3 :* Traitement audio (Whisper/Kokoro) et lecture de reçus.
*   *Mois 4 :* Création du Dashboard (Next.js) et intégration paiements SaaS.

---

## 3. Schéma de Base de Données (PostgreSQL)

4 tables fondamentales pour démarrer :
1.  **commercants** (id, nom_boutique, telephone_commercant, pack_actif...)
2.  **produits** (id, nom, prix, stock, embedding_vector...)
3.  **conversations** (id, commercant_id, client_id, statut...)
4.  **messages** (id, role, contenu, type_media...)
