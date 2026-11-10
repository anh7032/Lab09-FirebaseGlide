# Lab09 — Firebase Auth + Glide Gallery (Android, Kotlin)

Spec cho agent (opencode) hoàn thiện project này. Khung Gradle + resources đã dựng sẵn, **đừng đổi** trừ khi build lỗi.

## Đã có sẵn (đừng làm lại)
- `settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml` (AGP 8.5.2, Kotlin 2.0.0, KSP, google-services 4.4.2, Firebase BoM 33.1.2, firebase-auth-ktx, Glide 4.16.0, Material, RecyclerView)
- `app/build.gradle.kts`: namespace/applicationId `vn.edu.vlu.lab09`, viewBinding = true, plugin google-services + ksp đã apply
- `AndroidManifest.xml`: quyền INTERNET + ACCESS_NETWORK_STATE, AuthActivity là launcher, MainActivity exported=false
- `res/values/strings.xml` (đủ mọi string cần dùng — dùng string resource, không hardcode text), `colors.xml`, `themes.xml` (Theme.Material3.DayNight.NoActionBar)
- Drawables: `ic_launcher`, `ic_placeholder`, `ic_broken_image`, `ic_logout`
- `.gitignore` (đã ignore `app/google-services.json`)
- `gradle/wrapper/gradle-wrapper.properties` (Gradle 8.7) — **chưa có gradle-wrapper.jar**: chạy `gradle wrapper` hoặc copy từ project khác.

## Còn thiếu — việc của bạn

Tất cả code Kotlin trong package `vn.edu.vlu.lab09`. **Bắt buộc 100% ViewBinding, cấm findViewById.**

### 1. Layouts
- `res/layout/activity_auth.xml`:
  - `MaterialButtonToggleGroup` (2 nút: Login / Register, singleSelection, selectionRequired)
  - `TextInputLayout` + `TextInputEditText` cho email, password (`app:endIconMode="password_toggle"`), confirm password (ẩn ở chế độ Login)
  - Nút submit (`MaterialButton`), `CircularProgressIndicator` (ẩn mặc định), TextView "Forgot Password?"
- `res/layout/activity_main.xml`: `MaterialToolbar` (title @string/title_gallery) + `ShapeableImageView`/`ImageView` avatar 40dp trong toolbar + `RecyclerView` full màn.
- `res/layout/item_photo.xml`: `MaterialCardView` chứa `ImageView` (id `ivPhoto`, height ~220dp, scaleType centerCrop) + TextView title (id `tvTitle`) + TextView author (id `tvAuthor`).
- `res/menu/menu_main.xml`: item logout, icon `@drawable/ic_logout`, `app:showAsAction="always"`.

### 2. `model/PhotoItem.kt`
`data class PhotoItem(val id: Int, val title: String, val author: String, val imageUrl: String)` + object/companion cung cấp list ≥ 12 item dùng Picsum: `https://picsum.photos/id/<N>/800/600` (dùng các id tồn tại: 10,11,12,13,14,15,16,17,18,19,20,22 chẳng hạn), title/author tự đặt.

### 3. `adapter/PhotoAdapter.kt`
- `RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>`, ViewHolder giữ `ItemPhotoBinding`.
- Trong `onBindViewHolder` dùng ĐÚNG chuỗi Glide đề yêu cầu:
```kotlin
Glide.with(holder.itemView.context)
    .load(photo.imageUrl)
    .placeholder(R.drawable.ic_placeholder)
    .error(R.drawable.ic_broken_image)
    .transition(DrawableTransitionOptions.withCrossFade())
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .into(holder.binding.ivPhoto)
```
- Override `onViewRecycled(holder)`: `Glide.with(holder.itemView.context).clear(holder.binding.ivPhoto)`.

### 4. `AuthActivity.kt`
- `FirebaseAuth.getInstance()` (hoặc ktx `Firebase.auth`).
- **Auto-login trong onCreate TRƯỚC setContentView**: nếu `auth.currentUser != null` → start MainActivity, `finish()`, return.
- Toggle Login/Register: đổi visibility confirm-password + text nút submit.
- Validate client trước khi gọi Firebase: `Patterns.EMAIL_ADDRESS.matcher(email).matches()`, password ≥ 6 ký tự, confirm trùng (chỉ chế độ Register). Báo lỗi bằng `TextInputLayout.error`, clear error khi hợp lệ.
- Register: `createUserWithEmailAndPassword`, Login: `signInWithEmailAndPassword`. Khi gọi: hiện progress + disable nút; xong thì ngược lại.
- Xử lý lỗi trong `addOnFailureListener` bằng `when (exception)`:
  - `FirebaseAuthWeakPasswordException` → @string/error_weak_password
  - `FirebaseAuthUserCollisionException` → @string/error_user_collision
  - `FirebaseAuthInvalidCredentialsException` → @string/error_invalid_credentials (hiện "Incorrect email or password")
  - `FirebaseNetworkException` → @string/error_network
  - else → @string/error_unknown với message
  (import đúng: `com.google.firebase.auth.FirebaseAuthWeakPasswordException` v.v., `com.google.firebase.FirebaseNetworkException`)
- Thành công → Toast + start MainActivity + finish().
- **Bonus Forgot Password**: click → validate email → `auth.sendPasswordResetEmail(email)` → Toast @string/msg_reset_sent.

### 5. `MainActivity.kt`
- setSupportActionBar với toolbar từ binding; RecyclerView `LinearLayoutManager` + `PhotoAdapter(PhotoItem.samplePhotos)`.
- Menu: inflate `menu_main`, logout → `auth.signOut()` rồi:
```kotlin
val intent = Intent(this, AuthActivity::class.java)
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
startActivity(intent)
finish()
```
- Guard: nếu `auth.currentUser == null` trong onCreate → về AuthActivity luôn.
- **Bonus avatar**: load vào ImageView trên toolbar bằng Glide + `.circleCrop()`, URL động theo email: `"https://i.pravatar.cc/150?u=" + (auth.currentUser?.email ?: "guest")`.

### 6. Git
- `git init` (branch `main`), commit theo từng stage có nghĩa (setup gradle → auth UI → auth logic → gallery glide → logout + bonus).
- **Author phải là chủ máy (git config toàn cục sẵn có)**: KHÔNG set `user.name`/`user.email` khác, KHÔNG dùng `--author`, KHÔNG thêm bất kỳ dòng `Co-Authored-By`/attribution/"Generated with..." nào vào commit message. Commit message thuần mô tả thay đổi. Nếu máy chưa cấu hình git user thì DỪNG và hỏi user, không tự bịa.

### 7. Build check
- Cần `app/google-services.json` (user tự tải từ Firebase Console) — nếu chưa có thì KHÔNG build được với plugin google-services; cứ viết code xong, báo user bỏ file vào rồi mới `gradlew assembleDebug`.

## Tiêu chí chấm cần nhớ
- 0 findViewById, 100% ViewBinding; package: `model/`, `adapter/`, activity ở root package.
- Glide đủ: placeholder, error, crossfade, DiskCacheStrategy.ALL, clear trong onViewRecycled.
- Logout xoá back stack; auto-login bằng currentUser.
