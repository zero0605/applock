# applock vl

app khóa ứng dụng sử dụng shizuku, không cần accessibility service.

## tính năng

- khóa app khi thoát
- xác thực bằng pin/vân tay/khuôn mặt
- không cần root
- không cần accessibility service
- sử dụng shizuku để monitor app

## yêu cầu

- android 7.0+ (api 24)
- shizuku app và service
- quyền usage stats
- quyền system alert window

## cách dùng

1. cài đặt shizuku và chạy service
2. cài đặt applock vl
3. cấp quyền cho app
4. chọn app cần khóa
5. bật service monitor

## build

project sử dụng github actions để auto build apk.

push code lên github là tự động build và tạo apk trong artifacts.

## cảm ơn

- [shizuku](https://github.com/RikkaApps/Shizuku) - api để giao tiếp với system
- [zukulock](https://github.com/tiendnm/zukulock) - ý tưởng ban đầu

## license

mit license
