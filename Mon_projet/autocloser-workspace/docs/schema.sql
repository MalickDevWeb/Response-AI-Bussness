-- ============================================================
-- AUTOCLOSER AI - Schéma de Base de Données (Version 1.0)
-- À exécuter directement dans PostgreSQL / Supabase
-- ============================================================

-- Activation de l'extension vectorielle pour le système RAG
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- TABLE 1: Commerçants (Les clients du SaaS)
-- ============================================================
CREATE TABLE commercants (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nom_boutique TEXT NOT NULL,
    telephone_commercant TEXT UNIQUE NOT NULL, -- Format international ex: +221771234567
    email_notification TEXT NOT NULL,

    -- Modules d'abonnement (déverrouillés après paiement)
    whatsapp_actif BOOLEAN DEFAULT FALSE,
    instagram_actif BOOLEAN DEFAULT FALSE,
    tiktok_actif BOOLEAN DEFAULT FALSE,
    pack_media_actif BOOLEAN DEFAULT FALSE,   -- Active la voix + validation reçus

    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- ============================================================
-- TABLE 2: Produits (Catalogue + RAG)
-- ============================================================
CREATE TABLE produits (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    commercant_id UUID REFERENCES commercants(id) ON DELETE CASCADE,
    nom TEXT NOT NULL,
    description TEXT,
    prix NUMERIC NOT NULL,
    stock INTEGER DEFAULT 1,
    actif BOOLEAN DEFAULT TRUE,              -- Mis à FALSE si rupture de stock
    image_url TEXT,                          -- Lien Supabase Storage

    -- Vecteur d'embedding (4096 dimensions pour Llama 3 / Mistral)
    embedding VECTOR(4096),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- ============================================================
-- TABLE 3: Conversations (Sessions de vente actives)
-- ============================================================
CREATE TABLE conversations (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    commercant_id UUID REFERENCES commercants(id) ON DELETE CASCADE,
    client_id TEXT NOT NULL,                 -- Numéro ou identifiant unique du client
    plateforme TEXT NOT NULL,                -- 'whatsapp' | 'instagram' | 'tiktok'
    statut TEXT DEFAULT 'en_cours',          -- 'en_cours' | 'attente_paiement' | 'paye'

    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- ============================================================
-- TABLE 4: Messages (Historique pour le contexte du LLM)
-- ============================================================
CREATE TABLE messages (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    conversation_id UUID REFERENCES conversations(id) ON DELETE CASCADE,
    role TEXT NOT NULL,                      -- 'client' | 'ia' | 'commercant'
    contenu TEXT NOT NULL,
    type_media TEXT DEFAULT 'text',          -- 'text' | 'audio' | 'image'
    media_url TEXT,                          -- URL du fichier audio ou image

    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- ============================================================
-- INDEX DE PERFORMANCE
-- ============================================================
-- Recherche vectorielle ultra-rapide pour le RAG
CREATE INDEX ON produits USING hnsw (embedding vector_cosine_ops);

-- Accélération des jointures fréquentes
CREATE INDEX idx_produits_commercant ON produits(commercant_id);
CREATE INDEX idx_conversations_commercant ON conversations(commercant_id);
CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_conversations_client ON conversations(client_id);
