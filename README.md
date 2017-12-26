# rentalTool

## Prerequisites
- Install the latest version of [Java](http//java.com) and [Maven](http://maven.apache.org/download.html).

## Setup
- clone repository
`git clone https://github.com/marcelpra/rentalTool.git`
- configure username and password for connecting with database in `src\com\dbConnector\dbConnector.java`
- Import dependencies from Maven
`mvn package`
- add `web/VAADIN` folder and content of this folder from project to deployment output
- add war-file to Deployment configuration
- run sql init script in `dataBase/init_script.sql`
- you can login as Admin with:
`username: admin password: admin`