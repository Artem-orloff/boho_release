(function () {
  "use strict";

  /* =================== БАЗОВЫЕ UI-ШТУКИ =================== */
  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  // Год в футере
  const yearEl = $('#year');
  if (yearEl) yearEl.textContent = new Date().getFullYear();

  // Плавные якоря
  $$('a[href^="#"]').forEach(a => {
    a.addEventListener('click', e => {
      const id = a.getAttribute('href');
      if (id && id.length > 1) {
        e.preventDefault();
        $(id)?.scrollIntoView({ behavior: 'smooth' });
      }
    });
  });

  // Анимации появления
  const prefersReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (!prefersReduced && 'IntersectionObserver' in window) {
    const io = new IntersectionObserver((entries) => {
      entries.forEach(el => {
        if (el.isIntersecting) {
          el.target.classList.add('is-visible');
          io.unobserve(el.target);
        }
      });
    }, { threshold: 0.12, rootMargin: '0px 0px -10% 0px' });
    $$('.reveal').forEach(el => io.observe(el));
  } else {
    $$('.reveal').forEach(el => el.classList.add('is-visible'));
  }

  // Бургер
  const toggle = $('.menu-toggle');
  const nav = $('#primary-nav');
  toggle?.addEventListener('click', () => {
    const expanded = toggle.getAttribute('aria-expanded') === 'true';
    toggle.setAttribute('aria-expanded', String(!expanded));
    nav?.classList.toggle('open');
  });

  // Кнопка "вверх"
  const toTopBtn = $('.back-to-top');
  const toggleToTop = () => {
    const y = window.scrollY || document.documentElement.scrollTop;
    toTopBtn?.classList.toggle('show', y > 400);
  };
  toggleToTop();
  window.addEventListener('scroll', toggleToTop, { passive: true });
  toTopBtn?.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));

  /* =================== МОДАЛКА УСЛУГ (описание) =================== */
  const cardOverlay = $('#card-overlay');
  const serviceModal = $('#service-modal');
  const serviceModalTitle = serviceModal?.querySelector('.service-modal__title');
  const serviceModalBody = serviceModal?.querySelector('.service-modal__body');
  const cards = $$('.service');

  let lockBody = false, scrollY = 0;
  function lock() {
    if (lockBody) return;
    scrollY = window.scrollY || 0;
    Object.assign(document.body.style, { position: 'fixed', top: `-${scrollY}px`, left: '0', right: '0', width: '100%' });
    lockBody = true;
  }
  function unlock() {
    if (!lockBody) return;
    Object.assign(document.body.style, { position: '', top: '', left: '', right: '', width: '' });
    window.scrollTo(0, scrollY);
    lockBody = false;
  }

  function openFromCard(card) {
    const title = card.querySelector('.service__summary h3')?.textContent?.trim() || '';
    const detailsHTML = card.querySelector('.service__details')?.innerHTML || '';
    if (!serviceModal || !serviceModalTitle || !serviceModalBody) return;
    serviceModalTitle.textContent = title;
    serviceModalBody.innerHTML = detailsHTML;
    serviceModal.hidden = false;
    cardOverlay.hidden = false;
    lock();
  }
  function closeServiceModal() {
    if (!serviceModal || !serviceModalTitle || !serviceModalBody) return;
    serviceModal.hidden = true;
    cardOverlay.hidden = true;
    serviceModalTitle.textContent = '';
    serviceModalBody.innerHTML = '';
    unlock();
  }

  cards.forEach(card => {
    card.querySelector('.service__summary')?.addEventListener('click', (e) => {
      e.preventDefault(); e.stopPropagation();
      openFromCard(card);
    });
  });

  serviceModal?.addEventListener('click', closeServiceModal);
  cardOverlay?.addEventListener('click', () => {
    const bookingOpen = !$('#booking-modal')?.hidden;
    if (!bookingOpen) closeServiceModal();
  });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && !serviceModal?.hidden) closeServiceModal(); });

  /* =================== ТОСТ =================== */
  let toast = $('.toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.className = 'toast';
    toast.hidden = true;
    document.body.appendChild(toast);
  }
  function showToast(msg) {
    toast.textContent = msg;
    toast.hidden = false;
    toast.classList.add('visible');
    setTimeout(() => {
      toast.classList.remove('visible');
      setTimeout(() => toast.hidden = true, 220);
    }, 2000);
  }

  /* =================== БРОНЬ: модалка и логика =================== */
  const API_BASE = (window.BOHO_API_BASE || '').replace(/\/+$/, '');
  function sameOrigin(u) { try { const x = new URL(u, location.href); return x.origin === location.origin; } catch { return true; } }

  async function apiGET(url) {
    if (!url) throw new Error('API base is empty (BOHO_API_BASE не задан)');
    const opts = sameOrigin(url) ? {} : { mode: 'cors' };
    const r = await fetch(url, opts);
    if (!r.ok) {
      const txt = await r.text().catch(() => '');
      throw new Error(`HTTP ${r.status}: ${txt.slice(0, 200)}`);
    }
    const ct = (r.headers.get('content-type') || '').toLowerCase();
    if (r.status === 204) return null;
    if (ct.includes('application/json')) return r.json();
    throw new Error(`Ожидался JSON, получен ${ct || 'unknown'}`);
  }

  async function apiPOST(url, body, headers = {}) {
    if (!url) throw new Error('API base is empty (BOHO_API_BASE не задан)');
    const opts = sameOrigin(url)
      ? { method: 'POST', headers: { 'Content-Type': 'application/json', ...headers }, body: JSON.stringify(body) }
      : { method: 'POST', headers: { 'Content-Type': 'application/json', ...headers }, body: JSON.stringify(body), mode: 'cors' };
    const r = await fetch(url, opts);
    const t = await r.text();
    let j = null;
    try { j = t ? JSON.parse(t) : null; } catch { /* noop */ }
    if (!r.ok) {
      const e = new Error(j?.message || j?.error || t || ('HTTP ' + r.status));
      e.status = r.status; e.payload = j;
      throw e;
    }
    return j ?? {};
  }

  function uuid() {
    return (crypto.randomUUID ? crypto.randomUUID() : (Date.now() + '-' + Math.random().toString(16).slice(2)));
  }

  // Элементы формы
  const bookingModal = $('#booking-modal');
  const form = $('#bookingForm');
  const titleEl = bookingModal?.querySelector('.booking-modal__title');

  const hid = $('#bookingServiceId');
  const nameI = $('#bookingName');
  const phoneI = $('#bookingPhone');
  const durSel = $('#bookingDuration');
  const dateI = $('#bookingDate');
  const timeSel = $('#bookingTime');
  const commI = $('#bookingComment');
  const cancelBtn = $('#bookingCancel');
  const submitBtn = $('#submitBooking');

  // Сетка времени 10:00..22:00 шаг 15 мин
  const GRID = (() => {
    const out = [], d = new Date(), e = new Date();
    d.setHours(10, 0, 0, 0); e.setHours(22, 0, 0, 0);
    while (d < e) { out.push(d.toTimeString().slice(0, 5)); d.setMinutes(d.getMinutes() + 15); }
    return out;
  })();

  function overlaps(aStart, aEnd, bStart, bEnd) {
    return new Date(aStart) < new Date(bEnd) && new Date(bStart) < new Date(aEnd);
  }

  // Блокировка скролла и фокус-трап для формы
  let lock2 = false, scrollY2 = 0, prevFocus = null;
  function lockScroll() {
    if (lock2) return;
    scrollY2 = window.scrollY || 0;
    prevFocus = document.activeElement;
    Object.assign(document.body.style, { position: 'fixed', top: `-${scrollY2}px`, left: 0, right: 0, width: '100%' });
    lock2 = true;
    bookingModal?.addEventListener('keydown', focusTrap);
  }
  function unlockScroll() {
    if (!lock2) return;
    Object.assign(document.body.style, { position: '', top: '', left: '', right: '' });
    window.scrollTo(0, scrollY2);
    lock2 = false;
    bookingModal?.removeEventListener('keydown', focusTrap);
    try { prevFocus && prevFocus.focus(); } catch { }
  }
  function focusTrap(e) {
    if (e.key !== 'Tab') return;
    const FOCUSABLE = bookingModal?.querySelectorAll('button,[href],input,select,textarea,[tabindex]:not([tabindex="-1"])') || [];
    const list = Array.from(FOCUSABLE).filter(el => !el.hasAttribute('disabled'));
    if (!list.length) return;
    const first = list[0], last = list[list.length - 1];
    if (e.shiftKey && document.activeElement === first) { last.focus(); e.preventDefault(); }
    else if (!e.shiftKey && document.activeElement === last) { first.focus(); e.preventDefault(); }
  }

  // Маска телефона +7 (9xx) xxx-xx-xx
  const RU = /^\+7\s?\(9\d{2}\)\s?\d{3}-\d{2}-\d{2}$/;
  function digits(s) { return (s || '').replace(/\D/g, ''); }
  function fmtPhone(raw) {
    let d = digits(raw);
    if (d.startsWith('8')) d = '7' + d.slice(1);
    if (d.startsWith('9')) d = '79' + d.slice(1);
    if (!d.startsWith('7')) d = '7' + d;
    d = d.slice(0, 11);
    const p = (i) => d[i] || '';
    let out = `+${p(0)} (${p(1)}${p(2)}${p(3)}) ${p(4)}${p(5)}${p(6)}`;
    if (p(7)) out += `-${p(7)}${p(8)}`;
    if (p(9)) out += `-${p(9)}${p(10)}`;
    return out.replace('( )', '(9)').trim();
  }
  phoneI?.addEventListener('input', () => {
    const old = phoneI.value;
    phoneI.value = fmtPhone(old);
    try { phoneI.setSelectionRange(phoneI.value.length, phoneI.value.length); } catch { }
  });
  phoneI?.addEventListener('blur', () => setErr(phoneI, RU.test(phoneI.value.trim()) ? '' : 'Формат: +7 (9xx) xxx-xx-xx'));
  if (phoneI && !phoneI.value) phoneI.value = '+7 (9';

  // Ошибки/валидация
  function setErr(el, msg) {
    if (!form || !el) return;
    const box = form.querySelector(`.error[data-for="${el.name}"]`);
    if (box) box.textContent = msg || '';
    el.classList.toggle('invalid', !!msg);
  }
  function valid() {
    let ok = true;
    setErr(nameI, ''); setErr(phoneI, ''); setErr(durSel, ''); setErr(dateI, ''); setErr(timeSel, '');
    if (!nameI?.value.trim()) { setErr(nameI, 'Укажите имя'); ok = false; }
    if (!RU.test(phoneI?.value.trim() || '')) { setErr(phoneI, 'Формат: +7 (9xx) xxx-xx-xx'); ok = false; }
    if (!durSel?.value) { setErr(durSel, 'Выберите длительность'); ok = false; }
    if (!dateI?.value) { setErr(dateI, 'Выберите дату'); ok = false; }
    if (!timeSel?.value) { setErr(timeSel, 'Выберите время'); ok = false; }
    return ok;
  }

  // Загрузка длительностей — ВАЖНО: корректный URL
  async function loadDurations(serviceId) {
    if (!durSel) return;
    durSel.innerHTML = '<option value="" selected>Загрузка…</option>';
    const list = await apiGET(`${API_BASE}/api/services/${serviceId}/durations`);
    durSel.innerHTML = '<option value="" selected disabled>Выберите длительность…</option>';
    const data = (list || []).sort((a, b) =>
      (a.sortOrder ?? 100) - (b.sortOrder ?? 100) || (a.durationMin ?? 0) - (b.durationMin ?? 0)
    );
    if (!data.length) {
      const opt = new Option('Для этой услуги пока нет длительностей', '');
      opt.disabled = true;
      durSel.appendChild(opt);
      return;
    }
    data.forEach(d => {
      const label = d.priceCents != null
        ? `${d.durationMin} мин — ${(d.priceCents / 100).toFixed(0)} ₽`
        : `${d.durationMin} мин`;
      const opt = new Option(label, String(d.id));
      opt.dataset.durationMin = d.durationMin;
      durSel.appendChild(opt);
    });
  }

  // Отрисовка времени (учёт занятости + прошлого времени)
  // Отрисовка времени (учёт занятости + прошлого времени)
  async function renderTimes(serviceId) {
    if (!timeSel || !dateI || !durSel) return;
    timeSel.innerHTML = '';
    if (!dateI.value || !durSel.value) return;

    // 1) тянем занятость и нормализуем названия полей
    const rawBusy = await apiGET(`${API_BASE}/api/slots/busy?serviceId=${serviceId}&date=${dateI.value}`) || [];
    const busy = rawBusy
      .map(b => ({
        start: b.start ?? b.startTime ?? b.begin ?? b.from ?? null,
        end:   b.end   ?? b.endTime   ?? b.finish ?? b.to   ?? null
      }))
      .filter(b => b.start && b.end);

    // 2) лог на всякий случай (один раз на дату)
    console.debug('[busy]', dateI.value, busy.slice(0, 5));

    // 3) длительность из выбранной опции
    const minutes = Number(durSel.selectedOptions[0]?.dataset?.durationMin || 60);

    // 4) сегодняшнее прошлое время — недоступно
    const now = new Date();
    const isToday = dateI.value === `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

    // 5) строим сетку
    const disabled = new Set();
    GRID.forEach(t => {
      const start = new Date(`${dateI.value}T${t}:00`);
      const end = new Date(start); end.setMinutes(end.getMinutes() + minutes);

      const past = isToday && end.getTime() <= now.getTime();
      const conflict = busy.some(b => overlaps(start, end, b.start, b.end));

      const opt = new Option(
        t + (conflict ? ' — занято' : (past ? ' — прошло' : '')),
        t,
        false,
        false
      );
      if (conflict || past) {
        opt.disabled = true;
        disabled.add(t);
      }
      timeSel.appendChild(opt);
    });

    const firstFree = GRID.find(x => !disabled.has(x));
    timeSel.value = firstFree || '';
    if (!firstFree) showToast('Свободных слотов на выбранную дату нет');
  }





  // Открытие формы из карточки
  document.body.addEventListener('click', async (e) => {
    const btn = e.target.closest('.service__book-btn');
    if (!btn) return;

    const serviceId = Number(btn.dataset.serviceId || 0);
    if (!serviceId) { showToast('У кнопки нет data-service-id'); return; }

    const t = serviceModalTitle?.textContent?.trim();
    if (titleEl) titleEl.textContent = t || 'Запись на услугу';
    if (hid) hid.value = String(serviceId);

    // Мин. дата — сегодня
    const now = new Date(); const y = now.getFullYear(), m = String(now.getMonth() + 1).padStart(2, '0'), d = String(now.getDate()).padStart(2, '0');
    if (dateI) {
      dateI.min = `${y}-${m}-${d}`;
      if (!dateI.value) dateI.value = dateI.min;
    }

    // Показ формы
    if (serviceModal) serviceModal.hidden = true; // закрываем инфо-модалку
    if (cardOverlay) cardOverlay.hidden = false;
    if (bookingModal) bookingModal.hidden = false;
    lockScroll();

    try {
      await loadDurations(serviceId);
      await renderTimes(serviceId);
      nameI?.focus({ preventScroll: true });
    } catch (err) {
      console.error(err);
      showToast('Не удалось загрузить параметры услуги');
    }
  });

  // Закрытие формы
  function closeBooking() {
    if (bookingModal) bookingModal.hidden = true;
    if (cardOverlay) cardOverlay.hidden = true;
    form?.reset();
    unlockScroll();
  }
  cancelBtn?.addEventListener('click', closeBooking);
  cardOverlay?.addEventListener('click', () => { if (!bookingModal?.hidden) closeBooking(); });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && !bookingModal?.hidden) closeBooking(); });

  // Дебаунс на изменение длительности/даты
  let timr = null;
  function debounce(fn, ms) { return (...a) => { clearTimeout(timr); timr = setTimeout(() => fn(...a), ms); }; }
  const rerenderDebounced = debounce(async (sid) => { if (sid) await renderTimes(sid); }, 120);
  durSel?.addEventListener('change', () => rerenderDebounced(Number(hid?.value || 0)));
  dateI?.addEventListener('change', () => rerenderDebounced(Number(hid?.value || 0)));

  // Сабмит формы
  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!valid()) return;

    const serviceId = Number(hid?.value);
    const serviceDurationId = Number(durSel?.value);
    const startLocal = `${dateI?.value}T${timeSel?.value}`;

    submitBtn?.classList.add('loading');
    if (submitBtn) submitBtn.disabled = true;

    try {
      await apiPOST(`${API_BASE}/api/appointments`, {
        serviceId, serviceDurationId, startLocal,
        customerName: nameI?.value?.trim(),
        customerPhone: phoneI?.value?.trim(),
        comment: commI?.value?.trim()
      }, { 'Idempotency-Key': uuid() });

      showToast('✅ Заявка отправлена. Мы свяжемся с вами.');
      closeBooking();
    } catch (err) {
      if (err.status === 409) {
        showToast('Этот слот уже занят. Обновляю варианты…');
        await renderTimes(serviceId);
        timeSel?.focus();
      } else if (err.status === 400 && err.payload) {
        const map = err.payload?.errors || {};
        if (map.customerName) setErr(nameI, String(map.customerName));
        if (map.customerPhone) setErr(phoneI, String(map.customerPhone));
        if (map.serviceDurationId) setErr(durSel, String(map.serviceDurationId));
        if (map.startLocal) setErr(timeSel, String(map.startLocal));
        showToast('Проверьте поля формы');
      } else {
        console.error(err);
        showToast('Ошибка бронирования: ' + (err.message || 'неизвестно'));
      }
    } finally {
      submitBtn?.classList.remove('loading');
      if (submitBtn) submitBtn.disabled = false;
    }
  });

  /* =================== ДИАГНОСТИКА: подсказка в консоль =================== */
  try {
    console.log('BOHO_API_BASE =', window.BOHO_API_BASE);
  } catch { /* noop */ }
})();



