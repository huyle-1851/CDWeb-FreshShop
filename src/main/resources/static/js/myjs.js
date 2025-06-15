function sort() {
    let combobox = document.getElementById("sort");
    let value = combobox.value;
    switch (value) {
        case "1":
            sortOrderByPriceASC();
            break;
        case "2":
            sortOrderByPriceDESC();
            break;
        case "3":
            sortOrderByNewStatus();
            break;
        case "4":
            sortOrderBySaleStatus();
            break;
        default:
            break;
    }
}

function getList() {
    let id = document.getElementsByClassName("id-param");
    let name = document.getElementsByClassName("name-param");
    let price = document.getElementsByClassName("price-param");
    let image = document.getElementsByClassName("image-param");
    let status = document.getElementsByClassName("status-param");

    let list = [];
    for (let i = 0; i < id.length; i++) {
        let product = {};
        product.id = id[i].getAttribute("href");
        product.name = name[i].innerHTML;
        product.price = price[i].innerHTML;
        product.image = image[i].getAttribute("src");
        product.status = status[i].innerHTML;

        list[i] = product;
    }
    return list;
}

function transform(list) {
    let id = document.getElementsByClassName("id-param");
    let name = document.getElementsByClassName("name-param");
    let price = document.getElementsByClassName("price-param");
    let image = document.getElementsByClassName("image-param");
    let status = document.getElementsByClassName("status-param");

    for (let i = 0; i < list.length; i++) {
        id[i].setAttribute("href", list[i].id);
        name[i].innerHTML = list[i].name;
        price[i].innerHTML = list[i].price;
        image[i].setAttribute("src", list[i].image);
        status[i].innerHTML = list[i].status;
    }
}

function sortOrderByPriceASC() {
    let list = getList();
    list = priceACS(list);
    transform(list);
}

function sortOrderByPriceDESC() {
    let list = getList();
    list = priceDECS(list);
    transform(list);
}

function sortOrderByNewStatus() {
    let list = getList();
    list = sortByStatus(list, "mới");
    transform(list);
}

function sortOrderBySaleStatus() {
    let list = getList();
    list = sortByStatus(list, "giảm giá");
    transform(list);
}

function priceACS(list) {
    let clone = list;
    for (let i = 0; i < clone.length - 1; i++) {
        for (let j = i + 1; j < clone.length; j++) {
            if (Number.parseInt(clone[i].price) > Number.parseInt(clone[j].price)) {
                let tmp = clone[i];
                clone[i] = clone[j];
                clone[j] = tmp;
            }
        }
    }
    list = clone;
    return list;
}

function priceDECS(list) {
    let clone = list;
    for (let i = 0; i < clone.length - 1; i++) {
        for (let j = i + 1; j < clone.length; j++) {
            if (Number.parseInt(clone[i].price) < Number.parseInt(clone[j].price)) {
                let tmp = clone[i];
                clone[i] = clone[j];
                clone[j] = tmp;
            }
        }
    }
    list = clone;
    return list;
}

function sortByStatus(list, status) {
    let clone = list;
    for (let i = 0; i < clone.length - 1; i++) {
        for (let j = i + 1; j < clone.length; j++) {
            let i_status = clone[i].status.toLowerCase().trim();
            let j_status = clone[j].status.toLowerCase().trim();
            let be_compared = status.toLowerCase().trim();
            if (i_status != be_compared && j_status == be_compared) {
                let tmp = clone[i];
                clone[i] = clone[j];
                clone[j] = tmp;
            }
        }
    }
    list = clone;
    return list;
}

// Cart functionality
function updateCart(productId, quantity = 1, cumulative = true) {
    fetch('/add-item', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `idP=${productId}&quan=${quantity}&cumulative=${cumulative}`
    })
    .then(response => response.text())
    .then(data => {
        console.log('Cart updated:', data);
        // Update cart badge and dropdown if visible
        updateCartBadge();
        if (cartDropdownVisible) {
            loadCartItems();
        }
    })
    .catch(error => {
        console.error('Error updating cart:', error);
    });
}

function removeFromCart(productId) {
    fetch('/remove-item-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `idP=${productId}`
    })
    .then(response => response.text())
    .then(data => {
        console.log('Item removed from cart:', data);
        location.reload(); // Reload page to update cart display
    })
    .catch(error => {
        console.error('Error removing item from cart:', error);
    });
}

function clearCart() {
    if (!confirm('Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi giỏ hàng?')) {
        return;
    }

    fetch('/clear-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log('Cart cleared:', data);
        // Update cart dropdown if visible
        if (cartDropdownVisible) {
            loadCartItems();
        }
        updateCartBadge();
        showCartMessage('Đã xóa tất cả sản phẩm khỏi giỏ hàng!', 'success');
    })
    .catch(error => {
        console.error('Error clearing cart:', error);
        showCartMessage('Có lỗi xảy ra khi xóa giỏ hàng!', 'error');
    });
}

function updateCartBadge() {
    // Fetch current cart count from server
    fetch('/cart-count', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        // Update cart badge in header
        const cartBadge = document.querySelector('#cart-badge');
        if (cartBadge && data.count !== undefined) {
            cartBadge.textContent = data.count;
        }

        // Also update the old badge selector for backward compatibility
        const oldCartBadge = document.querySelector('.attr-nav .badge');
        if (oldCartBadge && data.count !== undefined) {
            oldCartBadge.textContent = data.count;
        }
    })
    .catch(error => {
        console.error('Error updating cart badge:', error);
    });
}

// Quick add to cart function for shop pages
function quickAddToCart(productId, quantity = 1) {
    updateCart(productId, quantity, true);

    // Show a brief success message
    showCartMessage('Đã thêm vào giỏ hàng!', 'success');
}

// Show cart message function
function showCartMessage(message, type = 'info') {
    // Create message element
    const messageDiv = document.createElement('div');

    // Determine alert class based on type
    let alertClass = 'alert-info';
    let bgColor = '#d1ecf1';
    let textColor = '#0c5460';

    if (type === 'success') {
        alertClass = 'alert-success';
        bgColor = '#d4edda';
        textColor = '#155724';
    } else if (type === 'error') {
        alertClass = 'alert-danger';
        bgColor = '#f8d7da';
        textColor = '#721c24';
    }

    messageDiv.className = `alert ${alertClass} cart-message`;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        padding: 12px 20px;
        border-radius: 5px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        background-color: ${bgColor};
        color: ${textColor};
        border: 1px solid ${textColor}33;
        min-width: 250px;
        font-weight: 500;
        animation: slideInRight 0.3s ease-out;
        font-family: Arial, sans-serif;
        font-size: 14px;
        line-height: 1.4;
    `;
    messageDiv.innerHTML = `<strong>${type === 'success' ? 'Thành công!' : type === 'error' ? 'Lỗi!' : 'Thông báo!'}</strong> ${message}`;

    // Add to page
    document.body.appendChild(messageDiv);

    // Remove after 3 seconds
    setTimeout(() => {
        messageDiv.style.animation = 'slideOutRight 0.3s ease-in';
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.parentNode.removeChild(messageDiv);
            }
        }, 300);
    }, 3000);
}

// Cart dropdown functionality
let cartDropdownVisible = false;

function toggleCartDropdown() {
    console.log('toggleCartDropdown called');
    testCartDropdown();
}

// Ultra simple test functions for cart dropdown
function showTestDropdown() {
    console.log('showTestDropdown called');
    alert('showTestDropdown function called!');

    const dropdown = document.getElementById('cart-dropdown');
    console.log('Looking for cart-dropdown element...');

    if (!dropdown) {
        console.log('ERROR: Cart dropdown element not found!');
        alert('ERROR: Cart dropdown element not found!');
        return;
    }

    console.log('Cart dropdown element found! Showing...');
    dropdown.style.display = 'block';
    alert('Dropdown should now be visible!');
}

function hideTestDropdown() {
    console.log('hideTestDropdown called');
    const dropdown = document.getElementById('cart-dropdown');

    if (dropdown) {
        dropdown.style.display = 'none';
        console.log('Cart dropdown hidden');
    } else {
        console.log('Cart dropdown element not found for hiding');
    }
}

// Legacy function names for compatibility
function testCartDropdown() {
    showTestDropdown();
}

function showCartDropdown() {
    showTestDropdown();
}

function toggleCartDropdown() {
    showTestDropdown();
}

function hideCartDropdown() {
    const dropdown = document.getElementById('cart-dropdown');
    if (!dropdown) return;

    console.log('Hiding cart dropdown...');

    cartDropdownVisible = false;

    // Hide dropdown with simple inline style override
    dropdown.style.display = 'none';
    dropdown.style.opacity = '0';
    dropdown.style.transform = 'translateY(-10px)';
    dropdown.style.pointerEvents = 'none';
    dropdown.style.visibility = 'hidden';

    document.removeEventListener('click', handleOutsideClick);
}

function handleOutsideClick(event) {
    const cartDropdown = document.querySelector('.cart-dropdown');
    if (cartDropdown && !cartDropdown.contains(event.target)) {
        hideCartDropdown();
    }
}

function loadCartItems() {
    const container = document.getElementById('cart-items-container');
    const loading = document.getElementById('cart-loading');

    if (!container) {
        console.log('Cart items container not found');
        return;
    }

    // Show loading
    if (loading) {
        loading.style.display = 'block';
    }

    // Fetch cart items
    fetch('/api/cart/items', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (loading) {
            loading.style.display = 'none';
        }
        renderCartItems(data);
    })
    .catch(error => {
        console.error('Error loading cart items:', error);
        if (loading) {
            loading.style.display = 'none';
        }
        if (container) {
            container.innerHTML = '<div class="cart-empty">Có lỗi xảy ra khi tải giỏ hàng</div>';
        }
    });
}

function renderCartItems(data) {
    const container = document.getElementById('cart-items-container');
    const totalElement = document.getElementById('cart-total-amount');
    const countElement = document.getElementById('cart-count-text');

    if (!container) {
        console.log('Cart items container not found in renderCartItems');
        return;
    }

    if (!data.items || data.items.length === 0) {
        container.innerHTML = '<div class="cart-empty">Giỏ hàng trống</div>';
        if (totalElement) {
            totalElement.textContent = '0đ';
        }
        if (countElement) {
            countElement.textContent = '0 sản phẩm';
        }
        return;
    }

    // Render cart items
    const itemsHtml = data.items.map(item => {
        const product = item.product;
        const imageUrl = (product.images && product.images.length > 0)
            ? product.images[0].url
            : '/images/Tao Fuji Newzealand - 1kg.jpg';

        const price = product.discountedPrice > 0 && product.discountedPrice !== product.price
            ? product.discountedPrice
            : product.price;

        return `
            <div class="cart-item">
                <img src="${imageUrl}" alt="${product.name}" class="cart-item-image">
                <div class="cart-item-details">
                    <div class="cart-item-name">${product.name}</div>
                    <div class="cart-item-price">${price}đ</div>
                    <div class="cart-item-quantity">Số lượng: ${item.quantity}</div>
                </div>
                <button class="cart-item-remove" onclick="removeFromCartDropdown(${product.id})" title="Xóa">
                    <i class="fa fa-times"></i>
                </button>
            </div>
        `;
    }).join('');

    container.innerHTML = itemsHtml;
    if (totalElement) {
        totalElement.textContent = data.total + 'đ';
    }
    if (countElement) {
        countElement.textContent = data.items.length + ' sản phẩm';
    }
}

function removeFromCartDropdown(productId) {
    fetch('/remove-item-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `idP=${productId}`
    })
    .then(response => response.text())
    .then(data => {
        console.log('Item removed from cart:', data);
        // Reload cart items
        loadCartItems();
        updateCartBadge();
        showCartMessage('Đã xóa sản phẩm khỏi giỏ hàng!', 'success');
    })
    .catch(error => {
        console.error('Error removing item from cart:', error);
        showCartMessage('Có lỗi xảy ra khi xóa sản phẩm!', 'error');
    });
}

// Initialize cart functionality on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('myjs.js loaded successfully!');

    updateCartBadge();

    // Test cart dropdown elements
    const cartDropdown = document.getElementById('cart-dropdown');
    const cartToggle = document.getElementById('cart-toggle');

    console.log('Cart dropdown element:', cartDropdown);
    console.log('Cart toggle element:', cartToggle);

    if (cartDropdown && cartToggle) {
        console.log('✅ Cart dropdown elements found!');
        cartDropdownVisible = false;
        cartDropdown.style.display = 'none';
    } else {
        console.log('❌ Cart dropdown elements not found on this page');
        if (!cartDropdown) console.log('Missing: cart-dropdown');
        if (!cartToggle) console.log('Missing: cart-toggle');
    }
});