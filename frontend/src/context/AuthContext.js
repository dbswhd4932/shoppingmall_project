import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // JWT 토큰 디코딩 함수
    const parseJwt = (token) => {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                atob(base64).split('').map(c => {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join('')
            );
            return JSON.parse(jsonPayload);
        } catch (error) {
            return null;
        }
    };

    // 사용자 역할 확인
    const hasRole = (role) => {
        if (!user) return false;
        const roles = user.roles || user.authorities || user.auth || [];
        if (Array.isArray(roles)) {
            return roles.includes(role);
        } else if (typeof roles === 'string') {
            return roles === role;
        }
        return false;
    };

    // ADMIN 권한 확인
    const isAdmin = () => {
        return hasRole('ROLE_ADMIN');
    };

    // SELLER 권한 확인
    const isSeller = () => {
        return hasRole('ROLE_SELLER');
    };

    // 초기 로드 시 토큰 확인
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            const payload = parseJwt(token);
            if (payload) {
                setUser(payload);
            }
        }
        setLoading(false);
    }, []);

    // 로그인
    const login = async (loginId, password) => {
        const response = await api.post('/members/login', {
            loginId,
            password,
            loginType: 'NO_SOCIAL'
        });

        const { accessToken, refreshToken } = response.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        const payload = parseJwt(accessToken);
        setUser(payload);

        return response.data;
    };

    // 로그아웃
    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setUser(null);
    };

    // 회원가입
    const signup = async (signupData) => {
        const response = await api.post('/members/signup', signupData);
        return response.data;
    };

    return (
        <AuthContext.Provider value={{
            user,
            login,
            logout,
            signup,
            hasRole,
            isAdmin,
            isSeller,
            loading,
            isAuthenticated: !!user
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
