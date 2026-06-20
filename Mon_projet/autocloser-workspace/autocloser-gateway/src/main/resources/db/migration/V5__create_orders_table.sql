-- ============================================================
-- V5__create_orders_table.sql
-- Table pour stocker les commandes du Mois 3
-- ============================================================

CREATE TABLE IF NOT EXISTS commandes (
    id UUID PRIMARY KEY,
    commercant_id UUID NOT NULL REFERENCES commercants(id) ON DELETE CASCADE,
    client_id VARCHAR(50) NOT NULL,
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    montant_total DOUBLE PRECISION NOT NULL,
    statut VARCHAR(50) NOT NULL, -- EN_ATTENTE_PAIEMENT, PAYEE, ANNULEE, LIVREE
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index pour accélérer les recherches par commerçant ou par conversation
CREATE INDEX IF NOT EXISTS idx_commandes_commercant ON commandes(commercant_id);
CREATE INDEX IF NOT EXISTS idx_commandes_conversation ON commandes(conversation_id);
