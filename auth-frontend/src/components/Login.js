import React, { useState, useMemo } from "react";
import {api} from "../api"; // axios 인스턴스 (baseURL 등 설정된 상태)
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./Login.css";

function Stars() {
  const stars = useMemo(() => {
    return Array.from({ length: 100 }, (_, i) => {
      const size = Math.random() * 2 + 1.5;
      const style = {
        width: size + "px",
        height: size + "px",
        top: Math.random() * 100 + "%",
        left: Math.random() * 100 + "%",
        animationDelay: Math.random() * 3 + "s",
      };
      return <div key={i} className="star" style={style}></div>;
    });
  }, []); // 한 번만 생성
  return <div className="stars">{stars}</div>;
}

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await api.post("/auth/login", { email, password });
      localStorage.setItem("accessToken", res.data.token);
      localStorage.setItem("refreshToken", res.data.refreshToken);
      toast.success("로그인 성공!");
      // TODO: 로그인 후 페이지 이동 등
    } catch (err) {
      // 서버에서 내려준 메시지 보여주기
      const msg =
        err.response?.data ||
        err.message ||
        "로그인 중 오류가 발생했습니다.";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-wrapper">
      <Stars />
      <header className="header">이미지 광고 서비스</header>
      <form className="login-container" onSubmit={handleSubmit}>
        <h2 className="login-title">로그인</h2>

        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoFocus
          className="login-input"
        />

        <div className="password-wrapper">
          <input
            type={showPassword ? "text" : "password"}
            value={password}
            placeholder="비밀번호"
            onChange={(e) => setPassword(e.target.value)}
            className="login-input"
            required
          />
          <button
            type="button"
            className="show-password-btn"
            onMouseDown={() => setShowPassword(true)}
            onMouseUp={() => setShowPassword(false)}
            onMouseLeave={() => setShowPassword(false)}
            aria-label="비밀번호 보기"
          >
            👁️
          </button>
        </div>

        <button type="submit" disabled={loading} className="login-button">
          {loading ? "로딩중..." : "로그인"}
        </button>

        <div className="login-footer">
          <a href="/register">회원가입</a> | <a href="/password-reset">비밀번호 재설정</a>
        </div>
      </form>
      <ToastContainer position="top-right" autoClose={3000} />
    </div>
  );
}

export default Login;
