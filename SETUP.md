# setup hướng dẫn

## bước 1: cài git (nếu chưa có)
download và cài git từ: https://git-scm.com/download/win

## bước 2: push code lên github
1. mở cmd/powershell trong folder này
2. chạy file `push-to-github.bat`
3. hoặc chạy từng lệnh:

```bash
git init
git add .
git commit -m "initial commit: applock vl with shizuku integration"
git remote add origin https://github.com/zero0605/applock.git
git branch -M main
git push -u origin main
```

## bước 3: đợi github actions build
1. vào https://github.com/zero0605/applock
2. click tab "actions"
3. đợi build xong (5-10 phút)
4. download apk từ artifacts

## bước 4: test app
1. cài shizuku app
2. chạy shizuku service
3. cài applock apk
4. cấp quyền và test

## lưu ý
- nếu git báo lỗi authentication, dùng github token
- nếu build fail, check logs trong actions tab
- app cần android 7.0+ và shizuku để hoạt động

## troubleshooting
- git not found: cài git for windows
- authentication failed: setup github token
- build failed: check github actions logs
- app crash: check shizuku service running
