import axios from "axios";

const API_URL = "http://localhost:8084/api";

const api = axios.create({ baseURL: API_URL });

// This function gets called fresh on every request
api.interceptors.request.use((config) => {
  // Read from localStorage (set by authSlice)
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  console.log("🔍 Interceptor - token:", token ? "EXISTS" : "MISSING");
  console.log("🔍 Interceptor - userId:", userId);

  if (token) config.headers["Authorization"] = `Bearer ${token}`;
  if (userId) config.headers["X-User-Id"] = userId;

  return config;
});

// ── Activity Service  →  /api/v1/activities
const ACT = "/v1/activities";

export const getActivities = () => {
  const userId = localStorage.getItem("userId");
  return api.get(`${ACT}/user/${userId}`);
};

export const syncActivities = () => {
  const userId = localStorage.getItem("userId");
  return api.post(`${ACT}/sync/${userId}`);
};

export const addActivity = (activity) => {
  const userId = localStorage.getItem("userId");
  return api.post(`${ACT}/manual`, {
    activityType: activity.type,
    duration: activity.duration ? Number(activity.duration) : null,
    caloriesBurned: activity.caloriesBurned ? Number(activity.caloriesBurned) : null,
    distance: activity.distance ? Number(activity.distance) : null,
    averageHeartRate: activity.averageHeartRate ? Number(activity.averageHeartRate) : null,
    startTime: activity.startTime || new Date().toISOString().slice(0, 19),
    endTime: activity.endTime || null,
    source: activity.source || "MANUAL",
    userId,
  });
};

export const getActivityById = (activityId) =>
  api.get(`${ACT}/activity/${activityId}`);

// ── AI Recommendation Service  →  /api/recommendation
export const getUserRecommendations = () => {
  const userId = localStorage.getItem("userId");
  return api.get(`/recommendation/user/${userId}`);
};

export const getActivityRecommendation = (activityId) =>
  api.get(`/recommendation/activity/${activityId}`);

export const getActivityDetail = (activityId) =>
  api.get(`${ACT}/activity/${activityId}`);  // ← fix: points to activity service