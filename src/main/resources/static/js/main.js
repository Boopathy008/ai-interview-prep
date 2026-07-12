/* ============================================================
   AI Interview Prep - Main JavaScript
   Handles: Auth, API calls, Theme, Toast, Utils
   ============================================================ */

// ---------------------------------------------------------------
// Theme Manager
// ---------------------------------------------------------------
const Theme = {
  init() {
    const saved = localStorage.getItem('theme') || 'light';
    this.apply(saved);
  },
  toggle() {
    const current = document.documentElement.getAttribute('data-theme') || 'light';
    const next = current === 'light' ? 'dark' : 'light';
    this.apply(next);
    localStorage.setItem('theme', next);
  },
  apply(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    const icon = document.getElementById('themeIcon');
    if (icon) icon.textContent = theme === 'dark' ? '☀️' : '🌙';
  }
};

// ---------------------------------------------------------------
// Auth Manager
// ---------------------------------------------------------------
const Auth = {
  getToken() { return localStorage.getItem('jwt_token'); },
  getUser() {
    const u = localStorage.getItem('user_data');
    return u ? JSON.parse(u) : null;
  },
  setSession(token, user) {
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('user_data', JSON.stringify(user));
  },
  clearSession() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_data');
  },
  isLoggedIn() { return !!this.getToken(); },
  isAdmin() { return this.getUser()?.role === 'ADMIN'; },
  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = '/login';
      return false;
    }
    return true;
  },
  requireAdmin() {
    if (!this.isAdmin()) {
      window.location.href = '/dashboard';
      return false;
    }
    return true;
  },
  logout() {
    this.clearSession();
    window.location.href = '/login';
  }
};

// ---------------------------------------------------------------
// API Client
// ---------------------------------------------------------------
const API = {
  BASE: '/api',

  async request(method, url, body = null, showLoader = true) {
    const options = {
      method,
      headers: { 'Content-Type': 'application/json' }
    };

    const token = Auth.getToken();
    if (token) options.headers['Authorization'] = `Bearer ${token}`;

    if (body) options.body = JSON.stringify(body);

    try {
      const res = await fetch(this.BASE + url, options);
      const data = await res.json();

      if (res.status === 401) {
        Auth.clearSession();
        Toast.error('Session expired', 'Please login again');
        setTimeout(() => window.location.href = '/login', 1500);
        return null;
      }

      if (!res.ok) {
        const msg = data.message || 'Something went wrong';
        Toast.error('Error', msg);
        return null;
      }

      return data;
    } catch (err) {
      console.error('API Error:', err);
      Toast.error('Network Error', 'Could not connect to server. Please try again.');
      return null;
    }
  },

  get: (url) => API.request('GET', url),
  post: (url, body) => API.request('POST', url, body),
  put: (url, body) => API.request('PUT', url, body),
  delete: (url) => API.request('DELETE', url),

  // Auth
  auth: {
    register: (data) => API.post('/auth/register', data),
    login: (data) => API.post('/auth/login', data)
  },

  // Topics
  topics: {
    getAll: () => API.get('/topics'),
    getByCategory: (cat) => API.get(`/topics/category/${cat}`)
  },

  // Tests
  tests: {
    create: (data) => API.post('/tests/create', data),
    submit: (data) => API.post('/tests/submit', data),
    get: (id) => API.get(`/tests/${id}`),
    my: () => API.get('/tests/my')
  },

  // Dashboard
  dashboard: {
    get: () => API.get('/dashboard'),
    progress: () => API.get('/dashboard/progress'),
    weakTopics: () => API.get('/dashboard/weak-topics')
  },

  // Mock Interview
  mock: {
    start: (data) => API.post('/mock-interview/start', data),
    respond: (id, answer, qNum) => API.post(`/mock-interview/${id}/respond?answer=${encodeURIComponent(answer)}&questionNumber=${qNum}`, {}),
    my: () => API.get('/mock-interview/my')
  },

  // Study Plan
  studyPlan: {
    generate: (data) => API.post('/study-plan/generate', data),
    save: (data) => API.post('/study-plan/save', data),
    active: () => API.get('/study-plan/active'),
    deleteActive: () => API.delete('/study-plan/active')
  },

  // AI
  ai: {
    chat: (data) => API.post('/ai/chat', data),
    codeReview: (data) => API.post('/ai/code-review', data)
  },

  // Admin
  admin: {
    stats: () => API.get('/admin/stats')
  }
};

// ---------------------------------------------------------------
// Toast Notifications
// ---------------------------------------------------------------
const Toast = {
  container: null,

  init() {
    this.container = document.getElementById('toastContainer');
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.id = 'toastContainer';
      this.container.className = 'toast-container';
      document.body.appendChild(this.container);
    }
  },

  show(type, title, message, duration = 4000) {
    if (!this.container) this.init();

    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
      <span class="toast-icon">${icons[type] || 'ℹ️'}</span>
      <div class="flex-1">
        <div class="toast-title">${title}</div>
        ${message ? `<div class="toast-msg">${message}</div>` : ''}
      </div>
      <button class="toast-close" onclick="this.closest('.toast').remove()">✕</button>
    `;

    this.container.appendChild(toast);

    setTimeout(() => {
      toast.style.animation = 'slideIn 0.3s reverse';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  },

  success: (title, msg) => Toast.show('success', title, msg),
  error: (title, msg) => Toast.show('error', title, msg),
  warning: (title, msg) => Toast.show('warning', title, msg),
  info: (title, msg) => Toast.show('info', title, msg)
};

// ---------------------------------------------------------------
// Sidebar Manager
// ---------------------------------------------------------------
const Sidebar = {
  init() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');
    const toggle = document.getElementById('sidebarToggle');

    if (toggle) {
      toggle.addEventListener('click', () => this.open());
    }
    if (overlay) {
      overlay.addEventListener('click', () => this.close());
    }

    // Mark active link
    const path = window.location.pathname;
    document.querySelectorAll('.sidebar-link').forEach(link => {
      const href = link.getAttribute('data-href') || link.getAttribute('href');
      if (href && path.startsWith(href) && href !== '/') {
        link.classList.add('active');
      } else if (href === '/' && path === '/') {
        link.classList.add('active');
      }
    });

    // Load user info
    this.loadUser();
  },

  open() {
    document.getElementById('sidebar')?.classList.add('open');
    document.getElementById('sidebarOverlay')?.classList.add('show');
  },

  close() {
    document.getElementById('sidebar')?.classList.remove('open');
    document.getElementById('sidebarOverlay')?.classList.remove('show');
  },

  loadUser() {
    const user = Auth.getUser();
    if (!user) return;

    const nameEl = document.getElementById('sidebarUserName');
    const roleEl = document.getElementById('sidebarUserRole');
    const avatarEl = document.getElementById('sidebarUserAvatar');

    if (nameEl) nameEl.textContent = user.fullName || user.username;
    if (roleEl) roleEl.textContent = user.role === 'ADMIN' ? '🛡️ Admin' : '🎓 Student';
    if (avatarEl) avatarEl.textContent = (user.fullName || user.username || 'U')[0].toUpperCase();
  }
};

// ---------------------------------------------------------------
// Loader Utility
// ---------------------------------------------------------------
const Loader = {
  show(containerId, message = 'AI is generating...', sub = 'This may take a few seconds') {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = `
      <div class="ai-loading">
        <div class="ai-loading-orb">🤖</div>
        <div class="ai-loading-text">${message}</div>
        <div class="ai-loading-subtext">${sub}</div>
      </div>
    `;
  },

  skeleton(containerId, rows = 3) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = Array(rows).fill(`
      <div style="margin-bottom:16px">
        <div class="skeleton" style="height:20px;width:60%;margin-bottom:8px"></div>
        <div class="skeleton" style="height:14px;width:90%;margin-bottom:6px"></div>
        <div class="skeleton" style="height:14px;width:75%"></div>
      </div>
    `).join('');
  }
};

// ---------------------------------------------------------------
// Score Circle SVG
// ---------------------------------------------------------------
function renderScoreCircle(containerId, score, color = '#6C63FF') {
  const el = document.getElementById(containerId);
  if (!el) return;

  const radius = 56;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;
  const gradeColor = score >= 80 ? '#00D8A0' : score >= 60 ? '#29B6F6' : score >= 40 ? '#FFB74D' : '#FF6B6B';

  el.innerHTML = `
    <div class="score-circle">
      <svg viewBox="0 0 140 140" width="140" height="140">
        <circle cx="70" cy="70" r="${radius}" fill="none" stroke="var(--bg-input)" stroke-width="10"/>
        <circle cx="70" cy="70" r="${radius}" fill="none" stroke="${gradeColor}"
          stroke-width="10" stroke-dasharray="${circumference}"
          stroke-dashoffset="${offset}" stroke-linecap="round"
          style="transition: stroke-dashoffset 1.5s cubic-bezier(0.4,0,0.2,1)"/>
      </svg>
      <div class="score-text">
        <div class="score-value" style="color:${gradeColor}">${Math.round(score)}</div>
        <div class="score-unit">/ 100</div>
      </div>
    </div>
  `;
}

// ---------------------------------------------------------------
// Progress Bar
// ---------------------------------------------------------------
function renderProgressBar(pct, color = 'primary') {
  return `
    <div class="progress-bar-container">
      <div class="progress-bar-fill ${color}" style="width:0%" data-width="${pct}%"></div>
    </div>
  `;
}

function animateProgressBars() {
  document.querySelectorAll('.progress-bar-fill[data-width]').forEach(bar => {
    setTimeout(() => {
      bar.style.width = bar.getAttribute('data-width');
    }, 100);
  });
}

// ---------------------------------------------------------------
// Grade helper
// ---------------------------------------------------------------
function getGrade(score) {
  if (score >= 90) return 'A+';
  if (score >= 80) return 'A';
  if (score >= 70) return 'B';
  if (score >= 60) return 'C';
  if (score >= 50) return 'D';
  return 'F';
}

function getGradeClass(grade) {
  if (grade?.startsWith('A')) return 'grade-A';
  if (grade?.startsWith('B')) return 'grade-B';
  if (grade?.startsWith('C')) return 'grade-C';
  return 'grade-D';
}

// ---------------------------------------------------------------
// Format helpers
// ---------------------------------------------------------------
function formatTime(seconds) {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}

function formatDate(dateStr) {
  if (!dateStr) return 'N/A';
  return new Date(dateStr).toLocaleDateString('en-US', {
    day: 'numeric', month: 'short', year: 'numeric'
  });
}

function timeSince(dateStr) {
  if (!dateStr) return '';
  const seconds = Math.floor((new Date() - new Date(dateStr)) / 1000);
  if (seconds < 60) return 'just now';
  const intervals = { year: 31536000, month: 2592000, week: 604800, day: 86400, hour: 3600, minute: 60 };
  for (const [unit, secs] of Object.entries(intervals)) {
    const interval = Math.floor(seconds / secs);
    if (interval >= 1) return `${interval} ${unit}${interval > 1 ? 's' : ''} ago`;
  }
  return 'just now';
}

function difficultyBadge(diff) {
  const classes = { EASY: 'difficulty-easy', MEDIUM: 'difficulty-medium', HARD: 'difficulty-hard' };
  const icons = { EASY: '🟢', MEDIUM: '🟡', HARD: '🔴' };
  return `<span class="badge ${classes[diff] || 'badge-secondary'}">${icons[diff] || ''} ${diff}</span>`;
}

function typeBadge(type) {
  const labels = { MCQ: '📝 MCQ', CODING: '💻 Coding', THEORY: '📚 Theory', OUTPUT: '🖥️ Output', DEBUGGING: '🐛 Debug', MIXED: '🔀 Mixed', MOCK_INTERVIEW: '🎤 Mock' };
  return `<span class="badge badge-primary">${labels[type] || type}</span>`;
}

function masteryBadge(level) {
  const cfg = {
    BEGINNER: { icon: '🌱', color: 'badge-secondary' },
    INTERMEDIATE: { icon: '📈', color: 'badge-info' },
    ADVANCED: { icon: '⭐', color: 'badge-warning' },
    EXPERT: { icon: '🏆', color: 'badge-success' }
  };
  const c = cfg[level] || cfg.BEGINNER;
  return `<span class="badge ${c.color}">${c.icon} ${level}</span>`;
}

// ---------------------------------------------------------------
// Language icons
// ---------------------------------------------------------------
const LANG_ICONS = {
  Java: '☕', Python: '🐍', JavaScript: '🟨', TypeScript: '🔷',
  'C': '⚙️', 'C++': '🔵', 'C#': '💜', Go: '🐹', Kotlin: '🎯',
  PHP: '🐘', SQL: '🗄️', DSA: '🧮', 'Spring Boot': '🍃',
  'System Design': '🏗️'
};

function getLangIcon(lang) {
  return LANG_ICONS[lang] || '💻';
}

// ---------------------------------------------------------------
// Confirm dialog
// ---------------------------------------------------------------
function confirmAction(message, onConfirm) {
  if (confirm(message)) onConfirm();
}

// ---------------------------------------------------------------
// Form validation
// ---------------------------------------------------------------
function validateForm(formId) {
  const form = document.getElementById(formId);
  if (!form) return false;
  let valid = true;
  form.querySelectorAll('[required]').forEach(input => {
    const errEl = form.querySelector(`[data-error="${input.name}"]`);
    if (!input.value.trim()) {
      input.style.borderColor = 'var(--accent)';
      if (errEl) { errEl.textContent = 'This field is required'; errEl.classList.add('show'); }
      valid = false;
    } else {
      input.style.borderColor = '';
      if (errEl) errEl.classList.remove('show');
    }
  });
  return valid;
}

// ---------------------------------------------------------------
// Copy to clipboard
// ---------------------------------------------------------------
async function copyToClipboard(text, btnEl) {
  try {
    await navigator.clipboard.writeText(text);
    const orig = btnEl?.textContent;
    if (btnEl) { btnEl.textContent = '✅ Copied!'; setTimeout(() => btnEl.textContent = orig, 2000); }
    Toast.success('Copied!', 'Content copied to clipboard');
  } catch {
    Toast.error('Failed', 'Could not copy to clipboard');
  }
}

// ---------------------------------------------------------------
// Dark mode syntax highlight helper
// ---------------------------------------------------------------
function highlightCode() {
  document.querySelectorAll('pre code, .code-block').forEach(el => {
    if (!el.classList.contains('highlighted')) {
      el.classList.add('highlighted');
    }
  });
}

// ---------------------------------------------------------------
// Init on DOM ready
// ---------------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
  Theme.init();
  Toast.init();

  // Theme toggle button
  const themeBtn = document.getElementById('themeToggle');
  if (themeBtn) themeBtn.addEventListener('click', () => Theme.toggle());

  // Logout buttons
  document.querySelectorAll('[data-action="logout"]').forEach(btn => {
    btn.addEventListener('click', () => Auth.logout());
  });

  // Animate progress bars on page load
  requestAnimationFrame(animateProgressBars);

  // Highlight code blocks
  highlightCode();
});
