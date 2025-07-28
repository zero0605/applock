@echo off
echo pushing gradle build action fix...

git add .
git commit -m "fix: use gradle-build-action for proper gradle setup"
git push origin main

echo fix v3 pushed! this should work now
pause
