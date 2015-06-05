REM Create symbolic links to hooks in this repository. 
REM Even create links for empty hook scripts, as we don't want this command to have to be re-run
mklink /H .git\hooks\applypatch-msg ..\hooks\applypatch-msg
mklink /H .git\hooks\pre-applypatch ..\hooks\pre-applypatch
mklink /H .git\hooks\post-applypatch ..\hooks\post-applypatch
mklink .git\hooks\pre-commit ..\..\hooks\pre-commit
mklink /H .git\hooks\prepare-commit-msg ..\hooks\prepare-commit-msg
mklink /H .git\hooks\commit-msg ..\hooks\commit-msg
mklink /H .git\hooks\post-commit hooks\post-commit
mklink /H .git\hooks\pre-rebase ..\hooks\pre-rebase
mklink /H .git\hooks\post-checkout ..\hooks\post-checkout
mklink /H .git\hooks\post-merge ..\hooks\post-merge
mklink /H .git\hooks\pre-push ..\hooks\pre-push
mklink /H .git\hooks\pre-receive ..\hooks\pre-receive
mklink /H .git\hooks\update ..\hooks\update
mklink /H .git\hooks\post-receive ..\hooks\post-receive
mklink /H .git\hooks\post-update ..\hooks\post-update
mklink /H .git\hooks\pre-auto-gc ..\hooks\pre-auto-gc
mklink /H .git\hooks\post-rewrite ..\hooks\post-rewrite




REM Run the post-checkout