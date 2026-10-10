import { useState } from "react";

function Dashboard() {
  const [email, setEmail] = useState("");
  const [offer, setOffer] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const analyzeOffer = async () => {
    if (!offer.trim()) {
      alert("Please enter the internship offer.");
      return;
    }

    setLoading(true);
    setResult(null);

    try {
      const response = await fetch("http://localhost:8081/api/analyze", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email,
          content: offer,
        }),
      });

      if (!response.ok) {
        throw new Error("Analysis failed");
      }

      const data = await response.json();
      setResult(data);
    } catch (error) {
      console.error(error);
      alert("Could not connect to the backend.");
    } finally {
      setLoading(false);
    }
  };

  const getRiskClass = (level) => {
    if (!level) return "";

    return level.toLowerCase();
  };

  return (
    <div className="dashboard">

      <header className="dashboard-header">
        <div>
          <h1>🛡️ InternShield AI</h1>
          <p>Internship Fraud Detection & Risk Assessment</p>
        </div>

        <div className="status">
          <span className="status-dot"></span>
          System Online
        </div>
      </header>

      <main className="dashboard-content">

        <section className="hero">
          <h2>Check an Internship Offer</h2>
          <p>
            Analyze an internship offer for common scam and fraud indicators.
          </p>
        </section>

        <section className="analyzer-card">

          <label>Recruiter Email</label>

          <input
            type="email"
            placeholder="recruiter@company.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <label>Internship Offer</label>

          <textarea
            rows="10"
            placeholder="Paste the internship offer here..."
            value={offer}
            onChange={(e) => setOffer(e.target.value)}
          />

          <button
            className="analyze-button"
            onClick={analyzeOffer}
            disabled={loading}
          >
            {loading ? "Analyzing..." : "🔍 Analyze Offer"}
          </button>

        </section>

        {result && (
          <section className="result-section">

            <div className={`risk-card ${getRiskClass(result.riskLevel)}`}>

              <div className="risk-title">
                <span>RISK LEVEL</span>
                <strong>{result.riskLevel}</strong>
              </div>

              <div className="score">
                {result.score}
                <small>/100</small>
              </div>

            </div>

            <div className="result-grid">

              <div className="result-panel">
                <h3>🔎 Why this score?</h3>

                {result.indicators?.length > 0 ? (
                  <ul>
                    {result.indicators.map((indicator, index) => (
                      <li key={index}>{indicator}</li>
                    ))}
                  </ul>
                ) : (
                  <p>No major configured scam signals were detected.</p>
                )}
              </div>

              <div className="result-panel">
                <h3>🛡️ What you should do</h3>

                <ul>
                  {result.recommendations?.map((recommendation, index) => (
                    <li key={index}>{recommendation}</li>
                  ))}
                </ul>
              </div>

            </div>

          </section>
        )}

      </main>

    </div>
  );
}

export default Dashboard;