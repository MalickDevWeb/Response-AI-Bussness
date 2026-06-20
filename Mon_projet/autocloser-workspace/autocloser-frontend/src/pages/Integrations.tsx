import { useState, useEffect, useRef } from 'react';
import toast from 'react-hot-toast';
import { apiClient } from '../api/axios';
import { QRCodeSVG } from 'qrcode.react';

type WhatsAppStatus = 'idle' | 'loading' | 'polling' | 'ready' | 'error';
type ErrorReason = 'timeout' | 'network' | null;
type RequestError = {
  response?: unknown;
  message?: string;
};

export default function Integrations() {
  const [qrCode, setQrCode]           = useState<string | null>(null);
  const [status, setStatus]           = useState<WhatsAppStatus>('idle');
  const [instanceNom, setInstanceNom] = useState<string | null>(null);
  const [attempts, setAttempts]       = useState(0);
  const [errorReason, setErrorReason] = useState<ErrorReason>(null);
  const networkErrorCount             = useRef(0);
  const pollingRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const MAX_ATTEMPTS = 12; // 12 × 3s = 36 secondes max

  useEffect(() => {
    // Fallback vers 'boutiquetest' si le localStorage a été vidé (pour simplifier vos tests)
    const saved = localStorage.getItem('autocloser_instance_nom') || 'boutiquetest';
    setInstanceNom(saved);
    return () => stopPolling();
  }, []);

  const stopPolling = () => {
    if (pollingRef.current) {
      clearTimeout(pollingRef.current);
      pollingRef.current = null;
    }
  };

  const pollQrCode = async (instance: string, attempt: number): Promise<'ready' | 'pending' | 'network_error'> => {
    try {
      networkErrorCount.current = 0; // reset on success
      const response = await apiClient.get(`/onboarding/qr/${instance}`);
      const data = response.data;

      if (data?.ready && data?.qrCodeBase64) {
        const prefix = data.qrCodeBase64.startsWith('data:') ? '' : 'data:image/png;base64,';
        setQrCode(prefix + data.qrCodeBase64);
        setStatus('ready');
        stopPolling();
        toast.success('✅ QR Code WhatsApp prêt ! Scannez maintenant.');
        return 'ready';
      }
      return 'pending';
    } catch (err: unknown) {
      const requestError = err as RequestError;
      const isNetworkError = !requestError.response; // ERR_CONNECTION_REFUSED, etc.
      if (isNetworkError) networkErrorCount.current += 1;
      console.warn(`Tentative QR ${attempt}`, isNetworkError ? '(réseau)' : '(API)', requestError.message);
      return isNetworkError ? 'network_error' : 'pending';
    }
  };

  const generateWhatsAppQR = async () => {
    if (!instanceNom) return toast.error('Instance introuvable. Veuillez vous reconnecter.');

    setStatus('polling');
    setQrCode(null);
    setAttempts(1);
    setErrorReason(null);
    networkErrorCount.current = 0;
    stopPolling();

    // Première tentative
    const result = await pollQrCode(instanceNom, 1);
    if (result === 'ready') return;

    // Polling récursif avec setTimeout
    const executePoll = async (currentAttempt: number) => {
      // Arrêt si trop d'erreurs réseau consécutives (backend inaccessible)
      if (networkErrorCount.current >= 3) {
        stopPolling();
        setErrorReason('network');
        setStatus('error');
        toast.error("❌ Serveur inaccessible. Rafraîchissez la page.");
        return;
      }

      if (currentAttempt > MAX_ATTEMPTS) {
        stopPolling();
        setErrorReason('timeout');
        setStatus('error');
        toast.error("⏱️ QR non généré. Le réseau bloque probablement WhatsApp.");
        return;
      }

      setAttempts(currentAttempt);
      const res = await pollQrCode(instanceNom, currentAttempt);

      if (res !== 'ready') {
        pollingRef.current = setTimeout(() => executePoll(currentAttempt + 1), 3000);
      }
    };

    pollingRef.current = setTimeout(() => executePoll(2), 3000);
  };

  const handleRetry = () => {
    setStatus('idle');
    setQrCode(null);
    setAttempts(0);
    setErrorReason(null);
    networkErrorCount.current = 0;
    stopPolling();
  };

  const statusLabel = () => {
    if (status === 'loading') return 'Initialisation...';
    if (status === 'polling') return `Génération du QR... (${attempts}/${MAX_ATTEMPTS})`;
    if (status === 'ready') return '✅ Connecté';
    if (status === 'error') return '❌ Erreur de connexion';
    return '';
  };

  return (
    <div>
      <h1 style={{ marginBottom: '0.5rem' }}>Intégrations Réseaux Sociaux</h1>
      <p style={{ color: 'var(--text-soft)', marginBottom: '2rem' }}>
        Connectez vos comptes pour permettre à l'IA de répondre à vos clients.
      </p>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.5rem' }}>

        {/* ─── Carte WhatsApp ─── */}
        <div style={{
          background: 'var(--bg-card)', padding: '1.5rem', borderRadius: '16px',
          border: `1px solid ${status === 'ready' ? '#25D366' : 'var(--primary)'}`,
          position: 'relative', overflow: 'hidden'
        }}>
          <div style={{
            position: 'absolute', top: 0, right: 0,
            background: status === 'ready' ? '#25D366' : 'var(--primary)',
            color: 'white', padding: '0.2rem 1rem',
            fontSize: '0.75rem', fontWeight: 'bold', borderBottomLeftRadius: '8px',
            transition: 'background 0.3s'
          }}>
            {status === 'ready' ? 'CONNECTÉ' : 'ACTIF'}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem' }}>
            <div style={{
              width: '48px', height: '48px', background: '#25D366', borderRadius: '12px',
              display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem'
            }}>💬</div>
            <div>
              <h3 style={{ margin: 0 }}>WhatsApp Business</h3>
              <p style={{ margin: 0, fontSize: '0.85rem', color: 'var(--text-soft)' }}>
                {status === 'ready' ? 'Appairage réussi ✅' : 'Connexion via QR Code'}
              </p>
            </div>
          </div>

          {/* ─── État : Idle ─── */}
          {status === 'idle' && (
            <button className="btn btn-primary btn-block" onClick={generateWhatsAppQR}>
              Associer mon numéro WhatsApp
            </button>
          )}

          {/* ─── État : Loading / Polling ─── */}
          {(status === 'loading' || status === 'polling') && (
            <div style={{ textAlign: 'center', padding: '1.5rem', background: '#0e0e0e', borderRadius: '12px' }}>
              <div className="qr-spinner" style={{ margin: '0 auto 1rem' }} />
              <p style={{ color: 'var(--text-soft)', fontSize: '0.85rem', marginBottom: '0.5rem' }}>
                {statusLabel()}
              </p>
              <div style={{
                height: '4px', background: '#2a2a2a', borderRadius: '2px', overflow: 'hidden'
              }}>
                <div style={{
                  height: '100%',
                  width: `${(attempts / MAX_ATTEMPTS) * 100}%`,
                  background: 'var(--primary)',
                  transition: 'width 0.5s ease',
                  borderRadius: '2px'
                }} />
              </div>
              <button
                className="btn btn-outline btn-block"
                style={{ marginTop: '1rem', fontSize: '0.8rem', opacity: 0.7 }}
                onClick={handleRetry}
              >
                Annuler
              </button>
            </div>
          )}

          {/* ─── État : QR Ready ─── */}
          {status === 'ready' && qrCode && (
            <div style={{
              textAlign: 'center', padding: '1.5rem',
              background: 'white', borderRadius: '12px',
              boxShadow: '0 10px 30px rgba(37,211,102,0.2)'
            }}>
              {qrCode.length > 100 ? (
                <img src={qrCode} alt="WhatsApp QR" style={{ width: '180px', height: '180px', borderRadius: '4px' }} />
              ) : (
                <QRCodeSVG value="https://wa.me/221770000000?text=AutoCloserDemo" size={180} fgColor="#080808" />
              )}
              <p style={{ color: '#111', fontSize: '0.9rem', marginTop: '1rem', fontWeight: 700 }}>
                📱 Scannez avec WhatsApp &gt; Appareils liés
              </p>
              <button className="btn btn-outline btn-block" style={{ marginTop: '0.75rem', fontSize: '0.8rem' }}
                onClick={handleRetry}>
                Générer un nouveau QR
              </button>
            </div>
          )}

          {/* ─── État : Erreur ─── */}
          {status === 'error' && (
            <div style={{ textAlign: 'center', padding: '1.5rem', background: '#1a0a0a', borderRadius: '12px', border: '1px solid #e74c3c' }}>
              <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
                {errorReason === 'network' ? '🔌' : '🛡️'}
              </div>
              <p style={{ color: '#e74c3c', fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem' }}>
                {errorReason === 'network'
                  ? 'Serveur indisponible'
                  : 'Réseau bloque WhatsApp'}
              </p>
              <p style={{ color: '#aaa', fontSize: '0.8rem', marginBottom: '1.25rem', lineHeight: 1.5 }}>
                {errorReason === 'network'
                  ? 'Le backend Spring Boot est inaccessible. Rafraîchissez la page.'
                  : 'Le pare-feu de votre réseau bloque la connexion vers WhatsApp. Utilisez un hotspot 4G/5G pour générer le QR.'}
              </p>
              <button className="btn btn-primary btn-block" onClick={handleRetry} style={{ marginBottom: '0.5rem' }}>
                🔄 Réessayer
              </button>
            </div>
          )}
        </div>

        {/* ─── Carte Telegram (Prochainement) ─── */}
        <div style={{
          background: 'var(--bg-surface)', padding: '1.5rem', borderRadius: '16px',
          border: '1px solid var(--border)', opacity: 0.7
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem' }}>
            <div style={{ width: '48px', height: '48px', background: '#0088cc', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem' }}>✈️</div>
            <div>
              <h3 style={{ margin: 0 }}>Telegram</h3>
              <p style={{ margin: 0, fontSize: '0.85rem', color: 'var(--text-soft)' }}>Bientôt disponible</p>
            </div>
          </div>
          <button className="btn btn-outline btn-block" disabled>Prochainement</button>
        </div>

        {/* ─── Carte Facebook (Prochainement) ─── */}
        <div style={{
          background: 'var(--bg-surface)', padding: '1.5rem', borderRadius: '16px',
          border: '1px solid var(--border)', opacity: 0.7
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem' }}>
            <div style={{ width: '48px', height: '48px', background: '#0084FF', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem' }}>📘</div>
            <div>
              <h3 style={{ margin: 0 }}>Facebook Messenger</h3>
              <p style={{ margin: 0, fontSize: '0.85rem', color: 'var(--text-soft)' }}>Bientôt disponible</p>
            </div>
          </div>
          <button className="btn btn-outline btn-block" disabled>Prochainement</button>
        </div>

      </div>
    </div>
  );
}
