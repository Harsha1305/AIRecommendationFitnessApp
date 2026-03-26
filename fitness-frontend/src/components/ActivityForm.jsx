import { useState } from "react";
import { addActivity } from "../services/api";

const TYPES = [
  { value: "RUNNING", label: "Running", icon: "🏃" },
  { value: "WALKING", label: "Walking", icon: "🚶" },
  { value: "CYCLING", label: "Cycling", icon: "🚴" },
  { value: "SWIMMING", label: "Swimming", icon: "🏊" },
  { value: "HIKING", label: "Hiking", icon: "🥾" },
  { value: "WORKOUT", label: "Workout", icon: "💪" },
];

const ActivityForm = ({ onActivityAdded }) => {
  const [activity, setActivity] = useState({ type: "RUNNING", duration: "", caloriesBurned: "", distance: "" });
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);

  const showToast = (msg, type = "success") => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!activity.duration || !activity.caloriesBurned) {
      showToast("Please fill in duration and calories", "error"); return;
    }
    setLoading(true);
    try {
      await addActivity(activity);
      showToast("Activity logged!");
      setActivity({ type: "RUNNING", duration: "", caloriesBurned: "", distance: "" });
      onActivityAdded?.();
    } catch {
      showToast("Failed to log activity", "error");
    } finally {
      setLoading(false);
    }
  };

  const selected = TYPES.find((t) => t.value === activity.type);

  return (
    <>
      <div className="form-card">
        <div className="form-card-header">
          <span style={{ fontSize: 20 }}>{selected?.icon}</span>
          <h3 className="form-card-title">Log Activity</h3>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-grid" style={{ gridTemplateColumns: "repeat(4, 1fr)" }}>
            <div className="form-group">
              <label className="form-label">Type</label>
              <select className="form-select" value={activity.type}
                onChange={(e) => setActivity({ ...activity, type: e.target.value })}>
                {TYPES.map((t) => <option key={t.value} value={t.value}>{t.icon} {t.label}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Duration</label>
              <input className="form-input" type="number" min="1" placeholder="minutes"
                value={activity.duration}
                onChange={(e) => setActivity({ ...activity, duration: e.target.value })} />
            </div>
            <div className="form-group">
              <label className="form-label">Calories</label>
              <input className="form-input" type="number" min="1" placeholder="kcal"
                value={activity.caloriesBurned}
                onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })} />
            </div>
            <div className="form-group">
              <label className="form-label">Distance (km)</label>
              <input className="form-input" type="number" min="0" step="0.1" placeholder="optional"
                value={activity.distance}
                onChange={(e) => setActivity({ ...activity, distance: e.target.value })} />
            </div>
          </div>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? "Logging…" : "Log Activity"}
          </button>
        </form>
      </div>
      {toast && (
        <div className={`toast ${toast.type === "error" ? "error" : ""}`}>{toast.msg}</div>
      )}
    </>
  );
};

export default ActivityForm;