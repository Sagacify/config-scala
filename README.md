# Config-scala

Config library for scala
To help with cross-project readability, this package enables the creation of a default and run_env specific configurations.
Look into the config folder for examples.

To create a default config, add a default.json file in the config folder of your project.
If a value is set to null, the Config expects a run_env config or an environment variable to override it. (If not it will throw an exception.)

If a default value is superseeded by an run_env config or an environment variable, the Config object will ensure that it has the same type as the value it overrides. String -> String, Int -> Int etc

If the default value is null, any type is accepted.

# Testing
```
docker-compose run builder
```

# Packaging
```
docker-compose run builder sbt package
```

# Include into your project.
Add the following to your build.sbt

```
lazy val config = ProjectRef(uri("git://github.com/Sagacify/config-scala.git#v0.0.2"), "config-scala")
```

And make your project depend on it.
