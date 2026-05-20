# Project Structure

## Root directories:

- `app-android` - main application directory.
- `features` - directory for features modules.
- `service` - directory for services that provide domain data via repositories.
- `library` - directory for universal ready-to-use libraries.

## Project schema:

```
feature
  ⮑ sample                -- sample feature module (depends: service / librares)

service
  ⮑ serviceName           -- repository for service (depends: librares)

library
  ⮑ appinfo               -- information about app name, version number and so on
  ⮑ coroutine             -- utilities that can simplify writing and testing coroutines code
  ⮑ database              -- database implementation
  ⮑ json                  -- json utilities (e.g. toJson/fromJson)
  ⮑ mvvm                  -- MVVM helper functions
  ⮑ navigation            -- navigation helper functions
  ⮑ preferences           -- preferences implementation
  ⮑ ui                    -- ui utilities (e.g. string resource provider)
```

## Module dependencies:

### Feature -> Services -> Libraries

Feature modules depend on services which use repository pattern to provide domain data.
Service modules depend on libraries which provide data access configuration.

```
   Feature                   Services                  Libraries
+--------------+  domain   +--------------+           +--------------+
|  sample      |  models   |  repository  |  setup    |  database    |
|              | --------> |              | --------> |  preferences |
+--------------+           +--------------+           +--------------+
```

### Feature -> Libraries

Feature modules can use libraries directly, without services.
Library which can be used directly by feature, provides units and common functions.

```
   Feature                   Libraries
+--------------+  units/   +--------------+
|  sample      |  commons  |  mvvm        |
|              | --------> |  ui          |
+--------------+           +--------------+
```
