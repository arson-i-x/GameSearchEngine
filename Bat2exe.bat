;@echo off

;rem ----------------------------------------------------------
;rem ----------------- change the paths here -----------------
;rem ----------------------------------------------------------
;set "target_exe=packed.exe"
;set "bat_file=.\Gamesearch.bat"
;set "jar_file=.\GameSearchEngine.jar"
;rem ----------------------------------------------------------

;for %%# in ("%bat_file%") do set "batch_file=%%~nx#"
;for %%# in ("%bat_file%") do set "bat_name=%%~nx#"
;for %%# in ("%bat_file%") do set "bat_dir=%%~dp#"

;for %%# in ("%jar_file%") do set "j_file=%%~nx#"
;for %%# in ("%jar_file%") do set "jar_name=%%~nx#"
;for %%# in ("%jar_file%") do set "jar_dir=%%~dp#"


;set "target.exe=%__cd__%%target_exe%"


;copy /y "%~f0" "%temp%\2exe.sed" >nul

;(echo()>>"%temp%\2exe.sed"
;(echo(AppLaunched=cmd.exe /c "%bat_name%")>>"%temp%\2exe.sed"
;(echo(TargetName=%target.exe%)>>"%temp%\2exe.sed"
;(echo(FILE0="%bat_name%")>>"%temp%\2exe.sed"
;(echo(FILE1="%jar_name%")>>"%temp%\2exe.sed"

;(echo([SourceFiles])>>"%temp%\2exe.sed"
;(echo(SourceFiles0=%bat_dir%)>>"%temp%\2exe.sed"
;(echo(SourceFiles1=%jar_dir%)>>"%temp%\2exe.sed"

;(echo([SourceFiles0])>>"%temp%\2exe.sed"
;(echo(%%FILE0%%=)>>"%temp%\2exe.sed"

;(echo([SourceFiles1])>>"%temp%\2exe.sed"
;(echo(%%FILE1%%=)>>"%temp%\2exe.sed"


;iexpress /n /q /m %temp%\2exe.sed

;rem del /q /f "%temp%\2exe.sed"
; exit /b 0

[Version]
Class=IEXPRESS
SEDVersion=3
[Options]
PackagePurpose=InstallApp
ShowInstallProgramWindow=1
HideExtractAnimation=1
UseLongFileName=1
InsideCompressed=0
CAB_FixedSize=0
CAB_ResvCodeSigning=0
RebootMode=N
InstallPrompt=%InstallPrompt%
DisplayLicense=%DisplayLicense%
FinishMessage=%FinishMessage%
TargetName=%TargetName%
FriendlyName=%FriendlyName%
AppLaunched=%AppLaunched%
PostInstallCmd=%PostInstallCmd%
AdminQuietInstCmd=%AdminQuietInstCmd%
UserQuietInstCmd=%UserQuietInstCmd%
SourceFiles=SourceFiles

[Strings]
InstallPrompt=
DisplayLicense=
FinishMessage=
FriendlyName=-
PostInstallCmd=<None>
AdminQuietInstCmd=
UserQuietInstCmd=
;