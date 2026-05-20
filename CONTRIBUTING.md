
# Contributing

## Branches

`develop` branch contains current project code. All the changes you want to introduce to the project should be merged to that branch.

Branches that are to be merged to the `develop` should be named in a form of `<prefix>/<jira-issue-id>_short_explanation`,  
e.g.:
* `feature/MUC-123_board_pieces_movement_implementatio`
* `fix/MUC-123_app_name_typo`.

## Rebase instead of merge

If you're working on your feature branch, and you need to sync it with `develop`, do not merge `develop` into the branch. This will   
unnecessarily create merge commit and spoil your branch commits. Instead, rebase your branch with `develop`. Same applies when working   
with feature branch and personal branches.

## Commits

* Merge commit has to start with one of the prefixes:
    * `fi`: for bug fixes
    * `ft`: for features
    * `im`: for improvements

* Each merge commit (develop <- feature_branch) should contain corresponding Jira ticket number and short explanation, i.e.: `ft:XY-123_board_pieces_movement_implementation.`

* Each PR should contain corresponding Jira ticket number and short explanation, i.e.: `feature/XY-123 Implemented board pieces movement`

## Merge requests

1. When creating merge request, first make sure your branch is rebased with `develop`. You can also run the same Gradle command that is   
   invoked on the CI before push to see if MR checks won't immediately fail on CI:
    * `./gradlew ktlint`
    * `./gradlew detekt`
    * `./gradlew :app:lintDebug`
    * `./gradlew koverXmlReport`.
1. Make sure to assign a reviewer when creating merge request.
1. Two approvals are required to merge merge request (at least 1 from Mudita side).
