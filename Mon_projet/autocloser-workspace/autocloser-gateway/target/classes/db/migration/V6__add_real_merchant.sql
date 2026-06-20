-- ============================================================
-- V6__add_real_merchant.sql
-- Ajout du commerçant réel pour le test local WhatsApp
-- ============================================================

-- On utilise l'indicatif 221 (Sénégal) sans le '+' pour être compatible avec Evolution API
INSERT INTO commercants (id, telephone_commercant, nom_boutique, email_notification, whatsapp_actif)
VALUES (
    '550e8400-e29b-41d4-a716-446655440003', 
    '221771719013', 
    'Boutique PMT', 
    'pmt@autocloser.ai',
    true
) ON CONFLICT (telephone_commercant) DO NOTHING;

-- Ajout d'un produit de test pour ce commerçant
INSERT INTO produits (id, commercant_id, nom, description, prix, stock, actif, embedding)
VALUES (
    '660e8400-e29b-41d4-a716-446655440004',
    '550e8400-e29b-41d4-a716-446655440003',
    'Robe en soie noire',
    'Une magnifique robe en soie noire pour vos soirées, taille M.',
    25000,
    10,
    true,
    NULL
) ON CONFLICT ON CONSTRAINT unique_product_per_merchant DO NOTHING;
