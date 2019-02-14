# AnkiOverdriveCollisionPrevention

A collision prevention project for the [anki overdrive kit](https://www.anki.com/de-de/overdrive) written in java.

# Dependencies

This project requires an adjusted version of the [JAnki](https://github.com/BerndMuller/JAnki) library.
See the JAnki folder for the adjusted version.

# Getting started

This project contains two versions of collision prevention.

One that uses stop and go logic and one that uses dynamic speed adjustment (dsa)

# Installing

Just run 

```
mvn clean package
```

# Executing

To start the prevention use
```
de.project.dsa.DSACar.main()
```
or
```
de.project.stopandgo.StopAndGoCar.main()
```
