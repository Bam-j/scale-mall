//detailPage.html에서 사용되는 재고 갱신 스크립트
(() => {
  const wrapper = document.querySelector('.item-detail-wrapper');
  if (!wrapper) {
    return;
  }

  const id = wrapper.getAttribute('data-product-id');
  const btn = document.querySelector('.purchase-button');
  const stockBadge = document.querySelector('#stock-badge');

  const POLL_INTERVAL = 3000;

  async function fetchStock() {
    const res = await fetch(`/api/products/${id}/stock`, {cache: 'no-store'});

    if (!res.ok) {
      return {stock: 0, soldOut: true};
    }

    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) {
      return res.json();
    } else {
      const text = await res.text();
      const stock = Number(text);
      return {stock, soldOut: stock <= 0};
    }
  }

  function applyStockToUI(data) {
    const {stock, soldOut} = data;
    if (stockBadge) {
      stockBadge.textContent = `재고: ${stock}`;
    }

    if (soldOut) {
      btn.disabled = true;
      btn.textContent = '품절';
    } else {
      btn.disabled = false;
      btn.textContent = '구매하기';
    }
  }

  async function refreshStock() {
    try {
      const data = await fetchStock();
      applyStockToUI(data);
    } catch (e) {
      btn.disabled = true;
      btn.textContent = '네트워크 오류';
    }
  }

  refreshStock();
  const timer = setInterval(refreshStock, POLL_INTERVAL);

  window.addEventListener('beforeunload', () => clearInterval(timer));

  btn.addEventListener('click', async () => {
    try {
      const resp = await fetch('/api/products/purchase', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({productId: Number(id)})
      });
      const msg = await resp.text();
      alert(msg);
    } finally {
      await refreshStock();
    }
  });
})();
