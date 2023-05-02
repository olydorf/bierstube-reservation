# Bierstube Reservation System


The frontend source lives in [`./src/main/vue/`](./src/main/vue).


## Build System
We use gradle as a build system.
To call gradle use `./gradlew` on UNIX systems
and `gradlew` on MS Windows.

- Run the app: `./gradlew run` ([App](http://localhost:8080))
- Run tests: `./gradlew test`
- Run tests and lints: `./gradlew check`
  - [Test Report][test-report]
  - [Checkstyle Report for Main][checkstyle-main]
  - [Checkstyle Report for Test][checkstyle-test]
  - Spotless' report is shown on stdout
- Run formatter: `./gradlew spotlessApply`
- Build Javadoc: `./gradlew javadoc` ([Resulting Javadoc][javadoc])
- Build self-contained JAR: `./gradlew bootJar` ([Resulting JAR](bootJar))

[javadoc]: ./build/docs/javadoc/index.html
[checkstyle-main]: ./build/reports/checkstyle/main.html
[checkstyle-test]: ./build/reports/checkstyle/test.html
[test-report]: ./build/reports/tests/test/index.html
[bootJar]: ./build/libs/AAMMN%20Reservation%20System-1.0.0.jar

