# Lab 09 — Firebase Authentication & Asynchronous Image Loading with Glide

Mobile Application Programming (71ITSE30603 / 72ITSE30603)
Sinh viên: Bùi Duy Anh — 2474802010525

## Tính năng

- **Đăng ký / Đăng nhập** bằng Email & Password qua Firebase Authentication, chuyển chế độ bằng `MaterialButtonToggleGroup`.
- **Auto-login**: `AuthActivity` kiểm tra `auth.currentUser` trong `onCreate()` và vào thẳng `MainActivity` nếu phiên còn hiệu lực.
- **Validate phía client**: email theo `Patterns.EMAIL_ADDRESS`, mật khẩu tối thiểu 6 ký tự, xác nhận mật khẩu phải trùng.
- **Xử lý lỗi Firebase**: `FirebaseAuthWeakPasswordException`, `FirebaseAuthUserCollisionException`,
  `FirebaseAuthInvalidCredentialsException`, `FirebaseAuthInvalidUserException`, `FirebaseNetworkException`.
- **Gallery ảnh online**: `RecyclerView` hiển thị 12 ảnh HTTPS từ Picsum, tải bằng Glide với placeholder,
  error icon, hiệu ứng cross-fade và `DiskCacheStrategy.ALL` (xem lại được khi offline).
  Request được huỷ trong `onViewRecycled()` để tránh nháy ảnh và tràn bộ nhớ.
- **Đăng xuất**: `auth.signOut()` kèm `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK` để xoá back stack.
- **Bonus**: Forgot Password (`sendPasswordResetEmail`) và avatar tròn trên Toolbar bằng Glide `circleCrop()`.

Toàn bộ truy cập view dùng **ViewBinding**, không có `findViewById` nào.

## Cấu trúc

```
app/src/main/java/vn/edu/vlu/lab09/
├── AuthActivity.kt        # đăng nhập / đăng ký / quên mật khẩu
├── MainActivity.kt        # toolbar, avatar, gallery, logout
├── adapter/PhotoAdapter.kt
└── model/PhotoItem.kt
```

## Chạy project

1. Tạo project trên [Firebase Console](https://console.firebase.google.com/), đăng ký app Android với
   package name `vn.edu.vlu.lab09` và SHA-1 debug (`gradlew signingReport`).
2. Bật provider **Email/Password** tại *Build → Authentication → Sign-in method*.
3. Tải `google-services.json` và đặt vào thư mục `app/`.
   File này được `.gitignore` nên không nằm trong repository.
4. Mở project bằng Android Studio và chạy trên thiết bị có kết nối Internet.

## Thư viện

| Thư viện | Phiên bản |
|---|---|
| Firebase BoM (`firebase-auth-ktx`) | 33.1.2 |
| Glide (+ KSP compiler) | 4.16.0 |
| Material Components | 1.12.0 |
