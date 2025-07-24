// api.js
import axios from "axios";

const baseURL = "https://8080-mlee9999-authtest-q05q86trbs2.ws-us120.gitpod.io";

const api = axios.create({
  baseURL,
  withCredentials: true,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 인증 필요 없는 API 전용 인스턴스 (헤더 설정 없음)
const noAuthApi = axios.create({
  baseURL,
  withCredentials: true,
});

export { api, noAuthApi };
