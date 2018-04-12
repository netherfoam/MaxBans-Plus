#!/bin/bash

# This is a little awkward because of how CodeBuild clones the repository
REMOTE_BRANCH=`git branch -a --contains HEAD | sed -n 2p`

echo "REMOTE_BRANCH: $REMOTE_BRANCH"

if [ "remotes/origin/master" == "$REMOTE_BRANCH" ]; then
    echo "Publishing build..."
    # Upload the file because it's a commit directly to Master
    aws s3 cp target/maxbans-plus-*.jar s3://maxgamer.org/plugins/
fi
