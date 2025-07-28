@echo off
echo pushing applock to github...

echo step 0: configuring git user...
git config --global user.email "zero0605@github.com"
git config --global user.name "zero0605"

echo step 1: initializing git repo...
git init

echo step 2: adding all files...
git add .

echo step 3: committing files...
git commit -m "initial commit: applock vl with shizuku integration"

echo step 4: removing existing remote (if any)...
git remote remove origin 2>nul

echo step 5: adding remote origin...
git remote add origin https://github.com/zero0605/applock.git

echo step 6: setting main branch...
git branch -M main

echo step 7: pushing to github...
git push -u origin main

echo done! check github actions for build status
pause
