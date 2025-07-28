@echo off
echo pushing minimal working version...

git add .
git commit -m "simplify: minimal working android app for successful build"
git push origin main

echo minimal version pushed! should build successfully now
pause
