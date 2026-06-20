import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Smartphone, Lock, Store, Mail, Eye, EyeOff, CheckCircle, RefreshCcw } from 'lucide-react';
import toast from 'react-hot-toast';
import { apiClient } from '../api/axios';

type Step = 1 | 2 | 3;

type ApiError = {
  response?: {
    data?: {
      erreur?: string;
    };
  };
};

export default function Register() {
  const navigate = useNavigate();
  const [step, setStep] = useState<Step>(1);
  const [loading, setLoading] = useState(false);

  // Fields
  const [telephone, setTelephone] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [generatedOtp, setGeneratedOtp] = useState('');
  const [nomBoutique, setNomBoutique] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPass, setShowPass] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  /* ── Étape 1 : Envoi OTP ── */
  const handleSendOTP = (e: React.FormEvent) => {
    e.preventDefault();
    const cleanedPhone = telephone.replace(/\D/g, '');

    if (cleanedPhone.length < 9) {
      return toast.error('Entrez un numéro WhatsApp valide');
    }

    const code = Math.floor(100000 + Math.random() * 900000).toString();
    setTelephone(cleanedPhone);
    setGeneratedOtp(code);
    setOtpCode('');
    toast.success('Code OTP généré pour la vérification');
    setStep(2);
  };

  /* ── Étape 2 : Vérification OTP ── */
  const handleVerifyOTP = (e: React.FormEvent) => {
    e.preventDefault();
    if (otpCode.length !== 6) {
      return toast.error('Le code OTP doit contenir 6 chiffres');
    }
    if (generatedOtp && otpCode !== generatedOtp) {
      return toast.error('Code OTP incorrect');
    }
    toast.success('Numéro vérifié');
    setStep(3);
  };

  const resendOtp = () => {
    const code = Math.floor(100000 + Math.random() * 900000).toString();
    setGeneratedOtp(code);
    setOtpCode('');
    toast.success('Nouveau code OTP généré');
  };

  /* ── Étape 3 : Finalisation ── */
  const handleFinalize = async (e: React.FormEvent) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      return toast.error('Les mots de passe ne correspondent pas !');
    }
    if (password.length < 6) {
      return toast.error('Le mot de passe doit contenir au moins 6 caractères');
    }
    setLoading(true);
    try {
      const response = await apiClient.post('/onboarding/inscription', {
        nomBoutique,
        telephone,
        email,
        domaine: 'Général',
      });
      if (response.data) {
        localStorage.setItem('autocloser_commercant_id', response.data.idCommercant);
        localStorage.setItem('autocloser_instance_nom', nomBoutique.replace(/[^a-zA-Z0-9]/g, '').toLowerCase());
        toast.success('Boutique créée ! Redirection...');
        setTimeout(() => navigate('/dashboard'), 1500);
      }
    } catch (error: unknown) {
      const apiError = error as ApiError;
      toast.error(apiError.response?.data?.erreur || "Erreur lors de l'inscription.");
    } finally {
      setLoading(false);
    }
  };

  const stepLabels = ['Numéro', 'Vérification', 'Finalisation'];

  return (
    <div className="auth-layout">
      {/* ── Colonne gauche : Hero ── */}
      <div className="auth-hero">
        <div className="auth-hero-content">
          <div className="auth-logo">Auto<span>Closer AI</span></div>
          <h1 className="auth-hero-title">
            Votre boutique.<br />
            <span style={{ color: 'var(--primary-light)' }}>Automatisée en 2 min.</span>
          </h1>
          <p className="auth-hero-sub">
            Inscrivez votre boutique, connectez WhatsApp et laissez l'intelligence artificielle gérer vos ventes automatiquement.
          </p>
          <ul className="auth-feature-list">
            <li>✅ Inscription rapide par numéro</li>
            <li>✅ Vérification OTP sécurisée</li>
            <li>✅ Connexion WhatsApp en 30 secondes</li>
            <li>✅ IA opérationnelle immédiatement</li>
          </ul>
        </div>
        <img src="/auth-hero.png" alt="AutoCloser AI" className="auth-hero-img" />
      </div>

      {/* ── Colonne droite : Formulaire ── */}
      <div className="auth-form-col">
        <div className="auth-form-box">
          {/* Stepper */}
          <div className="stepper">
            {stepLabels.map((label, i) => (
              <div key={i} className={`step-item ${step > i + 1 ? 'done' : ''} ${step === i + 1 ? 'active' : ''}`}>
                <div className="step-circle">
                  {step > i + 1 ? <CheckCircle size={14} /> : i + 1}
                </div>
                <span>{label}</span>
              </div>
            ))}
          </div>

          {/* ── ÉTAPE 1 ── */}
          {step === 1 && (
            <>
              <div className="auth-form-header">
                <h2>Créer ma boutique</h2>
                <p>Entrez votre numéro WhatsApp pour commencer</p>
              </div>
              <form onSubmit={handleSendOTP}>
                <div className="input-group">
                  <label className="input-label">Numéro WhatsApp</label>
                  <div className="input-icon-wrap">
                    <Smartphone size={18} className="input-icon" />
                    <input type="tel" className="input-field input-with-icon" placeholder="221 77 000 00 00"
                      value={telephone} onChange={e => setTelephone(e.target.value)} autoComplete="tel" required />
                  </div>
                  <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '0.4rem' }}>
                    Un code OTP sera envoyé sur ce numéro WhatsApp
                  </p>
                </div>
                <button className="btn btn-primary btn-block">Recevoir le code OTP</button>
              </form>
            </>
          )}

          {/* ── ÉTAPE 2 ── */}
          {step === 2 && (
            <>
              <div className="auth-form-header">
                <h2>Vérification</h2>
                <p>
                  Entrez le code reçu sur <strong style={{ color: 'var(--text-main)' }}>{telephone}</strong>
                </p>
              </div>
              <div className="otp-dev-code" aria-live="polite">
                <span>Code OTP de test</span>
                <strong>{generatedOtp}</strong>
              </div>
              <form onSubmit={handleVerifyOTP}>
                <div className="input-group">
                  <label className="input-label">Code OTP (6 chiffres)</label>
                  <input
                    type="text"
                    inputMode="numeric"
                    pattern="[0-9]*"
                    maxLength={6}
                    className="input-field otp-input"
                    value={otpCode}
                    onChange={e => setOtpCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                    autoComplete="one-time-code"
                    required
                  />
                  <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '0.4rem' }}>
                    Ne partagez jamais ce code. Il est confidentiel.
                  </p>
                </div>
                <button className="btn btn-primary btn-block">Vérifier le code</button>
                <button type="button" className="btn btn-outline btn-block" style={{ marginTop: '0.75rem' }}
                  onClick={resendOtp}>
                  <RefreshCcw size={18} /> Renvoyer un code
                </button>
                <button type="button" className="btn btn-ghost btn-block" style={{ marginTop: '0.75rem' }}
                  onClick={() => setStep(1)}>Retour au numéro</button>
              </form>
            </>
          )}

          {/* ── ÉTAPE 3 ── */}
          {step === 3 && (
            <>
              <div className="auth-form-header">
                <h2>Dernière étape</h2>
                <p>Configurez votre boutique et votre mot de passe</p>
              </div>
              <form onSubmit={handleFinalize}>
                <div className="input-group">
                  <label className="input-label">Nom de la boutique</label>
                  <div className="input-icon-wrap">
                    <Store size={18} className="input-icon" />
                    <input type="text" className="input-field input-with-icon" placeholder="Ex: Mon Shop Dakar"
                      value={nomBoutique} onChange={e => setNomBoutique(e.target.value)} required />
                  </div>
                </div>
                <div className="input-group">
                  <label className="input-label">Email professionnel</label>
                  <div className="input-icon-wrap">
                    <Mail size={18} className="input-icon" />
                    <input type="email" className="input-field input-with-icon" placeholder="contact@monshop.com"
                      value={email} onChange={e => setEmail(e.target.value)} required />
                  </div>
                </div>
                <div className="input-group">
                  <label className="input-label">Mot de passe</label>
                  <div className="input-icon-wrap">
                    <Lock size={18} className="input-icon" />
                    <input type={showPass ? 'text' : 'password'} className="input-field input-with-icon input-with-icon-right"
                      placeholder="Minimum 6 caractères" value={password} onChange={e => setPassword(e.target.value)} required />
                    <button type="button" className="input-icon-right" onClick={() => setShowPass(v => !v)} tabIndex={-1}>
                      {showPass ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                </div>
                <div className="input-group">
                  <label className="input-label">Confirmer le mot de passe</label>
                  <div className="input-icon-wrap">
                    <Lock size={18} className="input-icon" />
                    <input type={showConfirm ? 'text' : 'password'} className="input-field input-with-icon input-with-icon-right"
                      placeholder="Répétez le mot de passe"
                      value={confirmPassword}
                      onChange={e => setConfirmPassword(e.target.value)}
                      style={{ borderColor: confirmPassword && confirmPassword !== password ? '#e74c3c' : undefined }}
                      required />
                    <button type="button" className="input-icon-right" onClick={() => setShowConfirm(v => !v)} tabIndex={-1}>
                      {showConfirm ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                  {confirmPassword && confirmPassword !== password && (
                    <p style={{ fontSize: '0.78rem', color: '#e74c3c', marginTop: '0.4rem' }}>
                      ⚠ Les mots de passe ne correspondent pas
                    </p>
                  )}
                </div>
                <button className="btn btn-primary btn-block" disabled={loading} style={{ marginTop: '0.5rem' }}>
                  {loading ? 'Création...' : 'Lancer ma boutique'}
                </button>
              </form>
            </>
          )}

          {step === 1 && (
            <p style={{ textAlign: 'center', marginTop: '2rem', fontSize: '0.9rem', color: 'var(--text-soft)' }}>
              Déjà inscrit ?{' '}
              <Link to="/login" style={{ color: 'var(--primary-light)', textDecoration: 'none', fontWeight: 700 }}>
                Se connecter →
              </Link>
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
