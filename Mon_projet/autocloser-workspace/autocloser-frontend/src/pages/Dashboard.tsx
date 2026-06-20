import { Routes, Route, Link, useLocation } from 'react-router-dom';
import {
  Activity,
  ArrowUpRight,
  Bot,
  CheckCircle2,
  Clock3,
  Home,
  Link as LinkIcon,
  LogOut,
  MessageCircle,
  Settings,
  ShoppingBag,
  Wallet,
} from 'lucide-react';
import Integrations from './Integrations';

const kpis = [
  {
    label: 'Ventes du jour',
    value: '0 FCFA',
    detail: 'En attente des premières commandes',
    icon: Wallet,
    trend: 'Prêt',
  },
  {
    label: "Messages traités par l'IA",
    value: '0',
    detail: 'Connectez WhatsApp pour démarrer',
    icon: Bot,
    trend: 'IA inactive',
  },
  {
    label: 'Clients engagés',
    value: '0',
    detail: 'Aucun fil client pour le moment',
    icon: MessageCircle,
    trend: 'Suivi',
  },
  {
    label: 'Commandes ouvertes',
    value: '0',
    detail: 'Votre pipeline est vide',
    icon: ShoppingBag,
    trend: 'Stable',
  },
];

const activityItems = [
  {
    title: 'Boutique créée',
    detail: 'Votre espace commerçant est prêt à recevoir vos canaux.',
    time: 'Maintenant',
    state: 'done',
  },
  {
    title: 'WhatsApp à connecter',
    detail: 'Générez un QR code depuis la page Intégrations.',
    time: 'Étape suivante',
    state: 'pending',
  },
  {
    title: 'IA commerciale',
    detail: 'Elle commencera à répondre après la connexion du numéro.',
    time: 'En attente',
    state: 'idle',
  },
];

const hourlyBars = [18, 26, 16, 34, 42, 30, 48, 36, 58, 44, 66, 52];

function DashboardHome() {
  return (
    <div className="dashboard-page">
      <section className="dashboard-hero-panel">
        <div>
          <span className="dashboard-kicker">Espace commerçant</span>
          <h1>Tableau de bord</h1>
          <p>
            Suivez vos ventes automatisées, vos conversations WhatsApp et les prochaines actions à traiter.
          </p>
        </div>
        <Link to="/dashboard/integrations" className="btn btn-primary dashboard-hero-action">
          Connecter WhatsApp <ArrowUpRight size={18} />
        </Link>
      </section>

      <section className="dashboard-kpi-grid" aria-label="Indicateurs principaux">
        {kpis.map((item) => {
          const Icon = item.icon;
          return (
            <article className="dashboard-kpi-card" key={item.label}>
              <div className="dashboard-card-head">
                <div className="dashboard-icon-box">
                  <Icon size={20} />
                </div>
                <span>{item.trend}</span>
              </div>
              <p>{item.label}</p>
              <strong>{item.value}</strong>
              <small>{item.detail}</small>
            </article>
          );
        })}
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-panel dashboard-chart-panel">
          <div className="dashboard-panel-head">
            <div>
              <h2>Flux IA</h2>
              <p>Volume simulé des conversations par heure.</p>
            </div>
            <Activity size={20} />
          </div>
          <div className="dashboard-bars" aria-label="Graphique conversations">
            {hourlyBars.map((height, index) => (
              <span key={index} style={{ height: `${height}%` }} />
            ))}
          </div>
          <div className="dashboard-chart-footer">
            <span>08h</span>
            <span>14h</span>
            <span>20h</span>
          </div>
        </article>

        <article className="dashboard-panel dashboard-setup-panel">
          <div className="dashboard-panel-head">
            <div>
              <h2>Démarrage</h2>
              <p>Les étapes nécessaires pour activer les ventes automatiques.</p>
            </div>
            <span className="dashboard-progress">1/3</span>
          </div>
          <div className="dashboard-progress-track">
            <span />
          </div>
          <div className="dashboard-check-list">
            <div className="done">
              <CheckCircle2 size={18} />
              <span>Compte commerçant créé</span>
            </div>
            <div>
              <Clock3 size={18} />
              <span>Numéro WhatsApp à associer</span>
            </div>
            <div>
              <Clock3 size={18} />
              <span>Agent IA à activer</span>
            </div>
          </div>
        </article>

        <article className="dashboard-panel dashboard-activity-panel">
          <div className="dashboard-panel-head">
            <div>
              <h2>Activité récente</h2>
              <p>Les événements importants de votre boutique.</p>
            </div>
          </div>
          <div className="dashboard-timeline">
            {activityItems.map((item) => (
              <div className={`dashboard-timeline-row ${item.state}`} key={item.title}>
                <span className="dashboard-dot" />
                <div>
                  <strong>{item.title}</strong>
                  <p>{item.detail}</p>
                </div>
                <time>{item.time}</time>
              </div>
            ))}
          </div>
        </article>
      </section>
    </div>
  );
}

function SettingsPage() {
  return (
    <div className="dashboard-page">
      <section className="dashboard-hero-panel compact">
        <div>
          <span className="dashboard-kicker">Configuration</span>
          <h1>Paramètres</h1>
          <p>Centralisez les informations de votre boutique et les préférences de l'agent IA.</p>
        </div>
      </section>

      <section className="dashboard-content-grid settings-grid">
        <article className="dashboard-panel">
          <h2>Boutique</h2>
          <div className="settings-row">
            <span>Nom affiché</span>
            <strong>Ma boutique</strong>
          </div>
          <div className="settings-row">
            <span>Domaine</span>
            <strong>Général</strong>
          </div>
          <div className="settings-row">
            <span>Devise</span>
            <strong>FCFA</strong>
          </div>
        </article>

        <article className="dashboard-panel">
          <h2>Agent IA</h2>
          <div className="settings-row">
            <span>Réponses automatiques</span>
            <strong>En attente</strong>
          </div>
          <div className="settings-row">
            <span>Canal principal</span>
            <strong>WhatsApp</strong>
          </div>
          <Link to="/dashboard/integrations" className="btn btn-primary btn-block">
            Configurer les intégrations
          </Link>
        </article>
      </section>
    </div>
  );
}

export default function Dashboard() {
  const location = useLocation();

  const navItems = [
    { to: '/dashboard', path: '', label: 'Accueil', icon: Home },
    { to: '/dashboard/integrations', path: '/integrations', label: 'Intégrations', icon: LinkIcon },
    { to: '/dashboard/settings', path: '/settings', label: 'Paramètres', icon: Settings },
  ];

  const isActive = (path: string) =>
    location.pathname === `/dashboard${path}` || (path === '' && location.pathname === '/dashboard');

  return (
    <div className="dashboard-shell">
      <aside className="dashboard-sidebar" aria-label="Navigation principale">
        <div className="dashboard-brand">
          <div>Auto<span>Closer AI</span></div>
          <small>Ventes automatisées</small>
        </div>

        <nav className="dashboard-nav">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.to}
                to={item.to}
                className={`dashboard-nav-link ${isActive(item.path) ? 'active' : ''}`}
              >
                <Icon size={18} />
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>

        <div className="dashboard-sidebar-card">
          <span>Statut</span>
          <strong>À configurer</strong>
          <p>Connectez WhatsApp pour lancer votre agent IA.</p>
        </div>

        <Link to="/login" className="dashboard-logout">
          <LogOut size={18} /> Déconnexion
        </Link>
      </aside>

      <main className="dashboard-main">
        <header className="dashboard-mobile-header">
          <div className="dashboard-brand">
            <div>Auto<span>Closer AI</span></div>
          </div>
          <Link to="/dashboard/integrations" className="btn btn-outline">
            WhatsApp
          </Link>
        </header>

        <Routes>
          <Route path="/" element={<DashboardHome />} />
          <Route path="/integrations" element={<Integrations />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Routes>
      </main>
    </div>
  );
}
