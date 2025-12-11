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
```
You will mainly use:

- src/main/resources/com/ada/flightsproject/css/ – your CSS files

- src/main/resources/com/ada/flightsproject/views/MainView.fxml – layout & IDs/classes

- src/main/java/com/ada/flightsproject/App.java – used only to run the app

## 3. Setup (IntelliJ)

1. Install JDK 21+ (or whatever version the project uses).

2. Open IntelliJ IDEA → File > Open... → select the FlightsProject folder (the one with pom.xml).

3. IntelliJ should detect it as a Maven project and index it.

4. Wait until the Maven import finishes (look at the bottom status bar).

5. If IntelliJ asks to “Download Maven dependencies”, say yes.

## 4. How to run the app

1. In the Project panel, open
src/main/java/com/ada/flightsproject/App.java

2. Right-click the App file → Run 'App.main()'
or click the green ▶ icon next to the main method.

3. A window should appear – this is the JavaFX UI you’ll style.

