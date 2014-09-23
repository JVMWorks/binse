# BINSE - Business Intelligence for NSE

## Run the project
``
$ mvn clean install && foreman start
``

## Getting Started with Heroku
1. Install Heroku Toolbelt
2. Create a remote git repository
$ heroku create <project_name>
3. Push existing code to Heroku
$ git push heroku master
4. Set heroku path to use Java 1.7 
APP_PATH=`heroku config:get PATH`
heroku config:set PATH=/app/.jdk/bin:$APP_PATH

## Some Heroku dependencies
1. Procfile
2. system.properties
3. Maven to use Spark