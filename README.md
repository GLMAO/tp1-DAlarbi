[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/t19xNtmg)

#CS(14)
# 🧭 TP1 : Design Pattern Observer

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Pattern](https://img.shields.io/badge/Pattern-Observer-green.svg)]()

> **Module :** Génie Logiciel  
> **Sujet :** Implémentation du Pattern Observer avec gestion de la concurrence  
> **Auteur :** DAlarbi

---

## 🎯 Objectifs du TP

Comprendre et mettre en œuvre le **Design Pattern Observer** à travers un projet multi-modules Maven. Le but est de disposer d'un service de temps qui notifie plusieurs observateurs (horloges, comptes à rebours) à chaque changement de seconde.

---

## 🧱 Structure du Projet

```
tp1-DAlarbi/
├── tp-gl-master/
│   ├── timer-service/              → Interfaces (API)
│   │   └── src/main/java/org/emp/gl/timer/service/
│   │       └── TimerService.java
│   ├── timer-service-impl/         → Implémentation
│   │   └── src/main/java/org/emp/gl/timer/service/impl/
│   │       └── DummyTimeServiceImpl.java
│   └── timer-service-client/       → Observateurs (clients)
│       └── src/main/java/org/emp/gl/clients/
│           ├── CompteARebours.java
│           ├── Horloge.java
│           └── HorlogePreciseGUI.java
├── .gitignore
├── README.md
└── TP1.pdf
```

---

## 📝 Travail Réalisé

### Question (c) : Classe Horloge

**Objectif :** Afficher l'heure sur console à chaque seconde.

**Implémentation :**
- Implémente `TimerChangeListener`
- S'inscrit auprès du `TimerService` via `addTimeChangeListener(this)`
- Dépend uniquement de l'abstraction (interface `TimerService`)
- Réagit aux changements de la propriété `SECONDE_PROP`

**Test :**
```java
TimerService service = new DummyTimeServiceImpl();
Horloge horloge1 = new Horloge("Num 1", service);
Horloge horloge2 = new Horloge("Num 2", service);
```

**Résultat :**
```
Horloge Num 1 initialisée !
Horloge Num 2 initialisée !
Num 1 → 14:35:42
Num 2 → 14:35:42
Num 1 → 14:35:43
Num 2 → 14:35:43
```

✅ Plusieurs horloges coexistent et reçoivent les notifications indépendamment.

---

### Question (d) : Classe CompteARebours

**Objectif :** Créer un compte à rebours qui se décrémente jusqu'à 0.

#### **1. Instance avec paramètre 5**
```java
CompteARebours c = new CompteARebours("CD-5", 5, service);
```

**Résultat :**
```
CD-5 démarré avec 5s
CD-5 -> 4s
CD-5 -> 3s
CD-5 -> 2s
CD-5 -> 1s
CD-5 -> 0s
```
✅ Fonctionne correctement.

#### **2. Désinscription automatique à 0**

**Implémentation :**
- Méthode `tick()` synchronisée
- Appel à `removeTimeChangeListener(this)` quand `remaining == 0`
- Variable `active` pour éviter les doubles traitements

**Résultat :**
```
CD-5 -> 0s
CD-5 : terminé et désinscrit.
```
✅ Le compte à rebours se désinscrit automatiquement.

#### **3. 10 instances avec valeurs aléatoires (10-20s)**

```java
Random rnd = new Random();
for (int i = 1; i <= 10; i++) {
    int seconds = 10 + rnd.nextInt(11);
    new CompteARebours("CD-" + i, seconds, service);
}
```

**Résultat :**
```
CD-1 démarré avec 15s
CD-2 démarré avec 12s
CD-3 démarré avec 18s
...
CD-2 -> 0s
CD-2 : terminé et désinscrit.
CD-1 -> 0s
CD-1 : terminé et désinscrit.
```
✅ Les 10 comptes s'exécutent correctement en parallèle.

#### **4. Problème identifié : ConcurrentModificationException**

**Erreur observée :**
```
Exception in thread "Timer-0" java.util.ConcurrentModificationException
    at java.util.ArrayList$Itr.checkForComodification
```

**Analyse :**

Le problème survient lorsqu'un `CompteARebours` se désinscrit (appelle `removeListener()`) **pendant** que le `TimerService` itère sur la liste des listeners pour les notifier.

**Scénario du bug :**
```
Thread TimerService :
  1. Itère sur listeners : [CD-1, CD-2, CD-3, CD-4]
  2. Notifie CD-3 → CD-3 atteint 0
     → CD-3 appelle removeListener(this)
     → Liste modifiée pendant l'itération
  3. 💥 ConcurrentModificationException
```

**Causes :**
- Modification de collection pendant itération
- Accès concurrent non synchronisé
- Utilisation d'une `ArrayList` non thread-safe

---

### Question (e) : Solution avec PropertyChangeSupport

**Objectif :** Résoudre les problèmes de concurrence en délégant la gestion des observateurs à `PropertyChangeSupport`.

#### **Modifications effectuées :**

**1. Interface TimerChangeListener**

Hérite maintenant de `PropertyChangeListener` :
```java
public interface TimerChangeListener extends PropertyChangeListener {
    String SECONDE_PROP = "seconde";
    String MINUTE_PROP = "minute";
    String HEURE_PROP = "heure";
    // Hérite de : void propertyChange(PropertyChangeEvent evt);
}
```

**2. DummyTimeServiceImpl**

Remplacé la gestion manuelle par `PropertyChangeSupport` :
```java
// AVANT
private List<TimerChangeListener> listeners = new ArrayList<>();

// APRÈS
private final PropertyChangeSupport support = new PropertyChangeSupport(this);

@Override
public void addTimeChangeListener(TimerChangeListener pl) {
    support.addPropertyChangeListener(pl);
}

@Override
public void removeTimeChangeListener(TimerChangeListener pl) {
    support.removePropertyChangeListener(pl);
}

public void setSecondes(int newSecondes) {
    if (secondes == newSecondes) return;
    int oldValue = secondes;
    secondes = newSecondes;
    support.firePropertyChange(SECONDE_PROP, oldValue, secondes);
}
```

**3. Observateurs (Horloge & CompteARebours)**

Mise à jour de la signature :
```java
// AVANT
public void propertyChange(String prop, Object oldValue, Object newValue)

// APRÈS
@Override
public void propertyChange(PropertyChangeEvent evt) {
    if (SECONDE_PROP.equals(evt.getPropertyName())) {
        // traitement...
    }
}
```

#### **Résultat : Problème résolu ✅**

**Test de stress : 20 comptes simultanés**
```java
for (int i = 1; i <= 20; i++) {
    new CompteARebours("CD-" + i, 3, service);
}
```

| Avant PropertyChangeSupport | Après PropertyChangeSupport |
|------------------------------|----------------------------|
| ❌ ConcurrentModificationException | ✅ Aucune exception |
| ❌ Comportement imprévisible | ✅ Stable et déterministe |
| ❌ ~12/20 comptes terminés | ✅ 20/20 comptes terminés |

#### **Pourquoi ça fonctionne ?**

`PropertyChangeSupport` crée un **snapshot** (copie) de la liste des listeners avant de les notifier. Ainsi, même si un listener se retire pendant la notification, l'itération continue sur la copie sans erreur.

**Avez-vous résolu le problème ?** → **OUI ✅**

---

### Question (f) : Bonus - Interface Graphique

**Objectif :** Créer une application GUI pour afficher l'heure en temps réel.

**Implémentation :**
- Classe `HorlogePreciseGUI` extends `JFrame` implements `TimerChangeListener`
- Affichage de l'heure au format `HH:MM:SS.d` (avec dixièmes)
- Design moderne avec fond dégradé
- Thread-safety avec `SwingUtilities.invokeLater()`
- Met à jour l'affichage à chaque notification du service

**Exécution :**
```powershell
java -cp "out\timer-service;out\timer-service-impl;out\timer-service-client" org.emp.gl.clients.HorlogePreciseGUI
```

✅ Horloge graphique fonctionnelle avec mise à jour en temps réel.

---

## 🔧 Compilation et Exécution

### Compilation (PowerShell)

```powershell
# Nettoyer
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue

# Créer dossiers
New-Item -ItemType Directory -Force -Path out\timer-service
New-Item -ItemType Directory -Force -Path out\timer-service-impl
New-Item -ItemType Directory -Force -Path out\timer-service-client

# Compiler modules
javac -d out\timer-service timer-service\src\main\java\org\emp\gl\timer\service\*.java

javac -cp out\timer-service -d out\timer-service-impl timer-service-impl\src\main\java\org\emp\gl\timer\service\impl\*.java

javac -cp "out\timer-service;out\timer-service-impl" -d out\timer-service-client timer-service-client\src\main\java\org\emp\gl\clients\*.java
```

### Exécution

**Application GUI :**
```powershell
java -cp "out\timer-service;out\timer-service-impl;out\timer-service-client" org.emp.gl.clients.HorlogePreciseGUI
```

---

## 📊 Résultats et Conclusions

| Objectif | Statut | Commentaire |
|----------|--------|-------------|
| Horloge fonctionnelle | ✅ | Plusieurs instances simultanées |
| CompteARebours avec désinscription | ✅ | Auto-unsubscribe à 0 |
| 10 comptes aléatoires | ✅ | Valeurs entre 10-20s |
| Identification bugs concurrence | ✅ | ConcurrentModificationException |
| Résolution avec PropertyChangeSupport | ✅ | Thread-safe et stable |
| Interface graphique bonus | ✅ | Swing avec design moderne |



## 📚 Fichiers Ajoutés

- `timer-service-client/src/main/java/org/emp/gl/clients/CompteARebours.java`
- `timer-service-client/src/main/java/org/emp/gl/clients/HorlogePreciseGUI.java`

## 📝 Fichiers Modifiés

- `timer-service/src/main/java/org/emp/gl/timer/service/TimerChangeListener.java`
- `timer-service-impl/src/main/java/org/emp/gl/timer/service/impl/DummyTimeServiceImpl.java`
- `timer-service-client/src/main/java/org/emp/gl/clients/Horloge.java`

-