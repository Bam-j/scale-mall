//detailPage.html에서 사용되는 구매 요청 전송 및 처리 스크립트
(function () {
  const wrapper = document.querySelector('.item-detail-wrapper');
  if (!wrapper) {
    return;
  }

  const productId = wrapper.getAttribute('data-product-id');
  const $btn = document.querySelector('.purchase-button');

  function getCookie(name) {
    const matched = document.cookie.match(
        new RegExp('(?:^|; )' + name + '=([^;]*)'));
    return matched ? decodeURIComponent(matched[1]) : null;
  }

  function setCookie(name, value, days) {
    const expires = new Date(Date.now() + days * 86400 * 1000).toUTCString();
    document.cookie = `${name}=${encodeURIComponent(
        value)}; Expires=${expires}; Path=/`;
  }

  function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
      const r = crypto.getRandomValues(new Uint8Array(1))[0] & 15;
      const v = c === 'x' ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    });
  }

  function getOrCreateClientId() {
    let cid = getCookie('cid');
    if (!cid) {
      cid = uuidv4();
      setCookie('cid', cid, 30);
    }
    return cid;
  }

  const clientId = getOrCreateClientId();

  async function purchase(productId, clientId) {
    const res = await fetch('/api/products/purchase', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Client-Id': clientId
      },
      body: JSON.stringify({id: Number(productId)})
    });

    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) {
      const body = await res.json();
      alert(body.message || (res.ok ? '구매 성공' : '구매 실패'));
    } else {
      const text = await res.text();
      alert(text || (res.ok ? '구매 성공' : '구매 실패'));
    }
  }

  $btn.addEventListener('click', async () => {
    try {
      await purchase(productId, clientId);
    } catch (e) {
      alert('구매 요청 중 오류가 발생했습니다.');
    } finally {

      if (typeof window.refreshStock === 'function') {
        await window.refreshStock();
      }
    }
  });
})();
