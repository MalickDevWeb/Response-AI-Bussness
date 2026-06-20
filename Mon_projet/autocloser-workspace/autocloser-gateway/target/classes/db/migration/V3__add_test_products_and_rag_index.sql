-- ============================================================
-- V3__add_test_products_and_rag_index.sql
-- Mois 2 : Ajout des produits de test et index IVFFlat pour RAG
-- Note : La colonne embedding VECTOR(1536) a déjà été créée en V2
-- ============================================================

-- Index IVFFlat pour la recherche RAG (plus léger et plus rapide que HNSW pour le dev)
-- On supprime d'abord l'éventuel HNSW de la V2 pour le remplacer par IVFFlat
DROP INDEX IF EXISTS idx_produits_embedding;
CREATE INDEX IF NOT EXISTS idx_produits_embedding_ivf
    ON produits USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 10);

-- Données de test : 4 produits pour la boutique beta (commerçant +221770000001)
INSERT INTO produits (commercant_id, nom, description, prix, stock, actif)
SELECT c.id, 'Robe en soie noire', 'Robe élégante en soie noire, taille S/M/L', 25000, 5, TRUE
FROM commercants c WHERE c.telephone_commercant = '+221770000001'
ON CONFLICT DO NOTHING;

INSERT INTO produits (commercant_id, nom, description, prix, stock, actif)
SELECT c.id, 'Veste en cuir marron', 'Veste en cuir véritable, style moderne, taille M/L/XL', 45000, 3, TRUE
FROM commercants c WHERE c.telephone_commercant = '+221770000001'
ON CONFLICT DO NOTHING;

INSERT INTO produits (commercant_id, nom, description, prix, stock, actif)
SELECT c.id, 'Sac à main doré', 'Sac à main de luxe avec finition dorée, idéal soirée', 18000, 8, TRUE
FROM commercants c WHERE c.telephone_commercant = '+221770000001'
ON CONFLICT DO NOTHING;

INSERT INTO produits (commercant_id, nom, description, prix, stock, actif)
SELECT c.id, 'Chaussures talons rouge', 'Escarpins rouge vif 7cm de talon, pointure 36-41', 32000, 4, TRUE
FROM commercants c WHERE c.telephone_commercant = '+221770000001'
ON CONFLICT DO NOTHING;
