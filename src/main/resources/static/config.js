//(function () {
//  // Локалка → http://localhost:8080, прод → API-домен или пусто (если есть nginx-прокси /api)
//  const isLocal = ['localhost', '127.0.0.1'].includes(location.hostname);
//  window.BOHO_API_BASE = isLocal
//    ? 'http://localhost:8080'
//    : 'https://artem-orloff-boho-release-a1ff.twc.net'; // или '' если фронт проксирует /api на тот же домен
//})();
// Прод: тот же домен (пустая строка). Локалка: раскомментируй localhost.
(function () {
  const isLocal = /^(localhost|127\.0\.0\.1)$/.test(location.hostname);
  window.BOHO_API_BASE = isLocal ? 'http://localhost:8080' : '';
})();




