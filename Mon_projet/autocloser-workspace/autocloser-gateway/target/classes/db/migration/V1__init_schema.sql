-- ============================================================
-- V1__init_schema.sql
-- Migration Flyway initiale - AutoCloser AI (Mois 1)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS commercants (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nom_boutique TEXT NOT NULL,
    telephone_commercant TEXT UNIQUE NOT NULL,
    email_notification TEXT NOT NULL,
    whatsapp_actif BOOLEAN DEFAULT FALSE,
    instagram_actif BOOLEAN DEFAULT FALSE,
    tiktok_actif BOOLEAN DEFAULT FALSE,
    pack_media_actif BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL
);

CREATE TABLE IF NOT EXISTS produits (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    commercant_id UUID REFERENCES commercants(id) ON DELETE CASCADE,
    nom TEXT NOT NULL,
    description TEXT,
    prix NUMERIC NOT NULL,
    stock INTEGER DEFAULT 1,
    actif BOOLEAN DEFAULT TRUE,
    image_url TEXT,
    embedding VECTOR(4096),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL
);

CREATE TABLE IF NOT EXISTS conversations (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    commercant_id UUID REFERENCES commercants(id) ON DELETE CASCADE,
    client_id TEXT NOT NULL,
    plateforme TEXT NOT NULL,
    statut TEXT DEFAULT 'EN_COURS',
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    conversation_id UUID REFERENCES conversations(id) ON DELETE CASCADE,
    role TEXT NOT NULL,
    contenu TEXT NOT NULL,
    type_media TEXT DEFAULT 'text',
    media_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL
);

-- Index de performance
CREATE INDEX IF NOT EXISTS idx_produits_commercant ON produits(commercant_id);
CREATE INDEX IF NOT EXISTS idx_conversations_commercant ON conversations(commercant_id);
CREATE INDEX IF NOT EXISTS idx_conversations_client ON conversations(client_id);
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);

-- Données de test (Commerçant beta)
INSERT INTO commercants (nom_boutique, telephone_commercant, email_notification, whatsapp_actif)
VALUES ('Boutique Test', '+221770000001', 'test@autocloser.ai', TRUE)
ON CONFLICT (telephone_commercant) DO NOTHING;
