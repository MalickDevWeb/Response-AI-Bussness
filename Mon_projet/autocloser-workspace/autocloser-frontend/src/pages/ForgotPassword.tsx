import { Link } from 'react-router-dom';
import { Smartphone, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';

export default function ForgotPassword() {
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    toast.success('Instructions envoyées par SMS/WhatsApp !');
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh', alignItems: 'center', justifyContent: 'center', padding: '1rem' }}>
      <div style={{ background: 'var(--bg-card)', padding: '2.5rem', borderRadius: '16px', border: '1px solid var(--border)', width: '100%', maxWidth: '420px', boxShadow: '0 20px 40px rgba(0,0,0,0.5)' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h2>Mot de passe oublié</h2>
          <p style={{ color: 'var(--text-soft)', fontSize: '0.9rem', marginTop: '0.5rem' }}>
            Entrez votre numéro pour recevoir un code de réinitialisation.
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label className="input-label">Numéro de Téléphone</label>
            <div style={{ position: 'relative' }}>
              <Smartphone size={18} style={{ position: 'absolute', left: '1rem', top: '1rem', color: 'var(--text-muted)' }} />
              <input type="tel" className="input-field" placeholder="Ex: 221770000000" style={{ paddingLeft: '2.5rem' }} required />
            </div>
          </div>

          <button className="btn btn-primary btn-block" style={{ marginTop: '1rem' }}>
            Envoyer le lien <ArrowRight size={18} />
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.9rem', color: 'var(--text-soft)' }}>
          <Link to="/login" style={{ color: 'var(--primary-light)', textDecoration: 'none', fontWeight: 600 }}>Retour à la connexion</Link>
        </p>
      </div>
    </div>
  );
}
