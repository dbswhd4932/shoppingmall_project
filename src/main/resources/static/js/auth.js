// 인증 관련 JavaScript

/**
 * 로그인 상태 확인
 * @param {boolean} redirect - 비로그인 시 로그인 페이지로 리다이렉트 여부
 * @returns {boolean} 로그인 여부
 */
function checkAuth(redirect = true) {
    const token = localStorage.getItem('accessToken');
    const isLoggedIn = !!token;

    // 네비게이션 메뉴 업데이트
    updateNavigation(isLoggedIn);

    if (!isLoggedIn && redirect) {
        window.location.href = '/login';
        return false;
    }

    return isLoggedIn;
}

/**
 * 네비게이션 메뉴 업데이트
 * @param {boolean} isLoggedIn - 로그인 여부
 */
function updateNavigation(isLoggedIn) {
    const loginNav = document.getElementById('loginNav');
    const signupNav = document.getElementById('signupNav');
    const logoutNav = document.getElementById('logoutNav');
    const cartNav = document.getElementById('cartNav');
    const ordersNav = document.getElementById('ordersNav');
    const mypageNav = document.getElementById('mypageNav');
    const registerGoodsNav = document.getElementById('registerGoodsNav');

    if (isLoggedIn) {
        // 로그인 상태
        if (loginNav) loginNav.style.display = 'none';
        if (signupNav) signupNav.style.display = 'none';
        if (logoutNav) logoutNav.style.display = 'block';
        if (cartNav) cartNav.style.display = 'block';
        if (ordersNav) ordersNav.style.display = 'block';
        if (mypageNav) mypageNav.style.display = 'block';

        // SELLER 권한이 있으면 상품 등록 버튼 표시
        if (registerGoodsNav && hasRole('ROLE_SELLER')) {
            registerGoodsNav.style.display = 'block';
        }
    } else {
        // 비로그인 상태
        if (loginNav) loginNav.style.display = 'block';
        if (signupNav) signupNav.style.display = 'block';
        if (logoutNav) logoutNav.style.display = 'none';
        if (cartNav) cartNav.style.display = 'none';
        if (ordersNav) ordersNav.style.display = 'none';
        if (mypageNav) mypageNav.style.display = 'none';
        if (registerGoodsNav) registerGoodsNav.style.display = 'none';
    }
}

/**
 * 로그아웃
 */
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        alert('로그아웃 되었습니다.');
        window.location.href = '/';
    }
}

/**
 * Axios 인터셉터 설정
 * JWT 토큰을 자동으로 헤더에 추가
 */
axios.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

/**
 * Axios 응답 인터셉터
 * 401 에러 시 로그인 페이지로 리다이렉트
 */
axios.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response && error.response.status === 401) {
            // 토큰 만료 또는 인증 실패
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            alert('인증이 만료되었습니다. 다시 로그인해주세요.');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

/**
 * 페이지 로드 시 인증 상태 확인
 */
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    const publicPaths = ['/', '/login', '/signup', '/goods'];
    const isPublicPage = publicPaths.some(path => currentPath.startsWith(path));

    // 공개 페이지가 아니면 인증 확인
    if (!isPublicPage) {
        checkAuth(true);
    } else {
        checkAuth(false);
    }
});

/**
 * JWT 토큰 디코딩
 * @param {string} token - JWT 토큰
 * @returns {object} 디코딩된 페이로드
 */
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

/**
 * 토큰 만료 확인
 * @returns {boolean} 토큰 만료 여부
 */
function isTokenExpired() {
    const token = localStorage.getItem('accessToken');
    if (!token) return true;

    const payload = parseJwt(token);
    if (!payload || !payload.exp) return true;

    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
}

/**
 * 현재 사용자 역할 확인
 * @param {string} role - 확인할 역할 (ROLE_USER, ROLE_SELLER, ROLE_ADMIN)
 * @returns {boolean} 해당 역할 여부
 */
function hasRole(role) {
    const token = localStorage.getItem('accessToken');
    if (!token) return false;

    const payload = parseJwt(token);
    if (!payload) return false;

    // JWT 토큰의 권한 정보가 여러 형태로 올 수 있음
    const roles = payload.roles || payload.authorities || payload.auth || [];

    // 배열이거나 문자열일 수 있음
    if (Array.isArray(roles)) {
        return roles.includes(role);
    } else if (typeof roles === 'string') {
        return roles === role;
    }

    return false;
}
