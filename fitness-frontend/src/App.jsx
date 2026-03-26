import { useContext, useEffect } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router";
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";
import "./app.css";
 
const Navbar = ({ user, onLogout }) => {
  const initials = user
    ? `${user.given_name?.[0] ?? ""}${user.family_name?.[0] ?? ""}`.toUpperCase()
    : "?";
  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <div className="navbar-brand">
          <span className="brand-icon">⚡</span>
          <span className="brand-name">FITPULSE</span>
        </div>
        <div className="navbar-right">
          <div className="user-chip">
            <div className="user-avatar">{initials}</div>
            <span className="user-name">{user?.given_name ?? user?.email ?? ""}</span>
          </div>
          <button className="btn-logout" onClick={onLogout}>Logout</button>
        </div>
      </div>
    </nav>
  );
};
 
const FEATURES = [
  { icon: "📊", title: "Smart Analytics", desc: "Visual charts for every workout" },
  { icon: "🤖", title: "AI Coach", desc: "Personalised recommendations" },
  { icon: "🔥", title: "Calorie Tracking", desc: "Burn metrics & intensity scores" },
  { icon: "🔄", title: "Strava Sync", desc: "Import from any fitness source" },
];
 
const LoginPage = ({ onLogin }) => {
  const KC_BASE = "http://localhost:8181/realms/fitness-app/protocol/openid-connect";
  const REDIRECT = encodeURIComponent("http://localhost:5173");
  const CLIENT = "oauth2-pkce-client";
  const signupUrl = `http://localhost:8181/realms/fitness-app/protocol/openid-connect/registrations?client_id=${CLIENT}&redirect_uri=${REDIRECT}&response_type=code&scope=openid+profile+email`;
 
  return (
    <div className="login-page">
      <div className="login-bg-grid" />
      <div className="login-bg-orb orb-1" />
      <div className="login-bg-orb orb-2" />
 
      <div className="login-layout">
        <div className="login-left">
          <div className="login-logo-row">
            <span className="brand-icon" style={{ fontSize: 28 }}>⚡</span>
            <span className="brand-name" style={{ fontSize: 32 }}>FITPULSE</span>
          </div>
          <h1 className="login-headline">
            Train Smarter.<br />
            <em className="headline-accent">Not Harder.</em>
          </h1>
          <p className="login-tagline">
            AI-powered fitness tracking that analyses every rep, every mile, every beat.
          </p>
          <div className="feature-grid">
            {FEATURES.map((f) => (
              <div className="feature-card" key={f.title}>
                <span className="feature-icon">{f.icon}</span>
                <div>
                  <div className="feature-title">{f.title}</div>
                  <div className="feature-desc">{f.desc}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
 
        <div className="login-right">
          <div className="auth-card">
            <div className="auth-badge">Free Forever · No credit card</div>
            <h2 className="auth-title">Welcome Back</h2>
            <p className="auth-sub">Sign in or create your account to continue</p>
            
            <div className="auth-split">
              <button className="split-btn split-btn--login" onClick={onLogin}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                  <polyline points="10 17 15 12 10 7"/>
                  <line x1="15" y1="12" x2="3" y2="12"/>
                </svg>
                Log In
              </button>
              <a className="split-btn split-btn--signup" href={signupUrl}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <line x1="19" y1="8" x2="19" y2="14"/>
                  <line x1="22" y1="11" x2="16" y2="11"/>
                </svg>
                  Sign Up
                </a>
              </div>
            
 
            <div className="auth-divider"><span>or</span></div>
 
            <button className="google-btn" onClick={onLogin}>
              <svg width="18" height="18" viewBox="0 0 24 24"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/></svg>
              Continue with Google
            </button>
 
            <p className="auth-terms">
              By continuing you agree to our <span className="link-accent">Terms</span> &amp; <span className="link-accent">Privacy</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
 
const ActivitiesPage = () => (
  <div className="page-content">
    <div className="page-header">
      <div>
        <h2 className="page-title">Dashboard</h2>
        <p className="page-subtitle">Your fitness journey at a glance</p>
      </div>
    </div>
    <ActivityForm onActivityAdded={() => window.location.reload()} />
    <ActivityList />
  </div>
);
 
function App() {
  const { token, tokenData, logIn, logOut } = useContext(AuthContext);
  const dispatch = useDispatch();

  useEffect(() => {
    if (token && tokenData) {
      // Save directly to localStorage immediately
      localStorage.setItem("token", token);
      localStorage.setItem("userId", tokenData.sub);
      localStorage.setItem("user", JSON.stringify(tokenData));

      // Then dispatch to Redux
      dispatch(setCredentials({ token, user: tokenData }));

      console.log("✅ Token saved:", token.substring(0, 20) + "...");
      console.log("✅ UserId saved:", tokenData.sub);
    }
  }, [token, tokenData]);
 
  return (
    <Router>
      {!token ? (
        <LoginPage onLogin={logIn} />
      ) : (
        <div className="app-shell">
          <Navbar user={tokenData} onLogout={logOut} />
          <main className="main-content">
            <Routes>
              <Route path="/activities" element={<ActivitiesPage />} />
              <Route path="/activities/:id" element={<ActivityDetail />} />
              <Route path="/" element={<Navigate to="/activities" replace />} />
            </Routes>
          </main>
        </div>
      )}
    </Router>
  );
}
 
export default App;