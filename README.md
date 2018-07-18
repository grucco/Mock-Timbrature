# Mock Timbrature

# Prefazione
Il presente documento descrive in maniera sintetica la struttura dell'applicazione timbrature. 

# Repository
http://syskobotfarm.replycloud.prv/acea/timbrature_android.git
Branch più aggiornato Token_Mail_Auth

# Impostare i puntamenti
L'applicazione punta a diversi sistemi utilizzando diversi flavours di build, configurati nel build.gradle sotto la cartella app

# Activities
L'applicazione è estremamente semplice e si compone delle seguenti activity 
*LoginActivity.java
*MainActivity.java
*SplashScreen.java
*TimbraturaActivity.java


## SplashScreen
Prima activity che viene invocata all'apertura. Verifica se esiste un verification code. Se esiste apre la main activity, altrimenti apre la login activity

## Login activity
LoginActivity.java offre la funzionalità di accesso ed autenticazione dell'utente:
* Permette all'utente l'inserimento della matricola per iniziare la registrazione
* Può essere aperta attraverso un intent per completare il processo di registrazione. 
L'apertura con intent viene invocata con il tap sul link di verifica ricevuto dall'utente con email


## Main activity
La schermata principale dell'app:
* Permette l'apertura dell'activity per una nuova timbratura
* Consente di visualizzare un calendario e di visualizzare le timbrature del giorno selezionato

# Http client
Il client HTTP utilizzato è Ion. Le chiamate di rete sono gestite all'interno del file NetworkManager.java