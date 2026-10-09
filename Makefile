# Makefile - lancement de l'émulateur Android et de l'application
#
#   make emulator          lance seulement un émulateur (attend qu'il ait fini de démarrer)
#   make run               compile, installe et ouvre l'appli sur l'appareil déjà connecté
#   make start             lance l'émulateur puis l'appli dessus
#
#   Choisir un autre émulateur :  make start AVD=Pixel_7_API_33
#   Liste des émulateurs       :  make avds

SHELL := powershell.exe
.SHELLFLAGS := -NoProfile -ExecutionPolicy Bypass -Command

SDK      ?= $(LOCALAPPDATA)/Android/Sdk
ADB      := $(SDK)/platform-tools/adb.exe
EMULATOR := $(SDK)/emulator/emulator.exe
AVD      ?= Pixel_7_API_35
APP_ID   := com.example.testcour
ACTIVITY := $(APP_ID)/.MainActivity

.DEFAULT_GOAL := help
.PHONY: help avds devices emulator run start stop

help:
	@Write-Host "make emulator   - lance seulement l'emulateur ($(AVD))"
	@Write-Host "make run        - compile, installe et ouvre l'appli sur l'appareil connecte"
	@Write-Host "make start      - lance l'emulateur puis l'appli"
	@Write-Host "make stop       - ferme l'emulateur"
	@Write-Host "make avds       - liste les emulateurs disponibles"
	@Write-Host "make devices    - liste les appareils connectes"
	@Write-Host "Option : AVD=<nom>  (ex: make start AVD=Pixel_7_API_33)"

avds:
	@& '$(EMULATOR)' -list-avds

devices:
	@& '$(ADB)' devices

# Script sur plusieurs lignes : dans une variable, make fusionne les "\" en une seule ligne
# (dans une recette, il les transmettrait tels quels à PowerShell).
START_EMULATOR = \
	if (& '$(ADB)' devices | Select-String 'device$$') { Write-Host 'Un appareil est deja connecte.'; exit 0 }; \
	Write-Host 'Demarrage de l emulateur $(AVD)...'; \
	Start-Process -FilePath '$(EMULATOR)' -ArgumentList '-avd','$(AVD)'; \
	& '$(ADB)' wait-for-device; \
	while ("$$(& '$(ADB)' shell getprop sys.boot_completed)".Trim() -ne '1') { Start-Sleep -Seconds 2 }; \
	Write-Host 'Emulateur pret.'

emulator:
	@$(START_EMULATOR)

run:
	@if (-not (& '$(ADB)' devices | Select-String 'device$$')) { Write-Host 'Aucun appareil connecte : lance d abord make emulator (ou make start).'; exit 1 }
	./gradlew.bat installDebug
	@& '$(ADB)' shell am start -n $(ACTIVITY)

start: emulator
	@$(MAKE) --no-print-directory run

stop:
	@& '$(ADB)' emu kill
