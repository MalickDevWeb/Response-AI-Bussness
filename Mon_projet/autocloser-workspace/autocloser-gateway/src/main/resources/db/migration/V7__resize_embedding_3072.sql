-- ============================================================
-- V7__resize_embedding_3072.sql
-- qwen2.5-coder:1.5b utilise une dimension de 3072.
-- ============================================================

-- Suppression de l'index existant
DROP INDEX IF EXISTS idx_produits_embedding;
DROP INDEX IF EXISTS idx_produits_embedding_ivf;

-- Vider les embeddings existants de taille 1536 car on ne peut pas redimensionner avec des données existantes incompatibles
UPDATE produits SET embedding = NULL;

-- Redimensionnement
ALTER TABLE produits ALTER COLUMN embedding TYPE VECTOR(3072);

-- Note : pgvector ne supporte pas les index ivfflat ou hnsw pour des dimensions > 2000.
-- On laisse sans index, la recherche exacte par scan séquentiel (ORDER BY <=>) 
-- sera utilisée et c'est parfaitement suffisant pour un prototype.
