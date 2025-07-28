@echo off
echo fixing deprecated gradle property...

git add .
git commit -m "fix: remove deprecated android.enableBuildCache property"
git push origin main

echo deprecated property removed! check actions
pause
