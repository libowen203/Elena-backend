### To run

`./run.sh`

The script first packages the source code with maven, builds a docker image and runs the container. Please make sure port 8080 is avaiable. **Note:** You need to have Docker installed and running. Also, the automated test suites will be run at the same time. But we might have a couple incorrect test cases at the moment. Ignore the test cases failures.

### To run using spring boot's maven plugin

`mvn spring-boot:run -Dspring-boot.run.profiles=dev`

### To run performance script

`../performance.sh`

Enter **localhost** for ip and **8080** for port when the prompts show up for performance testing in local environment after you have started ELeNa system by following descriptions above.