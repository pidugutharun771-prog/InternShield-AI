import React, { useEffect, useState } from "react";
import { createRoot } from "react-dom/client";

import {
  ShieldCheck,
  AlertTriangle,
  History,
  Search,
  Activity,
  Mail,
  ShieldAlert,
  Database,
  Server,
  RefreshCw,
  Link,
  BarChart3,
  ExternalLink,
  Building2,
} from "lucide-react";

import "./styles.css";

const API = "https://internshield-ai-0pzs.onrender.com/api";
const HEALTH_API = "https://internshield-ai-0pzs.onrender.com/actuator/health";
const CompanyVerificationPage = ({companyName,
  setCompanyName,
  companyWebsite,
  setCompanyWebsite,
  companyEmail,
  setCompanyEmail,
  companyResult,
  companyLoading,
  companyError,
  verifyCompany,
  systemStatus}) => {
  const status = companyResult?.verificationStatus || "READY";

  return (
    <>
      <header>
        <div>
          <p className="eyebrow">COMPANY SECURITY</p>

          <h1>Company Verification</h1>

          <p className="sub">
            Check whether a recruiter, company website and email domain
            appear consistent.
          </p>
        </div>

        <div
          className={`status ${
            systemStatus === "Backend offline" ? "offline" : ""
          }`}
        >
          <span></span>

          {systemStatus === "Backend offline"
            ? "API Offline"
            : "API Online"}
        </div>
      </header>

      <section className="stats">
        <div>
          <small>VERIFICATION ENGINE</small>
          <strong>ACTIVE</strong>
        </div>

        <div>
          <small>DOMAIN CHECK</small>
          <strong>ON</strong>
        </div>

        <div>
          <small>API</small>
          <strong>RENDER</strong>
        </div>
      </section>

      <div className="grid">

        {/* INPUT PANEL */}

        <section className="panel">
          <h2>Verify a company</h2>

          <form onSubmit={verifyCompany}>

            <label>
              Company name
            </label>

            <div className="input">
              <Building2 size={17} />

              <input
                type="text"
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
                placeholder="Google"
              />
            </div>

            <label>
              Company website
            </label>

            <div className="input">
              <Link size={17} />

              <input
                type="text"
                value={companyWebsite}
                onChange={(e) => setCompanyWebsite(e.target.value)}
                placeholder="https://google.com"
              />
            </div>

            <label>
              Recruiter email
            </label>

            <div className="input">
              <Mail size={17} />

              <input
                type="email"
                value={companyEmail}
                onChange={(e) => setCompanyEmail(e.target.value)}
                placeholder="hr@company.com"
              />
            </div>

            {companyError && (
              <div className="error-message">
                ⚠️ {companyError}
              </div>
            )}

            <button
              type="submit"
              disabled={companyLoading}
            >
              {companyLoading
                ? "Verifying..."
                : "Verify Company"}
            </button>

          </form>
        </section>

        {/* RESULT PANEL */}

        <section className="panel result">

          <h2>Verification Result</h2>

          {!companyResult ? (

            <div className="empty">

              <Building2 size={48} />

              <p>Company verification will appear here</p>

              <small>
                InternShield compares the company name, website domain
                and recruiter email to identify inconsistencies.
              </small>

            </div>

          ) : (

            <>

              <div
                className={`risk ${
                  status === "VERIFIED"
                    ? "low"
                    : status === "NEEDS_VERIFICATION"
                    ? "medium"
                    : "critical"
                }`}
              >

                <div>
                  <small>VERIFICATION STATUS</small>

                  <strong>
                    {status.replaceAll("_", " ")}
                  </strong>
                </div>

                <div className="score">
                  {companyResult.score}
                  <span>/100</span>
                </div>

              </div>

              <div className="company-summary">

                <div>
                  <small>COMPANY</small>
                  <strong>
                    {companyResult.companyName}
                  </strong>
                </div>

                <div>
                  <small>WEBSITE DOMAIN</small>
                  <strong>
                    {companyResult.websiteDomain || "Unknown"}
                  </strong>
                </div>

                <div>
                  <small>RECRUITER EMAIL</small>
                  <strong>
                    {companyResult.email}
                  </strong>
                </div>

              </div>

              <h3>Verification findings</h3>

              {companyResult.findings?.length > 0 ? (

                <ul>

                  {companyResult.findings.map(
                    (finding, index) => (

                      <li key={index}>

                        <AlertTriangle size={16} />

                        <span>{finding}</span>

                      </li>

                    )
                  )}

                </ul>

              ) : (

                <p className="muted">
                  No major company verification findings.
                </p>

              )}

              <h3>What you should do</h3>

              <ul className="safe">

                {companyResult.recommendations?.map(
                  (recommendation, index) => (

                    <li key={index}>

                      <ShieldCheck size={16} />

                      <span>{recommendation}</span>

                    </li>

                  )
                )}

              </ul>

            </>

          )}

        </section>

      </div>

      <section className="panel">

        <h2>Verification checks performed</h2>

        <div className="url-check-grid">

          <div>🔒 HTTPS website</div>

          <div>📧 Recruiter email domain</div>

          <div>🏢 Company name</div>

          <div>🌐 Website domain</div>

          <div>🔗 Email ↔ website match</div>

          <div>⚠️ Public email provider</div>

          <div>🏷️ Domain structure</div>

          <div>🛡️ Verification score</div>

        </div>

      </section>
    </>
  );
};

function App() {
  const [activePage, setActivePage] = useState("analyzer");

  // =========================================================
  // INTERNSHIP ANALYZER STATE
  // =========================================================

  const [text, setText] = useState("");
  const [email, setEmail] = useState("");
  const [result, setResult] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // =========================================================
  // URL ANALYZER STATE
  // =========================================================

  const [url, setUrl] = useState("");
  const [urlEmail, setUrlEmail] = useState("");
  const [urlResult, setUrlResult] = useState(null);
  const [urlLoading, setUrlLoading] = useState(false);
  const [urlError, setUrlError] = useState("");
  // =========================================================
// COMPANY VERIFICATION STATE
// =========================================================

const [companyName, setCompanyName] = useState("");
const [companyWebsite, setCompanyWebsite] = useState("");
const [companyEmail, setCompanyEmail] = useState("");

const [companyResult, setCompanyResult] = useState(null);
const [companyLoading, setCompanyLoading] = useState(false);
const [companyError, setCompanyError] = useState("");

const [analytics, setAnalytics] = useState(null);
const [analyticsLoading, setAnalyticsLoading] = useState(false);
const [analyticsError, setAnalyticsError] = useState("");
const loadAnalytics = async () => {
  try {
    setAnalyticsLoading(true);
    setAnalyticsError("");

    const response = await fetch(`${API}/analytics`);

    if (!response.ok) {
      throw new Error("Failed to load analytics");
    }

    const data = await response.json();
    setAnalytics(data);

  } catch (error) {
    setAnalyticsError(error.message);
  } finally {
    setAnalyticsLoading(false);
  }
};

  // =========================================================
  // SYSTEM STATUS
  // =========================================================

  const [systemStatus, setSystemStatus] = useState("Checking...");
  const [checkingStatus, setCheckingStatus] = useState(false);

  // =========================================================
  // LOAD ANALYSIS HISTORY
  // =========================================================

  const loadHistory = async () => {
    try {
      const response = await fetch(`${API}/analysis/recent`);

      if (!response.ok) {
        throw new Error("Failed to load history");
      }

      const data = await response.json();
      setHistory(data);
    } catch (err) {
      console.error("History error:", err);
    }
  };

  // =========================================================
  // CHECK BACKEND STATUS
  // =========================================================

  const checkSystemStatus = async () => {
    setCheckingStatus(true);

    try {
      const response = await fetch(HEALTH_API);

      if (!response.ok) {
        throw new Error("Backend unavailable");
      }

      const data = await response.json();

      if (data.status === "UP") {
        setSystemStatus("All systems operational");
      } else {
        setSystemStatus("Backend needs attention");
      }
    } catch (err) {
      console.error("System status error:", err);
      setSystemStatus("Backend offline");
    } finally {
      setCheckingStatus(false);
    }
  };

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  useEffect(() => {
    loadHistory();
    checkSystemStatus();
  }, []);

  // =========================================================
  // INTERNSHIP OFFER ANALYZER
  // =========================================================

  const analyze = async (e) => {
    e.preventDefault();

    if (!text.trim()) {
      setError("Please enter an internship offer or message.");
      return;
    }

    setLoading(true);
    setError("");
    setResult(null);

    try {
      const response = await fetch(`${API}/analysis`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          content: text,
          email: email,
        }),
      });

      if (!response.ok) {
        throw new Error("Analysis request failed");
      }

      const data = await response.json();

      setResult(data);

      await loadHistory();
    } catch (err) {
      console.error("Analysis error:", err);

      setError(
        "Unable to connect to InternShield AI. Make sure the backend is running."
      );
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // URL SAFETY ANALYZER
  // =========================================================

  const analyzeUrl = async (e) => {
    e.preventDefault();

    if (!url.trim()) {
      setUrlError("Please enter a URL.");
      return;
    }

    setUrlLoading(true);
    setUrlError("");
    setUrlResult(null);

    try {
      const response = await fetch(`${API}/url/analyze`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          url: url.trim(),
          email: urlEmail.trim(),
        }),
      });

      if (!response.ok) {
        throw new Error("URL analysis request failed");
      }

      const data = await response.json();

      setUrlResult(data);
    } catch (err) {
      console.error("URL analysis error:", err);

      setUrlError(
        "Unable to analyze the URL. Make sure the backend is running."
      );
    } finally {
      setUrlLoading(false);
    }
  };
  // =========================================================
// COMPANY VERIFICATION
// =========================================================

const verifyCompany = async (e) => {
  e.preventDefault();

  if (!companyName.trim()) {
    setCompanyError("Please enter the company name.");
    return;
  }

  if (!companyWebsite.trim()) {
    setCompanyError("Please enter the company website.");
    return;
  }

  if (!companyEmail.trim()) {
    setCompanyError("Please enter the recruiter email.");
    return;
  }

  setCompanyLoading(true);
  setCompanyError("");
  setCompanyResult(null);

  try {
    const response = await fetch(`${API}/company/verify`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        companyName: companyName.trim(),
        website: companyWebsite.trim(),
        email: companyEmail.trim(),
      }),
    });

    if (!response.ok) {
      throw new Error("Company verification request failed");
    }

    const data = await response.json();

    setCompanyResult(data);
  } catch (err) {
    console.error("Company verification error:", err);

    setCompanyError(
      "Unable to verify the company. Make sure the backend is running."
    );
  } finally {
    setCompanyLoading(false);
  }
};

  // =========================================================
  // STATISTICS
  // =========================================================

  const counts = {
    LOW: history.filter((x) => x.riskLevel === "LOW").length,
    MEDIUM: history.filter((x) => x.riskLevel === "MEDIUM").length,
    HIGH: history.filter((x) => x.riskLevel === "HIGH").length,
    CRITICAL: history.filter((x) => x.riskLevel === "CRITICAL").length,
  };

  // =========================================================
  // SIDEBAR
  // =========================================================

  const Sidebar = () => (
    <aside>
      <div className="brand">
        <div className="logo">
          <ShieldCheck />
        </div>

        <div>
          <b>InternShield</b>
          <small>AI Safety Platform</small>
        </div>
      </div>

      <nav>
        <span
          className={activePage === "analyzer" ? "active" : ""}
          onClick={() => setActivePage("analyzer")}
        >
          <Search />
          Analyzer
        </span>

        <span
          className={activePage === "url" ? "active" : ""}
          onClick={() => setActivePage("url")}
        >
          <Link />
          URL Safety
        </span>
                <span
          className={activePage === "company" ? "active" : ""}
          onClick={() => setActivePage("company")}
        >
          <Building2 />
          Company Verification
        </span>

        <span
          className={activePage === "analytics" ? "active" : ""}
          onClick={() => {
            setActivePage("analytics");
            loadAnalytics();
          }}
        >
          <BarChart3 />
          Analytics
        </span>

        <span
          className={activePage === "history" ? "active" : ""}
          onClick={() => setActivePage("history")}
        >
          <History />
          History
        </span>

        <span
          className={activePage === "status" ? "active" : ""}
          onClick={() => {
            setActivePage("status");
            checkSystemStatus();
          }}
        >
          <Activity />
          System status
        </span>
      </nav>

      <div className="side-note">
        <ShieldAlert />

        <p>
          Protect yourself from fake internships, fee scams and credential
          theft.
        </p>
      </div>
    </aside>
  );

  // =========================================================
  // ANALYZER PAGE
  // =========================================================

  const AnalyzerPage = () => {
    const riskLevel = result?.riskLevel || "READY";

    return (
      <>
        <header>
          <div>
            <p className="eyebrow">INTERNSHIP SECURITY</p>

            <h1>Offer Risk Analyzer</h1>

            <p className="sub">
              Paste an internship message and get an explainable risk
              assessment.
            </p>
          </div>

          <div
            className={`status ${
              systemStatus === "Backend offline" ? "offline" : ""
            }`}
          >
            <span></span>

            {systemStatus === "Backend offline"
              ? "API Offline"
              : "API Online"}
          </div>
        </header>

        <section className="stats">
          <div>
            <small>ANALYSES</small>
            <strong>{history.length}</strong>
          </div>

          <div>
            <small>LOW RISK</small>
            <strong>{counts.LOW}</strong>
          </div>

          <div>
            <small>HIGH + CRITICAL</small>
            <strong>{counts.HIGH + counts.CRITICAL}</strong>
          </div>
        </section>

        <div className="grid">
          <section className="panel">
            <h2>Analyze an offer</h2>

            <form onSubmit={analyze}>
              <label>
                Recruiter email <span>optional</span>
              </label>

              <div className="input">
                <Mail size={17} />

                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="hr@company.com"
                />
              </div>

              <label>Internship message</label>

              <textarea
                value={text}
                onChange={(e) => setText(e.target.value)}
                placeholder="Paste the complete internship offer, WhatsApp message, email or job description here..."
              />

              {error && (
                <div className="error-message">⚠️ {error}</div>
              )}

              <button type="submit" disabled={loading}>
                {loading
                  ? "Analyzing..."
                  : "Analyze with InternShield AI"}
              </button>
            </form>
          </section>

          <section className="panel result">
            <h2>Assessment</h2>

            {!result ? (
              <div className="empty">
                <ShieldCheck size={48} />

                <p>Your result will appear here</p>

                <small>
                  We check payment requests, urgency, suspicious channels,
                  public email domains and sensitive-data requests.
                </small>
              </div>
            ) : (
              <>
                <div className={`risk ${riskLevel.toLowerCase()}`}>
                  <div>
                    <small>RISK LEVEL</small>
                    <strong>{riskLevel}</strong>
                  </div>

                  <div className="score">
                    {result.score}
                    <span>/100</span>
                  </div>
                </div>

                <h3>Why this score?</h3>

                {result.indicators &&
                result.indicators.length > 0 ? (
                  <ul>
                    {result.indicators.map((indicator, index) => (
                      <li key={index}>
                        <AlertTriangle size={16} />
                        <span>{indicator}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="muted">
                    No major configured scam signals were detected; this does
                    not prove the offer is legitimate.
                  </p>
                )}

                <h3>What you should do</h3>

                <ul className="safe">
                  {result.recommendations?.map(
                    (recommendation, index) => (
                      <li key={index}>
                        <ShieldCheck size={16} />
                        <span>{recommendation}</span>
                      </li>
                    )
                  )}
                </ul>
              </>
            )}
          </section>
        </div>

        <section className="panel">
          <div className="section-heading">
            <h2>Recent analyses</h2>

            <button
              className="small-button"
              type="button"
              onClick={() => setActivePage("history")}
            >
              View all
            </button>
          </div>

          <div className="history">
            {history.length === 0 ? (
              <p className="muted">No analyses yet.</p>
            ) : (
              history.slice(0, 5).map((item) => (
                <div className="row" key={item.id}>
                  <span
                    className={`dot ${item.riskLevel.toLowerCase()}`}
                  ></span>

                  <b>{item.riskLevel}</b>

                  <span>{item.score}/100</span>

                  <small>
                    {new Date(item.createdAt).toLocaleString()}
                  </small>
                </div>
              ))
            )}
          </div>
        </section>
      </>
    );
  };

  // =========================================================
  // URL SAFETY PAGE
  // =========================================================

  const UrlSafetyPage = () => {
    const riskLevel = urlResult?.riskLevel || "READY";

    return (
      <>
        <header>
          <div>
            <p className="eyebrow">LINK SECURITY</p>

            <h1>URL Safety Analyzer</h1>

            <p className="sub">
              Check an internship website or recruitment link for suspicious
              URL patterns.
            </p>
          </div>

          <div
            className={`status ${
              systemStatus === "Backend offline" ? "offline" : ""
            }`}
          >
            <span></span>

            {systemStatus === "Backend offline"
              ? "API Offline"
              : "API Online"}
          </div>
        </header>

        <section className="stats">
          <div>
            <small>URL ENGINE</small>
            <strong>ACTIVE</strong>
          </div>

          <div>
            <small>CHECKS</small>
            <strong>10+</strong>
          </div>

          <div>
            <small>API</small>
            <strong>8081</strong>
          </div>
        </section>

        <div className="grid">
          <section className="panel">
            <h2>Analyze a URL</h2>

            <form onSubmit={analyzeUrl}>
              <label>
                Internship / recruitment URL
              </label>

              <div className="input">
                <Link size={17} />

                <input
                  type="text"
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  placeholder="https://company.com/internship"
                />
              </div>

              <label>
                Recruiter email <span>optional</span>
              </label>

              <div className="input">
                <Mail size={17} />

                <input
                  type="email"
                  value={urlEmail}
                  onChange={(e) => setUrlEmail(e.target.value)}
                  placeholder="hr@company.com"
                />
              </div>

              {urlError && (
                <div className="error-message">
                  ⚠️ {urlError}
                </div>
              )}

              <button type="submit" disabled={urlLoading}>
                {urlLoading
                  ? "Checking URL..."
                  : "Analyze URL"}
              </button>
            </form>
          </section>

          <section className="panel result">
            <h2>URL Assessment</h2>

            {!urlResult ? (
              <div className="empty">
                <Link size={48} />

                <p>URL analysis will appear here</p>

                <small>
                  InternShield checks HTTPS, URL shorteners, IP addresses,
                  suspicious keywords, domains, ports and other URL signals.
                </small>
              </div>
            ) : (
              <>
                <div className={`risk ${riskLevel.toLowerCase()}`}>
                  <div>
                    <small>RISK LEVEL</small>
                    <strong>{riskLevel}</strong>
                  </div>

                  <div className="score">
                    {urlResult.score}
                    <span>/100</span>
                  </div>
                </div>

                <div className="url-display">
                  <ExternalLink size={15} />
                  <span>{urlResult.url}</span>
                </div>

                <div className="domain-display">
                  <small>DOMAIN</small>
                  <strong>{urlResult.domain || "Unknown"}</strong>
                </div>

                <h3>Why this score?</h3>

                {urlResult.indicators &&
                urlResult.indicators.length > 0 ? (
                  <ul>
                    {urlResult.indicators.map((indicator, index) => (
                      <li key={index}>
                        <AlertTriangle size={16} />
                        <span>{indicator}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="muted">
                    No major URL risk signals were detected.
                  </p>
                )}

                <h3>What you should do</h3>

                <ul className="safe">
                  {urlResult.recommendations?.map(
                    (recommendation, index) => (
                      <li key={index}>
                        <ShieldCheck size={16} />
                        <span>{recommendation}</span>
                      </li>
                    )
                  )}
                </ul>
              </>
            )}
          </section>
        </div>

        <section className="panel">
          <h2>URL checks performed</h2>

          <div className="url-check-grid">
            <div>🔒 HTTPS security</div>
            <div>🔗 URL shorteners</div>
            <div>🌐 IP-based URLs</div>
            <div>⚠️ Suspicious keywords</div>
            <div>🏷️ Domain structure</div>
            <div>🚪 Non-standard ports</div>
            <div>📧 Email-domain comparison</div>
            <div>📦 Suspicious file types</div>
          </div>
        </section>
      </>
    );
  };
  // =========================================================
// COMPANY VERIFICATION PAGE
// =========================================================


  // =========================================================
  // HISTORY PAGE
  // =========================================================

  const HistoryPage = () => (
    <>
      <header>
        <div>
          <p className="eyebrow">INTERNSHIP SECURITY</p>

          <h1>Analysis History</h1>

          <p className="sub">
            Review previous internship risk assessments.
          </p>
        </div>

        <div className="status">
          <span></span>
          API Online
        </div>
      </header>

      <section className="stats">
        <div>
          <small>TOTAL ANALYSES</small>
          <strong>{history.length}</strong>
        </div>

        <div>
          <small>MEDIUM + HIGH</small>
          <strong>{counts.MEDIUM + counts.HIGH}</strong>
        </div>

        <div>
          <small>CRITICAL</small>
          <strong>{counts.CRITICAL}</strong>
        </div>
      </section>

      <section className="panel">
        <div className="section-heading">
          <h2>All recent analyses</h2>

          <button
            className="small-button"
            type="button"
            onClick={loadHistory}
          >
            Refresh
          </button>
        </div>

        {history.length === 0 ? (
          <div className="empty history-empty">
            <History size={45} />

            <p>No analyses found</p>

            <small>
              Your completed internship risk assessments will appear here.
            </small>
          </div>
        ) : (
          <div className="history full-history">
            {history.map((item) => (
              <div className="history-card" key={item.id}>
                <div className="history-main">
                  <span
                    className={`dot ${item.riskLevel.toLowerCase()}`}
                  ></span>

                  <div>
                    <strong>{item.riskLevel}</strong>

                    <p>
                      Risk score: {item.score}/100
                    </p>
                  </div>
                </div>

                <div className="history-date">
                  <small>
                    {new Date(item.createdAt).toLocaleString()}
                  </small>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </>
  );
    // =========================================================
  // ANALYTICS DASHBOARD PAGE
  // =========================================================

  const AnalyticsPage = () => {
    const total = analytics?.totalScans || 0;
    const low = analytics?.lowRisk || 0;
    const medium = analytics?.mediumRisk || 0;
    const high = analytics?.highRisk || 0;
    const critical = analytics?.criticalRisk || 0;

    const getPercentage = (value) => {
      if (total === 0) return 0;
      return Math.round((value / total) * 100);
    };

    return (
      <>
        <header>
          <div>
            <p className="eyebrow">SECURITY ANALYTICS</p>

            <h1>Analytics Dashboard</h1>

            <p className="sub">
              Overview of internship safety scans performed by InternShield AI.
            </p>
          </div>

          <button
            className="primary-btn"
            onClick={loadAnalytics}
            disabled={analyticsLoading}
          >
            {analyticsLoading ? "Refreshing..." : "Refresh Data"}
          </button>
        </header>

        {analyticsError && (
          <div className="error-box">
            {analyticsError}
          </div>
        )}

        {analyticsLoading && !analytics && (
          <section className="card">
            <p>Loading analytics...</p>
          </section>
        )}

        {analytics && (
          <>
            {/* SUMMARY CARDS */}
            <section className="stats-grid">

              <div className="stat-card">
                <span className="stat-label">TOTAL SCANS</span>
                <strong>{total}</strong>
                <small>All internship analyses</small>
              </div>

              <div className="stat-card">
                <span className="stat-label">LOW RISK</span>
                <strong>{low}</strong>
                <small>{getPercentage(low)}% of scans</small>
              </div>

              <div className="stat-card">
                <span className="stat-label">MEDIUM RISK</span>
                <strong>{medium}</strong>
                <small>{getPercentage(medium)}% of scans</small>
              </div>

              <div className="stat-card">
                <span className="stat-label">HIGH RISK</span>
                <strong>{high}</strong>
                <small>{getPercentage(high)}% of scans</small>
              </div>

              <div className="stat-card">
                <span className="stat-label">CRITICAL</span>
                <strong>{critical}</strong>
                <small>{getPercentage(critical)}% of scans</small>
              </div>

            </section>
                        {/* RISK OVERVIEW */}
            <section className="analytics-overview">

              <div className="card overview-card">
                <div className="section-heading">
                  <div>
                    <p className="eyebrow">RISK OVERVIEW</p>
                    <h2>Overall Security Status</h2>
                  </div>
                </div>

                <div className="risk-overview-content">

                  <div
                    className="risk-donut"
                    style={{
                      background: `conic-gradient(
                        #22c55e 0deg ${getPercentage(low) * 3.6}deg,
                        #f59e0b ${getPercentage(low) * 3.6}deg ${(getPercentage(low) + getPercentage(medium)) * 3.6}deg,
                        #f97316 ${(getPercentage(low) + getPercentage(medium)) * 3.6}deg ${(getPercentage(low) + getPercentage(medium) + getPercentage(high)) * 3.6}deg,
                        #ef4444 ${(getPercentage(low) + getPercentage(medium) + getPercentage(high)) * 3.6}deg 360deg
                      )`
                    }}
                  >
                    <div className="donut-center">
                      <strong>{total}</strong>
                      <span>Scans</span>
                    </div>
                  </div>

                  <div className="risk-legend">

                    <div className="legend-item">
                      <span className="legend-dot low-dot"></span>
                      <div>
                        <strong>Low Risk</strong>
                        <small>{low} scans · {getPercentage(low)}%</small>
                      </div>
                    </div>

                    <div className="legend-item">
                      <span className="legend-dot medium-dot"></span>
                      <div>
                        <strong>Medium Risk</strong>
                        <small>{medium} scans · {getPercentage(medium)}%</small>
                      </div>
                    </div>

                    <div className="legend-item">
                      <span className="legend-dot high-dot"></span>
                      <div>
                        <strong>High Risk</strong>
                        <small>{high} scans · {getPercentage(high)}%</small>
                      </div>
                    </div>

                    <div className="legend-item">
                      <span className="legend-dot critical-dot"></span>
                      <div>
                        <strong>Critical</strong>
                        <small>{critical} scans · {getPercentage(critical)}%</small>
                      </div>
                    </div>

                  </div>

                </div>
              </div>

              <div className="card security-summary">
                <p className="eyebrow">SECURITY SUMMARY</p>

                <h2>Threat Exposure</h2>

                <p className="summary-text">
                  InternShield AI has analyzed <strong>{total}</strong> internship
                  offers so far.
                </p>

                <div className="summary-stat">
                  <span>Potentially risky scans</span>
                  <strong>{high + critical}</strong>
                </div>

                <div className="summary-stat">
                  <span>Risk exposure</span>
                  <strong>
                    {getPercentage(high + critical)}%
                  </strong>
                </div>

                <div className="summary-stat">
                  <span>Safe scans</span>
                  <strong>{low}</strong>
                </div>

              </div>

            </section>
            {/* RISK DISTRIBUTION */}
            <section className="card">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">RISK DISTRIBUTION</p>
                  <h2>Internship Scan Results</h2>
                </div>
              </div>

              <div className="risk-bars">

                <div className="risk-row">
                  <div className="risk-row-header">
                    <span>Low Risk</span>
                    <strong>{low}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill low"
                      style={{ width: `${getPercentage(low)}%` }}
                    />
                  </div>
                </div>

                <div className="risk-row">
                  <div className="risk-row-header">
                    <span>Medium Risk</span>
                    <strong>{medium}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill medium"
                      style={{ width: `${getPercentage(medium)}%` }}
                    />
                  </div>
                </div>

                <div className="risk-row">
                  <div className="risk-row-header">
                    <span>High Risk</span>
                    <strong>{high}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill high"
                      style={{ width: `${getPercentage(high)}%` }}
                    />
                  </div>
                </div>

                <div className="risk-row">
                  <div className="risk-row-header">
                    <span>Critical</span>
                    <strong>{critical}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill critical"
                      style={{ width: `${getPercentage(critical)}%` }}
                    />
                  </div>
                </div>

              </div>
            </section>
          </>
        )}
      </>
    );
  };


  // =========================================================
  // SYSTEM STATUS PAGE
  // =========================================================

  const SystemStatusPage = () => (
    <>
      <header>
        <div>
          <p className="eyebrow">SYSTEM MONITORING</p>

          <h1>System Status</h1>

          <p className="sub">
            Monitor the services powering InternShield AI.
          </p>
        </div>

        <button
          className="refresh-button"
          type="button"
          onClick={checkSystemStatus}
          disabled={checkingStatus}
        >
          <RefreshCw
            size={15}
            className={checkingStatus ? "spin" : ""}
          />

          {checkingStatus ? "Checking..." : "Refresh"}
        </button>
      </header>

      <section className="status-grid">
        <div className="panel system-card">
          <div className="system-icon">
            <Server />
          </div>

          <div className="system-info">
            <small>BACKEND API</small>

            <h2>{systemStatus}</h2>

            <p>Spring Boot REST API</p>
          </div>

          <span
            className={`system-badge ${
              systemStatus === "Backend offline"
                ? "badge-offline"
                : ""
            }`}
          >
            ●{" "}
            {systemStatus === "Backend offline"
              ? "Offline"
              : "Online"}
          </span>
        </div>

        <div className="panel system-card">
          <div className="system-icon">
            <Database />
          </div>

          <div className="system-info">
            <small>DATABASE</small>

            <h2>PostgreSQL</h2>

            <p>Analysis history storage</p>
          </div>

          <span
            className={`system-badge ${
              systemStatus === "Backend offline"
                ? "badge-offline"
                : ""
            }`}
          >
            ●{" "}
            {systemStatus === "Backend offline"
              ? "Unavailable"
              : "Connected"}
          </span>
        </div>

        <div className="panel system-card">
          <div className="system-icon">
            <ShieldCheck />
          </div>

          <div className="system-info">
            <small>RISK ENGINE</small>

            <h2>Operational</h2>

            <p>Internship risk analysis engine</p>
          </div>

          <span className="system-badge">
            ● Active
          </span>
        </div>

        <div className="panel system-card">
          <div className="system-icon">
            <Activity />
          </div>

          <div className="system-info">
            <small>FRONTEND</small>

            <h2>React + Vite</h2>

            <p>InternShield user interface</p>
          </div>

          <span className="system-badge">
            ● Running
          </span>
        </div>
      </section>

      <section className="panel">
        <h2>InternShield AI Architecture</h2>

        <div className="architecture">
          <div>React Frontend</div>
          <span>→</span>
          <div>Spring Boot API</div>
          <span>→</span>
          <div>Risk Engine</div>
          <span>→</span>
          <div>PostgreSQL</div>
        </div>
      </section>

      <section className="panel">
        <h2>System information</h2>

        <div className="system-info-grid">
          <div>
            <small>FRONTEND</small>
            <strong>React + Vite</strong>
          </div>

          <div>
            <small>BACKEND</small>
            <strong>Spring Boot</strong>
          </div>

          <div>
            <small>DATABASE</small>
            <strong>PostgreSQL</strong>
          </div>

          <div>
            <small>API PORT</small>
            <strong>8081</strong>
          </div>
        </div>
      </section>
    </>
  );

  // =========================================================
  // MAIN RETURN
  // =========================================================

  return (
    <div className="app">
      <Sidebar />

      <main>
        {activePage === "analyzer" && <AnalyzerPage />}

        {activePage === "url" && <UrlSafetyPage />} 

        {activePage === "company" && (
  <CompanyVerificationPage
    companyName={companyName}
    setCompanyName={setCompanyName}
    companyWebsite={companyWebsite}
    setCompanyWebsite={setCompanyWebsite}
    companyEmail={companyEmail}
    setCompanyEmail={setCompanyEmail}
    companyResult={companyResult}
    companyLoading={companyLoading}
    companyError={companyError}
    verifyCompany={verifyCompany}
    systemStatus={systemStatus}
  />
)}

        {activePage === "analytics" && <AnalyticsPage />}

        {activePage === "history" && <HistoryPage />}

        {activePage === "status" && <SystemStatusPage />}
      </main>
    </div>
  );
}

createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);