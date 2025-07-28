@echo off
echo pushing gradle wrapper fix v2...

git add .
git commit -m "fix: use gradle to generate wrapper instead of manual download"
git push origin main

echo fix v2 pushed! check actions again
pause
