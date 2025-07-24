// src/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "https://8080-mlee9999-authtest-q05q86trbs2.ws-us120.gitpod.io", // 백엔드 주소
  withCredentials: true,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
