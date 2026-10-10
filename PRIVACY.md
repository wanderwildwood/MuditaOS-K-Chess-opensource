# Privacy

Chess+ collects nothing, sends nothing, and asks for no permissions.

That is the whole policy. The rest of this page is the evidence for it, because a privacy
policy that cannot be checked is only a promise.

## No permissions

The app's manifest asks for no permission a person could be asked to grant: not network, not
storage, not contacts, not identifiers. Android will not give an app anything it has not asked
for, so there is nothing of yours it can reach.

## No network

There is no networking code in the app and no dependency that provides any. Without the
`INTERNET` permission it could not open a connection if there were. The engine runs on the
phone, as a program inside the app.

## What it keeps

The game in progress, the options you chose, and the statistics of games against the
computer, in the app's own storage on the phone. Nothing else, and none of it leaves the
phone. Uninstalling the app removes all of it.

It began as Mudita's Chess app, which reported crashes to Sentry; that was taken out when the
interface moved to Mudita's public libraries, and nothing has replaced it.

## Changes

If this changes, this file changes with it, in the same repository as the code.
