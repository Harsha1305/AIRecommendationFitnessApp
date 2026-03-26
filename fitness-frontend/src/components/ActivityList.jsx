import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { getActivities, getUserRecommendations } from "../services/api";
 
/* ── Helpers ── */
const ICON_MAP = {
  RUNNING: "🏃", WALKING: "🚶", CYCLING: "🚴",
  SWIMMING: "🏊", HIKING: "🥾", WORKOUT: "💪", WALK: "🚶",
};
const fmtDate = (d) => d ? new Date(d).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" }) : "";
const fmtTime = (d) => d ? new Date(d).toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }) : "";
 
/* ── Summary Stat Tiles ── */
const StatTiles = ({ activities }) => {
  const totalDuration = activities.reduce((s, a) => s + (Number(a.duration) || 0), 0);
  const totalCals = activities.reduce((s, a) => s + (Number(a.caloriesBurned) || 0), 0);
  const totalDist = activities.reduce((s, a) => s + (Number(a.distance) || 0), 0);
  const avgHR = activities.filter((a) => a.averageHeartRate).length
    ? Math.round(activities.reduce((s, a) => s + (Number(a.averageHeartRate) || 0), 0) / activities.filter((a) => a.averageHeartRate).length)
    : "—";
 
  return (
    <div className="stats-row">
      {[
        { icon: "🏁", label: "Total Activities", val: activities.length, unit: "" },
        { icon: "⏱", label: "Total Duration", val: totalDuration, unit: "min" },
        { icon: "🔥", label: "Calories Burned", val: totalCals.toLocaleString(), unit: "kcal" },
        { icon: "📍", label: "Total Distance", val: totalDist.toFixed(1), unit: "km" },
      ].map(({ icon, label, val, unit }) => (
        <div className="stat-tile" key={label}>
          <span className="stat-tile-icon">{icon}</span>
          <div className="stat-tile-val">{val}<span className="stat-tile-unit">{unit}</span></div>
          <div className="stat-tile-label">{label}</div>
        </div>
      ))}
    </div>
  );
};
 
/* ── Bar Chart: Calories per activity ── */
const CaloriesBarChart = ({ activities }) => {
  const last10 = [...activities].sort((a, b) => new Date(b.startTime || b.createdAt) - new Date(a.startTime || a.createdAt)).slice(0, 10).reverse();
  const maxCal = Math.max(...last10.map((a) => Number(a.caloriesBurned) || 0), 1);
  const W = 520, H = 160, padL = 32, padB = 24, padT = 16, padR = 10;
  const chartW = W - padL - padR;
  const chartH = H - padT - padB;
  const barW = Math.floor(chartW / last10.length) - 6;
  const gridLines = [0.25, 0.5, 0.75, 1];
 
  return (
    <div className="chart-card">
      <div className="chart-card-header">
        <span className="chart-card-title">Calories per Workout</span>
        <span className="chart-card-meta">Last {last10.length} sessions</span>
      </div>
      <div className="chart-wrap">
        <svg viewBox={`0 0 ${W} ${H}`} className="bar-chart-svg">
          {gridLines.map((pct) => {
            const y = padT + chartH - chartH * pct;
            return (
              <g key={pct}>
                <line x1={padL} x2={W - padR} y1={y} y2={y} className="chart-grid-line" />
                <text x={padL - 4} y={y + 3} className="chart-axis-label" textAnchor="end">
                  {Math.round(maxCal * pct)}
                </text>
              </g>
            );
          })}
          {last10.map((a, i) => {
            const cal = Number(a.caloriesBurned) || 0;
            const barH = Math.max(4, (cal / maxCal) * chartH);
            const x = padL + (chartW / last10.length) * i + (chartW / last10.length - barW) / 2;
            const y = padT + chartH - barH;
            const label = (a.activityType || a.type || "").slice(0, 3);
            return (
              <g key={a._id || a.id || i} className="bar-chart-bar">
                <rect x={x} y={y} width={barW} height={barH} fill="var(--orange)" rx="3" opacity="0.85" />
                <text x={x + barW / 2} y={y - 4} className="chart-value-label" textAnchor="middle">{cal}</text>
                <text x={x + barW / 2} y={H - 6} className="chart-axis-label" textAnchor="middle">{label}</text>
              </g>
            );
          })}
        </svg>
      </div>
    </div>
  );
};
 
/* ── Donut: Activity type distribution ── */
const ActivityDonut = ({ activities }) => {
  const counts = activities.reduce((acc, a) => {
    const t = a.activityType || a.type || "OTHER";
    acc[t] = (acc[t] || 0) + 1;
    return acc;
  }, {});
  const COLORS = ["#FC4C02", "#3b82f6", "#22c55e", "#f59e0b", "#a855f7", "#ec4899"];
  const total = activities.length || 1;
  const entries = Object.entries(counts).slice(0, 6);
  let cumulAngle = -90;
  const cx = 60, cy = 60, r = 50, ri = 32;
 
  const describeArc = (cx, cy, r, startAngle, endAngle) => {
    const toRad = (d) => (d * Math.PI) / 180;
    const s = { x: cx + r * Math.cos(toRad(startAngle)), y: cy + r * Math.sin(toRad(startAngle)) };
    const e = { x: cx + r * Math.cos(toRad(endAngle)), y: cy + r * Math.sin(toRad(endAngle)) };
    const large = endAngle - startAngle > 180 ? 1 : 0;
    return `M ${s.x} ${s.y} A ${r} ${r} 0 ${large} 1 ${e.x} ${e.y}`;
  };
 
  return (
    <div className="chart-card">
      <div className="chart-card-header">
        <span className="chart-card-title">Activity Types</span>
        <span className="chart-card-meta">{total} total</span>
      </div>
      <div className="donut-wrap">
        <svg viewBox="0 0 120 120" width="120" height="120" style={{ flexShrink: 0 }}>
          {entries.map(([type, count], i) => {
            const angle = (count / total) * 360;
            const start = cumulAngle;
            cumulAngle += angle;
            if (angle < 2) return null;
            const path = describeArc(cx, cy, r, start, cumulAngle - 1);
            return (
              <path key={type} d={path} fill="none" stroke={COLORS[i % COLORS.length]} strokeWidth={ri * 0.5}
                strokeLinecap="round" opacity="0.9" />
            );
          })}
          <text x={cx} y={cy - 5} textAnchor="middle" style={{ fill: "var(--text-1)", fontFamily: "var(--font-d)", fontSize: 20, fontWeight: 800 }}>{total}</text>
          <text x={cx} y={cy + 12} textAnchor="middle" style={{ fill: "var(--text-3)", fontFamily: "var(--font-b)", fontSize: 9 }}>workouts</text>
        </svg>
        <div className="donut-legend">
          {entries.map(([type, count], i) => (
            <div className="legend-item" key={type}>
              <div className="legend-dot" style={{ background: COLORS[i % COLORS.length] }} />
              <span className="legend-label">{type}</span>
              <span className="legend-val">{count}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
 
/* ── Duration Line Sparkline ── */
const DurationChart = ({ activities }) => {
  const sorted = [...activities]
    .sort((a, b) => new Date(a.startTime || a.createdAt) - new Date(b.startTime || b.createdAt))
    .slice(-12);
  const maxDur = Math.max(...sorted.map((a) => Number(a.duration) || 0), 1);
  const W = 520, H = 100, padL = 32, padB = 24, padT = 12, padR = 10;
  const cW = W - padL - padR, cH = H - padT - padB;
  const pts = sorted.map((a, i) => {
    const x = padL + (sorted.length > 1 ? (i / (sorted.length - 1)) * cW : cW / 2);
    const y = padT + cH - ((Number(a.duration) || 0) / maxDur) * cH;
    return `${x},${y}`;
  });
 
  return (
    <div className="chart-card">
      <div className="chart-card-header">
        <span className="chart-card-title">Duration Trend</span>
        <span className="chart-card-meta">Last {sorted.length} sessions</span>
      </div>
      <svg viewBox={`0 0 ${W} ${H}`} className="bar-chart-svg">
        {[0.25, 0.5, 0.75, 1].map((p) => (
          <line key={p} x1={padL} x2={W - padR} y1={padT + cH - cH * p} y2={padT + cH - cH * p} className="chart-grid-line" />
        ))}
        {pts.length > 1 && (
          <polyline points={pts.join(" ")} fill="none" stroke="var(--blue)" strokeWidth="2" strokeLinejoin="round" />
        )}
        {sorted.map((a, i) => {
          const x = padL + (sorted.length > 1 ? (i / (sorted.length - 1)) * cW : cW / 2);
          const y = padT + cH - ((Number(a.duration) || 0) / maxDur) * cH;
          return <circle key={i} cx={x} cy={y} r="3.5" fill="var(--blue)" />;
        })}
        {sorted.map((a, i) => {
          const x = padL + (sorted.length > 1 ? (i / (sorted.length - 1)) * cW : cW / 2);
          return <text key={i} x={x} y={H - 6} className="chart-axis-label" textAnchor="middle">{fmtDate(a.startTime || a.createdAt).split(",")[0]}</text>;
        })}
      </svg>
    </div>
  );
};
 
/* ── Sliding Activity Window ── */
const ActivitySlider = ({ activities, recommendations, onClickActivity }) => {
  const [idx, setIdx] = useState(0);
  const sorted = [...activities].sort((a, b) =>
    new Date(b.startTime || b.createdAt) - new Date(a.startTime || a.createdAt)
  );
  const PER_PAGE = 2;
  const maxIdx = Math.max(0, sorted.length - PER_PAGE);
  const visible = sorted.slice(idx, idx + PER_PAGE);
 
  const getRec = (activity) => {
    const id = activity._id || activity.id;
    return recommendations?.find((r) => r.activityId === id);
  };
 
  if (!sorted.length) return null;
 
  return (
    <div>
      <div className="slider-section-header">
        <div>
          <div className="slider-title">Recent Workouts</div>
          <div className="slider-counter">{sorted.length} activities · sorted by date</div>
        </div>
        <div className="slider-nav">
          <button className="slider-btn" onClick={() => setIdx(Math.max(0, idx - 1))} disabled={idx === 0}>←</button>
          <span style={{ fontSize: 12, color: "var(--text-3)", minWidth: 40, textAlign: "center" }}>
            {idx + 1}–{Math.min(idx + PER_PAGE, sorted.length)} / {sorted.length}
          </span>
          <button className="slider-btn" onClick={() => setIdx(Math.min(maxIdx, idx + 1))} disabled={idx >= maxIdx}>→</button>
        </div>
      </div>
 
      <div className="slider-viewport">
        <div className="slider-track" style={{ transform: `translateX(0)` }}>
          {visible.map((activity, i) => {
            const rec = getRec(activity);
            const icon = ICON_MAP[activity.activityType || activity.type] ?? "⚡";
            const type = activity.activityType || activity.type || "WORKOUT";
            const startD = fmtDate(activity.startTime || activity.createdAt);
            const startT = fmtTime(activity.startTime);
            const endT = fmtTime(activity.endTime);
            const intensity = activity.duration > 0
              ? Math.round((Number(activity.caloriesBurned) || 0) / Number(activity.duration))
              : 0;
 
            return (
              <div
                key={activity._id || activity.id || i}
                className={`activity-slide ${i === 0 ? "active" : ""}`}
                onClick={() => onClickActivity(activity._id || activity.id)}
              >
                <div className="slide-header">
                  <div className="slide-type-row">
                    <div className="slide-icon">{icon}</div>
                    <div>
                      <div className="slide-type">{type}</div>
                      {startD && <div className="slide-date">{startD}</div>}
                      {startT && endT && <div className="slide-time-range">{startT} → {endT}</div>}
                    </div>
                  </div>
                  <span className="slide-source">{activity.source || "MANUAL"}</span>
                </div>
 
                <div className="slide-stats">
                  <div className="slide-stat">
                    <div className="slide-stat-val">{activity.duration ?? "—"}</div>
                    <div className="slide-stat-unit">min</div>
                    <div className="slide-stat-label">Duration</div>
                  </div>
                  <div className="slide-stat">
                    <div className="slide-stat-val accent">{Number(activity.caloriesBurned || 0).toLocaleString()}</div>
                    <div className="slide-stat-unit">kcal</div>
                    <div className="slide-stat-label">Calories</div>
                  </div>
                  <div className="slide-stat">
                    <div className="slide-stat-val">{activity.distance ?? "—"}</div>
                    <div className="slide-stat-unit">km</div>
                    <div className="slide-stat-label">Distance</div>
                  </div>
                  <div className="slide-stat">
                    <div className="slide-stat-val">{activity.averageHeartRate ?? intensity}</div>
                    <div className="slide-stat-unit">{activity.averageHeartRate ? "bpm" : "kcal/m"}</div>
                    <div className="slide-stat-label">{activity.averageHeartRate ? "Avg HR" : "Intensity"}</div>
                  </div>
                </div>
 
                {rec ? (
                  <div className="slide-rec">
                    <div className="slide-rec-label">AI Coach</div>
                    <div className="slide-rec-text">
                      {rec.recommendation?.slice(0, 120)}{rec.recommendation?.length > 120 ? "…" : ""}
                    </div>
                    <span className="slide-rec-tip" onClick={(e) => { e.stopPropagation(); onClickActivity(activity._id || activity.id); }}>
                      View full analysis →
                    </span>
                  </div>
                ) : (
                  <div className="slide-rec" style={{ opacity: 0.5 }}>
                    <div className="slide-rec-label">AI Coach</div>
                    <div className="slide-rec-text">Tap to view AI recommendations for this workout</div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
 
      <div className="slider-dots">
        {Array.from({ length: Math.ceil(sorted.length / PER_PAGE) }).map((_, i) => (
          <div
            key={i}
            className={`slider-dot ${Math.floor(idx / PER_PAGE) === i ? "active" : ""}`}
            onClick={() => setIdx(i * PER_PAGE)}
          />
        ))}
      </div>
    </div>
  );
};
 
/* ── Main ActivityList ── */
const ActivityList = () => {
  const [activities, setActivities] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
 
  useEffect(() => {
    const load = async () => {
      try {
        const [actRes, recRes] = await Promise.allSettled([getActivities(), getUserRecommendations()]);
        if (actRes.status === "fulfilled") setActivities(actRes.value.data || []);
        if (recRes.status === "fulfilled") setRecommendations(recRes.value.data || []);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);
 
  if (loading) return <div className="state-center"><div className="spinner" /><span>Loading your dashboard…</span></div>;
  if (!activities.length) return (
    <div className="state-center">
      <div style={{ fontSize: 36 }}>🏁</div>
      <p style={{ color: "var(--text-3)", fontSize: 14 }}>No activities yet. Log your first workout above!</p>
    </div>
  );
 
  return (
    <div>
      <StatTiles activities={activities} />
 
      <p className="charts-section-title">Analytics Overview</p>
      <div className="charts-grid">
        <CaloriesBarChart activities={activities} />
        <ActivityDonut activities={activities} />
      </div>
      <div style={{ marginBottom: 32 }}>
        <DurationChart activities={activities} />
      </div>
 
      <ActivitySlider
        activities={activities}
        recommendations={recommendations}
        onClickActivity={(id) => navigate(`/activities/${id}`)}
      />
    </div>
  );
};
 
export default ActivityList;