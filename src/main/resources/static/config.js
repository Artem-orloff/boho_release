(function () {
  const isLocal = /^(localhost|127\.0\.0\.1)$/.test(location.hostname);
  window.BOHO_API_BASE = isLocal ? 'http://localhost:8080' : '';
})();




