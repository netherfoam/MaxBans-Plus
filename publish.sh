#!/bin/bash

# This is a little awkward because of how CodeBuild clones the repository
REMOTE_BRANCH=`git branch -a --contains HEAD | sed -n 2p`

# Trim off the two leading characters (they're whitespace, after sed is used)
REMOTE_BRANCH=${REMOTE_BRANCH:2}

echo "REMOTE_BRANCH: $REMOTE_BRANCH"

if [[ "master" == "$REMOTE_BRANCH" ]]; then
    echo "Publishing build..."
    # Upload the file because it's a commit directly to Master
    cp target/maxbans-plus-*.jar target/maxbans-plus-latest.jar

    aws s3 cp target/maxbans-plus-*.jar s3://maxgamer.org/plugins/
else
    echo "master != $REMOTE_BRANCH... Skipping publish"
fi
