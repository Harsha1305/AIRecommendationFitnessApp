import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getActivityDetail } from "../services/api";

const ICON_MAP = {
  RUNNING: "🏃", WALKING: "🚶", CYCLING: "🚴",
  SWIMMING: "🏊", HIKING: "🥾", WORKOUT: "💪", WALK: "🚶",
};

const fmtDateTime = (d) => d
  ? new Date(d).toLocaleDateString("en-US", { weekday: "long", month: "long", day: "numeric", year: "numeric", hour: "2-digit", minute: "2-digit" })
  : "";

const fmtTime = (d) => d ? new Date(d).toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }) : "";

const RecSection = ({ label, children }) => (
  <div className="rec-section">
    <div className="rec-section-label">{label}</div>
    {children}
  </div>
);

const ActivityDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [activity, setActivity] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await getActivityDetail(id);
        setActivity(res.data);
      } catch {
        setError("Could not load activity details.");
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [id]);

  if (loading) return <div className="state-center"><div className="spinner" /><span>Loading…</span></div>;
  if (error || !activity) return (
    <div className="state-center">
      <div style={{ fontSize: 32 }}>⚠️</div>
      <p style={{ color: "var(--text-3)", fontSize: 14 }}>{error ?? "Activity not found."}</p>
      <button className="btn-primary" style={{ marginTop: 16 }} onClick={() => navigate("/activities")}>← Back</button>
    </div>
  );

  const type = activity.activityType || activity.type || "WORKOUT";
  const icon = ICON_MAP[type] ?? "⚡";
  const intensity = activity.duration > 0
    ? Math.round((Number(activity.caloriesBurned) || 0) / Number(activity.duration))
    : 0;
  const hasRec = activity.recommendation || activity.improvements?.length || activity.suggestions?.length;

  return (
    <div className="detail-page">
      <button className="detail-back" onClick={() => navigate("/activities")}>← Back to Dashboard</button>

      <div className="detail-hero">
        <div className="detail-hero-top">
          <div className="detail-icon">{icon}</div>
          <div>
            <div className="detail-type">{type}</div>
            <div className="detail-date">{fmtDateTime(activity.startTime || activity.createdAt)}</div>
            {activity.startTime && activity.endTime && (
              <div style={{ fontSize: 12, color: "var(--text-3)", marginTop: 3 }}>
                {fmtTime(activity.startTime)} → {fmtTime(activity.endTime)}
              </div>
            )}
          </div>
          <div style={{ marginLeft: "auto" }}>
            <span style={{
              fontSize: 11, fontWeight: 500, letterSpacing: 1, textTransform: "uppercase",
              background: "var(--bg-inset)", border: "1px solid var(--border)",
              borderRadius: 6, padding: "4px 10px", color: "var(--text-3)"
            }}>
              {activity.source || "MANUAL"}
            </span>
          </div>
        </div>

        <div className="detail-stats-row">
          {[
            { val: activity.duration ?? "—", unit: "min", label: "Duration" },
            { val: Number(activity.caloriesBurned || 0).toLocaleString(), unit: "kcal", label: "Calories" },
            { val: activity.distance ?? "—", unit: "km", label: "Distance" },
            { val: activity.averageHeartRate ?? `${intensity}`, unit: activity.averageHeartRate ? "bpm" : "kcal/m", label: activity.averageHeartRate ? "Avg Heart Rate" : "Intensity" },
          ].map(({ val, unit, label }) => (
            <div className="detail-stat-box" key={label}>
              <div className="detail-stat-num">{val}<span className="detail-stat-unit">{unit}</span></div>
              <div className="detail-stat-label">{label}</div>
            </div>
          ))}
        </div>
      </div>

      {hasRec ? (
        <div className="rec-card">
          <div className="rec-card-header">
            <span className="rec-ai-badge">AI Analysis</span>
            <h3 className="rec-card-title">Coach Insights</h3>
          </div>
          <div className="rec-body">
            {activity.recommendation && (
              <RecSection label="Analysis">
                <p className="rec-text">{activity.recommendation}</p>
              </RecSection>
            )}
            {activity.improvements?.length > 0 && (
              <RecSection label="Improvements">
                <ul className="rec-list">{activity.improvements.map((item, i) => <li key={i}>{item}</li>)}</ul>
              </RecSection>
            )}
            {activity.suggestions?.length > 0 && (
              <RecSection label="Suggestions">
                <ul className="rec-list">{activity.suggestions.map((item, i) => <li key={i}>{item}</li>)}</ul>
              </RecSection>
            )}
            {activity.safety?.length > 0 && (
              <RecSection label="Safety Guidelines">
                <ul className="rec-list">{activity.safety.map((item, i) => <li key={i}>{item}</li>)}</ul>
              </RecSection>
            )}
          </div>
        </div>
      ) : (
        <div style={{
          background: "var(--bg-card)", border: "1px solid var(--border)",
          borderRadius: "var(--r-lg)", padding: 32, textAlign: "center",
          color: "var(--text-3)", fontSize: 14
        }}>
          <div style={{ fontSize: 32, marginBottom: 10 }}>🤖</div>
          AI recommendations will appear here after analysis
        </div>
      )}
    </div>
  );
};

export default ActivityDetail;