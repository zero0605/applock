@echo off
echo fixing resource linking issues...

git add .
git commit -m "fix: remove broken icon files and fix theme references"
git push origin main

echo resource fix pushed! check actions
pause
