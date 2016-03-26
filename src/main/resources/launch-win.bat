@echo off

for /r %%i in (airtaskdesktop-*-all.jar) do (
  javaw -jar %%~nxi
  goto :eof
)
