# Flights (Algorithm design and analysis)

This project implements a modified Dijkstra algorithm to compute the earliest possible 
arrival time between airports under flight time constraints.

---

## 1. How to run the file
1. Open project in IntelliJ
2. IntelliJ should detect it as a Maven project and index it.
3. Click Reload all Maven projects
4. Run App.java
5. Use GUI to input source, destination, and departure day/time
6. View earliest arrival result in table

---

## 2. Project structure

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
   │  │     ├─ App.java        <-- JavaFX entry point (starts the UI). Please run this one
   │  │     ├─ FlightRow.java
   │  │     ├─ Main.java       <-- testing out code
   │  │     └─ MainController.java
   │  └─ resources
   │     └─ com.ada.flightsproject
   │        ├─ css
   │        │  └─ flightstyle.css
   │        ├─ data
   │        │  └─ FlightPathData.csv
   │        └─ views
   │           └─ MainView.fxml <-- main UI layout
└─ pom.xml (Maven)
```

