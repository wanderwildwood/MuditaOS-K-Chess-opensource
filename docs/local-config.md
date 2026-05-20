#### Local configuration:

To build debug, qa and release versions of the app, add the following entries to `local.properties`:
```properties
mudita_repo_username={your.nexus.username}
mudita_repo_password={your.nexus.password}
mudita_nexus_repo_url={your.nexus.url}
```

For the qa and release build, additional Sentry configuration is required:
```properties
sentry_url={sentry.url}
sentry_project={sentry.project}
sentry_org={sentry.org}
sentry_dsn={sentry.dsn}
sentry_auth_token={sentry.auth.token}
```
