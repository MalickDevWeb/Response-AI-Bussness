import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Smartphone, Lock, Eye, EyeOff, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Login() {
  const navigate = useNavigate();
  const [showPass, setShowPass] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    toast.success('Connexion réussie !');
    navigate('/dashboard');
  };

  return (
    <div className="auth-layout">
      {/* ── Colonne gauche : Hero ── */}
      <div className="auth-hero">
        <div className="auth-hero-content">
          <div className="auth-logo">Auto<span>Closer AI</span></div>
          <h1 className="auth-hero-title">
            Simplifiez vos ventes.<br />
            <span style={{ color: 'var(--primary-light)' }}>L'IA vend pour vous.</span>
          </h1>
          <p className="auth-hero-sub">
            Connectez WhatsApp et laissez votre agent IA répondre, vendre et encaisser 24h/24 — même quand vous dormez.
          </p>
          <div className="auth-hero-stats">
            <div className="stat"><strong>+3x</strong><span>ventes moyennes</span></div>
            <div className="stat"><strong>24/7</strong><span>réponses auto</span></div>
            <div className="stat"><strong>0 CFA</strong><span>par conversation</span></div>
          </div>
        </div>
        <img src="/auth-hero.png" alt="AutoCloser AI" className="auth-hero-img" />
      </div>

      {/* ── Colonne droite : Formulaire ── */}
      <div className="auth-form-col">
        <div className="auth-form-box">
          <div style={{ marginBottom: '2rem' }}>
            <h2 style={{ fontSize: '1.8rem', marginBottom: '0.4rem' }}>Bon retour ! 👋</h2>
            <p style={{ color: 'var(--text-soft)', fontSize: '0.95rem' }}>Connectez-vous à votre espace commerçant</p>
          </div>

          <form onSubmit={handleSubmit}>
            {/* Numéro */}
            <div className="input-group">
              <label className="input-label">Numéro de Téléphone</label>
              <div className="input-icon-wrap">
                <Smartphone size={18} className="input-icon" />
                <input type="tel" className="input-field input-with-icon" placeholder="Ex: 221 77 000 00 00" required />
              </div>
            </div>

            {/* Mot de passe */}
            <div className="input-group">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <label className="input-label">Mot de passe</label>
                <Link to="/forgot-password" style={{ fontSize: '0.8rem', color: 'var(--primary)', textDecoration: 'none', fontWeight: 500 }}>
                  Mot de passe oublié ?
                </Link>
              </div>
              <div className="input-icon-wrap">
                <Lock size={18} className="input-icon" />
                <input
                  type={showPass ? 'text' : 'password'}
                  className="input-field input-with-icon input-with-icon-right"
                  placeholder="••••••••"
                  required
                />
                <button type="button" className="input-icon-right" onClick={() => setShowPass(v => !v)} tabIndex={-1}>
                  {showPass ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <button className="btn btn-primary btn-block" style={{ marginTop: '1.5rem', gap: '0.5rem' }}>
              Se connecter <ArrowRight size={18} />
            </button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '2rem', fontSize: '0.9rem', color: 'var(--text-soft)' }}>
            Pas encore de compte ?{' '}
            <Link to="/register" style={{ color: 'var(--primary-light)', textDecoration: 'none', fontWeight: 700 }}>
              Créer ma boutique →
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
