# API Documentation - Tech Mart

## Tài liệu API cho Tech Mart Backend

## Mục lục
- [Xác thực người dùng](#xác-thực-người-dùng)
- [Quản lý người dùng](#quản-lý-người-dùng)  
- [Quản lý sản phẩm](#quản-lý-sản-phẩm)
- [Quản lý danh mục](#quản-lý-danh-mục)
- [Quản lý giỏ hàng](#quản-lý-giỏ-hàng)
- [Quản lý đơn hàng](#quản-lý-đơn-hàng)
- [Mã giảm giá](#mã-giảm-giá)
- [Đánh giá sản phẩm](#đánh-giá-sản-phẩm)

## Xác thực người dùng

### Đăng nhập
**Endpoint:** `POST /auth/signin`

**Mô tả:** Xác thực người dùng và tạo JWT token

**Request:**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

### Đăng ký tài khoản
**Endpoint:** `POST /auth/signup`

**Mô tả:** Đăng ký tài khoản người dùng mới

**Request:**
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "phone": "0123456789",
  "address": "123 Đường ABC, Quận XYZ, Hà Nội",
  "gender": "MALE",
  "roles": ["ROLE_USER"]
}
```

**Response:**
```json
{
  "message": "User registered successfully!"
}
```

### Quên mật khẩu
**Endpoint:** `POST /auth/forgot-password`

**Mô tả:** Gửi email khôi phục mật khẩu

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "message": "Password reset email sent successfully"
}
```

### Đặt lại mật khẩu
**Endpoint:** `POST /auth/reset-password`

**Mô tả:** Đặt lại mật khẩu bằng token

**Request:**
```json
{
  "token": "reset-token-value",
  "password": "newpassword123"
}
```

**Response:**
```json
{
  "message": "Password reset successfully"
}
```

### Đổi mật khẩu
**Endpoint:** `POST /auth/change-password`

**Mô tả:** Đổi mật khẩu khi đã đăng nhập

**Request:**
```json
{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123"
}
```

**Response:**
```json
{
  "message": "Password changed successfully"
}
```

### Làm mới token
**Endpoint:** `POST /auth/refresh-token`

**Mô tả:** Làm mới JWT token

**Request:**
```json
{
  "refreshToken": "refresh-token-value"
}
```

**Response:**
```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

### Đăng xuất
**Endpoint:** `POST /auth/logout`

**Mô tả:** Đăng xuất và vô hiệu hóa token

**Response:**
```json
{
  "message": "Logged out successfully"
}
```

## Quản lý sản phẩm

### Lấy danh sách sản phẩm
**Endpoint:** `GET /api/products`

**Mô tả:** Lấy danh sách tất cả sản phẩm với phân trang

**Tham số:**
- `page` (mặc định: 0): Số trang
- `size` (mặc định: 10): Số sản phẩm trên mỗi trang
- `sortBy` (mặc định: "id"): Trường sắp xếp
- `sortDir` (mặc định: "asc"): Hướng sắp xếp (asc/desc)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 13",
      "description": "Apple iPhone 13 128GB",
      "price": 24990000,
      "stockQuantity": 100,
      "imageUrl": "iphone-13.jpg",
      "categoryId": 1,
      "categoryName": "Smartphones",
      "averageRating": 4.5
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "last": false
}
```

### Lấy thông tin chi tiết sản phẩm
**Endpoint:** `GET /api/products/{id}`

**Mô tả:** Lấy thông tin chi tiết của một sản phẩm

**Response:**
```json
{
  "id": 1,
  "name": "iPhone 13",
  "description": "Apple iPhone 13 128GB",
  "price": 24990000,
  "stockQuantity": 100,
  "imageUrl": "iphone-13.jpg",
  "categoryId": 1,
  "categoryName": "Smartphones",
  "averageRating": 4.5,
  "specifications": {
    "screen": "6.1 inch OLED",
    "os": "iOS 15",
    "camera": "12MP"
  }
}
```

### Lấy sản phẩm theo danh mục
**Endpoint:** `GET /api/products/category/{categoryId}`

**Mô tả:** Lấy danh sách sản phẩm theo danh mục

**Tham số:**
- `page` (mặc định: 0): Số trang
- `size` (mặc định: 10): Số sản phẩm trên mỗi trang
- `sortBy` (mặc định: "id"): Trường sắp xếp
- `sortDir` (mặc định: "asc"): Hướng sắp xếp (asc/desc)

**Response:** Tương tự như lấy danh sách sản phẩm

### Tìm kiếm sản phẩm
**Endpoint:** `GET /api/products/search`

**Mô tả:** Tìm kiếm sản phẩm theo từ khóa

**Tham số:**
- `keyword`: Từ khóa tìm kiếm
- `page` (mặc định: 0): Số trang
- `size` (mặc định: 10): Số sản phẩm trên mỗi trang

**Response:** Tương tự như lấy danh sách sản phẩm

### Lọc sản phẩm
**Endpoint:** `POST /api/products/filter`

**Mô tả:** Lọc sản phẩm theo nhiều tiêu chí

**Request:**
```json
{
  "categoryIds": [1, 2],
  "minPrice": 5000000,
  "maxPrice": 30000000,
  "sortBy": "price",
  "sortDirection": "asc",
  "page": 0,
  "size": 10
}
```

**Response:** Tương tự như lấy danh sách sản phẩm

### Thêm sản phẩm mới (Admin)
**Endpoint:** `POST /api/products`

**Mô tả:** Thêm sản phẩm mới (yêu cầu quyền ADMIN)

**Request:**
```json
{
  "name": "Samsung Galaxy S21",
  "description": "Samsung Galaxy S21 5G 128GB",
  "price": 20990000,
  "stockQuantity": 50,
  "imageUrl": "galaxy-s21.jpg",
  "categoryId": 1,
  "specifications": {
    "screen": "6.2 inch Dynamic AMOLED",
    "os": "Android 11",
    "camera": "64MP"
  }
}
```

**Response:** Sản phẩm đã tạo với ID

### Cập nhật sản phẩm (Admin)
**Endpoint:** `PUT /api/products/{id}`

**Mô tả:** Cập nhật thông tin sản phẩm (yêu cầu quyền ADMIN)

**Request:** Tương tự như thêm sản phẩm

**Response:** Sản phẩm đã cập nhật

### Xóa sản phẩm (Admin)
**Endpoint:** `DELETE /api/products/{id}`

**Mô tả:** Xóa sản phẩm (yêu cầu quyền ADMIN)

**Response:** 204 No Content

## Quản lý danh mục

### Lấy tất cả danh mục
**Endpoint:** `GET /api/categories`

**Mô tả:** Lấy danh sách tất cả danh mục sản phẩm

**Response:**
```json
[
  {
    "id": 1,
    "name": "Smartphones",
    "description": "Mobile phones"
  },
  {
    "id": 2,
    "name": "Laptops",
    "description": "Portable computers"
  }
]
```

### Lấy danh mục theo ID
**Endpoint:** `GET /api/categories/{id}`

**Mô tả:** Lấy thông tin chi tiết của một danh mục

**Response:**
```json
{
  "id": 1,
  "name": "Smartphones",
  "description": "Mobile phones"
}
```

### Lấy danh mục kèm số lượng sản phẩm
**Endpoint:** `GET /api/categories/with-product-count`

**Mô tả:** Lấy danh sách danh mục kèm số lượng sản phẩm trong mỗi danh mục

**Response:**
```json
[
  {
    "categoryId": 1,
    "categoryName": "Smartphones",
    "productCount": 25
  },
  {
    "categoryId": 2,
    "categoryName": "Laptops",
    "productCount": 15
  }
]
```

### Thêm danh mục mới (Admin)
**Endpoint:** `POST /api/categories`

**Mô tả:** Thêm danh mục mới (yêu cầu quyền ADMIN)

**Request:**
```json
{
  "name": "Tablets",
  "description": "Tablet computers"
}
```

**Response:** Danh mục đã tạo với ID

### Cập nhật danh mục (Admin)
**Endpoint:** `PUT /api/categories/{id}`

**Mô tả:** Cập nhật thông tin danh mục (yêu cầu quyền ADMIN)

**Request:**
```json
{
  "name": "Tablets",
  "description": "Updated description for tablets"
}
```

**Response:** Danh mục đã cập nhật

### Xóa danh mục (Admin)
**Endpoint:** `DELETE /api/categories/{id}`

**Mô tả:** Xóa danh mục (yêu cầu quyền ADMIN)

**Response:** 204 No Content

## Quản lý giỏ hàng

### Lấy giỏ hàng hiện tại
**Endpoint:** `GET /api/cart`

**Mô tả:** Lấy thông tin giỏ hàng của người dùng đang đăng nhập

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "items": [
    {
      "id": 101,
      "productId": 1,
      "productName": "iPhone 13",
      "quantity": 1,
      "price": 24990000,
      "imageUrl": "iphone-13.jpg"
    }
  ],
  "totalItems": 1,
  "subtotal": 24990000,
  "discount": 0,
  "total": 24990000,
  "discountCode": null
}
```

### Thêm sản phẩm vào giỏ hàng
**Endpoint:** `POST /api/cart/items`

**Mô tả:** Thêm sản phẩm vào giỏ hàng

**Request:**
```json
{
  "productId": 2,
  "quantity": 1
}
```

**Response:** Giỏ hàng đã cập nhật

### Cập nhật số lượng sản phẩm trong giỏ hàng
**Endpoint:** `PUT /api/cart/items/{itemId}`

**Mô tả:** Cập nhật số lượng sản phẩm trong giỏ hàng

**Request:**
```json
{
  "quantity": 2
}
```

**Response:** Giỏ hàng đã cập nhật

### Xóa sản phẩm khỏi giỏ hàng
**Endpoint:** `DELETE /api/cart/items/{itemId}`

**Mô tả:** Xóa sản phẩm khỏi giỏ hàng

**Response:** Giỏ hàng đã cập nhật

### Xóa toàn bộ giỏ hàng
**Endpoint:** `DELETE /api/cart/clear`

**Mô tả:** Xóa tất cả sản phẩm khỏi giỏ hàng

**Response:** Giỏ hàng trống

### Áp dụng mã giảm giá
**Endpoint:** `POST /api/cart/apply-discount`

**Mô tả:** Áp dụng mã giảm giá vào giỏ hàng

**Tham số:**
- `code`: Mã giảm giá

**Response:** Giỏ hàng đã cập nhật với giảm giá

### Xóa mã giảm giá
**Endpoint:** `DELETE /api/cart/remove-discount`

**Mô tả:** Xóa mã giảm giá khỏi giỏ hàng

**Response:** Giỏ hàng đã cập nhật

## Quản lý đơn hàng

### Lấy lịch sử đơn hàng
**Endpoint:** `GET /api/orders`

**Mô tả:** Lấy lịch sử đơn hàng của người dùng đang đăng nhập

**Tham số:**
- `page` (mặc định: 0): Số trang
- `size` (mặc định: 10): Số đơn hàng trên mỗi trang

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "orderDate": "2023-07-15T10:30:00",
      "status": "COMPLETED",
      "total": 24990000,
      "items": 2
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 5,
  "totalPages": 1,
  "last": true
}
```

### Lấy chi tiết đơn hàng
**Endpoint:** `GET /api/orders/{id}`

**Mô tả:** Lấy thông tin chi tiết của một đơn hàng

**Response:**
```json
{
  "id": 1,
  "orderDate": "2023-07-15T10:30:00",
  "status": "COMPLETED",
  "shippingAddress": "123 Đường ABC, Quận XYZ, Hà Nội",
  "paymentMethod": "COD",
  "subtotal": 25990000,
  "shippingFee": 30000,
  "discount": 1000000,
  "total": 24990000,
  "discountCode": "SUMMER2023",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "iPhone 13",
      "quantity": 1,
      "price": 24990000,
      "imageUrl": "iphone-13.jpg"
    },
    {
      "id": 2,
      "productId": 5,
      "productName": "AirPods Pro",
      "quantity": 1,
      "price": 5990000,
      "imageUrl": "airpods-pro.jpg"
    }
  ]
}
```

### Lọc đơn hàng
**Endpoint:** `POST /api/orders/filter`

**Mô tả:** Lọc đơn hàng theo nhiều tiêu chí

**Request:**
```json
{
  "startDate": "2023-01-01",
  "endDate": "2023-07-31",
  "status": ["COMPLETED", "PROCESSING"],
  "minTotal": 1000000,
  "maxTotal": 30000000,
  "page": 0,
  "size": 10
}
```

**Response:** Tương tự như lấy lịch sử đơn hàng

### Tạo đơn hàng mới
**Endpoint:** `POST /api/orders`

**Mô tả:** Tạo đơn hàng mới từ giỏ hàng

**Request:**
```json
{
  "shippingAddress": "123 Đường ABC, Quận XYZ, Hà Nội",
  "paymentMethod": "COD",
  "notes": "Giao hàng ngoài giờ hành chính"
}
```

**Response:**
```json
{
  "orderId": 10,
  "status": "PENDING",
  "total": 24990000,
  "paymentUrl": null
}
```

### Cập nhật trạng thái đơn hàng (Admin)
**Endpoint:** `PUT /api/orders/{id}/status`

**Mô tả:** Cập nhật trạng thái đơn hàng (yêu cầu quyền ADMIN)

**Tham số:**
- `status`: Trạng thái mới (PENDING, PROCESSING, SHIPPED, COMPLETED, CANCELLED)

**Response:** Đơn hàng đã cập nhật

### Lấy tất cả đơn hàng (Admin)
**Endpoint:** `GET /api/orders/all`

**Mô tả:** Lấy tất cả đơn hàng trong hệ thống (yêu cầu quyền ADMIN)

**Tham số:**
- `page` (mặc định: 0): Số trang
- `size` (mặc định: 10): Số đơn hàng trên mỗi trang
- `sortBy` (mặc định: "orderDate"): Trường sắp xếp
- `sortDir` (mặc định: "desc"): Hướng sắp xếp

**Response:** Tương tự như lấy lịch sử đơn hàng

## Mã giảm giá

### Lấy tất cả mã giảm giá (Admin)
**Endpoint:** `GET /api/discount-codes`

**Mô tả:** Lấy danh sách tất cả mã giảm giá (yêu cầu quyền ADMIN)

**Response:**
```json
[
  {
    "id": 1,
    "code": "SUMMER2023",
    "discountPercent": 10,
    "maxDiscount": 1000000,
    "minOrderValue": 5000000,
    "startDate": "2023-06-01",
    "endDate": "2023-08-31",
    "isActive": true,
    "usageLimit": 1000,
    "usageCount": 250
  }
]
```

### Lấy mã giảm giá theo mã
**Endpoint:** `GET /api/discount-codes/{code}`

**Mô tả:** Lấy thông tin chi tiết của một mã giảm giá

**Response:**
```json
{
  "id": 1,
  "code": "SUMMER2023",
  "discountPercent": 10,
  "maxDiscount": 1000000,
  "minOrderValue": 5000000,
  "startDate": "2023-06-01",
  "endDate": "2023-08-31",
  "isActive": true,
  "usageLimit": 1000,
  "usageCount": 250
}
```

### Thêm mã giảm giá mới (Admin)
**Endpoint:** `POST /api/discount-codes`

**Mô tả:** Tạo mã giảm giá mới (yêu cầu quyền ADMIN)

**Request:**
```json
{
  "code": "FALL2023",
  "discountPercent": 15,
  "maxDiscount": 2000000,
  "minOrderValue": 10000000,
  "startDate": "2023-09-01",
  "endDate": "2023-11-30",
  "isActive": true,
  "usageLimit": 500
}
```

**Response:** Mã giảm giá đã tạo

### Cập nhật mã giảm giá (Admin)
**Endpoint:** `PUT /api/discount-codes/{id}`

**Mô tả:** Cập nhật thông tin mã giảm giá (yêu cầu quyền ADMIN)

**Request:** Tương tự như thêm mã giảm giá

**Response:** Mã giảm giá đã cập nhật

### Xóa mã giảm giá (Admin)
**Endpoint:** `DELETE /api/discount-codes/{id}`

**Mô tả:** Xóa mã giảm giá (yêu cầu quyền ADMIN)

**Response:** 204 No Content

## Đánh giá sản phẩm

### Lấy đánh giá của sản phẩm
**Endpoint:** `GET /api/products/{productId}/reviews`

**Mô tả:** Lấy tất cả đánh giá của một sản phẩm

**Tham số:**
- `page` (mặc định: 0): Số trang
- `size` (mặc định: 10): Số đánh giá trên mỗi trang

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "rating": 5,
      "comment": "Sản phẩm tuyệt vời, đóng gói cẩn thận",
      "userName": "user123",
      "createdAt": "2023-07-20T15:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3,
  "last": false
}
```

### Thêm đánh giá mới
**Endpoint:** `POST /api/products/{productId}/reviews`

**Mô tả:** Thêm đánh giá mới cho sản phẩm

**Request:**
```json
{
  "rating": 4,
  "comment": "Sản phẩm tốt, giao hàng nhanh"
}
```

**Response:** Đánh giá đã tạo

### Cập nhật đánh giá
**Endpoint:** `PUT /api/products/{productId}/reviews/{reviewId}`

**Mô tả:** Cập nhật đánh giá đã tồn tại

**Request:**
```json
{
  "rating": 5,
  "comment": "Đã cập nhật đánh giá sau khi sử dụng thêm"
}
```

**Response:** Đánh giá đã cập nhật

### Xóa đánh giá
**Endpoint:** `DELETE /api/products/{productId}/reviews/{reviewId}`

**Mô tả:** Xóa đánh giá

**Response:** 204 No Content 