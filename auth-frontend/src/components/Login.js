import React, { useState, useMemo } from "react";
import api from "../api"; // axios 인스턴스
import { toast } from "react-toastify";
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
      // 로그인 후 리다이렉트 등 추가 가능
    } catch (err) {
      toast.error(err.response?.data || "로그인 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-wrapper">
      <Stars />
      <header className="header">
        My Awesome App
      </header>
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

        <button
          type="submit"
          disabled={loading}
          className="login-button"
        >
          {loading ? "로딩중..." : "로그인"}
        </button>

        <div className="login-footer">
          <a href="/register">회원가입</a> |{" "}
          <a href="/password-reset">비밀번호 재설정</a>
        </div>
      </form>
    </div>
  );
}

export default Login;
