-- ============================================================
-- V4__optimize_rag_index_and_constraints.sql
-- Corrections suite à code review (production readiness)
-- ============================================================

-- 1. Sécurité : s'assurer que pgvector est bien là
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. Supprimer l'ancien index IVFFlat sous-optimal (lists=10)
DROP INDEX IF EXISTS idx_produits_embedding_ivf;
DROP INDEX IF EXISTS idx_produits_embedding;

-- 3. Index IVFFlat optimisé pour scalabilité
--    Règle : lists ≈ sqrt(nb_lignes)
--    → 10 pour dev (100 rows), 100 pour prod (10K rows)
--    On part sur 100 pour être prod-ready dès maintenant
CREATE INDEX IF NOT EXISTS idx_produits_embedding_ivf
    ON produits USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- 4. Statistiques PostgreSQL : OBLIGATOIRE pour que le planner
--    utilise l'index IVFFlat correctement
ANALYZE produits;

-- 5. Contrainte d'unicité logique produit (idempotente)
--    Empêche d'insérer 10x "Robe en soie noire" pour le même commerçant
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'unique_product_per_merchant'
    ) THEN
        ALTER TABLE produits
            ADD CONSTRAINT unique_product_per_merchant
            UNIQUE (commercant_id, nom);
    END IF;
END$$;

-- 6. Commentaire d'audit
COMMENT ON INDEX idx_produits_embedding_ivf IS
    'Index IVFFlat pour recherche RAG par similarité cosinus. lists=100 (prod-ready). Migrer vers HNSW en prod > 50K produits.';
