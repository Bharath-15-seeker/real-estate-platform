// API Configuration
const API_BASE_URL = "http://localhost:8081/api";

// Utility Functions
function getAuthToken() {
    return localStorage.getItem('jwt_token');
}

function isAuthenticated() {
    const token = getAuthToken();
    if (!token) return false;
    
    try {
        // Basic token validation (check if it's expired)
        const payload = JSON.parse(atob(token.split('.')[1]));
        const now = Date.now() / 1000;
        return payload.exp > now;
    } catch (error) {
        // Invalid token format
        localStorage.removeItem('jwt_token');
        return false;
    }
}

function logout() {
    localStorage.removeItem('jwt_token');
    showMessage('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1000);
}

function updateNavigation() {
    const authButtons = document.getElementById('authButtons');
    const userMenu = document.getElementById('userMenu');
    
    if (isAuthenticated()) {
        if (authButtons) authButtons.style.display = 'none';
        if (userMenu) userMenu.style.display = 'flex';
    } else {
        if (authButtons) authButtons.style.display = 'flex';
        if (userMenu) userMenu.style.display = 'none';
    }
}

// HTTP Request Function
async function makeRequest(endpoint, method = 'GET', data = null, requireAuth = true, isMultipart = false) {
    const url = `${API_BASE_URL}${endpoint}`;

    const options = { method, headers: {} };

    // Add auth header if needed
    if (requireAuth && isAuthenticated()) {
        options.headers['Authorization'] = `Bearer ${getAuthToken()}`;
    }

    // Handle body
    if (data && (method === 'POST' || method === 'PUT')) {
        if (isMultipart) {
            // ✅ Don't set Content-Type manually (browser will do it)
            options.body = data;
        } else {
            options.headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(data);
        }
    }

    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('jwt_token');
                showMessage('Session expired. Please login again.', 'error');
                setTimeout(() => { window.location.href = 'login.html'; }, 2000);
                throw new Error('Unauthorized');
            }

            let errorMessage = `HTTP ${response.status}: ${response.statusText}`;
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || errorMessage;
            } catch (e) {}
            throw new Error(errorMessage);
        }

        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('Request failed:', error);
        if (!navigator.onLine) {
            throw new Error('No internet connection.');
        }
        throw error;
    }
}


// Message Display Functions
function showMessage(message, type = 'info') {
    const container = document.getElementById('messageContainer');
    if (!container) return;
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;
    
    container.appendChild(messageDiv);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.parentNode.removeChild(messageDiv);
        }
    }, 5000);
    
    // Allow manual removal by clicking
    messageDiv.addEventListener('click', () => {
        if (messageDiv.parentNode) {
            messageDiv.parentNode.removeChild(messageDiv);
        }
    });
}

// Form Validation Functions
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePassword(password) {
    return password.length >= 6;
}

function clearFormErrors() {
    const errorElements = document.querySelectorAll('.form-error');
    errorElements.forEach(el => el.textContent = '');
}

function showFormError(fieldId, message) {
    const errorElement = document.getElementById(fieldId + 'Error');
    if (errorElement) {
        errorElement.textContent = message;
    }
}

// Image Handling Functions
function handleImageUpload(inputElement, callback) {
    const files = inputElement.files;
    if (!files || files.length === 0) {
        callback([]);
        return;
    }
    
    const imageUrls = [];
    let processed = 0;
    
    Array.from(files).forEach((file, index) => {
        // Validate file type
        if (!file.type.startsWith('image/')) {
            showMessage(`File ${file.name} is not an image`, 'error');
            processed++;
            if (processed === files.length) callback(imageUrls);
            return;
        }
        
        // Validate file size (max 10MB)
        if (file.size > 10 * 1024 * 1024) {
            showMessage(`File ${file.name} is too large (max 10MB)`, 'error');
            processed++;
            if (processed === files.length) callback(imageUrls);
            return;
        }
        
        // For now, we'll just create a placeholder URL
        // In a real implementation, you would upload to a file storage service
        const reader = new FileReader();
        reader.onload = (e) => {
            imageUrls.push(e.target.result);
            processed++;
            if (processed === files.length) {
                callback(imageUrls);
            }
        };
        reader.readAsDataURL(file);
    });
}

// Utility Functions for Data Formatting
function formatPrice(price) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(price);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function truncateText(text, maxLength = 100) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// Loading State Management
function setButtonLoading(buttonElement, isLoading) {
    const btnText = buttonElement.querySelector('.btn-text');
    const btnLoading = buttonElement.querySelector('.btn-loading');
    
    if (btnText && btnLoading) {
        if (isLoading) {
            btnText.style.display = 'none';
            btnLoading.style.display = 'inline-block';
            buttonElement.disabled = true;
        } else {
            btnText.style.display = 'inline-block';
            btnLoading.style.display = 'none';
            buttonElement.disabled = false;
        }
    }
}

function showLoadingSpinner(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="loading">
                <i class="fas fa-spinner fa-spin"></i> Loading...
            </div>
        `;
    }
}

function hideLoadingSpinner(containerId) {
    const loadingElement = document.querySelector(`#${containerId} .loading`);
    if (loadingElement) {
        loadingElement.style.display = 'none';
    }
}

// Search and Filter Functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Local Storage Helpers
function saveToLocalStorage(key, data) {
    try {
        localStorage.setItem(key, JSON.stringify(data));
    } catch (error) {
        console.error('Failed to save to localStorage:', error);
    }
}

function getFromLocalStorage(key, defaultValue = null) {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
        console.error('Failed to get from localStorage:', error);
        return defaultValue;
    }
}
// Image URL Builder
function getImageUrl(path) {
    if (!path) return '';
    // Backend sends relative path like "/uploads/abc.jpg"
    if (path.startsWith('/uploads')) {
        return `http://localhost:8081${path}`;
    }
    return path; // In case backend already sends full URL
}

// URL Parameter Helpers
function getUrlParameter(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

function updateUrlParameter(name, value) {
    const url = new URL(window.location);
    if (value) {
        url.searchParams.set(name, value);
    } else {
        url.searchParams.delete(name);
    }
    window.history.replaceState({}, '', url);
}

// Modal Functions
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

// Event Delegation for Dynamic Content
function addEventListenerToParent(parentSelector, childSelector, eventType, handler) {
    const parent = document.querySelector(parentSelector);
    if (parent) {
        parent.addEventListener(eventType, function(event) {
            const target = event.target.closest(childSelector);
            if (target) {
                handler.call(target, event);
            }
        });
    }
}

// Initialize common functionality
document.addEventListener('DOMContentLoaded', function() {
    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (event.target === modal) {
                modal.style.display = 'none';
                document.body.style.overflow = 'auto';
            }
        });
    });
    
    // Close modals with Escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            const visibleModals = document.querySelectorAll('.modal[style*="display: block"]');
            visibleModals.forEach(modal => {
                modal.style.display = 'none';
                document.body.style.overflow = 'auto';
            });
        }
    });
    
    // Handle form submissions with loading states
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            const submitButton = form.querySelector('button[type="submit"]');
            if (submitButton && !submitButton.dataset.handledManually) {
                setButtonLoading(submitButton, true);
                
                // Reset loading state after a reasonable time if not handled manually
                setTimeout(() => {
                    setButtonLoading(submitButton, false);
                }, 30000);
            }
        });
    });
});

// Global error handler for unhandled promise rejections
window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled promise rejection:', event.reason);
    showMessage('An unexpected error occurred. Please try again.', 'error');
});

// Global error handler for JavaScript errors
window.addEventListener('error', function(event) {
    console.error('JavaScript error:', event.error);
    // Don't show error message for every JS error as it might be too noisy
});

// Export functions for use in other scripts (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        makeRequest,
        isAuthenticated,
        showMessage,
        formatPrice,
        formatDate,
        debounce
    };
}

function initCarousel() {
  const track = document.querySelector('.carousel-track');
  if (!track) return; // no carousel if only 1 image

  const slides = Array.from(track.children);
  const prevButton = document.querySelector('.carousel-btn.prev');
  const nextButton = document.querySelector('.carousel-btn.next');
  let currentIndex = 0;

  function updateCarousel() {
    track.style.transform = `translateX(-${currentIndex * 100}%)`;
  }

  if (nextButton) {
    nextButton.addEventListener('click', () => {
      currentIndex = (currentIndex + 1) % slides.length;
      updateCarousel();
    });
  }

  if (prevButton) {
    prevButton.addEventListener('click', () => {
      currentIndex = (currentIndex - 1 + slides.length) % slides.length;
      updateCarousel();
    });
  }
  // 🔥 Autoplay every 5 seconds
  setInterval(() => {
    currentIndex = (currentIndex + 1) % slides.length;
    updateCarousel();
  }, 10000);
}

// Call after portfolio loads
setTimeout(initCarousel, 500);


// Add review submission
const reviewForm = document.getElementById('reviewForm');
if (reviewForm) {
    reviewForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const rating = document.getElementById('rating').value;
        const comment = document.getElementById('comment').value;

        const requestBody = {
            portfolioId: portfolioId,
            rating: parseInt(rating),
            comment: comment
        };

        try {
            await makeRequest('/api/reviews', 'POST', requestBody, true);
            showMessage("Review added successfully!", "success");
            loadPortfolioDetail(); // reload reviews
        } catch (err) {
            showMessage("Failed to add review. Please try again.", "error");
        }
    });
}

