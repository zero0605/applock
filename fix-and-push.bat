@echo off
echo fixing gradle wrapper and pushing...

echo step 1: adding changes...
git add .

echo step 2: committing fix...
git commit -m "fix: gradle wrapper jar download issue"

echo step 3: pushing fix...
git push origin main

echo fix pushed! check github actions again
pause
