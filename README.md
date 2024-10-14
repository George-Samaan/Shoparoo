# Shoparoo 🛒

## Team Members
- **George Michel Louis** (Set 1)
- **Galal Ahmed Galal**   (Set 2)
- **Ahmed Gamal Mahmoud** (Set 3)

---

### Trello Link
[Shopify Team 3 Trello Board](https://trello.com/b/uO8iNMDy/shoparoo)

---

## Features

### Product Management
- **Browse Products**: Discover a wide range of products with detailed descriptions, images, and prices.
- **Search**: Effortlessly find products using intuitive keyword search and filters.
- **Product Categories**: Easily explore products organized into distinct categories.

### Shopping Cart
- **Add to Cart**: Seamless product addition to your shopping cart.
- **Cart Management**: Modify, view, or remove items from your cart with ease.
- **Checkout**: Smooth and secure checkout process.
- **Applying Coupons**: Enjoy discounts by applying coupons to products in the cart.

### User Capabilities
- **Login/Sign-Up**: Register and log in securely.
- **User Profile**: Manage personal information and preferences.
- **Currency Selection**: Pick your preferred currency for purchases.
- **Guest Mode**: Access select features without the need to log in.
- **Address Management**: Store and manage multiple shipping addresses.

### Brands Browsing
- Explore a variety of products organized by brand.

### Favorites Management
- Save products to your favorites list for easy access later.

---

## Technical Details

### Architecture
- **MVVM (Model-View-ViewModel)**: Employs clean architecture for separation of concerns and better maintainability.
- **Repository Pattern:** Centralizes data access logic by providing a clean API for data operations.

### Asynchronous Programming
- **Kotlin Coroutines & Flow**: Manage asynchronous data operations efficiently, ensuring smooth user interactions.
- **StateFlow**: Lifecycle-aware, ensuring the UI reacts seamlessly to changes in app state.

### Image Loading
- **Coil**: Efficient, high-performance image loading library.

### Networking
- **Retrofit**: Streamlines network communication for making API requests.

### Location Services
- **Google Location Services**: For retrieving users’ current location.
- **Geocoder**: Converts location data into human-readable addresses.

### UI & Design
- **Google Material Design**: Provides modern, intuitive UI components.
- **Navigation Component**: Simplifies app navigation across different screens.
- **Jetpack Compose**: Uses declarative UI for building a dynamic and responsive user interface.
- **Accompanist Pager & Indicators**: Enhances the browsing experience with sliders and visual indicators.
- **SwipeToRefresh**: Adds pull-to-refresh functionality.
- **Lottie Animations**: Creates engaging, lightweight animations.

### Additional Libraries and Tools
- **OkHttp**: Handles HTTP requests and logging for easy debugging.
- **TapTargetView**: Provides on-screen instructions through interactive highlights.
- **Firebase**: Provides robust authentication.
- **Testing**: Comprehensive unit testing setup using JUnit, Robolectric, and Mockk.

---
