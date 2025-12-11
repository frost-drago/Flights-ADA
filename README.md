# Flights (Algorithm design and analysis)

# FlightsProject – UI / CSS Guide

You **do not** need to write Java code for this task.  
Your job = **make the app look nice using JavaFX CSS**.

---

## 1. What this project is

A JavaFX desktop app that shows flight data.  
The Java and loading logic already work – you only touch **CSS** and maybe FXML labels/layout if needed.

---

## 2. Project structure (what you care about)

```text
FlightsProject
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com.ada.flightsproject
   │  │     ├─ data
   │  │     │  └─ FlightGraphLoader.java
   │  │     ├─ dataStructures
   │  │     │  └─ FlightGraph.java
   │  │     ├─ utility
   │  │     │  └─ Utility.java
   │  │     ├─ App.java        <-- JavaFX entry point (starts the UI)
   │  │     ├─ Main.java       <-- testing out code
   │  │     └─ MainController.java
   │  └─ resources
   │     └─ com.ada.flightsproject
   │        ├─ css
   │        │  └─ (put your stylesheets here)
   │        ├─ data
   │        │  └─ FlightPathData.csv
   │        └─ views
   │           └─ MainView.fxml <-- main UI layout
└─ pom.xml (Maven)
